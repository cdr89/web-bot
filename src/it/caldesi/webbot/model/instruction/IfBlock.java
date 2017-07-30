package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import javafx.scene.web.WebView;

@NoTargetInstruction
@ArgumentType(type = Type.BOOLEAN)
public class IfBlock extends Block {

	public static final String NAME = "if";

	public IfBlock() {
		super(NAME);
	}

	public boolean evaluateCondition(ScriptExecutionContext scriptExecutionContext) throws GenericException {
		if (arg != null && !arg.trim().isEmpty()) {
			if (arg.startsWith("$")) { // variable condition
				Boolean variableValue = scriptExecutionContext.resolveVariableValue(arg, Boolean.class);
				if (variableValue == null)
					throw new GenericException("Variable " + arg + " not defined or initialised");
				else if (variableValue == true)
					return true;
				else
					return false;
			} else {
				throw new GenericException(arg + " is not a variable");
			}
		}
		throw new ArgumentRequiredException();
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		return null;
	}

}
