package it.caldesi.webbot.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.model.instruction.ClickInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.utils.UIUtils;
import it.caldesi.webbot.utils.XMLUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TreeItem;
import javafx.util.converter.IntegerStringConverter;

public class PopupNewActionController implements Initializable {

	@FXML
	TextField xpathField;
	@FXML
	TextField delayField;
	@FXML
	TextField labelField;
	@FXML
	TextField argField;

	@FXML
	ComboBox<String> actionCombobox;

	@FXML
	Button okButton;
	@FXML
	Button cancelButton;

	private Consumer<Instruction<?>> instructionCallBack;

	private TreeItem<Instruction<?>> instructionItem;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		actionCombobox.setItems(Context.getInstructionsObservableList());
		actionCombobox.getSelectionModel().select(ClickInstruction.NAME);

		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (instructionCallBack != null) {
					Instruction<?> instruction = buildInstruction();
					instructionCallBack.accept(instruction);
				} else if (instructionItem != null) {
					Instruction<?> instruction = buildInstruction();
					instructionItem.setValue(instruction);
				}

				UIUtils.closeDialogFromEvent(event);
			}
		});
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				UIUtils.closeDialogFromEvent(event);
				if (instructionCallBack != null) {
					instructionCallBack.accept(null);
				}
			}
		});

		UnaryOperator<Change> integerFilter = UIUtils.getIntegerFieldFormatter(true);
		delayField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
	}

	public void initEventData(Event ev) {
		if (ev == null)
			return;
		Element el = (Element) ev.getTarget();
		String xPath = XMLUtils.getFullXPath(el);
		xpathField.setText(xPath);
	}

	public void initActionData(TreeItem<Instruction<?>> instructionItem) {
		this.instructionItem = instructionItem;
		Instruction<?> instruction = instructionItem.getValue();

		actionCombobox.setValue(instruction.getActionName());
		xpathField.setText(instruction.getObjectXPath());
		labelField.setText(instruction.getLabel());
		argField.setText(instruction.getArg());
		delayField.setText(Long.toString(instruction.getDelay()));
	}

	public void setInstructionCallback(Consumer<Instruction<?>> callback) {
		this.instructionCallBack = callback;
	}

	private Instruction<?> buildInstruction() {
		String actionName = actionCombobox.getSelectionModel().getSelectedItem();
		Instruction<?> instruction = Instruction.Builder.buildByName(actionName);
		instruction.setObjectXPath(xpathField.getText());
		instruction.setLabel(labelField.getText());
		instruction.setArg(argField.getText());
		try {
			instruction.setDelay(Long.parseLong(delayField.getText()));
		} catch (Exception e) {
			instruction.setDelay(0);
			e.printStackTrace();
		}

		return instruction;
	}

}
