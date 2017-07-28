package it.caldesi.webbot.utils;

import java.util.Map;

public class JSUtils {

	private static final String START_DELIM = "\\$#";
	private static final String END_DELIM = "#\\$";

	public static String loadParametrizedJS(String js, Map<String, String> paramValues) {
		for (Map.Entry<String, String> entry : paramValues.entrySet()) {
			js = js.replaceAll(START_DELIM + entry.getKey() + END_DELIM, entry.getValue());
		}
		return js;
	}

}
