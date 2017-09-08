package it.caldesi.webbot.script;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.controller.MainController;
import it.caldesi.webbot.exception.StopExecutionException;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.model.instruction.PageInstruction;
import it.caldesi.webbot.model.instruction.block.Block;
import it.caldesi.webbot.utils.UIUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.scene.control.TreeItem;

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
		scriptExecutionContext = new ScriptExecutionContext(this);

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
			boolean execSemaphoreAquired = false;

			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				System.out
						.println("[playListener] number of execSemaphore permits: " + execSemaphore.availablePermits());
				System.out.println("[playListener] -----LOCATION----->" + recordController.webEngine.getLocation());
				System.out.println("[playListener] State: " + ov.getValue().toString());

				if (newState == State.SCHEDULED || newState == State.READY || newState == State.RUNNING) {
					if (!mutexAcquired) {
						System.out.println("[playListener] Acquiring mutex");
						mutexAcquired = mutex.tryAcquire();
						if (!mutexAcquired) {
							System.out.println("[playListener] Failed tryAquire of mutex in state: " + newState.name());
						} else {
							System.out
									.println("[playListener] TryAquire of mutex success in state: " + newState.name());
						}

						execSemaphoreAquired = execSemaphore.tryAcquire();
						if (!execSemaphoreAquired) {
							System.out.println(
									"[playListener] Failed tryAquire of execSemaphore in state: " + newState.name());
						} else {
							System.out.println(
									"[playListener] TryAquire of execSemaphore success in state: " + newState.name());
						}
					}
				} else { // can go
					if (newState == State.SUCCEEDED) {
						recordController.onPageLoadSuccess();
						scriptExecutionContext.setGlobalVariablesJS(recordController.webEngine);
						if ((currentInstruction.getValue() instanceof PageInstruction)) {
							success(currentInstruction);
							execSemaphore.release();
						} else {
							if (execSemaphoreAquired)
								execSemaphore.release();
						}
						execSemaphoreAquired = false;
					} else if (newState == State.CANCELLED || newState == State.FAILED) {
						if ((currentInstruction.getValue() instanceof PageInstruction))
							failed(currentInstruction);
						forcedStop();
					}

					if (mutexAcquired || mutex.availablePermits() == 0) {
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

	public void addInstructionsToExecute(List<TreeItem<Instruction<?>>> instructions) {
		if (instructions == null)
			return;

		for (int i = instructions.size() - 1; i >= 0; i--) {
			executionStack.push(instructions.get(i));
		}
	}

	public void addInstructionToExecute(TreeItem<Instruction<?>> currentInstruction2) {
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
			// boolean acquired = false;
			// if (!forced) {
			// acquired = execSemaphore.tryAcquire();
			// }
			recordController.webEngine.getLoadWorker().stateProperty().removeListener(playListener);
			recordController.onFinishExecution();
			System.out.println("Enabling controls");
			recordController.enableControls();
			// if (!forced && execSemaphore.availablePermits() == 0 && acquired)
			// execSemaphore.release();
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
				if (instruction.isDisabled()) {
					disable(currentInstruction);
					execSemaphore.release();
					continue;
				}

				executing(currentInstruction);
				execSemaphore.release();

				if (instruction instanceof Block) {
					currentInstruction.setExpanded(true);
					Block block = (Block) instruction;

					try {
						if (block.canContinue(this, currentInstruction)) {
							success(currentInstruction);
							continue;
						} else {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
						failed = true;
						failed(currentInstruction);
						onFinish();
						break;
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
						if (!(instr.getValue() instanceof PageInstruction)) {
							execSemaphore.release();
						}
						System.out.println("[instructionRunnable] release execSemaphore, permits: "
								+ execSemaphore.availablePermits());
					} catch (StopExecutionException see) {
						see.printStackTrace();
						success(instr, true);
						forcedStop(true);
					} catch (Exception e) {
						e.printStackTrace();
						failed(instr);
						onFinish();
					} finally {

					}
				};
				runningInstruction = new Thread(instructionRunnable);
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
				currentInstruction2.setGraphic(UIUtils.getExecutionIndicator(UIUtils.Colors.GREY));
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
				currentInstruction2.setGraphic(UIUtils.getExecutionIndicator(UIUtils.Colors.RED));
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
				currentInstruction2.setGraphic(UIUtils.getExecutionIndicator(UIUtils.Colors.GREEN));
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
				currentInstruction2.setGraphic(UIUtils.getExecutionIndicator(UIUtils.Colors.YELLOW));
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

	public ScriptExecutionContext getContext() {
		return scriptExecutionContext;
	}

	public long getGlobalDelay() {
		return globalDelay;
	}

	public void setGlobalDelay(long globalDelay) {
		this.globalDelay = globalDelay;
	}

	public boolean hasFailed() {
		return failed;
	}

}
