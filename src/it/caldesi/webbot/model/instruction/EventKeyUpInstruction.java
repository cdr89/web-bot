package it.caldesi.webbot.model.instruction;

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
