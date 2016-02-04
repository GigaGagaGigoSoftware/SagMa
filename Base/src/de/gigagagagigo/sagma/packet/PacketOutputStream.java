package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import de.gigagagagigo.sagma.packet.mapper.MappedOutputStream;

public class PacketOutputStream implements AutoCloseable {

	private final MappedOutputStream out;

	public PacketOutputStream(OutputStream out) {
		this.out = new MappedOutputStream(Objects.requireNonNull(out, "out must not be null."));
	}

	public void write(Packet packet) {
		try {
			out.write(PacketPart.class, packet);
		} catch (IOException e) {
			throw new PacketException(e);
		}
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
