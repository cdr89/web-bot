package it.caldesi.webbot.context;

import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.model.instruction.block.ForTimesBlock;
import javafx.scene.web.WebEngine;

public class ScriptExecutionContext {

	public Map<String, Object> variableValues = new HashMap<>();
	public Map<ForTimesBlock, Integer> forTimesCounters = new HashMap<>();

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

	public void setGlobalVariablesJS(WebEngine webEngine) {
		for (Map.Entry<String, Object> var : variableValues.entrySet()) {
			webEngine.executeScript("var " + var.getKey() + " = \"" + var.getValue() + "\";");
		}
	}

}
