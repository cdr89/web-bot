package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.model.annotations.NoArgumentInstruction;

@NoArgumentInstruction
public class BlurInstruction extends JSInstruction {

	public static final String NAME = "blur";

	public BlurInstruction() {
		super(NAME);
	}

}
