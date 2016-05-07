package de.gigagagagigo.sagma.server.authstore;

public interface AuthStore {

	boolean hasUser(String username);
	void addUser(String username, byte[] salt, byte[] hash);
	byte[] getSalt(String username);
	byte[] getHash(String username);

}
