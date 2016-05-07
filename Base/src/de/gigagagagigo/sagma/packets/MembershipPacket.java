package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class MembershipPacket implements Packet {

	public String groupName;
	public boolean leave;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		groupName = in.readString();
		leave = in.readBoolean();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(groupName);
		out.writeBoolean(leave);
	}

}
