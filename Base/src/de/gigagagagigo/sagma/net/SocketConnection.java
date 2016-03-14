package de.gigagagagigo.sagma.net;

import java.io.*;
import java.net.Socket;

public class SocketConnection implements Connection {

	private final Socket socket;

	SocketConnection(Socket socket) {
		this.socket = socket;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

}
