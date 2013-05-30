package om.graph.uned;

import java.awt.Color;
import java.awt.Graphics2D;

import om.graph.GraphFormatException;
import om.graph.GraphPoint;
import om.graph.GraphScalar;
import om.graph.World;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 25-08-2011 - dballestin
/** 
 * Draws simple rectangles in the graph space.<br/><br/>
 * This version of rectangle item allows to use placeholders with its attributes, except with ID attribute.
 */
public class RectangleItem extends om.graph.RectangleItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for first corner of rectangle, X co-ordinate */
	private String placeholderX;
	
	/** Placeholder for first corner of rectangle, Y co-ordinate */
	private String placeholderY;
	
	/** Placeholder for second corner of rectangle, X co-ordinate */
	private String placeholderX2;
	
	/** Placeholder for second corner of rectangle, Y co-ordinate */
	private String placeholderY2;
	
	/** Placeholder for width of rectangle */
	private String placeholderWidth;
	
	/** Placeholder for height of rectangle */
	private String placeholderHeight;
	
	/** Placeholder for fill color */
	private String placeholderFillColour;
	
	/** Placeholder for outline color */
	private String placeholderLineColour;
	
	/** Placeholder for line width in pixels */
	private String placeholderLineWidth;
	
	/** Placeholder for state flag that indicates if this rectangle is displayed or not */
	private String placeholderDisplay=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this rectangle is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public GraphPoint gp1;
		public GraphPoint gp2;
		public GraphPoint gpSize;
		public Color cFill;
		public Color cLine;
		public int iLineWidth;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			gp1=getCorner1();
			gp2=getCorner2();
			gpSize=getSize();
			cFill=getFillColour();
			cLine=getLineColour();
			iLineWidth=getLineWidth();
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
							error.append("<rectangle>: Unexpected number value for x: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
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
							error.append("<rectangle>: Unexpected number value for y: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
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
					boolean notWidthHeight=gpSize==null;
					if (notWidthHeight && placeholderWidth!=null)
					{
						String rW=sq.applyPlaceholders(placeholderWidth);
						notWidthHeight=rW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE);
					}
					if (notWidthHeight && placeholderHeight!=null)
					{
						String rH=sq.applyPlaceholders(placeholderHeight);
						notWidthHeight=rH.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE);
					}
					if (notWidthHeight)
					{
						String rX2=sq.applyPlaceholders(placeholderX2);
						if (!rX2.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								GraphScalar gsX=new GraphScalar(rX2);
								if (gp2==null)
								{
									gp2=GraphPoint.ZERO;
								}
								gp2=gp2.newX(gsX);
							}
							catch (NumberFormatException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<rectangle>: Unexpected number value for x2: ");
								error.append(rX2);
								throw new GraphFormatException(error.toString());
							}
						}
					}
					else
					{
						throw new GraphFormatException(
								"<rectangle>: Can't specify both x2/y2 and width/height");
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
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
					boolean notWidthHeight=gpSize==null;
					if (notWidthHeight && placeholderWidth!=null)
					{
						String rW=sq.applyPlaceholders(placeholderWidth);
						notWidthHeight=rW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE);
					}
					if (notWidthHeight && placeholderHeight!=null)
					{
						String rH=sq.applyPlaceholders(placeholderHeight);
						notWidthHeight=rH.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE);
					}
					if (notWidthHeight)
					{
						String rY2=sq.applyPlaceholders(placeholderY2);
						if (!rY2.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								GraphScalar gsY=new GraphScalar(rY2);
								if (gp2==null)
								{
									gp2=GraphPoint.ZERO;
								}
								gp2=gp2.newY(gsY);
							}
							catch (NumberFormatException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<rectangle>: Unexpected number value for y2: ");
								error.append(rY2);
								throw new GraphFormatException(error.toString());
							}
						}
					}
					else
					{
						throw new GraphFormatException(
								"<rectangle>: Can't specify both x2/y2 and width/height");
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
					error.append(placeholderY2);
					error.append(" for y2 can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderWidth!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					if (gp2==null)
					{
						String rW=sq.applyPlaceholders(placeholderWidth);
						if (!rW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								GraphScalar gsW=new GraphScalar(rW);
								if (gpSize==null)
								{
									gpSize=GraphPoint.ZERO;
								}
								gpSize=gpSize.newX(gsW);
							}
							catch (NumberFormatException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<rectangle>: Unexpected number value for width: ");
								error.append(rW);
								throw new GraphFormatException(error.toString());
							}
						}
					}
					else
					{
						throw new GraphFormatException(
								"<rectangle>: Can't specify both x2/y2 and width/height");
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
					error.append(placeholderWidth);
					error.append(" for width can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderHeight!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					if (gp2==null)
					{
						String rH=sq.applyPlaceholders(placeholderHeight);
						if (!rH.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								GraphScalar gsH=new GraphScalar(rH);
								if (gpSize==null)
								{
									gpSize=GraphPoint.ZERO;
								}
								gpSize=gpSize.newY(gsH);
							}
							catch (NumberFormatException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<rectangle>: Unexpected number value for height: ");
								error.append(rH);
								throw new GraphFormatException(error.toString());
							}
						}
					}
					else
					{
						throw new GraphFormatException(
								"<rectangle>: Can't specify both x2/y2 and width/height");
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
					error.append(placeholderHeight);
					error.append(" for height can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderFillColour!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rFC=sq.applyPlaceholders(placeholderFillColour);
					if (!rFC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Color c=getWorld().convertColour(rFC);
						cFill=c;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
					error.append(placeholderFillColour);
					error.append(" for fillColour can not be replaced");
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
					error.append("<rectangle>: Placeholder ");
					error.append(placeholderLineColour);
					error.append(" for lineColour can not be replaced");
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
							error.append("<rectangle>: Unexpected number value for lineWidth: ");
							error.append(rLW);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<rectangle>: Placeholder ");
					error.append(placeholderLineWidth);
					error.append(" for lineWidth can not be replaced");
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
	public RectangleItem(World w) throws GraphFormatException
	{
		super(w);
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
		else if (attribute.equalsIgnoreCase("width"))
		{
			placeholderWidth=placeholder;
		}
		else if (attribute.equalsIgnoreCase("height"))
		{
			placeholderHeight=placeholder;
		}
		else if (attribute.equalsIgnoreCase("fillColour"))
		{
			placeholderFillColour=placeholder;
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
			error.append("<rectangle>: There is no placeholder support implemented for ");
			error.append(attribute);
			throw new AttributePlaceholderNotImplementedException(error.toString());
		}
	}
	
	@Override
	public void init() throws GraphFormatException
	{
		if (getSize()==null && getCorner2()==null && placeholderWidth==null && 
				placeholderHeight==null && placeholderX2==null && placeholderY2==null)
		{
			throw new GraphFormatException("<rectangle>: Must specify either x2/y2 or width/height");
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
	public void setX2(GraphScalar gsX) throws GraphFormatException
	{
		super.setX2(gsX);
		placeholderX2=null;
	}
	
	@Override
	public void setY2(GraphScalar gsY) throws GraphFormatException
	{
		super.setY2(gsY);
		placeholderY2=null;
	}
	
	@Override
	public void setWidth(GraphScalar gsW) throws GraphFormatException
	{
		super.setWidth(gsW);
		placeholderWidth=null;
	}
	
	@Override
	public void setHeight(GraphScalar gsH) throws GraphFormatException
	{
		super.setHeight(gsH);
		placeholderHeight=null;
	}
	
	@Override
	public void setFillColour(Color c)
	{
		super.setFillColour(c);
		placeholderFillColour=null;
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
	 * Set state flag that indicates if this rectangle is displayed (true) or not (false)
	 * @param display State flag that indicates if this rectangle is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display=display;
		placeholderDisplay=null;
	}
	
	/**
	 * Get first corner
	 * @return First corner
	 */
	@Override
	protected GraphPoint getCorner1()
	{
		return r==null?super.getCorner1():r.gp1;
	}
	
	/**
	 * Get second corner
	 * @return Second corner
	 */
	@Override
	protected GraphPoint getCorner2()
	{
		return r==null?super.getCorner2():r.gp2;
	}
	
	/**
	 * Get size (width and height)
	 * @return Size (width and height)
	 */
	@Override
	protected GraphPoint getSize()
	{
		return r==null?super.getSize():r.gpSize;
	}
	
	/**
	 * Get fill color
	 * @return Fill color
	 */
	@Override
	protected Color getFillColour()
	{
		return r==null?super.getFillColour():r.cFill;
	}
	
	/**
	 * Get outline color
	 * @return Outline color
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
	 * Get state flag that indicates if this rectangle is displayed or not
	 * @return true if this rectangle is displayed, false otherwise
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
						error.append("<rectangle>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<rectrangle>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
