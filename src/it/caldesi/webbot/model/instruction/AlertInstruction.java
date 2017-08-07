package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.ArgumentRequiredException;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.ArgumentType;
import it.caldesi.webbot.model.annotations.ArgumentType.Type;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.model.annotations.UIInstruction;
import it.caldesi.webbot.utils.UIUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebView;

@NoTargetInstruction
@UIInstruction
@ArgumentType(type = Type.STRING)
public class AlertInstruction extends Instruction<Void> {

	public static final String NAME = "alert";

	public AlertInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Alert");
		alert.setHeaderText(null);
		if (arg != null && !arg.trim().isEmpty()) {
			if (arg.startsWith("$")) // variable
				alert.setContentText(scriptExecutionContext.variableValues.get(arg.substring(1)).toString());
			else
				alert.setContentText(arg);
		} else {
			throw new ArgumentRequiredException();
		}
		UIUtils.centerAndShowPopupStage(Context.getPrimaryStage(), alert);

		return null;
	}

}
