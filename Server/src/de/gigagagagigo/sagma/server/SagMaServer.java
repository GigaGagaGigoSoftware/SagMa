package de.gigagagagigo.sagma.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.*;
import de.gigagagagigo.sagma.packets.UserListUpdatePacket;

public class SagMaServer implements Runnable {

	private static final String[] STRING_ARRAY = new String[0];

	private final ConnectionPoint connectionPoint;
	private final Map<String, ConnectionHandler> activeHandlers;

	public SagMaServer() throws IOException {
		this.connectionPoint = NetworkFactories.get().openConnectionPoint(SagMa.PORT);
		this.activeHandlers = new HashMap<>();
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

	public synchronized boolean register(String username, ConnectionHandler handler) {
		if (!activeHandlers.containsKey(username)) {
			UserListUpdatePacket update = new UserListUpdatePacket();
			update.added = new String[] { username };
			activeHandlers.values().forEach(h -> h.sendPacket(update));
			activeHandlers.put(username, handler);
			return true;
		} else {
			return false;
		}
	}

	public synchronized void unregister(String username) {
		if (username != null && activeHandlers.containsKey(username)) {
			activeHandlers.remove(username);
			UserListUpdatePacket update = new UserListUpdatePacket();
			update.removed = new String[] { username };
			activeHandlers.values().forEach(h -> h.sendPacket(update));
		}
	}

	public synchronized String[] getUsers() {
		return activeHandlers.keySet().toArray(STRING_ARRAY);
	}

	public synchronized void sendMessage(String from, String to, String message) {
		activeHandlers.computeIfPresent(to, (key, handler) -> {
			handler.sendMessage(from, message);
			return handler;
		});
	}

}
