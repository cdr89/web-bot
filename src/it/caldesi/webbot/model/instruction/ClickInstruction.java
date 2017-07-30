package it.caldesi.webbot.model.instruction;

import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import javafx.scene.web.WebView;

@NoArgumentInstruction
public class ClickInstruction extends JSInstruction<Void> {

	public static final String NAME = "click";

	public ClickInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
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
