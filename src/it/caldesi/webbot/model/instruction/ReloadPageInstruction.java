package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class ReloadPageInstruction extends Instruction<Void> {

	public static final String NAME = "reloadPage";

	public ReloadPageInstruction() {
		super(NAME);
	}

	@Override
	public String toJSCode() {
		return ""; // TODO
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		webView.getEngine().reload();
		return null;
	}

}
