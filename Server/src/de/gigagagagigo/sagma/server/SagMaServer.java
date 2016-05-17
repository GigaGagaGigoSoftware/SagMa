package de.gigagagagigo.sagma.server;

import static de.gigagagagigo.sagma.packets.AuthReplyPacket.*;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.*;
import de.gigagagagigo.sagma.packets.*;

public class SagMaServer implements Runnable {

	private static final String[] STRING_ARRAY = new String[0];

	private final Authenticator authenticator = new Authenticator();
	private final ConnectionPoint connectionPoint;
	private final Map<String, ConnectionHandler> activeHandlers;
	private final Map<String, List<ConnectionHandler>> groups;

	public SagMaServer() throws IOException {
		this.connectionPoint = NetworkFactories.get().openConnectionPoint(SagMa.PORT);
		this.activeHandlers = new HashMap<>();
		this.groups = new HashMap<>();
	}

	public void start() {
		new Thread(this).start();
	}

	public void stop() throws IOException {
		connectionPoint.close();
	}

	@Override
	public void run() {
		try {
			while (true) {
				Connection connection = connectionPoint.accept();
				ConnectionHandler handler = new ConnectionHandler(this, connection);
				handler.start();
			}
		} catch (ConnectionPointException e) {
			// expected
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized int addHandler(String username, String password, boolean register, ConnectionHandler handler) {
		if (register)
			return registerHandler(username, password, handler);
		else
			return loginHandler(username, password, handler);
	}

	private int registerHandler(String username, String password, ConnectionHandler handler) {
		if (activeHandlers.containsKey(username))
			return STATUS_USERNAME_TAKEN;
		int status = authenticator.register(username, password);
		updateUserListIfOk(status, username, handler);
		return status;
	}

	private int loginHandler(String username, String password, ConnectionHandler handler) {
		int status = authenticator.logIn(username, password);
		// Only send the information that the user is logged in if they provided valid credentials.
		if (status == STATUS_OK && activeHandlers.containsKey(username))
			return STATUS_ALREADY_LOGGED_IN;
		updateUserListIfOk(status, username, handler);
		return status;
	}

	private void updateUserListIfOk(int status, String username, ConnectionHandler handler) {
		if (status == STATUS_OK) {
			UserListUpdatePacket update = new UserListUpdatePacket();
			update.added = new String[] { username };
			activeHandlers.values().forEach(h -> h.sendPacket(update));
			activeHandlers.put(username, handler);
		}
	}

	public synchronized void removeHandler(String userName) {
		if (userName != null && activeHandlers.containsKey(userName)) {
			ConnectionHandler handler = activeHandlers.get(userName);
			for (Entry<String, List<ConnectionHandler>> group : groups.entrySet()) {
				if (group.getValue().contains(handler))
					removeFromGroup(handler, group.getKey());
			}
			activeHandlers.remove(userName);
			UserListUpdatePacket update = new UserListUpdatePacket();
			update.removed = new String[] { userName };
			activeHandlers.values().forEach(h -> h.sendPacket(update));
		}
	}

	public synchronized String[] getUsers() {
		return activeHandlers.keySet().toArray(STRING_ARRAY);
	}

	public synchronized String[] getGroups() {
		return groups.keySet().toArray(STRING_ARRAY);
	}

	public synchronized void sendMessage(String from, String to, String content) {
		if (activeHandlers.containsKey(to)) {
			MessagePacket packet = new MessagePacket();
			packet.userName = from;
			packet.groupName = null;
			packet.content = content;
			activeHandlers.get(to).sendPacket(packet);
		}
	}

	public synchronized void sendMessageToGroup(String from, String groupName, String content) {
		if (groups.containsKey(groupName)) {
			MessagePacket packet = new MessagePacket();
			packet.userName = from;
			packet.groupName = groupName;
			packet.content = content;
			for (ConnectionHandler handler : groups.get(groupName))
				if (!handler.getUserName().equals(from))
					handler.sendPacket(packet);
		}
	}

	public synchronized void removeFromGroup(ConnectionHandler connectionHandler, String groupName) {
		List<ConnectionHandler> group = groups.get(groupName);
		if (group == null)
			return;
		boolean removed = group.remove(connectionHandler);
		if (removed) {
			MemberListUpdatePacket update = new MemberListUpdatePacket();
			update.groupName = groupName;
			update.removed = new String[] { connectionHandler.getUserName() };
			group.forEach(h -> h.sendPacket(update));
		}
		if (group.isEmpty()) {
			groups.remove(groupName);
			GroupListUpdatePacket update = new GroupListUpdatePacket();
			update.removed = new String[] { groupName };
			activeHandlers.values().forEach(h -> h.sendPacket(update));
		}
	}

	public synchronized void addToGroup(ConnectionHandler connectionHandler, String groupName) {
		if (!groups.containsKey(groupName)) {
			groups.put(groupName, new ArrayList<>());
			GroupListUpdatePacket update = new GroupListUpdatePacket();
			update.added = new String[] { groupName };
			activeHandlers.values().forEach(h -> h.sendPacket(update));
		}
		List<ConnectionHandler> group = groups.get(groupName);
		MemberListUpdatePacket allUpdate = new MemberListUpdatePacket();
		allUpdate.groupName = groupName;
		allUpdate.added = new String[] { connectionHandler.getUserName() };
		group.forEach(h -> h.sendPacket(allUpdate));
		MemberListUpdatePacket newUpdate = new MemberListUpdatePacket();
		newUpdate.groupName = groupName;
		newUpdate.added = group.stream().map(ConnectionHandler::getUserName).toArray(i -> new String[i]);
		connectionHandler.sendPacket(newUpdate);
		group.add(connectionHandler);
	}

}
