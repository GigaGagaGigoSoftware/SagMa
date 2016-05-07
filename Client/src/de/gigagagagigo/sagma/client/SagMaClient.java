package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.Connection;
import de.gigagagagigo.sagma.net.NetworkFactories;
import de.gigagagagigo.sagma.packet.Packet;

public class SagMaClient {

	private final BlockingQueue<Packet> queue = new LinkedBlockingQueue<>();
	private final HandlerRef ref = new HandlerRef();

	public void start(String server) {
		new Thread(() -> {
			try {
				// Do not close the connection here, because we are starting the server.
				// The connection will be closed if one of the io streams created here is closed.
				Connection connection = NetworkFactories.get().openConnection(server, SagMa.PORT);
				new Thread(new PacketWriter(queue, connection.getOutputStream())).start();
				new Thread(new PacketReader(connection.getInputStream(), ref)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void sendPacket(Packet packet) {
		queue.add(packet);
	}

	public void setPacketHandler(PacketHandler handler) {
		ref.setHander(handler);
	}

}
