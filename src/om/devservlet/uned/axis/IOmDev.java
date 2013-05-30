package om.devservlet.uned.axis;

import java.rmi.Remote;
import java.rmi.RemoteException;

// UNED: 12-12-2011 - dballestin
/**
 * Interface of web service for Om Developer Web Application.
 */
public interface IOmDev extends Remote
{
	/** 
	 * Creates a question.
	 * @param packageName Package's name 
	 * @param path Path
	 * @param extraPackages Extra packages to include in question's jar
	 * @throws RemoteException
	 */
	public void createQuestion(String packageName,String path,String[] extraPackages) throws RemoteException;
	
	/** 
	 * Builds a question.
	 * @param packageName Package's name 
	 * @throws RemoteException
	 */
	public boolean buildQuestion(String packageName) throws RemoteException;
	
	/**
	 * Deletes a question.
	 * @param packageName Package's name
	 * @throws RemoteException
	 */
	public void deleteQuestion(String packageName) throws RemoteException;
	
	/**
	 * Checks if exists the jar file for the indicated question.  
	 * @param packageName Package's name
	 * @return true if exists the question's jar, false otherwise
	 * @throws RemoteException
	 */
	public boolean existQuestionJar(String packageName) throws RemoteException;
	
	/**
	 * @param packageName Package's name
	 * @return Question's jar last modified date measured as the number of milliseconds since the 
	 * standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT
	 * @throws RemoteException
	 */
	public long getQuestionJarLastModified(String packageName) throws RemoteException;
	
	/**
	 * Download a question's jar to an array of Base64 encoded strings.
	 * @param packageName Package's name
	 * @return Question's jar content as an array of Base64 encoded strings
	 * @throws RemoteException
	 */
	public String[] downloadQuestionJar(String packageName) throws RemoteException;
}
