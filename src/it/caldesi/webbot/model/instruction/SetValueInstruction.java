package it.caldesi.webbot.model.instruction;

import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class SetValueInstruction extends JSInstruction<Void> {

	public static final String NAME = "setValue";

	public SetValueInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
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
