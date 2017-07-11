package it.caldesi.webbot.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import it.caldesi.webbot.model.bean.Instruction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RecordController implements Initializable {

	@FXML
	private WebView webView;

	@FXML
	private TreeTableView<Instruction> scriptTreeTable;

	@FXML
	private TreeTableColumn<Instruction, String> treeColLabel;
	@FXML
	private TreeTableColumn<Instruction, String> treeColAction;
	@FXML
	private TreeTableColumn<Instruction, String> treeColObj;
	@FXML
	private TreeTableColumn<Instruction, String> treeColArgs;

	private WebEngine webEngine;

	@FXML
	private ResourceBundle resources;

	public RecordController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// ResourceBundle recordBundle =
		// ResourceBundle.getBundle("bundles.RecordScene", new Locale("it",
		// "IT"));
		// ResourceBundle recordBundle =
		// ResourceBundle.getBundle("it.caldesi.webbot.properties.RecordScene");

		initWebView(resources);
		initTreeTableView(resources);
	}

	public void initTreeTableView(ResourceBundle recordBundle) {
		scriptTreeTable.setPlaceholder((new Label(recordBundle.getString("scene.record.emptyScriptList"))));

		// Column mapping
		treeColLabel.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction, String>("label"));
		treeColAction.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction, String>("actionName"));
		treeColObj.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction, String>("objectXPath"));
		treeColArgs.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction, String>("args"));

		// root node
		Instruction root = new Instruction();
		root.setLabel("root");
		TreeItem<Instruction> rootItem = new TreeItem<Instruction>(root);
		scriptTreeTable.setRoot(rootItem);
		scriptTreeTable.setShowRoot(false);
	}

	public void initWebView(ResourceBundle recordBundle) {
		webEngine = webView.getEngine();

		webEngine.setOnAlert((WebEvent<String> wEvent) -> {
			System.out.println("JS alert() message: " + wEvent.getData());
		});

		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				System.out.println("-----LOCATION----->" + webEngine.getLocation());
				System.out.println("State: " + ov.getValue().toString());

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
		});

		// load the web page
		String URL = "https://www.facebook.com";
		webEngine.load(URL);
	}

	public void newActionPopup(Event ev) {
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

	private void appendInstructionToList(Instruction instruction) {
		TreeItem<Instruction> item = new TreeItem<Instruction>(instruction);

		TreeItem<Instruction> root = scriptTreeTable.getRoot();
		if (root == null) {
			scriptTreeTable.setRoot(item);
		} else {
			root.getChildren().add(item);
		}
	}

}
