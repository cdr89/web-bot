package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;

@NoArgumentInstruction
@NoTargetInstruction
public class RemoveHighlightInstruction extends JSInstruction {

	public static final String NAME = "removeHighlight";

	public RemoveHighlightInstruction() {
		super(NAME);
	}

}
