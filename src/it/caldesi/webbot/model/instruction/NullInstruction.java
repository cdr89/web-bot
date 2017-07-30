package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.InvisibleInstruction;
import javafx.scene.web.WebView;

@InvisibleInstruction
public class NullInstruction extends Instruction<Void> {

	public NullInstruction() {
		super();
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		return null;
	}

}
