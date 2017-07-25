package it.caldesi.webbot.script;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.instruction.Instruction;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ScriptExecutor {

	private WebView webView;
	private WebEngine webEngine;

	private Iterator<Instruction<?>> iteratorOnIstructions;
	private ChangeListener<State> playListener;

	public ScriptExecutor(WebView webView) {
		this.webView = webView;
		this.webEngine = webView.getEngine();

		playListener = new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				System.out.println("-----LOCATION----->" + webEngine.getLocation());
				System.out.println("State: " + ov.getValue().toString());
				if (newState == State.SUCCEEDED) {
					if (!hasNextInstruction())
						return;

					Instruction<?> instruction = nextInstruction();

					// TODO manage sequence of instructions that does not change
					// the state
					Runnable waitTask = () -> {
						waitFor(instruction.getDelay());
						Runnable secondPageRunnable = () -> {
							execute(instruction);
						};
						Platform.runLater(secondPageRunnable);
					};

					new Thread(waitTask).start();
				}
			}

			public void waitFor(long time) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		};

		webEngine.getLoadWorker().stateProperty().removeListener(Context.recordListener);
		webEngine.getLoadWorker().stateProperty().addListener(playListener);
	}

	private Instruction<?> nextInstruction() {
		return iteratorOnIstructions.next();
	}

	private boolean hasNextInstruction() {
		return iteratorOnIstructions.hasNext();
	}

	public void executeScript(TreeTableView<Instruction<?>> scriptTreeTable) {
		ObservableList<TreeItem<Instruction<?>>> instructions = scriptTreeTable.getRoot().getChildren();
		List<Instruction<?>> script = instructions.stream().map(TreeItem::getValue).collect(Collectors.toList());

		iteratorOnIstructions = script.iterator();

		if (hasNextInstruction()) {
			Instruction<?> instruction = nextInstruction();
			execute(instruction);
		}
	}

	private void execute(Instruction<?> instruction) {
		System.out.println("Executing: " + instruction.actionName);
		try {
			instruction.execute(webView);
		} catch (GenericException e) {
			e.printStackTrace();
		}
		System.out.println("Executed: " + instruction.toString());
	}

	private void onFinish() { // TODO call
		webEngine.getLoadWorker().stateProperty().removeListener(playListener);
		webEngine.getLoadWorker().stateProperty().addListener(Context.recordListener);
	}

}
