package om.tnavigator.auth.uned;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import om.tnavigator.NavigatorServlet;
import om.tnavigator.auth.Authentication;
import om.tnavigator.auth.UncheckedUserDetails;
import om.tnavigator.auth.UserDetails;
import om.tnavigator.db.DatabaseAccess;
import om.tnavigator.uned.UnedNavigatorConfig;
import om.tnavigator.uned.UnedNavigatorServlet;
import util.misc.IO;
import util.misc.Strings;
import util.uned.encryption.Encryption;
import util.xml.XHTML;
import util.xml.XML;

//TODO los grupos no han sido implementados de momento, por ahora solo se pueden tratar usuarios individualmente
/**
* This implementation of authentication uses user information from GEPEQ database.<br/><br/>
* Difference with GepepAuth is than an user with needed permission is allowed to login as other user even
* if he/she doesn't know the password of that user.<br/><br/>
* This can be useful for testing but obviously is not appropiate for the final environment where students
* perform the tests, so this authentication plugin is only recommended for a testing environment.<br/><br/>
* <b><u>Tables</u></b><br/>
* <ul>
* <li><b>users</b> (id,login,password,oucu,id_type,gepeq_user, ...)</li>
* <li><b>userpermissions</b> (id_user,id_permission,value, ...)</li>
* <li><b>usertypepermissions</b> (id_usertype,id_permission,value, ...)</li>
* <li><b>permissions</b> (id,name,default_value, ...)</li>
* </ul>
* <b><u>Login</u></b><br/><br/>
* User is logged in by comparing username with 'login' field and checking password against password digest 
* stored at DB.<br/><br/>
* Then if user is trying to login as other user we need to check that the user has permission to do that
* before continue.<br/><br/>
* If login is successful a cookie 'previewauth' is generated:<br/><br/>
* <u>username</u>:<u>authentication_digest</u><br/><br/>
* where:<br/><br/>
* <ol>
* <li><b>username</b>: This is the 'oucu' for username (or the 'oucu' for other user if the user has been 
* logged in as other user).</li>
* <li><b>authentication_digest</b>: This is an authenticated digest generated with help of a secret 
* password. The secret password changes for every user session (even for the same user) so old cookies 
* won't overcome authentication.</li>
* </ol>
* <b><u>Authentication</u></b><br/><br/>
* Authentication is done by checking authentication digest from cookie.<br/><br/>
* Moreover client IP is also checked to increase security.<br/><br/>
* <b><u>Logout</u></b><br/><br/>
* A logout option has been added to allow an user to logout when he/she wants (that invalidates any further 
* attempt to use last authentication cookie even from the same machine).
*/
public class PreviewAuth implements Authentication
{
	/** Name of cookie used for Preview authentication */
	public final static String COOKIE="previewauth";
	
	/** Character used as field separator within cookie */
	public final static char COOKIE_SEPARATOR=':';
	
	/** Command for a login request */
	public final static String LOGIN_REQUEST="login";
	
	/** Command for a logout request */
	public final static String LOGOUT_REQUEST="logout";
	
	/**
	 *  Default time (in milliseconds) that an user authentication session lurks around before expiring
	 * (by default 15 mins)
	 */
	private final static int DEFAULT_AUTH_SESSION_EXPIRY=15*60*1000;
	
	/**
	 * Utility class to construct a response that ignores clearing cookies.
     */
	private static class IgnoreClearCookiesResponse extends HttpServletResponseWrapper
	{
		public IgnoreClearCookiesResponse(HttpServletResponse response)
		{
			super(response);
		}

		@Override
		public void addCookie(Cookie cookie)
		{
			// Does nothing
		}
	}
	
	/** Navigator Servlet */
	private UnedNavigatorServlet ns;
	
	/** Map of secret passwords for users */
	private static Map<String,String> secretPasswords=new HashMap<String,String>();
	
	/** Map of remote IPs for users */
	private static Map<String,String> remoteIps=new HashMap<String,String>();
	
	/** Map of last action times for users */
	private static Map<String,Long> lastActionTimes=new HashMap<String,Long>();
	
	/** Template folder */
	private File templatesFolder;
	
	/**
	 *  Time (in milliseconds) that an user authentication session lurks around before expiring
	 * (default time is 15 mins)
	 */
	private int authSessionExpiry;
	
	/** From address for email */
	private String mailFrom;
	
	public static final String LOGIN_FORM="previewauth.login.xhtml";
	public static final String OFFER_LOGIN="previewauth.offerlogin.xhtml";
	public static final String LOGOUT_RESULT="previewauth.logout.xhtml";
	public static final String LOGOUT_ERROR="previewauth.logout.error.xhtml";
	
	private static Map<NavigatorServlet,PreviewAuth> authMap=new HashMap<NavigatorServlet,PreviewAuth>();
	
	/**
	 * Standard constructor.
	 * @param ns Servlet
	 * @throws SQLException If any problem accessing GEPEQ database
	 */
	public PreviewAuth(NavigatorServlet ns) throws SQLException
	{
		// Remember settings from servlet
		if (ns instanceof UnedNavigatorServlet)
		{
			this.ns=(UnedNavigatorServlet)ns;
		}
		else
		{
			throw new SQLException(
				"Error. PreviewAuth authentication requires a navigator servlet derived from om.tnavigator.uned.UnedNavigatorServlet class");
		}
		UnedNavigatorConfig nc=(UnedNavigatorConfig)ns.getNavigatorConfig();
		templatesFolder=ns.getTemplatesFolder();
		authSessionExpiry=nc.getAuthSessionExpiry();
		if (authSessionExpiry<=0)
		{
			authSessionExpiry=DEFAULT_AUTH_SESSION_EXPIRY;
		}
		mailFrom=nc.getAlertMailFrom();
		
		authMap.put(ns,this);
	}
	
	/**
	 * @param ns Servlet
	 * @return Authentication plugin instance
	 * @throws SQLException
	 */
	private static PreviewAuth getAuth(NavigatorServlet ns) throws SQLException
	{
		PreviewAuth auth=null;
		if (authMap.containsKey(ns))
		{
			auth=authMap.get(ns);
		}
		else
		{
			auth=new PreviewAuth(ns);
		}
		return auth;
	}
	
	@Override
	public void becomeTestUser(HttpServletResponse response,String suffix)
	{
		StringBuffer cookieContent=new StringBuffer("!tst");
		cookieContent.append(suffix);
		cookieContent.append(':');
		Cookie cookie=new Cookie(COOKIE,cookieContent.toString());
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	@Override
	public void close()
	{
	}
	
	@Override
	public String getLoginOfferXHTML(HttpServletRequest request) throws IOException
	{
		String offer=IO.loadString(new FileInputStream(new File(templatesFolder,OFFER_LOGIN)));
		return XML.replaceToken(
			offer,"%%","LOGINURL",XHTML.escape(getLoginPageURL(request),XHTML.ESCAPE_ATTRSQ));
	}
	
	/**
	 * @param request HTTP request
	 * @return Cookie from HTTP request
	 */
	public static String getCookie(HttpServletRequest request)
	{
		String cookie=null;
		Cookie[] cookies=request.getCookies();
		if (cookies!=null)
		{
			for (Cookie c:cookies)
			{
				if (c.getName().equals(COOKIE))
				{
					cookie=c.getValue();
					if (cookie.indexOf(COOKIE_SEPARATOR)==-1)
					{
						cookie=null;
					}
					break;
				}
			}
		}
		return cookie;
	}
	
	/**
	 * Generates a new cookie with provided username and secret password
	 * @param response HTTP response
	 * @param username Username
	 * @param secretPassword Secret password
	 */
	private void generateCookie(HttpServletResponse response,String username,String secretPassword)
	{
		StringBuffer cookieContent=new StringBuffer(username);
		cookieContent.append(COOKIE_SEPARATOR);
		StringBuffer authGenStr=new StringBuffer(cookieContent);
		authGenStr.append(secretPassword);
		cookieContent.append(Encryption.digest(authGenStr.toString()));
		Cookie cookie=new Cookie(COOKIE,cookieContent.toString());
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	/**
	 * Clear cookie.
	 * @param response HTTP response
	 */
	public static void clearCookie(HttpServletResponse response)
	{
		// Clear invalid cookie
		Cookie c=new Cookie(COOKIE,"");
		c.setMaxAge(0);
		c.setPath("/");
		response.addCookie(c);
	}
	
	@Override
	public UncheckedUserDetails getUncheckedUserDetails(HttpServletRequest request)
	{
		UncheckedUserDetails uncheckedUser=null;
		String cookie=getCookie(request);
		if(cookie==null)
		{
			uncheckedUser=new PreviewUncheckedUser(null);
		}
		else
		{
			uncheckedUser=new PreviewUncheckedUser(cookie);
		}
		return uncheckedUser;
	}
	
	/**
	 * @param cookie Cookie (string)
	 * @return Authentication digest from cookie
	 */
	private String readAuthenticationDigestFromCookie(String cookie)
	{
		String authDigest=null;
		if (cookie!=null)
		{
			int startPos=cookie.indexOf(COOKIE_SEPARATOR)+1;
			if (startPos>0 && startPos<cookie.length())
			{
				authDigest=cookie.substring(startPos);
			}
		}
		return authDigest;
	}
	
	@Override
	public UserDetails getUserDetails(HttpServletRequest request,HttpServletResponse response,
		boolean bRequireLogin) throws IOException
	{
		UserDetails user=null;
		PreviewUncheckedUser uncheckedUser=(PreviewUncheckedUser)getUncheckedUserDetails(request);
		String cookie=uncheckedUser.getCookie();
		String secretPassword=secretPasswords.get(uncheckedUser.getUsername());
		String remoteIp=remoteIps.get(uncheckedUser.getUsername());
		if (cookie!=null && secretPassword!=null && getRemoteIP(request).equals(remoteIp))
		{
			StringBuffer authCheckStr=new StringBuffer(uncheckedUser.getUsername());
			authCheckStr.append(COOKIE_SEPARATOR);
			authCheckStr.append(secretPassword);
			if (Encryption.matches(authCheckStr.toString(),readAuthenticationDigestFromCookie(cookie)))
			{
				// We check that user already exists
				DatabaseAccess.Transaction dat=null;
				try
				{
					StringBuffer query=new StringBuffer("SELECT COUNT(*) FROM users WHERE oucu=");
					query.append(Strings.sqlQuote(uncheckedUser.getUsername()));
					
					dat=ns.getLoginDatabaseAccess().newTransaction();
					ResultSet rs=dat.query(query.toString());
					rs.next();
					if (rs.getInt(1)>0)
					{
						//TODO faltaria obtener la info de los grupos de la BD de gepeq
						user=new PreviewUser(cookie,uncheckedUser.getUsername());
					}
				}
				catch (SQLException se)
				{
					StringBuffer errorMessage=new StringBuffer("Error authenticating user in database: ");
					errorMessage.append(se.getMessage());
					throw new IOException(errorMessage.toString(),se);
				}
				finally
				{
					if (dat!=null)
					{
						dat.finish();
					}
				}
			}
			if (user==null)
			{
				clearCookie(response);
			}
		}
		if (user==null)
		{
			// No cookie or auth failed
			if (bRequireLogin)
			{
				redirect(request,response);
			}
			else
			{
				user=PreviewUser.NOT_LOGGED_IN;
			}
		}
		return user;
	}
	
	/**
	 * @param errorText Text to be displayed at logout error page
	 * @return Logot error page
	 * @throws IOException 
	 */
	private Document getLogoutErrorPage(String errorText) throws IOException
	{
		Document doc=XML.parse(new File(templatesFolder,LOGOUT_ERROR));
		Element errorElem=XML.find(doc,"class","warning");
		XML.createText(errorElem,errorText);
		return doc;
	}
	
	@Override
	public boolean handleRequest(String subPath,HttpServletRequest request,HttpServletResponse response) 
		throws Exception
	{
		boolean handled=false;
		
		// Check path after /!auth/ for a recognized command for request
		if (subPath.equals(LOGIN_REQUEST))
		{
			handled=true;
			
			// Load form and fill in URL
			Document d=XML.parse(new File(templatesFolder,LOGIN_FORM));
			Element urlField=XML.find(d,"name","url");
			String url=request.getParameter("url");
			if (url==null)
			{
				throw new Exception("Missing url parameter");
			}
			urlField.setAttribute("value",url);
			
			// We only handle POST requests
			if (request.getMethod().equals("POST"))
			{
				// Get username (login) & password
				String username=request.getParameter("username");
				String password=request.getParameter("password");
				// Check they match
				DatabaseAccess.Transaction dat=ns.getLoginDatabaseAccess().newTransaction();
				try
				{ //id,login,password,oucu,id_type,gepeq_user
					StringBuffer query=new StringBuffer(
						"SELECT id,oucu,password,id_type,gepeq_user FROM users WHERE login=");
					query.append(Strings.sqlQuote(username));
					ResultSet rs=dat.query(query.toString());
					boolean ok=false; 
					if (rs.next())
					{
						long userId=rs.getLong(1);
						String oucu=rs.getString(2);
						String passwordDigest=rs.getString(3);
						long usertypeId=rs.getLong(4);
						boolean gepegUser=rs.getBoolean(5);
						
						// Check password
						if (Encryption.matches(password,passwordDigest))
						{
							// Get other username
							String otherUsername=request.getParameter("otherusername");
							if (otherUsername!=null && !otherUsername.equals(""))
							{
								if (gepegUser)
								{
									rs=dat.query(
										"SELECT id,default_value FROM permissions WHERE name='PERMISSION_OM_LOGIN_AS_OTHER_USER_FOR_PREVIEW_TESTS_ALLOWED'");
									long permissionId=0L;
									boolean loginAsOtherUserAllowedDefault=false;
									if (rs.next())
									{
										permissionId=rs.getLong(1);
										loginAsOtherUserAllowedDefault=rs.getString(2).equals("true");
									}
									if (permissionId>0L)
									{
										boolean permissionFound=false;
										query.setLength(0);
										query.append("SELECT value from userpermissions WHERE id_user=");
										query.append(userId);
										query.append(" AND id_permission=");
										query.append(permissionId);
										rs=dat.query(query.toString());
										if (rs.next())
										{
											permissionFound=true;
											ok=rs.getString(1).equals("true");
										}
										else if (usertypeId>0L)
										{
											query.setLength(0);
											query.append(
												"SELECT value from usertypepermissions WHERE id_usertype=");
											query.append(usertypeId);
											query.append("AND id_permission=");
											query.append(permissionId);
											rs=dat.query(query.toString());
											if (rs.next())
											{
												permissionFound=true;
												ok=rs.getString(1).equals("true");
											}
										}
										if (!permissionFound)
										{
											ok=loginAsOtherUserAllowedDefault;
										}
									}
								}
								if (ok)
								{
									query.setLength(0);
									query.append("SELECT oucu FROM users WHERE login=");
									query.append(Strings.sqlQuote(otherUsername));
									rs=dat.query(query.toString());
									if (rs.next())
									{
										oucu=rs.getString(1);
									}
									else
									{
										ok=false;
									}
								}
							}
							else
							{
								ok=true;
							}
							if (ok)
							{
								// Set a secret password
								String secretPassword=Encryption.generateSaltedPassword();
								secretPasswords.put(oucu,secretPassword);
								
								// Set remote IP
								remoteIps.put(oucu,getRemoteIP(request));
								
								// Set current time as the last action time
								lastActionTimes.put(oucu,System.currentTimeMillis());
								
								// Generate cookie
								generateCookie(response,oucu,secretPassword);
								
								// Redirect to URL
								response.sendRedirect(url);
							}
						}
					}
					if (!ok)
					{
						// Not OK, try again
						Element errorText=XML.find(d,"class","warning");
						XML.createText(errorText,
							"Username or password not recognized (or login as other user not allowed). Please try again, or contact the administrator for help.");
						XHTML.output(d,request,response,"en");
					}
				}
				finally
				{
					dat.finish();
				}
			}
			else
			{
				XHTML.output(d,request,response,"en");
			}
		}
		else if (subPath.equals(LOGOUT_REQUEST))
		{
			handled=true;
			
			// Get user
			UserDetails user=getUserDetails(request,response,false);
			if (user==null || !user.isLoggedIn())
			{
				XHTML.output(getLogoutErrorPage("Invalid attempt to logout. That can be caused if user is not logged in or if the athentication cookie is corrupt."),request,response,"en");
			}
			else
			{
				// We try to get actual username from GEPEQ db instead of oucu
				String gepeqUsername=null;
				DatabaseAccess.Transaction dat=ns.getLoginDatabaseAccess().newTransaction();
				try
				{
					StringBuffer query=new StringBuffer("SELECT login FROM users WHERE oucu=");
					query.append(Strings.sqlQuote(user.getUsername()));
					ResultSet rs=dat.query(query.toString());
					if (rs.next())
					{
						gepeqUsername=rs.getString(1);
					}
				}
				finally
				{
					dat.finish();
				}
				if (gepeqUsername==null)
				{
					// If we can't find actual username from GEPEQ db we will use oucu
					gepeqUsername=user.getUsername();
				}
				
				// We delete authentication cookie
				clearCookie(response);
				
				// We remove mappings for this user
				resetForUser(user.getUsername());
				
				// Redirect to URL
				response.sendRedirect(getLogoutResultPageURL(request,gepeqUsername));
			}
		}
		return handled;
	}
	
	@Override
	public void obtainPerformanceInfo(Map<String,Object> m)
	{
		// None tracked.
	}
	
	@Override
	public void redirect(HttpServletRequest request,HttpServletResponse response)
		throws IOException
	{
		response.sendRedirect(getLoginPageURL(request));
	}
	
	/**
	 * @param request HTTP request
	 * @return Login page URL
	 * @throws IOException 
	 */
	private String getLoginPageURL(HttpServletRequest request) throws IOException
	{
		// Get URL up to before webapp context, add the context back (doesn't end in /) 
		// then add in the path to login page
		String requestURL=request.getRequestURI();
		String contextPath=request.getContextPath();
		StringBuffer loginPageURL=new StringBuffer(requestURL.substring(0,requestURL.indexOf(contextPath)));
		loginPageURL.append(contextPath);
		loginPageURL.append("/!auth/login?url=");
		StringBuffer url=new StringBuffer(requestURL.substring(0,requestURL.indexOf(contextPath)));
		url.append(contextPath);
		url.append("/!logged-preview");
		url.append(requestURL.substring(requestURL.indexOf(contextPath)+contextPath.length()));
		url.append("?restart");
		loginPageURL.append(URLEncoder.encode(url.toString(),"UTF-8"));
		return loginPageURL.toString();
	}
	
	/**
	 * @param request HTTP request
	 * @param username Username
	 * @return Logout result page URL
	 * @throws IOException
	 */
	private String getLogoutResultPageURL(HttpServletRequest request,String username) throws IOException
	{
		// Get URL up to before webapp context, add the context back (doesn't end in /) 
		// then add in the path to login page
		String requestURL=request.getRequestURI();
		String contextPath=request.getContextPath();
		StringBuffer logoutResultPageURL=
			new StringBuffer(requestURL.substring(0,requestURL.indexOf(contextPath)));
		logoutResultPageURL.append(contextPath);
		logoutResultPageURL.append("/!logout-result?username=");
		logoutResultPageURL.append(URLEncoder.encode(username,"UTF-8"));
		return logoutResultPageURL.toString();
	}
	
	/*
	@Override
	public String sendMail(String username,String personID,String content,int emailType) throws IOException
	{
		// Split into subject and message
		String[] subjectAndMessage=content.split("\n",2);
		if (subjectAndMessage.length!=2)
		{
			throw new IOException(
				"Email content must begin with a one-line subject then contain additional content");
		}
		try
		{
			UnedMail.send("david.ballestin@gmail.com","david.ballestin@gmail.com",
				new String[] {"david.ballestin@gmail.com"},null,subjectAndMessage[0],subjectAndMessage[1],
				null);
		}
		catch(MessagingException me)
		{
			IOException e=new IOException("Error sending user email");
			e.initCause(me);
			e.printStackTrace();
			throw e;
		}
		return "";
	}
	*/
	
	//TODO falta por implementar el envio de correos.... arriba hay comentada una implementacion de prueba para ver que la nueva clase UnedMail funciona correctamente pero la implementacion se tendria que hacer diferente  
	@Override
	public String sendMail(String username,String personID,String content,int emailType) throws IOException
	{
		throw new IOException("Error sending user email. Not implemented for preview.");
	}
	
	/**
	 * @param request HTTP request
	 * @return Remote IP from client
	 */
	private String getRemoteIP(HttpServletRequest request)
	{
		String remoteIP=request.getHeader("client_ip");
		if (remoteIP==null)
		{
			remoteIP=request.getHeader("Client-IP");
		}
		if (remoteIP==null)
		{
			remoteIP=request.getHeader("X_FORWARDED_FOR");
		}
		if (remoteIP==null)
		{
			remoteIP=request.getRemoteAddr();
		}
		return remoteIP;
	}
	
	/**
	 * Reset authentication initializations for an specific user. 
	 * @param username Username
	 */
	public static void resetForUser(String username)
	{
		secretPasswords.remove(username);
		remoteIps.remove(username);
		lastActionTimes.remove(username);
	}
	
	/**
	 * This method check current time against last action time for current authenticated user to validate
	 * (or invalidate) his/her user session and update it if validation is successful.<br/><br/>
	 * Note that ellapsed time between last action time and current time cannot be more than 15 minutes.
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param ns Servlet
	 * @return true if last action time for current authenticated user has been updated, false otherwise 
	 * (it means that authentication has failed)
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static boolean touch(HttpServletRequest request,HttpServletResponse response,NavigatorServlet ns)
		throws SQLException,IOException
	{
		boolean ok=true;
		String cookie=getCookie(request);
		if (cookie!=null)
		{
			ok=false;
			PreviewAuth previewAuth=getAuth(ns);
			IgnoreClearCookiesResponse ignoreClearCookiesResponse=new IgnoreClearCookiesResponse(response);
			UserDetails user=previewAuth.getUserDetails(request,ignoreClearCookiesResponse,false);
			if (user!=null && user.isLoggedIn())
			{
				Long lastActionTime=lastActionTimes.get(user.getUsername());
				if (lastActionTime!=null && 
					lastActionTime.longValue()>=System.currentTimeMillis()-previewAuth.authSessionExpiry)
				{
					ok=true;
					lastActionTimes.put(user.getUsername(),Long.valueOf(System.currentTimeMillis()));
				}
			}
		}
		return ok;
	}
}
