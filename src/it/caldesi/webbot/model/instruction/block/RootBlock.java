package it.caldesi.webbot.model.instruction.block;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.InvisibleInstruction;
import javafx.scene.web.WebView;

@InvisibleInstruction
public class RootBlock extends Block {

	public RootBlock() {
		super();
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		return null;
	}

}
