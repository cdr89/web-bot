package it.caldesi.webbot.model.bean;

import java.util.List;

public class Instruction {

	private String label;
	private String actionName;
	private String objectXPath;
	private List<String> args;

	public Instruction() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
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

}
