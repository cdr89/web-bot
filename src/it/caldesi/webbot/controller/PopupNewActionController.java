package it.caldesi.webbot.controller;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import it.caldesi.webbot.utils.XMLUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PopupNewActionController {

	@FXML
	TextField xpathField;

	public void initEventData(Event ev) {
		Element el = (Element) ev.getTarget();
		String xPath = XMLUtils.getFullXPath(el);
		xpathField.setText(xPath);
	}

}
