package it.caldesi.webbot.exception;

public class VariableNotInitialisedException extends GenericException {

	private static final long serialVersionUID = 4468026169613067659L;

	public VariableNotInitialisedException(String variableName) {
		super("Variable '" + variableName + "' is not initialised!");
	}

}
