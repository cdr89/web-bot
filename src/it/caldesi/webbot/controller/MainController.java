package it.caldesi.webbot.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import it.caldesi.webbot.model.instruction.GoToPageInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.model.instruction.block.Block;
import it.caldesi.webbot.model.instruction.block.RootBlock;
import it.caldesi.webbot.script.ScriptExecutor;
import it.caldesi.webbot.utils.FileUtils;
import it.caldesi.webbot.utils.JSUtils;
import it.caldesi.webbot.utils.UIUtils;
import it.caldesi.webbot.utils.Utils;
import it.caldesi.webbot.utils.XMLUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {

	public final static int GLOBAL_DELAY = 200;

	@FXML
	public WebView webView;

	@FXML
	public TreeTableView<Instruction<?>> scriptTreeTable;

	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColLabel;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColVariable;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColAction;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColTarget;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColArgs;
	@FXML
	private TreeTableColumn<Instruction<?>, String> treeColDelay;

	@FXML
	public Button goButton;
	@FXML
	public Button executeButton;
	@FXML
	public Button stopButton;
	@FXML
	public TextField addressTextField;

	public WebEngine webEngine;

	private ResourceBundle resources;

	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
	private Timeline scrolltimeline = new Timeline();
	private double scrollDirection = 0;

	public ChangeListener<State> recordListener;
	private EventListener clickElementListener;
	// private EventListener overElementListener;

	private static String functionsJS = FileUtils.readResource("/it/caldesi/webbot/js/functions.js");
	private static String highlightJS = FileUtils.readResource("/it/caldesi/webbot/js/highlightElement.js");
	private static String removeHighlightJS = FileUtils.readResource("/it/caldesi/webbot/js/removeHighlight.js");

	public MainController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		initWebView(resources);
		initTreeTableView(resources);
	}

	public void initTreeTableView(ResourceBundle recordBundle) {
		scriptTreeTable.setPlaceholder((new Label(recordBundle.getString("scene.record.emptyScriptList"))));

		// Column mapping
		treeColLabel.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("label"));
		treeColVariable.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("variable"));
		treeColAction.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("actionName"));
		treeColTarget.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("target"));
		treeColArgs.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("arg"));
		treeColDelay.setCellValueFactory(new TreeItemPropertyValueFactory<Instruction<?>, String>("delay"));

		// root node
		Instruction<?> root = new RootBlock();
		root.setLabel("root");
		TreeItem<Instruction<?>> rootItem = new TreeItem<>(root);
		scriptTreeTable.setRoot(rootItem);
		scriptTreeTable.setShowRoot(false);

		// keyboard delete item listener
		scriptTreeTable.setOnKeyPressed(this::keyPressListener);
		// mouse double click new/edit listener
		scriptTreeTable.setOnMouseClicked(this::mouseListener);
		// drag and drop
		scriptTreeTable.setRowFactory(this::rowFactory);
		// scrolling
		setupScrolling();
	}

	public void initWebView(ResourceBundle recordBundle) {
		System.out.println("INIT WebView");
		webEngine = webView.getEngine();

		webEngine.setOnAlert((WebEvent<String> wEvent) -> {
			System.out.println("JS alert() message: " + wEvent.getData());
		});

		addRecordListener();
	}

	public void addRecordListener() {
		webEngine.getLoadWorker().stateProperty().addListener(recordListener = new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				if (!isFinishedExecution())
					return;

				System.out.println("[recordListener] -----LOCATION----->" + webEngine.getLocation());
				System.out.println("[recordListener] State: " + ov.getValue().toString());
				addressTextField.setText(webEngine.getLocation());

				if (newState == Worker.State.SUCCEEDED) {
					onPageLoadSuccess();

					clickElementListener = new EventListener() {
						public void handleEvent(Event ev) {
							// remove highlight
							webView.getEngine().executeScript(removeHighlightJS);

							// highlight component
							Element el = (Element) ev.getTarget();
							String xPath = XMLUtils.getFullXPath(el);

							try {
								Map<String, String> paramValues = new HashMap<>();
								paramValues.put("xPath", xPath);
								String highlightCompiled = JSUtils.loadParametrizedJS(highlightJS, paramValues);
								webView.getEngine().executeScript(highlightCompiled);
							} catch (Exception e) {
								e.printStackTrace();
							}
							newActionPopup(ev);
						}
					};

					Document doc = webEngine.getDocument();
					Element el = doc.getDocumentElement();
					((EventTarget) el).addEventListener("click", clickElementListener, false);
				}
			}
		});
	}

	private void loadPage(String urlString) {
		String filePrefix = "file://";
		if (urlString.startsWith(filePrefix))
			try {
				urlString = urlString.substring(filePrefix.length());
				// System.out.println(urlString);
				File resourceFile = new File(urlString);
				URL url = resourceFile.toURI().toURL();
				urlString = url.toExternalForm();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		webEngine.load(urlString);
	}

	private void newActionPopup(Event ev) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/caldesi/webbot/view/edit_action_popup.fxml"),
					resources);
			Parent root1 = loader.load();

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Action");
			stage.setScene(new Scene(root1));

			PopupActionEditController controller = loader.<PopupActionEditController> getController();
			controller.initEventData(ev);

			controller.setInstructionCallback(instruction -> {
				if (instruction != null)
					appendInstructionToList(instruction);

				try {
					webView.getEngine().executeScript(removeHighlightJS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			stage.setOnCloseRequest(event -> {
				try {
					webView.getEngine().executeScript(removeHighlightJS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void editActionPopup(TreeItem<Instruction<?>> instruction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/caldesi/webbot/view/edit_action_popup.fxml"),
					resources);
			Parent root1 = loader.load();

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Edit Action");
			stage.setScene(new Scene(root1));

			PopupActionEditController controller = loader.<PopupActionEditController> getController();
			controller.initActionData(instruction);

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

		UIUtils.clearInstructionIndicator(item);
	}

	public void goToAddress() {
		String url = addressTextField.getText();
		if (url == null || url.isEmpty())
			return;
		url = Utils.adjustUrl(url);
		loadPage(url);
		Instruction<?> instruction = Instruction.Builder.buildByName(GoToPageInstruction.NAME);
		instruction.setArg(url);
		appendInstructionToList(instruction);
	}

	public void executeScript() {
		if (scriptTreeTable.getRoot().getChildren().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(resources.getString("scene.record.alert.emptyScript.title"));
			alert.setHeaderText(resources.getString("scene.record.alert.emptyScript.header"));
			alert.setContentText(resources.getString("scene.record.alert.emptyScript.content"));
			alert.showAndWait();
			return;
		}

		executionFinished = false;
		final ObservableList<TreeItem<Instruction<?>>> rows = scriptTreeTable.getRoot().getChildren();
		UIUtils.clearExecutionIndicators(rows);

		executeButton.setDisable(true);
		stopButton.setDisable(false);
		goButton.setDisable(true);
		addressTextField.setDisable(true);

		scriptExecutor = new ScriptExecutor(this, GLOBAL_DELAY);
		(scriptExecutorThread = new Thread(scriptExecutor)).start();
	}

	private ScriptExecutor scriptExecutor;
	private Thread scriptExecutorThread;

	@SuppressWarnings("deprecation")
	public void stopScript() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(resources.getString("scene.record.alert.stop.title"));
		alert.setHeaderText(resources.getString("scene.record.alert.stop.header"));
		alert.setContentText(resources.getString("scene.record.alert.stop.content"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			if (scriptExecutor != null && scriptExecutorThread != null) {
				if (scriptExecutorThread.isAlive()) {
					scriptExecutor.forcedStop();
					scriptExecutorThread.stop();
					scriptExecutor = null;
					scriptExecutorThread = null;
				}
			}
		} else {
			return;
		}
	}

	private void mouseListener(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			TreeItem<Instruction<?>> item = scriptTreeTable.getSelectionModel().getSelectedItem();
			if (item != null)
				editActionPopup(item);
			else
				newActionPopup(null);
		}
	}

	private void keyPressListener(final KeyEvent keyEvent) {
		final TreeItem<Instruction<?>> selectedItem = scriptTreeTable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			if (keyEvent.getCode().equals(KeyCode.DELETE)) {
				selectedItem.getParent().getChildren().remove(selectedItem);
				scriptTreeTable.getSelectionModel().clearSelection();
			}
		}
	}

	private TreeTableRow<Instruction<?>> rowFactory(TreeTableView<Instruction<?>> view) {
		TreeTableRow<Instruction<?>> row = new TreeTableRow<>();
		row.setOnDragDetected(event -> {
			if (!row.isEmpty()) {
				Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
				db.setDragView(row.snapshot(null, null));
				ClipboardContent cc = new ClipboardContent();
				cc.put(SERIALIZED_MIME_TYPE, row.getIndex());
				db.setContent(cc);
				event.consume();
			}
		});

		row.setOnDragOver(event -> {
			// Dragboard db = event.getDragboard();
			// if (acceptable(db, row)) {
			// event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			// event.consume();
			// }
			event.acceptTransferModes(TransferMode.MOVE);
			event.consume();
		});

		row.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();

			int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
			TreeItem<Instruction<?>> item = scriptTreeTable.getTreeItem(index);
			item.getParent().getChildren().remove(item);

			if (acceptable(db, row)) { // drag into block
				getTarget(row).getChildren().add(item);
				event.setDropCompleted(true);
				scriptTreeTable.getSelectionModel().select(item);
				event.consume();
			} else { // drag outside block
				TreeItem<Instruction<?>> targetRow = getTarget(row);
				// targetRow.getChildren().add(item);
				int indexOfTarget = scriptTreeTable.getRoot().getChildren().indexOf(targetRow);
				System.out.println("indexOfTarget: " + indexOfTarget);

				if (indexOfTarget >= 0)
					scriptTreeTable.getRoot().getChildren().add(indexOfTarget, item);
				else
					scriptTreeTable.getRoot().getChildren().add(item);

				event.setDropCompleted(true);
				scriptTreeTable.getSelectionModel().select(item);
				event.consume();
			}
		});

		return row;
	}

	private boolean acceptable(Dragboard db, TreeTableRow<Instruction<?>> row) {
		boolean result = false;
		if (db.hasContent(SERIALIZED_MIME_TYPE)) {
			int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
			if (row.getIndex() != index) {
				TreeItem<Instruction<?>> target = getTarget(row);
				TreeItem<Instruction<?>> item = scriptTreeTable.getTreeItem(index);
				result = !isParent(item, target) && (target.getValue() instanceof Block);
			}
		}
		return result;
	}

	private TreeItem<Instruction<?>> getTarget(TreeTableRow<Instruction<?>> row) {
		TreeItem<Instruction<?>> target = scriptTreeTable.getRoot();
		if (!row.isEmpty()) {
			target = row.getTreeItem();
		}
		return target;
	}

	// prevent loops in the tree
	private boolean isParent(TreeItem<Instruction<?>> parent, TreeItem<Instruction<?>> child) {
		boolean result = false;
		while (!result && child != null) {
			result = child.getParent() == parent;
			child = child.getParent();
		}
		return result;
	}

	private void setupScrolling() {
		scrolltimeline.setCycleCount(Timeline.INDEFINITE);
		scrolltimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), "Scoll", (ActionEvent) -> {
			dragScroll();
		}));
		scriptTreeTable.setOnDragExited(event -> {
			if (event.getY() > 0) {
				scrollDirection = 1.0 / scriptTreeTable.getExpandedItemCount();
			} else {
				scrollDirection = -1.0 / scriptTreeTable.getExpandedItemCount();
			}
			scrolltimeline.play();
		});
		scriptTreeTable.setOnDragEntered(event -> {
			scrolltimeline.stop();
		});
		scriptTreeTable.setOnDragDone(event -> {
			scrolltimeline.stop();
		});

	}

	private void dragScroll() {
		ScrollBar sb = getVerticalScrollbar();
		if (sb != null) {
			double newValue = sb.getValue() + scrollDirection;
			newValue = Math.min(newValue, 1.0);
			newValue = Math.max(newValue, 0.0);
			sb.setValue(newValue);
		}
	}

	private ScrollBar getVerticalScrollbar() {
		ScrollBar result = null;
		for (Node n : scriptTreeTable.lookupAll(".scroll-bar")) {
			if (n instanceof ScrollBar) {
				ScrollBar bar = (ScrollBar) n;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					result = bar;
				}
			}
		}
		return result;
	}

	private boolean executionFinished = true;

	public void onPageLoadSuccess() {
		webView.getEngine().executeScript(functionsJS);
	}

	public void onFinishExecution() {
		executionFinished = true;

		try {
			Document doc = webEngine.getDocument();
			Element el = doc.getDocumentElement();

			((EventTarget) el).removeEventListener("click", clickElementListener, false);
			((EventTarget) el).addEventListener("click", clickElementListener, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected boolean isFinishedExecution() {
		return executionFinished;
	}

}
