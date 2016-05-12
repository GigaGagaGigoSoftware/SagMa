package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.util.ResourceBundle;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.Main;
import de.gigagagagigo.sagma.packets.AuthReplyPacket;
import de.gigagagagigo.sagma.packets.AuthRequestPacket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LogInController {

	@FXML
	private TextField tfServer;
	@FXML
	private TextField tfUsername;
	@FXML
	private PasswordField pfPassword;
	@FXML
	private Button okButton;
	@FXML
	private Button registerButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Label serverLabel;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;

	private Stage loginStage;
	private boolean okClicked = false;
	private String username, server;
	private AuthRequestPacket request;

	@FXML
	private void initialize(ResourceBundle resources) {
		ResourceBundle bundle = resources;
		serverLabel.setText(bundle.getString("server"));
		usernameLabel.setText(bundle.getString("user"));
		okButton.setText(bundle.getString("login"));
		cancelButton.setText(bundle.getString("cancel"));
		okButton.setPrefWidth(cancelButton.getWidth());
		okButton.prefWidthProperty().bind(cancelButton.prefWidthProperty());
	}

	/**
	 * sets loginStage
	 * @param loginStage Stage
	 */
	public void setLoginStage(Stage loginStage) {
		this.loginStage = loginStage;
	}

	/**
	 * returns if ok is clicked
	 * @return boolean
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * button click handler for cancelButton
	 */
	@FXML
	private void handleCancel() {
		loginStage.close();
	}

	/**
	 * button click handler for okButton
	 */
	@FXML
	private void handleOK() {
		request(false);
	}
	
	/**
	 * button click handler for RegisterButton
	 */
	@FXML
	private void register() {
		request(true);
	}

	/**
	 * sends a request to the server if login is correct or registers a new user
	 * @param isRegister boolean
	 */
	private void request(boolean isRegister) {

		if (isInputValid()) {
			changeButtonAccess();

			SagMaClient client = new SagMaClient();
			client.setPacketHandler(p -> {
				if (p instanceof AuthReplyPacket) {
					AuthReplyPacket reply = (AuthReplyPacket) p;
					if (reply.success) {
						client.setPacketHandler(null);
						Platform.runLater(() -> {
							this.closeWindow();
							Main.showChat(client, request.username);
						});
					} else {
						Platform.runLater(() -> changeButtonAccess());
					}
				} else {
					Platform.runLater(() -> changeButtonAccess());
				}
			});
			client.start(tfServer.getText());
			request = new AuthRequestPacket();
			request.username = tfUsername.getText();
			request.password = pfPassword.getText();
			request.register = isRegister;
			client.sendPacket(request);
		}
	}

	/**
	 * checks if the input is valid
	 * @return boolean
	 */
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

	/**
	 * closes the stage
	 */
	public void closeWindow() {
		this.loginStage.close();
	}

	/**
	 * retuurns the username
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * returns the server
	 * @return String
	 */
	public String getServer() {
		return server;
	}

	/**
	 * changes the button accessibility
	 */
	public void changeButtonAccess() {
		okButton.setDisable(!okButton.isDisabled());
		registerButton.setDisable(!registerButton.isDisabled());
	}
}
