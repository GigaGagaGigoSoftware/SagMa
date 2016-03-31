package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class UserListReplyPacket implements Packet {

	public String[] users;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		users = in.readStringArray();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeStringArray(users);
	}

}
