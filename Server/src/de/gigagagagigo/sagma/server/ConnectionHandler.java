package de.gigagagagigo.sagma.server;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.Connection;
import de.gigagagagigo.sagma.packet.*;
import de.gigagagagigo.sagma.packets.*;

public class ConnectionHandler implements Runnable {

	private final SagMaServer server;
	private final Connection connection;
	private final PacketInputStream in;
	private final PacketOutputStream out;
	private final BlockingQueue<Packet> queue;
	private final Lock closeLock = new ReentrantLock();
	private String username;

	public ConnectionHandler(SagMaServer server, Connection connection) throws IOException {
		this.server = server;
		this.connection = connection;
		this.in = new PacketInputStream(connection.getInputStream());
		this.out = new PacketOutputStream(connection.getOutputStream());
		this.queue = new LinkedBlockingQueue<>();
	}

	public void start() {
		new Thread(this).start();
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
			server.unregister(username);
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
		boolean success = server.register(request.username, this);
		if (success)
			this.username = request.username;
		LogInReplyPacket reply = new LogInReplyPacket();
		reply.success = success;
		out.write(reply);
		new Thread(new Writer()).start();
		return success;
	}

	private void listen() throws IOException {
		while (true) {
			Packet packet = in.read();
			if (packet instanceof ChatMessagePacket) {
				handleChatMessage((ChatMessagePacket) packet);
			} else if (packet instanceof UserListRequestPacket) {
				handleUserListRequest((UserListRequestPacket) packet);
			} else if (packet instanceof DisconnectPacket) {
				server.unregister(username);
				username = null;
				queue.add(new DisconnectPacket());
				break;
			} else {
				break;
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
		queue.add(reply);
	}

	private void close() {
		closeLock.lock();
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeLock.unlock();
		}
	}

	public void sendMessage(String username, String message) {
		ChatMessagePacket packet = new ChatMessagePacket();
		packet.username = username;
		packet.message = message;
		queue.add(packet);
	}

	private class Writer implements Runnable {
		@Override
		public void run() {
			closeLock.lock();
			try {
				Packet packet;
				do {
					packet = queue.take();
					out.write(packet);
				} while (!(packet instanceof DisconnectPacket));
			} catch (InterruptedException ignored) {} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeLock.unlock();
			}
		}
	}

}
