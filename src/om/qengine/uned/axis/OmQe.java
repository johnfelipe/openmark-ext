package om.qengine.uned.axis;

import java.io.File;
import java.rmi.RemoteException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

// UNED: 22-03-2012 - dballestin
/**
 * Implementation of web service for Om Question Engine Web Application.
 */
public class OmQe implements IOmQe
{
	@Override
	public void deleteQuestionFromCache(String packageName) throws RemoteException
	{
		File questionFromCache=getQuestionJarFile(packageName);
		if (questionFromCache.exists())
		{
			questionFromCache.delete();
		}
	}
	
	/**
	 * @param packageName Package's name
	 * @return Question's jar file
	 */
	private File getQuestionJarFile(String packageName)
	{
		StringBuffer questionJarPath=new StringBuffer();
		questionJarPath.append("questioncache");
		questionJarPath.append(File.separatorChar);
		questionJarPath.append(packageName);
		questionJarPath.append(".1.0.jar");
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
	
}
