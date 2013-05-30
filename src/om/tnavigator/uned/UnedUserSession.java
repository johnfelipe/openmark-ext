package om.tnavigator.uned;

import java.io.File;

import om.OmException;
import om.tnavigator.UserSession;

/**
 * Data stored about particular user.<br/><br/>
 * This is a special implementation for UNED.
 */
public class UnedUserSession extends UserSession
{
	private UnedNavigatorServlet ns;
	
	protected UnedUserSession(UnedNavigatorServlet owner,String cookie)
	{
		super(owner,cookie);
		this.ns=owner;
	}

	@Override
	public void loadTestDeployment(String testId) throws OmException
	{
		// Load test deploy, if necessary.
		if(getTestDeployment()==null)
		{
			File deployFile=ns.pathForTestDeployment(testId);
			setTestDeployment(new UnedTestDeployment(ns,deployFile));
		}
	}
}
