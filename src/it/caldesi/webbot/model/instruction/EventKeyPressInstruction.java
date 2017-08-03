package it.caldesi.webbot.model.instruction;

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
