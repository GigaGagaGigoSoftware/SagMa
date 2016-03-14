package de.gigagagagigo.sagma.net;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalNetworkFactory implements NetworkFactory {

	private final ConcurrentMap<Integer, LocalConnectionPoint> points = new ConcurrentHashMap<>();

	@Override
	public Connection openConnection(String host, int port) throws IOException {
		LocalConnectionPoint cp = points.get(port);
		if (cp == null)
			throw new IOException("No ConnectionPoint open.");
		LocalConnection client = null, server = null;
		try {
			client = new LocalConnection();
			server = new LocalConnection(client);
			cp.push(server);
			return client;
		} catch (IOException e) {
			closeConnection(client, e);
			closeConnection(server, e);
			throw e;
		}
	}

	private void closeConnection(Connection connection, IOException exception) {
		try {
			if (connection != null)
				connection.close();
		} catch (IOException e) {
			exception.addSuppressed(e);
		}
	}

	@Override
	public ConnectionPoint openConnectionPoint(int port) throws IOException {
		LocalConnectionPoint cp = new LocalConnectionPoint(() -> points.remove(port));
		LocalConnectionPoint previous = points.putIfAbsent(port, cp);
		if (previous != null)
			throw new IOException("Port not available.");
		return cp;
	}

}
