package om.tnavigator.uned;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import om.tnavigator.NavigatorConfig;
import om.tnavigator.db.OmQueries;
import util.uned.encryption.Encryption;
import util.xml.XML;

/**
 * Loads navigator configuration file.<br/><br/>
 * This is a special version for UNED with some additional features over standard 
 * <i>om.tnavigator.NavigationConfig</i> class:<br/><br/>
 * <ol>
 * <li>Supports encryption of some properties.</li>
 * <li>Provides information of a second DB used for login authentication.</li>
 * </ol>
 */
public class UnedNavigatorConfig extends NavigatorConfig
{
	public final static String PRIVATE_KEY_FILE_NAME="WEB-INF/security/private.key";
	
	/** Database server machine e.g. "codd" */
	private String dbServer;

	/** DB username */
	private String dbUsername;

	/** DB password */
	private String dbPassword;

	/** Database name e.g. "oms-dev" */
	private String dbName;
	
	/** Private key file pathname used for assymetric decryption */
	private String privateKeyPathname;
	
	/** Login database server machine */
	private String loginDbServer;
	
	/** Login DB username */
	private String loginDbUsername;

	/** Login DB password */
	private String loginDbPassword;

	/** Login database name */
	private String loginDbName;
	
	/** Password to decrypt some messages received from GEPEQ */
	private String gepeqDecryptionPassword;
	
	/** Time (in milliseconds) that an user authentication session lurks around before expiring */
	private int authSessionExpiry;
	
	/**
	 * Initialises config.
	 * @param sc Servlet context
	 * @param fConfig Config file
	 * @throws IOException If there's any problem parsing the file etc.
	 */
	public UnedNavigatorConfig(ServletContext sc,File fConfig) throws IOException
	{
		// Allow parent to do their initializations
		super(fConfig);
		
		Document dConfig=XML.parse(fConfig);
		Element eRoot=dConfig.getDocumentElement();
		
		Element eSecurity=null;
		if (XML.hasChild(eRoot,"security"))
		{
			eSecurity=XML.getChild(eRoot,"security");
		}
		privateKeyPathname=null;
		loginDbServer=null;
		loginDbName=null;
		loginDbUsername=null;
		loginDbPassword=null;
		gepeqDecryptionPassword=null;
		
		if (eSecurity!=null)
		{
			StringBuffer previewKeyPath=new StringBuffer(sc.getRealPath(""));
			previewKeyPath.append(File.separatorChar);
			previewKeyPath.append(PRIVATE_KEY_FILE_NAME.replace('/',File.separatorChar));
			if (new File(previewKeyPath.toString()).exists())
			{
				privateKeyPathname=previewKeyPath.toString();
			}
			Element eDB=XML.getChild(eRoot,"database");
			if (privateKeyPathname==null)
			{
				dbServer=XML.getText(eDB,"server");
				dbName=XML.getText(eDB,"name");
				dbUsername=XML.getText(eDB,"username");
				dbPassword=XML.getText(eDB,"password");
				if (XML.hasChild(eSecurity,"login-db"))
				{
					Element eLoginDB=XML.getChild(eSecurity,"login-db");
					loginDbServer=XML.getText(eLoginDB,"server");
					loginDbName=XML.getText(eLoginDB,"name");
					loginDbUsername=XML.getText(eLoginDB,"username");
					loginDbPassword=XML.getText(eLoginDB,"password");
				}
			}
			else
			{
				dbServer=decryptWithEncryptedPrivateKey(XML.getText(eDB,"server"));
				dbName=decryptWithEncryptedPrivateKey(XML.getText(eDB,"name"));
				dbUsername=decryptWithEncryptedPrivateKey(XML.getText(eDB,"username"));
				dbPassword=decryptWithEncryptedPrivateKey(XML.getText(eDB,"password"));
				if (XML.hasChild(eSecurity, "login-db"))
				{
					Element eLoginDB=XML.getChild(eSecurity,"login-db");
					loginDbServer=
						decryptWithEncryptedPrivateKey(XML.getText(eLoginDB,"server"));
					loginDbName=decryptWithEncryptedPrivateKey(XML.getText(eLoginDB,"name"));
					loginDbUsername=
						decryptWithEncryptedPrivateKey(XML.getText(eLoginDB,"username"));
					loginDbPassword=
						decryptWithEncryptedPrivateKey(XML.getText(eLoginDB,"password"));
				}
			}
			if (XML.hasChild(eSecurity,"gepeq-decryption-password"))
			{
				gepeqDecryptionPassword=XML.getText(eSecurity,"gepeq-decryption-password");
			}
			authSessionExpiry=0;
			if (XML.hasChild(eSecurity,"auth-session-expiry"))
			{
				String authSessionExpiryStr=XML.getText(eSecurity,"auth-session-expiry");
				try
				{
					if (authSessionExpiryStr.endsWith("h"))
					{
						authSessionExpiry=60*60*1000*
							Integer.parseInt(authSessionExpiryStr.substring(0,authSessionExpiryStr.length()-1));
					}
					else if (authSessionExpiryStr.endsWith("m"))
					{
						authSessionExpiry=60*1000*
							Integer.parseInt(authSessionExpiryStr.substring(0,authSessionExpiryStr.length()-1));
					}
					else if (authSessionExpiryStr.endsWith("ms"))
					{
						authSessionExpiry=
							Integer.parseInt(authSessionExpiryStr.substring(0,authSessionExpiryStr.length()-2));
					}
					else if (authSessionExpiryStr.endsWith("s"))
					{
						authSessionExpiry=1000*
							Integer.parseInt(authSessionExpiryStr.substring(0,authSessionExpiryStr.length()-1));
					}
					else
					{
						authSessionExpiry=Integer.parseInt(authSessionExpiryStr);
					}
				}
				catch (NumberFormatException nfe)
				{
					authSessionExpiry=0;
				}
			}
		}
		
		Element eMail=null;
		if (XML.hasChild(eRoot,"mail"))
		{
			eMail=XML.getChild(eRoot,"mail");
			if (XML.hasChild(eMail,"config"))
			{
				UnedMail.setMailConfig(XML.getText(eMail,"config"));
			}
			if (XML.hasChild(eMail,"username"))
			{
				if (privateKeyPathname==null)
				{
					UnedMail.setUsername(XML.getText(eMail,"username"));
					if (XML.hasChild(eMail,"password"))
					{
						UnedMail.setPassword(XML.getText(eMail,"password"));
					}
				}
				else
				{
					UnedMail.setUsername(decryptWithEncryptedPrivateKey(XML.getText(eMail,"username")));
					if (XML.hasChild(eMail,"password"))
					{
						UnedMail.setPassword(decryptWithEncryptedPrivateKey(XML.getText(eMail,"password")));
					}
				}
			}
			if (XML.hasChild(eMail,"hostname"))
			{
				UnedMail.setHostname(XML.getText(eMail,"hostname"));
			}
			else if (!UnedMail.isUsedMailConfig() && XML.hasChild(eRoot,"smtpserver"))
			{
				UnedMail.setHostname(XML.getText(eRoot,"smtpserver"));
			}
			if (XML.hasChild(eMail,"port"))
			{
				UnedMail.setPort(XML.getText(eMail,"port"));
			}
			if (XML.hasChild(eMail,"ssl"))
			{
				String ssl=XML.getText(eMail,"ssl");
				if ("true".equals(ssl))
				{
					UnedMail.setSSL(true);
				}
				else if ("false".equals(ssl))
				{
					UnedMail.setSSL(false);
				}
			}
			if (XML.hasChild(eMail,"start-tls"))
			{
				String startTLS=XML.getText(eMail,"start-tls");
				if ("true".equals(startTLS))
				{
					UnedMail.setStartTLS(true);
				}
				else if ("false".equals(startTLS))
				{
					UnedMail.setStartTLS(false);
				}
			}
			if (XML.hasChild(eMail,"debug"))
			{
				String debug=XML.getText(eMail,"debug");
				if ("true".equals(debug))
				{
					UnedMail.setDebug(true);
				}
				else if ("false".equals(debug))
				{
					UnedMail.setDebug(false);
				}
			}
		}
	}
	
	/**
	 * @return Private key file name used for assymetric decryption
	 */
	public String getPrivateKeyPathname()
	{
		return privateKeyPathname;
	}
	
	/**
	 * @return Salted password used to decrypt messages received from GEPEQ
	 */
	public String getGepeqDecryptionPassword()
	{
		return gepeqDecryptionPassword;
	}
	
	@Override
	protected String getDatabaseURL(OmQueries oq) throws ClassNotFoundException
	{
		return oq.getURL(dbServer,dbName,dbUsername,dbPassword);
	}
	
	/** @return Full JDBC URL of login database including username and password */
	public String getLoginDatabaseURL(OmQueries oq) throws ClassNotFoundException
	{
		return oq.getURL(loginDbServer,loginDbName,loginDbUsername,loginDbPassword);
	}
	
	/** 
	 * @return Time (in milliseconds) that an user authentication session lurks around before expiring 
	 * or 0 to use default time (depends on authentication plugin implementation but usually is 15 minutes)
	 */
	public int getAuthSessionExpiry()
	{
		return authSessionExpiry;
	}
	
	/**
	 * Decrypts a message using an encrypted private key file (assymetric encryption).
	 * @param message Message (string)
	 * @return Decrypted message (string) or original message if it can not be decrypted
	 */
	private String decryptWithEncryptedPrivateKey(String message)
	{
		String decryptedMessage=null;
		if (!message.equals(""))
		{
			decryptedMessage=Encryption.decryptWithEncryptedPrivateKey(message,getPrivateKeyPathname());
		}
		return decryptedMessage==null?message:decryptedMessage;
	}
	
	/**
	 * Decrypts a message using the salted password to decrypt messages received from GEPEQ (symetric encryption).
	 * @param message Message (string)
	 * @return Decrypted message (string), original message if there is no salted password provide to decrypt
	 * GEPEQ messages within configuration file or null otherwise
	 */
	public String decryptFromGEPEQ(String message)
	{
		String decryptedMessage=null;
		if (message!=null)
		{
			String gepeqDecryptionPassword=getGepeqDecryptionPassword();
			if (gepeqDecryptionPassword==null)
			{
				decryptedMessage=message;
			}
			else
			{
				decryptedMessage=Encryption.decryptWithSymmetricPassword(message,gepeqDecryptionPassword);
			}
		}
		return decryptedMessage;
	}
}
