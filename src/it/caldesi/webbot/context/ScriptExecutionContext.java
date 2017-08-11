package it.caldesi.webbot.context;

import java.util.HashMap;
import java.util.Map;

import it.caldesi.webbot.model.instruction.block.ForBlock;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.web.WebEngine;

public class ScriptExecutionContext {

	private ScriptExecutor scriptExecutor;

	public Map<String, Object> variableValues = new HashMap<>();
	public Map<ForBlock, Integer> forCounters = new HashMap<>();

	public ScriptExecutionContext(ScriptExecutor scriptExecutor) {
		this.scriptExecutor = scriptExecutor;
	}

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

	public void setGlobalDelay(long globalDelay) {
		scriptExecutor.setGlobalDelay(globalDelay);
	}

}
