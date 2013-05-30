package om.tnavigator.uned;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import om.tnavigator.Mail;

/** 
 * Utilities for sending email.<br/><br/>
 * Improved version with some properties to be able to use authenticated mail servers.<br/><br/> 
 */
public class UnedMail extends Mail
{
	/** Flag to indicate that it has been used one of the predefined default configuration strings. */
	private static boolean usedMailConfig=false;
	
	/** Username for mail authentication (null by default: no authentication) */
	private static String username=null;
	
	/** Password for mail authentication (empty string by default) */
	private static String password="";
	
	/** Flag to indicate if we need configure session for SSL (false by default) **/
	private static boolean ssl=false;
	
	/** SMTP/STMPS server host name */
	private static String hostname=null;
	
	/** Port used for mail communication (null by default: default port will be used) */
	private static Integer port=null;
	
	/** Flag to indicate if we need to configure session for TLS (false by default) */ 
	private static boolean startTLS=false;
	
	/** Flag to indicate if we want to get debug information by console (false by default) */
	private static boolean debug=false;
	
	/**
	 * Set username for mail authentication.
	 * @param username Username
	 */
	public static void setUsername(String username)
	{
		UnedMail.username=username;
	}
	
	/**
	 * Set password for mail authentication.
	 * @param password Password
	 */
	public static void setPassword(String password)
	{
		UnedMail.password=password;
	}
	
	/**
	 * Set flag to indicate if we need configure session for SSL
	 * @param ssl true to configure session for SSL, false otherwise
	 */
	public static void setSSL(boolean ssl)
	{
		UnedMail.ssl=ssl;
	}
	
	/**
	 * Set SMTP/STMPS server host name.
	 * @param hostname SMTP/STMPS server host name
	 */
	public static void setHostname(String hostname)
	{
		UnedMail.hostname=hostname;
	}
	
	/**
	 * Set port used for mail communication.
	 * @param sPort Port (string)
	 */
	public static void setPort(String sPort)
	{
		if (sPort==null)
		{
			UnedMail.setPort(null);
		}
		else
		{
			try
			{
				UnedMail.port=new Integer(sPort);
			}
			catch (NumberFormatException nfe)
			{
				UnedMail.port=null;
			}
		}
	}
	
	/**
	 * Set port used for mail communication.
	 * @param port Port
	 */
	public static void setPort(int port)
	{
		UnedMail.port=Integer.valueOf(port);
	}
	
	/**
	 * Set flag to indicate if we need to configure session for TLS.
	 * @param startTLS true to configure session for TLS, false otherwise
	 */
	public static void setStartTLS(boolean startTLS)
	{
		UnedMail.startTLS=startTLS;
	}
	
	/**
	 * Set flag to indicate if we want to get debug information by console.
	 * @param debug true to indicate if we want to get debug information by console, false otherwise
	 */
	public static void setDebug(boolean debug)
	{
		UnedMail.debug=debug;
	}
	
	/**
	 * @return true to indicate that it has been used one of the predefined default configuration strings,
	 * false otherwise
	 */
	public static boolean isUsedMailConfig()
	{
		return usedMailConfig;
	}
	
	/**
	 * Configure properties as one of the predefined default configuration strings:<br/>
	 * <ul>
	 * <li><b>Gmail</b>: For using a Gmail account for mail.</li>
	 * <li><b>Yahoo!Mail</b>: For using a Yahoo! Mail account for mail.</li>
	 * <li><b>Hotmail</b>: For using a Hotmail account for mail.</li>
	 * </ul>
	 * However it is still possible to change some configuration properties later if needed.<br/><br/>
	 * @param mailConfig 
	 */
	public static void setMailConfig(String mailConfig)
	{
		if ("Gmail".equalsIgnoreCase(mailConfig))
		{
			setSSL(true);
			setHostname("smtp.gmail.com");
			usedMailConfig=true;
		}
		else if ("Yahoo!Mail".equalsIgnoreCase(mailConfig))
		{
			setSSL(true);
			setHostname("smtp.mail.yahoo.com");
			usedMailConfig=true;
		}
		else if ("Hotmail".equalsIgnoreCase(mailConfig))
		{
			setHostname("smtp.live.com");
			setStartTLS(true);
			setPort(587);
			usedMailConfig=true;
		}
		else
		{
			setSSL(false);
			setHostname(null);
			setStartTLS(false);
			setPort(null);
			usedMailConfig=false;
		}
	}
	
	/**
	 * Sends the given mail message with a given MIME format.
	 * @param from Sender address
	 * @param replyTo Reply-to address
	 * @param to Array of target addresses - or null
	 * @param cc Array of CC addresses - or null
	 * @param cco Array of CCO addresses - or null
	 * @param subject Subject
	 * @param message Message
	 * @param mimeType Mime-type of the message; if null, sends text/plain.
	 * @throws MessagingException If any failure occurs in contacting mail server, etc.
	 */
	public static void send(String from,String replyTo,String[] to,String[] cc,String[] cco,String subject,
		String message,String mimeType) throws MessagingException
	{
		Session s=getMailSession(from);
		MimeMessage mm=createMessage(s,from,replyTo,to,cc,cco,subject);
		if(mimeType==null)
		{
			mm.setText(message,"UTF-8");
		}
		else
		{
			mm.setDataHandler(new DataHandler(message,mimeType));
		}
		Transport t=s.getTransport(ssl?"smtps":"smtp");
		try
		{
			if (username==null)
			{
				if (port==null)
				{
					t.connect(hostname,null,null);
				}
				else
				{
					t.connect(hostname,port.intValue(),null,null);
				}
			}
			else
			{
				if (port==null)
				{
					t.connect(hostname,username,password);
				}
				else
				{
					t.connect(hostname,port.intValue(),username,password);
				}
			}
			t.sendMessage(mm,mm.getAllRecipients());
		}
		finally
		{
			t.close();
		}
	}
	
	/**
	 * @param from Sender address
	 * @return Mail session
	 */
	private static Session getMailSession(String from)
	{
		Properties p=new Properties();
		if (username!=null)
		{
			p.setProperty(ssl?"mail.smtps.auth":"mail.smtp.auth","true");
		}
		p.setProperty(ssl?"mail.smtps.host":"mail.smtp.host",hostname);
		if (port!=null)
		{
			p.setProperty(ssl?"mail.smtps.port":"mail.smtp.port",Integer.toString(port));
		}
		if (startTLS)
		{
			p.setProperty("mail.smtp.starttls.enable","true");
		}
		p.setProperty("mail.from",from);
		Session s=Session.getInstance(p);
		if (debug)
		{
			s.setDebug(true);
		}
		return s;
	}
	
	/**
	 * Create an empty MimeMessage object with all properties set.
	 * @param session Mail session
	 * @param from Sender address
	 * @param replyTo Reply-to address (null to omit)
	 * @param to Array of target addresses - or null
	 * @param cc Array of CC addresses - or null
	 * @param cco Array of CCO addresses - or null
	 * @param subject Subject
	 */
	private static MimeMessage createMessage(Session session,String from,String replyTo,String[] to,String[] cc,
		String[] cco,String subject) throws MessagingException
	{
		MimeMessage mm=new MimeMessage(session);
		if (to!=null)
		{
			InternetAddress[] aiaTo=new InternetAddress[to.length];
			for (int i=0;i<aiaTo.length;++i)
			{
				aiaTo[i]=new InternetAddress(to[i]);
			}
			mm.addRecipients(Message.RecipientType.TO,aiaTo);
		}
		if (cc!=null)
		{
			InternetAddress[] aiaCC=new InternetAddress[cc.length];
			for(int i=0;i<aiaCC.length;++i)
			{
				aiaCC[i]=new InternetAddress(cc[i]);
			}
			mm.addRecipients(Message.RecipientType.CC,aiaCC);
		}
		if (cco!=null)
		{
			InternetAddress[] aiaCCO=new InternetAddress[cco.length];
			for (int i=0;i<aiaCCO.length;++i)
			{
				aiaCCO[i]=new InternetAddress(cco[i]);
			}
			mm.addRecipients(Message.RecipientType.BCC,aiaCCO);
		}
		if (replyTo!=null)
		{
			InternetAddress[] aiaReplyTo=new InternetAddress[1];
			aiaReplyTo[0]=new InternetAddress(replyTo);
			mm.setReplyTo(aiaReplyTo);
		}
		mm.setFrom(new InternetAddress(from));
		mm.setSubject(subject,"UTF-8");
		mm.setSentDate(new Date());
		return mm;
	}
}
