package de.gigagagagigo.sagma.client;

import java.io.IOException;

public class IncompatibleProtocolVersionException extends IOException {

	private final int clientVersion, serverVersion;

	public IncompatibleProtocolVersionException(int clientVersion, int serverVersion) {
		this.clientVersion = clientVersion;
		this.serverVersion = serverVersion;
	}

	public int getClientVersion() {
		return clientVersion;
	}

	public int getServerVersion() {
		return serverVersion;
	}

	@Override
	public String getMessage() {
		return "{ client: " + clientVersion + ", server: " + serverVersion + " }";
	}

}
