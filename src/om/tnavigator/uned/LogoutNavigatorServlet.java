package om.tnavigator.uned;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import om.tnavigator.NavigatorServlet;
import om.tnavigator.UserSession;
import om.tnavigator.db.DatabaseAccess;
import om.tnavigator.uned.axis.OmTn.TestDeployFileFilter;
import util.misc.Strings;
import util.xml.XHTML;
import util.xml.XML;
import util.xml.XMLException;
import util.xml.uned.UnedXML;

/**
 * Om test navigator; implementation of the test delivery engine.<br/><br/>
 * This is a special implementation that add a <i>Logout</i> button within navigation bar of test pages 
 * when an user has been logged in.<br/><br/>
 * It also tries to check ellapsed time between user actions to invalidate user session if have passed 
 * a lot of time.<br/><br/>
 * Moreover it is derived from <i>UnedNavigatorServlet</i> so it also loads additional information from 
 * navigator configuration file:<br/><br/>
 * <ol>
 * <li>Supports encryption of some properties.</li>
 * <li>Provides information of a second DB used for login authentication.</li>
 * <li>Improved mail configuration.</li>
 * </ol>
 */
@SuppressWarnings("serial")
public class LogoutNavigatorServlet extends UnedNavigatorServlet
{
    /**
	 * Utility class to construct a response wrapper to dispatch to the authentication system 
	 * to logout current session.
     */
	private class AuthLogoutNoRedirectResponseWrapper extends HttpServletResponseWrapper
	{
		public AuthLogoutNoRedirectResponseWrapper(HttpServletResponse response)
		{
			super(response);
		}
		
		@Override
		public void addHeader(String name,String value)
		{
		}
		
		@Override
		public void setContentType(String type)
		{
		}
		
		@Override
		public void setCharacterEncoding(String charset)
		{
		}
		
		@Override
		public PrintWriter getWriter() throws IOException
		{
			return new PrintWriter(new StringWriter());
		}
		
		@Override
		public void sendRedirect(String location) throws IOException
		{
		}
	}
	
	/**
	 * Utility class to construct a response wrapper to invoke <i>serveTestContent</i> method from parent
	 * class with a chance to modify output of that call before sending it to the client.
     */
	private class ServeTestContentResponseWrapper extends HttpServletResponseWrapper
	{
		private StringWriter testContentHTML;
		
		public ServeTestContentResponseWrapper(HttpServletResponse response)
		{
			super(response);
			testContentHTML=new StringWriter();
		}
		
		/**
		 * @return Test content output as XHTML, so we can parse, modify and send it to client safely
		 */
		public String getTestContentAsXHTML()
		{
			String finalPageHTML=testContentHTML.toString();
			StringBuffer finalPageXHTML=new StringBuffer("<xhtml>");
			finalPageXHTML.append(
				finalPageHTML.substring(finalPageHTML.indexOf("<head>"),finalPageHTML.indexOf("</body>")));
			finalPageXHTML.append("</body></xhtml>");
			return finalPageXHTML.toString();
		}
		
		@Override
		public void addHeader(String name,String value)
		{
		}

		@Override
		public void setContentType(String type)
		{
		}
		
		@Override
		public void setCharacterEncoding(String charset)
		{
		}
		
		@Override
		public PrintWriter getWriter() throws IOException
		{
			return new PrintWriter(testContentHTML);
		}
	}
	
	/**
	 * Handler for customize UNED specific options/tasks for Test Navigator.<br/><br/>
	 * For now we have been implemented the following options:<br/><br/>
	 * <table border="1">
	 * <tr><th>Option</th><th>Description</th></tr>
	 * <tr><td>/!logout-result</td><td>Displays a page indicating that the user has logged out.</td></tr>
	 * </table>
	 * @param bPost Flag indicating if servlet has been invoked by POST (true) or GET (false)
	 * @param sPath Path info
	 * @param rt Request timings
	 * @param request Servlet request
	 * @param response Servlet response
	 * @return true if a custom option has been handled (and we need to stop handling), false otherwise
	 */
	@Override
	protected boolean handleCustom(boolean bPost,String sPath,RequestTimings rt,HttpServletRequest request,
		HttpServletResponse response) throws Exception
	{
		boolean handled=false;
		String logoutResultTemplate=getLogoutResultTemplate();
		if (logoutResultTemplate!=null && sPath.startsWith("/!logout-result"))
		{
			handled=true;
			
			// Load logout result page
			Document d=XML.parse(new File(getTemplatesFolder(),logoutResultTemplate));
			String username=request.getParameter("username");
			if (username!=null)
			{
				Element usernameField=XML.find(d,"name","username");
				XML.createText(usernameField,username);
			}
			
			// Display logout result page
			XHTML.output(d,request,response,"en");
		}
		else if (bPost && sPath.startsWith("/!check-oucu/"))
		{
			handled=true;
			response.setContentType("text/plain");
			response.getWriter().println(isOUCUAvailable(sPath.substring("/!check-oucu/".length()))?"OK":"KO");
		}
		else if (bPost && sPath.startsWith("/!decrypt-gepeq-message/"))
		{
			handled=true;
			response.setContentType("text/plain");
			response.getWriter().print(((UnedNavigatorConfig)getNavigatorConfig()).decryptFromGEPEQ(
				sPath.substring("/!decrypt-gepeq-message/".length())));
		}
		if (!handled && !sPath.startsWith("/!auth/"))
		{
			if (!touch(request,response))
			{
				String requestURL=request.getRequestURI();
				String contextPath=request.getContextPath();
				StringBuffer logoutPageURL=
					new StringBuffer(requestURL.substring(0,requestURL.indexOf(contextPath)));
				logoutPageURL.append(contextPath);
				logoutPageURL.append("/!auth/logout");
				response.sendRedirect(logoutPageURL.toString());
				handled=true;
			}
		}
		return handled;
	}
	
	@Override
	public void serveTestContent(UserSession us,String sTitle,String sAuxTitle,String sTip,
		String sProgressInfo,String sXHTML,boolean bIncludeNav,HttpServletRequest request,
		HttpServletResponse response, boolean bClearCSS) throws IOException
	{
		// We check if user is logged in, because we want to include a link/button for logout in that case
		if (us.isUserLoggedIn())
		{
			// We will invoke parent method with a response wrapper because we want to modify output
			ServeTestContentResponseWrapper responseWrapper=new ServeTestContentResponseWrapper(response);
			super.serveTestContent(us,sTitle,sAuxTitle,sTip,sProgressInfo,sXHTML,bIncludeNav,request,
				responseWrapper,bClearCSS);
			
			// Parse XHTML output from the response wrapper
			Document d=XML.parse(responseWrapper.getTestContentAsXHTML());
			
			// Check if we are in plain mode or not
			String sAccessibility=getAccessibilityCookie(request);
			boolean plainMode=sAccessibility.indexOf("[plain]")!=-1;
			
			Element eLogoutLink=null;
			if (us.isSingle())
			{
				if (plainMode)
				{
					// Find body
					Element eBody=UnedXML.findByTagName(d,"body");
					
					// Find link to leave plain mode
					Element eLeavePlainMode=XML.find(eBody,"href","?plainmode");
					if (eLeavePlainMode==null)
					{
						Element[] eBodyChildren=XML.getChildren(eBody);
						if (eBodyChildren.length>0 && 
							!eBodyChildren[eBodyChildren.length-1].getTagName().equals("br"))
						{
							// Add a break line
							XML.createChild(eBody,"br");
						}
						
						// Add logout link
						eLogoutLink=XML.createChild(eBody,"a");
					}
					else
					{
						// Add logout link
						eLogoutLink=d.createElement("a");
						eBody.insertBefore(eLogoutLink,eLeavePlainMode);
						
						// Add a break line
						eBody.insertBefore(d.createElement("br"),eLeavePlainMode);
					}
				}
				else
				{
					// Find Div for the singleslinks section
					Element eSinglesLinks=XML.find(d,"class","singleslinks");
					
					Element[] eSinglesLinksChildren=XML.getChildren(eSinglesLinks);
					if (eSinglesLinksChildren.length>0 && 
						!eSinglesLinksChildren[eSinglesLinksChildren.length-1].getTagName().equals("br"))
					{
						// Add a break line
						XML.createChild(eSinglesLinks,"br");
					}
					
					// Add logout link
					eLogoutLink=XML.createChild(eSinglesLinks,"a");
				}
			}
			else
			{
				// Find Div for the buttons section
				Element eButtons=XML.find(d,"id","buttons");
				
				if (!plainMode)
				{
					Element[] eButtonsChildren=XML.getChildren(eButtons);
					if (eButtonsChildren.length>0 && 
						!eButtonsChildren[eButtonsChildren.length-1].getTagName().equals("br"))
					{
						// Add a break line
						XML.createChild(eButtons,"br");
					}
				}
				
				// Add logout button
				Element eLogoutButton=XML.createChild(eButtons,"div");
				if (!plainMode)
				{
					eLogoutButton.setAttribute("class","button");
				}
				eLogoutLink=XML.createChild(eLogoutButton,"a");
			}
			
			// Generate logout link
			StringBuffer authLogout=new StringBuffer(request.getContextPath());
			authLogout.append("/!auth/logout");
			eLogoutLink.setAttribute("href",authLogout.toString());
			XML.createText(eLogoutLink,"Logout");
			
			// Whew! Now send to user
			breakBack(request,response);
			XHTML.output(d,request,response,"en");
		}
		else
		{
			// Let parent method do its job
			super.serveTestContent(
				us,sTitle,sAuxTitle,sTip,sProgressInfo,sXHTML,bIncludeNav,request,response,bClearCSS);
		}
	}
	
	/**
	 * @return Template for logout result page
	 */
	@SuppressWarnings("rawtypes")
	protected String getLogoutResultTemplate()
	{
		String logoutResultTemplate=null;
		try
		{
			Class authClass=Class.forName(getNavigatorConfig().getAuthClass());
			Field logoutResultConstant=authClass.getField("LOGOUT_RESULT");
			if (Modifier.isStatic(logoutResultConstant.getModifiers()) && 
				Modifier.isFinal(logoutResultConstant.getModifiers()))
			{
				logoutResultTemplate=(String)logoutResultConstant.get(null);
			}
		}
		catch (Exception e)
		{
			logoutResultTemplate=null;
		}
		return logoutResultTemplate;
	}
	
	/**
	 * This method first checks if authentication plugin supports <i>touch</i> operation.<br/><br/>
	 * In that case this method check current time against last action time for current authenticated user 
	 * to validate (or invalidate) his/her user session and update it if validation is successful.<br/><br/>
	 * Allowed ellapsed time between last action time and current time depends on authentication plugin
	 * implementation (usually is about 15 minutes).
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param ns Servlet
	 * @return true if last action time for current authenticated user has been updated or if <i>touch</i>
	 * operation is not supported, false otherwise 
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	protected boolean touch(HttpServletRequest request,HttpServletResponse response)
	{
		boolean ok=true;
		try
		{
			Class authClass=auth.getClass();
			Method touchMethod=authClass.getMethod("touch",
				new Class[] {HttpServletRequest.class,HttpServletResponse.class,NavigatorServlet.class});
			if (Modifier.isStatic(touchMethod.getModifiers()))
			{
				ok=((Boolean)touchMethod.invoke(null,new Object[] {request,response,this})).booleanValue();
			}
		}
		catch (Exception e)
		{
			ok=true;
		}
		return ok;
	}
	
	/**
	 * Throw away session.<br/><br/>
	 * This method is invoked on error from <i>sendError</i> method if <i>keepSession</i> is false.<br/><br/>
	 * This implementation throws away all the current session when using <i>PreviewAuth</i> or <i>GepeqAuth</i> 
	 * authentication system, otherwise throws away user session on this test but keeps user logged in.
	 * <br/><br/>
	 * You can override this method within subclasses if you need a different behaviour.
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param sessions Map of user sessions... cookie (String) -> UserSession
	 * @param us User session to throw away
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void throwAwaySession(HttpServletRequest request,HttpServletResponse response,
		Map<String,UserSession> sessions,UserSession us)
	{
		String logoutRequest=null;
		try
		{
			Class authClass=auth.getClass();
			Field logoutRequestConstant=authClass.getField("LOGOUT_REQUEST");
			int logoutRequestConstantModifiers=logoutRequestConstant.getModifiers();
			if (Modifier.isFinal(logoutRequestConstantModifiers) && Modifier.isStatic(logoutRequestConstantModifiers))
			{
				logoutRequest=(String)logoutRequestConstant.get(null);
			}
		}
		catch (Exception e)
		{
			logoutRequest=null;
		}
		if (logoutRequest!=null)
		{
			try
			{
				auth.handleRequest(logoutRequest,request,new AuthLogoutNoRedirectResponseWrapper(response));
			}
			catch (Exception e)
			{
			}
		}
		
		// Let parent method do its job
		super.throwAwaySession(request,response,sessions,us);
	}
	
	/**
	 * @param oucu OUCU
	 * @return true if OUCU is available, false if it is used
	 */
	private boolean isOUCUAvailable(String sOUCU)
	{
		boolean available=true;
		DatabaseAccess.Transaction dat=null;
		try
		{
			dat=getDatabaseAccess().newTransaction();
			
			StringBuffer query=new StringBuffer("SELECT COUNT(*) FROM ");
			query.append(getOmQueries().getPrefix());
			query.append("tests WHERE oucu=");
			query.append(Strings.sqlQuote(sOUCU));
			ResultSet rs=dat.query(query.toString());
			rs.next();
			available=rs.getInt(1)==0;
		}
		catch (Exception e)
		{
			available=true;
		}
		finally
		{
			if (dat!=null)
			{
				dat.finish();
			}
		}
		if (available)
		{
			for (File deployFile:getTestbankFolder().listFiles(new TestDeployFileFilter()))
			{
				String publisher=null;
				Document dDeploy=null;
				try
				{
					dDeploy=XML.parse(deployFile);
				}
				catch (IOException e)
				{
				}
				if (dDeploy!=null)
				{
					Element eRoot=dDeploy.getDocumentElement();
					try
					{
						if (XML.hasChild(eRoot,"publisher"))
						{
							publisher=XML.getText(eRoot,"publisher");
						}
					}
					catch (XMLException xe)
					{
					}
				}
				if (sOUCU.equals(publisher))
				{
					available=false;
					break;
				}
			}
		}
		return available;
	}
}
