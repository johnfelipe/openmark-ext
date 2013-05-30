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

import java.awt.Color;
import java.awt.Graphics2D;

/** Draws f(x) functions in the graph space */
public class ColourFieldItem extends GraphItem
{
	// UNED: 27-09-2011 - dballestin - Initialization code moved to initColourField method
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public ColourFieldItem(World w) throws GraphFormatException
	{
		super(w);
		initColourField();
	}
	
	// UNED: 27-09-2011 - dballestin - Initialization code moved from constructor
	protected void initColourField()
	{
		grX=new GraphRange(getWorld().getLeftX(),getWorld().getRightX());
		grY=new GraphRange(getWorld().getTopY(),getWorld().getBottomY());
	}
	
	/** Range within which to calculate function */
	private GraphRange grX=null,grY=null;

	/** Actual function */
	private Function f=null;

	/** Size of blocks (so it doesn't have to calculate every pixel) */
	private int iBlockSize=4;

	/** Interface callers must provide */
	public static interface Function
	{
		/**
		 * Evaluates function. This will be called once per block in the given range.
		 * @param x X value (world co-ordinates)
		 * @param y Y value (world co-ordinates)
		 * @return Colour in Java object
		 */
		public Color f(double x,double y);
	}

	/**
	 * Set the actual function implementation.
	 * @param f Function implementor
	 */
	public void setFunction(Function f)
	{
		this.f=f;
	}

	// UNED: 26-09-2011 - dballestin - Changed to access fields with getters
	@Override
	public void paint(Graphics2D g2)
	{
		if(getFunction()==null) return;

		int
			iX1=getWorld().convertX(getXRange().getMin()),
			iX2=getWorld().convertX(getXRange().getMax());
		int iMinX=Math.min(iX1,iX2),iMaxX=Math.max(iX1,iX2);

		int
			iY1=getWorld().convertY(getYRange().getMin()),
			iY2=getWorld().convertY(getYRange().getMax());
		int iMinY=Math.min(iY1,iY2),iMaxY=Math.max(iY1,iY2);

		for(int iY=iMinY;iY<=iMaxY;iY+=getBlockSize())
		{
			for(int iX=iMinX;iX<=iMaxX;iX+=getBlockSize())
			{
				// Work out how much of this block gets drawn
				int
					iW=Math.min(iMaxX-iX+1,getBlockSize()),
					iH=Math.min(iMaxY-iY+1,getBlockSize());

				// Get colour for block
				double
					dX=getWorld().convertXBack(iX+iW/2),
					dY=getWorld().convertYBack(iY+iH/2);
				Color c=getFunction().f(dX,dY);

				// Draw block
				g2.setColor(c);
				g2.fillRect(iX,iY,iW,iH);
			}
		}
	}

	/**
	 * Sets size of blocks in pixels (4 = 4x4 blocks). Calculating and drawing
	 * smaller blocks takes longer.
	 * @param iBlockSize Length of block edge
	 */
	public void setBlockSize(int iBlockSize)
	{
		this.iBlockSize=iBlockSize;
	}

	/**
	 * @param d New maximum extent of function
	 */
	public void setMaxX(double d)
	{
		grX=new GraphRange(grX.getMin(),d);
	}

	/**
	 * @param d New minimum extent of function
	 */
	public void setMinX(double d)
	{
		grX=new GraphRange(d,grX.getMax());
	}

	/**
	 * @param d New maximum extent of function
	 */
	public void setMaxY(double d)
	{
		grY=new GraphRange(grY.getMin(),d);
	}

	/**
	 * @param d New minimum extent of function
	 */
	public void setMinY(double d)
	{
		grY=new GraphRange(d,grY.getMax());
	}
	
	// UNED: 26-09-2011 - dballestin
	/**
	 * Get the actual function implementation.
	 * @return Actual function implementation
	 */
	protected Function getFunction()
	{
		return f;
	}
	
	// UNED: 26-09-2011 - dballestin
	/**
	 * Get horizontal range within which to calculate function
	 * @return Horizontal range within which to calculate function
	 */
	protected GraphRange getXRange()
	{
		return grX;
	}
	
	// UNED: 26-09-2011 - dballestin
	/**
	 * Get vertical range within which to calculate function
	 * @return Vertical range within which to calculate function
	 */
	protected GraphRange getYRange()
	{
		return grY;
	}
	
	// UNED: 26-09-2011 - dballestin
	/**
	 * Get size of blocks in pixels
	 * @return Size of blocks in pixels
	 */
	protected int getBlockSize()
	{
		return iBlockSize;
	}
	
	// UNED: 27-09-2011 - dballestin
	/**
	 * Set horizontal range within which to calculate function
	 * @param grX Horizontal range within which to calculate function
	 */
	protected void setXRange(GraphRange grX)
	{
		this.grX=grX;
	}
	
	// UNED: 27-09-2011 - dballestin
	/**
	 * Set vertical range within which to calculate function
	 * @param grY Vertical range within which to calculate function
	 */
	protected void setYRange(GraphRange grY)
	{
		this.grY=grY;
	}
}
