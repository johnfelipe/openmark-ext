package om.graph.uned;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import om.graph.GraphFormatException;
import om.graph.GraphPoint;
import om.graph.GraphScalar;
import om.graph.World;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 25-08-2011 - dballestin
/** 
 * Draws text in the graph space.<br/><br/>
 * This version of text item allows to use placeholders with its attributes, except with ID attribute.
 */
public class TextItem extends om.graph.TextItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for origin point, X co-ordinate */
	private String placeholderX;
	
	/** Placeholder for origin point, Y co-ordinate */
	private String placeholderY;
	
	/** Placeholder for color */
	private String placeholderColour;
	
	/** Placeholder for angle of text */
	private String placeholderAngle;
	
	/** Placeholder for font to use */
	private String placeholderFont;
	
	/** Placeholder for text alignment */
	private String placeholderAlign;
	
	/** Placeholder for actual text */
	private String placeholderText;
	
	/** Placeholder for state flag that indicates if this text is displayed or not */
	private String placeholderDisplay=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this text is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public String sText;
		public GraphPoint gpOrigin;
		public double dAngle;
		public Font fText;
		public Color cText;
		public String sAlign;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			sText=getText();
			gpOrigin=getLocation();
			dAngle=getAngle();
			fText=getFont();
			cText=getColour();
			sAlign=getAlign();
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
							gpOrigin=gpOrigin.newX(gsX);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<text>: Unexpected number value for x: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<text>: Placeholder ");
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
							gpOrigin=gpOrigin.newY(gsY);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<text>: Unexpected number value for y: ");
							error.append(rY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<text>: Placeholder ");
					error.append(placeholderY);
					error.append(" for y can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderColour!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rC=sq.applyPlaceholders(placeholderColour);
					if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Color c=getWorld().convertColour(rC);
						cText=c;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<text>: Placeholder ");
					error.append(placeholderColour);
					error.append(" for colour can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderAngle!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rAn=sq.applyPlaceholders(placeholderAngle);
					if (!rAn.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double an=new Double(rAn);
							dAngle=an.doubleValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<text>: Unexpected number value for angle: ");
							error.append(rAn);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<text>: Placeholder ");
					error.append(placeholderAngle);
					error.append(" for angle can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderFont!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rF=sq.applyPlaceholders(placeholderFont);
					if (!rF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Font f=getWorld().convertFont(rF);
						fText=f;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<text>: Placeholder ");
					error.append(placeholderFont);
					error.append(" for font can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderAlign!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rAl=sq.applyPlaceholders(placeholderAlign);
					if (!rAl.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						String sAl=sq.applyPlaceholders(placeholderAlign);
						if (sAl.equals(ALIGN_CENTRE) || sAl.equals(ALIGN_LEFT) || 
								sAl.equals(ALIGN_RIGHT))
						{
							sAlign=sAl;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<text>: invalid align= value: ");
							error.append(sAl);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<text>: Placeholder ");
					error.append(placeholderAlign);
					error.append(" for align can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderText!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					sText=sq.applyPlaceholders(placeholderText);
				}
				else
				{
					sText=placeholderText;
				}
			}
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public TextItem(World w) throws GraphFormatException
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
		else if (attribute.equalsIgnoreCase("colour"))
		{
			placeholderColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("angle"))
		{
			placeholderAngle=placeholder;
		}
		else if (attribute.equalsIgnoreCase("font"))
		{
			placeholderFont=placeholder;
		}
		else if (attribute.equalsIgnoreCase("align"))
		{
			placeholderAlign=placeholder;
		}
		else if (attribute.equalsIgnoreCase("text"))
		{
			placeholderText=placeholder;
		}
		else if (attribute.equalsIgnoreCase("display"))
		{
			placeholderDisplay=placeholder;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<text>: There is no placeholder support implemented for ");
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
	public void setColour(Color c)
	{
		super.setColour(c);
		placeholderColour=null;
	}

	@Override
	public void setAngle(double dDegrees)
	{
		super.setAngle(dDegrees);
		placeholderAngle=null;
	}

	@Override
	public void setFont(Font f)
	{
		super.setFont(f);
		placeholderFont=null;
	}

	@Override
	public void setAlign(String s) throws GraphFormatException
	{
		super.setAlign(s);
		placeholderAlign=null;
	}

	@Override
	public void setText(String s)
	{
		super.setText(s);
		placeholderText=null;
	}
	
	/**
	 * Set state flag that indicates if this text is displayed (true) or not (false)
	 * @param display State flag that indicates if this text is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display = display;
		placeholderDisplay=null;
	}
	
	/**
	 * Get actual text
	 * @return Actual text
	 */
	@Override
	protected String getText()
	{
		return r==null?super.getText():r.sText;
	}
	
	/**
	 * Get location of text
	 * @return Location of text
	 */
	@Override
	protected GraphPoint getLocation()
	{
		return r==null?super.getLocation():r.gpOrigin;
	}
	
	/**
	 * Get angle
	 * @return Angle
	 */
	@Override
	protected double getAngle()
	{
		return r==null?super.getAngle():r.dAngle;
	}
	
	/**
	 * Get font
	 * @return Font
	 */
	@Override
	protected Font getFont()
	{
		return r==null?super.getFont():r.fText;
	}
	
	/**
	 * Get color
	 * @return Color
	 */
	@Override
	protected Color getColour()
	{
		return r==null?super.getColour():r.cText;
	}
	
	/**
	 * Get alignment
	 * @return Alignment
	 */
	@Override
	protected String getAlign()
	{
		return r==null?super.getAlign():r.sAlign;
	}
	
	/**
	 * Get state flag that indicates if this text is displayed or not
	 * @return true if this text is displayed, false otherwise
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
						error.append("<text>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<text>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
