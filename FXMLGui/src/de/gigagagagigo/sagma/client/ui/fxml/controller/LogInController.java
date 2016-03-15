package de.gigagagagigo.sagma.client.ui.fxml.controller;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.Main;
import de.gigagagagigo.sagma.packets.LogInReplyPacket;
import de.gigagagagigo.sagma.packets.LogInRequestPacket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LogInController {

	@FXML
	private TextField tfServer;
	@FXML
	private TextField tfUsername;
	@FXML
	private Button okButton;

	private Stage loginStage;
	private boolean okClicked = false;
	private String username, server;
	private LogInRequestPacket request;

	@FXML
	private void initialize() {
	}

	public void setLoginStage(Stage loginStage) {
		this.loginStage = loginStage;
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleOK() {

		if (isInputValid()) {
			changeButtonAccess();

			SagMaClient client = new SagMaClient();
			client.setPacketHandler(p -> {
				if (p instanceof LogInReplyPacket) {
					System.out.println("Test1");
					LogInReplyPacket reply = (LogInReplyPacket) p;
					if (reply.success) {
						System.out.println("Test2");
						Platform.runLater(()->{
							System.out.println("LogInReply1");
							this.closeWindow();
							Main.showChat(client, request.username);
						});
					} else{
						System.out.println("LogInError1");
						Platform.runLater(()-> changeButtonAccess());
					}
				} else{
					System.out.println("LogInError2");
					Platform.runLater(()-> changeButtonAccess());
				}
			});
			client.start(tfServer.getText());
			request = new LogInRequestPacket();
			request.username = tfUsername.getText();;
			client.sendPacket(request);
		}
	}

	@FXML
	private void handleCancel() {
		loginStage.close();
	}

	private boolean isInputValid() {
		String errorMessage = "";

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Dialogs.showErrorDialog(loginStage, errorMessage, "Invalid
			// Input!");
			return false;
		}
	}

	public void closeWindow() {
		this.loginStage.close();
	}

	public String getUsername() {
		return username;
	}

	public String getServer() {
		return server;
	}

	public void changeButtonAccess() {
		okButton.setDisable(!okButton.isDisable());
	}
}
