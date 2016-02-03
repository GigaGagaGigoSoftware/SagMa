package de.gigagagagigo.sagma.packet;

public class PacketException extends RuntimeException {

	public PacketException() {}

	public PacketException(String message) {
		super(message);
	}

	public PacketException(Throwable cause) {
		super(cause);
	}

	public PacketException(String message, Throwable cause) {
		super(message, cause);
	}

}
