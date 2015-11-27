package de.gigagagagigo.sagma;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

import de.gigagagagigo.sagma.packet.PacketSerializer;
import de.gigagagagigo.sagma.packet.StreamPacketSerializer;

public class StreamPacketSerializerTest {

	@Test
	public void testTrueIsWrittenAs1() {
		OutputStreamMock mock = new OutputStreamMock(new int[] { 1 });
		PacketSerializer s = new StreamPacketSerializer(mock);
		s.writeBoolean(true);
		assertTrue(mock.successful());
	}

}

class OutputStreamMock extends OutputStream {

	private final int[] expected;
	private int index = 0;
	private boolean failure = false;

	public OutputStreamMock(int[] expected) {
		this.expected = expected;
	}

	public boolean successful() {
		return !failure && index == expected.length;
	}

	@Override
	public void write(int b) throws IOException {
		if (index == expected.length) {
			failure = true;
		} else if ((expected[index++] & 0xff) != (b & 0xff)) {
			failure = true;
		}
	}

}