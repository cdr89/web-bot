package it.caldesi.webbot.script;

import java.util.List;
import java.util.stream.Collectors;

import it.caldesi.webbot.exception.GenericException;
import it.caldesi.webbot.model.instruction.Instruction;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.web.WebView;

public class ScriptExecutor {

	// TODO
	public static void executeScript(TreeTableView<Instruction<?>> scriptTreeTable, WebView webView) {
		ObservableList<TreeItem<Instruction<?>>> instructions = scriptTreeTable.getRoot().getChildren();
		List<Instruction<?>> script = instructions.stream().map(TreeItem::getValue).collect(Collectors.toList());
		
		for (Instruction<?> instruction : script) {
			try {
				instruction.execute(webView);
			} catch (GenericException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
