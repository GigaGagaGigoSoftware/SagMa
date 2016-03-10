package controller;

import java.util.HashMap;
import java.util.Map;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.ChatPane;
import de.gigagagagigo.sagma.client.ui.fxml.TestList;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class ChatController {

	private String username;
	private SagMaClient client;
	private final Map<String, ChatPane> chats = new HashMap();

	//TODO
		private TestList list;

	@FXML
	private void initialize() {

		//TODO
		list = new TestList(this);
	}

	public void setUsername(String username){
		this.username = username;
	}

	public void setClient(SagMaClient client) {
		this.client = client;
	}

	public void setPacketHandler(){
		this.client.setPacketHandler(this::handlePacket);
	}

	private void handlePacket(Packet packet){
		if(packet instanceof UserListReplyPacket){
			UserListReplyPacket reply = (UserListReplyPacket) packet;
			Platform.runLater(()->{
				if(list != null){
					list.handleUserListReply(reply);
				}
			});
		} else if(packet instanceof ChatMessagePacket){
			ChatMessagePacket message = (ChatMessagePacket) packet;
			Platform.runLater(()->{

				//TODO
				getChatPane(message.username).handleChatMessage(message);
			});
		}
	}

	public void sendPacket(Packet packet){
		client.sendPacket(packet);
	}

	private ChatPane getChatPane(String partner){
		ChatPane pane = chats.get(partner);
		if(pane == null){
			pane = new ChatPane(this, partner);
			chats.put(partner, pane);
		}
		return pane;
	}

	public void closeChatPane(String partner){
		chats.remove(partner);
	}

	public void deleteList(){
		list = null;
	}
}
