package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class ChatMessagePacket implements Packet {

	public String username;
	public String message;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		username = in.readString();
		message = in.readString();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(username);
		out.writeString(message);
	}

}
