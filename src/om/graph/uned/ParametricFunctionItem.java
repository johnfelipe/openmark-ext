package om.graph.uned;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import om.OmDeveloperException;
import om.graph.GraphFormatException;
import om.graph.GraphPoint;
import om.graph.GraphRange;
import om.graph.World;
import om.helper.uned.NumericTester;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 27-09-2011 - dballestin
/** 
 * Draws (x,y)=f(t) functions in the graph space.<br/><br/>
 * This version of parametric function item allows to use placeholders with its attributes, 
 * except with ID attribute.
 */
public class ParametricFunctionItem extends om.graph.ParametricFunctionItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for function colour */
	private String placeholderColour=null;
	
	/** Placeholder for line width in pixels */
	private String placeholderLineWidth=null;
	
	/** Placeholder for number of points at which t is evaluated */
	private String placeholderSteps=null;
	
	/** Placeholder for maximum extent of function */
	private String placeholderMaxT=null;
	
	/** Placeholder for minimum extent of function */
	private String placeholderMinT=null;
	
	/** Placeholder for formula for the X co-ordinate */
	private String placeholderX=null;
	
	/** Placeholder for formula for the Y co-ordinate */
	private String placeholderY=null;
	
	/** Placeholder for state flag that indicates if this function is displayed or not */
	private String placeholderDisplay=null;
	
	/** Formula for the X co-ordinate */
	private String x=null;
	
	/** Formula for the Y co-ordinate */
	private String y=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this parametric function is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public GraphRange gr;
		public int iSteps;
		public double dLineWidth;
		public Color cLine;
		public String x;
		public String y;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			gr=getRange();
			iSteps=getSteps();
			dLineWidth=getLineWidth();
			cLine=getColour();
			x=getX();
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
					error.append("<parametricFunction>: Placeholder ");
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
							error.append("<parametricFunction>: Unexpected number value for lineWidth: ");
							error.append(rLW);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<parametricFunction>: Placeholder ");
					error.append(placeholderLineWidth);
					error.append(" for lineWidth can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderSteps!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rS=sq.applyPlaceholders(placeholderSteps);
					if (!rS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer steps=new Integer(rS);
							iSteps=steps.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<parametricFunction>: Unexpected number value for steps: ");
							error.append(rS);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<parametricFunction>: Placeholder ");
					error.append(placeholderSteps);
					error.append(" for steps can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMaxT!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rT=sq.applyPlaceholders(placeholderMaxT);
					if (!rT.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							double maxT=Double.parseDouble(rT);
							gr=new GraphRange(gr.getMin(),maxT);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<parametricFunction>: Unexpected number value for maxT: ");
							error.append(rT);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<parametricFunction>: Placeholder ");
					error.append(placeholderMaxT);
					error.append(" for maxT can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMinT!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rT=sq.applyPlaceholders(placeholderMinT);
					if (!rT.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							double minT=Double.parseDouble(rT);
							gr=new GraphRange(minT,gr.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<parametricFunction>: Unexpected number value for minT: ");
							error.append(rT);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<parametricFunction>: Placeholder ");
					error.append(placeholderMinT);
					error.append(" for minT can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderX!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					x=sq.applyPlaceholders(placeholderX);
				}
				else
				{
					x=placeholderX;
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
	public ParametricFunctionItem(World w) throws GraphFormatException
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
		if (attribute.equalsIgnoreCase("colour"))
		{
			placeholderColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("lineWidth"))
		{
			placeholderLineWidth=placeholder;
		}
		else if (attribute.equalsIgnoreCase("steps"))
		{
			placeholderSteps=placeholder;
		}
		else if (attribute.equalsIgnoreCase("maxT"))
		{
			placeholderMaxT=placeholder;
		}
		else if (attribute.equalsIgnoreCase("minT"))
		{
			placeholderMinT=placeholder;
		}
		else if (attribute.equalsIgnoreCase("x"))
		{
			placeholderX=placeholder;
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
			error.append("<parametricFunction>: There is no placeholder support implemented for ");
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
	public void setSteps(int iSteps)
	{
		super.setSteps(iSteps);
		placeholderSteps=null;
	}
	
	@Override
	public void setMaxT(double d)
	{
		super.setMaxT(d);
		placeholderMaxT=null;
	}
	
	@Override
	public void setMinT(double d)
	{
		super.setMinT(d);
		placeholderMinT=null;
	}
	
	/**
	 * Set formula for the X co-ordinate.
	 * @param x Formula for the X co-ordinate
	 */
	public void setX(String x)
	{
		this.x=x;
		placeholderX=null;
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
	 * Set state flag that indicates if this parametric function is displayed (true) or not (false)
	 * @param display State flag that indicates if this parametric function is displayed or not
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
	
	@Override
	protected int getSteps()
	{
		return r==null?super.getSteps():r.iSteps;
	}
	
	/**
	 * Get the actual function implementation.<br/><br/>
	 * If not exists a function implementation and at least one of the x or y attributes has been set try 
	 * to generate one using the formulaes of those attributes to get the values of X and Y co-ordinates 
	 * respectively.<br/><br/>
	 * If one of the x or y attributes has not been set the associated co-ordinate will be considered 0.0
	 * <br/><br/>
	 * If an error occurs while resolving the formulaes a GraphFormatRuntimeException with a 
	 * descriptive error message will be thrown.
	 * @return Actual function implementation
	 */
	protected Function getFunction()
	{
		Function f=null;
		if (f==null && (getX()!=null || getY()!=null))
		{
			f=new Function()
			{
				@Override
				public GraphPoint f(double t)
				{
					Map<String,Double> variables=new HashMap<String,Double>();
					variables.put("t",new Double(t));
					double x=0.0;
					if (getX()!=null)
					{
						try
						{
							x=NumericTester.expression(getX(),variables,getQuestion());
							if (Double.isNaN(x) || Double.isInfinite(x))
							{
								throw new GraphFormatRuntimeException(
										"<parametricFunction> Value for X co-ordinate is not a valid number");
							}
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<parametricFunction> x: ");
							error.append(e.getMessage());
							throw new GraphFormatRuntimeException(error.toString());
						}
					}
					double y=0.0;
					if (getY()!=null)
					{
						try
						{
							y=NumericTester.expression(getY(),variables,getQuestion());
							if (Double.isNaN(y) || Double.isInfinite(y))
							{
								throw new GraphFormatRuntimeException(
										"<parametricFunction> Value for Y co-ordinate is not a valid number");
							}
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<parametricFunction> y: ");
							error.append(e.getMessage());
							throw new GraphFormatRuntimeException(error.toString());
						}
					}
					return new GraphPoint(x,y);
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
	 * Get formula for the X co-ordinate
	 * @return Formula for the X co-ordinate
	 */
	protected String getX()
	{
		return r==null?x:r.x;
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
	 * Get state flag that indicates if this parametric function is displayed or not
	 * @return true if this parametric function is displayed, false otherwise
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
						error.append("<parametricFunction>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<parametricFunction>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
