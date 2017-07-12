package it.caldesi.webbot.exception;

import java.util.Arrays;
import java.util.List;

public class ArgumentRequiredException extends GenericException {

	private static final long serialVersionUID = 4468026169613067659L;

	public ArgumentRequiredException() {
		super("Argument required!");
	}

	public ArgumentRequiredException(String... argName) {
		this();
		if (argName != null && argName.length > 0) {
			if (argName.length == 1) {
				setMessage("Argument required: " + argName[0]);
			} else {
				List<String> argNameList = Arrays.asList(argName);
				setMessage("Arguments required: " + argNameList.toString());
			}
		}
	}

}
