package de.gigagagagigo.sagma.client.ui.swing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import de.gigagagagigo.sagma.client.Handler;
import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.*;

public class WindowManager implements Handler {

	private final SagMaClient client;
	private final String username;
	private final Map<String, ChatFrame> chats = new HashMap<>();
	private final Map<String, GroupFrame> groups = new HashMap<>();
	private ListFrame list;

	public WindowManager(SagMaClient client, String username) {
		this.client = client;
		this.username = username;
		this.client.setHandler(this);
		this.list = new ListFrame(this);
	}

	@Override
	public void handlePacket(Packet packet) {
		if (packet instanceof UserListUpdatePacket) {
			UserListUpdatePacket update = (UserListUpdatePacket) packet;
			if (list != null)
				list.handleUserListUpdate(update);
		} else if (packet instanceof GroupListUpdatePacket) {
			GroupListUpdatePacket update = (GroupListUpdatePacket) packet;
			if (list != null)
				list.handleGroupListUpdate(update);
		} else if (packet instanceof MemberListUpdatePacket) {
			MemberListUpdatePacket update = (MemberListUpdatePacket) packet;
			if (groups.containsKey(update.groupName))
				groups.get(update.groupName).handleMemberListUpdate(update);
		} else if (packet instanceof MessagePacket) {
			MessagePacket message = (MessagePacket) packet;
			String groupName = message.groupName;
			if (groupName == null) {
				getChatFrame(message.userName).handleChatMessage(message);
			} else if (groups.containsKey(groupName)) {
				groups.get(groupName).handleChatMessage(message);
			}
		}
	}

	@Override
	public void handleException(Exception exception) {
		for (ChatFrame chat : chats.values())
			chat.dispose();
		for (GroupFrame group : groups.values())
			group.dispose();
		if (list != null)
			list.dispose();
		JOptionPane.showMessageDialog(
			null,
			"Die Verbindung zum Server wurde unterbrochen!",
			"Fehler!",
			JOptionPane.ERROR_MESSAGE);
	}

	public String getUsername() {
		return username;
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

	public void showGroupFrame(String groupName) {
		if (groups.containsKey(groupName)) {
			groups.get(groupName).setVisible(true);
		} else {
			MembershipPacket packet = new MembershipPacket();
			packet.groupName = groupName;
			packet.leave = false;
			client.sendPacket(packet);

			GroupFrame frame = new GroupFrame(this, groupName);
			groups.put(groupName, frame);
			frame.setVisible(true);
		}
	}

	public void closeGroupFrame(String groupName) {
		MembershipPacket packet = new MembershipPacket();
		packet.groupName = groupName;
		packet.leave = true;
		client.sendPacket(packet);
		groups.remove(groupName);
		checkClose();
	}

	public void closeListFrame() {
		list = null;
		checkClose();
	}

	private void checkClose() {
		if (list == null && chats.isEmpty() && groups.isEmpty()) {
			client.sendPacket(new DisconnectPacket());
		}
	}

}