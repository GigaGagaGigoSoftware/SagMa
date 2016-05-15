package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.Connection;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packet.PacketOutputStream;
import de.gigagagagigo.sagma.packets.DisconnectPacket;
import de.gigagagagigo.sagma.packets.VersionCheckRequestPacket;

class PacketWriter implements Runnable {

	private final PacketOutputStream out;
	private final BlockingQueue<Packet> queue;
	private final HandlerRef ref;

	public PacketWriter(Connection connection, BlockingQueue<Packet> queue, HandlerRef ref) throws IOException {
		this.out = new PacketOutputStream(connection.getOutputStream());
		this.queue = queue;
		this.ref = ref;
	}

	@Override
	public void run() {
		try {
			sendVersionCheckRequest();
			Packet packet;
			do {
				packet = queue.take();
				out.write(packet);
			} while (!(packet instanceof DisconnectPacket));
		} catch (IOException e) {
			ref.submitException(e);
		} catch (InterruptedException ignored) {}
		// Do not close out, because it is part of a connection.
	}

	private void sendVersionCheckRequest() throws IOException {
		VersionCheckRequestPacket request = new VersionCheckRequestPacket();
		request.clientVersion = SagMa.VERSION;
		out.write(request);
	}

}
