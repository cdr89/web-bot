package it.caldesi.webbot.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SwitchButton extends HBox {

	private final Label label = new Label();
	private final Button button = new Button();

	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);

	private void init() {
		label.setText("OFF");

		getChildren().addAll(label, button);
		button.setOnAction((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		label.setOnMouseClicked((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		setStyle();
		bindProperties();
	}

	private void setStyle() {
		setMaxSize(60, 0);
		setWidth(60);
		label.setAlignment(Pos.CENTER);
		setStyle("-fx-background-color: #bbbbbb; -fx-text-fill: black; -fx-background-radius: 40;");
		setAlignment(Pos.CENTER_LEFT);
	}

	private void bindProperties() {
		label.prefWidthProperty().bind(widthProperty().divide(2));
		label.prefHeightProperty().bind(heightProperty());
		button.prefWidthProperty().bind(widthProperty().divide(2));
		button.prefHeightProperty().bind(heightProperty());
		button.setStyle("-fx-background-radius: 40;");
	}

	public SwitchButton() {
		init();
		switchedOn.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				label.setText("ON");
				setStyle("-fx-background-color: #33bb33; -fx-text-fill: black; -fx-background-radius: 40;");
				label.toFront();
			} else {
				label.setText("OFF");
				setStyle("-fx-background-color: #bbbbbb; -fx-text-fill: black; -fx-background-radius: 40;");
				button.toFront();
			}
		});
	}

	public void addListener(InvalidationListener listener) {
		switchedOn.addListener(listener);
	}

	public void removeListener(InvalidationListener listener) {
		switchedOn.removeListener(listener);
	}

	public void addListener(ChangeListener<? super Boolean> listener) {
		switchedOn.addListener(listener);
	}

	public void removeListener(ChangeListener<? super Boolean> listener) {
		switchedOn.removeListener(listener);
	}

	public void switched(boolean status) {
		switchedOn.set(status);
	}

	public boolean isSwitched() {
		return switchedOn.get();
	}

}