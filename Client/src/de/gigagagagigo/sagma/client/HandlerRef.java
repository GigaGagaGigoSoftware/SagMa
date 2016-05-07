package de.gigagagagigo.sagma.client;

import java.util.LinkedList;
import java.util.Queue;

import de.gigagagagigo.sagma.packet.Packet;

public class HandlerRef {

	private final Queue<Packet> outstanding = new LinkedList<>();
	private PacketHandler handler = null;

	public synchronized void submitPacket(Packet packet) {
		if (handler != null)
			handler.handle(packet);
		else
			outstanding.add(packet);
	}

	public synchronized void setHander(PacketHandler handler) {
		this.handler = handler;
		for (Packet p : outstanding)
			handler.handle(p);
		outstanding.clear();
	}

}
