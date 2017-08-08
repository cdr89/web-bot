package it.caldesi.webbot.model.instruction.js.event;

import it.caldesi.webbot.model.annotations.EventInstruction;

@EventInstruction(name = "keydown")
public class EventKeyDownInstruction extends EventKeyboardInstruction {

	public static final String NAME = "eventKeyDown";

	public EventKeyDownInstruction() {
		super(NAME);
	}

	@Override
	protected String getEventName() {
		return "keydown";
	}

}
