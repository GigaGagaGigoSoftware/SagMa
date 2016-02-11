package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.packet.Packet;

public class SagMaClient {

	private final BlockingQueue<Packet> queue = new LinkedBlockingQueue<>();
	private final AtomicReference<PacketHandler> handlerReference = new AtomicReference<>();

	public void start(String server) {
		new Thread(() -> {
			try {
				@SuppressWarnings("resource")
				// Do not close the socket here, because we are starting the server.
				// The socket will be closed if one of the io streams created here is closed.
				Socket socket = new Socket(server, SagMa.PORT);
				new Thread(new PacketWriter(queue, socket.getOutputStream())).start();
				new Thread(new PacketReader(socket.getInputStream(), handlerReference)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void sendPacket(Packet packet) {
		queue.add(packet);
	}

	public void setPacketHandler(PacketHandler handler) {
		handlerReference.set(handler);
	}

}
