package de.gigagagagigo.sagma.packet;

import java.io.IOException;

public interface Packet {
	default void read(PacketDataInputStream in) throws IOException {}
	default void write(PacketDataOutputStream out) throws IOException {}
}
