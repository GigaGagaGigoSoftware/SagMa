package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.ChatPane;
import de.gigagagagigo.sagma.client.ui.fxml.TestList;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import de.gigagagagigo.sagma.packets.UserListRequestPacket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ChatController {

	private String username;
	private SagMaClient client;
	private final Map<String, ChatPane> chats = new HashMap();

	TreeItem<String> tiUsers;

	@FXML
	private TreeView<String> userTree;


	@FXML
	private void initialize() {

		// TODO TreeViewListener hinzufügen

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


	/**
	 * TODO
	 * Update
	 */

	private void updateUserTree(){
		sendPacket(new UserListRequestPacket());
	}


	/**
	 * PacketHandler
	 */

	/**
	 *
	 * @param packet
	 */
	private void handlePacket(Packet packet){
		if(packet instanceof UserListReplyPacket){
			UserListReplyPacket reply = (UserListReplyPacket) packet;
			Platform.runLater(()->{
					handleUserListReply(reply);
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

	private void handleUserListReply(UserListReplyPacket reply){
		tiUsers = new TreeItem<String> ("Users");
		tiUsers.setExpanded(true);
		for(String user : reply.users)
			tiUsers.getChildren().add(new TreeItem<String>(user));
		userTree.setRoot(tiUsers);
	}
}
