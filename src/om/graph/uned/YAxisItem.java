package om.graph.uned;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import om.graph.AxisItemBase;
import om.graph.GraphFormatException;
import om.graph.GraphRange;
import om.graph.World;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

//UNED: 26-08-2011 - dballestin
/**
 * A Y axis. See {@link AxisItemBase} for most of the properties and a more detailed explanation.<br/><br/>
 * This version of yAxis item allows to use placeholders with its attributes, except with ID attribute.
 */
public class YAxisItem extends om.graph.YAxisItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Placeholder for axis label text */
	private String placeholderLabel;
	
	/** Placeholder for the tick spacing */
	private String placeholderTicks;
	
	/** Placeholder for number spacing */
	private String placeholderNumbers;
	
	/** Placeholder for the range in which numbers aren't drawn */
	private String placeholderOmitNumbers;
	
	/** Placeholder for the range in which ticks aren't drawn */
	private String placeholderOmitTicks;
	
	/** Placeholder for size of major ticks in pixels */
	private String placeholderMajorTickSize;
	
	/** Placeholder for size of minor ticks in pixels */
	private String placeholderMinorTickSize;
	
	/** Placeholder for line color */
	private String placeholderLineColour;
	
	/** Placeholder for numbers color */
	private String placeholderNumbersColour;
	
	/** Placeholder for label color */
	private String placeholderLabelColour;
	
	/** Placeholder for font to use for numbers */
	private String placeholderNumbersFont;
	
	/** Placeholder for font to use for label */
	private String placeholderLabelFont;
	
	/** Placeholder for the side of the axis that ticks go on */
	private String placeholderTickSide;
	
	/** Placeholder for rotation for numbers */
	private String placeholderRotateNumbers;
	
	/** Placeholder for rotation direction for numbers (and labels, on Y axis) */
	private String placeholderRotateFlip;
	
	/** Placeholder for margin between numbers and axis or tickmarks */
	private String placeholderNumbersMargin;
	
	/** Placeholder for margin between label and numbers (or axis/tickmarks) */
	private String placeholderLabelMargin;
	
	/** Placeholder for maximum extent of axis */
	private String placeholderMaxY;
	
	/** Placeholder for minimum extent of axis */
	private String placeholderMinY;
	
	/** Placeholder for X co-ordinate of axis */
	private String placeholderX;
	
	/** Placeholder for label rotation */
	private String placeholderRotateLabel;
	
	/** Placeholder for state flag that indicates if this vertical axis is displayed or not */
	private String placeholderDisplay=null;
	
	/** Replaced attributes */
	private ReplacedAttributes r=null;
	
	/** State flag that indicates if this vertical axis is displayed or not */
	private boolean display=true;
	
	/**
	 * Helper inner class to get attributes taking account placeholders
	 */
	protected class ReplacedAttributes
	{
		public String sLabel;
		public double dMajorTicks;
		public double dMinorTicks;
		public double dNumbers;
		public GraphRange grOmitNumbers;
		public GraphRange grOmitTicks;
		public int iMajorTickSize;
		public int iMinorTickSize;
		public int iTickSide;
		public Color cLine;
		public Color cLabel;
		public Color cNumbers;
		public Font fNumbers;
		public Font fLabel;
		public boolean bRotateNumbers;
		public boolean bRotateFlip;
		public int iNumbersMargin;
		public int iLabelMargin;
		public GraphRange gr;
		public double dX;
		public boolean bRotateLabel;
		
		public ReplacedAttributes() throws GraphFormatException
		{
			sLabel=getLabel();
			dMajorTicks=getMajorTicks();
			dMinorTicks=getMinorTicks();
			dNumbers=getNumbers();
			grOmitNumbers=getOmitNumbers();
			grOmitTicks=getOmitTicks();
			iMajorTickSize=getMajorTickSize();
			iMinorTickSize=getMinorTickSize();
			iTickSide=getTickSide();
			cLine=getLineColour();
			cLabel=getLabelColour();
			cNumbers=getNumbersColour();
			fNumbers=getNumbersFont();
			fLabel=getLabelFont();
			bRotateNumbers=isRotateNumbers();
			bRotateFlip=isRotateFlip();
			iNumbersMargin=getNumbersMargin();
			iLabelMargin=getLabelMargin();
			gr=getRange();
			dX=getX();
			bRotateLabel=isLabelRotated();
			if (placeholderLabel!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					sLabel=sq.applyPlaceholders(placeholderLabel);
				}
				else
				{
					sLabel=placeholderLabel;
				}
			}
			if (placeholderTicks!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rT=sq.applyPlaceholders(placeholderTicks);
					if (!rT.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							int iComma=rT.indexOf(",");
							if (iComma==rT.length()-1)
							{
								StringBuffer error=new StringBuffer();
								error.append("<yAxis>: Invalid tick specification: ");
								error.append(rT);
								throw new GraphFormatException(error.toString());
							}
							else if(rT.length()==0)
							{
								dMajorTicks=0.0;
								dMinorTicks=0.0;
							}
							else if(iComma==-1)
							{
								dMajorTicks=Double.parseDouble(rT);
								dMinorTicks=0.0;
							}
							else
							{
								dMajorTicks=Double.parseDouble(rT.substring(0,iComma));
								dMinorTicks=Double.parseDouble(rT.substring(iComma+1));
							}
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Invalid tick specification: ");
							error.append(rT);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderTicks);
					error.append(" for ticks can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderNumbers!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rN=sq.applyPlaceholders(placeholderNumbers);
					if (!rN.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double n=new Double(rN);
							dNumbers=n;
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for numbers: ");
							error.append(rN);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderNumbers);
					error.append(" for numbers can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderOmitNumbers!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rON=sq.applyPlaceholders(placeholderOmitNumbers);
					if (!rON.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						GraphRange grOmN=new GraphRange(rON);
						grOmitNumbers=grOmN;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderOmitNumbers);
					error.append(" for omitNumbers can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderOmitTicks!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rOT=sq.applyPlaceholders(placeholderOmitTicks);
					if (!rOT.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						GraphRange grOmT=new GraphRange(rOT);
						grOmitTicks=grOmT;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderOmitTicks);
					error.append(" for omitTicks can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMajorTickSize!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rMTS=sq.applyPlaceholders(placeholderMajorTickSize);
					if (!rMTS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer mts=new Integer(rMTS);
							iMajorTickSize=mts.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for majorTickSize: ");
							error.append(rMTS);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderMajorTickSize);
					error.append(" for majorTickSize can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMinorTickSize!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rMTS=sq.applyPlaceholders(placeholderMinorTickSize);
					if (!rMTS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer mts=new Integer(rMTS);
							iMinorTickSize=mts.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for minorTickSize: ");
							error.append(rMTS);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderMinorTickSize);
					error.append(" for minorTickSize can not be replaced");
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
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderLineColour);
					error.append(" for lineColour can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderNumbersColour!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rNC=sq.applyPlaceholders(placeholderNumbersColour);
					if (!rNC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Color c=getWorld().convertColour(rNC);
						cNumbers=c;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderNumbersColour);
					error.append(" for numbersColour can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderLabelColour!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rLC=sq.applyPlaceholders(placeholderLabelColour);
					if (!rLC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Color c=getWorld().convertColour(rLC);
						cLabel=c;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderLabelColour);
					error.append(" for labelColour can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderNumbersFont!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rNF=sq.applyPlaceholders(placeholderNumbersFont);
					if (!rNF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Font f=getWorld().convertFont(rNF);
						fNumbers=f;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderNumbersFont);
					error.append(" for numbersFont can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderLabelFont!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rLF=sq.applyPlaceholders(placeholderLabelFont);
					if (!rLF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						Font f=getWorld().convertFont(sq.applyPlaceholders(placeholderLabelFont));
						fLabel=f;
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderLabelFont);
					error.append(" for labelFont can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderTickSide!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rTS=sq.applyPlaceholders(placeholderTickSide);
					if (!rTS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						if (rTS.equals(AXISSIDE_POSITIVE))
						{
							iTickSide=1;
						}
						else if (rTS.equals(AXISSIDE_NEGATIVE))
						{
							iTickSide=-1;
						}
						else if (rTS.equals(AXISSIDE_BOTH))
						{
							iTickSide=0;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected tickSide value (must be ");
							error.append(AXISSIDE_POSITIVE);
							error.append(", ");
							error.append(AXISSIDE_NEGATIVE);
							error.append(", or ");
							error.append(AXISSIDE_BOTH);
							error.append("): ");
							error.append(rTS);
							throw new GraphFormatException(error.toString());							
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderTickSide);
					error.append(" for tickSide can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderRotateNumbers!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rRN=sq.applyPlaceholders(placeholderRotateNumbers);
					if (!rRN.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						if (rRN.equals("yes"))
						{
							bRotateNumbers=true;
						}
						else if (rRN.equals("no"))
						{
							bRotateNumbers=false;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected boolean value for rotateNumbers: ");
							error.append(rRN);
							throw new GraphFormatException(error.toString());							
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderRotateNumbers);
					error.append(" for rotateNumbers can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderRotateFlip!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rRF=sq.applyPlaceholders(placeholderRotateFlip);
					if (!rRF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						if (rRF.equals("yes"))
						{
							bRotateFlip=true;
						}
						else if (rRF.equals("no"))
						{
							bRotateFlip=false;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected boolean value for rotateFlip: ");
							error.append(rRF);
							throw new GraphFormatException(error.toString());							
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderRotateFlip);
					error.append(" for rotateFlip can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderNumbersMargin!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rNM=sq.applyPlaceholders(placeholderNumbersMargin);
					if (!rNM.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer nm=new Integer(rNM);
							iNumbersMargin=nm.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for numbersMargin: ");
							error.append(rNM);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderNumbersMargin);
					error.append(" for numbersMargin can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderLabelMargin!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rLM=sq.applyPlaceholders(placeholderLabelMargin);
					if (!rLM.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Integer lm=new Integer(rLM);
							iLabelMargin=lm.intValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for labelMargin: ");
							error.append(rLM);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderLabelMargin);
					error.append(" for labelMargin can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderMaxY!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rMY=sq.applyPlaceholders(placeholderMaxY);
					if (!rMY.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double maxY=new Double(rMY);
							gr=new GraphRange(gr.getMin(),maxY.doubleValue());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for maxY: ");
							error.append(rMY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
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
					String rMY=sq.applyPlaceholders(placeholderMinY);
					if (!rMY.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double minY=new Double(rMY);
							gr=new GraphRange(minY.doubleValue(),gr.getMax());
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for minY: ");
							error.append(rMY);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderMinY);
					error.append(" for minY can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderX!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rX=sq.applyPlaceholders(placeholderX);
					if (!rX.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						try
						{
							Double x=new Double(rX);
							dX=x.doubleValue();
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected number value for x: ");
							error.append(rX);
							throw new GraphFormatException(error.toString());
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderX);
					error.append(" for x can not be replaced");
					error.append(" because placeholders have not been initialized");
					throw new GraphFormatException(error.toString());
				}
			}
			if (placeholderRotateLabel!=null)
			{
				if (getQuestion().isPlaceholdersInitialized())
				{
					String rRL=sq.applyPlaceholders(placeholderRotateLabel);
					if (!rRL.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
					{
						if (rRL.equals("yes"))
						{
							bRotateLabel=true;
						}
						else if (rRL.equals("no"))
						{
							bRotateLabel=false;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<yAxis>: Unexpected boolean value for rotateLabel: ");
							error.append(rRL);
							throw new GraphFormatException(error.toString());							
						}
					}
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<yAxis>: Placeholder ");
					error.append(placeholderRotateLabel);
					error.append(" for rotateLabel can not be replaced");
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
	public YAxisItem(World w) throws GraphFormatException
	{
		super(w);
		initUNEDYAxis();
	}
	
	@Override
	protected void initYAxis()
	{
	}
	
	protected void initUNEDYAxis()
	{
		// Init range to full size
		om.graph.uned.World w=(om.graph.uned.World)getWorld();
		double maxY=Double.MAX_VALUE;
		double minY=Double.MIN_VALUE;
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
		setRange(new GraphRange(minY,maxY));
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
		if (attribute.equalsIgnoreCase("label"))
		{
			placeholderLabel=placeholder;
		}
		else if (attribute.equalsIgnoreCase("ticks"))
		{
			placeholderTicks=placeholder;
		}
		else if (attribute.equalsIgnoreCase("numbers"))
		{
			placeholderNumbers=placeholder;
		}
		else if (attribute.equalsIgnoreCase("omitNumbers"))
		{
			placeholderOmitNumbers=placeholder;
		}
		else if (attribute.equalsIgnoreCase("omitTicks"))
		{
			placeholderOmitTicks=placeholder;
		}
		else if (attribute.equalsIgnoreCase("majorTickSize"))
		{
			placeholderMajorTickSize=placeholder;
		}
		else if (attribute.equalsIgnoreCase("minorTickSize"))
		{
			placeholderMinorTickSize=placeholder;
		}
		else if (attribute.equalsIgnoreCase("lineColour"))
		{
			placeholderLineColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("numbersColour"))
		{
			placeholderNumbersColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("labelColour"))
		{
			placeholderLabelColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("colour"))
		{
			placeholderLineColour=placeholder;
			placeholderNumbersColour=placeholder;
			placeholderLabelColour=placeholder;
		}
		else if (attribute.equalsIgnoreCase("numbersFont"))
		{
			placeholderNumbersFont=placeholder;
		}
		else if (attribute.equalsIgnoreCase("labelFont"))
		{
			placeholderLabelFont=placeholder;
		}
		else if (attribute.equalsIgnoreCase("tickSide"))
		{
			placeholderTickSide=placeholder;
		}
		else if (attribute.equalsIgnoreCase("rotateNumbers"))
		{
			placeholderRotateNumbers=placeholder;
		}
		else if (attribute.equalsIgnoreCase("rotateFlip"))
		{
			placeholderRotateFlip=placeholder;
		}
		else if (attribute.equalsIgnoreCase("numbersMargin"))
		{
			placeholderNumbersMargin=placeholder;
		}
		else if (attribute.equalsIgnoreCase("labelMargin"))
		{
			placeholderLabelMargin=placeholder;
		}
		else if (attribute.equalsIgnoreCase("maxY"))
		{
			placeholderMaxY=placeholder;
		}
		else if (attribute.equalsIgnoreCase("minY"))
		{
			placeholderMinY=placeholder;
		}
		else if (attribute.equalsIgnoreCase("X"))
		{
			placeholderX=placeholder;
		}
		else if (attribute.equalsIgnoreCase("rotateLabel"))
		{
			placeholderRotateLabel=placeholder;
		}
		else if (attribute.equalsIgnoreCase("display"))
		{
			placeholderDisplay=placeholder;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<yAxis>: There is no placeholder support implemented for ");
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
	public void setLabel(String sLabel)
	{
		super.setLabel(sLabel);
		placeholderLabel=null;
	}
	
	@Override
	public void setTicks(String sTicks) throws GraphFormatException
	{
		if (sTicks.charAt(sTicks.length()-1)==',')
		{
			StringBuffer error=new StringBuffer();
			error.append("<*axis>: Invalid tick specification: ");
			error.append(sTicks);
			throw new GraphFormatException(error.toString());
		}
		else
		{
			super.setTicks(sTicks);
			placeholderTicks=null;
		}
	}
	
	@Override
	public void setNumbers(double dNumbers)
	{
		super.setNumbers(dNumbers);
		placeholderNumbers=null;
	}
	
	@Override
	public void setOmitNumbers(GraphRange gr)
	{
		super.setOmitNumbers(gr);
		placeholderOmitNumbers=null;
	}
	
	@Override
	public void setOmitTicks(GraphRange gr)
	{
		super.setOmitTicks(gr);
		placeholderOmitTicks=null;
	}
	
	@Override
	public void setMajorTickSize(int iSize)
	{
		super.setMajorTickSize(iSize);
		placeholderMajorTickSize=null;
	}
	
	@Override
	public void setMinorTickSize(int iSize)
	{
		super.setMinorTickSize(iSize);
		placeholderMinorTickSize=null;
	}
	
	@Override
	public void setLineColour(Color c)
	{
		super.setLineColour(c);
		placeholderLineColour=null;
	}
	
	@Override
	public void setNumbersColour(Color c)
	{
		super.setNumbersColour(c);
		placeholderNumbersColour=null;
	}
	
	@Override
	public void setLabelColour(Color c)
	{
		super.setLabelColour(c);
		placeholderLabelColour=null;
	}
	
	@Override
	public void setColour(Color c)
	{
		super.setColour(c);
		placeholderLineColour=null;
		placeholderNumbersColour=null;
		placeholderLabelColour=null;
	}
	
	@Override
	public void setNumbersFont(Font f)
	{
		super.setNumbersFont(f);
		placeholderNumbersFont=null;
	}
	
	@Override
	public void setLabelFont(Font f)
	{
		super.setLabelFont(f);
		placeholderLabelFont=null;
	}
	
	@Override
	public void setTickSide(String s) throws GraphFormatException
	{
		super.setTickSide(s);
		placeholderTickSide=null;
	}
	
	@Override
	public void setRotateNumbers(boolean b)
	{
		super.setRotateNumbers(b);
		placeholderRotateNumbers=null;
	}
	
	@Override
	public void setRotateFlip(boolean b)
	{
		super.setRotateFlip(b);
		placeholderRotateFlip=null;
	}
	
	@Override
	public void setNumbersMargin(int i)
	{
		super.setNumbersMargin(i);
		placeholderNumbersMargin=null;
	}
	
	@Override
	public void setLabelMargin(int i)
	{
		super.setLabelMargin(i);
		placeholderLabelMargin=null;
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
	
	@Override
	public void setx(double d)
	{
		super.setx(d);
		placeholderX=null;
	}

	@Override
	public void setRotateLabel(boolean bRotateLabel)
	{
		super.setRotateLabel(bRotateLabel);
		placeholderRotateLabel=null;
	}
	
	/**
	 * Set state flag that indicates if this vertical axis is displayed (true) or not (false)
	 * @param display State flag that indicates if this vertical axis is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display = display;
		placeholderDisplay=null;
	}
	
	/**
	 * Get axis label text
	 * @return Axis label text
	 */
	@Override
	protected String getLabel()
	{
		return r==null?super.getLabel():r.sLabel;
	}
	
	/**
	 * Get major tick spacing (0.0 = no major ticks)
	 * @return Major tick spacing
	 */
	@Override
	protected double getMajorTicks()
	{
		return r==null?super.getMajorTicks():r.dMajorTicks;
	}
	
	/**
	 * Get minor tick spacing (0.0 = no minor ticks)
	 * @return Minor tick spacing
	 */
	@Override
	protected double getMinorTicks()
	{
		return r==null?super.getMinorTicks():r.dMinorTicks;
	}
	
	/**
	 * Get number spacing (0.0 = no numbers)
	 * @return Number spacing
	 */
	@Override
	protected double getNumbers()
	{
		return r==null?super.getNumbers():r.dNumbers;
	}
	
	/**
	 * Get the range in which numbers aren't drawn
	 * @return Range in which numbers aren't drawn
	 */
	@Override
	protected GraphRange getOmitNumbers()
	{
		return r==null?super.getOmitNumbers():r.grOmitNumbers;
	}
	
	/**
	 * Get the range in which ticks aren't drawn
	 * @return Range in which ticks aren't drawn
	 */
	@Override
	protected GraphRange getOmitTicks()
	{
		return r==null?super.getOmitTicks():r.grOmitTicks;
	}
	
	/**
	 * Get size of major ticks in pixels
	 * @return Size of major ticks in pixels
	 */
	@Override
	protected int getMajorTickSize()
	{
		return r==null?super.getMajorTickSize():r.iMajorTickSize;
	}
	
	/**
	 * Get size of minor ticks in pixels
	 * @return Size of minor ticks in pixels
	 */
	@Override
	protected int getMinorTickSize()
	{
		return r==null?super.getMinorTickSize():r.iMinorTickSize;
	}
	
	/**
	 * Get the side of the axis that ticks go on; 1 is positive side (default), 
	 * -1 is negative side, 0 is both sides.
	 * @return Side of the axis that ticks go on
	 */
	@Override
	protected int getTickSide()
	{
		return r==null?super.getTickSide():r.iTickSide;
	}
	
	/**
	 * Get line color
	 * @return Line color
	 */
	@Override
	protected Color getLineColour()
	{
		return r==null?super.getLineColour():r.cLine;
	}
	
	/**
	 * Get label color
	 * @return Label color
	 */
	@Override
	protected Color getLabelColour()
	{
		return r==null?super.getLabelColour():r.cLabel;
	}
	
	/**
	 * Get numbers color
	 * @return Numbers color
	 */
	@Override
	protected Color getNumbersColour()
	{
		return r==null?super.getNumbersColour():r.cNumbers;
	}
	
	/**
	 * Get font to use for numbers
	 * @return Font to use for numbers
	 */
	@Override
	protected Font getNumbersFont()
	{
		return r==null?super.getNumbersFont():r.fNumbers;
	}
	
	/**
	 * Get font to use for label
	 * @return Font to use for label
	 */
	@Override
	protected Font getLabelFont()
	{
		return r==null?super.getLabelFont():r.fLabel;
	}
	
	/**
	 * Get rotation for numbers
	 * @return Rotation for numbers
	 */
	@Override
	protected boolean isRotateNumbers()
	{
		return r==null?super.isRotateNumbers():r.bRotateNumbers;
	}
	
	/**
	 * Get rotation direction for numbers (and labels, on Y axis), by default false, 
	 * if true numbers are rotated the other way around
	 * @return Rotation direction for numbers (and labels, on Y axis)
	 */
	@Override
	protected boolean isRotateFlip()
	{
		return r==null?super.isRotateFlip():r.bRotateFlip;
	}
	
	/**
	 * Get margin between numbers and axis or tickmarks
	 * @return Margin between numbers and axis or tickmarks
	 */
	@Override
	protected int getNumbersMargin()
	{
		return r==null?super.getNumbersMargin():r.iNumbersMargin;
	}
	
	/**
	 * Get margin between label and numbers (or axis/tickmarks)
	 * @return Margin between label and numbers (or axis/tickmarks)
	 */
	@Override
	protected int getLabelMargin()
	{
		return r==null?super.getLabelMargin():r.iLabelMargin;
	}
	
	@Override
	protected GraphRange getRange()
	{
		return r==null?super.getRange():r.gr;
	}
	
	@Override
	protected boolean isLabelRotated()
	{
		return r==null?super.isLabelRotated():r.bRotateLabel;
	}
	
	/**
	 * Get X co-ordinate of axis
	 * @return X co-ordinate of axis
	 */
	@Override
	protected double getX()
	{
		return r==null?super.getX():r.dX;
	}
	
	/**
	 * Get state flag that indicates if this vertical axis is displayed or not
	 * @return true if this vertical axis is displayed, false otherwise
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
						error.append("<yAxis>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<yAxis>: Placeholder ");
				error.append(placeholderDisplay);
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
