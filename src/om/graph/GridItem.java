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
import java.awt.geom.Line2D;

/** Draws a grid. */
public class GridItem extends GraphItem
{
	// UNED: 25-08-2011 - dballestin - Changed access modifier of this inner class to public so
	//                                 it would be possible to access it from other classes
	// UNED: 25-08-2011 - dballestin - Changed access modifiers of fields of this inner class to public
	//                                 so it would be possible to access them from other classes
	/** 
	 * Represents major and minor spacing intervals
	 */
	public static class MajorMinor
	{
		public double dMajor=0.0,dMinor=0.0;
	}

	/** Grid spacing */
	private MajorMinor mmXSpacing=new MajorMinor(),mmYSpacing=new MajorMinor();

	/** Opacity of major and minor gridlines */
	private MajorMinor mmOpacity=new MajorMinor();

	/** Range (x,y) of grid lines */
	private GraphRange grX,grY;

	/** Default stroke used for axis lines */
	private final static BasicStroke DEFAULTSTROKE=new BasicStroke(1.0f);

	// UNED: 27-09-2011 - dballestin - Initialization code moved to initGrid method
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public GridItem(World w) throws GraphFormatException
	{
		super(w);
		initGrid();
	}
	
	// UNED: 27-09-2011 - dballestin - Initialization code moved from constructor
	protected void initGrid()
	{
		// Init range to full size
		grX=new GraphRange(getWorld().getLeftX(),getWorld().getRightX());
		grY=new GraphRange(getWorld().getTopY(),getWorld().getBottomY());
		mmOpacity.dMajor=0.25;
		mmOpacity.dMinor=mmOpacity.dMajor/2.0;
	}
	
	/** Colour */
	private Color cLine=null;

	// UNED: 27-09-2011 - dballestin - Changed to access fields with getters
	@Override
	public void init() throws GraphFormatException
	{
		if (getColour()==null)
		{
			setColour(getWorld().convertColour("fg"));
		}
	}

	// UNED: 25-08-2011 - dballestin - Changed to access fields with getters
	private Color getMajorColour()
	{
		return new Color(getColour().getRed(),getColour().getGreen(),getColour().getBlue(),
			Math.min(255,(int)(256.0*getOpacity().dMajor)));
	}
	
	// UNED: 25-08-2011 - dballestin - Changed to access fields with getters
	private Color getMinorColour()
	{
		return new Color(getColour().getRed(),getColour().getGreen(),getColour().getBlue(),
			Math.min(255,(int)(256.0*getOpacity().dMinor)));
	}

	// UNED: 25-08-2011 - dballestin - Changed to access fields with getters
	@Override
	public void paint(Graphics2D g2)
	{
		g2.setStroke(DEFAULTSTROKE);

		g2.setColor(getMajorColour());
		if(getXSpacing().dMajor!=0.0)
		{
			// Draw all lines in range except within omit range
			for(
				double dPoint=(int)(getXRange().getMin() / getXSpacing().dMajor) * getXSpacing().dMajor;
				dPoint<=getXRange().getMax();
				dPoint+=getXSpacing().dMajor)
			{
				float fX=getWorld().convertXFloat(dPoint);
				g2.draw(new Line2D.Float(fX,getWorld().convertYFloat(getYRange().getMin()),
					fX,getWorld().convertYFloat(getYRange().getMax())));
			}
		}
		if(getYSpacing().dMajor!=0.0)
		{
			// Draw all lines in range except within omit range
			for(
				double dPoint=(int)(getYRange().getMin() / getYSpacing().dMajor) * getYSpacing().dMajor;
				dPoint<=getYRange().getMax();
				dPoint+=getYSpacing().dMajor)
			{
				float fY=getWorld().convertYFloat(dPoint);
				g2.draw(new Line2D.Float(getWorld().convertXFloat(getXRange().getMin()),fY,
					getWorld().convertXFloat(getXRange().getMax()),fY));
			}
		}

		g2.setColor(getMinorColour());
		if(getXSpacing().dMinor!=0.0)
		{
			// Draw all lines in range except within omit range
			for(
				double dPoint=(int)(getXRange().getMin() / getXSpacing().dMinor) * getXSpacing().dMinor;
				dPoint<=getXRange().getMax();
				dPoint+=getXSpacing().dMinor)
			{
				// Was there a major tick here?
				if(getXSpacing().dMajor!=0.0 &&
					Math.abs(Math.round(dPoint / getXSpacing().dMajor) * getXSpacing().dMajor - dPoint)<getXSpacing().dMinor/10.0)
					continue;

				float fX=getWorld().convertXFloat(dPoint);
				g2.draw(new Line2D.Float(fX,getWorld().convertYFloat(getYRange().getMin()),
					fX,getWorld().convertYFloat(getYRange().getMax())));
			}
		}
		if(getYSpacing().dMinor!=0.0)
		{
			// Draw all lines in range except within omit range
			for(
				double dPoint=(int)(getYRange().getMin() / getYSpacing().dMinor) * getYSpacing().dMinor;
				dPoint<=getYRange().getMax();
				dPoint+=getYSpacing().dMinor)
			{
				// Was there a major tick here?
				if(getYSpacing().dMajor!=0.0 &&
					Math.abs(Math.round(dPoint / getYSpacing().dMajor) * getYSpacing().dMajor - dPoint)<getYSpacing().dMinor/10.0)
					continue;

				float fY=getWorld().convertYFloat(dPoint);
				g2.draw(new Line2D.Float(getWorld().convertXFloat(getXRange().getMin()),fY,
					getWorld().convertXFloat(getXRange().getMax()),fY));
			}
		}

	}


	/**
	 * Sets colour for grid lines. By default, major gridlines are drawn at
	 * 50% and minor at 30% transparency of this colour. Colour defaults to
	 * normal foreground (usually black).
	 * <p>
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c New colour
	 */
	public void setColour(Color c)
	{
		cLine=c;
	}

	/**
	 * Sets X spacing of gridlines. You can have major and minor grid lines;
	 * minor ones are usually more transparent.
	 * @param sSpacing For example "0.5" if you want major gridlines every
	 *   0.5, or "0.5,0.1" if you also want minor ones every 0.1.
	 * @throws GraphFormatException If spacing string is invalid
	 */
	public void setXSpacing(String sSpacing) throws GraphFormatException
	{
		mmXSpacing=convertMajorMinor(sSpacing);
	}

	/**
	 * Sets Y spacing of gridlines. You can have major and minor grid lines;
	 * minor ones are usually more transparent.
	 * @param sSpacing For example "0.5" if you want major gridlines every
	 *   0.5, or "0.5,0.1" if you also want minor ones every 0.1.
	 * @throws GraphFormatException If spacing string is invalid
	 */
	public void setYSpacing(String sSpacing) throws GraphFormatException
	{
		mmYSpacing=convertMajorMinor(sSpacing);
	}

	// UNED: 25-08-2011 - dballestin - Changed access modifier of this method to protected so
	//                                 it would be possible to access it from subclasses
	/**
	 * Converts spacing from a string in format "" (=0.0,0.0) "0.3" (=0.3,0.0)
	 * or "0.3,0.1" (=0.3,0.1).
	 * @param sSpacing String defining major/minor spacing
	 * @return Object containing information as doubles. If a value was unspecified
	 *   it is set to 0.0
	 * @throws GraphFormatException If string format is invalid
	 */
	protected MajorMinor convertMajorMinor(String sSpacing) throws GraphFormatException
	{
		MajorMinor s=new MajorMinor();
		try
		{
			int iComma=sSpacing.indexOf(",");
			if(sSpacing.length()==0)
			{
				s.dMajor=0.0;
				s.dMinor=0.0;
			}
			else if(iComma==-1)
			{
				s.dMajor=Double.parseDouble(sSpacing);
				s.dMinor=0.0;
			}
			else
			{
				s.dMajor=Double.parseDouble(sSpacing.substring(0,iComma));
				s.dMinor=Double.parseDouble(sSpacing.substring(iComma+1));
			}
		}
		catch(NumberFormatException nfe)
		{
			throw new GraphFormatException(
				"<grid>: Invalid spacing specification: "+sSpacing);
		}
		return s;
	}

	/**
	 * Sets opacity of gridlines. The default is 0.25 for major and half that for
	 * minor.
	 * @param sOpacity For example "0.5" if you want major gridlines to be 50%,
	 *   or "0.5,0.1" if you also want minor ones to be 10%. (Minor ones default
	 *   to half the major one.)
	 * @throws GraphFormatException If opacity string is invalid
	 */
	public void setOpacity(String sOpacity) throws GraphFormatException
	{
		mmOpacity=convertMajorMinor(sOpacity);
		if(mmOpacity.dMinor==0.0) mmOpacity.dMinor=0.5*mmOpacity.dMajor;
	}

	/**
	 * @param d New maximum extent of grid
	 */
	public void setMaxX(double d)
	{
		grX=new GraphRange(grX.getMin(),d);
	}

	/**
	 * @param d New minimum extent of grid
	 */
	public void setMinX(double d)
	{
		grX=new GraphRange(d,grX.getMax());
	}

	/**
	 * @param d New maximum extent of grid
	 */
	public void setMaxY(double d)
	{
		grY=new GraphRange(grY.getMin(),d);
	}

	/**
	 * @param d New minimum extent of grid
	 */
	public void setMinY(double d)
	{
		grY=new GraphRange(d,grY.getMax());
	}
	
	// UNED: 25-08-2011 - dballestin
	/**
	 * Get horizontal grid spacing
	 * @return Horizontal grid spacing
	 */
	protected MajorMinor getXSpacing()
	{
		return mmXSpacing;
	}
	
	// UNED: 25-08-2011 - dballestin
	/**
	 * Get vertical grid spacing
	 * @return Vertical grid spacing
	 */
	protected MajorMinor getYSpacing()
	{
		return mmYSpacing;
	}
	
	// UNED: 25-08-2011 - dballestin
	/**
	 * Get opacity of major and minor gridlines
	 * @return Opacity of major and minor gridlines
	 */
	protected MajorMinor getOpacity()
	{
		return mmOpacity;
	}
	
	// UNED: 25-08-2011 - dballestin
	/**
	 * Get horizontal range of grid lines
	 * @return Horizontal range of grid lines
	 */
	protected GraphRange getXRange()
	{
		return grX;
	}
	
	// UNED: 25-08-2011 - dballestin
	/**
	 * Get vertical range of grid lines
	 * @return Vertical range of grid lines
	 */
	protected GraphRange getYRange()
	{
		return grY;
	}
	
	// UNED: 25-08-2011 - dballestin
	/**
	 * Get color for grid lines
	 * @return Color for grid lines
	 */
	protected Color getColour()
	{
		return cLine;
	}
	
	// UNED: 27-09-2011 - dballestin
	/**
	 * Set horizontal range of grid lines
	 * @param grX Horizontal range of grid lines
	 */
	protected void setXRange(GraphRange grX)
	{
		this.grX=grX;
	}
	
	// UNED: 27-09-2011 - dballestin
	/**
	 * Set vertical range of grid lines
	 * @param grY Vertical range of grid lines
	 */
	protected void setYRange(GraphRange grY)
	{
		this.grY=grY;
	}
}
