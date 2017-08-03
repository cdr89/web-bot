package it.caldesi.webbot.model.instruction.block;

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

	// protected List<Instruction<?>> block = new LinkedList<>();

}
