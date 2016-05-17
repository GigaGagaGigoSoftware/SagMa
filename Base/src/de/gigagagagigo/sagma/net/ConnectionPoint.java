package de.gigagagagigo.sagma.net;

import java.io.IOException;

public interface ConnectionPoint extends AutoCloseable {

	/**
	 * Waits for a new connection.
	 * 
	 * @return a new connection
	 * @throws ConnectionPointException if the point was closed
	 * @throws IOException if an IO error occurs
	 */
	Connection accept() throws IOException;

	@Override
	void close() throws IOException;

}
