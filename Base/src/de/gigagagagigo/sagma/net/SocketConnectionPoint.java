package de.gigagagagigo.sagma.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class SocketConnectionPoint implements ConnectionPoint {

	private final ServerSocket server;

	SocketConnectionPoint(int port) throws IOException {
		server = new ServerSocket(port);
	}

	@Override
	public Connection accept() throws IOException {
		try {
			return new SocketConnection(server.accept());
		} catch (SocketException e) {
			throw new ConnectionPointException(e);
		}
	}

	@Override
	public void close() throws IOException {
		server.close();
	}

}
