package it.caldesi.webbot.model.instruction.js.event;

import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.instruction.js.JSInstruction;

@NoArgumentInstruction
@EventInstruction(name = "blur")
public class BlurInstruction extends JSInstruction {

	public static final String NAME = "blur";

	public BlurInstruction() {
		super(NAME);
	}

}
