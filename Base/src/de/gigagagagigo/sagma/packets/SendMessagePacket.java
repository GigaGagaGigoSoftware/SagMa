package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class SendMessagePacket implements Packet {

	public String entityName;
	public boolean isGroup;
	public String content;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		entityName = in.readString();
		isGroup = in.readBoolean();
		content = in.readString();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(entityName);
		out.writeBoolean(isGroup);
		out.writeString(content);
	}

}
