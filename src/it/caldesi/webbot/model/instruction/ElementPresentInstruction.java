package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;

@NoArgumentInstruction
@AssignableInstruction
public class ElementPresentInstruction extends JSInstruction<Boolean> {

	public static final String NAME = "elementPresent";

	public ElementPresentInstruction() {
		super(NAME);
	}

	@Override
	public Boolean execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		try {
			super.execute(scriptExecutionContext, webView);

			return true;
		} catch (JSException e) {
			String message = e.getMessage();
			if (message.startsWith("TypeError: null is not an object")) {
				return false;
			} else {
				e.printStackTrace();
				throw new GenericException(e);
			}
		}
	}

}
