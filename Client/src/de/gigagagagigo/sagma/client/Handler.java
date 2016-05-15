package de.gigagagagigo.sagma.client;

import de.gigagagagigo.sagma.packet.Packet;

public interface Handler {
	void handlePacket(Packet packet);
	void handleException(Exception exception);
}
