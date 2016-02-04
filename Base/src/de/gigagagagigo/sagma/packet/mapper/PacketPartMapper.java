package de.gigagagagigo.sagma.packet.mapper;

import java.io.IOException;
import java.lang.reflect.Field;

import de.gigagagagigo.sagma.packet.*;

class PacketPartMapper implements Mapper<PacketPart> {

	@Override
	public void write(MappedOutputStream out, PacketPart data) throws IOException {
		new PacketPartWriter(out).write(data);
	}

	@Override
	public PacketPart read(MappedInputStream in) throws IOException {
		return new PacketPartReader(in).read();
	}

	private static class PacketPartWriter {

		private final MappedOutputStream out;

		public PacketPartWriter(MappedOutputStream out) {
			this.out = out;
		}

		public void write(PacketPart part) throws IOException {
			if (part == null) {
				out.writeString(null);
			} else {
				writeNonNull(part);
			}
		}

		private void writeNonNull(PacketPart part) throws IOException {
			Class<?> packetClass = part.getClass();
			out.writeString(packetClass.getName());
			writeFields(part, packetClass);
		}

		private void writeFields(PacketPart part, Class<?> partClass) throws IOException {
			Field[] fields = partClass.getFields();
			out.writeInt(fields.length);
			for (Field field : fields) {
				writeField(part, field);
			}
		}

		private void writeField(PacketPart part, Field field) throws IOException {
			try {
				tryWriteField(part, field);
			} catch (IllegalAccessException e) {
				throw new PacketException(e);
			}
		}

		@SuppressWarnings("unchecked")
		private void tryWriteField(PacketPart part, Field field) throws IOException, IllegalAccessException {
			out.writeString(field.getName());
			Class<?> type = field.getType();
			if (type == boolean.class) {
				out.writeBoolean(field.getBoolean(part));
			} else if (type == byte.class) {
				out.writeByte(field.getByte(part));
			} else if (type == short.class) {
				out.writeShort(field.getShort(part));
			} else if (type == int.class) {
				out.writeInt(field.getInt(part));
			} else if (type == long.class) {
				out.writeLong(field.getLong(part));
			} else if (type == char.class) {
				out.writeChar(field.getChar(part));
			} else if (type == float.class) {
				out.writeFloat(field.getFloat(part));
			} else if (type == double.class) {
				out.writeDouble(field.getDouble(part));
			} else {
				out.write((Class<Object>) type, field.get(part));
			}
		}

	}

	private class PacketPartReader {

		private final MappedInputStream in;

		public PacketPartReader(MappedInputStream in) {
			this.in = in;
		}

		public PacketPart read() throws IOException {
			String className = in.readString();
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

		private Packet tryReadNonNull(String className) throws IOException,
			ReflectiveOperationException {
			Class<?> packetClass = Class.forName(className);
			Packet packet = (Packet) packetClass.newInstance();
			int fieldCount = in.readInt();
			for (int i = 0; i < fieldCount; i++) {
				readField(packetClass, packet);
			}
			return packet;
		}

		private void readField(Class<?> packetClass, Packet packet) throws IOException, ReflectiveOperationException {
			String fieldName = in.readString();
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
			} else {
				field.set(packet, in.read(type));
			}
		}

	}

}
