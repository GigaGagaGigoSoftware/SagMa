package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.net.Connection;
import de.gigagagagigo.sagma.net.NetworkFactories;
import de.gigagagagigo.sagma.packet.Packet;

public class SagMaClient {

	private final BlockingQueue<Packet> queue;
	private final HandlerRef ref;

	public SagMaClient(RunnableExecutor executor) {
		queue = new LinkedBlockingQueue<>();
		ref = new HandlerRef(executor);
	}

	public void start(String server) {
		new Thread(() -> {
			try {
				Connection connection = NetworkFactories.get().openConnection(server, SagMa.PORT);
				Thread writer = new Thread(new PacketWriter(connection, queue, ref));
				Thread reader = new Thread(new PacketReader(connection, writer, ref));
				writer.start();
				reader.start();
				try {
					writer.join();
					reader.join();
				} catch (InterruptedException ignored) {}
				connection.close();
			} catch (IOException e) {
				ref.submitException(e);
			}
		}).start();
	}

	public void sendPacket(Packet packet) {
		queue.add(packet);
	}

	public void setHandler(Handler handler) {
		ref.setHander(handler);
	}

}
