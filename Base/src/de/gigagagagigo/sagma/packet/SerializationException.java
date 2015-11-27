package de.gigagagagigo.sagma.packet;

public class SerializationException extends RuntimeException {

	public SerializationException() {
		super();
	}

	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

}
