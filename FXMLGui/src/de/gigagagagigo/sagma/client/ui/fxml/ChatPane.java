package de.gigagagagigo.sagma.client.ui.fxml;

import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ChatPane extends ScrollPane {

	private String username;
	private VBox messages = new VBox();

	public ChatPane(String partner, String username, AnchorPane messagePane) {
		this.username = username;
		this.setContent(messages);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
//		messages.getStyleClass().add("chatPane");
		messages.setId("chatPane");
		messages.setFillWidth(false);
		messages.minWidthProperty().bind(this.widthProperty());
		messages.minHeightProperty().bind(this.heightProperty());

	}

	private void scrollDown() {
		this.setVvalue(1.0);
	}

	public void handleChatMessage(ChatMessagePacket message) {
		appendMessage(message.username, message.message);
	}

	public void appendMessage(String author, String message) {
		MessagePane messagePane = createMessagePane(author, message);
		messagePane.getStyleClass().add("partnerMessagePane");
		Platform.runLater(() -> {
			scrollDown();
		});
	}

	public void appendOwnMessage(String message) {
		MessagePane messagePane = createMessagePane(this.username, message);
		messagePane.getStyleClass().add("ownMessagePane");
		Platform.runLater(() -> {
			scrollDown();
		});
	}

	private MessagePane createMessagePane(String author, String message) {
		MessagePane messagePane = new MessagePane(author, message);
		messagePane.maxWidthProperty().bind(messages.widthProperty().subtract(20));
		messages.getChildren().add(messagePane);
		return messagePane;
	}

	private static class MessagePane extends VBox {
		public MessagePane(String author, String message) {
			this.getStyleClass().add("messagePane");
			Label lAuthor = new Label(author);
			lAuthor.getStyleClass().add("authorLabel");
			Label lMessage = new Label(message);
			lMessage.setWrapText(true);
			VBox vbox = new VBox(lAuthor);
			vbox.setPadding(new Insets(0, 10, 0, 10));

			this.getChildren().addAll(vbox, lMessage);
		}
	}
}
