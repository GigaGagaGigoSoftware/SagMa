package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
	private final ObservableList<ActiveChatCell> activeChatsCells = FXCollections.observableArrayList();

	TreeItem<String> tiUsers;

	@FXML
	private TreeView<String> userTree;
	@FXML
	private TreeItem<String> userTreeItem;
	@FXML
	private ListView<ActiveChatCell> activeChatsList;
	@FXML
	private AnchorPane messagePane;
	@FXML
	private Button bSend;
	@FXML
	private TextArea sendTextArea;

	public ChatController(SagMaClient client, String username) {
		this.client = client;
		this.client.setPacketHandler(this::handlePacket);
		this.username = username;
	}

	@FXML
	private void initialize() {
		activeChatsList.setItems(activeChatsCells);
		userTreeItem.setValue("Users");

		userTree.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2 && userTree.getSelectionModel().getSelectedItem().getValue() != null
					&& userTree.getSelectionModel().getSelectedItem().isLeaf()) {
				String selected = userTree.getSelectionModel().getSelectedItem().getValue();

				openChatPane(getChatPane(selected), selected);
			}
		});

		activeChatsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveChatCell>() {
			@Override
			public void changed(ObservableValue<? extends ActiveChatCell> observable, ActiveChatCell oldPartner,
					ActiveChatCell newPartner) {
				if (!activeChats.isEmpty()) {
					openChatPane(getChatPane(newPartner.getPartner()), newPartner.getPartner());
				}
			}
		});

		bSend.setOnAction((e) -> sendMessage());
		sendTextArea.setOnKeyReleased((e) -> {
			if (e.getCode().equals(KeyCode.ENTER) && e.isShiftDown()) {
				sendTextArea.appendText("\n");
			} else if (e.getCode().equals(KeyCode.ENTER)) {
				sendMessage();
			}
		});

		sendPacket(new UserListRequestPacket());
	}

	/**
	 * TODO Open the Chatpane
	 *
	 */
	private void openChatPane(ChatPane pane, String partner) {
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);

		messagePane.getChildren().clear();
		messagePane.getChildren().add(pane);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				activeChatsList.scrollTo(getActiveChatCell(partner));
				activeChatsList.getSelectionModel().select(getActiveChatCell(partner));
			}
		});
	}

	private ChatPane getChatPane(String partner) {
//		messagePane.setStyle("-fx-background-color:red");
		ChatPane pane = chats.get(partner);
		if (pane == null) {
			pane = new ChatPane(partner, username);
			pane.getStyleClass().add("chatPane");
			chats.put(partner, pane);
			activeChats.add(partner);

			Button removeButton = new Button();
			removeButton.setOnAction((e) -> {
				closeChatPane(partner);
			});
			ActiveChatCell cell = new ActiveChatCell(partner, removeButton);
			activeChatsCells.add(cell);

		}
		return pane;
	}

	public void closeChatPane(String partner) {
		if(this.activeChatsList.getSelectionModel().getSelectedItem().getPartner().equals(partner)){
			messagePane.getChildren().clear();
		}
		chats.remove(partner);
		activeChats.remove(partner);
		activeChatsCells.remove(getActiveChatCell(partner));
	}

	private ActiveChatCell getActiveChatCell(String partner) {
		for (ActiveChatCell cell : activeChatsCells) {
			if (cell.getPartner().equals(partner)) {
				return cell;
			}
		}
		return null;
	}

	private void sendMessage() {
		if (activeChatsList.getSelectionModel().getSelectedItem() != null) {
			sendTextArea.setStyle(" -fx-border-color: blue;");
			String partner = activeChatsList.getSelectionModel().getSelectedItem().getPartner();

			String text = sendTextArea.getText();
			sendTextArea.setText("");

			if (!text.trim().equals("")) {
				ChatMessagePacket message = new ChatMessagePacket();
				message.username = partner;
				message.message = text;
				sendPacket(message);

				getChatPane(partner).appendOwnMessage(text);
			}
		} else {
			sendTextArea.setStyle(" -fx-border-color: red;");
		}
	}

	/**
	 * TODO Update
	 */

	private void updateUserTree() {
		sendPacket(new UserListRequestPacket());
	}

	/**
	 * PacketHandler
	 */

	/**
	 *
	 * @param packet
	 */
	private void handlePacket(Packet packet) {
		if (packet instanceof UserListReplyPacket) {
			UserListReplyPacket reply = (UserListReplyPacket) packet;
			Platform.runLater(() -> {
				handleUserListReply(reply);
			});
		} else if (packet instanceof ChatMessagePacket) {
			ChatMessagePacket message = (ChatMessagePacket) packet;
			Platform.runLater(() -> {
				getChatPane(message.username).handleChatMessage(message);
			});
		}
	}

	public void sendPacket(Packet packet) {
		client.sendPacket(packet);
	}

	private void handleUserListReply(UserListReplyPacket reply) {
		userTreeItem.getChildren().removeAll(userTreeItem.getChildren());
		userTreeItem.setExpanded(true);
		if (reply.users != null) {
			for (String user : reply.users) {
				userTreeItem.getChildren().add(new TreeItem<String>(user));
			}
		}
		userTreeItem.getChildren().add(new TreeItem<String>("Test1"));
		userTree.setRoot(userTreeItem);

	}

	/**
	 * ListItem with button for delete
	 *
	 */
	public static class ActiveChatCell extends HBox {
		Label label = new Label();
		Button button;
		String partner;

		ActiveChatCell(String partner, Button button) {
			super();
			this.button = button;
			this.partner = partner;
			label.setText(partner);
			label.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(label, Priority.ALWAYS);
			button.setText("X");

			this.getChildren().addAll(label, button);
		}

		public String getPartner() {
			return this.partner;
		}

		public Button getButton() {
			return button;
		}
	}
}
