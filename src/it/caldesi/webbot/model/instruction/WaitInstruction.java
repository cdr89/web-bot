package it.caldesi.webbot.model.instruction;

import it.caldesi.webbot.exception.GenericException;
import javafx.scene.web.WebView;

public class WaitInstruction extends Instruction<Void> {

	public static final String NAME = "wait";

	public WaitInstruction() {
		super(NAME);
	}

	@Override
	public Void execute(WebView webView) throws GenericException {
		try{
			Thread.sleep(Long.parseLong(arg));
		}catch(Exception e){
			e.printStackTrace();
			throw new GenericException(e);
		}
		return null;
	}

}
