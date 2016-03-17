package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packet.PacketInputStream;
import de.gigagagagigo.sagma.packets.DisconnectPacket;
import de.gigagagagigo.sagma.packets.VersionCheckReplyPacket;

class PacketReader implements Runnable {

	private final PacketInputStream in;
	private final AtomicReference<PacketHandler> handlerReference;

	public PacketReader(InputStream in, AtomicReference<PacketHandler> handlerReference) {
		this.in = new PacketInputStream(in);
		this.handlerReference = Objects.requireNonNull(handlerReference, "handlerReference must not be null");
	}

	@Override
	public void run() {
		try {
			receiveVersionCheckReply();
			while (true) {
				Packet packet = in.read();
				if (packet instanceof DisconnectPacket)
					break;
				PacketHandler handler = handlerReference.get();
				if (handler != null)
					handler.handle(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IncompatibleProtocolVersionException e) {
			// expected
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void receiveVersionCheckReply() throws IOException, IncompatibleProtocolVersionException {
		VersionCheckReplyPacket reply = in.read(VersionCheckReplyPacket.class);
		if (!reply.success)
			throw new IncompatibleProtocolVersionException(SagMa.VERSION, reply.serverVersion);
	}

}
