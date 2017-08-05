package it.caldesi.webbot.model.instruction.block;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.control.TreeItem;

public class WhileBlock extends EvaluableBlock {

	public static final String NAME = "while";

	public WhileBlock() {
		super(NAME);
	}

	@Override
	public boolean canContinue(ScriptExecutor scriptExecutor, TreeItem<Instruction<?>> currentInstruction)
			throws GenericException {
		ScriptExecutionContext scriptExecutionContext = scriptExecutor.getContext();
		if (evaluateCondition(scriptExecutionContext)) {
			scriptExecutor.addInstructionToExecute(currentInstruction);
			scriptExecutor.addInstructionsToExecute(currentInstruction.getChildren());
		}

		return true;
	}
}
