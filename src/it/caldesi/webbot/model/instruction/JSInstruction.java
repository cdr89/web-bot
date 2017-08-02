package it.caldesi.webbot.model.instruction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.UIInstruction;
import it.caldesi.webbot.utils.FileUtils;
import it.caldesi.webbot.utils.JSUtils;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

@UIInstruction
public abstract class JSInstruction extends Instruction<Object> {

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

	protected Object executeJS(WebView webView, Map<String, String> paramValues) throws GenericException {
		WebEngine engine = webView.getEngine();
		try {
			String script = getJSScript(paramValues);
			System.out.println("Executing script: " + script);
			return engine.executeScript(script);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
	}

	@Override
	public Object execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		setParams(webView.getEngine());
		return executeJS(webView, paramValues);
	}

	protected Map<String, String> paramValues = new HashMap<>();

	protected void setParams(WebEngine engine) {
		// TODO escape the XPath
		if (target != null && !target.trim().isEmpty()) {
			if (target.startsWith("$")) { // variable
				engine.executeScript("var webbot_target = " + target.substring(1) + ";");
				paramValues.put("target", target.substring(1));
			} else { // value
				engine.executeScript("var webbot_target = getElementByXPath(\"" + target + "\");");
				paramValues.put("target", '"' + target + '"');
			}
		}
		// TODO escape value
		if (arg != null && !arg.trim().isEmpty()) {
			if (arg.startsWith("$")) // variable
				paramValues.put("value", arg.substring(1));
			else { // value
				Type argumentType = Context.getArgumentType(actionName);
				if (argumentType != null && (argumentType == Type.BOOLEAN || argumentType == Type.INTEGER))
					paramValues.put("value", arg);
				else
					paramValues.put("value", '"' + arg + '"');

			}
		}
		if (variable != null && !variable.trim().isEmpty()) {
			paramValues.put("variable", variable);
		}
	}

}
