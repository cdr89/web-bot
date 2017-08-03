package it.caldesi.webbot.model.instruction;

import javafx.scene.web.WebEngine;

public abstract class EventKeyboardInstruction extends JSInstruction {

	public EventKeyboardInstruction(String actionName) {
		super(actionName);
	}

	@Override
	protected String getJSFileName() {
		return "keyboardEvent.js";
	}

	@Override
	protected void setParams(WebEngine engine) {
		super.setParams(engine);
		paramValues.put("eventName", '"' + getEventName() + '"');
	}

	protected abstract String getEventName();

}
