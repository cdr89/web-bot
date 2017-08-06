package it.caldesi.webbot.model.instruction.js;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import javafx.scene.web.WebView;

@NoTargetInstruction
@ArgumentType(type = Type.STRING)
public class InitArrayInstruction extends JSInstruction {

	public static final String NAME = "initArray";

	public InitArrayInstruction() {
		super(NAME);
	}

	@Override
	public Object execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		if (arg == null || arg.trim().isEmpty())
			throw new ArgumentRequiredException();

		List<String> variableValue = new LinkedList<>();
		StringTokenizer st = new StringTokenizer(arg, ",");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		while (st.hasMoreTokens()) {
			String nextToken = st.nextToken();
			variableValue.add(nextToken);
			stringBuilder.append('"');
			stringBuilder.append(nextToken);
			stringBuilder.append('"');
			if (st.hasMoreTokens())
				stringBuilder.append(",");
		}
		stringBuilder.append("]");
		scriptExecutionContext.variableValues.put(variable, variableValue);
		paramValues.put("array", stringBuilder.toString());

		return super.execute(scriptExecutionContext, webView);
	}

}
