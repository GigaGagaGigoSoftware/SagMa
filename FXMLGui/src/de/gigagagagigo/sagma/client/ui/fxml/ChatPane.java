package de.gigagagagigo.sagma.client.ui.fxml;

import java.awt.BorderLayout;

import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChatPane extends Pane{

	private TextArea chat = new TextArea();
	private String username;
	private VBox messages = new VBox();

	public ChatPane(String partner, String username) {
		chat.setEditable(false);
		this.getChildren().add(chat);
		this.username = username;
	}

	public void handleChatMessage(ChatMessagePacket message) {
		appendMessage(message.username, message.message);

	}

	public void appendMessage(String author, String message){
		MessagePane messagePane = createMessagePane(author, message);
		//TODO Style the Pane
		messagePane.setStyle("");

//		System.out.println(author + ": " + message);
//		String text = chat.getText();
//		text += author + ": " + message + "\n";
//		chat.setText(text);
	}
	public void appendOwnMessage(String message){
		MessagePane messagePane = createMessagePane(this.username, message);
		//TODO Style the Pane
		messagePane.setStyle("");
//
//		System.out.println(username + ": " + message);
//		String text = chat.getText();
//		text += username + ": " + message + "\n";
//		chat.setText(text);
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
