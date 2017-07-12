package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class NullInstruction extends Instruction<Void> {

	public NullInstruction() {
		super();
	}

	@Override
	public String toJSCode() {
		return "";
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		return null;
	}

}
