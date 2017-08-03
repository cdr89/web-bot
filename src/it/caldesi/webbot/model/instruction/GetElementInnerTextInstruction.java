package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.exception.TargetRequiredException;
import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import javafx.scene.web.WebView;

@NoArgumentInstruction
@AssignableInstruction
public class GetElementInnerTextInstruction extends JSInstruction {

	public static final String NAME = "getElementInnerText";

	public GetElementInnerTextInstruction() {
		super(NAME);
	}

	@Override
	public String execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		if (target == null || target.trim().isEmpty())
			throw new TargetRequiredException();

		Object result = super.execute(scriptExecutionContext, webView);
		
		return result.toString();
	}

}
