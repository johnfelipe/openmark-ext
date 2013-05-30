package om.graph.uned;

// UNED: 30-08-2011 - dballestin
/** 
 * Thrown when tried to set a placeholder for an attribute with a set method but without a placeholder 
 * replacement for it.
 */
@SuppressWarnings("serial")
public class AttributePlaceholderNotImplementedException extends Exception
{
	public AttributePlaceholderNotImplementedException(String message)
	{
		super(message);
	}
	
	public AttributePlaceholderNotImplementedException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
