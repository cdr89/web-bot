package it.caldesi.webbot.model.instruction;

import javafx.scene.web.WebEngine;

public class NullInstruction extends Instruction<Void> {

	public NullInstruction() {
		super();
	}

	@Override
	public String toJSCode() {
		return "";
	}

	@Override
	public Void execute(WebEngine webEngine) {
		return null;
	}

}
