package it.caldesi.webbot.model.instruction;

import java.net.CookieHandler;
import java.net.CookieManager;

import it.caldesi.webbot.context.ScriptExecutionContext;
import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.annotations.NoArgumentInstruction;
import it.caldesi.webbot.model.annotations.NoTargetInstruction;
import javafx.scene.web.WebView;

@NoTargetInstruction
@NoArgumentInstruction
public class ClearCookiesInstruction extends Instruction<Void> {

	public static final String NAME = "clearCookies";

	public ClearCookiesInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(ScriptExecutionContext scriptExecutionContext, WebView webView) throws GenericException {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		// cookieManager.getCookieStore().removeAll();

		return null;
	}

}
