package om.graph.uned;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import om.OmDeveloperException;
import om.graph.GraphFormatException;
import om.graph.GraphRange;
import om.graph.World;
import om.helper.uned.NumericTester;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

//UNED: 26-09-2011 - dballestin
/** 
 * Fill an area in the graph space with color calculated with a function f(x,y).<br/><br/>
 * This version of colour field item allows to use placeholders with its attributes, except with ID attribute.
 */
public class ColourFieldItem extends om.graph.ColourFieldItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for size of blocks in pixels */
	private String placeholderBlockSize=null;
	
	/** Placeholder for new maximum extent of function, X co-ordinate */
	private String placeholderMaxX=null;
	
	/** Placeholder for new minimum extent of function, X co-ordinate */
	private String placeholderMinX=null;
	
	/** Placeholder for new maximum extent of function, Y co-ordinate */
	private String placeholderMaxY=null;
	
	/** Placeholder for new minimum extent of function, Y co-ordinate */
	private String placeholderMinY=null;
	
	/** Placeholder for formula for the red component of color */
	private String placeholderRed=null;
	
	/** Placeholder for formula for the green component of color */
	private String placeholderGreen=null;
	
	/** Placeholder for formula for the blue component of color */
	private String placeholderBlue=null;
	
	/** Placeholder for state flag that indicates if this colour field is displayed or not */
	private String placeholderDisplay=null;
	
	/** Formula for the red component of color */
	private String red=null;
	
	/** Formula for the green component of color */
	private String green=null;
	
	/** Formula for the blue component of color */
	private String blue=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this colour field is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public GraphRange grX;
		public GraphRange grY;
		public int iBlockSize;
		public String red;
		public String green;
		public String blue;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			grX=getXRange();
			grY=getYRange();
			iBlockSize=getBlockSize();
			red=getRed();
			green=getGreen();
			blue=getBlue();
			if (placeholderBlockSize!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rBS=sq.applyPlaceholders(placeholderBlockSize);
					if (!rBS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer blockSize=new Integer(rBS);
							iBlockSize=blockSize.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField>: Unexpected number value for blockSize: ");
							error.append(rBS);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<colourField>: Placeholder ");
					error.append(placeholderBlockSize);
					error.append(" for blockSize can not be replaced");
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
							double maxX=Double.parseDouble(rX);
							grX=new GraphRange(grX.getMin(),maxX);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField>: Unexpected number value for maxX: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<colourField>: Placeholder ");
					error.append(placeholderMaxX);
					error.append(" for maxX can not be replaced");
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
							double minX=Double.parseDouble(rX);
							grX=new GraphRange(minX,grX.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField>: Unexpected number value for minX: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<colourField>: Placeholder ");
					error.append(placeholderMinX);
					error.append(" for minX can not be replaced");
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
							double maxY=Double.parseDouble(rY);
							grY=new GraphRange(grY.getMin(),maxY);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField>: Unexpected number value for maxY: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<colourField>: Placeholder ");
					error.append(placeholderMaxY);
					error.append(" for maxY can not be replaced");
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
							double minY=Double.parseDouble(rY);
							grY=new GraphRange(minY,grY.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField>: Unexpected number value for minY: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<colourField>: Placeholder ");
					error.append(placeholderMinY);
					error.append(" for minY can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderRed!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					red=sq.applyPlaceholders(placeholderRed);
				}
				else
				{
					red=placeholderRed;
				}
			}
			if (placeholderGreen!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					green=sq.applyPlaceholders(placeholderGreen);
				}
				else
				{
					green=placeholderGreen;
				}
			}
			if (placeholderBlue!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					blue=sq.applyPlaceholders(placeholderBlue);
				}
				else
				{
					blue=placeholderBlue;
				}
			}
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public ColourFieldItem(World w) throws GraphFormatException
	{
		super(w);
		initUNEDColourField();
	}
	
	@Override
	protected void initColourField()
	{
	}
	
	protected void initUNEDColourField()
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
		if (attribute.equalsIgnoreCase("blockSize"))
		{
			placeholderBlockSize=placeholder;
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
		else if (attribute.equalsIgnoreCase("red"))
		{
			placeholderRed=placeholder;
		}
		else if (attribute.equalsIgnoreCase("green"))
		{
			placeholderGreen=placeholder;
		}
		else if (attribute.equalsIgnoreCase("blue"))
		{
			placeholderBlue=placeholder;
		}
		else if (attribute.equalsIgnoreCase("display"))
		{
			placeholderDisplay=placeholder;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<colourField>: There is no placeholder support implemented for ");
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
	public void setBlockSize(int iBlockSize)
	{
		super.setBlockSize(iBlockSize);
		placeholderBlockSize=null;
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
	 * Set formula for the red component of color.
	 * @param red Formula for the red component of color
	 */
	public void setRed(String red)
	{
		this.red=red;
		placeholderRed=null;
	}
	
	/**
	 * Set formula for the green component of color.
	 * @param green Formula for the green component of color
	 */
	public void setGreen(String green)
	{
		this.green=green;
		placeholderGreen=null;
	}
	
	/**
	 * Set formula for the blue component of color.
	 * @param blue Formula for the blue component of color
	 */
	public void setBlue(String blue)
	{
		this.blue=blue;
		placeholderBlue=null;
	}
	
	/**
	 * Set state flag that indicates if this colour field is displayed (true) or not (false)
	 * @param display State flag that indicates if this colour field is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display = display;
		placeholderDisplay=null;
	}
	
	/**
	 * Get the actual function implementation.<br/><br/>
	 * If not exists a function implementation and at least one of the red, green or blue attributes has been 
	 * set try to generate one using the formulaes of those attributes to get the values of the red, green 
	 * and blue components of color respectively.<br/><br/>
	 * If some of the red, green or blue attributes has not been set the associated colors components will be 
	 * considered 0.0<br/><br/>
	 * If an error occurs while resolving the formulaes (or results are outside of the range 0.0 to 1.0) 
	 * a GraphFormatRuntimeException with a descriptive error message will be thrown.
	 * @return Actual function implementation
	 */
	@Override
	protected Function getFunction()
	{
		Function f=super.getFunction();
		if (f==null && (getRed()!=null || getGreen()!=null || getBlue()!=null))
		{
			f=new Function()
			{
				@Override
				public Color f(double x,double y)
				{
					Map<String,Double> variables=new HashMap<String,Double>();
					variables.put("x",new Double(x));
					variables.put("y",new Double(y));
					float red=0.0f;
					if (getRed()!=null)
					{
						try
						{
							red=(float)NumericTester.expression(getRed(),variables,getQuestion());
							if (Float.isNaN(red) || Float.isInfinite(red) || red<0.0f || red>1.0f)
							{
								throw new GraphFormatRuntimeException(
										"<colourField> Value for red component is outside of the range 0.0 to 1.0");
							}
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField> red: ");
							error.append(e.getMessage());
							throw new GraphFormatRuntimeException(error.toString());
						}
					}
					float green=0.0f;
					if (getGreen()!=null)
					{
						try
						{
							green=(float)NumericTester.expression(getGreen(),variables,getQuestion());
							if (Float.isNaN(green) || Float.isInfinite(green) || green<0.0f || green>1.0f)
							{
								throw new GraphFormatRuntimeException(
										"<colourField> Value for green component is outside of the range 0.0 to 1.0");
							}
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField> green: ");
							error.append(e.getMessage());
							throw new GraphFormatRuntimeException(error.toString());
						}
					}
					float blue=0.0f;
					if (getBlue()!=null)
					{
						try
						{
							blue=(float)NumericTester.expression(getBlue(),variables,getQuestion());
							if (Float.isNaN(blue) || Float.isInfinite(blue) || blue<0.0f || blue>1.0f)
							{
								throw new GraphFormatRuntimeException(
										"<colourField> Value for blue component is outside of the range 0.0 to 1.0");
							}
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<colourField> blue: ");
							error.append(e.getMessage());
							throw new GraphFormatRuntimeException(error.toString());
						}
					}
					return new Color(red,green,blue);
				}
			};
		}
		return f;
	}
	
	@Override
	protected GraphRange getXRange()
	{
		return r==null?super.getXRange():r.grX;
	}
	
	@Override
	protected GraphRange getYRange()
	{
		return r==null?super.getYRange():r.grY;
	}
	
	@Override
	protected int getBlockSize()
	{
		return r==null?super.getBlockSize():r.iBlockSize;
	}
	
	/**
	 * Get formula for the red component of color
	 * @return Formula for the red component of color
	 */
	protected String getRed()
	{
		return r==null?red:r.red;
	}
	
	/**
	 * Get formula for the green component of color
	 * @return Formula for the green component of color
	 */
	protected String getGreen()
	{
		return r==null?green:r.green;
	}
	
	/**
	 * Get formula for the blue component of color
	 * @return Formula for the blue component of color
	 */
	protected String getBlue()
	{
		return r==null?blue:r.blue;
	}
	
	/**
	 * Get state flag that indicates if this colour field is displayed or not
	 * @return true if this colour field is displayed, false otherwise
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
						error.append("<colourField>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<colourField>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
