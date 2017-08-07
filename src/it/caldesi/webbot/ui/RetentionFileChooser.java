package it.caldesi.webbot.ui;

import java.io.File;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class RetentionFileChooser {
	private static FileChooser fileChooser = null;
	private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

	public RetentionFileChooser(ExtensionFilter extensionFilter) {
		fileChooser = new FileChooser();
		fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
		fileChooser.getExtensionFilters().setAll(extensionFilter);
	}

	public File showOpenDialog() {
		return showOpenDialog(null);
	}

	public File showOpenDialog(Window ownerWindow) {
		File chosenFile = fileChooser.showOpenDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}

	public File showSaveDialog() {
		return showSaveDialog(null);
	}

	public File showSaveDialog(Window ownerWindow) {
		File chosenFile = fileChooser.showSaveDialog(ownerWindow);
		if (chosenFile != null) {
			lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
		}
		return chosenFile;
	}
}
