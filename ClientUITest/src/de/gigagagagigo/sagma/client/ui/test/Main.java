package de.gigagagagigo.sagma.client.ui.test;

import java.io.IOException;

import de.gigagagagigo.sagma.net.LocalNetworkFactory;
import de.gigagagagigo.sagma.net.NetworkFactories;

public class Main {

	private static final boolean SERVER_GUI = true;
	private static final boolean JAVA_FX = true;
	private static final int CLIENTS = 2;

	public static void main(String[] args) {
		NetworkFactories.set(new LocalNetworkFactory());
		startServer();
		startClients();
	}

	private static void startServer() {
		if (SERVER_GUI) {
			de.gigagagagigo.sagma.server.ui.swing.Main.main(new String[0]);
		} else {
			new Thread(() -> {
				try {
					de.gigagagagigo.server.cli.Main.main(new String[0]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	private static void startClients() {
		int clients = 0;
		if (JAVA_FX) {
			new Thread(() -> {
				de.gigagagagigo.sagma.client.ui.fxml.Main.main(new String[0]);
			}).start();
			clients++;
		}
		for (; clients < CLIENTS; clients++) {
			de.gigagagagigo.sagma.client.ui.swing.Main.main(new String[0]);
		}
	}

}
