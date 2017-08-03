package it.caldesi.webbot.model.instruction.js;

import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;

@NoArgumentInstruction
@NoTargetInstruction
public class ScrollDownInstruction extends JSInstruction {

	public static final String NAME = "scrollDown";

	public ScrollDownInstruction() {
		super(NAME);
	}

}
