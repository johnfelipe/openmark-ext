package util.uned.encryption;

import java.io.IOException;

import es.uned.lsi.encryption.AssymetricEncryptor;
import es.uned.lsi.encryption.AssymetricEncryptor.AssymetricKey;
import es.uned.lsi.encryption.PasswordDigester;
import es.uned.lsi.encryption.SymmetricEncryptor;

/**
 * Utility class used for security that allows to encrypt and decrypt message by several methods.<br/><br/>
 * It needs the file <i>encryption.jar</i> to be in the <i>WEB-INF/lib</i> folder of the web application.
 */
public final class Encryption
{
	/**
	 * Private default constructor to avoid class instantiation.
	 */
	private Encryption()
	{
	}
	
    /**
     * @return Salted password (Salt and password secure random generated) or null if an error occurs
     */
    public static String generateSaltedPassword()
    {
    	return SymmetricEncryptor.generateSaltedPassword();
    }
	
	/**
	 * Digests a message (string).
	 * @param message Message (string)
	 * @return Digested message (string) or null if message can not be digested
	 */
	public static String digest(String message)
	{
		return PasswordDigester.digest(message);
	}
	
	/**
	 * Compare message with the digest.
	 * @param message Message
	 * @param digest Digest
	 * @return true if the message matches the digest, false if not (or if occurs an error)
	 */
	public static boolean matches(String message,String digest)
	{
		return PasswordDigester.matches(message,digest);
	}
	
	/**
	 * Decrypts a message using an encrypted private key file (assymetric encryption).
	 * @param message Message (string)
	 * @param privateKeyFileName Private key file name
	 * @return Decrypted message (string) or null if message can not be decrypted
	 */
	public static String decryptWithEncryptedPrivateKey(String message,String privateKeyFileName)
	{
		String decryptedMessage=null;
		try
		{
			AssymetricKey privateKey=AssymetricEncryptor.readAssymetricKeyFromEncryptedFile(privateKeyFileName);
			decryptedMessage=AssymetricEncryptor.decrypt(message,privateKey);
		}
		catch (IOException ioe)
		{
			decryptedMessage=null;
		}
		return decryptedMessage;
	}
	
	/**
	 * Decrypts a message using a symmetric salted password (symmetric encryption).
	 * @param message Message (string)
	 * @param symmetricPassword Symmetric salted password
	 * @return Decrypted message (string) or null if message can not be decrypted
	 */
	public static String decryptWithSymmetricPassword(String message,String symmetricPassword)
	{
		return SymmetricEncryptor.decrypt(message,symmetricPassword);
	}
}
