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

// UNED: 26-09-2011 - dballestin
/** 
 * Draws f(x) functions in the graph space.<br/><br/>
 * This version of function item allows to use placeholders with its attributes, except with ID attribute.
 */
public class FunctionItem extends om.graph.FunctionItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for function colour */
	private String placeholderColour=null;
	
	/** Placeholder for line width in pixels */
	private String placeholderLineWidth=null;
	
	/** Placeholder for maximum extent of function */
	private String placeholderMaxX=null;
	
	/** Placeholder for minimum extent of function */
	private String placeholderMinX=null;
	
	/** Placeholder for formula for the Y co-ordinate */
	private String placeholderY=null;
	
	/** Placeholder for state flag that indicates if this function is displayed or not */
	private String placeholderDisplay=null;
	
	/** Formula for the Y co-ordinate */
	private String y=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this colour field is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public GraphRange gr;
		public double dLineWidth;
		public Color cLine;
		public String y;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			gr=getRange();
			dLineWidth=getLineWidth();
			cLine=getColour();
			y=getY();
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
					error.append("<function>: Placeholder ");
					error.append(placeholderColour);
					error.append(" for colour can not be replaced");
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
							Double lWidth=new Double(rLW);
							dLineWidth=lWidth.doubleValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<function>: Unexpected number value for lineWidth: ");
							error.append(rLW);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<function>: Placeholder ");
					error.append(placeholderLineWidth);
					error.append(" for lineWidth can not be replaced");
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
							gr=new GraphRange(gr.getMin(),maxX);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<function>: Unexpected number value for maxX: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<function>: Placeholder ");
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
							gr=new GraphRange(minX,gr.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<function>: Unexpected number value for minX: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<function>: Placeholder ");
					error.append(placeholderMinX);
					error.append(" for minX can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderY!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					y=sq.applyPlaceholders(placeholderY);
				}
				else
				{
					y=placeholderY;
				}
			}
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public FunctionItem(World w) throws GraphFormatException
	{
		super(w);
		initUNEDFunction();
	}
	
	@Override
	protected void initFunction()
	{
	}
	
	protected void initUNEDFunction()
	{
		om.graph.uned.World w=(om.graph.uned.World)getWorld();
		double maxX=Double.MAX_VALUE;
		double minX=Double.MIN_VALUE;
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
		setRange(new GraphRange(minX,maxX));
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
		else if (attribute.equalsIgnoreCase("lineWidth"))
		{
			placeholderLineWidth=placeholder;
		}
		else if (attribute.equalsIgnoreCase("maxX"))
		{
			placeholderMaxX=placeholder;
		}
		else if (attribute.equalsIgnoreCase("minX"))
		{
			placeholderMinX=placeholder;
		}
		else if (attribute.equalsIgnoreCase("y"))
		{
			placeholderY=placeholder;
		}
		else if (attribute.equalsIgnoreCase("display"))
		{
			placeholderDisplay=placeholder;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<function>: There is no placeholder support implemented for ");
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
	public void setLineWidth(double d) throws GraphFormatException
	{
		super.setLineWidth(d);
		placeholderLineWidth=null;
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
	
	/**
	 * Set formula for the Y co-ordinate.
	 * @param y Formula for the Y co-ordinate
	 */
	public void setY(String y)
	{
		this.y=y;
		placeholderY=null;
	}
	
	/**
	 * Set state flag that indicates if this function is displayed (true) or not (false)
	 * @param display State flag that indicates if this function is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display=display;
		placeholderDisplay=null;
	}
	
	@Override
	protected GraphRange getRange()
	{
		return r==null?super.getRange():r.gr;
	}
	
	/**
	 * Get the actual function implementation.<br/><br/>
	 * If not exists a function implementation and y attribute has been set try to generate one using the 
	 * formula of that attribute to get the value of Y co-ordinate.<br/><br/>
	 * If an error occurs while resolving the formula a GraphFormatRuntimeException with a descriptive 
	 * error message will be thrown.
	 * @return Actual function implementation
	 */
	@Override
	protected Function getFunction()
	{
		Function f=super.getFunction();
		if (f==null && getY()!=null)
		{
			f=new Function()
			{
				@Override
				public double f(double x)
				{
					Map<String,Double> variables=new HashMap<String,Double>();
					variables.put("x",new Double(x));
					double y=0.0;
					try
					{
						y=NumericTester.expression(getY(),variables,getQuestion());
						if (Double.isNaN(y) || Double.isInfinite(y))
						{
							throw new GraphFormatRuntimeException(
									"<function> Value for Y co-ordinate is not a valid number");
						}
					}
					catch (OmDeveloperException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<function> y: ");
						error.append(e.getMessage());
						throw new GraphFormatRuntimeException(error.toString());
					}
					return y;
				}
			};
		}
		return f;
	}
	
	@Override
	protected double getLineWidth()
	{
		return r==null?super.getLineWidth():r.dLineWidth;
	}
	
	@Override
	protected Color getColour()
	{
		return r==null?super.getColour():r.cLine;
	}
	
	/**
	 * Get formula for the Y co-ordinate
	 * @return Formula for the Y co-ordinate
	 */
	protected String getY()
	{
		return r==null?y:r.y;
	}
	
	/**
	 * Get state flag that indicates if this function is displayed or not
	 * @return true if this function is displayed, false otherwise
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
						error.append("<function>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<function>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
