package it.caldesi.webbot.model.instruction;

public abstract class Block extends Instruction<Void> {

	public Block() {
		super();
	}

	public Block(String name) {
		super(name);
	}

	// protected List<Instruction<?>> block = new LinkedList<>();

}
