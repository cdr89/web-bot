package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;

@NoArgumentInstruction
@AssignableInstruction
public class GetElementInstruction extends JSInstruction<Void> {

	public static final String NAME = "getElement";

	public GetElementInstruction() {
		super(NAME);
	}

}
