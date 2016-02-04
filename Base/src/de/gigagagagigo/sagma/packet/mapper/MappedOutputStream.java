package de.gigagagagigo.sagma.packet.mapper;

import java.io.*;
import java.util.Objects;

public class MappedOutputStream implements AutoCloseable {

	private final DataOutputStream out;

	public MappedOutputStream(OutputStream out) {
		this.out = new DataOutputStream(Objects.requireNonNull(out, "out must not be null."));
	}

	public void writeBoolean(boolean data) throws IOException {
		out.writeBoolean(data);
	}

	public void writeByte(byte data) throws IOException {
		out.writeByte(data);
	}

	public void writeShort(short data) throws IOException {
		out.writeShort(data);
	}

	public void writeInt(int data) throws IOException {
		out.writeInt(data);
	}

	public void writeLong(long data) throws IOException {
		out.writeLong(data);
	}

	public void writeChar(char data) throws IOException {
		out.writeChar(data);
	}

	public void writeFloat(float data) throws IOException {
		out.writeFloat(data);
	}

	public void writeDouble(double data) throws IOException {
		out.writeDouble(data);
	}

	public void writeString(String data) throws IOException {
		write(String.class, data);
	}

	public <T> void write(Class<T> theClass, T data) throws IOException {
		MapperRegistry.getMapper(theClass).write(this, data);
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
