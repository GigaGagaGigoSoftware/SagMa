package de.gigagagagigo.sagma.client.ui.test;

import java.io.IOException;

import de.gigagagagigo.sagma.net.LocalNetworkFactory;
import de.gigagagagigo.sagma.net.NetworkFactories;

public class Main {

	private static final boolean SWING = true;
	private static final int CLIENTS = 2;

	public static void main(String[] args) {
		NetworkFactories.set(new LocalNetworkFactory());
		startServer();
		for (int i = 0; i < CLIENTS; i++) {
			if (SWING) {
				de.gigagagagigo.sagma.client.ui.swing.Main.main(new String[0]);
			} else {
				de.gigagagagigo.sagma.client.ui.fxml.Main.main(new String[0]);
			}
		}
	}

	private static void startServer() {
		new Thread(() -> {
			try {
				de.gigagagagigo.server.cli.Main.main(new String[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

}
