package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
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
		// TODO Auto-generated method stub
		return null;
	}

}
