package de.gigagagagigo.sagma.net;

import java.io.IOException;

public interface NetworkFactory {

	Connection openConnection(String host, int port) throws IOException;
	ConnectionPoint openConnectionPoint(int port) throws IOException;

}
