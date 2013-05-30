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
 * Draws simple ellipses in the graph space.<br/><br/>
 * This version of ellipse item allows to use placeholders with its attributes, except with ID attribute.
 */
public class EllipseItem extends om.graph.EllipseItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for centre of ellipse, X co-ordinate */
	private String placeholderX=null;
	
	/** Placeholder for centre of ellipse, Y co-ordinate */
	private String placeholderY=null;
	
	/** Placeholder for width (horizontal diameter) of ellipse */
	private String placeholderWidth=null;
	
	/** Placeholder for height (vertical diameter) of ellipse */
	private String placeholderHeight=null;
	
	/** Placeholder for fill color */
	private String placeholderFillColour=null;
	
	/** Placeholder for outline color */
	private String placeholderLineColour=null;
	
	/** Placeholder for line width in pixels */
	private String placeholderLineWidth=null;
	
	/** Placeholder for state flag that indicates if this ellipse is displayed or not */
	private String placeholderDisplay=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this ellipse is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public GraphPoint gpCentre;
		public GraphPoint gpSize;
		public Color cFill;
		public Color cLine;
		public int iLineWidth;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			gpCentre=getCentre();
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
							gpCentre=gpCentre.newX(gsX);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<ellipse>: Unexpected number value for x: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<ellipse>: Placeholder ");
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
							gpCentre=gpCentre.newY(gsY);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<ellipse>: Unexpected number value for y: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<ellipse>: Placeholder ");
					error.append(placeholderY);
					error.append(" for y can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderWidth!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rW=sq.applyPlaceholders(placeholderWidth);
					if (!rW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							GraphScalar gsW=new GraphScalar(rW);
							if (gpSize==null)
							{
								gpSize=new GraphPoint(gsW,gsW);
							}
							else
							{
								gpSize=gpSize.newX(gsW);
							}
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<ellipse>: Unexpected number value for width: ");
							error.append(rW);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<ellipse>: Placeholder ");
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
					String rH=sq.applyPlaceholders(placeholderHeight);
					if (!rH.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							GraphScalar gsH=new GraphScalar(rH);
							if (gpSize==null)
							{
								gpSize=new GraphPoint(gsH,gsH);
							}
							else
							{
								gpSize=gpSize.newY(gsH);
							}
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<ellipse>: Unexpected number value for height: ");
							error.append(rH);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<ellipse>: Placeholder ");
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
					error.append("<ellipse>: Placeholder ");
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
					error.append("<ellipse>: Placeholder ");
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
							error.append("<ellipse>: Unexpected number value for lineWidth: ");
							error.append(rLW);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<ellipse>: Placeholder ");
					error.append(placeholderLineWidth);
					error.append(" for lineWidth can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (cLine!=null && iLineWidth==-1)
			{
				iLineWidth=1;
			}
			else if (iLineWidth>0 && cLine==null)
			{
				cLine=getWorld().convertColour("fg");
			}
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public EllipseItem(World w) throws GraphFormatException
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
			error.append("<ellipse>: There is no placeholder support implemented for ");
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
	 * Set state flag that indicates if this ellipse is displayed (true) or not (false)
	 * @param display State flag that indicates if this ellipse is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display = display;
		placeholderDisplay=null;
	}

	/**
	 * Get centre
	 * @return Centre
	 */
	@Override
	protected GraphPoint getCentre()
	{
		return r==null?super.getCentre():r.gpCentre;
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
	 * Get state flag that indicates if this ellipse is displayed or not
	 * @return true if this ellipse is displayed, false otherwise
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
						error.append("<ellipse>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<ellipse>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
