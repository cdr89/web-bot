package it.caldesi.webbot.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import it.caldesi.webbot.model.bean.Instruction;
import it.caldesi.webbot.utils.UIUtils;
import it.caldesi.webbot.utils.XMLUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PopupNewActionController implements Initializable {

	@FXML
	TextField xpathField;

	@FXML
	Button okButton;

	private Consumer<Instruction> instructionCallBack;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (instructionCallBack != null) {
					Instruction instruction = buildInstruction();
					instructionCallBack.accept(instruction);

					UIUtils.closeDialogFromEvent(event);
				}
			}
		});
	}

	public void initEventData(Event ev) {
		Element el = (Element) ev.getTarget();
		String xPath = XMLUtils.getFullXPath(el);
		xpathField.setText(xPath);
	}

	public void setInstructionCallback(Consumer<Instruction> callback) {
		this.instructionCallBack = callback;
	}

	private Instruction buildInstruction() {
		Instruction instruction = new Instruction();
		instruction.setActionName("click"); // TODO take this as input
		instruction.setObjectXPath(xpathField.getText());

		return instruction;
	}

}
