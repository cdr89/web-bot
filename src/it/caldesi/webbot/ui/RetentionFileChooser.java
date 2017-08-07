package it.caldesi.webbot.ui;

import java.io.File;
import java.util.prefs.Preferences;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class RetentionFileChooser {
	private SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

	private FileChooser fileChooser = null;
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

	public RetentionFileChooser(ExtensionFilter extensionFilter) {
		getLastDir();

		fileChooser = new FileChooser();
		fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
		fileChooser.getExtensionFilters().setAll(extensionFilter);
	}

	public File showOpenDialog() {
		return showOpenDialog(null);
	}

	public File showOpenDialog(Window ownerWindow) {
		File chosenFile = fileChooser.showOpenDialog(ownerWindow);
		setLastDir(chosenFile);
		return chosenFile;
	}

	public File showSaveDialog() {
		return showSaveDialog(null);
	}

	public File showSaveDialog(Window ownerWindow) {
		File chosenFile = fileChooser.showSaveDialog(ownerWindow);
		setLastDir(chosenFile);
		return chosenFile;
	}

	public void getLastDir() {
		String lastPath = prefs.get("lastPath", null);
		if (lastPath != null)
			lastKnownDirectoryProperty.setValue(new File(lastPath));
	}

	public void setLastDir(File chosenFile) {
		if (chosenFile != null) {
			File parentFile = chosenFile.getParentFile();
			lastKnownDirectoryProperty.setValue(parentFile);
			prefs.put("lastPath", parentFile.getAbsolutePath());
		}
	}
}
