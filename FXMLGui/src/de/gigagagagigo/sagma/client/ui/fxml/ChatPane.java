package de.gigagagagigo.sagma.client.ui.fxml;

import java.awt.BorderLayout;

import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class ChatPane extends Pane{

	private TextArea chat = new TextArea();

	public ChatPane(ChatController chatController, String partner) {
		chat.setEditable(false);
		this.getChildren().add(chat);
	}

	public void handleChatMessage(ChatMessagePacket message) {
		appendMessage(message.username, message.message);

	}

	private void appendMessage(String author, String message){
		String text = chat.getText();
		text += author + ": " + message + "\n";
		chat.setText(text);
	}

}
