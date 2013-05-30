package om.qengine.uned.axis;

import java.rmi.Remote;
import java.rmi.RemoteException;

// UNED: 22-03-2012 - dballestin
/**
 * Interface of web service for Om Question Engine Web Application.
 */
public interface IOmQe extends Remote
{
	/**
	 * Deletes a question jar file from 'questioncache' folder of OM Question Engine web application.
	 * <br/><br/>
	 * It is needed to call it after redeploying a question on OM Test Navigator web application to avoid
	 * using an older version of the question.
	 * @param packageName
	 * @throws RemoteException
	 */
	public void deleteQuestionFromCache(String packageName) throws RemoteException;
}
