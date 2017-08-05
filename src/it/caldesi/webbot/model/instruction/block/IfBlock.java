package it.caldesi.webbot.model.instruction.block;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.control.TreeItem;

public class IfBlock extends EvaluableBlock {

	public static final String NAME = "if";

	public IfBlock() {
		super(NAME);
	}

	@Override
	public boolean canContinue(ScriptExecutor scriptExecutor, TreeItem<Instruction<?>> currentInstruction)
			throws GenericException {
		ScriptExecutionContext scriptExecutionContext = scriptExecutor.getContext();
		if (evaluateCondition(scriptExecutionContext))
			scriptExecutor.addInstructionsToExecute(currentInstruction.getChildren());

		return true;
	}

}
