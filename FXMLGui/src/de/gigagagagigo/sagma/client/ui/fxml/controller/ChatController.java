package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.ChatPane;
import de.gigagagagigo.sagma.client.ui.fxml.Main;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.ChatMessagePacket;
import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import de.gigagagagigo.sagma.packets.UserListRequestPacket;
import de.gigagagagigo.sagma.packets.UserListUpdatePacket;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChatController {

	private String username;
	private SagMaClient client;
	private static ResourceBundle language;
	private final Map<String, ChatPane> chats = new HashMap<String, ChatPane>();
	private final ObservableList<String> activeChats = FXCollections.observableArrayList();
	private final ObservableList<ActiveChatCell> activeChatsCells = FXCollections.observableArrayList();

	TreeItem<String> tiUsers;
	@FXML
	private ResourceBundle resources;
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
	@FXML
	private Label activeChatsLabel;
	@FXML
	private Label userLabel;
	@FXML
	private Menu mSagMa, mActions, mHelp;
	@FXML
	private MenuItem miClose, miNameChange, miStatus, miLogOut, miChangeLanguage, miOptions, miAbout, miTerms;

	public ChatController(SagMaClient client, String username) {
		this.client = client;
		this.client.setPacketHandler(this::handlePacket);
		this.username = username;
		language = ResourceBundle.getBundle("de\\gigagagagigo\\sagma\\client\\ui\\fxml\\language\\chat",
				new Locale("en", "EN"));
	}

	@FXML
	private void initialize() {

		resources = ResourceBundle.getBundle("de\\gigagagagigo\\sagma\\client\\ui\\fxml\\language\\chat",
				new Locale("en", "EN"));

		activeChatsList.setItems(activeChatsCells);
		sendTextArea.setWrapText(true);

		userTree.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2 && userTree.getSelectionModel().getSelectedItem().getValue() != null
					&& userTree.getSelectionModel().getSelectedItem().isLeaf()
					&& !userTree.getSelectionModel().getSelectedItem().getValue().equals("Users")) {
				String selected = userTree.getSelectionModel().getSelectedItem().getValue();

				openChatPane(getChatPane(selected), selected);
			}
		});

		activeChatsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveChatCell>() {
			@Override
			public void changed(ObservableValue<? extends ActiveChatCell> observable, ActiveChatCell oldPartner,
					ActiveChatCell newPartner) {
				if (newPartner != null) {
					if (!activeChats.isEmpty()) {
						openChatPane(getChatPane(newPartner.getPartner()), newPartner.getPartner());
					}
					newPartner.changeNewMessage(false);
				}
			}
		});

		sendTextArea.setOnKeyReleased((e) -> {
			if (e.getCode().equals(KeyCode.ENTER) && e.isShiftDown()) {
				sendTextArea.appendText("\n");
			} else if (e.getCode().equals(KeyCode.ENTER)) {
				sendMessage();
			}
		});

		sendPacket(new UserListRequestPacket());
	}

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
				sendTextArea.getStyleClass().remove("sendNoPartner");
			}
		});
	}

	private ChatPane getChatPane(String partner) {
		ChatPane pane = chats.get(partner);
		if (pane == null) {
			pane = new ChatPane(partner, username, messagePane);
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
		if (this.activeChatsList.getSelectionModel().getSelectedItem().getPartner().equals(partner)) {
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

	public void sendMessage() {
		if (activeChatsList.getSelectionModel().getSelectedItem() != null) {
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
			sendTextArea.getStyleClass().add("sendNoPartner");
		}
	}

	/**
	 * PacketHandler
	 */

	/**
	 *
	 * @param packet
	 */
	private void handlePacket(Packet packet) {
		if (packet instanceof UserListUpdatePacket) {
			UserListUpdatePacket update = (UserListUpdatePacket) packet;
			System.out.println("handlepacket");
			Platform.runLater(() -> {
				System.out.println("runlaterhandlepacket");
				handleUserListUpdate(update);
			});
		} else if (packet instanceof UserListReplyPacket) {
			UserListReplyPacket reply = (UserListReplyPacket) packet;
			Platform.runLater(() -> {
				handleUserListReply(reply);
			});
		} else if (packet instanceof ChatMessagePacket) {
			ChatMessagePacket message = (ChatMessagePacket) packet;
			Platform.runLater(() -> {
				newMessage(message);
			});
		}
	}

	private void newMessage(ChatMessagePacket message) {
		getChatPane(message.username).handleChatMessage(message);

		if (activeChatsList.getSelectionModel().getSelectedItem() != null) {
			if (!activeChatsList.getSelectionModel().getSelectedItem().getPartner().equals(message.username)) {
				for (ActiveChatCell cell : activeChatsList.getItems()) {
					if (cell.getPartner().equals(message.username)) {
						cell.changeNewMessage(true);
					}
				}
			}
		}
	}

	public void sendPacket(Packet packet) {
		client.sendPacket(packet);
	}

	private void handleUserListUpdate(UserListUpdatePacket update) {
		System.out.println("update");
		if (update.removed != null) {
			System.out.println("removed");
			for (String user : update.removed) {
				for (TreeItem<String> item : userTreeItem.getChildren()) {
					if (item.getValue().equals(user)) {
						userTreeItem.getChildren().remove(item);
						break;
					}
				}
			}
		}
		if (update.added != null) {
			System.out.println("added");
			for (String user : update.added) {
				userTreeItem.getChildren().add(new TreeItem<String>(user));
			}
		}
		// userTreeItem.setExpanded(true);
		// userTreeItem.getChildren().add(new TreeItem<String>("Test1"));
		// userTree.setRoot(userTreeItem);

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

	/*
	 * Menu
	 */

	public void close() {
		Platform.exit();
	}

	public void changeLanguage(ActionEvent e) {
		Locale locale = new Locale("en", "EN");
		switch (((MenuItem) e.getSource()).getText()) {

		case "DE":
			locale = new Locale("de", "DE");
			break;
		case "EN":
			locale = new Locale("en", "EN");
			break;
		case "RU":
			locale = new Locale("ru", "RU");
			break;
		}
		language = ResourceBundle.getBundle("de\\gigagagagigo\\sagma\\client\\ui\\fxml\\language\\chat", locale);
		activeChatsLabel.setText(language.getString("activeList"));
		userLabel.setText(language.getString("userList"));
		bSend.setText(language.getString("sendButton"));
		mSagMa.setText(language.getString("mSagMa"));
		mHelp.setText(language.getString("mHelp"));
		miClose.setText(language.getString("miClose"));
		miChangeLanguage.setText(language.getString("miLanguage"));
		miOptions.setText(language.getString("miOptions"));
		miAbout.setText(language.getString("miAbout"));
		miTerms.setText(language.getString("miTerms"));
		userTreeItem.setValue(language.getString("users"));

	}

	public void showAbout() {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/gigagagagigo/sagma/client/ui/fxml/About.fxml"));
		showNewWindow(loader);
	}

	public void showTerms() {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/gigagagagigo/sagma/client/ui/fxml/About.fxml"));
		showNewWindow(loader);

	}

	private void showNewWindow(FXMLLoader loader) {
		try {
			loader.setController(new InfoController());
			loader.setResources(language);
			AnchorPane root = loader.load();
			Stage aboutStage = new Stage();
			aboutStage.initModality(Modality.WINDOW_MODAL);
			aboutStage.initOwner(messagePane.getScene().getWindow());
			InfoController controller = loader.getController();
			controller.setStage(aboutStage);
			Scene scene = new Scene(root, 400, 300);
			aboutStage.setScene(scene);
			aboutStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ListItem for active Chats It contains a region for showing that unread
	 * messages exists, a label for the partner and a delete button
	 *
	 */
	public static class ActiveChatCell extends HBox {
		Label lPartner;
		Button button;
		String partner;
		FadeTransition ft;

		ActiveChatCell(String partner, Button button) {
			super();
			this.button = button;
			this.partner = partner;
			this.setMaxHeight(10);
			lPartner = new Label(partner);
			lPartner.setMaxWidth(Double.MAX_VALUE);
			lPartner.getStyleClass().add("unreadMessage");
			HBox.setHgrow(lPartner, Priority.ALWAYS);
			button.setText("X");
			button.getStyleClass().add("deleteButton");
			button.prefWidthProperty().bind(button.heightProperty());

			ft = new FadeTransition(Duration.millis(800), lPartner);
			ft.setFromValue(1.0);
			ft.setToValue(0.4);
			ft.setCycleCount(Animation.INDEFINITE);
			ft.setAutoReverse(true);

			this.setSpacing(3);
			this.getChildren().addAll(lPartner, button);
		}

		public void changeNewMessage(boolean isNew) {
			if (isNew) {
				lPartner.getStyleClass().add("unreadMessage");
				ft.play();
			} else {
				lPartner.getStyleClass().remove("unreadMessage");
				ft.stop();
			}
		}

		public String getPartner() {
			return this.partner;
		}

		public Button getButton() {
			return button;
		}
	}
}
