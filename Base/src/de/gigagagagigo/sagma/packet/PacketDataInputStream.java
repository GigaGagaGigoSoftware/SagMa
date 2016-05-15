package de.gigagagagigo.sagma.packet;

import java.io.*;

public class PacketDataInputStream implements AutoCloseable {

	private final DataInputStream in;

	public PacketDataInputStream(InputStream in) {
		this.in = new DataInputStream(in);
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
		int length = readInt();
		if (length < 0) {
			return null;
		} else {
			byte[] bytes = new byte[length];
			for (int i = 0; i < length; i++)
				bytes[i] = readByte();
			return new String(bytes, "UTF-8");
		}
	}

	public String[] readStringArray() throws IOException {
		int length = readInt();
		String[] data = (length < 0) ? null : new String[length];
		for (int i = 0; i < length; i++)
			data[i] = readString();
		return data;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
