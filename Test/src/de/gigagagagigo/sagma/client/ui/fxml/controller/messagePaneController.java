package de.gigagagagigo.sagma.client.ui.fxml.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class messagePaneController {

	@FXML
	private AnchorPane mainContainer;

	@FXML
	private void initialize() {
		mainContainer.setStyle("-fx-background-color: red");
	}
}
