package de.gigagagagigo.sagma.client.ui.fxml;

import java.awt.BorderLayout;

import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChatPane extends Pane{

	private String username;
	private VBox messages = new VBox();

	public ChatPane(String partner, String username, AnchorPane messagePane) {
		this.username = username;
		this.getStyleClass().add("chatPane");
		this.getChildren().add(messages);
		messages.setSpacing(5);
		messages.maxWidthProperty().bind(messagePane.widthProperty().subtract(10));
	}

	public void handleChatMessage(ChatMessagePacket message) {
		appendMessage(message.username, message.message);
	}

	public void appendMessage(String author, String message){
		MessagePane messagePane = createMessagePane(author, message);
		messagePane.getStyleClass().add("partnerMessagePane");
	}

	public void appendOwnMessage(String message){
		MessagePane messagePane = createMessagePane(this.username, message);
		messagePane.getStyleClass().add("ownMessagePane");//
	}

	private MessagePane createMessagePane(String author, String message){
		MessagePane messagePane = new MessagePane(author, message);
		messages.getChildren().add(messagePane);
		return messagePane;
	}

	private static class MessagePane extends VBox{
		public MessagePane(String author, String message){
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
