package de.gigagagagigo.sagma.net;

import java.io.*;

public interface Connection extends AutoCloseable {

	InputStream getInputStream() throws IOException;
	OutputStream getOutputStream() throws IOException;
	@Override
	void close() throws IOException;

}
