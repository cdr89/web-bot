package it.caldesi.webbot.model.instruction;

import org.w3c.dom.Document;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.AssignableInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import it.caldesi.webbot.utils.XMLUtils;
import javafx.scene.web.WebView;

@NoTargetInstruction
@AssignableInstruction
public class TextPresentInstruction extends Instruction<Boolean> {

	public static final String NAME = "textPresent";

	public TextPresentInstruction() {
		super(NAME);
	}

	@Override
	public Boolean execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		if (arg == null)
			arg = "";
		Document document = webView.getEngine().getDocument();
		try {
			String documentString = XMLUtils.nodeToString(document);

			return documentString.contains(arg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericException(e);
		}
	}

}
