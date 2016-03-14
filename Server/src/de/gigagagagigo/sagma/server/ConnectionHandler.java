package de.gigagagagigo.sagma.server;

import java.io.IOException;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.Connection;
import de.gigagagagigo.sagma.packet.*;
import de.gigagagagigo.sagma.packets.*;

public class ConnectionHandler implements Runnable {

	private final SagMaServer server;
	private final Connection connection;
	private final PacketInputStream in;
	private final PacketOutputStream out;
	private String username;

	public ConnectionHandler(SagMaServer server, Connection connection) throws IOException {
		this.server = server;
		this.connection = connection;
		this.in = new PacketInputStream(connection.getInputStream());
		this.out = new PacketOutputStream(connection.getOutputStream());
	}

	public void start() {
		new Thread(this).start();
	}

	public void sendMessage(String username, String message) {
		try {
			ChatMessagePacket chatMessage = new ChatMessagePacket();
			chatMessage.username = username;
			chatMessage.message = message;
			out.write(chatMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			if (checkVersion() && logIn()) {
				listen();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	private boolean checkVersion() throws IOException {
		VersionCheckRequestPacket request = in.read(VersionCheckRequestPacket.class);
		boolean success = (request.clientVersion == SagMa.VERSION);
		VersionCheckReplyPacket reply = new VersionCheckReplyPacket();
		reply.success = success;
		reply.serverVersion = SagMa.VERSION;
		out.write(reply);
		return success;
	}

	private boolean logIn() throws IOException {
		LogInRequestPacket request = in.read(LogInRequestPacket.class);
		this.username = request.username;
		boolean success = server.register(this.username, this);
		LogInReplyPacket reply = new LogInReplyPacket();
		reply.success = success;
		out.write(reply);
		return success;
	}

	private void listen() throws IOException {
		while (true) {
			Packet packet = in.read();
			if (packet instanceof ChatMessagePacket) {
				handleChatMessage((ChatMessagePacket) packet);
			} else if (packet instanceof UserListRequestPacket) {
				handleUserListRequest((UserListRequestPacket) packet);
			} else {
				return;
			}
		}
	}

	private void handleChatMessage(ChatMessagePacket chatMessage) {
		server.sendMessage(this.username, chatMessage.username, chatMessage.message);
	}

	private void handleUserListRequest(UserListRequestPacket request) throws IOException {
		UserListReplyPacket reply = new UserListReplyPacket();
		String[] serverUsers = server.getUsers();
		reply.users = new String[serverUsers.length - 1];
		for (int serverIndex = 0, index = 0; serverIndex < serverUsers.length
			&& index < reply.users.length; serverIndex++)
			if (!serverUsers[serverIndex].equals(username))
				reply.users[index++] = serverUsers[serverIndex];
		out.write(reply);
	}

	private void close() {
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
