package de.gigagagagigo.sagma.client;

import de.gigagagagigo.sagma.packet.Packet;

@FunctionalInterface
public interface PacketHandler {
	void handle(Packet packet);
}
