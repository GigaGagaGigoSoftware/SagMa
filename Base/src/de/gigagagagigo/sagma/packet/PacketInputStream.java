package de.gigagagagigo.sagma.packet;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Objects;

public class PacketInputStream implements AutoCloseable {

	private final DataInputStream in;

	public PacketInputStream(InputStream in) {
		this.in = new DataInputStream(Objects.requireNonNull(in, "in must not be null."));
	}

	public Packet read() {
		try {
			return tryRead();
		} catch (IOException e) {
			throw new PacketException(e);
		}
	}

	private Packet tryRead() throws IOException {
		String className = readString();
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
			throw new PacketException(e);
		}
	}

	private Packet tryReadNonNull(String className) throws IOException, ReflectiveOperationException {
		Class<?> packetClass = Class.forName(className);
		Packet packet = (Packet) packetClass.newInstance();
		int fieldCount = in.readInt();
		for (int i = 0; i < fieldCount; i++) {
			readField(packetClass, packet);
		}
		return packet;
	}

	private void readField(Class<?> packetClass, Packet packet) throws IOException, ReflectiveOperationException {
		String fieldName = readString();
		Field field = packetClass.getField(fieldName);
		Class<?> type = field.getType();
		if (type == boolean.class) {
			field.setBoolean(packet, in.readBoolean());
		} else if (type == byte.class) {
			field.setByte(packet, in.readByte());
		} else if (type == short.class) {
			field.setShort(packet, in.readShort());
		} else if (type == int.class) {
			field.setInt(packet, in.readInt());
		} else if (type == long.class) {
			field.setLong(packet, in.readLong());
		} else if (type == char.class) {
			field.setChar(packet, in.readChar());
		} else if (type == float.class) {
			field.setFloat(packet, in.readFloat());
		} else if (type == double.class) {
			field.setDouble(packet, in.readDouble());
		} else if (type == String.class) {
			field.set(packet, readString());
		} else {
			throw new PacketException("unknown field type: " + type);
		}
	}

	private String readString() throws IOException {
		int length = in.readInt();
		if (length < 0) {
			return null;
		} else {
			byte[] data = new byte[length];
			in.readFully(data);
			return new String(data, "UTF-8");
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
