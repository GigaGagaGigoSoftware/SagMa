package de.gigagagagigo.sagma.client.ui.fxml;

import java.awt.BorderLayout;

import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChatPane extends Pane{

	private String username;
	private VBox messages = new VBox();

	public ChatPane(String partner, String username) {
		this.username = username;
		this.getStyleClass().add("chatPane");
		this.getChildren().add(messages);
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
		private String author, message;
		public MessagePane(String author, String message){
			this.author = author;
			this.message = message;
			this.getStyleClass().add("messagePane");

			this.getChildren().addAll(new Label(author), new Label(message));
		}
	}
}
