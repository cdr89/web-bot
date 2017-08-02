package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.model.annotations.NoArgumentInstruction;

@NoArgumentInstruction
public class HighlightInstruction extends JSInstruction {

	public static final String NAME = "highlight";

	public HighlightInstruction() {
		super(NAME);
	}

}
