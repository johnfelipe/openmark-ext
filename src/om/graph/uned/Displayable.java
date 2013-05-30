package om.graph.uned;

// UNED: 01-09-2011 - dballestin
/**
 * Graph items implementing this interface have the display property that indicates if this
 * graph item is going to be displayed or not
 */
public interface Displayable
{
	/**
	 * Set state flag that indicates if this graph item is displayed (true) or not (false)
	 * @param display State flag that indicates if this graph item is displayed or not
	 */
	public void setDisplay(boolean display);
}
