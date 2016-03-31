package de.gigagagagigo.sagma.client.ui.fxml;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.client.ui.fxml.controller.LogInController;
import de.gigagagagigo.sagma.packets.LogInReplyPacket;
import de.gigagagagigo.sagma.packets.LogInRequestPacket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Main extends Application {

	private Stage primaryStage;
	private String username, server;
	private LogInRequestPacket request;
	private static ResourceBundle language;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		language = ResourceBundle.getBundle("de\\gigagagagigo\\sagma\\client\\ui\\fxml\\language\\chat", new Locale("en", "EN"));
		showLogIn();
	}

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

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
