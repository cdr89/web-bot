package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.model.annotations.UIInstruction;

@NoTargetInstruction
@UIInstruction
public abstract class PageInstruction extends Instruction<Void> {

	public PageInstruction() {
	}

	protected PageInstruction(String actionName) {
		super(actionName);
	}

}
