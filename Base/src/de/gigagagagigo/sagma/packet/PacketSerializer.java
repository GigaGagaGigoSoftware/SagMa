package de.gigagagagigo.sagma.packet;

public interface PacketSerializer {
	void writeBoolean(boolean b);
	void writeByte(byte b);
	void writeShort(short s);
	void writeInt(int i);
	void writeLong(long l);
	void writeFloat(float f);
	void writeDouble(double d);
	void writeChar(char c);
	void writeString(String s);
}
