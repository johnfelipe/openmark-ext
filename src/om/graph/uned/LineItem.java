package om.graph.uned;

import java.awt.Color;
import java.awt.Graphics2D;

import om.graph.GraphFormatException;
import om.graph.GraphPoint;
import om.graph.GraphScalar;
import om.graph.World;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 24-08-2011 - dballestin
/** 
 * Draws straight lines in the graph space.<br/><br/>
 * This version of line item allows to use placeholders with its attributes, except with ID attribute.
 */
public class LineItem extends om.graph.LineItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for first point of line, X co-ordinate */
	private String placeholderX=null;
	
	/** Placeholder for first point of line, Y co-ordinate */
	private String placeholderY=null;
	
	/** Placeholder for second point of line, X co-ordinate */
	private String placeholderX2=null;
	
	/** Placeholder for second point of line, Y co-ordinate */
	private String placeholderY2=null;
	
	/** Placeholder for outline colour */
	private String placeholderLineColour=null;
	
	/** Placeholder for line width in pixels */
	private String placeholderLineWidth=null;
	
	/** Placeholder for state flag that indicates if this line is displayed or not */
	private String placeholderDisplay=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this line is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public GraphPoint gp1;
		public GraphPoint gp2;
		public int iLineWidth;
		public Color cLine;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			gp1=getCorner1();
			gp2=getCorner2();
			iLineWidth=getLineWidth();
			cLine=getLineColour();
			if (placeholderX!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rX=sq.applyPlaceholders(placeholderX);
					if (!rX.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							GraphScalar gsX=new GraphScalar(rX);
							gp1=gp1.newX(gsX);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<line>: Unexpected number value for x: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<line>: Placeholder ");
					error.append(placeholderX);
					error.append(" for x can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderY!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rY=sq.applyPlaceholders(placeholderY);
					if (!rY.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							GraphScalar gsY=new GraphScalar(rY);
							gp1=gp1.newY(gsY);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<line>: Unexpected number value for y: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<line>: Placeholder ");
					error.append(placeholderY);
					error.append(" for y can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderX2!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rX2=sq.applyPlaceholders(placeholderX2);
					if (!rX2.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							GraphScalar gsX2=new GraphScalar(rX2);
							gp2=gp2.newX(gsX2);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<line>: Unexpected number value for x2: ");
							error.append(rX2);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<line>: Placeholder ");
					error.append(placeholderX2);
					error.append(" for x2 can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderY2!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rY2=sq.applyPlaceholders(placeholderY2);
					if (!rY2.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							GraphScalar gsY2=new GraphScalar(rY2);
							gp2=gp2.newY(gsY2);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<line>: Unexpected number value for y2: ");
							error.append(rY2);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<line>: Placeholder ");
					error.append(placeholderY2);
					error.append(" for y2 can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderLineWidth!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rLW=sq.applyPlaceholders(placeholderLineWidth);
					if (!rLW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer lWidth=new Integer(rLW);
							iLineWidth=lWidth.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<line>: Unexpected number value for lineWidth: ");
							error.append(rLW);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<line>: Placeholder ");
					error.append(placeholderLineWidth);
					error.append(" for lineWidth can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderLineColour!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rLC=sq.applyPlaceholders(placeholderLineColour);
					if (!rLC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Color c=getWorld().convertColour(rLC);
						cLine=c;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<line>: Placeholder ");
					error.append(placeholderLineColour);
					error.append(" for lineColour can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (iLineWidth>0 && cLine==null)
			{
				cLine=getWorld().convertColour("fg");
			}
			else if (cLine!=null && iLineWidth==-1)
			{
				iLineWidth=1;
			}
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public LineItem(World w) throws GraphFormatException
	{
		super(w);
	}
	
	@Override
	public void init() throws GraphFormatException
	{
		r=null;
		try
		{
			ReplacedAttributes rAux=new ReplacedAttributes();
			r=rAux;
			super.init();
		}
		catch (GraphFormatException e)
		{
			throw new GraphFormatRuntimeException(e.getMessage(),e);
		}
	}
	
	@Override
	public StandardQuestion getQuestion()
	{
		return sq;
	}
	
	@Override
	public void setQuestion(StandardQuestion sq)
	{
		this.sq=sq;
	}
	
	@Override
	public void setAttributePlaceholder(String attribute,String placeholder)
			throws AttributePlaceholderNotImplementedException
	{
		if (attribute.equalsIgnoreCase("X"))
		{
			placeholderX=placeholder;
		}
		else if (attribute.equalsIgnoreCase("Y"))
		{
			placeholderY=placeholder;
		}
		else if (attribute.equalsIgnoreCase("X2"))
		{
			placeholderX2=placeholder;
		}
		else if (attribute.equalsIgnoreCase("Y2"))
		{
			placeholderY2=placeholder;
		}
		else if (attribute.equalsIgnoreCase("lineColour"))
		{
			placeholderLineColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("lineWidth"))
		{
			placeholderLineWidth=placeholder;
		}
		else if (attribute.equalsIgnoreCase("display"))
		{
			placeholderDisplay=placeholder;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<line>: There is no placeholder support implemented for ");
			error.append(attribute);
			throw new AttributePlaceholderNotImplementedException(error.toString());
		}
	}
	
	/**
	 * Paints the graph item.
	 * @param g2 Target graphics context
	 */
	@Override
	public void paint(Graphics2D g2)
	{
		if (isDisplay())
		{
			r=null;
			try
			{
				ReplacedAttributes rAux=new ReplacedAttributes();
				r=rAux;
				super.paint(g2);
			}
			catch (GraphFormatException e)
			{
				throw new GraphFormatRuntimeException(e.getMessage(),e);
			}
		}
	}
	
	@Override
	public void setX(GraphScalar gsX)
	{
		super.setX(gsX);
		placeholderX=null;
	}
	
	@Override
	public void setY(GraphScalar gsY)
	{
		super.setY(gsY);
		placeholderY=null;
	}
	
	@Override
	public void setX2(GraphScalar gsX)
	{
		super.setX2(gsX);
		placeholderX2=null;
	}
	
	@Override
	public void setY2(GraphScalar gsY)
	{
		super.setY2(gsY);
		placeholderY2=null;
	}
	
	@Override
	public void setLineColour(Color c)
	{
		super.setLineColour(c);
		placeholderLineColour=null;
	}
	
	@Override
	public void setLineWidth(int i) throws GraphFormatException
	{
		super.setLineWidth(i);
		placeholderLineWidth=null;
	}
	
	/**
	 * Set state flag that indicates if this line is displayed (true) or not (false)
	 * @param display State flag that indicates if this line is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display = display;
		placeholderDisplay=null;
	}
	
	/**
	 * Get first corner of line
	 * @return First corner of line
	 */
	@Override
	protected GraphPoint getCorner1()
	{
		return r==null?super.getCorner1():r.gp1;
	}
	
	/**
	 * Get second corner of line
	 * @return Second corner of line
	 */
	@Override
	protected GraphPoint getCorner2()
	{
		return r==null?super.getCorner2():r.gp2;
	}
	
	/**
	 * Get outline colour
	 * @return Outline colour
	 */
	@Override
	protected Color getLineColour()
	{
		return r==null?super.getLineColour():r.cLine;
	}
	
	/**
	 * Get line width in pixels
	 * @return Line width in pixels
	 */
	@Override
	protected int getLineWidth()
	{
		return r==null?super.getLineWidth():r.iLineWidth;
	}
	
	/**
	 * Get state flag that indicates if this line is displayed or not
	 * @return true if this line is displayed, false otherwise
	 */
	protected boolean isDisplay()
	{
		boolean bDisplay=display;
		if (placeholderDisplay!=null)
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String sDisplay=sq.applyPlaceholders(placeholderDisplay);
				if (!sDisplay.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					if (sDisplay.equals("yes"))
					{
						bDisplay=true;
					}
					else if (sDisplay.equals("no"))
					{
						bDisplay=false;
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<line>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<line>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
