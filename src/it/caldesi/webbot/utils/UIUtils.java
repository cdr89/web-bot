package it.caldesi.webbot.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class UIUtils {

	public static void closeDialogFromEvent(Event event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	public static void takeScreenshot(WebView webView, String path) throws Exception {
		WritableImage image = webView.snapshot(null, null);
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		ImageIO.write(bufferedImage, "png", new File(path));
	}

	public static UnaryOperator<Change> getIntegerFieldFormatter(boolean onlyPositive) {
		UnaryOperator<Change> integerFilter = change -> {
			String newText = change.getControlNewText();
			String regex = "(0|[1-9][0-9]*)?";
			if (!onlyPositive)
				regex = "-?" + regex;
			if (newText.matches(regex)) {
				return change;
			}
			return null;
		};
		return integerFilter;
	}

	public static class Colors {
		public static final String TRANSPARENT = "#00000000";

		public static final String GREEN = "#68C953";
		public static final String RED = "#CF3E3E";
		public static final String YELLOW = "#EDAD18";
	}

}
