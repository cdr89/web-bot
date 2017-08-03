package it.caldesi.webbot.model.instruction;

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
