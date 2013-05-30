package om.graph.uned;

// UNED: 29-08-2011 - dballestin
/** 
 * Thrown to indicate problems when replacing values for placeholders in a canvas.
 */
@SuppressWarnings("serial")
public class GraphFormatRuntimeException extends RuntimeException
{
	public GraphFormatRuntimeException(String message)
	{
		super(message);
	}
	
	public GraphFormatRuntimeException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
