package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.ChatPane;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import de.gigagagagigo.sagma.packets.UserListRequestPacket;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class ChatController {

	private String username;
	private SagMaClient client;
	private final Map<String, ChatPane> chats = new HashMap<String, ChatPane>();
	private final ObservableList<String> activeChats = FXCollections.observableArrayList();
	private final ObservableList<HBoxCell> activeChatsCells = FXCollections.observableArrayList();



	TreeItem<String> tiUsers;

	@FXML
	private TreeView<String> userTree;
	@FXML
	private ListView<HBoxCell> activeChatsList;
	@FXML
	private Pane messagePane;
	@FXML
	private AnchorPane messagePaneField;


	@FXML
	private void initialize() {
		activeChatsList.setItems(activeChatsCells);

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

		activeChatsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<HBoxCell>(){

			@Override
			public void changed(ObservableValue<? extends HBoxCell> observable, HBoxCell oldPartner, HBoxCell newPartner) {
				openChatPane(getChatPane(newPartner.getPartner()), newPartner.getPartner());
			}

		});

		handlePacket(new UserListReplyPacket());

	}

	/**
	 * TODO Show the right chatpane
	 */
	private void openChatPane(ChatPane pane, String partner){


		messagePaneField.getChildren().clear();
//		FXMLLoader loader = new FXMLLoader();
//			AnchorPane child = loader.load(getClass().getResource("messagePane.fxml").openStream());
		messagePaneField.getChildren().add(pane);




		messagePane = pane;
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				activeChatsList.scrollTo(getHBoxCell(partner));
				activeChatsList.getSelectionModel().select(getHBoxCell(partner));
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

			HBoxCell cell = new HBoxCell(partner);

			activeChatsCells.add(cell);
		}
		return pane;
	}

	public void closeChatPane(String partner){
		chats.remove(partner);
		activeChats.remove(partner);
		activeChatsCells.remove(getHBoxCell(partner));
	}

	private HBoxCell getHBoxCell(String partner){
		for(HBoxCell activeChat : activeChatsCells){
			if(activeChat.getPartner().equals(partner)){
				return activeChat;
			}
		}
		return null;
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
					handleUserListReply();
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

	private void handleUserListReply(){
		tiUsers = new TreeItem<String>("Users");
		tiUsers.setExpanded(true);
		tiUsers.getChildren().add(new TreeItem<String>("User1"));
		tiUsers.getChildren().add(new TreeItem<String>("User2"));
		tiUsers.getChildren().add(new TreeItem<String>("User3"));
		tiUsers.getChildren().add(new TreeItem<String>("User4"));
		tiUsers.getChildren().add(new TreeItem<String>("User5"));
		tiUsers.getChildren().add(new TreeItem<String>("User6"));
		tiUsers.getChildren().add(new TreeItem<String>("User7"));
		userTree.setRoot(tiUsers);
	}

	public static class HBoxCell extends HBox{
		Label label = new Label();
		Button button = new Button();
		String partner;

		HBoxCell(String partner){
			super();
			this.partner = partner;
			label.setText(partner);
			label.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(label, Priority.ALWAYS);
			button.setText("X");

			this.getChildren().addAll(label, button);

			button.setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent event) {
				}

			});

		}

		public String getPartner(){
			return partner;
		}

		public Button getButton(){
			return button;
		}
	}
}
