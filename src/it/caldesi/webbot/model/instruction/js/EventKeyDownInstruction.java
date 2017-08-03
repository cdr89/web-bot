package it.caldesi.webbot.model.instruction.js;

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
