package de.gigagagagigo.sagma.client;

import java.io.IOException;
import java.net.Socket;

import de.gigagagagigo.sagma.SagMa;
import de.gigagagagigo.sagma.packet.PacketInputStream;
import de.gigagagagigo.sagma.packet.PacketOutputStream;
import de.gigagagagigo.sagma.packets.*;

public class SagMaClient implements AutoCloseable {

	private final Socket socket;
	private final PacketInputStream in;
	private final PacketOutputStream out;

	public SagMaClient(String server) throws IOException {
		socket = new Socket(server, SagMa.PORT);
		in = new PacketInputStream(socket.getInputStream());
		out = new PacketOutputStream(socket.getOutputStream());
	}

	public boolean start(String username) {
		return checkVersion() && logIn(username);
	}

	private boolean checkVersion() {
		VersionCheckRequestPacket request = new VersionCheckRequestPacket();
		request.clientVersion = SagMa.VERSION;
		out.write(request);
		VersionCheckReplyPacket reply = in.read(VersionCheckReplyPacket.class);
		return reply.success;
	}

	private boolean logIn(String username) {
		LogInRequestPacket request = new LogInRequestPacket();
		request.username = username;
		out.write(request);
		LogInReplyPacket reply = in.read(LogInReplyPacket.class);
		return reply.success;
	}

	public String[] getUserList() {
		UserListRequestPacket request = new UserListRequestPacket();
		out.write(request);
		UserListReplyPacket reply = in.read(UserListReplyPacket.class);
		return reply.users;
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

}
