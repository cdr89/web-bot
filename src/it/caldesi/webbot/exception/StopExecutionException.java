package it.caldesi.webbot.exception;

public class StopExecutionException extends GenericException {

	private static final long serialVersionUID = -6554110655739514047L;

	public StopExecutionException() {
		super("Execution stopped");
	}

}
