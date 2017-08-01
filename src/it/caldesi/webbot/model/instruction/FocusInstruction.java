package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.model.annotations.NoArgumentInstruction;

@NoArgumentInstruction
public class FocusInstruction extends JSInstruction<Void> {

	public static final String NAME = "focus";

	public FocusInstruction() {
		super(NAME);
	}

}
