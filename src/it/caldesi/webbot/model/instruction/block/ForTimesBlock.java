package it.caldesi.webbot.model.instruction.block;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.control.TreeItem;

@ArgumentType(type = Type.INTEGER)
public class ForTimesBlock extends ForBlock {

	public static final String NAME = "forTimes";

	public ForTimesBlock() {
		super(NAME);
		this.instanceId = instanceIdCounter++;
	}

	@Override
	public boolean canContinue(ScriptExecutor scriptExecutor, TreeItem<Instruction<?>> currentInstruction)
			throws GenericException {
		ScriptExecutionContext scriptExecutionContext = scriptExecutor.getContext();
		Integer execCount = scriptExecutionContext.forCounters.get(this);

		if (execCount == null) {
			int count = Integer.parseInt(getArg());
			scriptExecutionContext.forCounters.put(this, count);
			execCount = count;
		}

		if (execCount == 0)
			return true;

		if (execCount > 0) {
			scriptExecutionContext.forCounters.put(this, execCount - 1);
			scriptExecutor.addInstructionToExecute(currentInstruction);
			scriptExecutor.addInstructionsToExecute(currentInstruction.getChildren());
		}

		return true;
	}

}
