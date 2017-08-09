package it.caldesi.webbot.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
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

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.model.instruction.GoToPageInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.model.instruction.block.Block;
import it.caldesi.webbot.model.instruction.block.RootBlock;
import it.caldesi.webbot.script.ScriptExecutor;
import it.caldesi.webbot.ui.RetentionFileChooser;
import it.caldesi.webbot.ui.SwitchButton;
import it.caldesi.webbot.utils.FileUtils;
import it.caldesi.webbot.utils.JSUtils;
import it.caldesi.webbot.utils.UIUtils;
import it.caldesi.webbot.utils.Utils;
import it.caldesi.webbot.utils.XMLUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;

public class MainController implements Initializable {

	public final static int GLOBAL_DELAY = 200;
	public final static String USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

	@FXML
	public WebView webView;

	@FXML
	public TreeTableView<Instruction<?>> scriptTreeTable;

	@FXML
	private TreeTableColumn<Instruction<?>, Boolean> treeColDisabled;
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
	public TextField addressTextField;

	@FXML
	public Button goButton;
	@FXML
	public Button executeButton;
	@FXML
	public Button stopButton;
	@FXML
	public Button saveButton;
	@FXML
	public Button loadButton;

	@FXML
	public SwitchButton recordModeSwitch;

	public WebEngine webEngine;

	private ResourceBundle resources;

	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
	private Timeline scrolltimeline = new Timeline();
	private double scrollDirection = 0;

	public ChangeListener<State> recordListener;
	private EventListener clickElementListener;

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
		initRecordMode(resources);
	}

	private void initRecordMode(ResourceBundle resourceBundle) {
		recordModeSwitch.addListener((observable, oldValue, newValue) -> {
			if (newValue)
				addRecordListener();
			else
				removeRecordListener();
		});
	}

	private void initTreeTableView(ResourceBundle resourceBundle) {
		scriptTreeTable.setPlaceholder((new Label(resourceBundle.getString("scene.record.emptyScriptList"))));

		// Column mapping
		treeColDisabled.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Instruction<?>, Boolean>, //
		ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<Instruction<?>, Boolean> param) {
				TreeItem<Instruction<?>> treeItem = param.getValue();
				Instruction<?> instruction = treeItem.getValue();
				SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(instruction.isDisabled());
				booleanProp.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						instruction.setDisabled(newValue);
					}
				});
				return booleanProp;
			}
		});
		treeColDisabled.setCellFactory(
				new Callback<TreeTableColumn<Instruction<?>, Boolean>, TreeTableCell<Instruction<?>, Boolean>>() {
					@Override
					public TreeTableCell<Instruction<?>, Boolean> call(TreeTableColumn<Instruction<?>, Boolean> p) {
						CheckBoxTreeTableCell<Instruction<?>, Boolean> cell = new CheckBoxTreeTableCell<Instruction<?>, Boolean>();
						cell.setAlignment(Pos.CENTER);
						return cell;
					}
				});
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

	private void initWebView(ResourceBundle resourceBundle) {
		System.out.println("INIT WebView");
		webEngine = webView.getEngine();
		// TODO set user agent
		// System.out.println("USER AGENT: " + webEngine.getUserAgent());
		// webEngine.setUserAgent(USER_AGENT_STRING);
		// System.out.println("USER AGENT SET ON: " + webEngine.getUserAgent());

		webEngine.setOnAlert((WebEvent<String> wEvent) -> {
			System.out.println("JS alert() message: " + wEvent.getData());
		});
	}

	private void removeRecordListener() {
		try {
			Document doc = webEngine.getDocument();
			Element el = doc.getDocumentElement();
			((EventTarget) el).removeEventListener("click", clickElementListener, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		webEngine.getLoadWorker().stateProperty().removeListener(recordListener);
	}

	private void addRecordListener() {
		addListenersOnPageLoadSuccess();
		webEngine.getLoadWorker().stateProperty().addListener(recordListener = new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				if (!isFinishedExecution())
					return;

				System.out.println("[recordListener] -----LOCATION----->" + webEngine.getLocation());
				System.out.println("[recordListener] State: " + ov.getValue().toString());
				addressTextField.setText(webEngine.getLocation());

				if (newState == State.SCHEDULED) {
					// TODO become event listener on page load
					Instruction<?> instruction = Instruction.Builder.buildByName(GoToPageInstruction.NAME);
					instruction.setArg(webEngine.getLocation());
					appendInstructionToList(instruction);
				} else if (newState == Worker.State.SUCCEEDED) {
					addListenersOnPageLoadSuccess();
				}
			}
		});
	}

	public void addListenersOnPageLoadSuccess() {
		try {
			webView.getEngine().executeScript(functionsJS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			clickElementListener = new EventListener() {
				public void handleEvent(Event ev) {
					System.out.println("event: " + ev.getType());
					try {
						removeHighlight();
					} catch (Exception e) {
						// ignore it
					}

					// highlight component
					Element el = (Element) ev.getTarget();
					String xPath = XMLUtils.getFullXPath(el);

					highlightElement(xPath);
					newActionPopup(ev);
				}
			};
			Document doc = webEngine.getDocument();
			Element el = doc.getDocumentElement();
			((EventTarget) el).addEventListener("click", clickElementListener, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				removeHighlight();
			});

			stage.setOnCloseRequest(event -> {
				removeHighlight();
			});

			UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), stage);
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

			UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), stage);
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
	}

	public void executeScript() {
		if (scriptTreeTable.getRoot().getChildren().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(resources.getString("scene.record.alert.emptyScript.title"));
			alert.setHeaderText(resources.getString("scene.record.alert.emptyScript.header"));
			alert.setContentText(resources.getString("scene.record.alert.emptyScript.content"));
			UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), alert);
			return;
		}

		executionFinished = false;
		final ObservableList<TreeItem<Instruction<?>>> rows = scriptTreeTable.getRoot().getChildren();
		UIUtils.clearExecutionIndicators(rows);
		recordModeSwitch.switched(false);
		disableControls();

		scriptExecutor = new ScriptExecutor(this, GLOBAL_DELAY);
		(scriptExecutorThread = new Thread(scriptExecutor)).start();
	}

	public void disableControls() {
		executeButton.setDisable(true);
		saveButton.setDisable(true);
		loadButton.setDisable(true);
		stopButton.setDisable(false);
		goButton.setDisable(true);
		addressTextField.setDisable(true);
		recordModeSwitch.setDisable(true);
	}

	public void enableControls() {
		executeButton.setDisable(false);
		stopButton.setDisable(true);
		goButton.setDisable(false);
		addressTextField.setDisable(false);
		saveButton.setDisable(false);
		loadButton.setDisable(false);
		recordModeSwitch.setDisable(false);
	}

	private ScriptExecutor scriptExecutor;
	private Thread scriptExecutorThread;

	@SuppressWarnings("deprecation")
	public void stopScript() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(resources.getString("scene.record.alert.stop.title"));
		alert.setHeaderText(resources.getString("scene.record.alert.stop.header"));
		alert.setContentText(resources.getString("scene.record.alert.stop.content"));

		Optional<ButtonType> result = UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), alert);
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

	public void saveScript() {
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WebBot files (*.wbt)", "*.wbt");
		RetentionFileChooser fileChooser = new RetentionFileChooser(extFilter);

		// Show save file dialog
		Window window = webView.getParent().getScene().getWindow();
		File file = fileChooser.showSaveDialog(window);

		if (file == null)
			return;

		try {
			TreeItem<Instruction<?>> root = scriptTreeTable.getRoot();
			Utils.saveScript(root, file);
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Save");
			alert.setHeaderText("Cannot save the file");
			alert.setContentText("Cannot save the file " + file.getAbsolutePath());

			GridPane expContent = setAlertExceptionField(e);
			alert.getDialogPane().setExpandableContent(expContent);

			UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), alert);
		}
	}

	public void loadScript() {
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("WebBot files (*.wbt)", "*.wbt");
		RetentionFileChooser fileChooser = new RetentionFileChooser(extFilter);

		// Show save file dialog
		Window window = webView.getParent().getScene().getWindow();
		File file = fileChooser.showOpenDialog(window);

		if (file == null)
			return;

		try {
			TreeItem<Instruction<?>> loadedScript = Utils.loadScript(file);
			scriptTreeTable.setRoot(loadedScript);
			UIUtils.clearExecutionIndicators(loadedScript.getChildren());
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Load");
			alert.setHeaderText("Cannot load the file");
			alert.setContentText("Cannot load the file " + file.getAbsolutePath());

			GridPane expContent = setAlertExceptionField(e);
			alert.getDialogPane().setExpandableContent(expContent);

			UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), alert);
		}
	}

	public GridPane setAlertExceptionField(Exception e) {
		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		return expContent;
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
		try {
			webView.getEngine().executeScript(functionsJS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onFinishExecution() {
		executionFinished = true;
	}

	protected boolean isFinishedExecution() {
		return executionFinished;
	}

	public void historyBack() {
		// TODO verify listener enabled/disabled
		final WebHistory history = webView.getEngine().getHistory();
		try {
			history.go(-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void historyForward() {
		// TODO verify listener enabled/disabled
		final WebHistory history = webView.getEngine().getHistory();
		try {
			history.go(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeHighlight() {
		try {
			webView.getEngine().executeScript(removeHighlightJS);
		} catch (Exception e) {
			// do-nothing
		}
	}

	public void highlightElement(String xPath) {
		try {
			Map<String, String> paramValues = new HashMap<>();
			paramValues.put("xPath", xPath);
			String highlightCompiled = JSUtils.loadParametrizedJS(highlightJS, paramValues);
			webView.getEngine().executeScript(highlightCompiled);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
