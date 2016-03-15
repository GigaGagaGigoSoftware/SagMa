package de.gigagagagigo.sagma.client.ui.test;

import java.io.IOException;

import de.gigagagagigo.sagma.net.LocalNetworkFactory;
import de.gigagagagigo.sagma.net.NetworkFactories;

public class Main {

	private static final boolean SERVER_GUI = true;
	private static final boolean SWING = false;
	private static final int SWING_CLIENTS = 2;

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
		if (SWING) {
			for (int i = 0; i < SWING_CLIENTS; i++) {
				de.gigagagagigo.sagma.client.ui.swing.Main.main(new String[0]);
			}
		} else {
			de.gigagagagigo.sagma.client.ui.fxml.Main.main(new String[0]);
		}
	}

}
