package it.caldesi.webbot.script;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.controller.MainController;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.exception.StopExecutionException;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.model.instruction.PageInstruction;
import it.caldesi.webbot.model.instruction.block.Block;
import it.caldesi.webbot.model.instruction.block.ForTimesBlock;
import it.caldesi.webbot.model.instruction.block.IfBlock;
import it.caldesi.webbot.model.instruction.block.WhileBlock;
import it.caldesi.webbot.utils.UIUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ScriptExecutor implements Runnable {

	private static int SET_GRAPHIC_DELAY = 100;

	private Stack<TreeItem<Instruction<?>>> executionStack = new Stack<>();
	private ChangeListener<State> playListener;

	private Semaphore mutex;
	private Semaphore execSemaphore;
	// the following semaphore is to avoid setGraphic delay if another thread is
	// executing using Platform.runLater()
	private Semaphore graphicChangeSemaphore;

	private MainController recordController;

	private ScriptExecutionContext scriptExecutionContext;

	public ScriptExecutor(MainController recordController, long globalDelay) {
		this.recordController = recordController;
		scriptExecutionContext = new ScriptExecutionContext();

		// Initialize iterator
		ObservableList<TreeItem<Instruction<?>>> instructions = recordController.scriptTreeTable.getRoot()
				.getChildren();
		addInstructionsToExecute(instructions);

		this.globalDelay = globalDelay;
		this.mutex = new Semaphore(1);
		this.execSemaphore = new Semaphore(1);
		this.graphicChangeSemaphore = new Semaphore(1);

		playListener = new ChangeListener<State>() {
			boolean mutexAcquired = false;

			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				System.out
						.println("[playListener] number of execSemaphore permits: " + execSemaphore.availablePermits());

				System.out.println("[playListener] -----LOCATION----->" + recordController.webEngine.getLocation());
				System.out.println("[playListener] State: " + ov.getValue().toString());

				if (newState == State.SCHEDULED || newState == State.READY || newState == State.RUNNING) {
					if (!mutexAcquired) {
						System.out.println("[playListener] Acquire mutex");
						mutexAcquired = mutex.tryAcquire();

						boolean tryAcquire = execSemaphore.tryAcquire();
						if (!tryAcquire) {
							System.out.println("Failed tryAquire of execSemaphore in state: " + newState.name());
						} else {
							System.out.println("TryAquire of execSemaphore success in state: " + newState.name());
						}
					}
				} else { // can go
					if (newState == State.SUCCEEDED) {
						recordController.onPageLoadSuccess();
						if ((currentInstruction.getValue() instanceof PageInstruction))
							success(currentInstruction);
					} else if (newState == State.CANCELLED || newState == State.FAILED) {
						if ((currentInstruction.getValue() instanceof PageInstruction))
							failed(currentInstruction);
						forcedStop();
					}

					if (mutexAcquired) {
						if (execSemaphore.availablePermits() == 0) {
							execSemaphore.release();
							System.out.println("Release execSemaphore from listener in state: " + newState.name());
						}

						System.out.println("[playListener] Release mutex");
						mutex.release();
						mutexAcquired = false;
					}
				}
			}
		};

		// recordController.webEngine.getLoadWorker().stateProperty().removeListener(recordController.recordListener);
		recordController.webEngine.getLoadWorker().stateProperty().addListener(playListener);
	}

	private void addInstructionsToExecute(List<TreeItem<Instruction<?>>> instructions) {
		if (instructions == null)
			return;

		for (int i = instructions.size() - 1; i >= 0; i--) {
			executionStack.push(instructions.get(i));
		}
	}

	private void addInstructionToExecute(TreeItem<Instruction<?>> currentInstruction2) {
		if (currentInstruction2 == null)
			return;

		executionStack.push(currentInstruction2);
	}

	private TreeItem<Instruction<?>> nextInstruction() {
		return executionStack.pop();
	}

	private boolean hasNextInstruction() {
		return !executionStack.isEmpty();
	}

	private void execute(TreeItem<Instruction<?>> treeItem) throws Exception {
		Instruction<?> instruction = treeItem.getValue();

		System.out.println("Executing: " + instruction.actionName);

		Object result = instruction.execute(scriptExecutionContext, recordController.webView);
		String variable = instruction.getVariable();
		if (Context.isAssignable(instruction.actionName) && variable != null && !variable.trim().isEmpty()) {
			scriptExecutionContext.variableValues.put(variable, result);
		}

		if (!(instruction instanceof PageInstruction))
			success(treeItem);
		System.out.println("Executed: " + instruction.toString());
	}

	private boolean finished = false;

	private void onFinish() {
		onFinish(false);
	}

	private void onFinish(boolean forced) {
		if (finished)
			return;
		finished = true;

		Runnable onFinishRunnable = () -> {
			waitFor(globalDelay);
			boolean acquired = false;
			if (!forced) {
				try {
					acquired = execSemaphore.tryAcquire(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			recordController.webEngine.getLoadWorker().stateProperty().removeListener(playListener);
			recordController.onFinishExecution();
			System.out.println("Enabling controls");
			recordController.enableControls();
			if (!forced && execSemaphore.availablePermits() == 0 && acquired)
				execSemaphore.release();
		};
		Platform.runLater(onFinishRunnable);
	}

	public void waitFor(long time) {
		if (time == 0)
			return;
		try {
			System.out.println("Waiting ms: " + time);
			Thread.sleep(time);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private long globalDelay;
	TreeItem<Instruction<?>> currentInstruction;

	private boolean failed = false;

	private Thread runningInstruction;

	@Override
	public void run() {
		while (hasNextInstruction() && !failed) {
			waitFor(globalDelay);

			if (failed)
				break;

			try {
				System.out.println("[scriptExecutor] Acquiring mutex");
				mutex.acquire();
				System.out.println("[scriptExecutor] Acquired mutex");

				execSemaphore.acquire();
				currentInstruction = nextInstruction();
				Instruction<?> instruction = currentInstruction.getValue();
				// waitFor(instruction.getDelay());
				if (instruction.isDisabled()) {
					disable(currentInstruction);
					execSemaphore.release();
					continue;
				}

				executing(currentInstruction);
				execSemaphore.release();

				if (instruction instanceof Block) {
					currentInstruction.setExpanded(true);

					if (instruction instanceof IfBlock) {
						IfBlock ifBlock = (IfBlock) instruction;
						try {
							if (ifBlock.evaluateCondition(scriptExecutionContext))
								addInstructionsToExecute(currentInstruction.getChildren());
							success(currentInstruction);
						} catch (GenericException e) {
							failed = true;
							failed(currentInstruction);
							onFinish();
							e.printStackTrace();
							break;
						}
						continue;
					} else if (instruction instanceof WhileBlock) {
						WhileBlock whileBlock = (WhileBlock) instruction;
						try {
							if (whileBlock.evaluateCondition(scriptExecutionContext)) {
								addInstructionToExecute(currentInstruction);
								addInstructionsToExecute(currentInstruction.getChildren());
							}
							success(currentInstruction);
						} catch (GenericException e) {
							failed = true;
							failed(currentInstruction);
							onFinish();
							e.printStackTrace();
							break;
						}
						continue;
					} else if (instruction instanceof ForTimesBlock) {
						ForTimesBlock forTimesBlock = (ForTimesBlock) instruction;
						success(currentInstruction);
						Integer execCount = scriptExecutionContext.forTimesCounters.get(forTimesBlock);
						if (execCount == null) {
							int count = Integer.parseInt(forTimesBlock.getArg());
							scriptExecutionContext.forTimesCounters.put(forTimesBlock, count);
							execCount = count;
						}
						if (execCount == 0)
							continue;
						if (execCount > 0) {
							scriptExecutionContext.forTimesCounters.put(forTimesBlock, execCount - 1);
							addInstructionToExecute(currentInstruction);
							addInstructionsToExecute(currentInstruction.getChildren());
						}
						continue;
					}
				}

				if (currentInstruction.getValue().getDelay() > 0) {
					Runnable delayRunnable = () -> {
						final TreeItem<Instruction<?>> instr = currentInstruction;
						try {
							if (failed)
								return;

							waitFor(instr.getValue().getDelay());
						} catch (Exception e) {
							e.printStackTrace();
							failed(instr);
							onFinish();
						} finally {
							execSemaphore.release();
						}
					};
					Thread delayThread = new Thread(delayRunnable);
					execSemaphore.acquire();
					delayThread.start();
				}

				Runnable instructionRunnable = () -> {
					final TreeItem<Instruction<?>> instr = currentInstruction;
					try {
						if (failed)
							return;

						System.out.println("[instructionRunnable] number of execSemaphore permits: "
								+ execSemaphore.availablePermits());
						execute(instr);
					} catch (StopExecutionException see) {
						see.printStackTrace();
						success(instr, true);
						forcedStop(true);
					} catch (Exception e) {
						e.printStackTrace();
						failed(instr);
						onFinish();
					} finally {
						if (!(instr.getValue() instanceof PageInstruction)) {
							execSemaphore.release();
						}
						System.out.println("[instructionRunnable] release execSemaphore, permits: "
								+ execSemaphore.availablePermits());
					}
				};
				runningInstruction = new Thread(instructionRunnable);
				// graphicChangeSemaphore.acquire();
				execSemaphore.acquire();
				if (Context.isUIInstruction(instruction.getClass()))
					Platform.runLater(runningInstruction);
				else
					runningInstruction.start();
				// graphicChangeSemaphore.release();
			} catch (Exception e) {
				e.printStackTrace();
				onFinish();
			} finally {
				System.out.println("[scriptExecutor] Release mutex");
				mutex.release();
			}
		}

		waitFor(globalDelay);
		try {
			mutex.acquire();
			execSemaphore.acquire();
			onFinish();
			execSemaphore.release();
			mutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void disable(final TreeItem<Instruction<?>> instr) {
		disable(instr, false);
	}

	private void disable(final TreeItem<Instruction<?>> currentInstruction2, boolean forced) {
		try {
			if (!forced)
				graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.GREY)));
				waitFor(SET_GRAPHIC_DELAY);
				if (!forced)
					graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void failed(final TreeItem<Instruction<?>> instr) {
		failed(instr, false);
	}

	private void failed(final TreeItem<Instruction<?>> currentInstruction2, boolean forced) {
		failed = true;
		try {
			if (!forced)
				graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.RED)));
				waitFor(SET_GRAPHIC_DELAY);
				if (!forced)
					graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void success(final TreeItem<Instruction<?>> instr) {
		success(instr, false);
	}

	private void success(final TreeItem<Instruction<?>> currentInstruction2, boolean forced) {
		try {
			if (!forced)
				graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.GREEN)));
				waitFor(SET_GRAPHIC_DELAY);
				if (!forced)
					graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void executing(final TreeItem<Instruction<?>> currentInstruction2) {
		try {
			graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.YELLOW)));
				if (currentInstruction2.getValue() instanceof Block) {
					UIUtils.clearExecutionIndicators(currentInstruction2.getChildren());
				}
				waitFor(SET_GRAPHIC_DELAY);
				graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void forcedStop(boolean success) {
		failed = true;
		onFinish(true);
		if (runningInstruction != null && runningInstruction.isAlive()) {
			runningInstruction.stop();
			if (!success)
				failed(currentInstruction, true);
		}
	}

	public void forcedStop() {
		forcedStop(false);
	}

}
