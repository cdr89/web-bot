package it.caldesi.webbot.model.instruction.js.event;

import org.w3c.dom.events.Event;

import com.sun.webkit.dom.KeyboardEventImpl;

import it.caldesi.webbot.model.instruction.EventListener;
import it.caldesi.webbot.model.instruction.js.JSInstruction;
import javafx.scene.web.WebEngine;

public abstract class EventKeyboardInstruction extends JSInstruction implements EventListener {

	protected Boolean bubbles;
	protected Boolean cancelable;
	protected Boolean ctrlKey;
	protected Boolean altKey;
	protected Boolean shiftKey;
	protected Boolean metaKey;
	protected Integer keyCode;
	protected Integer charCode;

	public EventKeyboardInstruction(String actionName) {
		super(actionName);
	}

	@Override
	protected String getJSFileName() {
		return "keyboardEvent.js";
	}

	protected abstract String getEventName();

	@Override
	public void setFiedsByEvent(Event e) {
		setTarget(getTargetFromEvent(e));

		KeyboardEventImpl keyEvent = ((KeyboardEventImpl) e);
		arg = keyEvent.getKeyIdentifier();
		bubbles = keyEvent.getBubbles();
		cancelable = keyEvent.getCancelable();
		ctrlKey = keyEvent.getCtrlKey();
		altKey = keyEvent.getAltKey();
		shiftKey = keyEvent.getShiftKey();
		metaKey = keyEvent.getMetaKey();
		keyCode = keyEvent.getKeyCode();
		charCode = keyEvent.getCharCode();
	}

	@Override
	protected void setParams(WebEngine engine) {
		super.setParams(engine);
		paramValues.put("eventName", '"' + getEventName() + '"');

		paramValues.put("bubbles", bubbles.toString());
		paramValues.put("cancelable", cancelable.toString());
		paramValues.put("ctrlKey", ctrlKey.toString());
		paramValues.put("altKey", altKey.toString());
		paramValues.put("shiftKey", shiftKey.toString());
		paramValues.put("metaKey", metaKey.toString());
		paramValues.put("keyCode", keyCode.toString());
		paramValues.put("charCode", charCode.toString());
	}

}
