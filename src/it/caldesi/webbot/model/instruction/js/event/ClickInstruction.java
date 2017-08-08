package it.caldesi.webbot.model.instruction.js.event;

import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.instruction.js.JSInstruction;

@NoArgumentInstruction
@EventInstruction(name = "click")
public class ClickInstruction extends JSInstruction {

	public static final String NAME = "click";

	public ClickInstruction() {
		super(NAME);
	}

}
