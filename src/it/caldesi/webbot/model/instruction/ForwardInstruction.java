package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

@ArgumentType(type = Type.INTEGER, onlyPositive = true)
public class ForwardInstruction extends PageInstruction {

	public static final String NAME = "forward";

	public ForwardInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		final WebHistory history = webView.getEngine().getHistory();
		try {
			int offset = Integer.parseInt(arg);
			history.go(offset);
		} catch (Exception e) {
			throw new GenericException(e);
		}

		return null;
	}

}
