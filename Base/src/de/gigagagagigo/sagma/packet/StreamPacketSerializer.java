package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class StreamPacketSerializer implements PacketSerializer {

	private final OutputStream out;

	public StreamPacketSerializer(OutputStream out) {
		this.out = Objects.requireNonNull(out, "out must not be null.");
	}

	@Override
	public void writeBoolean(boolean b) {
		handleException(o -> o.write(1));
	}

	@Override
	public void writeByte(byte b) {}

	@Override
	public void writeShort(short s) {}

	@Override
	public void writeInt(int i) {
		handleException(o -> {
			o.write(i >> 24);
			o.write(i >> 16);
			o.write(i >> 8);
			o.write(i);
		});
	}

	@Override
	public void writeLong(long l) {}

	@Override
	public void writeFloat(float f) {}

	@Override
	public void writeDouble(double d) {}

	@Override
	public void writeChar(char c) {}

	@Override
	public void writeString(String s) {
		handleException(o -> {
			Objects.requireNonNull(s, "s must not be null.");
			byte[] data = s.getBytes("UTF-8");
			writeInt(data.length);
			o.write(data);
		});
	}

	@FunctionalInterface
	private static interface WriteFunction {
		void write(OutputStream o) throws IOException;
	}

	public void handleException(WriteFunction function) {
		try {
			function.write(out);
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}

}
