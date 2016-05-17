package de.gigagagagigo.sagma.server;

import static de.gigagagagigo.sagma.packets.AuthReplyPacket.*;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.generators.SCrypt;

import de.gigagagagigo.sagma.server.authstore.AuthStore;
import de.gigagagagigo.sagma.server.authstore.FileAuthStore;

public class Authenticator {

	private final static int SALT_BYTES = 32;
	private final static int HASH_BYTES = 128;
	private final static int SCRYPT_N = 16_384; // 2 ^ 14
	private final static int SCRYPT_R = 8;
	private final static int SCRYPT_P = 1;

	private final AuthStore store = new FileAuthStore("sagma.auth");

	public int register(String username, String password) {
		if (store.hasUser(username))
			return STATUS_USERNAME_TAKEN;
		if (password.isEmpty())
			return STATUS_INVALID_PASSWORD;
		SecureRandom random = new SecureRandom();
		byte[] salt = random.generateSeed(SALT_BYTES);
		byte[] hash = generateHash(password, salt);
		store.addUser(username, salt, hash);
		return STATUS_OK;
	}

	public int logIn(String username, String password) {
		if (!store.hasUser(username))
			return STATUS_INVALID_CREDENTIALS;
		byte[] salt = store.getSalt(username);
		byte[] expected = store.getHash(username);
		byte[] hash = generateHash(password, salt);
		return Arrays.equals(hash, expected) ? STATUS_OK : STATUS_INVALID_CREDENTIALS;
	}

	private byte[] generateHash(String password, byte[] salt) {
		try {
			byte[] pw = password.getBytes("UTF-8");
			return SCrypt.generate(pw, salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, HASH_BYTES);
		} catch (UnsupportedEncodingException e) {
			// UTF-8 must be supported
			throw new RuntimeException("UTF-8 not supported.", e);
		}
	}

}
