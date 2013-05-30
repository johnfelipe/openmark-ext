package om.stdcomponent.uned;

// UNED: 03-11-2011 - dballestin
/** 
 * Thrown to indicate format errors found when parsing the "parameters" attribute of a 
 * task component.
 */
@SuppressWarnings("serial")
public class TaskFormatRuntimeException extends RuntimeException
{
	public TaskFormatRuntimeException(String message)
	{
		super(message);
	}
	
	public TaskFormatRuntimeException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
