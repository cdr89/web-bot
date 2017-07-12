package it.caldesi.webbot.exception;

public class GenericException extends Exception {

	private static final long serialVersionUID = -2908818516998015542L;

	protected String message;

	public GenericException() {
		super();
	}

	public GenericException(String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
