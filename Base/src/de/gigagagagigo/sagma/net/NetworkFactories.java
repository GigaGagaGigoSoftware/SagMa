package de.gigagagagigo.sagma.net;

import java.util.Objects;

public final class NetworkFactories {

	private static NetworkFactory FACTORY = new SocketNetworkFactory();

	public static NetworkFactory get() {
		return FACTORY;
	}

	public static void set(NetworkFactory factory) {
		FACTORY = Objects.requireNonNull(factory);
	}

	// prevent instantiation
	private NetworkFactories() {
		throw new UnsupportedOperationException();
	}

}
