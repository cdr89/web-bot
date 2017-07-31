package it.caldesi.webbot.model.instruction;

import java.util.Optional;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebView;

@NoTargetInstruction
@AssignableInstruction
public class UserInputInstruction extends Instruction<String> {

	public static final String NAME = "userInput";

	public UserInputInstruction() {
		super(NAME);
	}

	@Override
	public String execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		if (arg == null)
			arg = "";

		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("User input");
		dialog.setHeaderText(null);
		dialog.setContentText(arg);

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}

		return null;
	}

}
