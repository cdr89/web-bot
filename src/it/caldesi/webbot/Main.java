package it.caldesi.webbot;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import it.caldesi.webbot.context.Context;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		try {
			Context.loadContext(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ResourceBundle recordBundle = ResourceBundle.getBundle("it.caldesi.webbot.properties.RecordScene");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/caldesi/webbot/view/main_scene.fxml"),
					recordBundle);
			Parent root = loader.load();

			Scene scene = new Scene(root, 1200, 600);
			primaryStage.setTitle("WebBot");// TODO from properties
			primaryStage.setScene(scene);

			double x_saved = prefs.getDouble("x", -1);
			double y_saved = prefs.getDouble("y", -1);
			if (x_saved == -1 && y_saved == -1) {
				System.out.println("No position saved for main window");
			} else {
				// verify not out of screen
				ChangeListener<Number> boundsListener = (obs, oldValue, newValue) -> {
					Bounds allScreenBounds = computeAllScreenBounds();
					double x = primaryStage.getX();
					double y = primaryStage.getY();
					double w = primaryStage.getWidth();
					double h = primaryStage.getHeight();
					if (x < allScreenBounds.getMinX()) {
						primaryStage.setX(allScreenBounds.getMinX());
					}
					if (x + w > allScreenBounds.getMaxX()) {
						primaryStage.setX(allScreenBounds.getMaxX() - w);
					}
					if (y < allScreenBounds.getMinY()) {
						primaryStage.setY(allScreenBounds.getMinY());
					}
					if (y + h > allScreenBounds.getMaxY()) {
						primaryStage.setY(allScreenBounds.getMaxY() - h);
					}
				};
				primaryStage.xProperty().addListener(boundsListener);
				primaryStage.yProperty().addListener(boundsListener);
				primaryStage.widthProperty().addListener(boundsListener);
				primaryStage.heightProperty().addListener(boundsListener);

				// restore old position
				primaryStage.setX(x_saved);
				primaryStage.setY(y_saved);
			}

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		double x = primaryStage.getX();
		double y = primaryStage.getY();
		prefs.putDouble("x", x);
		prefs.putDouble("y", y);
		System.out.println("Saved window position: x = " + x + "; y = " + y + ";");
	}

	public static void main(String[] args) {
		launch(args);
	}

	private Bounds computeAllScreenBounds() {
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
