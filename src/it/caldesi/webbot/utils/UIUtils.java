package it.caldesi.webbot.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import it.caldesi.webbot.model.instruction.Instruction;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TreeItem;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
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

	public static void centerAndShowPopupStage(Stage primaryStage, Stage popUpStage) {
		// Calculate the center position of the parent Stage
		double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
		double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;

		// Hide the pop-up stage before it is shown and becomes relocated
		popUpStage.setOnShowing(ev -> popUpStage.hide());

		// Relocate the pop-up Stage
		popUpStage.setOnShown(ev -> {
			popUpStage.setX(centerXPosition - popUpStage.getWidth() / 2d);
			popUpStage.setY(centerYPosition - popUpStage.getHeight() / 2d);
			popUpStage.show();
		});

		popUpStage.showAndWait();
	}

	public static Optional<ButtonType> centerAndShowPopupStage(Stage primaryStage, Alert alert) {
		// Calculate the center position of the parent Stage
		double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
		double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;

		// Hide the pop-up stage before it is shown and becomes relocated
		// alert.setOnShowing(ev -> alert.hide());

		// Relocate the pop-up Stage
		alert.setOnShown(ev -> {
			alert.setX(centerXPosition - alert.getWidth() / 2d);
			alert.setY(centerYPosition - alert.getHeight() / 2d);
			alert.show();
		});

		return alert.showAndWait();
	}

	public static void setBoundsListener(Stage primaryStage, int allowedError) {
		ChangeListener<Number> boundsListener = (obs, oldValue, newValue) -> {
			Bounds allScreenBounds = computeAllScreenBounds();
			double x = primaryStage.getX();
			double y = primaryStage.getY();
			double w = primaryStage.getWidth();
			double h = primaryStage.getHeight();
			if (x < allScreenBounds.getMinX() - allowedError) {
				primaryStage.setX(allScreenBounds.getMinX());
			}
			if (x + w > allScreenBounds.getMaxX() + allowedError) {
				primaryStage.setX(allScreenBounds.getMaxX() - w);
			}
			if (y < allScreenBounds.getMinY() - allowedError) {
				primaryStage.setY(allScreenBounds.getMinY());
			}
			if (y + h > allScreenBounds.getMaxY() + allowedError) {
				primaryStage.setY(allScreenBounds.getMaxY() - h);
			}
		};
		primaryStage.xProperty().addListener(boundsListener);
		primaryStage.yProperty().addListener(boundsListener);
		primaryStage.widthProperty().addListener(boundsListener);
		primaryStage.heightProperty().addListener(boundsListener);
	}

	public static Bounds computeAllScreenBounds() {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D screenBounds = screen.getBounds();
			if (screenBounds.getMinX() < minX) {
				minX = screenBounds.getMinX();
			}
			if (screenBounds.getMinY() < minY) {
				minY = screenBounds.getMinY();
			}
			if (screenBounds.getMaxX() > maxX) {
				maxX = screenBounds.getMaxX();
			}
			if (screenBounds.getMaxY() > maxY) {
				maxY = screenBounds.getMaxY();
			}
		}
		return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
	}

}
