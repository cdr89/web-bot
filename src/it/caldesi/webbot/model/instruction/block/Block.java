package it.caldesi.webbot.model.instruction.block;

import java.util.LinkedList;
import java.util.List;

import it.caldesi.webbot.model.annotations.NoDelayInstruction;
import it.caldesi.webbot.model.instruction.Instruction;

@NoDelayInstruction
public abstract class Block extends Instruction<Void> {

	public Block() {
		super();
	}

	public Block(String name) {
		super(name);
	}

	 public List<Instruction<?>> children = new LinkedList<>();

}
