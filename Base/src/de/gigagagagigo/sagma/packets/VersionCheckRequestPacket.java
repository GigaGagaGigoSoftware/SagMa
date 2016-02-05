package de.gigagagagigo.sagma.packets;

import java.io.IOException;

import de.gigagagagigo.sagma.packet.*;

public class VersionCheckRequestPacket implements Packet {

	public int clientVersion;

	@Override
	public void read(PacketDataInputStream in) throws IOException {
		clientVersion = in.readInt();
	}

	@Override
	public void write(PacketDataOutputStream out) throws IOException {
		out.writeInt(clientVersion);
	}

}
