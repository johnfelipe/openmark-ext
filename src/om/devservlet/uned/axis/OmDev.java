package om.devservlet.uned.axis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import om.OmUnexpectedException;

import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import org.apache.axis.transport.http.HTTPConstants;

// UNED: 12-12-2011 - dballestin
/**
 * Implementation of web service for Om Developer Web Application.
 */
public class OmDev implements IOmDev
{
	/** String written in response when a build is sucessful */
    private final static String BUILD_SUCCESS_STRING="BUILD SUCCESSFUL";
	
    /**
	 * Utility class to construct a request to dispatch to Om Developer Servlet from this web service
	 * to create a question.
     */
    private class CreateQuestionRequest extends HttpServletRequestWrapper
    {
    	private Map<String,String> parameters;
    	
    	public CreateQuestionRequest(HttpServletRequest request,String packageName,String path,String[] extraPackages)
    	{
    		super(request);
    		parameters=new HashMap<String,String>();
    		if (packageName!=null)
    		{
        		parameters.put("package",packageName);
    		}
    		if (path!=null)
    		{
        		parameters.put("source",path);
    		}
    		if (extraPackages!=null)
    		{
        		for (int i=0;i<extraPackages.length;i++)
        		{
        			StringBuffer sbExtra=new StringBuffer("extra");
        			sbExtra.append(i);
        			parameters.put(sbExtra.toString(),extraPackages[i]);
        		}
    		}
    	}
    	
		@Override
		public String getPathInfo()
		{
			return "/";
		}
		
		@Override
		public String getParameter(String name)
		{
			return parameters.get(name);
		}
		
		@Override
		public String getRequestURI()
		{
			return getDevServletRequestURI();
		}
		
		@Override
		public void setCharacterEncoding(String enc)
		{
		}
    }
    
    /**
	 * Utility class to construct a response to dispatch to Om Developer Servlet from this web service
	 * to create a question.
     */
    private class CreateQuestionResponse extends HttpServletResponseWrapper
    {
    	public CreateQuestionResponse(HttpServletResponse response)
    	{
    		super(response);
    	}
    	
		@Override
		public void sendRedirect(String location) throws IOException
		{
			throw new IOException();
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
	 * Utility class to construct a request to dispatch to Om Developer Servlet from this web service
	 * to build a question.
	 */
	private class BuildQuestionRequest extends HttpServletRequestWrapper
	{
		private String packageName;
		
		public BuildQuestionRequest(HttpServletRequest request,String packageName)
		{
			super(request);
			this.packageName=packageName;
		}
		
		@Override
		public String getPathInfo()
		{
			StringBuffer sBuildPathInfo=new StringBuffer("/build/");
			sBuildPathInfo.append(packageName);
			return sBuildPathInfo.toString();
		}
		
		@Override
		public String getRequestURI()
		{
			return getDevServletRequestURI();
		}
		
		@Override
		public void setCharacterEncoding(String enc)
		{
		}
	}
	
	/** 
	 * Utility class to construct a response to dispatch to Om Developer Servlet from this web service
	 * to build a question.
	 */
	private class BuildQuestionResponse extends HttpServletResponseWrapper
	{
		private StringWriter buildString;
		
		public BuildQuestionResponse(HttpServletResponse response)
		{
			super(response);
			buildString=new StringWriter();
		}
		
		public String getBuildString()
		{
			return buildString.toString();
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
			return new PrintWriter(buildString);
		}
	}
	
	/** 
	 * Utility class to construct a request to dispatch to Om Developer Servlet from this web service
	 * to delete a question.
	 */
	private class DeleteQuestionRequest extends HttpServletRequestWrapper
	{
		private String packageName;
		
		public DeleteQuestionRequest(HttpServletRequest request,String packageName)
		{
			super(request);
			this.packageName=packageName;
		}
		
		@Override
		public String getPathInfo()
		{
			StringBuffer sDeletePathInfo=new StringBuffer("/remove/");
			sDeletePathInfo.append(packageName);
			return sDeletePathInfo.toString();
		}
		
		@Override
		public String getRequestURI()
		{
			return getDevServletRequestURI();
		}
		
		@Override
		public void setCharacterEncoding(String enc)
		{
		}
	}
	
	/**
	 * Utility class to construct a response to dispatch to Om Developer Servlet from this web service
	 * to delete a question.
	 */
	private class DeleteQuestionResponse extends HttpServletResponseWrapper
	{
		public DeleteQuestionResponse(HttpServletResponse response)
		{
			super(response);
		}
		
		@Override
		public void sendRedirect(String location) throws IOException
		{
			throw new IOException();
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
	
	@Override
	public void createQuestion(String packageName,String path,String[] extraPackages) throws RemoteException
	{
		CreateQuestionRequest createRequest=new CreateQuestionRequest(getRequest(),packageName,path,extraPackages);
		CreateQuestionResponse createResponse=new CreateQuestionResponse(getResponse());
	    try
	    {
			getServletContext().getRequestDispatcher(getDevServletRequestURI()).include(createRequest,createResponse);
		}
	    catch (Exception e)
	    {
			throw new OmUnexpectedException("Unexpected error invoking om servlet",e);
		}
	}
	
	@Override
	public boolean buildQuestion(String packageName) throws RemoteException
	{
		BuildQuestionRequest buildRequest=new BuildQuestionRequest(getRequest(),packageName);
		BuildQuestionResponse buildResponse=new BuildQuestionResponse(getResponse());
	    try
	    {
			getServletContext().getRequestDispatcher(getDevServletRequestURI()).include(buildRequest,buildResponse);
		}
	    catch (Exception e)
	    {
			throw new OmUnexpectedException("Unexpected error invoking om servlet",e);
		}
		return buildResponse.getBuildString().contains(BUILD_SUCCESS_STRING);
	}
	
	@Override
	public void deleteQuestion(String packageName) throws RemoteException
	{
		DeleteQuestionRequest deleteRequest=new DeleteQuestionRequest(getRequest(),packageName);
		DeleteQuestionResponse deleteResponse=new DeleteQuestionResponse(getResponse());
		try
		{
			getServletContext().getRequestDispatcher(getDevServletRequestURI()).include(deleteRequest,deleteResponse);
		}
		catch (Exception e)
		{
			throw new OmUnexpectedException("Unexpected error invoking om servlet",e);
		}
	}
	
	@Override
	public boolean existQuestionJar(String packageName) throws RemoteException
	{
		return getQuestionJarFile(packageName).exists();
	}
	
	@Override
	public long getQuestionJarLastModified(String packageName) throws RemoteException
	{
		return getQuestionJarFile(packageName).lastModified();
	}
	
	@Override
	public String[] downloadQuestionJar(String packageName) throws RemoteException
	{
		return readFileToBase64Array(getQuestionJarFile(packageName));
	}
	
	/**
	 * Read a file to an array of Base64 encoded strings
	 * @param f File
	 * @return File content to an array of Base64 encoded strings
	 */
	private String[] readFileToBase64Array(File f)
	{
		List<String> base64File=null;
		if (f.exists())
		{
			FileInputStream fis=null;
			try
			{
				fis=new FileInputStream(f);
				base64File=new ArrayList<String>();
				byte[] base64FileBuffer=new byte[8192];
				for (int readedBytes=fis.read(base64FileBuffer);readedBytes>0;readedBytes=fis.read(base64FileBuffer))
				{
					base64File.add(Base64.encode(base64FileBuffer,0,readedBytes));
				}
			}
			catch (IOException e)
			{
				base64File=null;
			}
			finally
			{
				try
				{
					fis.close();
				}
				catch (IOException e2)
				{
				}
			}
		}
		return base64File==null?null:base64File.toArray(new String[base64File.size()]);
	}
	
	/**
	 * @param packageName Package's name
	 * @return Question's jar file
	 */
	private File getQuestionJarFile(String packageName)
	{
		StringBuffer questionJarPath=new StringBuffer();
		questionJarPath.append("questions");
		questionJarPath.append(File.separatorChar);
		questionJarPath.append(packageName);
		questionJarPath.append(".jar");
		return new File(getServletContext().getRealPath(questionJarPath.toString()));
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
	 * @return DevServlet URI
	 */
	private String getDevServletRequestURI()
	{
		String requestURI=getRequest().getRequestURI();
		return requestURI.substring(0,requestURI.indexOf("/services/")+1);
	}
}
