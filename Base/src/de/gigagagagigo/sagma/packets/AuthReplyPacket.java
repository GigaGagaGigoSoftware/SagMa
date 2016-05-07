package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class AuthReplyPacket implements Packet {

	public boolean success;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		success = in.readBoolean();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeBoolean(success);
	}

}
