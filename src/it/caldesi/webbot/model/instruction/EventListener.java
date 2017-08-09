package it.caldesi.webbot.model.instruction;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import it.caldesi.webbot.exception.IncompatibleEventTypeException;
import it.caldesi.webbot.utils.XMLUtils;

public interface EventListener {

	public void setFiedsByEvent(Event e) throws IncompatibleEventTypeException;

	public default String getTargetFromEvent(Event e) {
		Element el = (Element) e.getTarget();
		String xPath = XMLUtils.getFullXPath(el);
		return xPath;
	}

}
