package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class GoToPageInstruction extends Instruction<Void> {

	public static final String NAME = "goToPage";

	public GoToPageInstruction() {
		super(NAME);
	}

	@Override
	public String toJSCode() {
		return ""; // TODO
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		if (args == null || args.isEmpty())
			throw new ArgumentRequiredException("URL");
		webView.getEngine().load(args.get(0));
		return null;
	}

}
