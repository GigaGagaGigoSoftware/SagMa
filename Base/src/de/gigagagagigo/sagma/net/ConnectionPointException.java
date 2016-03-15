package de.gigagagagigo.sagma.net;

import java.io.IOException;

public class ConnectionPointException extends IOException {

	public ConnectionPointException() {}

	public ConnectionPointException(String message) {
		super(message);
	}

	public ConnectionPointException(Throwable cause) {
		super(cause);
	}

	public ConnectionPointException(String message, Throwable cause) {
		super(message, cause);
	}

}
