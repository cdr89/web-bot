package it.caldesi.webbot.model.bean;

import java.util.LinkedList;
import java.util.List;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebEngine;

public abstract class Instruction<T> {

	protected static String actionName;

	protected String label;
	protected String objectXPath;
	protected List<String> args;

	public Instruction() {
		args = new LinkedList<>();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getObjectXPath() {
		return objectXPath;
	}

	public void setObjectXPath(String objectXPath) {
		this.objectXPath = objectXPath;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public abstract String toJSCode();

	public abstract T execute(WebEngine webEngine) throws GenericException;

}
