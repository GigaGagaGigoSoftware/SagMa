package de.gigagagagigo.sagma.server;

import static de.gigagagagigo.sagma.packets.AuthReplyPacket.*;

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
			if (checkVersion() && authenticate()) {
				listen();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			server.removeHandler(username);
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

	private boolean authenticate() throws IOException {
		AuthRequestPacket request = in.read(AuthRequestPacket.class);
		int status = server.addHandler(request.username, request.password, request.register, this);
		if (status == STATUS_OK)
			this.username = request.username;
		AuthReplyPacket reply = new AuthReplyPacket();
		reply.status = status;
		out.write(reply);
		if (status == STATUS_OK) {
			UserListUpdatePacket update = new UserListUpdatePacket();
			update.added = getCustomUserList();
			out.write(update);
			GroupListUpdatePacket groupUpdate = new GroupListUpdatePacket();
			groupUpdate.added = server.getGroups();
			out.write(groupUpdate);
			new Thread(new Writer()).start();
		} else {
			out.write(new DisconnectPacket());
		}
		return status == STATUS_OK;
	}

	private void listen() throws IOException {
		while (true) {
			Packet packet = in.read();
			if (packet instanceof SendMessagePacket) {
				handleMessage((SendMessagePacket) packet);
			} else if (packet instanceof MembershipPacket) {
				handleMembership((MembershipPacket) packet);
			} else if (packet instanceof DisconnectPacket) {
				server.removeHandler(username);
				username = null;
				queue.add(new DisconnectPacket());
				break;
			} else {
				break;
			}
		}
	}

	private void handleMessage(SendMessagePacket message) {
		if (message.isGroup)
			server.sendMessageToGroup(this.username, message.entityName, message.content);
		else
			server.sendMessage(this.username, message.entityName, message.content);
	}

	private void handleMembership(MembershipPacket membership) {
		if (membership.leave)
			server.removeFromGroup(this, membership.groupName);
		else
			server.addToGroup(this, membership.groupName);
	}

	private String[] getCustomUserList() {
		String[] serverUsers = server.getUsers();
		String[] customUsers = new String[serverUsers.length - 1];
		for (int serverIndex = 0, index = 0; serverIndex < serverUsers.length
			&& index < customUsers.length; serverIndex++)
			if (!serverUsers[serverIndex].equals(username))
				customUsers[index++] = serverUsers[serverIndex];
		return customUsers;
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

	public String getUserName() {
		return username;
	}

	public void sendPacket(Packet packet) {
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
