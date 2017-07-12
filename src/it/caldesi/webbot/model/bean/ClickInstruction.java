package it.caldesi.webbot.model.bean;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebEngine;

public class ClickInstruction extends Instruction<Void> {
	
	protected static String actionName = "click";

	public ClickInstruction() {
		super();
	}

	@Override
	public String toJSCode() {
		return ""; //TODO
	}

	@Override
	public Void execute(WebEngine webEngine) throws GenericException{
		// TODO Auto-generated method stub
		return null;
	}

}
