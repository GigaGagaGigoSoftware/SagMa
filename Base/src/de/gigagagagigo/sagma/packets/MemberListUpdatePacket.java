package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class MemberListUpdatePacket implements Packet {

	public String groupName;
	public String[] added;
	public String[] removed;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		groupName = in.readString();
		added = in.readStringArray();
		removed = in.readStringArray();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(groupName);
		out.writeStringArray(added);
		out.writeStringArray(removed);
	}

}
