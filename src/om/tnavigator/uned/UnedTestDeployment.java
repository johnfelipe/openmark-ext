package om.tnavigator.uned;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import om.OmException;
import om.OmUnexpectedException;
import om.tnavigator.TestDeployment;
import util.xml.XML;
import util.xml.XMLException;
import util.xml.uned.UnedXML;

public class UnedTestDeployment extends TestDeployment
{
	private UnedUserFilter allUserFilter=null;
	
	private Map<UnedUserFilter,StringBuffer> supportContactsMap=null;
	private Map<UnedUserFilter,StringBuffer> evaluatorsMap=null;
	
	public UnedTestDeployment(UnedNavigatorServlet ns,File f) throws OmException
	{
		// Allow parent constructor do its job
		super(f);
		
		// Initialize an user filter that accept all users
		allUserFilter=new UnedUserFilter(ns,UnedUserFilter.ALL_USER_FILTER,"");
		
		// Initialize support contacts map
		if (XML.hasChild(getDeploy().getDocumentElement(),"supportcontacts"))
		{
			try
			{
				Element eSupportcontacts=UnedXML.findByTagName(getDeploy(),"supportcontacts");
				String mainSupportContacts=XML.getText(eSupportcontacts);
				if (!"".equals(mainSupportContacts))
				{
					// Initialize support contacts map
					supportContactsMap=new HashMap<UnedUserFilter,StringBuffer>();
					
					supportContactsMap.put(new UnedUserFilter(ns,UnedUserFilter.ALL_USER_FILTER,""),
						new StringBuffer(XML.getText(eSupportcontacts)));
				}
				for (Element eUserFilter:XML.getChildren(eSupportcontacts,"user-filter"))
				{
					String supportContactsToAdd=XML.getText(eUserFilter);
					if (!"".equals(supportContactsToAdd))
					{
						UnedUserFilter userFilter=new UnedUserFilter(
							ns,eUserFilter.getAttribute("type"),eUserFilter.getAttribute("value"));
						if (supportContactsMap!=null && supportContactsMap.containsKey(userFilter))
						{
							StringBuffer supportContacts=supportContactsMap.get(userFilter);
							supportContacts.append(',');
							supportContacts.append(supportContactsToAdd);
						}
						else
						{
							if (supportContactsMap==null)
							{
								// Initialize support contacts map
								supportContactsMap=new HashMap<UnedUserFilter,StringBuffer>();
							}
							supportContactsMap.put(userFilter,new StringBuffer(supportContactsToAdd));
						}
					}
				}
			}
			catch (XMLException xe)
			{
				throw new OmUnexpectedException(xe);
			}
		}
		
		// Initialize evaluators map
		if (XML.hasChild(getDeploy().getDocumentElement(),"evaluators"))
		{
			try
			{
				Element eEvaluators=UnedXML.findByTagName(getDeploy(),"evaluators");
				String mainEvaluators=XML.getText(eEvaluators);
				if (!"".equals(mainEvaluators))
				{
					// Initialize support contacts map
					evaluatorsMap=new HashMap<UnedUserFilter,StringBuffer>();
					
					evaluatorsMap.put(new UnedUserFilter(ns,UnedUserFilter.ALL_USER_FILTER,""),
						new StringBuffer(XML.getText(eEvaluators)));
				}
				for (Element eUserFilter:XML.getChildren(eEvaluators,"user-filter"))
				{
					String evaluatorsToAdd=XML.getText(eUserFilter);
					if (!"".equals(evaluatorsToAdd))
					{
						UnedUserFilter userFilter=new UnedUserFilter(
							ns,eUserFilter.getAttribute("type"),eUserFilter.getAttribute("value"));
						if (evaluatorsMap!=null && evaluatorsMap.containsKey(userFilter))
						{
							StringBuffer evaluators=evaluatorsMap.get(userFilter);
							evaluators.append(',');
							evaluators.append(evaluatorsToAdd);
						}
						else
						{
							if (evaluatorsMap==null)
							{
								// Initialize support contacts map
								evaluatorsMap=new HashMap<UnedUserFilter,StringBuffer>();
							}
							evaluatorsMap.put(userFilter,new StringBuffer(evaluatorsToAdd));
						}
					}
				}
			}
			catch (XMLException xe)
			{
				throw new OmUnexpectedException(xe);
			}
		}
	}
	
	/**
	 * @return the information about support contact mails to send error reports for a test and optionally filtered
	 * by users.
	 */
	public Map<UnedUserFilter,StringBuffer> getSupportContactsMap()
	{
		return supportContactsMap;
	}
	
	@Override
	public String getSupportContacts()
	{
		return supportContactsMap==null?"":supportContactsMap.get(allUserFilter).toString();
	}
	
	/**
	 * @return the information about who to send test results by email when an user submits an assesed test 
	 * and optionally filtered by users.
	 */
	public Map<UnedUserFilter,StringBuffer> getEvaluatorsMap()
	{
		return evaluatorsMap;
	}
	
	/**
	 * @return the information about who to send test results by email when an user submits an assesed test.
	 */
	public String getEvaluators()
	{
		return evaluatorsMap==null?"":evaluatorsMap.get(allUserFilter).toString();
	}
}
