package de.gigagagagigo.sagma.net;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LocalConnectionPoint implements ConnectionPoint {

	@FunctionalInterface
	static interface CloseAction {
		void execute();
	}

	// Special marker object to indicate that the point
	// has been closed and accept should throw an exception.
	private static final Connection CLOSED = new Connection() {
		@Override
		public OutputStream getOutputStream() {
			return null;
		}
		@Override
		public InputStream getInputStream() {
			return null;
		}
		@Override
		public void close() {}
	};

	private final CloseAction closeAction;
	private final BlockingQueue<Connection> connections = new ArrayBlockingQueue<>(50);
	private volatile boolean closed = false;

	LocalConnectionPoint(CloseAction action) {
		this.closeAction = action;
	}

	synchronized void push(Connection connection) throws IOException {
		if (closed)
			throw new IOException("Connection refused.");
		boolean success = connections.offer(connection);
		if (!success)
			throw new IOException("Connection refused.");
	}

	@Override
	public Connection accept() throws IOException {
		try {
			Connection c = connections.take();
			if (c != CLOSED)
				return c;
			connections.add(CLOSED);
		} catch (InterruptedException e) {}
		throw new ConnectionPointException("ConnectionPoint is closed.");
	}

	@Override
	public synchronized void close() throws IOException {
		if (!closed) {
			closed = true;
			closeAction.execute();
			connections.add(CLOSED);
		}
	}

}
