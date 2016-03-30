package io.github.furti.jmxhealth.crypt;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptUtils {
	public static final String CRYPT_KEY = "io.github.furti.jmxhealth.crypt-key";

	private static final int KEY_SIZE_IN_BITS = 128;

	private static SecretKey SECRET_KEY = null;

	private CryptUtils() {

	}

	public static String generateKey() throws Exception {
		SecureRandom random = SecureRandom.getInstanceStrong();
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(KEY_SIZE_IN_BITS, random);
		SecretKey key = keyGen.generateKey();

		return new String(Base64.getEncoder().encode(key.getEncoded()), "UTF-8");
	}

	public static String encrypt(String value) throws Exception {
		Cipher cipher = getEncryptionCipher();

		byte[] encoded = cipher.doFinal(value.getBytes(Charset.forName("UTF-8")));

		return new String(Base64.getEncoder().encode(encoded), "UTF-8");
	}

	public static String decrypt(String value) throws Exception {
		Cipher cipher = getDecryptionCipher();

		byte[] decoded = cipher.doFinal(Base64.getDecoder().decode(value.getBytes(Charset.forName("UTF-8"))));

		return new String(decoded, "UTF-8");
	}

	private static Cipher getEncryptionCipher() throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		if (SECRET_KEY == null) {
			initializeSecretKey();
		}

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new IvParameterSpec(SECRET_KEY.getEncoded()));

		return cipher;
	}

	private static Cipher getDecryptionCipher() throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		if (SECRET_KEY == null) {
			initializeSecretKey();
		}

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new IvParameterSpec(SECRET_KEY.getEncoded()));

		return cipher;
	}

	private static void initializeSecretKey() {
		String encodedKey = System.getProperty(CRYPT_KEY);

		if (encodedKey == null) {
			throw new NullPointerException(
					"No Crypt Key set. Did you forget to set the " + CRYPT_KEY + " system property?");
		}

		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		SECRET_KEY = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	}
}
