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
		in.noBoxedBoolean = null;
		in.someBoxedBoolean = false;
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
