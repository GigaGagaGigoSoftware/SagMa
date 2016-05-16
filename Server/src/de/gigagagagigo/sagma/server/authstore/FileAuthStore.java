package de.gigagagagigo.sagma.server.authstore;

import java.io.*;
import java.util.Set;

public class FileAuthStore extends InMemoryAuthStore {

	private final String filename;

	public FileAuthStore(String filename) {
		this.filename = filename;
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				String[] parts = line.split("\t");
				if (parts.length != 3) // ignore malformed lines
					continue;
				byte[] salt = parseHexadecimal(parts[1]);
				byte[] hash = parseHexadecimal(parts[2]);
				if (salt != null && hash != null)
					super.addUser(parts[0], salt, hash);
			}
		} catch (IOException ignored) {}
	}

	@Override
	public void addUser(String username, byte[] salt, byte[] hash) {
		super.addUser(username, salt, hash);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			Set<String> users = hashes.keySet();
			for (String user : users) {
				writer.write(user);
				writer.write("\t");
				writer.write(toHexadecimal(getSalt(user)));
				writer.write("\t");
				writer.write(toHexadecimal(getHash(user)));
				writer.newLine();
			}
		} catch (IOException ignored) {}
	}

	private static final String HEX_CHARS = "0123456789abcdef";

	private byte[] parseHexadecimal(String text) {
		if (!text.matches("(?:[0-9a-fA-F]{2})*"))
			return null;
		text = text.toLowerCase();
		byte[] values = new byte[text.length() / 2];
		for (int i = 0; i < values.length; i++) {
			char highChar = text.charAt(2 * i + 0);
			char lowChar = text.charAt(2 * i + 1);
			int high = HEX_CHARS.indexOf(highChar);
			int low = HEX_CHARS.indexOf(lowChar);
			values[i] = (byte) ((high << 4) | (low));
		}
		return values;
	}

	private String toHexadecimal(byte[] values) {
		StringBuilder result = new StringBuilder(2 * values.length);
		for (byte byteValue : values) {
			int intValue = Byte.toUnsignedInt(byteValue);
			int high = intValue >> 4;
			int low = intValue & 0xf;
			result.append(HEX_CHARS.charAt(high));
			result.append(HEX_CHARS.charAt(low));
		}
		return result.toString();
	}

}
