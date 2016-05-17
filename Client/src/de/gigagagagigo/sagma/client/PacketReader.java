package de.gigagagagigo.sagma.client;

import java.io.IOException;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.Connection;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packet.PacketInputStream;
import de.gigagagagigo.sagma.packets.DisconnectPacket;
import de.gigagagagigo.sagma.packets.VersionCheckReplyPacket;

class PacketReader implements Runnable {

	private final PacketInputStream in;
	private final Thread writer;
	private final HandlerRef ref;

	public PacketReader(Connection connection, Thread writer, HandlerRef ref) throws IOException {
		this.in = new PacketInputStream(connection.getInputStream());
		this.writer = writer;
		this.ref = ref;
	}

	@Override
	public void run() {
		try {
			receiveVersionCheckReply();
			while (true) {
				Packet packet = in.read();
				if (packet instanceof DisconnectPacket)
					break;
				ref.submitPacket(packet);
			}
		} catch (IOException e) {
			ref.submitException(e);
		} finally {
			writer.interrupt();
		}
		// Do not close in, because it is part of a connection.
	}

	private void receiveVersionCheckReply() throws IOException {
		VersionCheckReplyPacket reply = in.read(VersionCheckReplyPacket.class);
		if (!reply.success)
			throw new IncompatibleProtocolVersionException(SagMa.VERSION, reply.serverVersion);
	}

}
