package om.stdcomponent.uned;

import om.OmDeveloperException;

/** 
 * Interface callers must provide to implement specific properties checks for om.stdcomponent.uned
 * components.
 */
public interface PropertyCheck
{
	/**
	 * Checks a property.<br/><br/>
	 * This will be called after component initializations if the property checked has been set 
	 * without using placeholders.<br/><br/>
	 * But if the property checked has been set using placeholders this will be called every time you try 
	 * to get the property's value.<br/><br/>
	 * Property's value will be encapsulated within the appropiated Java class object, so if property's type is string
	 * a java.lang.String object will be passed, etc. 
	 * @param value Property's value
	 * @throws OmDeveloperException
	 */
	public void check(Object value) throws OmDeveloperException;
}
