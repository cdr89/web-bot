package it.caldesi.webbot.model.instruction.block;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.InvisibleInstruction;
import it.caldesi.webbot.model.instruction.Instruction;
import it.caldesi.webbot.script.ScriptExecutor;
import javafx.scene.control.TreeItem;
import javafx.scene.web.WebView;

@InvisibleInstruction
public class RootBlock extends Block {

	public RootBlock() {
		super();
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		return null;
	}

	@Override
	public boolean canContinue(ScriptExecutor scriptExecutor, TreeItem<Instruction<?>> currentInstruction)
			throws GenericException {
		return true;
	}

}
