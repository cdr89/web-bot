package it.caldesi.webbot;

import java.io.IOException;
import java.util.ResourceBundle;

import it.caldesi.webbot.context.Context;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			Context.loadContext();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			ResourceBundle recordBundle = ResourceBundle.getBundle("it.caldesi.webbot.properties.RecordScene");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/caldesi/webbot/view/main_scene.fxml"),
					recordBundle);
			Parent root = loader.load();

			Scene scene = new Scene(root, 1200, 600);
			primaryStage.setTitle("WebBot");// TODO from properties
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
