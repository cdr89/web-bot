package it.caldesi.webbot.model.instruction.js.event;

import org.w3c.dom.events.Event;

import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.instruction.EventListener;
import it.caldesi.webbot.model.instruction.js.JSInstruction;

@NoArgumentInstruction
@EventInstruction(name = "focus")
public class FocusInstruction extends JSInstruction implements EventListener {

	public static final String NAME = "focus";

	public FocusInstruction() {
		super(NAME);
	}

	@Override
	public void setFiedsByEvent(Event e) {
		setTarget(getTargetFromEvent(e));
	}

}