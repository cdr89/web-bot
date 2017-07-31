package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import javafx.scene.web.WebView;

@NoArgumentInstruction
public class ReloadPageInstruction extends PageInstruction {

	public static final String NAME = "reloadPage";

	public ReloadPageInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		webView.getEngine().reload();
		return null;
	}

}
