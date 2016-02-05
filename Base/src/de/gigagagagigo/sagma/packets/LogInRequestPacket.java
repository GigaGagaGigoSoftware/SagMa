package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class LogInRequestPacket implements Packet {

	public String username;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		username = in.readString();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(username);
	}

}
