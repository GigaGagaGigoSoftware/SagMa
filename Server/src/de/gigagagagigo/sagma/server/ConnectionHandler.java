package de.gigagagagigo.sagma.server;

import java.io.IOException;
import java.net.Socket;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.packet.*;
import de.gigagagagigo.sagma.packets.*;

public class ConnectionHandler implements Runnable {

	private final SagMaServer server;
	private final Socket socket;
	private final PacketInputStream in;
	private final PacketOutputStream out;
	private String username;

	public ConnectionHandler(SagMaServer server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		this.in = new PacketInputStream(socket.getInputStream());
		this.out = new PacketOutputStream(socket.getOutputStream());
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
		reply.users = server.getUsers();
		out.write(reply);
	}

	private void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
