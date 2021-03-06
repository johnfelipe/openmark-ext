/* OpenMark online assessment system
   Copyright (C) 2007 The Open University

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package om.graph;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Base class for standard X and Y axes. Contains all properties that are
 * shared between the two axes (i.e. most of them).
 * <p>
 * See property descriptions for details, but axis features are:
 * <ul>
 * <li>A simple axis line with any range (start and end) and other co-ordinate
 *   (X position for Y axis, Y position for X). Colour can be altered.</li>
 * <li>Optional major and optional minor tickmarks at any frequency. These can
 *   optionally be omitted within a given range of the axis. Their size and
 *   colour can be altered along with their position (on the + or - side or
 *   both sides of the axis).</li>
 * <li>Optional numbering at any frequency. This can optionally be omitted
 *   within a given range of the axis. Font and colour can be altered. The
 *   margin between the axis (or any protruding tickmarks) and numbers can
 *   be altered. Numbers may be rotated 90 degrees in either direction, or
 *   left horizontal.</li>
 * <li>An optional text label. Font and colour can be altered. The label is
 *   always rotated for Y axis and left alone for X axis, but its direction
 *   of rotation can be changed.</li>
 * </ul>
 */
public abstract class AxisItemBase extends GraphItem
{
	protected AxisItemBase(World w) throws GraphFormatException
	{
		super(w);
	}

	/** Label text */
	private String sLabel=null;

	/** Tick spacing (0.0 = no ticks) */
	private double dMajorTicks=0.0,dMinorTicks=0.0;

	/** Number spacing */
	private double dNumbers=0.0;

	/** Range within which to not draw numbers or ticks */
	private GraphRange grOmitNumbers=null,grOmitTicks=null;

	/** Tick size */
	private int iMajorTickSize=4,iMinorTickSize=2;

	/** Which side of axis (+1=positive, 0=both, -1=negative side) ticks go on) */
	private int iTickSide=1;

	/** Colours */
	private Color cLine=null,cLabel=null,cNumbers=null;

	/** Fonts */
	private Font fNumbers,fLabel;

	/** Rotation */
	private boolean bRotateNumbers,bRotateFlip;

	/** Margins */
	private int iNumbersMargin=2,iLabelMargin=2;

	/** Constant for use with setTickSide */
	public final static String AXISSIDE_POSITIVE="+";
	/** Constant for use with setTickSide */
	public final static String AXISSIDE_NEGATIVE="-";
	/** Constant for use with setTickSide */
	public final static String AXISSIDE_BOTH="both";

	/** Default stroke used for axis lines */
	private final static BasicStroke DEFAULTSTROKE=new BasicStroke(1.0f);

	/**
	 * Sets the axis label text.
	 * @param sLabel Label text
	 */
	public void setLabel(String sLabel)
	{
		this.sLabel=sLabel;
	}

	// UNED: 28-09-2011 - dballestin - Changed to access fields with getters and setters
	@Override
	public void init() throws GraphFormatException
	{
		if (getLineColour()==null)
		{
			setLineColour(getWorld().convertColour("fg"));
		}
		if (getLabelColour()==null)
		{
			setLabelColour(getWorld().convertColour("fg"));
		}
		if (getNumbersColour()==null)
		{
			setNumbersColour(getWorld().convertColour("fg"));
		}
		if (getNumbersFont()==null)
		{
			setNumbersFont(getWorld().getDefaultFont(true));
		}
		if (getLabelFont()==null)
		{
			setLabelFont(getWorld().getDefaultFont(false));
		}
	}
	
	// UNED: 26-08-2011 - dballestin - Changed to access fields with getters
	@Override
	public void paint(Graphics2D g2)
	{
		GraphRange grAxis=getRange();

		g2.setColor(getLineColour());
		g2.setStroke(DEFAULTSTROKE);
		paintLine(g2,grAxis);

		int iTickSpace=0;
		if(getMajorTicks()!=0.0)
		{
			// Display all ticks in range except within omit range
			for(
				double dPoint=(int)(grAxis.getMin() / getMajorTicks()) * getMajorTicks();
				dPoint<=grAxis.getMax();
				dPoint+=getMajorTicks())
			{
				if(getOmitTicks()!=null && getOmitTicks().inRange(dPoint))
					continue;
				if(getTickSide()>-1) paintTick(g2,dPoint,getMajorTickSize(),true);
				if(getTickSide()<1)
				{
					paintTick(g2,dPoint,getMajorTickSize(),false);
					iTickSpace=Math.max(iTickSpace,getMajorTickSize());
				}
			}
		}

		if(getMinorTicks()!=0.0)
		{
			// Display all ticks in range except within omit range
			for(
				double dPoint=(int)(grAxis.getMin() / getMinorTicks()) * getMinorTicks();
				dPoint<=grAxis.getMax();
				dPoint+=getMinorTicks())
			{
				if(getOmitTicks()!=null && getOmitTicks().inRange(dPoint))
					continue;

				// Was there a major tick here?
				if(getMajorTicks()!=0.0 &&
					Math.abs(Math.round(dPoint / getMajorTicks()) * getMajorTicks() - dPoint)<getMinorTicks()/10.0)
					continue;

				if(getTickSide()>-1) paintTick(g2,dPoint,getMinorTickSize(),true);
				if(getTickSide()<1)
				{
					paintTick(g2,dPoint,getMinorTickSize(),false);
					iTickSpace=Math.max(iTickSpace,getMinorTickSize());
				}
			}
		}

		int iSpaceUsed=0;
		if(getNumbers()!=0.0)
		{
			g2.setColor(getNumbersColour());
			g2.setFont(getNumbersFont());
			// Get ready to display numbers with right # of decimal places
			NumberFormat nf=NumberFormat.getNumberInstance(Locale.UK);
			int iDigits=getDecimalPlaces(getNumbers());
			nf.setMaximumFractionDigits(iDigits);
			nf.setMinimumFractionDigits(iDigits);

			// Display all numbers in range except within omit range
			for(
				double dPoint=(int)(grAxis.getMin() / getNumbers()) * getNumbers();
				dPoint<=grAxis.getMax();
				dPoint+=getNumbers())
			{
				if(getOmitNumbers()!=null && getOmitNumbers().inRange(dPoint))
					continue;

				int iSpace=paintAxisText(g2,dPoint,nf.format(dPoint),
					isRotateNumbers(),isRotateFlip(),true,getNumbersMargin()+iTickSpace);
				iSpaceUsed=Math.max(iSpaceUsed,iSpace);
			}
		}

		if(getLabel()!=null)
		{
			g2.setColor(getLabelColour());
			g2.setFont(getLabelFont());

			paintAxisText(g2,(grAxis.getMin()+grAxis.getMax())/2.0,getLabel(),
				isLabelRotated(),isRotateFlip(),false,getLabelMargin()+iSpaceUsed);
		}

	}


	/**
	 * @param d Number to check
	 * @return The number of decimal places needed to display this number
	 *   (maximum 6)
	 */
	private static int getDecimalPlaces(double d)
	{
		NumberFormat nf=NumberFormat.getNumberInstance(Locale.UK);
		nf.setMaximumFractionDigits(6);
		nf.setMinimumFractionDigits(6);
		String s=nf.format(d);

		int i;
		for(i=0;i<6;i++)
		{
			if(!(s.charAt(s.length()-i-1)=='0')) break;
		}
		return 6-i;
	}

	/**
	 * Obtains overall axis range
	 * @return Range for axis
	 */
	protected abstract GraphRange getRange();

	/**
	 * Paints the main axis line.
	 * @param g2 Target graphics context
	 * @param gr Start and end points of line
	 */
	protected abstract void paintLine(Graphics2D g2,GraphRange gr);

	/**
	 * Paints some text. Use current font/colour in context.
	 * @param g2 Target graphics context
	 * @param dPoint Position
	 * @param sText Number as string
	 * @param bRotate Whether to rotate the number in line with axis
	 * @param bFlip Whether to rotate it the other direction from normal
	 * @param iExtraOffset Space from axis to use
	 * @param bNumbers If true, assumes text is numbers (for spacing)
	 * @return Space from axis used (including offset) to paint this text
	 */
	protected abstract int paintAxisText(
		Graphics2D g2,double dPoint,String sText,boolean bRotate,boolean bFlip,
		boolean bNumbers,int iExtraOffset);

	/** @return True if label should be rotated */
	protected abstract boolean isLabelRotated();

	/**
	 * Paints a tick mark.
	 * @param g2 Target graphics context
	 * @param dPoint Position
	 * @param iSize Size in pixels
	 * @param bPositiveSide If true, paint on pos. side, otherwise negative
	 */
	protected abstract void paintTick(Graphics2D g2,double dPoint,int iSize,boolean bPositiveSide);

	/**
	 * Sets the tick spacing.
	 * @param sTicks Tick specification: "1.0" = major ticks every 1,
	 *   "1.0,0.5" = major ticks and minor ticks. "" or "0.0" = no ticks
	 * @throws GraphFormatException
	 */
	public void setTicks(String sTicks) throws GraphFormatException
	{
		try
		{
			int iComma=sTicks.indexOf(",");
			if(sTicks.length()==0)
			{
				dMajorTicks=0.0;
				dMinorTicks=0.0;
			}
			else if(iComma==-1)
			{
				dMajorTicks=Double.parseDouble(sTicks);
				dMinorTicks=0.0;
			}
			else
			{
				dMajorTicks=Double.parseDouble(sTicks.substring(0,iComma));
				dMinorTicks=Double.parseDouble(sTicks.substring(iComma+1));
			}
		}
		catch(NumberFormatException nfe)
		{
			throw new GraphFormatException(
				"<*axis>: Invalid tick specification: "+sTicks);
		}
	}

	/**
	 * Sets number spacing.
	 * @param dNumbers Number spacing, 0.0 for no numbers
	 */
	public void setNumbers(double dNumbers)
	{
		this.dNumbers=dNumbers;
	}

	/**
	 * Sets the range in which numbers aren't drawn (e.g. set to 0)
	 * @param gr Desired range
	 */
	public void setOmitNumbers(GraphRange gr)
	{
		this.grOmitNumbers=gr;
	}

	/**
	 * Sets the range in which ticks aren't drawn (e.g. set to 0)
	 * @param gr Desired range
	 */
	public void setOmitTicks(GraphRange gr)
	{
		this.grOmitTicks=gr;
	}


	/**
	 * Sets size of major ticks.
	 * @param iSize Size in pixels
	 */
	public void setMajorTickSize(int iSize)
	{
		this.iMajorTickSize=iSize;
	}
	/**
	 * Sets size of minor ticks.
	 * @param iSize Size in pixels
	 */
	public void setMinorTickSize(int iSize)
	{
		this.iMinorTickSize=iSize;
	}

	/**
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c New line colour
	 */
	public void setLineColour(Color c)
	{
		this.cLine=c;
	}

	/**
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c New numbers colour
	 */
	public void setNumbersColour(Color c)
	{
		cNumbers=c;
	}

	/**
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c New label colour
	 */
	public void setLabelColour(Color c)
	{
		cLabel=c;
	}

	/**
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c New colour for all three elements of the axis
	 */
	public void setColour(Color c)
	{
		cLine=c;
		cNumbers=c;
		cLabel=c;
	}

	/**
	 * @param f Font to use for numbers
	 */
	public void setNumbersFont(Font f)
	{
		fNumbers=f;
	}

	/**
	 * @param f Font to use for label
	 */
	public void setLabelFont(Font f)
	{
		fLabel=f;
	}

	/**
	 * Sets the side of the axis that ticks go on.
	 * @param s In code, use an AXISSIDE_x constant; in XML, "+" is positive
	 *   side (default), "-" is negative side, "both" is both sides.
	 * @throws GraphFormatException
	 */
	public void setTickSide(String s) throws GraphFormatException
	{
		if(s.equals(AXISSIDE_POSITIVE))
			iTickSide=1;
		else if(s.equals(AXISSIDE_NEGATIVE))
			iTickSide=-1;
		else if(s.equals(AXISSIDE_BOTH))
			iTickSide=0;
		else throw new GraphFormatException(
			"<*axis>: Unexpected tickSide value (must be +, -, or both): " +s);
	}

	/**
	 * Sets rotation for numbers.
	 * @param b If true, numbers are rotated in line with axis (but see
	 *   setRotateFlip)
	 */
	public void setRotateNumbers(boolean b)
	{
		bRotateNumbers=b;
	}

	/**
	 * Chooses alternate rotation direction for numbers (and labels, on Y axis)
	 * @param b If true, numbers are rotated the other way around
	 */
	public void setRotateFlip(boolean b)
	{
		bRotateFlip=b;
	}

	/** @param i Margin between numbers and axis or tickmarks. Negative margins OK */
	public void setNumbersMargin(int i)
	{
		iNumbersMargin=i;
	}

	/** @param i Margin between label and numbers (or axis/tickmarks). Negative margins OK */
	public void setLabelMargin(int i)
	{
		iLabelMargin=i;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get axis label text
	 * @return Axis label text
	 */
	protected String getLabel()
	{
		return sLabel;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get major tick spacing (0.0 = no major ticks)
	 * @return Major tick spacing
	 */
	protected double getMajorTicks()
	{
		return dMajorTicks;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get minor tick spacing (0.0 = no minor ticks)
	 * @return Minor tick spacing
	 */
	protected double getMinorTicks()
	{
		return dMinorTicks;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get number spacing (0.0 = no numbers)
	 * @return Number spacing
	 */
	protected double getNumbers()
	{
		return dNumbers;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get the range in which numbers aren't drawn
	 * @return Range in which numbers aren't drawn
	 */
	protected GraphRange getOmitNumbers()
	{
		return grOmitNumbers;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get the range in which ticks aren't drawn
	 * @return Range in which ticks aren't drawn
	 */
	protected GraphRange getOmitTicks()
	{
		return grOmitTicks;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get size of major ticks in pixels
	 * @return Size of major ticks in pixels
	 */
	protected int getMajorTickSize()
	{
		return iMajorTickSize;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get size of minor ticks in pixels
	 * @return Size of minor ticks in pixels
	 */
	protected int getMinorTickSize()
	{
		return iMinorTickSize;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get the side of the axis that ticks go on; 1 is positive side (default), 
	 * -1 is negative side, 0 is both sides.
	 * @return Side of the axis that ticks go on
	 */
	protected int getTickSide()
	{
		return iTickSide;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get line color
	 * @return Line color
	 */
	protected Color getLineColour()
	{
		return cLine;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get label color
	 * @return Label color
	 */
	protected Color getLabelColour()
	{
		return cLabel;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get numbers color
	 * @return Numbers color
	 */
	protected Color getNumbersColour()
	{
		return cNumbers;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get font to use for numbers
	 * @return Font to use for numbers
	 */
	protected Font getNumbersFont()
	{
		return fNumbers;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get font to use for label
	 * @return Font to use for label
	 */
	protected Font getLabelFont()
	{
		return fLabel;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get rotation for numbers
	 * @return Rotation for numbers
	 */
	protected boolean isRotateNumbers()
	{
		return bRotateNumbers;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get rotation direction for numbers (and labels, on Y axis), by default false, 
	 * if true numbers are rotated the other way around
	 * @return Rotation direction for numbers (and labels, on Y axis)
	 */
	protected boolean isRotateFlip()
	{
		return bRotateFlip;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get margin between numbers and axis or tickmarks
	 * @return Margin between numbers and axis or tickmarks
	 */
	protected int getNumbersMargin()
	{
		return iNumbersMargin;
	}
	
	// UNED: 26-08-2011 - dballestin
	/**
	 * Get margin between label and numbers (or axis/tickmarks)
	 * @return Margin between label and numbers (or axis/tickmarks)
	 */
	protected int getLabelMargin()
	{
		return iLabelMargin;
	}
}
