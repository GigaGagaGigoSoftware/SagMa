package de.gigagagagigo.sagma.packet;

import java.io.*;

public class PacketDataOutputStream implements AutoCloseable {

	private final DataOutputStream out;

	public PacketDataOutputStream(OutputStream out) {
		this.out = new DataOutputStream(out);
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
		if (data == null) {
			writeInt(-1);
		} else {
			byte[] bytes = data.getBytes("UTF-8");
			writeInt(bytes.length);
			for (int i = 0; i < bytes.length; i++)
				writeByte(bytes[i]);
		}
	}

	public void writeStringArray(String[] data) throws IOException {
		int length = (data == null) ? -1 : data.length;
		writeInt(length);
		for (int i = 0; i < length; i++)
			writeString(data[i]);
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
