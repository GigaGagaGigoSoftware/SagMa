package de.gigagagagigo.sagma.client.ui.fxml;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader();
			loader.setResources(ResourceBundle.getBundle("language\\chat", new Locale("en", "EN")));
			BorderPane root = loader.load(getClass().getResource("chat.fxml").openStream());
			primaryStage.setTitle("SagMa");
			Scene scene = new Scene(root,550,550);
			primaryStage.setMinHeight(550);
			primaryStage.setMinWidth(350);
			scene.getStylesheets().add(getClass().getResource("blackstyle.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
