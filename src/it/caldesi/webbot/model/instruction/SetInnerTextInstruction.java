package it.caldesi.webbot.model.instruction;

import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class SetInnerTextInstruction extends JSInstruction<Void> {

	public static final String NAME = "setInnerText";

	public SetInnerTextInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		if (arg == null)
			arg = "";

		Map<String, String> paramValues = new HashMap<>();
		// TODO escape the XPath
		paramValues.put("objectXPath", objectXPath);
		// TODO escape value
		paramValues.put("value", arg);
		executeJS(webView, paramValues);

		return null;
	}

}
