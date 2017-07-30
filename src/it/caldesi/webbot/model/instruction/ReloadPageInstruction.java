package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import javafx.scene.web.WebView;

@NoArgumentInstruction
@NoTargetInstruction
public class ReloadPageInstruction extends Instruction<Void> {

	public static final String NAME = "reloadPage";

	public ReloadPageInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		webView.getEngine().reload();
		return null;
	}

}
