package de.gigagagagigo.sagma.client.ui.swing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.*;

public class WindowManager {

	private final SagMaClient client;
	private final String username;
	private final Map<String, ChatFrame> chats = new HashMap<>();
	private ListFrame list;

	public WindowManager(SagMaClient client, String username) {
		this.client = client;
		this.username = username;
		this.client.setPacketHandler(this::handlePacket);
		list = new ListFrame(this);
	}

	public String getUsername() {
		return username;
	}

	private void handlePacket(Packet packet) {
		if (packet instanceof UserListReplyPacket) {
			UserListReplyPacket reply = (UserListReplyPacket) packet;
			SwingUtilities.invokeLater(() -> {
				if (list != null) {
					list.handleUserListReply(reply);
				}
			});
		} else if (packet instanceof ChatMessagePacket) {
			ChatMessagePacket message = (ChatMessagePacket) packet;
			SwingUtilities.invokeLater(() -> {
				getChatFrame(message.username).handleChatMessage(message);
			});
		}
	}

	public void sendPacket(Packet packet) {
		client.sendPacket(packet);
	}

	public void showChatFrame(String partner) {
		getChatFrame(partner).setVisible(true);
	}

	private ChatFrame getChatFrame(String partner) {
		ChatFrame frame = chats.get(partner);
		if (frame == null) {
			frame = new ChatFrame(this, partner);
			chats.put(partner, frame);
		}
		return frame;
	}

	public void closeChatFrame(String partner) {
		chats.remove(partner);
		checkClose();
	}

	public void closeListFrame() {
		list = null;
		checkClose();
	}

	private void checkClose() {
		if (list == null && chats.isEmpty()) {
			client.sendPacket(new DisconnectPacket());
		}
	}

}
