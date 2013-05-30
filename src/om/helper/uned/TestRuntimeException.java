package om.helper.uned;

// UNED: 21-09-2011 - dballestin
/** 
 * Thrown to indicate problems when replacing values for placeholders at tests.
 */
@SuppressWarnings("serial")
public class TestRuntimeException extends RuntimeException
{
	public TestRuntimeException(String message)
	{
		super(message);
	}
	
	public TestRuntimeException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
