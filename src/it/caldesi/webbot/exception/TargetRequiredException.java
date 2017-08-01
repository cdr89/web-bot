package it.caldesi.webbot.exception;

public class TargetRequiredException extends GenericException {

	private static final long serialVersionUID = 4468026169613067659L;

	public TargetRequiredException() {
		super("Target required!");
	}

}
