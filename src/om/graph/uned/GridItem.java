package om.graph.uned;

import java.awt.Color;
import java.awt.Graphics2D;

import om.graph.GraphFormatException;
import om.graph.GraphRange;
import om.graph.World;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 25-08-2011 - dballestin
/** 
 * Draws a grid.<br/><br/>
 * This version of grid item allows to use placeholders with its attributes, except with ID attribute.
 */
public class GridItem extends om.graph.GridItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for color */
	private String placeholderColour;
	
	/** Placeholder for horizontal grid spacing */
	private String placeholderXSpacing;
	
	/** Placeholder for vertical grid spacing */
	private String placeholderYSpacing;
	
	/** Placeholder for opacity of major and minor gridlines */
	private String placeholderOpacity;
	
	/** Placeholder for maximum extent of horizontal grid lines */
	private String placeholderMaxX;
	
	/** Placeholder for minimum extent of horizontal grid lines */
	private String placeholderMinX;
	
	/** Placeholder for maximum extent of vertical grid lines */
	private String placeholderMaxY;
	
	/** Placeholder for minimum extent of vertical grid lines */
	private String placeholderMinY;
	
	/** Placeholder for state flag that indicates if this grid is displayed or not */
	private String placeholderDisplay=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this grid is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public Color cLine;
		public MajorMinor mmXSpacing;
		public MajorMinor mmYSpacing;
		public MajorMinor mmOpacity;
		public GraphRange grX;
		public GraphRange grY;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			cLine=getColour();
			mmXSpacing=getXSpacing();
			mmYSpacing=getYSpacing();
			mmOpacity=getOpacity();
			grX=getXRange();
			grY=getYRange();
			if (placeholderColour!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rC=sq.applyPlaceholders(placeholderColour);
					if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Color c=getWorld().convertColour(rC);
						cLine=c;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderColour);
					error.append(" for colour cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderXSpacing!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rXS=sq.applyPlaceholders(placeholderXSpacing);
					if (!rXS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						MajorMinor mmX=convertMajorMinor(rXS);
						mmXSpacing=mmX;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderXSpacing);
					error.append(" for xSpacing cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderYSpacing!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rYS=sq.applyPlaceholders(placeholderYSpacing);
					if (!rYS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						MajorMinor mmY=convertMajorMinor(rYS);
						mmYSpacing=mmY;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderYSpacing);
					error.append(" for ySpacing cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderOpacity!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rOp=sq.applyPlaceholders(placeholderOpacity);
					if (!rOp.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						MajorMinor mmOp=convertMajorMinor(rOp);
						if (mmOp.dMinor==0.0)
						{
							mmOp.dMinor=0.5*mmOp.dMajor;
						}
						mmOpacity=mmOp;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderOpacity);
					error.append(" for opacity cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMaxX!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rX=sq.applyPlaceholders(placeholderMaxX);
					if (!rX.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double maxX=new Double(rX);
							grX=new GraphRange(grX.getMin(),maxX.doubleValue());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<grid>: Unexpected number value for maxX: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderMaxX);
					error.append(" for maxX cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMinX!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rX=sq.applyPlaceholders(placeholderMinX);
					if (!rX.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double minX=new Double(rX);
							grX=new GraphRange(minX.doubleValue(),grX.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<grid>: Unexpected number value for minX: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderMinX);
					error.append(" for minX cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMaxY!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rY=sq.applyPlaceholders(placeholderMaxY);
					if (!rY.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double maxY=new Double(sq.applyPlaceholders(placeholderMaxY));
							grY=new GraphRange(grY.getMin(),maxY.doubleValue());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<grid>: Unexpected number value for maxY: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderMaxY);
					error.append(" for maxY cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMinY!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rY=sq.applyPlaceholders(placeholderMinY);
					if (!rY.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double minY=new Double(rY);
							grY=new GraphRange(minY.doubleValue(),grY.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<grid>: Unexpected number value for minY: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<grid>: Placeholder ");
					error.append(placeholderMinY);
					error.append(" for minY cannot be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public GridItem(World w) throws GraphFormatException
	{
		super(w);
		initUNEDGrid();
	}
	
	@Override
	protected void initGrid()
	{
		MajorMinor mmOpacity=super.getOpacity();
		mmOpacity.dMajor=0.25;
		mmOpacity.dMinor=mmOpacity.dMajor/2.0;
	}
	
	protected void initUNEDGrid()
	{
		om.graph.uned.World w=(om.graph.uned.World)getWorld();
		double maxX=Double.MAX_VALUE;
		double minX=Double.MIN_VALUE;
		double maxY=Double.MAX_VALUE;
		double minY=Double.MIN_VALUE;
		String placeholderXRight=w.getPlaceholder(om.graph.uned.World.ATTRIBUTE_XRIGHT);
		if (placeholderXRight==null)
		{
			maxX=w.getRightX();
		}
		else
		{
			placeholderMaxX=placeholderXRight;
		}
		String placeholderXLeft=w.getPlaceholder(om.graph.uned.World.ATTRIBUTE_XLEFT);
		if (placeholderXLeft==null)
		{
			minX=w.getLeftX();
		}
		else
		{
			placeholderMinX=placeholderXLeft;
		}
		setXRange(new GraphRange(minX,maxX));
		String placeholderYBottom=w.getPlaceholder(om.graph.uned.World.ATTRIBUTE_YBOTTOM);
		if (placeholderYBottom==null)
		{
			maxY=w.getBottomY();
		}
		else
		{
			placeholderMaxY=placeholderYBottom;
		}
		String placeholderYTop=w.getPlaceholder(om.graph.uned.World.ATTRIBUTE_YTOP);
		if (placeholderYTop==null)
		{
			minY=w.getTopY();
		}
		else
		{
			placeholderMinY=placeholderYTop;
		}
		setYRange(new GraphRange(minY,maxY));
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
		if (attribute.equalsIgnoreCase("colour"))
		{
			placeholderColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("XSpacing"))
		{
			placeholderXSpacing=placeholder;
		}
		else if (attribute.equalsIgnoreCase("YSpacing"))
		{
			placeholderYSpacing=placeholder;
		}
		else if (attribute.equalsIgnoreCase("opacity"))
		{
			placeholderOpacity=placeholder;
		}
		else if (attribute.equalsIgnoreCase("maxX"))
		{
			placeholderMaxX=placeholder;
		}
		else if (attribute.equalsIgnoreCase("minX"))
		{
			placeholderMinX=placeholder;
		}
		else if (attribute.equalsIgnoreCase("maxY"))
		{
			placeholderMaxY=placeholder;
		}
		else if (attribute.equalsIgnoreCase("minY"))
		{
			placeholderMinY=placeholder;
		}
		else if (attribute.equalsIgnoreCase("display"))
		{
			placeholderDisplay=placeholder;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<grid>: There is no placeholder support implemented for ");
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
	public void setColour(Color c)
	{
		super.setColour(c);
		placeholderColour=null;
	}
	
	@Override
	public void setXSpacing(String sSpacing) throws GraphFormatException
	{
		super.setXSpacing(sSpacing);
		placeholderXSpacing=null;
	}
	
	@Override
	public void setYSpacing(String sSpacing) throws GraphFormatException
	{
		super.setYSpacing(sSpacing);
		placeholderYSpacing=null;
	}
	
	@Override
	public void setOpacity(String sOpacity) throws GraphFormatException
	{
		super.setOpacity(sOpacity);
		placeholderOpacity=null;
	}
	
	@Override
	public void setMaxX(double d)
	{
		super.setMaxX(d);
		placeholderMaxX=null;
	}
	
	@Override
	public void setMinX(double d)
	{
		super.setMinX(d);
		placeholderMinX=null;
	}
	
	@Override
	public void setMaxY(double d)
	{
		super.setMaxY(d);
		placeholderMaxY=null;
	}
	
	@Override
	public void setMinY(double d)
	{
		super.setMinY(d);
		placeholderMinY=null;
	}
	
	/**
	 * Set state flag that indicates if this grid is displayed (true) or not (false)
	 * @param display State flag that indicates if this grid is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display = display;
		placeholderDisplay=null;
	}
	
	/**
	 * Get horizontal grid spacing
	 * @return Horizontal grid spacing
	 */
	@Override
	protected MajorMinor getXSpacing()
	{
		return r==null?super.getXSpacing():r.mmXSpacing;
	}
	
	/**
	 * Get vertical grid spacing
	 * @return Vertical grid spacing
	 */
	@Override
	protected MajorMinor getYSpacing()
	{
		return r==null?super.getYSpacing():r.mmYSpacing;
	}
	
	/**
	 * Get opacity of major and minor gridlines
	 * @return Opacity of major and minor gridlines
	 */
	@Override
	protected MajorMinor getOpacity()
	{
		return r==null?super.getOpacity():r.mmOpacity;
	}
	
	/**
	 * Get horizontal range of grid lines
	 * @return Horizontal range of grid lines
	 */
	@Override
	protected GraphRange getXRange()
	{
		return r==null?super.getXRange():r.grX;
	}
	
	/**
	 * Get vertical range of grid lines
	 * @return Vertical range of grid lines
	 */
	@Override
	protected GraphRange getYRange()
	{
		return r==null?super.getYRange():r.grY;
	}
	
	/**
	 * Get color for grid lines
	 * @return Color for grid lines
	 */
	@Override
	protected Color getColour()
	{
		return r==null?super.getColour():r.cLine;
	}
	
	/**
	 * Get state flag that indicates if this grid is displayed or not
	 * @return true if this grid is displayed, false otherwise
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
						error.append("<grid>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<grid>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
