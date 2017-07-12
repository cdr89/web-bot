package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.utils.UIUtils;
import javafx.scene.web.WebView;

public class TakeScreenshotInstruction extends Instruction<Void> {

	public static final String NAME = "takeScreenshot";

	public TakeScreenshotInstruction() {
		super(NAME);
	}

	@Override
	public String toJSCode() {
		return "";
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		if (args == null || args.isEmpty())
			throw new ArgumentRequiredException("filePath");
		try {
			UIUtils.takeScreenshot(webView, args.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
		return null;
	}

}
