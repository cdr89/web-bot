package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ClickInstruction extends Instruction<Void> {

	public static final String NAME = "click";

	public ClickInstruction() {
		super(NAME);
	}

	@Override
	public String toJSCode() {
		return ""; // TODO
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		WebEngine engine = webView.getEngine();
		// TODO escape the XPath
		String script = "var element = document.evaluate( \"" + objectXPath
				+ "\" ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue.click();";
		engine.executeScript(script);

		return null;
	}

}
