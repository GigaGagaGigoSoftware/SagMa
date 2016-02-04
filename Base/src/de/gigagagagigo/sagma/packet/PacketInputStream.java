package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.gigagagagigo.sagma.packet.mapper.MappedInputStream;

public class PacketInputStream implements AutoCloseable {

	private final MappedInputStream in;

	public PacketInputStream(InputStream in) {
		this.in = new MappedInputStream(Objects.requireNonNull(in, "in must not be null."));
	}

	public Packet read() {
		try {
			return (Packet) in.read(PacketPart.class);
		} catch (IOException e) {
			throw new PacketException(e);
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
