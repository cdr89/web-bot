package it.caldesi.webbot.model.instruction;

import java.io.File;

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
	public Void execute(WebView webView) throws GenericException {
		if (arg == null || arg.trim().isEmpty())
			throw new ArgumentRequiredException("filePath");
		try {
			File output = UIUtils.takeScreenshot(webView, arg);
			System.out.println("Screenshot saved: " + output.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
		return null;
	}

}
