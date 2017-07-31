package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.model.annotations.UIInstruction;
import javafx.scene.web.WebView;

@NoTargetInstruction
@UIInstruction
public class GoToPageInstruction extends Instruction<Void> {

	public static final String NAME = "goToPage";

	public GoToPageInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		if (arg == null || arg.trim().isEmpty())
			throw new ArgumentRequiredException("URL");
			webView.getEngine().load(arg);
		return null;
	}

}
