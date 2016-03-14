package de.gigagagagigo.sagma.client.ui.fxml;

import de.gigagagagigo.sagma.client.ui.fxml.controller.ChatController;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ChatPane extends Pane{

	public ChatPane(){
		this.setStyle("-fx-background-color: red;");
	}

	public ChatPane(ChatController chatController, String partner) {
		// TODO Auto-generated constructor stub
	}

	public void handleChatMessage(ChatMessagePacket message) {
		// TODO Auto-generated method stub

	}

}
