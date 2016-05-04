package de.gigagagagigo.sagma.client.ui.fxml.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InfoController {

	@FXML
	private Button closeButton;

	Stage stage;
	public void close(){
		stage.close();
	}

	public void setStage(Stage stage){
		this.stage = stage;
	}

}
