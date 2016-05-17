package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class AuthReplyPacket implements Packet {

	public static final int STATUS_OK = 0;
	// login status
	public static final int STATUS_INVALID_CREDENTIALS = 1;
	public static final int STATUS_ALREADY_LOGGED_IN = 2;
	// registration status
	public static final int STATUS_USERNAME_TAKEN = 3;
	public static final int STATUS_INVALID_PASSWORD = 4;

	public int status;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		status = in.readInt();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeInt(status);
	}

}
