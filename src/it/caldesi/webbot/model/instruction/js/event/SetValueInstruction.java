package it.caldesi.webbot.model.instruction.js.event;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLInputElement;

import com.sun.webkit.dom.EventImpl;

import it.caldesi.webbot.exception.IncompatibleEventTypeException;
import it.caldesi.webbot.model.annotations.EventInstruction;
import it.caldesi.webbot.model.instruction.EventListener;
import it.caldesi.webbot.model.instruction.js.JSInstruction;

@EventInstruction(name = "change")
public class SetValueInstruction extends JSInstruction implements EventListener {

	public static final String NAME = "setValue";

	public SetValueInstruction() {
		super(NAME);
	}

	@Override
	public void setFiedsByEvent(Event e) throws IncompatibleEventTypeException {
		setTarget(getTargetFromEvent(e));

		EventImpl event = (EventImpl) e;
		EventTarget eventTarget = event.getTarget();
		if (eventTarget instanceof HTMLInputElement) {
			HTMLInputElement htmlInputElement = (HTMLInputElement) eventTarget;
			arg = htmlInputElement.getValue();
		} else {
			System.out.println("CHANGED BUT NOT RECOGNISED: " + eventTarget);
			throw new IncompatibleEventTypeException(NAME);
		}
	}

}
