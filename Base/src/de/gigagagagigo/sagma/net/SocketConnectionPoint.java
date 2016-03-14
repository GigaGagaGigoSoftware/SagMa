package de.gigagagagigo.sagma.net;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketConnectionPoint implements ConnectionPoint {

	private final ServerSocket server;

	public SocketConnectionPoint(int port) throws IOException {
		server = new ServerSocket(port);
	}

	@Override
	public Connection accept() throws IOException {
		return new SocketConnection(server.accept());
	}

	@Override
	public void close() throws IOException {
		server.close();
	}

}
