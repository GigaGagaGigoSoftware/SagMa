package de.gigagagagigo.server.cli;

import java.io.IOException;
import java.util.Scanner;

import de.gigagagagigo.sagma.server.SagMaServer;

public class Main {

	public static void main(String[] args) throws IOException {
		SagMaServer server = new SagMaServer();
		server.start();
		try (Scanner scanner = new Scanner(System.in)) {
			boolean done = false;
			while (!done) {
				String line = scanner.nextLine();
				if ("stop".equalsIgnoreCase(line)) {
					done = true;
				} else {
					System.out.println("Enter stop to stop the server.");
				}
			}
		}
		server.stop();
	}

}
