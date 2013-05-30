package om.stdquestion.uned;

import om.OmDeveloperException;
import om.question.InitParams;
import om.stdcomponent.uned.ComponentRegistry;

// UNED: 21-06-2011 - dballestin
/**
 * Manages list of available component classes with some new properties in some standard components
 * used to be able to make a generic class for question.xml
 */
public class QComponentManager extends om.stdquestion.QComponentManager
{
	/**
	 * Constructs with standard components plus anything from jar files specified
	 * in InitParams.
	 * @param ip Question initialisation parameters
	 * @throws OmDeveloperException If the component registry throws a wobbly
	 */
	protected QComponentManager(InitParams ip) throws OmDeveloperException
	{
		// Call the new constructor of superclass so it will register our standard components
		super(ip,false);
		
		//Now we register our components calling om.stdcomponent.uned.ComponentRegistry.fill method
		ComponentRegistry.fill(this);
	}
}
