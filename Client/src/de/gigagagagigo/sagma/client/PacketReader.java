package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.io.InputStream;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packet.PacketInputStream;
import de.gigagagagigo.sagma.packets.DisconnectPacket;
import de.gigagagagigo.sagma.packets.VersionCheckReplyPacket;

class PacketReader implements Runnable {

	private final PacketInputStream in;
	private final HandlerRef ref;

	public PacketReader(InputStream in, HandlerRef ref) {
		this.in = new PacketInputStream(in);
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
