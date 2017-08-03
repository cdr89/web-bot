package it.caldesi.webbot.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import it.caldesi.webbot.model.instruction.Instruction;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class UIUtils {

	public static void closeDialogFromEvent(Event event) {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	public static File takeScreenshot(WebView webView, String path) throws Exception {
		WritableImage image = webView.snapshot(null, null);
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		File output = new File(path);
		ImageIO.write(bufferedImage, "png", output);

		return output;
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
		public static final String GREY = "#DDDDDD";
	}
	
	public static void clearExecutionIndicators(final ObservableList<TreeItem<Instruction<?>>> rows) {
		if (rows == null)
			return;

		rows.parallelStream().forEach(row -> {
			clearInstructionIndicator(row);
			clearExecutionIndicators(row.getChildren());
		});
	}

	public static void clearInstructionIndicator(TreeItem<Instruction<?>> row) {
		row.setGraphic(new Circle(10.0, Paint.valueOf(UIUtils.Colors.TRANSPARENT)));
	}

}
