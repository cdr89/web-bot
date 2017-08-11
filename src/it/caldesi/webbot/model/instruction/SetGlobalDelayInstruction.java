package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import javafx.scene.web.WebView;

@NoTargetInstruction
@ArgumentType(type = Type.INTEGER, onlyPositive = true)
public class SetGlobalDelayInstruction extends Instruction<Void> {

	public static final String NAME = "setGlobalDelay";

	public SetGlobalDelayInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		try {
			long globalDelay = Long.parseLong(arg);
			scriptExecutionContext.setGlobalDelay(globalDelay);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
		return null;
	}

}
