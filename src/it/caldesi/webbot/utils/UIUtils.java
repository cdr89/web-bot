package it.caldesi.webbot.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Node;
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

}
