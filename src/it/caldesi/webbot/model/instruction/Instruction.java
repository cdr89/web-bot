package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public abstract class Instruction<T> {

	public String actionName;

	protected String label;
	protected String objectXPath;
	protected String arg;
	protected long delay;

	public Instruction() {
	}

	protected Instruction(String actionName) {
		this();
		this.actionName = actionName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
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

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public abstract T execute(WebView webView) throws GenericException;

	public static class Builder {

		public static Instruction<?> buildByName(String actionName) {
			return Context.getInstructionInstanceByType(actionName);
		}

	}

}
