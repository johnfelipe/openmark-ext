package om.tnavigator.uned.axis;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import om.OmUnexpectedException;
import om.tnavigator.uned.UnedUserFilter;

import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import org.apache.axis.transport.http.HTTPConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.xml.XML;
import util.xml.XMLException;

/**
 * Implementation of web service for Om Test Navigator Web Application.
 */
public class OmTn implements IOmTn
{
	/** Initial date-time pattern match for validation */
	private static final Pattern DATETIME=Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}(?: ([0-9:]+))?$");
	/** Matches time including seconds */
	private static final Pattern FULLTIME=Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}$");
	/** Matches time without seconds */
	private static final Pattern PARTTIME=Pattern.compile("^[0-9]{2}:[0-9]{2}$");
	
    /**
	 * Utility class to construct a request to dispatch to Om Navigator Servlet from this web service
	 * to stop all active Test Navigator sessions that are using a question.
     */
	private class StopAllSessionsForQuestionRequest extends HttpServletRequestWrapper
	{
		private String packageName;
		
		public StopAllSessionsForQuestionRequest(HttpServletRequest request,String packageName)
		{
			super(request);
			this.packageName=packageName;
		}
		
		@Override
		public String getPathInfo()
		{
			StringBuffer sStopAllSessionsForQuestionPathInfo=new StringBuffer("/!stopallsessionsforquestion/");
			sStopAllSessionsForQuestionPathInfo.append(packageName);
			return sStopAllSessionsForQuestionPathInfo.toString();
		}
		
		@Override
		public String getRequestURI()
		{
			return getTnServletRequestURI();
		}
		
		@Override
		public String getQueryString()
		{
			return null;
		}
		
		@Override
		public void setCharacterEncoding(String enc)
		{
		}
	}
	
    /**
	 * Utility class to construct a response to dispatch to Om Navigator Servlet from this web service
	 * to stop all active Test Navigator sessions that are using a question.
     */
	private class StopAllSessionsForQuestionResponse extends HttpServletResponseWrapper
	{
		public StopAllSessionsForQuestionResponse(HttpServletResponse response)
		{
			super(response);
		}
		
		@Override
		public void addHeader(String name,String value)
		{
		}
		
		@Override
		public void setStatus(int sc)
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
	}
	
    /**
	 * Utility class to construct a request to dispatch to Om Navigator Servlet from this web service
	 * to check if an OUCU is available or not.
     */
	private class CheckOucuRequest extends HttpServletRequestWrapper
	{
		private String oucu;
		
		public CheckOucuRequest(HttpServletRequest request,String oucu)
		{
			super(request);
			this.oucu=oucu;
		}
		
		@Override
		public String getPathInfo()
		{
			StringBuffer sCheckOucuPathInfo=new StringBuffer("/!check-oucu/");
			sCheckOucuPathInfo.append(oucu);
			return sCheckOucuPathInfo.toString();
		}
		
		@Override
		public String getRequestURI()
		{
			return getTnServletRequestURI();
		}
		
		@Override
		public String getQueryString()
		{
			return null;
		}
		
		@Override
		public void setCharacterEncoding(String enc)
		{
		}
	}
	
    /**
	 * Utility class to construct a response to dispatch to Om Navigator Servlet from this web service
	 * to check if an OUCU is available or not.
     */
	private class CheckOucuResponse extends HttpServletResponseWrapper
	{
		private StringWriter checkString;
		
		public CheckOucuResponse(HttpServletResponse response)
		{
			super(response);
			checkString=new StringWriter();
		}
		
		public boolean isOUCUAvailable()
		{
			return checkString.toString().contains("OK");
		}
		
		@Override
		public void setStatus(int sc)
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
			return new PrintWriter(checkString);
		}
	}
	
    /**
	 * Utility class to construct a request to dispatch to Om Navigator Servlet from this web service
	 * to decrypt a message received from GEPEQ.
     */
	private class DecryptGepeqMessageRequest extends HttpServletRequestWrapper
	{
		private String message;
		
		public DecryptGepeqMessageRequest(HttpServletRequest request,String message)
		{
			super(request);
			this.message=message;
		}
		
		@Override
		public String getPathInfo()
		{
			StringBuffer sDecryptGepeqMessagePathInfo=new StringBuffer("/!decrypt-gepeq-message/");
			sDecryptGepeqMessagePathInfo.append(message);
			return sDecryptGepeqMessagePathInfo.toString();
		}
		
		@Override
		public String getRequestURI()
		{
			return getTnServletRequestURI();
		}
		
		@Override
		public String getQueryString()
		{
			return null;
		}
		
		@Override
		public void setCharacterEncoding(String enc)
		{
		}
	}
	
    /**
	 * Utility class to construct a response to dispatch to Om Navigator Servlet from this web service
	 * to decrypt a message received from GEPEQ.
     */
	private class DecryptGepeqMessageResponse extends HttpServletResponseWrapper
	{
		private StringWriter decryptedMessage;
		
		public DecryptGepeqMessageResponse(HttpServletResponse response)
		{
			super(response);
			decryptedMessage=new StringWriter();
		}
		
		public String getDecryptedMessage()
		{
			return decryptedMessage.toString();
		}
		
		@Override
		public void setStatus(int sc)
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
			return new PrintWriter(decryptedMessage);
		}
	}
	
	/**
	 * Utility class to filter deploy files by test name.
	 */
	public static class TestDeployFileFilter implements FileFilter
	{
		private String testName;
		
		public TestDeployFileFilter()
		{
			testName=null;
		}
		
		public TestDeployFileFilter(String testName)
		{
			this.testName=testName;
		}
		
		@Override
		public boolean accept(File pathname)
		{
			boolean ok=pathname.isFile() && pathname.canRead() && pathname.getName().endsWith(".deploy.xml");
			if (ok && testName!=null)
			{
				ok=pathname.getName().startsWith(testName);
			}
			return ok;
		}
	}
	
	private class QuestionJarFileFilter implements FileFilter
	{
		private String packageName;
		
		public QuestionJarFileFilter(String packageName)
		{
			this.packageName=packageName;
		}
		
		@Override
		public boolean accept(File pathname)
		{
			return pathname.isFile() && pathname.canRead() && pathname.getName().startsWith(packageName) &&
				pathname.getName().endsWith(".jar");
		}
	}
	
	@Override
	public boolean existTestXmls(String testName,int version) throws RemoteException
	{
		return (version==0 && getTestXmlFile(testName).exists()) || 
			getDeployXmlFile(getTestNameWithVersion(testName,version)).exists();
	}
	
	@Override
	public boolean existQuestionXml(String packageName) throws RemoteException
	{
		return getDeployXmlFile(packageName).exists();
	}
	
	@Override
	public boolean existQuestionJar(String packageName,String version) throws RemoteException
	{
		return getQuestionJarFile(packageName,version).exists();
	}
	
	@Override
	public long getQuestionJarLastModified(String packageName,String version) throws RemoteException
	{
		return getQuestionJarFile(packageName,version).lastModified();
	}
	
	@Override
	public long getTestXmlLastModified(String testName) throws RemoteException
	{
		return getTestXmlFile(testName).lastModified();
	}
	
	@Override
	public long getDeployXmlLastModified(String testName,int version) throws RemoteException
	{
		return getDeployXmlFile(getTestNameWithVersion(testName,version)).lastModified();
	}
	
	@Override
	public boolean uploadTestXml(String testName,String[] base64TestXml) throws RemoteException
	{
		boolean ok=false;
		testName=decryptGepeqMessage(testName);
		if (checkTestName(testName))
		{
			ok=writeFileFromBase64Array(getTestXmlFile(testName),base64TestXml);
		}
		return ok; 
	}
	
	@Override
	public boolean uploadDeployXml(String name,int version,String[] base64DeployXml) throws RemoteException
	{
		boolean ok=false;
		name=decryptGepeqMessage(name);
		if (checkTestName(name) || (version==0 && checkPackageName(name) ))
		{
			ok=writeFileFromBase64Array(getDeployXmlFile(getTestNameWithVersion(name,version)),base64DeployXml);
		}
		return ok;
	}
	
	@Override
	public boolean uploadQuestionJar(String packageName,String version,String[] base64QuestionJar) 
		throws RemoteException
	{
		File questionJarFile=null;
		packageName=decryptGepeqMessage(packageName);
		if (checkPackageName(packageName))
		{
			questionJarFile=getQuestionJarFile(packageName,version);
			if (version==null && questionJarFile.exists())
			{
				// We need to upload a new version, so we must find the next free version number
				StringBuffer questionJarPathPrefix=new StringBuffer();
				questionJarPathPrefix.append("questionbank");
				questionJarPathPrefix.append(File.separatorChar);
				questionJarPathPrefix.append(packageName);
				String lastVersion=questionJarFile.getPath().substring(
					questionJarFile.getPath().indexOf(questionJarPathPrefix.toString())+questionJarPathPrefix.length(),
					questionJarFile.getPath().length()-".jar".length());
				if (lastVersion.length()==".d.d".length() && lastVersion.charAt(0)=='.' && 
					Character.isDigit(lastVersion.charAt(1)) && lastVersion.charAt(2)=='.' && 
					Character.isDigit(lastVersion.charAt(3)))
				{
					int majorVersion=lastVersion.charAt(1)-'0';
					int minorVersion=lastVersion.charAt(3)-'0';
					if (minorVersion==9)
					{
						minorVersion=0;
						if (majorVersion<9)
						{
							majorVersion++;
						}
						else
						{
							majorVersion=0;
						}
					}
					else
					{
						minorVersion++;
					}
					if (majorVersion>0)
					{
						StringBuffer newVersion=new StringBuffer();
						newVersion.append(majorVersion);
						newVersion.append('.');
						newVersion.append(minorVersion);
						version=newVersion.toString();
					}
				}
				if (version==null)
				{
					questionJarFile=null;
				}
				else
				{
					questionJarFile=getQuestionJarFile(packageName,version);
				}
			}
		}
		return questionJarFile==null?false:writeFileFromBase64Array(questionJarFile,base64QuestionJar);
	}
	
	@Override
	public void deleteTestXmls(String testName,int version) throws RemoteException
	{
		testName=decryptGepeqMessage(testName);
		if (checkTestName(testName))
		{
			File testXml=getTestXmlFile(testName);
			if (testXml.exists())
			{
				testXml.delete();
			}
			File deployXml=getDeployXmlFile(getTestNameWithVersion(testName,version));
			if (deployXml.exists())
			{
				deployXml.delete();
			}
		}
	}
	
	@Override
	public void deleteQuestionXml(String packageName) throws RemoteException
	{
		packageName=decryptGepeqMessage(packageName);
		if (checkPackageName(packageName))
		{
			File deployXml=getDeployXmlFile(packageName);
			if (deployXml.exists())
			{
				deployXml.delete();
			}
		}
	}
	
	@Override
	public void deleteQuestionJar(String packageName) throws RemoteException
	{
		packageName=decryptGepeqMessage(packageName);
		if (checkPackageName(packageName))
		{
			File questionJar=getQuestionJarFile(packageName,"1.0");
			if (questionJar.exists())
			{
				questionJar.delete();
			}
		}
	}
	
	@Override
	public void stopAllSessionsForQuestion(String packageName) throws RemoteException
	{
		StopAllSessionsForQuestionRequest stopAllSessionsForQuestionRequest=
			new StopAllSessionsForQuestionRequest(getRequest(),packageName);
		StopAllSessionsForQuestionResponse stopAllSessionsForQuestionResponse=
			new StopAllSessionsForQuestionResponse(getResponse());
		try
		{
			getServletContext().getRequestDispatcher(getTnServletRequestURI()).include(
				stopAllSessionsForQuestionRequest,stopAllSessionsForQuestionResponse);
		}
		catch (Exception e)
		{
			throw new OmUnexpectedException("Unexpected error invoking om-tn servlet",e);
		}
	}
	
	@Override
	public boolean isOUCUAvailable(String oucu) throws RemoteException
	{
		CheckOucuRequest checkOucuRequest=new CheckOucuRequest(getRequest(),oucu);
		CheckOucuResponse checkOucuResponse=new CheckOucuResponse(getResponse());
		try
		{
			getServletContext().getRequestDispatcher(getTnServletRequestURI()).include(
				checkOucuRequest,checkOucuResponse);
		}
		catch (Exception e)
		{
			throw new OmUnexpectedException("Unexpected error invoking om-tn servlet",e);
		}
		return checkOucuResponse.isOUCUAvailable();
	}
	
	@Override
	public String getQuestionsReleasesMetadata() throws RemoteException
	{
		StringBuffer questionsReleasesMetadata=new StringBuffer();
		for (File deployFile:getTestbankFolder().listFiles(new TestDeployFileFilter()))
		{
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
					String packageName=XML.getText(eRoot,"question");
					if (!checkPackageName(packageName))
					{
						throw new XMLException("Package name not recognized.");
					}
					String publisher="";
					if (XML.hasChild(eRoot,"publisher"))
					{
						publisher=XML.getText(eRoot,"publisher");
					}
					DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String releaseDate="";
					if (XML.hasChild(eRoot,"release-date"))
					{
						releaseDate=XML.getText(eRoot,"release-date");
						Date dReleaseDate=getDate(releaseDate);
						if (dReleaseDate==null)
						{
							throw new XMLException("Release date invalid.");
						}
						else
						{
							releaseDate=dateFormat.format(dReleaseDate);
						}
					}
					String deleteDate="";
					if (XML.hasChild(eRoot,"delete-date"))
					{
						deleteDate=XML.getText(eRoot,"delete-date");
						Date dDeleteDate=getDate(deleteDate);
						if (dDeleteDate==null)
						{
							throw new XMLException("Delete date invalid.");
						}
						else
						{
							deleteDate=dateFormat.format(dDeleteDate);
						}
					}
					Element eDates=XML.getChild(eRoot,"dates");
					String startDate=XML.getText(eDates,"open");
					if ("yes".equals(startDate))
					{
						startDate="";
					}
					else
					{
						Date dStartDate=getDate(startDate);
						if (dStartDate==null)
						{
							throw new XMLException("Start date invalid.");
						}
						else
						{
							startDate=dateFormat.format(dStartDate);
						}
					}
					String closeDate="";
					if (XML.hasChild(eDates,"forbid"))
					{
						if (XML.hasChild(eDates,"close"))
						{
							Date dCloseDate=getDate(XML.getText(eDates,"close"));
							if (dCloseDate==null)
							{
								throw new XMLException("Warning date invalid.");
							}
							Date dForbidDate=getDate(XML.getText(eDates,"forbid"));
							if (dForbidDate==null)
							{
								throw new XMLException("Close date invalid.");
							}
							closeDate=dateFormat.format(dForbidDate);
						}
						else
						{
							throw new XMLException("Unexpected forbid date.");
						}
					}
					else if (XML.hasChild(eDates,"close"))
					{
						throw new XMLException("Unexpected close date.");
					}
					
					if (questionsReleasesMetadata.length()>0)
					{
						questionsReleasesMetadata.append(';');
					}
					questionsReleasesMetadata.append(packageName);
					questionsReleasesMetadata.append(';');
					questionsReleasesMetadata.append(publisher);
					questionsReleasesMetadata.append(';');
					questionsReleasesMetadata.append(releaseDate);
					questionsReleasesMetadata.append(';');
					questionsReleasesMetadata.append(startDate);
					questionsReleasesMetadata.append(';');
					questionsReleasesMetadata.append(closeDate);
					questionsReleasesMetadata.append(';');
					questionsReleasesMetadata.append(deleteDate);
				}
				catch (XMLException xe)
				{
				}
			}
		}
		return questionsReleasesMetadata.toString();
	}
	
	@Override
	public String getQuestionReleaseMetadata(String packageName) throws RemoteException
	{
		StringBuffer questionReleaseMetadata=new StringBuffer();
		File deployFile=getDeployXmlFile(packageName);
		if (deployFile.isFile() && deployFile.canRead())
		{
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
					String publisher="";
					if (XML.hasChild(eRoot,"publisher"))
					{
						publisher=XML.getText(eRoot,"publisher");
					}
					DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String releaseDate="";
					if (XML.hasChild(eRoot,"release-date"))
					{
						Date dReleaseDate=getDate(XML.getText(eRoot,"release-date"));
						if (dReleaseDate==null)
						{
							throw new XMLException("Release date invalid.");
						}
						else
						{
							releaseDate=dateFormat.format(dReleaseDate);
						}
					}
					String deleteDate="";
					if (XML.hasChild(eRoot,"delete-date"))
					{
						deleteDate=XML.getText(eRoot,"delete-date");
						Date dDeleteDate=getDate(deleteDate);
						if (dDeleteDate==null)
						{
							throw new XMLException("Delete date invalid.");
						}
						else
						{
							deleteDate=dateFormat.format(dDeleteDate);
						}
					}
					Element eDates=XML.getChild(eRoot,"dates");
					String startDate=XML.getText(eDates,"open");
					if ("yes".equals(startDate))
					{
						startDate="";
					}
					else
					{
						Date dStartDate=getDate(startDate);
						if (dStartDate==null)
						{
							throw new XMLException("Start date invalid.");
						}
						else
						{
							startDate=dateFormat.format(dStartDate);
						}
					}
					String closeDate="";
					String warningDate="";
					if (XML.hasChild(eDates,"forbid"))
					{
						if (XML.hasChild(eDates,"close"))
						{
							Date dCloseDate=getDate(XML.getText(eDates,"close"));
							if (dCloseDate==null)
							{
								throw new XMLException("Warning date invalid.");
							}
							Date dForbidDate=getDate(XML.getText(eDates,"forbid"));
							if (dForbidDate==null)
							{
								throw new XMLException("Close date invalid.");
							}
							Calendar cCloseDate=Calendar.getInstance();
							cCloseDate.setTime(dCloseDate);
							Calendar cForbidDate=Calendar.getInstance();
							cForbidDate.setTime(dForbidDate);
							closeDate=dateFormat.format(dForbidDate);
							if (cForbidDate.after(cCloseDate))
							{
								warningDate=dateFormat.format(dCloseDate);
							}
						}
						else
						{
							throw new XMLException("Unexpected forbid date.");
						}
					}
					else if (XML.hasChild(eDates,"close"))
					{
						throw new XMLException("Unexpected close date.");
					}
					Element eAccess=XML.getChild(eRoot,"access");
					Element eUsers=XML.getChild(eAccess,"users");
					boolean allUsersAllowed=true;
					StringBuffer users=new StringBuffer();
					StringBuffer userGroups=new StringBuffer(";@");
					if (!"yes".equals(eUsers.getAttribute("world")))
					{
						allUsersAllowed=false;
						for(Element user:XML.getChildren(eUsers,"oucu"))
						{
							users.append(';');
							users.append(XML.getText(user));
						}
						for (Element userGroup:XML.getChildren(eUsers,"authid"))
						{
							userGroups.append(';');
							userGroups.append(XML.getText(userGroup));
						}
					}
					questionReleaseMetadata.append(publisher);
					questionReleaseMetadata.append(';');
					questionReleaseMetadata.append(releaseDate);
					questionReleaseMetadata.append(';');
					questionReleaseMetadata.append(startDate);
					questionReleaseMetadata.append(';');
					questionReleaseMetadata.append(closeDate);
					questionReleaseMetadata.append(';');
					questionReleaseMetadata.append(deleteDate);
					questionReleaseMetadata.append(';');
					questionReleaseMetadata.append(warningDate);
					questionReleaseMetadata.append(allUsersAllowed?";true":";false");
					questionReleaseMetadata.append(users.toString());
					questionReleaseMetadata.append(userGroups.toString());
				}
				catch (XMLException xe)
				{
				}
			}
		}
		return questionReleaseMetadata.toString();
	}
	
	@Override
	public String getTestsReleasesMetadata() throws RemoteException
	{
		StringBuffer testsReleasesMetadata=new StringBuffer();
		for (File deployFile:getTestbankFolder().listFiles(new TestDeployFileFilter()))
		{
			String testNameWithVersion=
				deployFile.getName().substring(0,deployFile.getName().length()-".deploy.xml".length());
			int version=0;
			if (testNameWithVersion.length()>3 && 
				testNameWithVersion.charAt(testNameWithVersion.length()-3)=='-' &&
				Character.isDigit(testNameWithVersion.charAt(testNameWithVersion.length()-2)) &&
				Character.isDigit(testNameWithVersion.charAt(testNameWithVersion.length()-1)))
			{
				version=
					Integer.parseInt(testNameWithVersion.substring(testNameWithVersion.length()-"dd".length()));
			}
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
					String testName=XML.getText(eRoot,"definition");
					if (!checkTestName(testName))
					{
						throw new XMLException("Test name not recognized.");
					}
					String publisher="";
					if (XML.hasChild(eRoot,"publisher"))
					{
						publisher=XML.getText(eRoot,"publisher");
					}
					DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String releaseDate="";
					if (XML.hasChild(eRoot,"release-date"))
					{
						releaseDate=XML.getText(eRoot,"release-date");
						Date dReleaseDate=getDate(releaseDate);
						if (dReleaseDate==null)
						{
							throw new XMLException("Release date invalid.");
						}
						else
						{
							releaseDate=dateFormat.format(dReleaseDate);
						}
					}
					
					String deleteDate="";
					if (XML.hasChild(eRoot,"delete-date"))
					{
						deleteDate=XML.getText(eRoot,"delete-date");
						Date dDeleteDate=getDate(deleteDate);
						if (dDeleteDate==null)
						{
							throw new XMLException("Delete date invalid.");
						}
						else
						{
							deleteDate=dateFormat.format(dDeleteDate);
						}
					}
					Element eDates=XML.getChild(eRoot,"dates");
					String startDate=XML.getText(eDates,"open");
					if ("yes".equals(startDate))
					{
						startDate="";
					}
					else
					{
						Date dStartDate=getDate(startDate);
						if (dStartDate==null)
						{
							throw new XMLException("Start date invalid.");
						}
						else
						{
							startDate=dateFormat.format(dStartDate);
						}
					}
					String closeDate="";
					if (XML.hasChild(eDates,"forbid"))
					{
						if (XML.hasChild(eDates,"close"))
						{
							Date dCloseDate=getDate(XML.getText(eDates,"close"));
							if (dCloseDate==null)
							{
								throw new XMLException("Warning date invalid.");
							}
							Date dForbidDate=getDate(XML.getText(eDates,"forbid"));
							if (dForbidDate==null)
							{
								throw new XMLException("Close date invalid.");
							}
							closeDate=dateFormat.format(dForbidDate);
						}
						else
						{
							throw new XMLException("Unexpected forbid date.");
						}
					}
					else if (XML.hasChild(eDates,"close"))
					{
						throw new XMLException("Unexpected close date.");
					}
					if (testsReleasesMetadata.length()>0)
					{
						testsReleasesMetadata.append(';');
					}
					testsReleasesMetadata.append(testName);
					testsReleasesMetadata.append(';');
					testsReleasesMetadata.append(version);
					testsReleasesMetadata.append(';');
					testsReleasesMetadata.append(publisher);
					testsReleasesMetadata.append(';');
					testsReleasesMetadata.append(releaseDate);
					testsReleasesMetadata.append(';');
					testsReleasesMetadata.append(startDate);
					testsReleasesMetadata.append(';');
					testsReleasesMetadata.append(closeDate);
					testsReleasesMetadata.append(';');
					testsReleasesMetadata.append(deleteDate);
				}
				catch (XMLException xe)
				{
				}
			}
		}
		return testsReleasesMetadata.toString();
	}
	
	@Override
	public String getTestReleaseMetadata(String testName,int version) throws RemoteException
	{
		StringBuffer testReleaseMetadata=new StringBuffer();
		File deployFile=getDeployXmlFile(getTestNameWithVersion(testName,version));
		File testFile=getTestXmlFile(testName);
		if (deployFile.isFile() && deployFile.canRead() && testFile.isFile() && testFile.canRead())
		{
			Document dDeploy=null;
			Document dTest=null;
			try
			{
				dDeploy=XML.parse(deployFile);
				dTest=XML.parse(testFile);
			}
			catch (IOException e)
			{
			}
			if (dDeploy!=null && dTest!=null)
			{
				Element eDeployRoot=dDeploy.getDocumentElement();
				Element eTestRoot=dTest.getDocumentElement();
				try
				{
					String publisher="";
					if (XML.hasChild(eDeployRoot,"publisher"))
					{
						publisher=XML.getText(eDeployRoot,"publisher");
					}
					DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String releaseDate="";
					if (XML.hasChild(eDeployRoot,"release-date"))
					{
						releaseDate=XML.getText(eDeployRoot,"release-date");
						Date dReleaseDate=getDate(releaseDate);
						if (dReleaseDate==null)
						{
							throw new XMLException("Release date invalid.");
						}
						else
						{
							releaseDate=dateFormat.format(dReleaseDate);
						}
					}
					String deleteDate="";
					if (XML.hasChild(eDeployRoot,"delete-date"))
					{
						deleteDate=XML.getText(eDeployRoot,"delete-date");
						Date dDeleteDate=getDate(deleteDate);
						if (dDeleteDate==null)
						{
							throw new XMLException("Delete date invalid.");
						}
						else
						{
							deleteDate=dateFormat.format(dDeleteDate);
						}
					}
					String assessement="ASSESSEMENT_NOT_ASSESSED";
					if (XML.hasChild(eDeployRoot,"assessed"))
					{
						if (XML.hasChildWithAttribute(eDeployRoot,"assessed","optional","yes"))
						{
							assessement="ASSESSEMENT_OPTIONAL";
						}
						else
						{
							assessement="ASSESSEMENT_REQUIRED";
						}
					}
					Element eDates=XML.getChild(eDeployRoot,"dates");
					String startDate=XML.getText(eDates,"open");
					if ("yes".equals(startDate))
					{
						startDate="";
					}
					else
					{
						Date dStartDate=getDate(startDate);
						if (dStartDate==null)
						{
							throw new XMLException("Start date invalid.");
						}
						else
						{
							startDate=dateFormat.format(dStartDate);
						}
					}
					String closeDate="";
					String warningDate="";
					if (XML.hasChild(eDates,"forbid"))
					{
						if (XML.hasChild(eDates,"close"))
						{
							Date dCloseDate=getDate(XML.getText(eDates,"close"));
							if (dCloseDate==null)
							{
								throw new XMLException("Warning date invalid.");
							}
							Date dForbidDate=getDate(XML.getText(eDates,"forbid"));
							if (dForbidDate==null)
							{
								throw new XMLException("Close date invalid.");
							}
							Calendar cCloseDate=Calendar.getInstance();
							cCloseDate.setTime(dCloseDate);
							Calendar cForbidDate=Calendar.getInstance();
							cForbidDate.setTime(dForbidDate);
							closeDate=dateFormat.format(dForbidDate);
							if (cForbidDate.after(cCloseDate))
							{
								warningDate=dateFormat.format(dCloseDate);
							}
						}
						else
						{
							throw new XMLException("Unexpected forbid date.");
						}
					}
					else if (XML.hasChild(eDates,"close"))
					{
						throw new XMLException("Unexpected close date.");
					}
					String feedbackDate="";
					if (XML.hasChild(eDates,"feedback"))
					{
						Date dFeedbackDate=getDate(XML.getText(eDates,"feedback"));
						if (dFeedbackDate==null)
						{
							throw new XMLException("Feedback date invalid.");
						}
						else
						{
							feedbackDate=dateFormat.format(dFeedbackDate);
						}
					}
					StringBuffer supportContacts=new StringBuffer();
					if (XML.hasChild(eDeployRoot,"supportcontacts"))
					{
						Element eSupportcontacts=XML.getChild(eDeployRoot,"supportcontacts");
						Map<UnedUserFilter,StringBuffer> supportContactsMap=
							new HashMap<UnedUserFilter,StringBuffer>();
						String mainSupportContacts=XML.getText(eSupportcontacts);
						if (!"".equals(mainSupportContacts))
						{
							supportContactsMap.put(new UnedUserFilter(UnedUserFilter.ALL_USER_FILTER,""),
								new StringBuffer(XML.getText(eSupportcontacts)));
						}
						for (Element eUserFilter:XML.getChildren(eSupportcontacts,"user-filter"))
						{
							String supportContactsToAdd=XML.getText(eUserFilter);
							if (!"".equals(supportContactsToAdd))
							{
								UnedUserFilter userFilter=new UnedUserFilter(
									eUserFilter.getAttribute("type"),eUserFilter.getAttribute("value"));
								if (supportContactsMap!=null && supportContactsMap.containsKey(userFilter))
								{
									StringBuffer supportContactsValue=supportContactsMap.get(userFilter);
									supportContactsValue.append(',');
									supportContactsValue.append(supportContactsToAdd);
								}
								else
								{
									supportContactsMap.put(userFilter,new StringBuffer(supportContactsToAdd));
								}
							}
						}
						for (Map.Entry<UnedUserFilter,StringBuffer> supportContactEntry:
							supportContactsMap.entrySet())
						{
							if (supportContacts.length()>0)
							{
								supportContacts.append(':');
							}
							UnedUserFilter userFilter=supportContactEntry.getKey();
							supportContacts.append(userFilter.getType());
							supportContacts.append(':');
							supportContacts.append(userFilter.getValue());
							supportContacts.append(':');
							supportContacts.append(supportContactEntry.getValue());
						}
					}
					StringBuffer evaluators=new StringBuffer();
					if (XML.hasChild(eDeployRoot,"evaluators"))
					{
						Element eEvaluators=XML.getChild(eDeployRoot,"evaluators");
						Map<UnedUserFilter,StringBuffer> evaluatorsMap=new HashMap<UnedUserFilter,StringBuffer>();
						String mainEvaluators=XML.getText(eEvaluators);
						if (!"".equals(mainEvaluators))
						{
							evaluatorsMap.put(new UnedUserFilter(UnedUserFilter.ALL_USER_FILTER,""),
								new StringBuffer(XML.getText(eEvaluators)));
						}
						for (Element eUserFilter:XML.getChildren(eEvaluators,"user-filter"))
						{
							String evaluatorsToAdd=XML.getText(eUserFilter);
							if (!"".equals(evaluatorsToAdd))
							{
								UnedUserFilter userFilter=new UnedUserFilter(
									eUserFilter.getAttribute("type"),eUserFilter.getAttribute("value"));
								if (evaluatorsMap!=null && evaluatorsMap.containsKey(userFilter))
								{
									StringBuffer evaluatorsValue=evaluatorsMap.get(userFilter);
									evaluatorsValue.append(',');
									evaluatorsValue.append(evaluatorsToAdd);
								}
								else
								{
									evaluatorsMap.put(userFilter,new StringBuffer(evaluatorsToAdd));
								}
							}
						}
						for (Map.Entry<UnedUserFilter,StringBuffer> evaluatorEntry:evaluatorsMap.entrySet())
						{
							if (evaluators.length()>0)
							{
								evaluators.append(':');
							}
							UnedUserFilter userFilter=evaluatorEntry.getKey();
							evaluators.append(userFilter.getType());
							evaluators.append(':');
							evaluators.append(userFilter.getValue());
							evaluators.append(':');
							evaluators.append(evaluatorEntry.getValue());
						}
					}
					Element eAccess=XML.getChild(eDeployRoot,"access");
					Element eUsers=XML.getChild(eAccess,"users");
					boolean allUsersAllowed=true;
					StringBuffer users=new StringBuffer();
					StringBuffer userGroups=new StringBuffer(";@");
					if (!"yes".equals(eUsers.getAttribute("world")))
					{
						allUsersAllowed=false;
						for (Element user:XML.getChildren(eUsers,"oucu"))
						{
							users.append(';');
							users.append(XML.getText(user));
						}
						for (Element userGroup:XML.getChildren(eUsers,"authid"))
						{
							userGroups.append(';');
							userGroups.append(XML.getText(userGroup));
						}
					}
					Element eAdmins=null;
					boolean allowAdminReports=false;
					StringBuffer admins=new StringBuffer(";@");
					StringBuffer adminGroups=new StringBuffer(";@");
					try
					{
						eAdmins=XML.getChild(eAccess,"admins");
					}
					catch (XMLException xe)
					{
						eAdmins=null;
					}
					if (eAdmins!=null)
					{
						try
						{
							String[] reports=XML.getAttributeFromChildren(eAdmins,"oucu","reports");
							allowAdminReports=true;
							for (String report:reports)
							{
								if (!"yes".equals(report))
								{
									allowAdminReports=false;
									break;
								}
							}
						}
						catch (XMLException xe)
						{
							allowAdminReports=false;
						}
						for (Element admin:XML.getChildren(eAdmins,"oucu"))
						{
							admins.append(';');
							admins.append(XML.getText(admin));
						}
						for (Element adminGroup:XML.getChildren(eAdmins,"authid"))
						{
							adminGroups.append(';');
							adminGroups.append(XML.getText(adminGroup));
						}
					}
					Element eOptions=XML.getChild(eTestRoot,"options");
					boolean freeSummary="yes".equals(eOptions.getAttribute("freesummary"));
					boolean freeStop="yes".equals(eOptions.getAttribute("freestop"));
					boolean summaryQuestions=!"no".equals(eOptions.getAttribute("summaryquestions"));
					boolean summaryScores="yes".equals(eOptions.getAttribute("summaryscores"));
					boolean summaryAttempts="yes".equals(eOptions.getAttribute("summaryattempts"));
					boolean navigation="yes".equals(eOptions.getAttribute("navigation"));
					String navLocation=eOptions.getAttribute("navlocation");
					if ("left".equals(navLocation))
					{
						navLocation="NAVLOCATION_LEFT";
					}
					else if ("wide".equals(navLocation))
					{
						navLocation="NAVLOCATION_WIDE";
					}
					else
					{
						navLocation="NAVLOCATION_BOTTOM";
					}
					boolean optRedoQuestion="yes".equals(eOptions.getAttribute("redoquestion"));
					boolean optRedoQuestionAuto="yes".equals(eOptions.getAttribute("redoquestionauto"));
					if (optRedoQuestionAuto) 
					{
						optRedoQuestion=true;
					}
					String redoQuestion="NO";
					if (optRedoQuestion)
					{
						if (optRedoQuestionAuto)
						{
							redoQuestion="YES";
						}
						else
						{
							redoQuestion="ASK";
						}
					}
					boolean redoTest="yes".equals(eOptions.getAttribute("redotest"));
					testReleaseMetadata.append(publisher);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(releaseDate);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(startDate);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(closeDate);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(deleteDate);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(warningDate);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(feedbackDate);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(assessement);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(allUsersAllowed?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(allowAdminReports?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(freeSummary?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(freeStop?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(summaryQuestions?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(summaryScores?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(summaryAttempts?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(navigation?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(navLocation);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(redoQuestion);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(redoTest?"true":"false");
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(supportContacts);
					testReleaseMetadata.append(';');
					testReleaseMetadata.append(evaluators);
					testReleaseMetadata.append(users);
					testReleaseMetadata.append(userGroups);
					testReleaseMetadata.append(admins);
					testReleaseMetadata.append(adminGroups);
				}
				catch (XMLException xe)
				{
				}
			}
		}
		return testReleaseMetadata.toString();
	}
	
	@Override
	public String getTestReleaseVersions(String testName) throws RemoteException
	{
		StringBuffer testReleaseVersions=new StringBuffer();
		for (File deployFile:getTestbankFolder().listFiles(new TestDeployFileFilter(testName)))
		{
			String sVersion=deployFile.getName().substring(
				testName.length(),deployFile.getName().length()-".deploy.xml".length());
			if (sVersion.length()==3 && sVersion.charAt(0)=='-' && Character.isDigit(sVersion.charAt(1)) && 
				Character.isDigit(sVersion.charAt(2)))
			{
				int version=10*(sVersion.charAt(1)-'0')+sVersion.charAt(2)-'0';
				if (testReleaseVersions.length()>0)
				{
					testReleaseVersions.append(';');
				}
				testReleaseVersions.append(version);
			}
		}
		return testReleaseVersions.toString();
	}
	
	/**
	 * @param sDate Date as string
	 * @return Parsed date, null if date can't be parsed.
	 */
	private Date getDate(String sDate)
	{
		Date date=null;
		if (sDate!=null)
		{
			StringBuffer sbDate=null;
			
			// Check it with regex
			Matcher m=DATETIME.matcher(sDate);
			if (m.matches())
			{
				sbDate=new StringBuffer(sDate);
				sbDate.append(" 00:00:00");
			}
			else
			{
				// Check time
				String sTime=m.group(1);
				Matcher mPart=PARTTIME.matcher(sTime);
				if (mPart.matches())
				{
					sbDate=new StringBuffer(sDate);
					sbDate.append(":00");
				}
				else if (FULLTIME.matcher(sTime).matches())
				{
					sbDate=new StringBuffer(sDate);
				}
			}
			if (sbDate!=null)
			{
				try
				{
					DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					date=dateFormat.parse(sbDate.toString());
				}
				catch(ParseException e)
				{
					date=null;
				}
			}
		}
		return date;
	}
	
	/**
	 * Write a file from an array of Base64 encoded strings
	 * @param f File
	 * @param base64File File content as an array of Base64 encoded strings
	 * @return true if successful, false otherwise
	 */
	private boolean writeFileFromBase64Array(File f,String[] base64File)
	{
		boolean ok=true;
		FileOutputStream fos=null;
		try
		{
			fos=new FileOutputStream(f);
			if (base64File!=null)
			{
				for (String base64FileBuffer:base64File)
				{
					byte[] fileBuffer=Base64.decode(base64FileBuffer);
					fos.write(fileBuffer);
				}
			}
		}
		catch (IOException e)
		{
			ok=false;
		}
		finally
		{
			try
			{
				fos.close();
			}
			catch (IOException e2)
			{
			}
		}
		return ok;
	}
	
	private String getTestNameWithVersion(String testName,int version)
	{
		StringBuffer testNameWithVersion=new StringBuffer(testName);
		if (version>0)
		{
			testNameWithVersion.append('-');
			if (version<10)
			{
				testNameWithVersion.append('0');
			}
			testNameWithVersion.append(version);
		}
		return testNameWithVersion.toString();
	}
	
	/**
	 * @param testName Test's name
	 * @return Test xml file
	 */
	private File getTestXmlFile(String testName)
	{
		StringBuffer testXmlPath=new StringBuffer();
		testXmlPath.append("testbank");
		testXmlPath.append(File.separatorChar);
		testXmlPath.append(testName);
		testXmlPath.append(".test.xml");
		return new File(getServletContext().getRealPath(testXmlPath.toString()));
	}
	
	/**
	 * @param name Test's name (including version if needed) or question's name
	 * @return Deploy xml file
	 */
	private File getDeployXmlFile(String name)
	{
		StringBuffer testXmlPath=new StringBuffer();
		testXmlPath.append("testbank");
		testXmlPath.append(File.separatorChar);
		testXmlPath.append(name);
		testXmlPath.append(".deploy.xml");
		return new File(getServletContext().getRealPath(testXmlPath.toString()));
	}
	
	/**
	 * @param packageName Package's name
	 * @return Question's jar file
	 */
	private File getQuestionJarFile(String packageName,String version)
	{
		StringBuffer questionJarPath=new StringBuffer();
		questionJarPath.append("questionbank");
		questionJarPath.append(File.separatorChar);
		questionJarPath.append(packageName);
		if (version==null)
		{
			int iVersion=1;
			version="1.0";
			for (File questionJarFile:getQuestionbankFolder().listFiles(new QuestionJarFileFilter(packageName)))
			{
				String questionJarVersion=questionJarFile.getName().substring(
					packageName.length(),questionJarFile.getName().length()-".jar".length());
				if (questionJarVersion.length()==4 && questionJarVersion.charAt(0)=='.' && 
					Character.isDigit(questionJarVersion.charAt(1)) && questionJarVersion.charAt(2)=='.' && 
					Character.isDigit(questionJarVersion.charAt(3)))
				{
					int iQuestionJarVersion=10*(questionJarVersion.charAt(1)-'0')+questionJarVersion.charAt(3)-'0';
					if (iQuestionJarVersion>iVersion)
					{
						iVersion=iQuestionJarVersion;
						version=questionJarVersion.substring(1);
					}
				}
			}
		}
		questionJarPath.append('.');
		questionJarPath.append(version);
		questionJarPath.append(".jar");
		return new File(getServletContext().getRealPath(questionJarPath.toString()));
	}
	
	/**
	 * @return Questionbank folder
	 */
	private File getQuestionbankFolder()
	{
		return new File(getServletContext().getRealPath("questionbank"));
	}
	
	/**
	 * @return Testbank folder
	 */
	private File getTestbankFolder()
	{
		return new File(getServletContext().getRealPath("testbank"));
	}
	
	/**
	 * @return Web service servlet context
	 */
	private ServletContext getServletContext()
	{
		return ((HttpServlet)MessageContext.getCurrentContext().getProperty(
			HTTPConstants.MC_HTTP_SERVLET)).getServletContext();
	}
	
	/**
	 * @return Web service request
	 */
	private HttpServletRequest getRequest()
	{
		return (HttpServletRequest)MessageContext.getCurrentContext().getProperty(
			HTTPConstants.MC_HTTP_SERVLETREQUEST);
	}
	
	/**
	 * @return Web service response
	 */
	private HttpServletResponse getResponse()
	{
		return (HttpServletResponse)MessageContext.getCurrentContext().getProperty(
			HTTPConstants.MC_HTTP_SERVLETRESPONSE);
	}
	
	/**
	 * @return TnServlet URI
	 */
	private String getTnServletRequestURI()
	{
		String requestURI=getRequest().getRequestURI();
		return requestURI.substring(0,requestURI.indexOf("/services/")+1);
	}
	
	/**
	 * @param packageName Package name
	 * @return true if package name is valid, false otherwise
	 */
	private boolean checkPackageName(String packageName)
	{
		boolean ok=false;
		if (packageName!=null && packageName.startsWith("u"))
		{
			int iStartQId=packageName.indexOf('q');
			if (iStartQId!=-1 && iStartQId+1<packageName.length())
			{
				try
				{
					ok=true;
					for (int i=1;i<iStartQId-1;i++)
					{
						if (!Character.isDigit(packageName.charAt(i)))
						{
							ok=false;
							break;
						}
					}
					if (ok)
					{
						Long.parseLong(packageName.substring(1,iStartQId-1));
						for (int i=iStartQId+1;i<packageName.length();i++)
						{
							if (!Character.isDigit(packageName.charAt(i)))
							{
								ok=false;
							}
						}
					}
					if (ok)
					{
						Long.parseLong(packageName.substring(iStartQId+1));
					}
				}
				catch (NumberFormatException nfe)
				{
					ok=false;
				}
			}
		}
		return ok;
	}
	
	/**
	 * @param testName Test name
	 * @return true if test name is valid, false otherwise
	 */
	private boolean checkTestName(String testName)
	{
		boolean ok=false;
		if (testName!=null && testName.startsWith("u"))
		{
			int iStartTId=testName.indexOf('t');
			if (iStartTId!=-1 && iStartTId+1<testName.length())
			{
				try
				{
					ok=true;
					for (int i=1;i<iStartTId-1;i++)
					{
						if (!Character.isDigit(testName.charAt(i)))
						{
							ok=false;
						}
					}
					if (ok)
					{
						Long.parseLong(testName.substring(1,iStartTId-1));
						for (int i=iStartTId+1;i<testName.length();i++)
						{
							if (!Character.isDigit(testName.charAt(i)))
							{
								ok=false;
							}
						}
					}
					if (ok)
					{
						Long.parseLong(testName.substring(iStartTId+1));
					}
				}
				catch (NumberFormatException nfe)
				{
					ok=false;
				}
			}
		}
		return ok;
	}
	
	/**
	 * @param message Message received from GEPEQ
	 * @return Decrypted message
	 */
	private String decryptGepeqMessage(String message)
	{
		String decryptedMessage=null;
		DecryptGepeqMessageRequest decryptGepeqMessageRequest=
			new DecryptGepeqMessageRequest(getRequest(),message);
		DecryptGepeqMessageResponse decryptGepeqMessageResponse=new DecryptGepeqMessageResponse(getResponse());
		try
		{
			getServletContext().getRequestDispatcher(getTnServletRequestURI()).include(
				decryptGepeqMessageRequest,decryptGepeqMessageResponse);
			decryptedMessage=decryptGepeqMessageResponse.getDecryptedMessage();
		}
		catch (Exception e)
		{
			throw new OmUnexpectedException("Unexpected error invoking om-tn servlet",e);
		}
		return decryptedMessage;
	}
}
