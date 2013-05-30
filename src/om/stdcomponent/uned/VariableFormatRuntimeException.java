package om.stdcomponent.uned;

// UNED: 12-02-2011 - dballestin
/** 
 * Thrown to indicate format errors found when parsing the "set" attribute of a variable component.
 */
@SuppressWarnings("serial")
public class VariableFormatRuntimeException extends RuntimeException
{
	public VariableFormatRuntimeException(String message)
	{
		super(message);
	}
	
	public VariableFormatRuntimeException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
