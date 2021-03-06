package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream implements AutoCloseable {

	private final PacketDataInputStream in;

	public PacketInputStream(InputStream in) {
		this.in = new PacketDataInputStream(in);
	}

	@SuppressWarnings("unchecked")
	public synchronized <P extends Packet> P read(Class<P> packetClass) throws IOException {
		Packet packet = read();
		if (packet != null && !packetClass.isInstance(packet)) {
			throw new IOException("Expected packet of type '" + packetClass.getName()
				+ "', but found '" + packet.getClass().getName() + "' instead.");
		}
		return (P) packet;
	}

	public synchronized Packet read() throws IOException {
		String className = in.readString();
		if (className == null) {
			return null;
		} else {
			return readNonNull(className);
		}
	}

	private Packet readNonNull(String className) throws IOException {
		try {
			return tryReadNonNull(className);
		} catch (ReflectiveOperationException e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Packet tryReadNonNull(String className) throws ReflectiveOperationException, IOException {
		Class<? extends Packet> packetClass = (Class<? extends Packet>) Class.forName(className);
		Packet packet = packetClass.newInstance();
		packet.read(in);
		return packet;
	}

	@Override
	public synchronized void close() throws IOException {
		in.close();
	}

}
