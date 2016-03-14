package de.gigagagagigo.sagma.net;

import java.io.IOException;

public interface ConnectionPoint extends AutoCloseable {

	Connection accept() throws IOException;
	@Override
	void close() throws IOException;

}
