package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.ChatPane;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import de.gigagagagigo.sagma.packets.UserListRequestPacket;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ChatController {

	private String username;
	private SagMaClient client;
	private final Map<String, ChatPane> chats = new HashMap();
	private final ObservableList<String> activeChats = FXCollections.observableArrayList();


	TreeItem<String> tiUsers;

	@FXML
	private TreeView<String> userTree;
	@FXML
	private ListView<String> activeChatsList;
	@FXML
	private Pane messagePane;


	@FXML
	private void initialize() {
		activeChatsList.setItems(activeChats);

		userTree.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e){
				//TODO Edit for groups (leafquery)
				if(e.getClickCount() == 2 && userTree.getSelectionModel().getSelectedItem() != null && userTree.getSelectionModel().getSelectedItem().isLeaf()){
					String selected = userTree.getSelectionModel().getSelectedItem().getValue();

					openChatPane(getChatPane(selected), selected);
				}
			}
		});

		activeChatsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldPartner, String newPartner) {
				openChatPane(getChatPane(newPartner), newPartner);
			}

		});

		handlePacket(new UserListReplyPacket());


	}

	/**
	 * TODO Open the Chatpane
	 *
	 */
	private void openChatPane(ChatPane pane, String partner){
		messagePane = pane;
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				activeChatsList.scrollTo(partner);
				activeChatsList.getSelectionModel().select(partner);
			}
		});
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
			activeChats.add(partner);
		}
		return pane;
	}

	public void closeChatPane(String partner){
		chats.remove(partner);
		activeChats.remove(partner);
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
