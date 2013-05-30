package om.stdcomponent.uned;

// UNED: 07-11-2011 - dballestin
/** 
 * Thrown to indicate format errors found when parsing the "attribute" attribute of an 
 * attribute or summaryattribute component.
 */
@SuppressWarnings("serial")
public class AttributeFormatRuntimeException extends RuntimeException
{
	public AttributeFormatRuntimeException(String message)
	{
		super(message);
	}
	
	public AttributeFormatRuntimeException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
