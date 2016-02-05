package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class UserListReplyPacket implements Packet {

	public String[] users;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		int length = in.readInt();
		users = (length < 0) ? null : new String[length];
		for (int i = 0; i < length; i++)
			users[i] = in.readString();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		int length = (users == null) ? 0 : users.length;
		out.writeInt(length);
		for (int i = 0; i < length; i++)
			out.writeString(users[i]);
	}

}
