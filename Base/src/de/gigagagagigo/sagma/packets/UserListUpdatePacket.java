package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class UserListUpdatePacket implements Packet {

	public String[] added;
	public String[] removed;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		added = in.readStringArray();
		removed = in.readStringArray();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeStringArray(added);
		out.writeStringArray(removed);
	}

}
