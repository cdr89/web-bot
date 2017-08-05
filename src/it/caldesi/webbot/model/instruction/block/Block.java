package it.caldesi.webbot.model.instruction.block;

import java.util.LinkedList;
import java.util.List;

import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoDelayInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.control.TreeItem;

@NoDelayInstruction
public abstract class Block extends Instruction<Void> {

	public Block() {
		super();
	}

	public Block(String name) {
		super(name);
	}

	public List<Instruction<?>> children = new LinkedList<>();

	public abstract boolean canContinue(ScriptExecutor scriptExecutor, TreeItem<Instruction<?>> currentInstruction)
			throws GenericException;

}
