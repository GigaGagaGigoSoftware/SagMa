package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream implements AutoCloseable {

	private final PacketDataOutputStream out;

	public PacketOutputStream(OutputStream out) {
		this.out = new PacketDataOutputStream(out);
	}

	public void write(Packet packet) {
		try {
			tryWrite(packet);
		} catch (IOException e) {
			throw new PacketException(e);
		}
	}

	private void tryWrite(Packet packet) throws IOException {
		if (packet == null) {
			out.writeString(null);
		} else {
			tryWriteNonNull(packet);
		}
	}

	private void tryWriteNonNull(Packet packet) throws IOException {
		Class<?> packetClass = packet.getClass();
		out.writeString(packetClass.getName());
		packet.write(out);
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
