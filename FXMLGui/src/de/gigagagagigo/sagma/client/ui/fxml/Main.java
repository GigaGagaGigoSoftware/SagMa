package de.gigagagagigo.sagma.client.ui.fxml;

import java.util.Locale;
import java.util.ResourceBundle;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.client.ui.fxml.controller.LogInController;
import de.gigagagagigo.sagma.packets.DisconnectPacket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private static ResourceBundle language;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * starting method shows the login screen
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.getIcons().addAll(new Image(getClass().getResourceAsStream("img/SagMaIcon64.png")),new Image(getClass().getResourceAsStream("img/SagMaIcon16.png")));
		language = ResourceBundle.getBundle("de.gigagagagigo.sagma.client.ui.fxml.language.chat", new Locale(
			"en", "EN"));
		showLogIn();
	}

	/**
	 * shows the chat scene after the login 
	 * @param client SagMaClient
	 * @param username String
	 */
	public static void showChat(SagMaClient client, String username) {
		try {

			Stage primaryStage = new Stage();
			primaryStage.setTitle("SagMa");
			primaryStage.setMinHeight(550);
			primaryStage.setMinWidth(350);

			ChatController controller = new ChatController(client, username);
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/gigagagagigo/sagma/client/ui/fxml/chat.fxml"));
			loader.setController(controller);
			loader.setResources(language);
			BorderPane root = loader.load();
			Scene scene = new Scene(root, 550, 550);
			scene.getStylesheets().add(Main.class.getResource("blackstyle.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(e -> client.sendPacket(new DisconnectPacket()));

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * shows the login scene
	 * @return boolean
	 */
	private boolean showLogIn() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setResources(language);
			GridPane root = loader.load(getClass().getResource("LogIn.fxml").openStream());
			Stage loginStage = new Stage();
			loginStage.setTitle("Log In");
			loginStage.initModality(Modality.WINDOW_MODAL);
			loginStage.initOwner(primaryStage);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(Main.class.getResource("blackstyle.css").toExternalForm());
			loginStage.setScene(scene);

			LogInController controller = loader.getController();
			controller.setLoginStage(loginStage);

			loginStage.showAndWait();

			return controller.isOkClicked();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
