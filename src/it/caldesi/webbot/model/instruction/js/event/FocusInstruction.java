package it.caldesi.webbot.model.instruction.js.event;

import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.instruction.js.JSInstruction;

@NoArgumentInstruction
@EventInstruction(name = "focus")
public class FocusInstruction extends JSInstruction {

	public static final String NAME = "focus";

	public FocusInstruction() {
		super(NAME);
	}

}
