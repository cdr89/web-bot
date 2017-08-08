package it.caldesi.webbot.model.instruction.js.event;

import it.caldesi.webbot.model.annotations.EventInstruction;

@EventInstruction(name = "keypress")
public class EventKeyPressInstruction extends EventKeyboardInstruction {

	public static final String NAME = "eventKeyPress";

	public EventKeyPressInstruction() {
		super(NAME);
	}

	@Override
	protected String getEventName() {
		return "keypress";
	}

}
