package om.tnavigator.uned;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import om.tnavigator.Log;
import om.tnavigator.Mail;
import om.tnavigator.NavigatorConfig;
import om.tnavigator.NavigatorServlet;
import om.tnavigator.TestDefinition;
import om.tnavigator.UserSession;
import om.tnavigator.auth.UserDetails;
import om.tnavigator.auth.uned.PreviewUser;
import om.tnavigator.db.DatabaseAccess;
import om.tnavigator.db.DatabaseAccess.Transaction;
import om.tnavigator.scores.CombinedScore;

import org.w3c.dom.Element;

import util.misc.IO;
import util.misc.Strings;
import util.xml.XML;

/**
 * Om test navigator; implementation of the test delivery engine.<br/><br/>
 * This is an implementation for UNED that loads additional information from navigator configuration file:
 * <br/><br/>
 * <ol>
 * <li>Supports encryption of some properties.</li>
 * <li>Provides information of a second DB used for login authentication.</li>
 * <li>Improved mail configuration.</li>
 * </ol>
 */
@SuppressWarnings("serial")
public class UnedNavigatorServlet extends NavigatorServlet
{
	/** Thread that hangs around for a bit then sends support contacts alert messages */
	private class SupportContactsAlertLater extends Thread
	{
		SupportContactsAlertLater()
		{
			start();
		}
		
		@Override
		public void run()
		{
			try
			{
				Thread.sleep(SUPPORT_CONTACTS_ALERT_DELAY);
			}
			catch(InterruptedException e)
			{
			}
			sendSupportContactsAlertNow();
		}
	}
	
	private final static DateFormat DATE_FORMAT=new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
	
	/** Synch object for support contact alerts */
	private Object supportContactsAlertSynch=new Object();
	
	/** 
	 * Map of buffers holding text of next support contacts alerts to send 
	 * (a single buffer for every support contact),
	 * null if thread not running
	 */
	private Map<String,StringBuffer> supportContactsAlerts=null;
	
	/** Time last alert was sent at */
	private long lastSupportContactsAlert=0;
	
	/** Delay per which support contacts alert emails are sent (4 hours) */
	private static final int SUPPORT_CONTACTS_ALERT_DELAY=4*60*60*1000;
	
	/** Login database access */
	private DatabaseAccess lda;
	
	/** @return the DatabaseAccess object used by this servlet to access login DB. */
	public DatabaseAccess getLoginDatabaseAccess()
	{
		return lda;
	}
	
	@Override
	public void init() throws ServletException
	{
		// Set default time zone for date formatting within log files.
		Log.DATEFORMAT.setTimeZone(TimeZone.getDefault());
		Log.DATETIMEFORMAT.setTimeZone(TimeZone.getDefault());
		Log.TIMEFORMAT.setTimeZone(TimeZone.getDefault());
		
		// Let parent method do its job
		super.init();
		
		// Initialize access pool to login DB
		try
		{
			lda=new DatabaseAccess(((UnedNavigatorConfig)getNavigatorConfig()).getLoginDatabaseURL(getOmQueries()),
				getNavigatorConfig().hasDebugFlag("log-sql")?l:null);
		}
		catch (Exception e)
		{
			StringBuffer error=new StringBuffer(
				"Error creating login database class or JDBC driver (make sure DB plugin and JDBC driver are both installed): ");
			error.append(e.getMessage());
			throw new ServletException(error.toString());
		}
	}

	@Override
	protected NavigatorConfig initializeNavigatorConfig(ServletContext sc) throws IOException
	{
		return new UnedNavigatorConfig(sc,new File(sc.getRealPath("navigator.xml")));
	}
	
	@Override
	protected UserSession createUserSession(String sCookie)
	{
		return new UnedUserSession(this,sCookie);
	}
	
	/**
	 * Sends the given mail message with a given MIME format.<br/><br/>
	 * This implementation uses <i>om.tnavigator.uned.UnedMail</i> class to send mail.<br/><br/>
	 * You can override this method within subclasses if you need to use a different implementation 
	 * for sending mails.
	 * @param from Sender address
	 * @param replyTo Reply-to address
	 * @param to Array of target addresses - or null
	 * @param cc Array of CC addresses - or null
	 * @param coo Array of CCO addresses - or null
	 * @param subject Subject
	 * @param message Message
	 * @param mimeType Mime-type of the message; if null, sends text/plain.
	 * @throws MessagingException If any failure occurs in contacting mail server, etc.
	 */
	@Override
	protected void sendMail(String from,String replyTo,String[] to,String[] cc,String[] cco,String subject,
		String message,String mimeType) throws MessagingException
	{
			UnedMail.send(from,replyTo,to,cc,cco,subject,message,mimeType);
	}

	@Override
	protected void handleEndSubmitEmail(RequestTimings rt,UserSession us,UserDetails ud,TestDefinition td,
		int ti,Element eFinal,String sOUCU) throws Exception
	{
		Map<UnedUserFilter,StringBuffer> evaluatorsMap=
			((UnedTestDeployment)us.getTestDeployment()).getEvaluatorsMap();
		if (evaluatorsMap!=null)
		{
			List<String> evaluators=new ArrayList<String>();
			StringBuffer sbEvaluators=new StringBuffer();
			for (Entry<UnedUserFilter,StringBuffer> evaluatorEntry:evaluatorsMap.entrySet())
			{
				if (evaluatorEntry.getKey().accept(sOUCU))
				{
					for (String evaluator:evaluatorEntry.getValue().toString().split(","))
					{
						if (!evaluators.contains(evaluator))
						{
							evaluators.add(evaluator);
							if (sbEvaluators.length()>0)
							{
								sbEvaluators.append(',');
							}
							sbEvaluators.append(evaluator);
						}
					}
				}
			}
			if (!evaluators.isEmpty())
			{
				String[] subjectAndMessage=getTestResultMessage(rt,us,ud,td,ti,eFinal).split("\n",2);
				if(subjectAndMessage.length!=2)
				{
					throw new IOException(
						"Email content must begin with a one-line subject then contain additional content");
				}
				String alertMailfrom=getNavigatorConfig().getAlertMailFrom();
				String[] to=null;
				String[] cco=null;
				if (evaluators.size()==1)
				{
					// If we send mail only to one evaluator we don't need to hide his/her mail address
					to=new String[] {evaluators.get(0)};
				}
				else
				{
					// If we send mail to several evaluators we hide their mail addresses
					cco=evaluators.toArray(new String[evaluators.size()]);
				}
				try
				{
					sendMail(alertMailfrom,null,to,null,cco,subjectAndMessage[0],subjectAndMessage[1],null);
					StringBuffer logEmailSent=new StringBuffer("Sent submit test results email to ");
					logEmailSent.append(sbEvaluators);
					logEmailSent.append(" for ");
					logEmailSent.append(us.getTestId());
					logEmailSent.append(" test results (OUCU: ");
					logEmailSent.append(sOUCU);
					logEmailSent.append(')');
					getLog().logNormal(logEmailSent.toString());
				}
				catch (MessagingException me)
				{
					StringBuffer logEmailSentError=new StringBuffer("Error sending submit test results email to ");
					logEmailSentError.append(sbEvaluators);
					logEmailSentError.append(" for ");
					logEmailSentError.append(us.getTestId());
					logEmailSentError.append(" test results (OUCU: ");
					logEmailSentError.append(sOUCU);
					logEmailSentError.append(')');
					getLog().logError(logEmailSentError.toString(),me);
				}
			}
		}
		
		// Finally we allow parent to handle sending of confirmation email to user if required 
		super.handleEndSubmitEmail(rt,us,ud,td,ti,eFinal,sOUCU);
	}
	
	/**
	 * @param rt Timings
	 * @param us User session
	 * @param ud User details
	 * @param td Test definition
	 * @param ti Database ID for test
	 * @param eFinal XML &lt;final&gt; element from test.xml for this test
	 * @return Text for subject and message to send as mail to support contacts as result of test submitted 
	 * by user
	 * @throws Exception
	 */
	private String getTestResultMessage(RequestTimings rt,UserSession us,UserDetails ud,TestDefinition td,
		int ti,Element eFinal) throws Exception
	{
		StringBuffer testResultMessage=new StringBuffer();
		testResultMessage=new StringBuffer(IO.loadString(
			new FileInputStream(getServletContext().getRealPath("WEB-INF/templates/test.result.email.txt"))));
		
		// Get username from GEPEQ database
		String oucu=ud.getUsername();
		StringBuffer username=null;
		DatabaseAccess gepeqDB=null;
		Transaction dat=null;
		UnedNavigatorConfig nc=(UnedNavigatorConfig)getNavigatorConfig();
		try
		{
			gepeqDB=new DatabaseAccess(nc.getLoginDatabaseURL(getOmQueries()),null);
			dat=gepeqDB.newTransaction();
			StringBuffer query=new StringBuffer("SELECT login,name,surname FROM users WHERE oucu=");
			query.append(Strings.sqlQuote(oucu));
			ResultSet rs=dat.query(query.toString());
			if (rs.next())
			{
				String name=rs.getString(2);
				if (name==null || "".equals(name))
				{
					username=new StringBuffer(rs.getString(1));
				}
				else
				{
					username=new StringBuffer(name);
					String surname=rs.getString(3);
					if (surname!=null && !surname.equals(""))
					{
						username.append(' ');
						username.append(surname);
					}
				}
			}
			else
			{
				username=new StringBuffer(oucu);
			}
		}
		catch(Exception e)
		{
			StringBuffer errorMessage=new StringBuffer(
				"Error creating database class or JDBC driver (make sure DB plugin and JDBC driver are both installed): ");
			errorMessage.append(e.getMessage());
			throw new SQLException(errorMessage.toString(),e);
		}
		finally
		{
			if (dat!=null)
			{
				dat.finish();
			}
			gepeqDB.close();
		}
		
		Map<String,String> mReplace=new HashMap<String,String>();
		mReplace.put("TEST_NAME",td.getName());
		mReplace.put("USER_NAME",username.toString());
		mReplace.put("TIME",formatDate(new Date()));
		
		StringBuffer testReport=new StringBuffer();
		if (ud instanceof PreviewUser)
		{
			testReport.append(
				"*** CAREFUL. This test result has been generated from a test environment for testing purposes. ***\n\n");
		}
		try
		{
			dat=getDatabaseAccess().newTransaction();
			
			ResultSet rs=queryUserReportSubmittedSession(dat,ti);
			if (rs.next())
			{
				int iTAttempt=rs.getInt(1);
				Timestamp tsDate=rs.getTimestamp(2);				
				String sIP=rs.getString(3);
				String sUserAgent=rs.getString(4);
				
				testReport.append("Time: ");
				testReport.append(formatDate(tsDate));
				testReport.append("\nIP address: ");
				testReport.append(sIP);
				testReport.append("\nUser agent: ");
				testReport.append(sUserAgent);
				
				rs=getOmQueries().queryUserReportTest(dat,us.getTestId(),oucu);
				int iCurrentQAttempt=0;
				while (rs.next())
				{
					int iTAttemptR=rs.getInt(1);
					int iFinished=rs.getInt(2);
					if (iFinished>0 && iTAttempt==iTAttemptR)
					{
						String sQuestion=rs.getString(3);
						Timestamp tsDateR=rs.getTimestamp(5);
						String questionSummary=rs.getString(6);
						if (questionSummary==null)
						{
							questionSummary="The question did not return this information.";
						}
						String answerSummary=rs.getString(7);
						if (answerSummary==null)
						{
							answerSummary="The question did not return this information.";
						}
						String actionSummary=rs.getString(8);
						if (actionSummary==null)
						{
							actionSummary="The question did not return this information.";
						}
						String attemptString=NavigatorServlet.getAttemptsString(rs.getInt(9));
						if (rs.wasNull())
						{
							attemptString="The question did not return this information.";
						}
						String sAxis=rs.getString(10);
						String sAxisScore=rs.getString(11);
						int iQNumber=rs.getInt(12);
						Timestamp tsMinAction=rs.getTimestamp(14);
						Timestamp tsMaxAction=rs.getTimestamp(15);
						testReport.append("\n\n#");
						testReport.append(iQNumber);
						testReport.append(" (");
						testReport.append(sQuestion);
						testReport.append(") attempt ");
						testReport.append(iCurrentQAttempt);
						testReport.append("\nAccess time: ");
						testReport.append(formatDate(tsDateR));
						testReport.append("\nAction times: ");
						testReport.append(formatDate(tsMinAction));
						testReport.append(" - ");
						testReport.append(formatDate(tsMaxAction));
						testReport.append("\nQuestion: ");
						testReport.append(questionSummary);
						testReport.append("\nUser's answer: ");
						testReport.append(answerSummary);
						testReport.append("\nSummary:\n");
						testReport.append(actionSummary);
						testReport.append("\nResult: ");
						testReport.append(attemptString);
						testReport.append("\nScores: ");
						if (sAxis!=null)
						{
							testReport.append(sAxis.equals("")?"Default":sAxis);
							testReport.append(": ");
							testReport.append(sAxisScore);
						}
					}
				}
				rs.close();
			}
		}
		finally
		{
			dat.finish();
		}
		
		// Process scores and append them to mail if needed  
		Element eScores=XML.getChild(eFinal,"scores");
		if (eScores!=null)
		{
			// Get axis labels
			Element[] eAxisArr=XML.getChildren(eScores,"axislabel");
			
			// Get scores
			CombinedScore scores=us.getTestRealisation().getScore(rt,this,getDatabaseAccess(),getOmQueries());
			
			// Loop through each score axis
			boolean writeScoresLabel=true;
			for (String axis:scores.getAxesOrdered())
			{
				boolean writeScore=true;
				
				// Get default label - use the axis name, capitalised
				StringBuffer label=new StringBuffer();
				if (axis!=null && !axis.equals(""))
				{
					label.append(axis.substring(0,1).toUpperCase());
					if (axis.length()>1)
					{
						label.append(axis.substring(1));
					}
				}
				
				// Get axis label for this axis if provided
				for (Element eAxis:eAxisArr)
				{
					if ((axis==null && !eAxis.hasAttribute("axis")) || 
						(axis!=null & axis.equals(eAxis.getAttribute("axis"))))
					{
						// If axis label is mark as hidden we don't write this score
						writeScore=!"yes".equals(eAxis.getAttribute("hide"));
						if (writeScore)
						{
							// We replace default label with text from axis label
							label.setLength(0);
							label.append(XML.getText(eAxis));
						}
						break;
					}
				}
				
				if (writeScore)
				{
					// If this is the first not hidden score we need first to write a label for scores
					if (writeScoresLabel)
					{
						testReport.append("\n\nOverall score:\n");
						writeScoresLabel=false;
					}
					
					int iScore=(int)Math.round(scores.getScore(axis));
					int iMax=(int)Math.round(scores.getMax(axis));
					int iPercentage=(int)Math.round(100.0*iScore/iMax);
					
					// Note that marks and percentage by axis are allways sent in mail reports, 
					// even if they are disabled for feedback to user 
					testReport.append('\n');
					if (label.length()>0)
					{
						testReport.append(label);
						testReport.append(' ');
					}
					testReport.append(iScore);
					testReport.append(" (out of ");
					testReport.append(iMax);
					testReport.append(") ");
					testReport.append(iPercentage);
					testReport.append('%');
				}
			}
		}
		
		mReplace.put("TEST_REPORT",testReport.toString());
		return XML.replaceTokens(testResultMessage.toString(),"%%",mReplace);
	}
	
	/**
	 * @param date Date
	 * @return String representation of this date
	 */
	private String formatDate(Date date)
	{
		if (date==null)
		{
			return "[date not available]";
		}
		return DATE_FORMAT.format(date);
	}
	
	/**
	 * @param date Date (as a timestamp)
	 * @return String representation of this date
	 */
	private String formatDate(Timestamp date)
	{
		if (date==null)
		{
			return "[date not available]";
		}
		return DATE_FORMAT.format(date);
	}
	
	/**
	 * Get user session where the indicated test has been submitted.
	 * @param dat the transaction within which the query should be executed.
	 * @param ti Database ID for test
	 * @return the requested data.
	 * @throws SQLException
	 */
	private ResultSet queryUserReportSubmittedSession(Transaction dat,int ti) throws SQLException
	{
		StringBuffer query=new StringBuffer("SELECT t.attempt,si.clock,si.ip,si.useragent FROM ");
		query.append(getOmQueries().getPrefix());
		query.append("tests t INNER JOIN ");
		query.append(getOmQueries().getPrefix());
		query.append("sessioninfo si ON t.ti=si.ti WHERE t.ti=");
		query.append(ti);
		return dat.query(query.toString());
	}
	
	@Override
	public void sendError(UserSession us,HttpServletRequest request,HttpServletResponse response,int code,
		boolean isBug,boolean keepSession, String backToTest,String title,String message,Throwable exception) 
		throws StopException
	{
		StopException stopException=null;
		try
		{
			// Let parent method do its job
			super.sendError(us,request,response,code,isBug,keepSession,backToTest,title,message,exception);
		}
		catch (StopException se)
		{
			stopException=se;
		}
		
		// If error is a bug output is already send by email to support contacts
		if (us!=null && isBug)
		{
			Map<String,String> m=new HashMap<String,String>();
			m.put("STATUSCODE",Integer.toString(code));
			m.put("TITLE",title);
			m.put("MESSAGE",message);
			StringBuffer sbRequest=new StringBuffer();
			String sPath=request.getPathInfo();
			if (sPath!=null)
			{
				sbRequest.append(sPath);
			}
			String sQueryString=request.getQueryString();
			if (sQueryString!=null)
			{
				sbRequest.append('?');
				sbRequest.append(sQueryString);
			}
			m.put("REQUEST",sbRequest.toString());
			String sOUCU=auth.getUncheckedUserDetails(request).getUsername();
			m.put("OUCU",sOUCU==null?"[not logged in]":sOUCU);
			StringBuffer sbQEngine=new StringBuffer();
			if (us.getOmServiceSession()!=null)
			{
				sbQEngine.append(displayServletURL(us.getOmServiceSession().getEngineURL()));
				sbQEngine.append(" (");
				sbQEngine.append(us.getOmServiceSession().getQuestionID());
				sbQEngine.append('.');
				sbQEngine.append(us.getOmServiceSession().getQuestionVersion());
				sbQEngine.append(')');
			}
			else
			{
				sbQEngine.append("n/a");
			}
			m.put("QENGINE",sbQEngine.toString());
			String sAccess=getAccessibilityCookie(request);
			m.put("ACCESS","".equals(sAccess)?"n/a":sAccess);
			m.put("TINDEX",Integer.toString(us.getTestPosition()));
			m.put("TSEQ",Integer.toString(us.getDBSeq()));
			String sMessageSummary="??";
			try
			{
				sMessageSummary=IO.loadString(
					new FileInputStream(getServletContext().getRealPath("WEB-INF/templates/errortemplate.txt")));
			}
			catch (IOException ioe)
			{
				// Ignore
			}
			sMessageSummary=XML.replaceTokens(sMessageSummary,"%%",m);
			
			// Send email to support contacts - output is already sent to user so no need to worry if it takes a while
			sendSupportContactsAlert(us,sOUCU,sMessageSummary,exception);
		}
		throw stopException==null?new StopException():stopException;
	}
	
	/**
	 * Sends support contacts alert message by email.<br/><br/>
	 * Messages are automatically queued so it doesn't send more than one per SUPPORT_CONTACTS_ALERT_DELAY.<br/><br/>
	 * Time is automatically recorded.
	 * @param us User session
	 * @param sOUCU User's OUCU
	 * @param sMessage Message text; will be included in one line after the date.
	 * Can include a multi-line message with \n if necessary
	 * @param tException Exception to trace (optional, null if none)
	 */
	private void sendSupportContactsAlert(UserSession us,String sOUCU,String sMessage,Throwable tException)
	{
		Map<UnedUserFilter, StringBuffer> supportContactsMap=
			((UnedTestDeployment)us.getTestDeployment()).getSupportContactsMap();
		if (supportContactsMap!=null)
		{
			List<String> supportContacts=new ArrayList<String>();
			for (Entry<UnedUserFilter,StringBuffer> supportContactEntry:supportContactsMap.entrySet())
			{
				try
				{
					if (supportContactEntry.getKey().accept(sOUCU))
					{
						for (String supportContact:supportContactEntry.getValue().toString().split(","))
						{
							if (!supportContacts.contains(supportContact))
							{
								supportContacts.add(supportContact);
							}
						}
					}
				}
				catch (IOException ioe)
				{
					// Ignore error... we still try sending mails to other support contacts accepted without errors
				}
			}
			
			// We only send mails if someone is receiving them!
			if (!supportContacts.isEmpty())
			{
				synchronized (supportContactsAlertSynch)
				{
					boolean bNew=false;
					if(supportContactsAlerts==null)
					{
						supportContactsAlerts=new HashMap<String,StringBuffer>();
						bNew=true;
					}
					
					StringBuffer alert=new StringBuffer();
					alert.append("----------------------------------------------------------------------\n");
					alert.append(Log.DATETIMEFORMAT.format(new Date()));
					alert.append('\n');
					alert.append(sMessage);
					alert.append("\n\n");
					if (tException!=null)
					{
						alert.append(Log.getOmExceptionString(tException));
						alert.append("\n\n");
					}
					
					for (String supportContact:supportContacts)
					{
						StringBuffer supportContactAlerts=supportContactsAlerts.get(supportContact);
						if (supportContactAlerts==null)
						{
							supportContactsAlerts.put(supportContact,alert);
						}
						else
						{
							supportContactAlerts.append(alert);
						}
					}
					
					if (bNew && (System.currentTimeMillis()-lastSupportContactsAlert)>SUPPORT_CONTACTS_ALERT_DELAY)
					{
						sendSupportContactsAlertNow();
					}
					else if (bNew)
					{
						new SupportContactsAlertLater();
					}
				}
			}
		}
	}
	
	/** Sends the actual email */
	private void sendSupportContactsAlertNow()
	{
		synchronized (supportContactsAlertSynch)
		{
			lastSupportContactsAlert=System.currentTimeMillis();
			
			StringBuffer alertWarning=new StringBuffer();
			alertWarning.append("======================================================================\n");
			alertWarning.append("Next email will not be sent until ");
			alertWarning.append(
				Log.TIMEFORMAT.format(new Date(lastSupportContactsAlert+SUPPORT_CONTACTS_ALERT_DELAY)));
			alertWarning.append('\n');
			
			// Send mails now
			for (Entry<String,StringBuffer> supportContactAlertsEntry:supportContactsAlerts.entrySet())
			{
				String[] to=new String[] {supportContactAlertsEntry.getKey()};
				StringBuffer supportContactAlerts=supportContactAlertsEntry.getValue();
				supportContactAlerts.append(alertWarning);
				StringBuffer subject=new StringBuffer("Om alert: ");
				subject.append(getFriendlyServerName());
				try
				{
					sendMail(getNavigatorConfig().getAlertMailFrom(),null,to,null,null,subject.toString(),
						supportContactAlerts.toString(),Mail.TEXTPLAIN);
				}
				catch (MessagingException me)
				{
					l.logError("Support contacts alerts","Failed to send message",me);
				}
			}
			
			// Clear all support contacts alerts
			supportContactsAlerts=null;
		}
	}
	
	/**
	 * Throw away session.<br/><br/>
	 * This method is invoked on error from <i>sendError</i> method if <i>keepSession</i> is false.<br/><br/>
	 * This implementation stops the question session before throwing away session.<br/><br/>
	 * You can override this method within subclasses if you need a different behaviour.
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param sessions Map of user sessions... cookie (String) -> UserSession
	 * @param us User session to throw away
	 */
	@Override
	protected void throwAwaySession(HttpServletRequest request,HttpServletResponse response,
		Map<String,UserSession> sessions,UserSession us)
	{
		// Stops question session
		try
		{
			stopQuestionSession(new RequestTimings(),us);
		}
		catch (Exception e)
		{
		}
		
		// Let parent method do its job
		super.throwAwaySession(request,response,sessions,us);
	}
}
