package it.caldesi.webbot.model.instruction.js.event;

import it.caldesi.webbot.model.annotations.EventInstruction;

@EventInstruction(name = "keyup")
public class EventKeyUpInstruction extends EventKeyboardInstruction {

	public static final String NAME = "eventKeyUp";

	public EventKeyUpInstruction() {
		super(NAME);
	}

	@Override
	protected String getEventName() {
		return "keyup";
	}

}
