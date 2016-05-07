package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class MessagePacket implements Packet {

	public String userName;
	public String groupName;
	public String content;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		userName = in.readString();
		groupName = in.readString();
		content = in.readString();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(userName);
		out.writeString(groupName);
		out.writeString(content);
	}

}
