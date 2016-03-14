package de.gigagagagigo.sagma.net;

import java.io.IOException;
import java.net.Socket;

public class SocketNetworkFactory implements NetworkFactory {

	@Override
	public Connection openConnection(String host, int port) throws IOException {
		return new SocketConnection(new Socket(host, port));
	}

	@Override
	public ConnectionPoint openConnectionPoint(int port) throws IOException {
		return new SocketConnectionPoint(port);
	}

}
