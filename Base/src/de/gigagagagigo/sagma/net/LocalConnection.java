package de.gigagagagigo.sagma.net;

import java.io.*;

public class LocalConnection implements Connection {

	private final PipedInputStream in;
	private final PipedOutputStream out;

	LocalConnection() {
		this.in = new PipedInputStream();
		this.out = new PipedOutputStream();
	}

	LocalConnection(LocalConnection other) throws IOException {
		this.in = new PipedInputStream(other.out);
		this.out = new PipedOutputStream(other.in);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return in;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return out;
	}

	@Override
	public void close() throws IOException {
		in.close();
		out.close();
	}

}
