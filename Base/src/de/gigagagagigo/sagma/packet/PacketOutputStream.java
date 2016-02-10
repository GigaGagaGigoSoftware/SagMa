package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream implements AutoCloseable {

	private final PacketDataOutputStream out;

	public PacketOutputStream(OutputStream out) {
		this.out = new PacketDataOutputStream(out);
	}

	public synchronized void write(Packet packet) throws IOException {
		if (packet == null) {
			out.writeString(null);
		} else {
			writeNonNull(packet);
		}
	}

	private void writeNonNull(Packet packet) throws IOException {
		Class<?> packetClass = packet.getClass();
		out.writeString(packetClass.getName());
		packet.write(out);
	}

	@Override
	public synchronized void close() throws IOException {
		out.close();
	}

}
