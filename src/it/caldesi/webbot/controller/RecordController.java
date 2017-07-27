package it.caldesi.webbot.controller;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.model.instruction.GoToPageInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.model.instruction.NullInstruction;
import it.caldesi.webbot.script.ScriptExecutor;
import it.caldesi.webbot.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RecordController implements Initializable {

	public final static int GLOBAL_DELAY = 200;

	@FXML
	public WebView webView;

	@FXML
	public TreeTableView<Instruction<?>> scriptTreeTable;

	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColLabel;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColAction;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColObj;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColArgs;

	@FXML
	public Button goButton;
	@FXML
	public Button executeButton;
	@FXML
	public TextField addressTextField;

	public WebEngine webEngine;

	@FXML
	private ResourceBundle resources;

	public RecordController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initWebView(resources);
		initTreeTableView(resources);
	}

	public void initTreeTableView(ResourceBundle recordBundle) {
		scriptTreeTable.setPlaceholder((new Label(recordBundle.getString("scene.record.emptyScriptList"))));

		// Column mapping
		treeColLabel.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("label"));
		treeColAction.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("actionName"));
		treeColObj.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("objectXPath"));
		treeColArgs.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("args"));

		// root node
		Instruction<?> root = new NullInstruction();
		root.setLabel("root");
		TreeItem<Instruction<?>> rootItem = new TreeItem<>(root);
		scriptTreeTable.setRoot(rootItem);
		scriptTreeTable.setShowRoot(false);

		scriptTreeTable.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent keyEvent) {
				final TreeItem<Instruction<?>> selectedItem = scriptTreeTable.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					if (keyEvent.getCode().equals(KeyCode.DELETE)) {
						selectedItem.getParent().getChildren().remove(selectedItem);
					}
				}
			}
		});
	}

	public void initWebView(ResourceBundle recordBundle) {
		System.out.println("INIT WebView");
		webEngine = webView.getEngine();

		webEngine.setOnAlert((WebEvent<String> wEvent) -> {
			System.out.println("JS alert() message: " + wEvent.getData());
		});

		Context.recordListener = new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				System.out.println("[recordListener] -----LOCATION----->" + webEngine.getLocation());
				System.out.println("[recordListener] State: " + ov.getValue().toString());
				addressTextField.setText(webEngine.getLocation());

				if (newState == Worker.State.SUCCEEDED) {
					EventListener listener = new EventListener() {
						public void handleEvent(Event ev) {
							newActionPopup(ev);
						}
					};

					Document doc = webEngine.getDocument();
					Element el = doc.getDocumentElement();
					((EventTarget) el).addEventListener("click", listener, false);
				}
			}
		};

		webEngine.getLoadWorker().stateProperty().addListener(Context.recordListener);
	}

	private void loadPage(String url) {
		webEngine.load(url);
	}

	private void newActionPopup(Event ev) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/caldesi/webbot/view/popup_new_action.fxml"),
					resources);
			Parent root1 = loader.load();

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("New Action");
			stage.setScene(new Scene(root1));

			PopupNewActionController controller = loader.<PopupNewActionController> getController();
			controller.initEventData(ev);

			controller.setInstructionCallback(instruction -> appendInstructionToList(instruction));

			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void appendInstructionToList(Instruction<?> instruction) {
		TreeItem<Instruction<?>> item = new TreeItem<>(instruction);

		TreeItem<Instruction<?>> root = scriptTreeTable.getRoot();
		if (root == null) {
			scriptTreeTable.setRoot(item);
		} else {
			root.getChildren().add(item);
		}
	}

	public void goToAddress() {
		String url = addressTextField.getText();
		if (url == null || url.isEmpty())
			return;
		url = Utils.adjustUrl(url);
		loadPage(url);
		Instruction<?> instruction = Instruction.Builder.buildByName(GoToPageInstruction.NAME);
		LinkedList<String> args = new LinkedList<>();
		args.add(url);
		instruction.setArgs(args);
		appendInstructionToList(instruction);
	}

	public void executeScript() {
		webEngine.getLoadWorker().stateProperty().removeListener(Context.recordListener);
		final ObservableList<TreeItem<Instruction<?>>> rows = scriptTreeTable.getRoot().getChildren();
		rows.parallelStream().forEach(row -> row.setGraphic(new Circle(10.0, Paint.valueOf("#ffffff"))));

		executeButton.setDisable(true);
		goButton.setDisable(true);
		addressTextField.setDisable(true);

		ScriptExecutor scriptExecutor = new ScriptExecutor(this, GLOBAL_DELAY);
		new Thread(scriptExecutor).start();
	}

}
