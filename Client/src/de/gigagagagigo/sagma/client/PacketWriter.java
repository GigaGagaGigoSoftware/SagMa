package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packet.PacketOutputStream;
import de.gigagagagigo.sagma.packets.VersionCheckRequestPacket;

class PacketWriter implements Runnable {

	private final BlockingQueue<Packet> queue;
	private final PacketOutputStream out;

	public PacketWriter(BlockingQueue<Packet> queue, OutputStream out) {
		this.queue = Objects.requireNonNull(queue, "queue must not be null.");
		this.out = new PacketOutputStream(out);
	}

	@Override
	public void run() {
		try {
			sendVersionCheckRequest();
			while (true)
				out.write(queue.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendVersionCheckRequest() throws IOException {
		VersionCheckRequestPacket request = new VersionCheckRequestPacket();
		request.clientVersion = SagMa.VERSION;
		out.write(request);
	}

}
