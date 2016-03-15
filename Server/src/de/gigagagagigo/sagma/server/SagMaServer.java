package de.gigagagagigo.sagma.server;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.*;

public class SagMaServer implements Runnable {

	private static final String[] STRING_ARRAY = new String[0];

	private final ConnectionPoint connectionPoint;
	private final ConcurrentMap<String, ConnectionHandler> activeHandlers;

	public SagMaServer() throws IOException {
		this.connectionPoint = NetworkFactories.get().openConnectionPoint(SagMa.PORT);
		this.activeHandlers = new ConcurrentHashMap<>();
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

	public boolean register(String username, ConnectionHandler handler) {
		return activeHandlers.putIfAbsent(username, handler) == null;
	}

	public String[] getUsers() {
		return activeHandlers.keySet().toArray(STRING_ARRAY);
	}

	public void sendMessage(String from, String to, String message) {
		ConnectionHandler handler = activeHandlers.get(to);
		if (handler != null)
			handler.sendMessage(from, message);
	}

}
