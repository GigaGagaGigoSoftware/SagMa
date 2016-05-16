package de.gigagagagigo.sagma.server.authstore;

public interface AuthStore {

	/**
	 * Checks whether a record for the given username exists.
	 * 
	 * @param username not null
	 */
	boolean hasUser(String username);

	/**
	 * Adds a new record to the auth store. If a record with the given username already exists, it will be overwritten.
	 * 
	 * @param username not null
	 * @param salt not null
	 * @param hash not null
	 */
	void addUser(String username, byte[] salt, byte[] hash);

	/**
	 * Returns the salt associated with the username.
	 * 
	 * @param username not null
	 * @return the salt for the user or null if no record was found
	 */
	byte[] getSalt(String username);

	/**
	 * Returns the hash associated with the username.
	 * 
	 * @param username not null
	 * @return the hash for the user or null if no record was found
	 */
	byte[] getHash(String username);

}
