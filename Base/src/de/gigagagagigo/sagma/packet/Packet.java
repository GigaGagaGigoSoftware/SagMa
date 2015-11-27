package de.gigagagagigo.sagma.packet;

public interface Packet {
	void serialize(PacketSerializer s);
	void deserialize(PacketDeserializer d);
}
