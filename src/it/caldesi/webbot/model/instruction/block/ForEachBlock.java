package it.caldesi.webbot.model.instruction.block;

import java.util.List;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.control.TreeItem;

@ArgumentType(type = Type.STRING)
@AssignableInstruction
public class ForEachBlock extends ForBlock {

	public static final String NAME = "forEach";

	public ForEachBlock() {
		super(NAME);
		this.instanceId = instanceIdCounter++;
	}

	@Override
	public boolean canContinue(ScriptExecutor scriptExecutor, TreeItem<Instruction<?>> currentInstruction)
			throws GenericException {
		if (this.variable == null || this.variable.isEmpty())
			throw new ArgumentRequiredException("variable");

		ScriptExecutionContext scriptExecutionContext = scriptExecutor.getContext();
		if (arg != null && !arg.trim().isEmpty()) {
			if (arg.startsWith("$")) { // variable condition
				List<?> variableValue = scriptExecutionContext.resolveVariableValue(arg, List.class);
				if (variableValue == null)
					throw new GenericException("Variable " + arg + " not defined or initialised");
				else if (variableValue.isEmpty())
					return true;
				else { // check size
					Integer index = scriptExecutionContext.forCounters.get(this);
					if (index == null) {
						scriptExecutionContext.forCounters.put(this, 0);
						index = 0;
					} else {
						index++;
						scriptExecutionContext.forCounters.put(this, index);
					}
					if (index >= variableValue.size()) { // end of cycle
						scriptExecutionContext.variableValues.remove(variable);
						return true;
					} else {
						Object value = variableValue.get(index);
						scriptExecutionContext.variableValues.put(variable, value);
						if (index < variableValue.size() - 1)
							scriptExecutor.addInstructionToExecute(currentInstruction);
						scriptExecutor.addInstructionsToExecute(currentInstruction.getChildren());
						return true;
					}
				}
			} else {
				throw new GenericException(arg + " is not a variable");
			}
		}
		throw new ArgumentRequiredException();
	}

}
