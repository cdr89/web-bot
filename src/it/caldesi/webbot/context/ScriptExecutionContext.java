package it.caldesi.webbot.context;

import java.util.HashMap;
import java.util.Map;

public class ScriptExecutionContext {

	public Map<String, Object> variableValues = new HashMap<>();

	public <T> T resolveVariableValue(String variableName, Class<T> type) {
		if (variableName == null || variableName.trim().isEmpty())
			return null;

		if (variableName.startsWith("$"))
			variableName = variableName.substring(1);

		Object value = variableValues.get(variableName);
		if (value == null)
			return null;
		else {
			return type.cast(value);
		}
	}

}
