package it.caldesi.webbot.model.instruction;

import java.io.File;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.model.annotations.UIInstruction;
import it.caldesi.webbot.utils.UIUtils;
import javafx.scene.web.WebView;

@NoTargetInstruction
@UIInstruction
public class TakeScreenshotInstruction extends Instruction<Void> {

	public static final String NAME = "takeScreenshot";

	public TakeScreenshotInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
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
