package de.gigagagagigo.sagma.client.ui.fxml.controller;

import java.io.IOException;
import java.util.*;

import de.gigagagagigo.sagma.client.Handler;
import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.client.ui.fxml.ChatPane;
import de.gigagagagigo.sagma.client.ui.fxml.Main;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.*;
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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for FXML-File chat.fxml
 */
public class ChatController implements Handler {

	private String username;
	private SagMaClient client;
	private static ResourceBundle language;
	private final Map<String, ChatPane> chats = new HashMap<String, ChatPane>();
	private final ObservableList<String> activeChats = FXCollections.observableArrayList();
	private final ObservableList<ActiveChatCell> activeChatsCells = FXCollections.observableArrayList();

	TreeItem<String> tiUsers;
	@FXML private ResourceBundle resources;
	@FXML private TreeView<String> userTree;
	@FXML private TreeItem<String> treeRoot;
	@FXML private ListView<ActiveChatCell> activeChatsList;
	@FXML private AnchorPane messagePane;
	@FXML private Button bSend;
	@FXML private TextArea sendTextArea;
	@FXML private Label activeChatsLabel;
	@FXML private Label userLabel;
	@FXML private Menu mSagMa, mActions, mHelp;
	@FXML private MenuItem miClose, miNameChange, miStatus, miNewGroup, miLogOut, miChangeLanguage, miOptions,
		miAbout, miTerms;

	private TreeItem<String> userTreeItem, groupTreeItem;

	public ChatController(SagMaClient client, String username) {
		this.client = client;
		this.username = username;
		language = ResourceBundle.getBundle("de.gigagagagigo.sagma.client.ui.fxml.language.chat", new Locale(
			"en", "EN"));
	}

	/**
	 * Sets the lists and events for activeChats, ser annd group tree and the messageTextField
	 */
	@FXML
	private void initialize() {

		resources = ResourceBundle.getBundle("de.gigagagagigo.sagma.client.ui.fxml.language.chat", new Locale(
			"en", "EN"));

		activeChatsList.setItems(activeChatsCells);
		sendTextArea.setWrapText(true);
		userTreeItem = new TreeItem<String>(resources.getString("users"));
		groupTreeItem = new TreeItem<String>(resources.getString("groups"));

		userTree.setRoot(treeRoot);
		userTree.setShowRoot(false);
		treeRoot.getChildren().addAll(userTreeItem, groupTreeItem);

		userTree.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2 && userTree.getSelectionModel().getSelectedItem().getValue() != null
				&& userTree.getSelectionModel().getSelectedItem().getParent() != treeRoot) {
				String selected = userTree.getSelectionModel().getSelectedItem().getValue();

				if (userTree.getSelectionModel().getSelectedItem().getParent() == groupTreeItem) {
					openChatPane(getChatPane(selected, true), selected);
				} else {
					openChatPane(getChatPane(selected), selected);
				}
			}
		});

		activeChatsList.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<ActiveChatCell>() {
				@Override
				public void changed(ObservableValue<? extends ActiveChatCell> observable,
					ActiveChatCell oldPartner, ActiveChatCell newPartner) {
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

		client.setHandler(this);
	}

	/**
	 * opens the ChatPane for the given chat in the messagePane
	 * 
	 * @param pane ChatPane
	 * @param partner String
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
				sendTextArea.getStyleClass().remove("validateError");
			}
		});
	}

	/**
	 * Overloading methode for single user calls getChatPane(String, boolean)
	 * 
	 * @param partner String
	 * @return ChatPane
	 */
	private ChatPane getChatPane(String partner) {
		return getChatPane(partner, false);
	}

	/**
	 * creates a new ChatPane if the searched one does not exist and/or returns the searched pane
	 * 
	 * @param partner String
	 * @param isGroup boolean
	 * @return ChatPane
	 */
	private ChatPane getChatPane(String partner, boolean isGroup) {
		ChatPane pane = chats.get(partner);
		if (pane == null) {

			if (isGroup) {
				MembershipPacket packet = new MembershipPacket();
				packet.groupName = partner;
				client.sendPacket(packet);
			}
			pane = new ChatPane(username, messagePane);
			pane.getStyleClass().add("chatPane");
			chats.put(partner, pane);
			activeChats.add(partner);

			Button removeButton = new Button();
			removeButton.setOnAction((e) -> {
				if (isGroup) {
					MembershipPacket packet = new MembershipPacket();
					packet.groupName = partner;
					packet.leave = true;
					client.sendPacket(packet);
				}
				closeChatPane(partner);
			});
			ActiveChatCell cell = new ActiveChatCell(partner, removeButton, isGroup);
			activeChatsCells.add(cell);

		}
		return pane;
	}

	/**
	 * closes the selected ChatPane and remove the chat from the lists
	 * 
	 * @param partner String
	 */
	public void closeChatPane(String partner) {
		if (this.activeChatsList.getSelectionModel().getSelectedItem().getPartner().equals(partner)) {
			messagePane.getChildren().clear();
		}
		chats.remove(partner);
		activeChats.remove(partner);
		if(getActiveChatCell(partner).isGroup){
			for(TreeItem<String> group : groupTreeItem.getChildren()){
				if(group.getValue().equals(partner)){
					group.getChildren().clear();
					break;
				}
			}
		}
		activeChatsCells.remove(getActiveChatCell(partner));
	}

	/**
	 * returns the ActiveChatCell for the given partner
	 * 
	 * @param partner String
	 * @return ActiveChatCell
	 */
	private ActiveChatCell getActiveChatCell(String partner) {
		for (ActiveChatCell cell : activeChatsCells) {
			if (cell.getPartner().equals(partner)) {
				return cell;
			}
		}
		return null;
	}

	/**
	 * validates the message to be send and sends a new messagePacket to the server appends the message to the
	 * own messagePane
	 */
	public void sendMessage() {
		if (activeChatsList.getSelectionModel().getSelectedItem() != null) {
			String partner = activeChatsList.getSelectionModel().getSelectedItem().getPartner();

			String text = sendTextArea.getText();
			sendTextArea.setText("");

			if (!text.trim().equals("")) {
				SendMessagePacket message = new SendMessagePacket();
				message.entityName = partner;
				message.isGroup = getActiveChatCell(partner).isGroup();
				message.content = text;
				sendPacket(message);

				getChatPane(partner).appendOwnMessage(text);
			}
		} else {
			sendTextArea.getStyleClass().add("validateError");
		}
	}

	/**
	 * PacketHandler
	 */

	/**
	 * handles all incoming packets and calls the specific method
	 * 
	 * @param packet Packet
	 */
	@Override
	public void handlePacket(Packet packet) {
		if (packet instanceof UserListUpdatePacket) {
			UserListUpdatePacket update = (UserListUpdatePacket) packet;
			handleUserListUpdate(update);
		} else if (packet instanceof GroupListUpdatePacket) {
			GroupListUpdatePacket update = (GroupListUpdatePacket) packet;
			handleGroupListUpdate(update);
		} else if (packet instanceof MessagePacket) {
			MessagePacket message = (MessagePacket) packet;
			newMessage(message);
		} else if (packet instanceof MemberListUpdatePacket){
			MemberListUpdatePacket update = (MemberListUpdatePacket) packet;
			handleMemberListUpdate(update);
		}
	}

	@Override
	public void handleException(Exception exception) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Fehler!");
		alert.setHeaderText(null);
		alert.setContentText("Die Verbindung zum Server wurde unterbrochen!");
		alert.showAndWait();
		Platform.exit();
	}

	/**
	 * handles incoming messages and appends it to the chat
	 * 
	 * @param message MessagePacket
	 */
	private void newMessage(MessagePacket message) {
		String target = "";

		if (message.groupName == null) {
			target = message.userName;
		} else {
			target = message.groupName;
		}
		getChatPane(target).handleChatMessage(message);
		if (activeChatsList.getSelectionModel().getSelectedItem() != null) {
			if (!activeChatsList.getSelectionModel().getSelectedItem().getPartner().equals(target)) {
				for (ActiveChatCell cell : activeChatsList.getItems()) {
					if (cell.getPartner().equals(target)) {
						cell.changeNewMessage(true);
					}
				}
			}
		}

	}

	/**
	 * sends a packet
	 * 
	 * @param packet Packet
	 */
	public void sendPacket(Packet packet) {
		client.sendPacket(packet);
	}

	/**
	 * handles UserListUpdatePackets to refresh the userlist if any changes appear
	 * 
	 * @param update UserListUpdatePacket
	 */
	private void handleUserListUpdate(UserListUpdatePacket update) {
		userTreeItem.setExpanded(true);
		if (update.removed != null) {
			for (String user : update.removed) {
				userTreeItem.getChildren().removeIf(item -> item.getValue().equals(user));
			}
		}
		if (update.added != null) {
			for (String user : update.added) {
				userTreeItem.getChildren().add(new TreeItem<String>(user));
			}
		}
	}

	/**
	 * handles GroupListUpdatePackets to refresh the grouplist if any changes appear
	 * 
	 * @param update GroupListUpdatePacket
	 */
	private void handleGroupListUpdate(GroupListUpdatePacket update) {
		groupTreeItem.setExpanded(true);
		if (update.removed != null) {
			for (String user : update.removed) {
				groupTreeItem.getChildren().removeIf(item -> item.getValue().equals(user));
			}
		}
		if (update.added != null) {
			for (String user : update.added) {
				groupTreeItem.getChildren().add(new TreeItem<String>(user));
			}
		}
	}
	
	private void handleMemberListUpdate(MemberListUpdatePacket update){
		if(update.removed != null){
			for(TreeItem<String> group : groupTreeItem.getChildren()){
				if(group.getValue().equals(update.groupName)){
					for(String user : update.removed){
						group.getChildren().removeIf(item -> item.getValue().equals(user));
					}
					break;
				}
			}
		}
		if(update.added != null){
			for(TreeItem<String> group : groupTreeItem.getChildren()){
				if(group.getValue().equals(update.groupName)){
					for(String user : update.added){
						group.getChildren().add(new TreeItem<String>(user));
					}
					break;
				}
			}
		}
	}

	/**
	 * opens a new dialog window for the user to create a new chat group
	 */
	public void newGroup() {
		Stage newGroupStage = new Stage();
		newGroupStage.initModality(Modality.WINDOW_MODAL);
		newGroupStage.initOwner(messagePane.getScene().getWindow());

		HBox box = new HBox();
		box.getStyleClass().add("newGroup");
		TextField tfName = new TextField();
		Button bGroupOK = new Button(language.getString("create"));
		bGroupOK.setOnAction(e -> {
			if (!tfName.getText().trim().isEmpty()) {
				createGroup(tfName.getText().trim());
				newGroupStage.close();
			} else {
				tfName.getStyleClass().add("validateError");
			}
		});
		box.getChildren().add(new Label(language.getString("groupName")));
		box.getChildren().add(tfName);
		box.getChildren().add(bGroupOK);

		Scene scene = new Scene(box);
		scene.getStylesheets().add(Main.class.getResource("blackstyle.css").toExternalForm());
		newGroupStage.setScene(scene);
		newGroupStage.show();
	}

	/**
	 * creates the new group
	 * 
	 * @param name String
	 */
	private void createGroup(String name) {
		openChatPane(getChatPane(name, true), name);
	}

	/*
	 * Menu
	 */

	/**
	 * closes the window
	 */
	public void close() {
		client.sendPacket(new DisconnectPacket());
		Platform.exit();
	}

	/**
	 * sends the locale to the editText method to change the language
	 * 
	 * @param e ActionEvent
	 */
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
		editText(locale);
	}

	/**
	 * change the text of all fields to the new locale
	 * 
	 * @param locale Locale
	 */
	private void editText(Locale locale) {
		language = ResourceBundle.getBundle("de.gigagagagigo.sagma.client.ui.fxml.language.chat", locale);
		activeChatsLabel.setText(language.getString("activeList"));
		userLabel.setText(language.getString("userList"));
		bSend.setText(language.getString("sendButton"));
		mSagMa.setText(language.getString("mSagMa"));
		mHelp.setText(language.getString("mHelp"));
		miClose.setText(language.getString("miClose"));
		miChangeLanguage.setText(language.getString("miLanguage"));
		miOptions.setText(language.getString("miOptions"));
		miAbout.setText(language.getString("miAbout"));
		miNewGroup.setText(language.getString("miNewGroup"));
		miTerms.setText(language.getString("miTerms"));
		userTreeItem.setValue(language.getString("users"));
		groupTreeItem.setValue(language.getString("groups"));

	}

	/**
	 * shows a new window with the about us text
	 */
	public void showAbout() {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(
			"/de/gigagagagigo/sagma/client/ui/fxml/About.fxml"));
		showNewWindow(loader);
	}

	/**
	 * shows a new window with the terms
	 */
	public void showTerms() {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(
			"/de/gigagagagigo/sagma/client/ui/fxml/About.fxml"));
		showNewWindow(loader);

	}

	/**
	 * shows a new window for about and terms
	 * 
	 * @param loader FXMLLoader
	 */
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
	 * ListItem for active Chats It contains a region for showing that unread messages exists, a label for the
	 * partner and a delete button
	 */
	public static class ActiveChatCell extends HBox {
		boolean isGroup;
		Label lPartner;
		Image chatIcon;
		Button button;
		String partner;
		FadeTransition ft;

		ActiveChatCell(String partner, Button button, boolean isGroup) {
			super();
			this.isGroup = isGroup;
			this.button = button;
			this.partner = partner;
			this.setMaxHeight(10);
			if (isGroup) {
				chatIcon = new Image(Main.class.getResourceAsStream("img/group.png"));
			} else {
				chatIcon = new Image(Main.class.getResourceAsStream("img/person.png"));
			}
			ImageView iconView = new ImageView(chatIcon);
			iconView.setFitHeight(5);
			lPartner = new Label(partner);
			lPartner.setMaxWidth(Double.MAX_VALUE);
			lPartner.getStyleClass().add("unreadMessage");
			HBox.setHgrow(lPartner, Priority.ALWAYS);
			// button.prefWidthProperty().bind(button.heightProperty());
			button.setText("X");
			button.getStyleClass().add("deleteButton");

			ft = new FadeTransition(Duration.millis(800), lPartner);
			ft.setFromValue(1.0);
			ft.setToValue(0.4);
			ft.setCycleCount(Animation.INDEFINITE);
			ft.setAutoReverse(true);

			this.setSpacing(3);
			this.getChildren().addAll(new ImageView(chatIcon), lPartner, button);
		}

		/**
		 * if an unread message exist the text signals this event
		 * 
		 * @param isNew boolean
		 */
		public void changeNewMessage(boolean isNew) {
			if (isNew) {
				lPartner.getStyleClass().add("unreadMessage");
				ft.play();
			} else {
				lPartner.getStyleClass().remove("unreadMessage");
				ft.stop();
			}
		}

		/**
		 * returns the partner
		 * 
		 * @return String
		 */
		public String getPartner() {
			return this.partner;
		}

		/**
		 * returns the deleteButton
		 * 
		 * @return Button
		 */
		public Button getButton() {
			return button;
		}

		/**
		 * retrns wether the chat is a group chat or not
		 * 
		 * @return boolean
		 */
		public boolean isGroup() {
			return isGroup;
		}
	}
}
