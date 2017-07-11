package it.caldesi.webbot.utils;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;

public class UIUtils {

	public static void closeDialogFromEvent(Event event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

}
