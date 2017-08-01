package it.caldesi.webbot.model.instruction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.UIInstruction;
import it.caldesi.webbot.utils.FileUtils;
import it.caldesi.webbot.utils.JSUtils;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

@UIInstruction
public abstract class JSInstruction<T> extends Instruction<T> {

	public JSInstruction(String name) {
		super(name);
	}

	protected String getJSFileName() {
		return actionName + ".js";
	}

	protected String getJSScript(Map<String, String> paramValues) throws IOException {
		String jsPath = "/it/caldesi/webbot/js/instructions/" + getJSFileName();
		String script = FileUtils.readResource(jsPath);

		if (paramValues != null)
			script = JSUtils.loadParametrizedJS(script, paramValues);

		return script;
	}

	protected void executeJS(WebView webView, Map<String, String> paramValues) throws GenericException {
		WebEngine engine = webView.getEngine();
		try {
			String script = getJSScript(paramValues);
			engine.executeScript(script);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
	}

	@Override
	public T execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		setParams();
		executeJS(webView, paramValues);

		return null;
	}

	protected Map<String, String> paramValues = new HashMap<>();

	protected void setParams() {
		// TODO escape the XPath
		paramValues.put("objectXPath", objectXPath);
		// TODO escape value
		paramValues.put("value", arg);
	}

}
