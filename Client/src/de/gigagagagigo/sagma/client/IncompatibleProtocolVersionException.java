package de.gigagagagigo.sagma.client;

public class IncompatibleProtocolVersionException extends Exception {

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
