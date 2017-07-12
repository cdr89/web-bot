package it.caldesi.webbot.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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

public class PopupNewActionController implements Initializable {

	@FXML
	TextField xpathField;

	@FXML
	ComboBox<String> actionCombobox;

	@FXML
	Button okButton;
	@FXML
	Button cancelButton;

	private Consumer<Instruction<?>> instructionCallBack;

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

					UIUtils.closeDialogFromEvent(event);
				}
			}
		});
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				UIUtils.closeDialogFromEvent(event);
			}
		});
	}

	public void initEventData(Event ev) {
		Element el = (Element) ev.getTarget();
		String xPath = XMLUtils.getFullXPath(el);
		xpathField.setText(xPath);
	}

	public void setInstructionCallback(Consumer<Instruction<?>> callback) {
		this.instructionCallBack = callback;
	}

	private Instruction<?> buildInstruction() {
		String actionName = actionCombobox.getSelectionModel().getSelectedItem();
		Instruction<?> instruction = Instruction.Builder.buildByName(actionName);
		instruction.setObjectXPath(xpathField.getText());

		return instruction;
	}

}
