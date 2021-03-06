package it.caldesi.webbot.model.instruction.js.event;

import org.w3c.dom.events.Event;

import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.instruction.EventListener;
import it.caldesi.webbot.model.instruction.js.JSInstruction;

@NoArgumentInstruction
@EventInstruction(name = "click")
public class ClickInstruction extends JSInstruction implements EventListener {

	public static final String NAME = "click";

	public ClickInstruction() {
		super(NAME);
	}

	@Override
	public void setFiedsByEvent(Event e) {
		setTarget(getTargetFromEvent(e));
	}

}
