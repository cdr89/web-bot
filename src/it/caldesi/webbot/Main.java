package it.caldesi.webbot;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import it.caldesi.webbot.context.Context;
import it.caldesi.webbot.utils.UIUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
				UIUtils.setBoundsListener(primaryStage);

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

}
