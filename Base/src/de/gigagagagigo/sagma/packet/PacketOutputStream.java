package de.gigagagagigo.sagma.packet;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Objects;

public class PacketOutputStream implements AutoCloseable {

	private final DataOutputStream out;

	public PacketOutputStream(OutputStream out) {
		this.out = new DataOutputStream(Objects.requireNonNull(out, "out must not be null."));
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
			writeString(null);
		} else {
			writeNonNull(packet);
		}
	}

	private void writeNonNull(Packet packet) throws IOException {
		Class<?> packetClass = packet.getClass();
		writeString(packetClass.getName());
		writeFields(packet, packetClass);
	}

	private void writeFields(Packet packet, Class<?> packetClass) throws IOException {
		Field[] fields = packetClass.getFields();
		out.writeInt(fields.length);
		for (Field field : fields) {
			writeField(packet, field);
		}
	}

	private void writeField(Packet packet, Field field) throws IOException {
		try {
			tryWriteField(packet, field);
		} catch (IllegalAccessException e) {
			throw new PacketException(e);
		}
	}

	private void tryWriteField(Packet packet, Field field) throws IOException, IllegalAccessException {
		writeString(field.getName());
		Class<?> type = field.getType();
		if (type == boolean.class) {
			out.writeBoolean(field.getBoolean(packet));
		} else if (type == byte.class) {
			out.writeByte(field.getByte(packet));
		} else if (type == short.class) {
			out.writeShort(field.getShort(packet));
		} else if (type == int.class) {
			out.writeInt(field.getInt(packet));
		} else if (type == long.class) {
			out.writeLong(field.getLong(packet));
		} else if (type == char.class) {
			out.writeChar(field.getChar(packet));
		} else if (type == float.class) {
			out.writeFloat(field.getFloat(packet));
		} else if (type == double.class) {
			out.writeDouble(field.getDouble(packet));
		} else if (type == String.class) {
			writeString((String) field.get(packet));
		} else {
			throw new PacketException("unknown field type: " + type);
		}
	}

	private void writeString(String string) throws IOException {
		if (string == null) {
			out.writeInt(-1);
		} else {
			byte[] data = string.getBytes("UTF-8");
			out.writeInt(data.length);
			out.write(data, 0, data.length);
		}
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
