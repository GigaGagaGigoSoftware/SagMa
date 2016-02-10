package de.gigagagagigo.sagma.server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.gigagagagigo.sagma.SagMa;

public class SagMaServer implements Runnable {

	private static final String[] STRING_ARRAY = new String[0];

	private final ServerSocket serverSocket;
	private final ConcurrentMap<String, ConnectionHandler> activeHandlers;

	public SagMaServer() throws IOException {
		this.serverSocket = new ServerSocket(SagMa.PORT);
		this.activeHandlers = new ConcurrentHashMap<>();
	}

	public void start() {
		new Thread(this).start();
	}

	public void stop() throws IOException {
		serverSocket.close();
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				ConnectionHandler handler = new ConnectionHandler(this, socket);
				handler.start();
			}
		} catch (SocketException e) {
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
