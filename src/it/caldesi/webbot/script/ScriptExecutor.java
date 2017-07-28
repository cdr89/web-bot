package it.caldesi.webbot.script;

import java.util.Iterator;
import java.util.concurrent.Semaphore;

import it.caldesi.webbot.controller.RecordController;
import it.caldesi.webbot.model.instruction.Instruction;
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

	private Iterator<TreeItem<Instruction<?>>> iteratorOnIstructions;
	private ChangeListener<State> playListener;

	private Semaphore mutex;
	private Semaphore execSemaphore;
	// the following semaphore is to avoid setGraphic delay if another thread is
	// executing using Platform.runLater()
	private Semaphore graphicChangeSemaphore;

	private RecordController recordController;

	public ScriptExecutor(RecordController recordController, long globalDelay) {
		this.recordController = recordController;

		// Initialize iterator
		ObservableList<TreeItem<Instruction<?>>> instructions = recordController.scriptTreeTable.getRoot()
				.getChildren();
		iteratorOnIstructions = instructions.iterator();

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
				// update state variables
				lastState = newState;

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

	private TreeItem<Instruction<?>> nextInstruction() {
		return iteratorOnIstructions.next();
	}

	private boolean hasNextInstruction() {
		return iteratorOnIstructions.hasNext();
	}

	private void execute(TreeItem<Instruction<?>> treeItem) throws Exception {
		Instruction<?> instruction = treeItem.getValue();

		System.out.println("Executing: " + instruction.actionName);
		instruction.execute(recordController.webView);
		success(treeItem);
		System.out.println("Executed: " + instruction.toString());
	}

	private boolean finished = false;

	private void onFinish() {
		if (finished)
			return;
		finished = true;

		Runnable onFinishRunnable = () -> {
			waitFor(globalDelay);
			recordController.webEngine.getLoadWorker().stateProperty().removeListener(playListener);
			recordController.onFinishExecution();
			System.out.println("Enabling controls");
			recordController.executeButton.setDisable(false);
			recordController.goButton.setDisable(false);
			recordController.addressTextField.setDisable(false);
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

	private State lastState;
	private long globalDelay;
	TreeItem<Instruction<?>> currentInstruction;

	private boolean failed = false;

	@Override
	public void run() {
		while (hasNextInstruction() && !failed) {
			waitFor(globalDelay);

			if (failed)
				break;

			try {
				currentInstruction = nextInstruction();
				executing(currentInstruction);

				System.out.println("[scriptExecutor] Acquire mutex");
				mutex.acquire();
				waitFor(currentInstruction.getValue().getDelay());

				if (lastState == State.CANCELLED || lastState == State.FAILED) {
					failed = true;
					break;
				}

				Runnable instructionRunnable = () -> {
					final TreeItem<Instruction<?>> instr = currentInstruction;
					try {
						if (failed)
							return;

						System.out.println("[instructionRunnable] number of execSemaphore permits: "
								+ execSemaphore.availablePermits());
						// execSemaphore.acquire();
						execute(instr);
					} catch (Exception e) {
						e.printStackTrace();
						failed(instr);
						onFinish();
					} finally {
						execSemaphore.release();
						System.out.println("[instructionRunnable] release execSemaphore, permits: "
								+ execSemaphore.availablePermits());
					}
				};
				Thread instrThread = new Thread(instructionRunnable);
				graphicChangeSemaphore.acquire();
				execSemaphore.acquire();
				Platform.runLater(instrThread);
				graphicChangeSemaphore.release();
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

	private void failed(TreeItem<Instruction<?>> currentInstruction2) {
		failed = true;
		try {
			graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.RED)));
				waitFor(SET_GRAPHIC_DELAY);
				graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void success(TreeItem<Instruction<?>> currentInstruction2) {
		try {
			graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.GREEN)));
				waitFor(SET_GRAPHIC_DELAY);
				graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void executing(TreeItem<Instruction<?>> currentInstruction2) {
		try {
			graphicChangeSemaphore.acquire();
			Runnable changeGrapics = () -> {
				currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.YELLOW)));
				waitFor(SET_GRAPHIC_DELAY);
				graphicChangeSemaphore.release();
			};
			Platform.runLater(changeGrapics);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
