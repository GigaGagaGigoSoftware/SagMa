package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class AuthRequestPacket implements Packet {

	public String username;
	public String password;
	public boolean register;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		username = in.readString();
		password = in.readString();
		register = in.readBoolean();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeString(username);
		out.writeString(password);
		out.writeBoolean(register);
	}

}
