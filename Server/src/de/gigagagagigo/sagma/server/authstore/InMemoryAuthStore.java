package de.gigagagagigo.sagma.server.authstore;

import java.util.HashMap;
import java.util.Map;

public class InMemoryAuthStore implements AuthStore {

	protected final Map<String, byte[]> salts = new HashMap<>();
	protected final Map<String, byte[]> hashes = new HashMap<>();

	@Override
	public boolean hasUser(String username) {
		return salts.containsKey(username);
	}

	@Override
	public void addUser(String username, byte[] salt, byte[] hash) {
		salts.put(username, salt);
		hashes.put(username, hash);
	}

	@Override
	public byte[] getSalt(String username) {
		return salts.get(username);
	}
	@Override
	public byte[] getHash(String username) {
		return hashes.get(username);
	}

}
