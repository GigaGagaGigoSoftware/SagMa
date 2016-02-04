package de.gigagagagigo.sagma.packet.mapper;

import java.io.*;
import java.util.Objects;

public class MappedInputStream implements AutoCloseable {

	private final DataInputStream in;

	public MappedInputStream(InputStream in) {
		this.in = new DataInputStream(Objects.requireNonNull(in, "in must not be null."));
	}

	public boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	public byte readByte() throws IOException {
		return in.readByte();
	}

	public short readShort() throws IOException {
		return in.readShort();
	}

	public int readInt() throws IOException {
		return in.readInt();
	}

	public long readLong() throws IOException {
		return in.readLong();
	}

	public char readChar() throws IOException {
		return in.readChar();
	}

	public float readFloat() throws IOException {
		return in.readFloat();
	}

	public double readDouble() throws IOException {
		return in.readDouble();
	}

	public String readString() throws IOException {
		return read(String.class);
	}

	public <T> T read(Class<T> theClass) throws IOException {
		return MapperRegistry.getMapper(theClass).read(this);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
