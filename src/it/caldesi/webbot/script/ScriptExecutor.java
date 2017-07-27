package it.caldesi.webbot.script;

import java.util.Iterator;
import java.util.concurrent.Semaphore;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.controller.RecordController;
import it.caldesi.webbot.model.instruction.Instruction;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ScriptExecutor implements Runnable {

	private Iterator<TreeItem<Instruction<?>>> iteratorOnIstructions;
	private ChangeListener<State> playListener;

	private Semaphore mutex;
	private Semaphore execSemaphore;

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

		playListener = new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				System.out
						.println("[playListener] number of execSemaphore permits: " + execSemaphore.availablePermits());
				// try {
				// System.out.println("[playListener] Acquire mutex");
				// mutex.acquire();
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }

				System.out.println("[playListener] -----LOCATION----->" + recordController.webEngine.getLocation());
				System.out.println("[playListener] State: " + ov.getValue().toString());
				// update state variables
				// previousState = oldState;
				lastState = newState;

				if (newState == State.SCHEDULED || newState == State.READY || newState == State.RUNNING) {
					try {
						if (mutex.availablePermits() == 1) {
							System.out.println("[playListener] Acquire mutex");
							mutex.acquire();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					boolean tryAcquire = execSemaphore.tryAcquire();
					if (!tryAcquire) {
						System.out.println("Failed tryAquire of execSemaphore in state: " + newState.name());
					} else {
						System.out.println("TryAquire of execSemaphore success in state: " + newState.name());
					}
				} else { // can go
					if (execSemaphore.availablePermits() == 0) {
						execSemaphore.release();
						System.out.println("Release execSemaphore from listener in state: " + newState.name());
					}

					if (mutex.availablePermits() == 0) {
						System.out.println("[playListener] Release mutex");
						mutex.release();
					}
				}

			}
		};

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

		Runnable instructionRunnable = () -> {
			recordController.webEngine.getLoadWorker().stateProperty().removeListener(playListener);
			recordController.webEngine.getLoadWorker().stateProperty().addListener(Context.recordListener);
			System.out.println("Enabling controls");
			recordController.executeButton.setDisable(false);
			recordController.goButton.setDisable(false);
			recordController.addressTextField.setDisable(false);
		};
		Platform.runLater(instructionRunnable);

		finished = true;
	}

	public void waitFor(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	// private State previousState;
	private State lastState;
	private long globalDelay;
	TreeItem<Instruction<?>> currentInstruction;

	private boolean failed = false;

	@Override
	public void run() {
		while (hasNextInstruction() && !failed) {
			waitFor(globalDelay);

			try {
				System.out.println("[scriptExecutor] Acquire mutex");
				mutex.acquire();

				if (lastState == State.CANCELLED || lastState == State.FAILED) {
					failed = true;
					break;
				}

				currentInstruction = nextInstruction();
				Instruction<?> instruction = currentInstruction.getValue();

				executing(currentInstruction);

				waitFor(instruction.getDelay());
				Runnable instructionRunnable = () -> {
					final TreeItem<Instruction<?>> instr = currentInstruction;
					try {
						if (failed)
							return;
						System.out.println("[instructionRunnable] number of execSemaphore permits: "
								+ execSemaphore.availablePermits());
						execSemaphore.acquire();
						execute(instr);
					} catch (Exception e) {
						e.printStackTrace();
						failed(instr);
						onFinish();
					} finally {
						execSemaphore.release();
					}
				};
				Thread instrThread = new Thread(instructionRunnable);
				Platform.runLater(instrThread);

				// instrThread.join();
			} catch (Exception e) {
				e.printStackTrace();
				onFinish();
			} finally {
				System.out.println("[scriptExecutor] Release mutex");
				mutex.release();
			}
		}

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

	// private final Node rootIcon = new ImageView(new
	// Image(getClass().getResourceAsStream("root.png")));

	private static final String GREEN = "#68C953";
	private static final String RED = "#CF3E3E";
	private static final String YELLOW = "#EDAD18";

	private void failed(TreeItem<Instruction<?>> currentInstruction2) {
		currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(RED)));
		failed = true;
	}

	private void success(TreeItem<Instruction<?>> currentInstruction2) {
		currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(GREEN)));
	}

	private void executing(TreeItem<Instruction<?>> currentInstruction2) {
		currentInstruction2.setGraphic(new Circle(10.0, Paint.valueOf(YELLOW)));
	}

}
