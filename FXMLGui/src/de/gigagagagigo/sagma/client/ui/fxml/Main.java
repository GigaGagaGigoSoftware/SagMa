package de.gigagagagigo.sagma.client.ui.fxml;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import controller.ChatController;
import controller.LogInController;
import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.packets.LogInReplyPacket;
import de.gigagagagigo.sagma.packets.LogInRequestPacket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	private Stage primaryStage;
	private String username, server;
	private LogInRequestPacket request;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		showLogIn();
	}

	private void showChat(SagMaClient client) {

		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setResources(ResourceBundle.getBundle("language\\chat", new Locale("en", "EN")));
			BorderPane root = loader.load(getClass().getResource("chat.fxml").openStream());
			this.primaryStage.setTitle("SagMa");
			Scene scene = new Scene(root, 550, 550);
			this.primaryStage.setMinHeight(550);
			this.primaryStage.setMinWidth(350);
			scene.getStylesheets().add(getClass().getResource("blackstyle.css").toExternalForm());
			this.primaryStage.setScene(scene);

			ChatController controller = loader.getController();
			controller.setUsername(username);


			controller.setClient(client);
			controller.setPacketHandler();

			this.primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean showLogIn() {

		try {
			FXMLLoader loader = new FXMLLoader();
			BorderPane root = loader.load(getClass().getResource("LogIn.fxml").openStream());
			Stage loginStage = new Stage();
			loginStage.setTitle("Log In");
			loginStage.initModality(Modality.WINDOW_MODAL);
			loginStage.initOwner(primaryStage);
			Scene scene = new Scene(root);
			loginStage.setScene(scene);

			LogInController controller = loader.getController();
			controller.setLoginStage(loginStage);

			loginStage.showAndWait();

			if(controller.isOkClicked()){

				controller.changeButtonAccess();
				this.server = controller.getServer();
				this.username = controller.getUsername();

				login(controller);
			}
			return controller.isOkClicked();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void login(LogInController controller){

		SagMaClient client = new SagMaClient();
		client.setPacketHandler(p -> {
			if (p instanceof LogInReplyPacket) {
				LogInReplyPacket reply = (LogInReplyPacket) p;
				if (reply.success) {
					Platform.runLater(()->{
						controller.closeWindow();
						showChat(client);
					});
				} else{
					Platform.runLater(()-> controller.changeButtonAccess());
				}
			} else{
				Platform.runLater(()-> controller.changeButtonAccess());
			}
		});
		client.start(server);
		request = new LogInRequestPacket();
		request.username = username;
		client.sendPacket(request);

	}
}
