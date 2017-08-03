package it.caldesi.webbot.model.instruction.js;

import it.caldesi.webbot.model.annotations.NoArgumentInstruction;

@NoArgumentInstruction
public class ClickInstruction extends JSInstruction {

	public static final String NAME = "click";

	public ClickInstruction() {
		super(NAME);
	}

}
