package it.caldesi.webbot.model.instruction;

import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class ClickInstruction extends JSInstruction<Void> {

	public static final String NAME = "click";

	public ClickInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		Map<String, String> paramValues = new HashMap<>();
		// TODO escape the XPath
		paramValues.put("objectXPath", objectXPath);
		executeJS(webView, paramValues);

		return null;
	}

	@Override
	protected String getJSFileName() {
		return "click.js";
	}

}
