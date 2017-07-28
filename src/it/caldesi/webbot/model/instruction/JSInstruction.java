package it.caldesi.webbot.model.instruction;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.utils.FileUtils;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public abstract class JSInstruction<T> extends Instruction<T> {

	public JSInstruction(String name) {
		super(name);
	}

	protected String getJSFileName() {
		return actionName + ".js";
	}

	protected String getJSScript(Map<String, String> paramValues) throws IOException {
		URL resource = getClass().getResource("/it/caldesi/webbot/js/instructions/" + getJSFileName());
		String script = FileUtils.readFile(resource);

		if (paramValues != null)
			script = FileUtils.loadParametrizedJS(script, paramValues);

		return script;
	}

	protected void executeJS(WebView webView, Map<String, String> paramValues) throws GenericException {
		WebEngine engine = webView.getEngine();
		String script;
		try {
			script = getJSScript(paramValues);
		} catch (IOException e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
		engine.executeScript(script);
	}

}
