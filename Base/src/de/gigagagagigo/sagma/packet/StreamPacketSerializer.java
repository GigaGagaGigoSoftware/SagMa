package de.gigagagagigo.sagma.packet;

import java.io.IOException;
import java.io.OutputStream;

public class StreamPacketSerializer implements PacketSerializer {

	private final OutputStream out;

	public StreamPacketSerializer(OutputStream out) {
		this.out = out;
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
	public void writeInt(int i) {}

	@Override
	public void writeLong(long l) {}

	@Override
	public void writeFloat(float f) {}

	@Override
	public void writeDouble(double d) {}

	@Override
	public void writeChar(char c) {}

	@Override
	public void writeString(String s) {}

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
