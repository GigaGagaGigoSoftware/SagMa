package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class VersionCheckReplyPacket implements Packet {

	public boolean success;
	public int serverVersion;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		success = in.readBoolean();
		serverVersion = in.readInt();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeBoolean(success);
		out.writeInt(serverVersion);
	}

}
