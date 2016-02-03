package de.gigagagagigo.sagma.packet;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;

import org.junit.Test;

public class PacketIntegrationTest {

	@Test
	public void sendingNull() throws IOException {
		Packet out = writeAndReadPacket(null);
		assertNull(out);
	}

	@Test
	public void sendingEmptyPacket() throws IOException {
		EmptyTestPacket in = new EmptyTestPacket();

		Packet out = writeAndReadPacket(in);

		assertNotNull(out);
		assertNotSame(in, out);
		assertThat(out, instanceOf(EmptyTestPacket.class));
	}

	@Test
	public void sendingDataPacket() throws IOException {
		DataTestPacket in = new DataTestPacket();
		in.theBoolean = true;
		in.theByte = 1;
		in.theShort = 2;
		in.theInt = 3;
		in.theLong = 4;
		in.theChar = 'a';
		in.theFloat = 1.1f;
		in.theDouble = 2.2;
		in.noString = null;
		in.someString = "abc";

		Packet out = writeAndReadPacket(in);

		assertNotNull(out);
		assertNotSame(in, out);
		assertEquals(in, out);
	}

	private Packet writeAndReadPacket(Packet packet) throws IOException {
		try (ByteArrayOutputStream dataOut = new ByteArrayOutputStream()) {
			try (PacketOutputStream packetOut = new PacketOutputStream(dataOut)) {
				packetOut.write(packet);
			}
			try (ByteArrayInputStream dataIn = new ByteArrayInputStream(dataOut.toByteArray());
				PacketInputStream packetIn = new PacketInputStream(dataIn)) {
				return packetIn.read();
			}
		}
	}

}

class EmptyTestPacket implements Packet {}

class DataTestPacket implements Packet {
	public boolean theBoolean;
	public byte theByte;
	public short theShort;
	public int theInt;
	public long theLong;
	public char theChar;
	public float theFloat;
	public double theDouble;
	public String noString;
	public String someString;

	@Override
	public String toString() {
		return "[DataTestPacket]{ boolean: " + theBoolean
			+ ", byte: " + theByte
			+ ", short: " + theShort
			+ ", int: " + theInt
			+ ", long: " + theLong
			+ ", char: " + theChar
			+ ", float: " + theFloat
			+ ", double: " + theDouble
			+ ", no string: " + noString
			+ ", some string: " + someString
			+ " }";
	}

	// BEGIN GENERATED CODE

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((noString == null) ? 0 : noString.hashCode());
		result = prime * result + ((someString == null) ? 0 : someString.hashCode());
		result = prime * result + (theBoolean ? 1231 : 1237);
		result = prime * result + theByte;
		result = prime * result + theChar;
		long temp = Double.doubleToLongBits(theDouble);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Float.floatToIntBits(theFloat);
		result = prime * result + theInt;
		result = prime * result + (int) (theLong ^ (theLong >>> 32));
		result = prime * result + theShort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataTestPacket other = (DataTestPacket) obj;
		if (noString == null) {
			if (other.noString != null)
				return false;
		} else if (!noString.equals(other.noString))
			return false;
		if (someString == null) {
			if (other.someString != null)
				return false;
		} else if (!someString.equals(other.someString))
			return false;
		if (theBoolean != other.theBoolean)
			return false;
		if (theByte != other.theByte)
			return false;
		if (theChar != other.theChar)
			return false;
		if (Double.doubleToLongBits(theDouble) != Double.doubleToLongBits(other.theDouble))
			return false;
		if (Float.floatToIntBits(theFloat) != Float.floatToIntBits(other.theFloat))
			return false;
		if (theInt != other.theInt)
			return false;
		if (theLong != other.theLong)
			return false;
		if (theShort != other.theShort)
			return false;
		return true;
	}

	// END GENERATED CODE
}