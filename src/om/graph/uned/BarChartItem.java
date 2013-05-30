package om.graph.uned;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import om.graph.GraphFormatException;
import om.graph.GraphItem;
import om.graph.GraphPoint;
import om.graph.World;
import om.helper.uned.AnswerChecking;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

//UNED: 13-10-2011 - dballestin
/** 
 * Draws a bar chart.<br/><br/>
 * Note that this graph item doesn't draw axes (use &lt;xAxis&gt; and/or &lt;yAxis&gt; items if you need 
 * them).<br/><br/>
 * Moreover, if you use this graph item in combination with axes items be careful with the labels 
 * of all these items because they can be drawn overlapped. 
 */
public class BarChartItem extends GraphItem implements Displayable,Replaceable
{
	/** Constant used in 'direction' attribute to define a vertical bar chart */
	private final static String VERTICAL="vertical";
	
	/** Constant used in 'direction' attribute to define an horizontal bar chart */
	private final static String HORIZONTAL="horizontal";
	
	/* Constants used to identify bar's attribute in getBars() method */
	private final static int BAR_VALUE=0;
	private final static int BAR_LABEL=1;
	private final static int BAR_BARCOLOUR=2;
	private final static int BAR_LABELCOLOUR=3;
	private final static int BAR_LABELFONT=4;
	
	/* Labels position */
	private final static int BASE_POSITION=0;
	private final static int VALUE_POSITION=1;
	private final static int INSIDE_POSITION=2;
	
	/* Constant values used to set labels position */
	private final static String BASE_LABEL_POSITION="base";
	private final static String VALUE_LABEL_POSITION="value";
	private final static String INSIDE_LABEL_POSITION="inside";
	
	/* Labels alignments */
	private final static int TOP_ALIGNMENT=0x20;
	private final static int MIDDLE_ALIGNMENT=0x10;
	private final static int BOTTOM_ALIGNMENT=0x8;
	private final static int LEFT_ALIGNMENT=0x4;
	private final static int CENTER_ALIGNMENT=0x2;
	private final static int RIGHT_ALIGNMENT=0x1;
	
	/* Labels alignment's masks */
	private final static int VERTICAL_ALIGNMENT_MASK=0x38;
	private final static int HORIZONTAL_ALIGNMENT_MASK=0x7;
	
	/** Default labels alignment (middle center) */
	private final static int DEFAULT_LABEL_ALIGNMENT=0x12;
	
	/* Constant values used to set labels alignment */
	private final static String TOP_LABEL_ALIGNMENT="top";
	private final static String MIDDLE_LABEL_ALIGNMENT="middle";
	private final static String BOTTOM_LABEL_ALIGNMENT="bottom";
	private final static String LEFT_LABEL_ALIGNMENT="left";
	private final static String CENTER_LABEL_ALIGNMENT="center";
	private final static String RIGHT_LABEL_ALIGNMENT="right";
	
	/** Standard question */
	private StandardQuestion sq;
	
	/** 
	 * Base co-ordinate for bars (expressed in Y co-ordinates for vertical bar charts and X co-ordinates 
	 * for horizontal bar charts) */
	private Double base=new Double(0.0);
	
	/** 
	 * Start co-ordinate for first bar (expressed in X co-ordinates for vertical bar charts and 
	 * Y-coordinates for horizontal bar charts)
	 */
	private Double start=null;
	
	/**
	 * Bar's width (expressed in X co-ordinates for vertical bar charts and Y co-ordinates for horizontal 
	 * bar charts)
	 */
	private Double width=null;
	
	/**
	 * End co-ordinate for last bar (expressed in X co-ordinates for vertical bar charts and 
	 * Y-coordinates for horizontal bar charts)
	 */
	private Double end=null;
	
	/** Flag to indicate if this is a vertical bar chart (true) or an horizontal bar chart (false) */
	private boolean vertical=true;
	
	/** Separation between bars (expressed in pixels) */
	private int separation=0;
	
	/** Bars */
	private Map<Integer,Bar> bars=new HashMap<Integer,Bar>();
	
	/** Default bars colours */
	private Map<Integer,Color> defaultBarColours=new HashMap<Integer,Color>();
	
	/** Default labels colours */
	private Map<Integer,Color> defaultLabelColours=new HashMap<Integer,Color>();
	
	/** Default labels fonts */
	private Map<Integer,Font> defaultLabelFonts=new HashMap<Integer,Font>();
	
	/** Labels position */
	private int labelsPosition=BASE_POSITION;
	
	/** Labels alignment */
	private int labelsAlign=DEFAULT_LABEL_ALIGNMENT;
	
	/** Labels margin (expressed in pixels) */
	private int labelsMargin=2;
	
	/** Labels rotation flag */
	private boolean bRotateLabels=false;
	
	/** Labels rotation direction flag (true:clockwise, false: counterclockwise) */
	private boolean bRotateFlip=false;
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** State flag that indicates if this bar chart is displayed or not */
	private boolean display=true;
	
	/** Utility class to group bar properties */
	private class Bar
	{
		public double value=0.0;
		public String label=null;
		public Color barColour=null;
		public Color labelColour=null;
		public Font labelFont=null;
	}
	
	/** Utility class to order bars */
	private class BarWithIndex implements Comparable<BarWithIndex>
	{
		public int index;
		public Bar bar;
		
		@Override
		public int compareTo(BarWithIndex o)
		{
			int result=0;
			if (index<o.index)
			{
				result=-1;
			}
			else if (index>o.index)
			{
				result=1;
			}
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			boolean isEquals=false;
			if (obj!=null && obj instanceof BarWithIndex)
			{
				isEquals=index==((BarWithIndex)obj).index;
			}
			return isEquals;
		}
	}
	
	/** Utility class to order colors */
	private class ColorWithIndex implements Comparable<ColorWithIndex>
	{
		public int index;
		public Color colour;
		
		@Override
		public int compareTo(ColorWithIndex o)
		{
			int result=0;
			if (index<o.index)
			{
				result=-1;
			}
			else if (index>o.index)
			{
				result=1;
			}
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			boolean isEquals=false;
			if (obj!=null && obj instanceof ColorWithIndex)
			{
				isEquals=index==((ColorWithIndex)obj).index;
			}
			return isEquals;
		}
	}
	
	/** Utility class to order fonts */
	private class FontWithIndex implements Comparable<FontWithIndex>
	{
		public int index;
		public Font font;
		
		@Override
		public int compareTo(FontWithIndex o)
		{
			int result=0;
			if (index<o.index)
			{
				result=-1;
			}
			else if (index>o.index)
			{
				result=1;
			}
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			boolean isEquals=false;
			if (obj!=null && obj instanceof FontWithIndex)
			{
				isEquals=index==((FontWithIndex)obj).index;
			}
			return isEquals;
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public BarChartItem(World w) throws GraphFormatException
	{
		super(w);
	}
	
	@Override
	public void init() throws GraphFormatException
	{
		if (getNumberOfBars()<1)
		{
			throw new GraphFormatException("<barChart>: Must specify at least 1 bar");
		}
		if (getNumberOfDefaultBarColours()==0)
		{
			setDefaultBarColour(getWorld().convertColour("fg"));
		}
		if (getNumberOfDefaultLabelColours()==0)
		{
			setDefaultLabelColour(getWorld().convertColour("fg"));
		}
		if (getNumberOfDefaultLabelFonts()==0)
		{
			setDefaultLabelFont(getWorld().getDefaultFont(true));
		}
	}
	
	@Override
	public void paint(Graphics2D g2)
	{
		if (isDisplay())
		{
			// Initializations
			
			// Get the bars, default colors, default fonts and their iterators
			List<Bar> bars=getBars();
			List<Color> defaultBarColours=getDefaultBarColours();
			List<Color> defaultLabelColours=getDefaultLabelColours();
			List<Font> defaultLabelFonts=getDefaultLabelFonts();
			Iterator<Bar> itBars=bars.iterator();
			Iterator<Color> itDefaultBarColours=defaultBarColours.iterator();
			Iterator<Color> itDefaultLabelColours=defaultLabelColours.iterator();
			Iterator<Font> itDefaultLabelFonts=defaultLabelFonts.iterator();
			
			// Vertical or horizontal bar chart?
			boolean vertical=isVertical();
			
			// Trend (true for positive trend, false for negative trend)
			boolean trend=hasPositiveTrend(bars);
			
			// Labels position
			int labelsPosition=getLabelsPosition();
			
			// Labels alignment
			int labelsAlign=getLabelsAlign();
			
			// Labels margin
			int labelsMargin=getLabelsMargin();
			
			// Labels rotation flag
			boolean bRotateLabels=isRotateLabels();
			
			// Labels rotation direction flag (true:clockwise, false: counterclockwise)
			boolean bRotateFlip=isRotateFlip();
			
			// Get start value
			double start=0.0;
			if (getStart()==null)
			{
				if (vertical)
				{
					start=getWorld().getLeftX();
				}
				else
				{
					start=getWorld().getTopY();
				}
			}
			else
			{
				start=getStart();
			}
			
			// Get width value
			double width=0.0;
			if (getWidth()!=null)
			{
				width=getWidth().doubleValue();
			}
			else if (getEnd()!=null)
			{
				width=(getEnd().doubleValue()-start)/bars.size();
			}
			else if (vertical)
			{
				width=(getWorld().getRightX()-start)/bars.size();
			}
			else
			{
				width=(getWorld().getBottomY()-start)/bars.size();
			}
			
			// Divide separation in two parts to paint bars centered
			int separationStart=getSeparation()/2+getSeparation()%2;
			int separationEnd=getSeparation()/2;
			if (width<0.0)
			{
				separationStart=-separationStart;
				separationEnd=-separationEnd;
			}
			
			// First draw bars
			int barNumber=0;
			while (itBars.hasNext())
			{
				// Get bar and its default color 
				Bar bar=itBars.next();
				if (!itDefaultBarColours.hasNext())
				{
					itDefaultBarColours=defaultBarColours.iterator();
				}
				Color defaultBarColour=itDefaultBarColours.next();
				
				// Paint the bar
				paintBar(g2,bar,barNumber,vertical,start,width,defaultBarColour,separationStart,
						separationEnd);
				
				// Next bar
				barNumber++;
			}
			
			// Finally draw labels
			itBars=bars.iterator();
			barNumber=0;
			while (itBars.hasNext())
			{
				// Get bar and default color and default font for its label 
				Bar bar=itBars.next();
				if (!itDefaultLabelColours.hasNext())
				{
					itDefaultLabelColours=defaultLabelColours.iterator();
				}
				Color defaultLabelColour=itDefaultLabelColours.next();
				if (!itDefaultLabelFonts.hasNext())
				{
					itDefaultLabelFonts=defaultLabelFonts.iterator();
				}
				Font defaultLabelFont=itDefaultLabelFonts.next();
				
				// Paint the label (if exists)
				if (bar.label!=null && !bar.label.equals(""))
				{
					paintLabel(g2,bar,barNumber,vertical,start,width,trend,labelsPosition,labelsAlign,
							labelsMargin,bRotateLabels,bRotateFlip,defaultLabelColour,defaultLabelFont);
				}
				
				// Next label
				barNumber++;
			}
		}
	}
	
	/**
	 * Paints a bar of the bar chart.
	 * @param g2 Target graphics context
	 * @param bar Bar
	 * @param barNumber Bar's number
	 * @param vertical true if this is a vertical bar chart or false if this is an horizontal bar chart
	 * @param start Start co-ordinate for first bar
	 * @param width Bar's width
	 * @param defaultColor Default bar's color
	 */
	protected void paintBar(Graphics2D g2,Bar bar,int barNumber,boolean vertical,double start,double width,
			Color defaultColor,int separationStart,int separationEnd)
	{
		// Get base value
		double base=getBase().doubleValue();
		
		// Get bar's corners in world coordinates
		GraphPoint startCorner=null;
		GraphPoint endCorner=null;
		if (vertical)
		{
			startCorner=new GraphPoint(start+width*barNumber,separationStart,base,0);
			endCorner=new GraphPoint(start+width*(barNumber+1),-separationEnd,bar.value,0);
		}
		else
		{
			startCorner=new GraphPoint(base,0,start+width*barNumber,-separationStart);
			endCorner=new GraphPoint(bar.value,0,start+width*(barNumber+1),separationEnd);
		}
		
		// Work out the bar's corners and convert to pixels
		Point startPoint=startCorner.convert(getWorld());
		Point endPoint=endCorner.convert(getWorld());
		int iX=Math.min(startPoint.x,endPoint.x);
		int iY=Math.min(startPoint.y,endPoint.y);
		int iWidth=Math.max(startPoint.x,endPoint.x)-iX;
		int iHeight=Math.max(startPoint.y,endPoint.y)-iY;
		
		// Get bar's color
		Color barColour=bar.barColour==null?defaultColor:bar.barColour;
		
		// Draw the bar
		g2.setColor(barColour);
		g2.fillRect(iX,iY,iWidth,iHeight);
	}
	
	/**
	 * Paints a label for a bar of the bar chart.
	 * @param g2 Target graphics context
	 * @param bar Bar
	 * @param barNumber Bar's number
	 * @param vertical true if this is a vertical bar chart, false if this is an horizontal bar chart
	 * @param start Start co-ordinate for first bar
	 * @param width Bar's width
	 * @param trend true for positive trend, false for negative
	 * @param labelPosition Label's position
	 * @param labelAlign Label's alignment
	 * @param labelMargin Label's margin
	 * @param bRotateLabel true if this label is going to be rotated, false otherwise
	 * @param bRotateFlip true for clockwise rotation direction, 
	 * false for counterclockwise rotation direction
	 * @param defaultColor Default label's color
	 * @param defaultFont Default label's font
	 */
	protected void paintLabel(Graphics2D g2,Bar bar,int barNumber,boolean vertical,double start,
			double width,boolean trend,int labelPosition,int labelAlign,int labelMargin,
			boolean bRotateLabel,boolean bRotateFlip,Color defaultColor,Font defaultFont)
	{
		switch (labelPosition)
		{
			case BASE_POSITION:
				paintLabelAtBasePosition(g2,bar,barNumber,vertical,start,width,trend,labelAlign,
						labelMargin,bRotateLabel,bRotateFlip,defaultColor,defaultFont);
				break;
			case VALUE_POSITION:
				paintLabelAtValuePosition(g2,bar,barNumber,vertical,start,width,trend,labelAlign,
						labelMargin,bRotateLabel,bRotateFlip,defaultColor,defaultFont);
				break;
			case INSIDE_POSITION:
				paintLabelAtInsidePosition(g2,bar,barNumber,vertical,start,width,labelAlign,labelMargin,
						bRotateLabel,bRotateFlip,defaultColor,defaultFont);
		}
	}
	
	/**
	 * Paints a label for a bar of the bar chart at base position.
	 * @param g2 Target graphics context
	 * @param bar Bar
	 * @param barNumber Bar's number
	 * @param vertical true if this is a vertical bar chart, false if this is an horizontal bar chart
	 * @param start Start co-ordinate for first bar
	 * @param width Bar's width
	 * @param trend true for positive trend, false for negative
	 * @param labelAlign Label's alignment
	 * @param labelMargin Label's margin
	 * @param bRotateLabel true if this label is going to be rotated, false otherwise
	 * @param bRotateFlip true for clockwise rotation direction, 
	 * false for counterclockwise rotation direction
	 * @param defaultColor Default label's color
	 * @param defaultFont Default label's font
	 */
	protected void paintLabelAtBasePosition(Graphics2D g2,Bar bar,int barNumber,boolean vertical,
			double start,double width,boolean trend,int labelAlign,int labelMargin,boolean bRotateLabel,
			boolean bRotateFlip,Color defaultColor,Font defaultFont)
	{
		// Get label's color and font
		Color labelColour=bar.labelColour==null?defaultColor:bar.labelColour;
		Font labelFont=bar.labelFont==null?defaultFont:bar.labelFont;
		
		// Get base value
		double base=getBase().doubleValue();
		
		// Calculate value that identifies if lower x co-ordinates are at left (false) or right (true)
		boolean xFlip=false;
		if (getWorld().getLeftX()>getWorld().getRightX())
		{
			xFlip=true;
		}
		
		// Calculate value that identifies if lower y co-ordinates are at top (false) or bottom (true)
		boolean yFlip=false;
		if (getWorld().getTopY()>getWorld().getBottomY())
		{
			yFlip=true;
		}
		
		// Get label's ascent and descent in pixels
		int ascent=g2.getFontMetrics(labelFont).getAscent();
		int descent=g2.getFontMetrics(labelFont).getDescent();
		
		// Get label's width and heigth in pixel and world co-ordinates
		int iLabelWidth=(int)g2.getFontMetrics(labelFont).getStringBounds(bar.label,g2).getWidth();
		int iLabelHeight=ascent+descent;
		double labelWidth=0.0;
		double labelHeight=0.0;
		if (bRotateLabel)
		{
			labelWidth=convertWidthBack(iLabelHeight);
			labelHeight=convertHeightBack(iLabelWidth);
		}
		else
		{
			labelWidth=convertWidthBack(iLabelWidth);
			labelHeight=convertHeightBack(iLabelHeight);
		}
		
		// Get label's margin in world co-ordinates
		double margin=0.0;
		if (vertical)
		{
			margin=convertHeightBack(labelMargin);
		}
		else
		{
			margin=convertWidthBack(labelMargin);
		}
		
		// Get label's center in world coordinates
		GraphPoint labelCenter=null;
		if (vertical)
		{
			// Get X center for vertical bar chart
			double xCenter=0.0;
			switch (labelAlign&HORIZONTAL_ALIGNMENT_MASK)
			{
				case LEFT_ALIGNMENT:
					if (xFlip)
					{
						xCenter=start+width*(barNumber+1)-labelWidth/2.0;
					}
					else
					{
						xCenter=start+width*barNumber+labelWidth/2.0;
					}
					break;
				case CENTER_ALIGNMENT:
					xCenter=start+width*(barNumber+0.5);
					break;
				case RIGHT_ALIGNMENT:
					if (xFlip)
					{
						xCenter=start+width*barNumber+labelWidth/2.0;
					}
					else
					{
						xCenter=start+width*(barNumber+1)-labelWidth/2.0;
					}
			}
			
			// Get Y center for vertical bar chart
			double yCenter=0.0;
			if (bar.value>base)
			{
				yCenter=base-margin-labelHeight/2.0;
			}
			else if (bar.value<base)
			{
				yCenter=base+margin+labelHeight/2.0;
			}
			else if (trend)
			{
				yCenter=base-margin-labelHeight/2.0;
			}
			else
			{
				yCenter=base+margin+labelHeight/2.0;
			}
			
			// Finally we set label's center
			labelCenter=new GraphPoint(xCenter,yCenter);
		}
		else
		{
			// Get X center for horizontal bar chart
			double xCenter=0.0;
			if (bar.value>base)
			{
				xCenter=base-margin-labelWidth/2.0;
			}
			else if (bar.value<base)
			{
				xCenter=base+margin+labelWidth/2.0;
			}
			else if (trend)
			{
				xCenter=base-margin-labelWidth/2.0;
			}
			else
			{
				xCenter=base+margin+labelWidth/2.0;
			}
			
			// Get Y center for horizontal bar chart
			double yCenter=0.0;
			switch (labelAlign&VERTICAL_ALIGNMENT_MASK)
			{
				case BOTTOM_ALIGNMENT:
					if (yFlip)
					{
						yCenter=start+width*barNumber+labelHeight/2.0;
					}
					else
					{
						yCenter=start+width*(barNumber+1)-labelHeight/2.0;
					}
					break;
				case MIDDLE_ALIGNMENT:
					yCenter=start+width*(barNumber+0.5);
					break;
				case TOP_ALIGNMENT:
					if (yFlip)
					{
						yCenter=start+width*(barNumber+1)-labelHeight/2.0;
					}
					else
					{
						yCenter=start+width*barNumber+labelHeight/2.0;
					}
			}
			
			// Finally we set label's center
			labelCenter=new GraphPoint(xCenter,yCenter);
		}
		
		// Work out label's center and convert to pixels
		Point pCenter=labelCenter.convert(getWorld());
		
		// Work out label's start point
		Point pStart=new Point(pCenter.x-iLabelWidth/2,pCenter.y+iLabelHeight/2-descent);
		
		// Set graphics color and font
		g2.setColor(labelColour);
		g2.setFont(labelFont);
		
		if (bRotateLabel)
		{
			AffineTransform at=g2.getTransform();
			
			if (bRotateFlip)
			{
				// Clockwise rotation
				g2.rotate(Math.PI/2.0,pCenter.x,pCenter.y);
			}
			else
			{
				// Counterclockwise rotation
				g2.rotate(-Math.PI/2.0,pCenter.x,pCenter.y);
			}

			// Paint rotated label
			g2.drawString(bar.label,pStart.x,pStart.y);
			
			g2.setTransform(at);
		}
		else
		{
			// Paint non rotated label
			g2.drawString(bar.label,pStart.x,pStart.y);
		}
	}
	
	/**
	 * Paints a label for a bar of the bar chart at value position.
	 * @param g2 Target graphics context
	 * @param bar Bar
	 * @param barNumber Bar's number
	 * @param vertical true if this is a vertical bar chart, false if this is an horizontal bar chart
	 * @param start Start co-ordinate for first bar
	 * @param width Bar's width
	 * @param trend true for positive trend, false for negative
	 * @param labelAlign Label's alignment
	 * @param labelMargin Label's margin
	 * @param bRotateLabel true if this label is going to be rotated, false otherwise
	 * @param bRotateFlip true for clockwise rotation direction, 
	 * false for counterclockwise rotation direction
	 * @param defaultColor Default label's color
	 * @param defaultFont Default label's font
	 */
	protected void paintLabelAtValuePosition(Graphics2D g2,Bar bar,int barNumber,boolean vertical,
			double start,double width,boolean trend,int labelAlign,int labelMargin,boolean bRotateLabel,
			boolean bRotateFlip,Color defaultColor,Font defaultFont)
	{
		// Get label's color and font
		Color labelColour=bar.labelColour==null?defaultColor:bar.labelColour;
		Font labelFont=bar.labelFont==null?defaultFont:bar.labelFont;
		
		// Get base value
		double base=getBase().doubleValue();
		
		// Calculate value that identifies if lower x co-ordinates are at left (false) or right (true)
		boolean xFlip=false;
		if (getWorld().getLeftX()>getWorld().getRightX())
		{
			xFlip=true;
		}
		
		// Calculate value that identifies if lower y co-ordinates are at top (false) or bottom (true)
		boolean yFlip=false;
		if (getWorld().getTopY()>getWorld().getBottomY())
		{
			yFlip=true;
		}
		
		// Get label's ascent and descent in pixels
		int ascent=g2.getFontMetrics(labelFont).getAscent();
		int descent=g2.getFontMetrics(labelFont).getDescent();
		
		// Get label's width and heigth in pixel and world co-ordinates
		int iLabelWidth=(int)g2.getFontMetrics(labelFont).getStringBounds(bar.label,g2).getWidth();
		int iLabelHeight=ascent+descent;
		double labelWidth=0.0;
		double labelHeight=0.0;
		if (bRotateLabel)
		{
			labelWidth=convertWidthBack(iLabelHeight);
			labelHeight=convertHeightBack(iLabelWidth);
		}
		else
		{
			labelWidth=convertWidthBack(iLabelWidth);
			labelHeight=convertHeightBack(iLabelHeight);
		}
		
		// Get label's margin in world co-ordinates
		double margin=0.0;
		if (vertical)
		{
			margin=convertHeightBack(labelMargin);
		}
		else
		{
			margin=convertWidthBack(labelMargin);
		}
		
		// Get label's center in world coordinates
		GraphPoint labelCenter=null;
		if (vertical)
		{
			// Get X center for vertical bar chart
			double xCenter=0.0;
			switch (labelAlign&HORIZONTAL_ALIGNMENT_MASK)
			{
				case LEFT_ALIGNMENT:
					if (xFlip)
					{
						xCenter=start+width*(barNumber+1)-labelWidth/2.0;
					}
					else
					{
						xCenter=start+width*barNumber+labelWidth/2.0;
					}
					break;
				case CENTER_ALIGNMENT:
					xCenter=start+width*(barNumber+0.5);
					break;
				case RIGHT_ALIGNMENT:
					if (xFlip)
					{
						xCenter=start+width*barNumber+labelWidth/2.0;
					}
					else
					{
						xCenter=start+width*(barNumber+1)-labelWidth/2.0;
					}
			}
			
			// Get Y center for vertical bar chart
			double yCenter=0.0;
			if (bar.value>base)
			{
				yCenter=bar.value+margin+labelHeight/2.0;
			}
			else if (bar.value<base)
			{
				yCenter=bar.value-margin-labelHeight/2.0;
			}
			else if (trend)
			{
				yCenter=bar.value+margin+labelHeight/2.0;
			}
			else
			{
				yCenter=bar.value-margin-labelHeight/2.0;
			}
			
			// Finally we set label's center
			labelCenter=new GraphPoint(xCenter,yCenter);
		}
		else
		{
			// Get X center for horizontal bar chart
			double xCenter=0.0;
			if (bar.value>base)
			{
				xCenter=bar.value+margin+labelWidth/2.0;
			}
			else if (bar.value<base)
			{
				xCenter=bar.value-margin-labelWidth/2.0;
			}
			else if (trend)
			{
				xCenter=bar.value+margin+labelWidth/2.0;
			}
			else
			{
				xCenter=bar.value-margin-labelWidth/2.0;
			}
			
			// Get Y center for horizontal bar chart
			double yCenter=0.0;
			switch (labelAlign&VERTICAL_ALIGNMENT_MASK)
			{
				case BOTTOM_ALIGNMENT:
					if (yFlip)
					{
						yCenter=start+width*barNumber+labelHeight/2.0;
					}
					else
					{
						yCenter=start+width*(barNumber+1)-labelHeight/2.0;
					}
					break;
				case MIDDLE_ALIGNMENT:
					yCenter=start+width*(barNumber+0.5);
					break;
				case TOP_ALIGNMENT:
					if (yFlip)
					{
						yCenter=start+width*(barNumber+1)-labelHeight/2.0;
					}
					else
					{
						yCenter=start+width*barNumber+labelHeight/2.0;
					}
			}
			
			// Finally we set label's center
			labelCenter=new GraphPoint(xCenter,yCenter);
		}
		
		// Work out label's center and convert to pixels
		Point pCenter=labelCenter.convert(getWorld());
		
		// Work out label's start point
		Point pStart=new Point(pCenter.x-iLabelWidth/2,pCenter.y+iLabelHeight/2-descent);
		
		// Set graphics color and font
		g2.setColor(labelColour);
		g2.setFont(labelFont);
		
		if (bRotateLabel)
		{
			AffineTransform at=g2.getTransform();
			
			if (bRotateFlip)
			{
				// Clockwise rotation
				g2.rotate(Math.PI/2.0,pCenter.x,pCenter.y);
			}
			else
			{
				// Counterclockwise rotation
				g2.rotate(-Math.PI/2.0,pCenter.x,pCenter.y);
			}

			// Paint rotated label
			g2.drawString(bar.label,pStart.x,pStart.y);
			
			g2.setTransform(at);
		}
		else
		{
			// Paint non rotated label
			g2.drawString(bar.label,pStart.x,pStart.y);
		}
	}
	
	/**
	 * Paints a label for a bar of the bar chart at inside position.
	 * @param g2 Target graphics context
	 * @param bar Bar
	 * @param barNumber Bar's number
	 * @param vertical true if this is a vertical bar chart, false if this is an horizontal bar chart
	 * @param start Start co-ordinate for first bar
	 * @param width Bar's width
	 * @param labelAlign Label's alignment
	 * @param labelMargin Label's margin
	 * @param bRotateLabel true if this label is going to be rotated, false otherwise
	 * @param bRotateFlip true for clockwise rotation direction, 
	 * false for counterclockwise rotation direction
	 * @param defaultColor Default label's color
	 * @param defaultFont Default label's font
	 */
	protected void paintLabelAtInsidePosition(Graphics2D g2,Bar bar,int barNumber,boolean vertical,
			double start,double width,int labelAlign,int labelMargin,boolean bRotateLabel,
			boolean bRotateFlip,Color defaultColor,Font defaultFont)
	{
		// Get label's color and font
		Color labelColour=bar.labelColour==null?defaultColor:bar.labelColour;
		Font labelFont=bar.labelFont==null?defaultFont:bar.labelFont;
		
		// Get base value
		double base=getBase().doubleValue();
		
		// Calculate value that identifies if lower x co-ordinates are at left (false) or right (true)
		boolean xFlip=false;
		if (getWorld().getLeftX()>getWorld().getRightX())
		{
			xFlip=true;
		}
		
		// Calculate value that identifies if lower y co-ordinates are at top (false) or bottom (true)
		boolean yFlip=false;
		if (getWorld().getTopY()>getWorld().getBottomY())
		{
			yFlip=true;
		}
		
		// Get label's ascent and descent in pixels
		int ascent=g2.getFontMetrics(labelFont).getAscent();
		int descent=g2.getFontMetrics(labelFont).getDescent();
		
		// Get label's width and heigth in pixel and world co-ordinates
		int iLabelWidth=(int)g2.getFontMetrics(labelFont).getStringBounds(bar.label,g2).getWidth();
		int iLabelHeight=ascent+descent;
		double labelWidth=0.0;
		double labelHeight=0.0;
		if (bRotateLabel)
		{
			labelWidth=convertWidthBack(iLabelHeight);
			labelHeight=convertHeightBack(iLabelWidth);
		}
		else
		{
			labelWidth=convertWidthBack(iLabelWidth);
			labelHeight=convertHeightBack(iLabelHeight);
		}
		
		// Get label's margins in world co-ordinates
		double marginX=convertWidthBack(labelMargin);;
		double marginY=convertHeightBack(labelMargin);;
		
		// Get label's center in world coordinates
		GraphPoint labelCenter=null;
		if (vertical)
		{
			// Get X center for vertical bar chart
			double xCenter=0.0;
			switch (labelAlign&HORIZONTAL_ALIGNMENT_MASK)
			{
				case LEFT_ALIGNMENT:
					if (xFlip)
					{
						xCenter=start+width*(barNumber+1)-marginX-labelWidth/2.0;
					}
					else
					{
						xCenter=start+width*barNumber+marginX+labelWidth/2.0;
					}
					break;
				case CENTER_ALIGNMENT:
					xCenter=start+width*(barNumber+0.5);
					break;
				case RIGHT_ALIGNMENT:
					if (xFlip)
					{
						xCenter=start+width*barNumber+marginX+labelWidth/2.0;
					}
					else
					{
						xCenter=start+width*(barNumber+1)-marginX-labelWidth/2.0;
					}
			}
			
			// Get Y center for vertical bar chart
			double yCenter=0.0;
			switch (labelAlign&VERTICAL_ALIGNMENT_MASK)
			{
				case BOTTOM_ALIGNMENT:
					if (bar.value>base)
					{
						if (yFlip)
						{
							yCenter=base+marginY+labelHeight/2.0;
						}
						else
						{
							yCenter=bar.value-marginY-labelHeight/2.0;
						}
					}
					else
					{
						if (yFlip)
						{
							yCenter=bar.value+marginY+labelHeight/2.0;
						}
						else
						{
							yCenter=base-marginY-labelHeight/2.0;
						}
					}
					break;
				case MIDDLE_ALIGNMENT:
					if (bar.value>=base)
					{
						yCenter=base+(bar.value-base)/2.0;
					}
					else
					{
						yCenter=base-(base-bar.value)/2.0;
					}
					break;
				case TOP_ALIGNMENT:
					if (bar.value>base)
					{
						if (yFlip)
						{
							yCenter=bar.value-marginY-labelHeight/2.0;
						}
						else
						{
							yCenter=base+marginY+labelHeight/2.0;
						}
					}
					else
					{
						if (yFlip)
						{
							yCenter=base-marginY-labelHeight/2.0;
						}
						else
						{
							yCenter=bar.value+marginY+labelHeight/2.0;
						}
					}
			}
			
			// Finally we set label's center
			labelCenter=new GraphPoint(xCenter,yCenter);
		}
		else
		{
			// Get X center for horizontal bar chart
			double xCenter=0.0;
			switch (labelAlign&HORIZONTAL_ALIGNMENT_MASK)
			{
				case LEFT_ALIGNMENT:
					if (bar.value>base)
					{
						if (xFlip)
						{
							xCenter=bar.value-marginX-labelWidth/2.0;
						}
						else
						{
							xCenter=base+marginX+labelWidth/2.0;
						}
					}
					else
					{
						if (xFlip)
						{
							xCenter=base-marginX-labelWidth/2.0;
						}
						else
						{
							xCenter=bar.value+marginX+labelWidth/2.0;
						}
					}
					break;
				case CENTER_ALIGNMENT:
					if (bar.value>=base)
					{
						xCenter=base+(bar.value-base)/2.0;
					}
					else
					{
						xCenter=base-(base-bar.value)/2.0;
					}
					break;
				case RIGHT_ALIGNMENT:
					if (bar.value>base)
					{
						if (xFlip)
						{
							xCenter=base+marginX+labelWidth/2.0;
						}
						else
						{
							xCenter=bar.value-marginX-labelWidth/2.0;
						}
					}
					else
					{
						if (xFlip)
						{
							xCenter=bar.value+marginX+labelWidth/2.0;
						}
						else
						{
							xCenter=base-marginX-labelWidth/2.0;
						}
					}
			}
			
			// Get Y center for horizontal bar chart
			double yCenter=0.0;
			switch (labelAlign&VERTICAL_ALIGNMENT_MASK)
			{
				case BOTTOM_ALIGNMENT:
					if (yFlip)
					{
						yCenter=start+width*barNumber+marginY+labelHeight/2.0;
					}
					else
					{
						yCenter=start+width*(barNumber+1)-marginY-labelHeight/2.0;
					}
					break;
				case MIDDLE_ALIGNMENT:
					yCenter=start+width*(barNumber+0.5);
					break;
				case TOP_ALIGNMENT:
					if (yFlip)
					{
						yCenter=start+width*(barNumber+1)-marginY-labelHeight/2.0;
					}
					else
					{
						yCenter=start+width*barNumber+marginY+labelHeight/2.0;
					}
			}
			
			// Finally we set label's center
			labelCenter=new GraphPoint(xCenter,yCenter);
		}
		
		// Work out label's center and convert to pixels
		Point pCenter=labelCenter.convert(getWorld());
		
		// Work out label's start point
		Point pStart=new Point(pCenter.x-iLabelWidth/2,pCenter.y+iLabelHeight/2-descent);
		
		// Set graphics color and font
		g2.setColor(labelColour);
		g2.setFont(labelFont);
		
		if (bRotateLabel)
		{
			AffineTransform at=g2.getTransform();
			
			if (bRotateFlip)
			{
				// Clockwise rotation
				g2.rotate(Math.PI/2.0,pCenter.x,pCenter.y);
			}
			else
			{
				// Counterclockwise rotation
				g2.rotate(-Math.PI/2.0,pCenter.x,pCenter.y);
			}

			// Paint rotated label
			g2.drawString(bar.label,pStart.x,pStart.y);
			
			g2.setTransform(at);
		}
		else
		{
			// Paint non rotated label
			g2.drawString(bar.label,pStart.x,pStart.y);
		}
	}
	
	/**
	 * Converts a width expressed in pixel co-ordinates to graph co-ordinates.
	 * @param iW Width expressed in pixel co-ordinates
	 * @return Width expressed in graph co-ordinates
	 */
	private double convertWidthBack(int iW)
	{
		double dW=0.0;
		if (getWorld() instanceof om.graph.uned.World)
		{
			dW=((om.graph.uned.World)getWorld()).convertWidthBack(iW);
		}
		else
		{
			double pW=
					getWorld().convertX(getWorld().getRightX())-getWorld().convertX(getWorld().getLeftX());
			dW=(double)iW/pW*Math.abs(getWorld().getRightX()-getWorld().getLeftX());
		}
		return dW;
	}
	
	/**
	 * Converts an height expressed in pixel co-ordinates to graph co-ordinates.
	 * @param iH Height expressed in pixel co-ordinates
	 * @return Height expressed in graph co-ordinates
	 */
	private double convertHeightBack(int iH)
	{
		double dH=0.0;
		if (getWorld() instanceof om.graph.uned.World)
		{
			dH=((om.graph.uned.World)getWorld()).convertHeightBack(iH);
		}
		else
		{
			double pH=
					getWorld().convertY(getWorld().getBottomY())-getWorld().convertY(getWorld().getTopY());
			dH=(double)iH/pH*Math.abs(getWorld().getBottomY()-getWorld().getTopY());
		}
		return dH;
	}
	
	/**
	 * @param bars List of bars
	 * @return true if top positive value is equal or greater than bottom value respect the base,
	 * false if bottom value is greater than top value respect the base
	 */
	private boolean hasPositiveTrend(List<Bar> bars)
	{
		double base=getBase().doubleValue();
		return Math.abs(getTopValue(bars)-base)>=Math.abs(base-getBottomValue(bars));
	}
	
	/**
	 * @param bars List of bars
	 * @return Top value of bars or base value if it is greater than all values
	 */
	private double getTopValue(List<Bar> bars)
	{
		double top=getBase().doubleValue();
		for (Bar bar:bars)
		{
			if (bar.value>top)
			{
				top=bar.value;
			}
		}
		return top;
	}
	
	/**
	 * @param bars List of bars
	 * @return Bottom value of bars or base values if it is lower than all values
	 */
	private double getBottomValue(List<Bar> bars)
	{
		double bottom=getBase().doubleValue();
		for (Bar bar:bars)
		{
			if (bar.value<bottom)
			{
				bottom=bar.value;
			}
		}
		return bottom;
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
		attribute=attribute.toLowerCase();
		if (attribute.equals("defaultcolour"))
		{
			placeholders.put("defaultbarcolour0",placeholder);
			placeholders.put("defaultlabelcolour0",placeholder);
		}
		else if (attribute.startsWith("defaultcolour"))
		{
			int n=-1;
			try
			{
				n=Integer.parseInt(attribute.substring("defaultcolour".length()));
			}
			catch (NumberFormatException e)
			{
				n=-1;
			}
			if (n!=-1)
			{
				StringBuffer placeholderDefaultBarColour=new StringBuffer();
				placeholderDefaultBarColour.append("defaultbarcolour");
				placeholderDefaultBarColour.append(n);
				placeholders.put(placeholderDefaultBarColour.toString(),placeholder);
				StringBuffer placeholderDefaultLabelColour=new StringBuffer();
				placeholderDefaultLabelColour.append("defaultlabelcolour");
				placeholderDefaultLabelColour.append(n);
				placeholders.put(placeholderDefaultLabelColour.toString(),placeholder);
			}
		}
		else if (attribute.equals("defaultbarcolour"))
		{
			placeholders.put("defaultbarcolour0",placeholder);
		}
		else if (attribute.equals("defaultlabelcolour"))
		{
			placeholders.put("defaultlabelcolour0",placeholder);
		}
		else if (attribute.equals("defaultlabelfont"))
		{
			placeholders.put("defaultlabelfont0",placeholder);
		}
		else
		{
			placeholders.put(attribute,placeholder);
		}
	}
	
	/**
	 * Sets base co-ordinate for bars (expressed in Y co-ordinates for vertical bar charts and 
	 * X co-ordinates for horizontal bar charts).
	 * <p>
	 * @param base Base co-ordinate for bars
	 */
	public void setBase(double base)
	{
		this.base=new Double(base);
		if (placeholders.containsKey("base"))
		{
			placeholders.remove("base");
		}
	}
	
	/**
	 * Sets start co-ordinate for first bar (expressed in X co-ordinates for vertical bar charts and 
	 * Y-coordinates for horizontal bar charts)
	 * @param start Start co-ordinate for first bar
	 */
	public void setStart(double start)
	{
		this.start=new Double(start);
		if (placeholders.containsKey("start"))
		{
			placeholders.remove("start");
		}
	}
	
	/**
	 * Sets bar's width (expressed in X co-ordinates for vertical bar charts and Y co-ordinates 
	 * for horizontal bar charts).<br/><br/>
	 * Note that a negative width is valid if you want to draw bars in inverted direction (for example
	 * from right to left where the usual direction is left to right).<br/><br/>
	 * <b>IMPORTANT</b>: May not use in conjunction with end.
	 * @param width Bar's width
	 * @throws GraphFormatException
	 */
	public void setWidth(double width) throws GraphFormatException
	{
		if (end!=null || placeholders.containsKey("end"))
		{
			throw new GraphFormatException("<barChart>: Can't specify both width and end");
		}
		this.width=new Double(width);
		if (placeholders.containsKey("width"))
		{
			placeholders.remove("width");
		}
	}
	
	/**
	 * Sets end co-ordinate for last bar (expressed in X co-ordinates for vertical bar charts and 
	 * Y-coordinates for horizontal bar charts).<br/><br/>
	 * <b>IMPORTANT</b>: May not use in conjunction with width.
	 * @param end End co-ordinate for last bar
	 * @throws GraphFormatException
	 */
	public void setEnd(double end) throws GraphFormatException
	{
		if (width!=null || placeholders.containsKey("width"))
		{
			throw new GraphFormatException("<barChart>: Can't specify both width and end");
		}
		this.end=new Double(end);
		if (placeholders.containsKey("end"))
		{
			placeholders.remove("end");
		}
	}
	
	/**
	 * Sets bar chart direction, "vertical" for a vertical bar chart (default) or "horizontal" for an
	 * horizontal bar chart. 
	 * @param direction Bar chart direction
	 */
	public void setDirection(String direction)
	{
		if (direction!=null)
		{
			boolean removePlaceholder=false;
			if (direction.equals(VERTICAL))
			{
				vertical=true;
				removePlaceholder=true;
			}
			else if (direction.equals(HORIZONTAL))
			{
				vertical=false;
				removePlaceholder=true;
			}
			if (removePlaceholder && placeholders.containsKey("direction"))
			{
				placeholders.remove("direction");
			}
		}
	}
	
	/**
	 * Sets separation between bars (expressed in pixels).
	 * @param separation Separation between bars
	 */
	public void setSeparation(int separation)
	{
		this.separation=separation;
		if (placeholders.containsKey("separation"))
		{
			placeholders.remove("separation");
		}
	}
	
	/**
	 * Set value of a numbered bar (expressed in Y co-ordinates for vertical bar charts and 
	 * X-coordinates for horizontal bar charts).
	 * @param n Bar's number
	 * @param value Value
	 */
	public void setValue(int n,double value)
	{
		Bar bar=null;
		Integer index=new Integer(n);
		if (bars.containsKey(index))
		{
			bar=bars.get(index);
		}
		else
		{
			bar=new Bar();
		}
		bar.value=value;
		bars.put(index,bar);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("value");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set label of a numbered bar.
	 * @param n Bar's number
	 * @param label Label
	 */
	public void setLabel(int n,String label)
	{
		Bar bar=null;
		Integer index=new Integer(n);
		if (bars.containsKey(index))
		{
			bar=bars.get(index);
		}
		else
		{
			bar=new Bar();
		}
		bar.label=label;
		bars.put(index,bar);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("label");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set color of a numbered bar and its label.
	 * @param n Bar's number
	 * @param colour Color
	 */
	public void setColour(int n,Color colour)
	{
		setBarColour(n,colour);
		setLabelColour(n,colour);
	}
	
	/**
	 * Set color of a numbered bar.
	 * @param n Bar's number
	 * @param colour Color
	 */
	public void setBarColour(int n,Color colour)
	{
		Bar bar=null;
		Integer index=new Integer(n);
		if (bars.containsKey(index))
		{
			bar=bars.get(index);
		}
		else
		{
			bar=new Bar();
		}
		bar.barColour=colour;
		bars.put(index,bar);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("barcolour");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set color of a numbered bar's label.
	 * @param n Bar's number
	 * @param colour Color
	 */
	public void setLabelColour(int n,Color colour)
	{
		Bar bar=null;
		Integer index=new Integer(n);
		if (bars.containsKey(index))
		{
			bar=bars.get(index);
		}
		else
		{
			bar=new Bar();
		}
		bar.labelColour=colour;
		bars.put(index,bar);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("labelcolour");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set font of a numbered bar's label.
	 * @param n Bar's number
	 * @param font Font
	 */
	public void setLabelFont(int n,Font font)
	{
		Bar bar=null;
		Integer index=new Integer(n);
		if (bars.containsKey(index))
		{
			bar=bars.get(index);
		}
		else
		{
			bar=new Bar();
		}
		bar.labelFont=font;
		bars.put(index,bar);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("labelfont");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set first default color for bars and their labels.
	 * @param colour Color
	 */
	public void setDefaultColour(Color colour)
	{
		setDefaultColour(0,colour);
	}
	
	/**
	 * Set a numbered default color for bars and their labels.
	 * @param n Color's number
	 * @param colour Color
	 */
	public void setDefaultColour(int n,Color colour)
	{
		setDefaultBarColour(n,colour);
		setDefaultLabelColour(n,colour);
	}
	
	/**
	 * Set first default color for bars.
	 * @param colour Color
	 */
	public void setDefaultBarColour(Color colour)
	{
		setDefaultBarColour(0,colour);
	}
	
	/**
	 * Set a numbered default color for bars.
	 * @param n Color's number
	 * @param colour Color
	 */
	public void setDefaultBarColour(int n,Color colour)
	{
		defaultBarColours.put(new Integer(n),colour);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("defaultbarcolour");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set first default color for bar's labels.
	 * @param colour Color
	 */
	public void setDefaultLabelColour(Color colour)
	{
		setDefaultLabelColour(0,colour);
	}
	
	/**
	 * Set a numbered default color for bar's labels.
	 * @param n Color's number
	 * @param colour Color
	 */
	public void setDefaultLabelColour(int n,Color colour)
	{
		defaultLabelColours.put(new Integer(n),colour);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("defaultlabelcolour");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set first default font for bar's labels.
	 * @param font Font
	 */
	public void setDefaultLabelFont(Font font)
	{
		setDefaultLabelFont(0,font);
	}
	
	/**
	 * Set a numbered default font for bar's labels.
	 * @param n Color's number
	 * @param font Font
	 */
	public void setDefaultLabelFont(int n,Font font)
	{
		defaultLabelFonts.put(new Integer(n),font);
		StringBuffer placeholderValue=new StringBuffer();
		placeholderValue.append("defaultlabelfont");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			placeholders.remove(placeholderValue.toString());
		}
	}
	
	/**
	 * Set labels position.
	 * @param position Position
	 * @throws GraphFormatException
	 */
	public void setLabelsPosition(String position) throws GraphFormatException
	{
		if (position.equals(BASE_LABEL_POSITION))
		{
			labelsPosition=BASE_POSITION;
		}
		else if (position.equals(VALUE_LABEL_POSITION))
		{
			labelsPosition=VALUE_POSITION;
		}
		else if (position.equals(INSIDE_LABEL_POSITION))
		{
			labelsPosition=INSIDE_POSITION;
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<barChart>: Unexpected position value for labelsPosition: ");
			error.append(position);
			throw new GraphFormatException(error.toString());
		}
		if (placeholders.containsKey("labelsposition"))
		{
			placeholders.remove("labelsposition");
		}
	}
	
	/**
	 * Set labels alignment.
	 * @param align Alignment
	 * @throws GraphFormatException
	 */
	public void setLabelsAlign(String align) throws GraphFormatException
	{
		int alignMask=0;
		align=AnswerChecking.singledWhitespace(align,false);
		String[] alignValues=align.split(" ");
		for (String alignValue:alignValues)
		{
			int alignBit=0;
			if (alignValue.equals(TOP_LABEL_ALIGNMENT))
			{
				alignBit=TOP_ALIGNMENT;
			}
			else if (alignValue.equals(MIDDLE_LABEL_ALIGNMENT))
			{
				alignBit=MIDDLE_ALIGNMENT;
			}
			else if (alignValue.equals(BOTTOM_LABEL_ALIGNMENT))
			{
				alignBit=BOTTOM_ALIGNMENT;
			}
			else if (alignValue.equals(LEFT_LABEL_ALIGNMENT))
			{
				alignBit=LEFT_ALIGNMENT;
			}
			else if (alignValue.equals(CENTER_LABEL_ALIGNMENT))
			{
				alignBit=CENTER_ALIGNMENT;
			}
			else if (alignValue.equals(RIGHT_LABEL_ALIGNMENT))
			{
				alignBit=RIGHT_ALIGNMENT;
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Unexpected alignment value for labelsAlign: ");
				error.append(alignValue);
				throw new GraphFormatException(error.toString());
			}
			if ((alignBit & VERTICAL_ALIGNMENT_MASK)!=0 && (alignMask & VERTICAL_ALIGNMENT_MASK)!=0 && 
					(alignMask & VERTICAL_ALIGNMENT_MASK)!=alignBit )
			{
				throw new GraphFormatException(
						"<barChart> Can't specify more than one vertical alignment (top|middle|bottom).");
			}
			else if ((alignBit & HORIZONTAL_ALIGNMENT_MASK)!=0 && 
					(alignMask & HORIZONTAL_ALIGNMENT_MASK)!=0 && 
					(alignMask & HORIZONTAL_ALIGNMENT_MASK)!=alignBit )
			{
				throw new GraphFormatException(
						"<barChart> Can't specify more than one horizontal alignment (left|center|right).");
			}
			alignMask=alignMask|alignBit;
		}
		if ((alignMask & VERTICAL_ALIGNMENT_MASK)!=0)
		{
			labelsAlign=labelsAlign & HORIZONTAL_ALIGNMENT_MASK;
		}
		if ((alignMask & HORIZONTAL_ALIGNMENT_MASK)!=0)
		{
			labelsAlign=labelsAlign & VERTICAL_ALIGNMENT_MASK;
		}
		labelsAlign=labelsAlign | alignMask;
		if (placeholders.containsKey("labelsalign"))
		{
			placeholders.remove("labelsalign");
		}
	}
	
	/**
	 * Set labels margin (expressed in pixels).
	 * @param margin Labels margin
	 */
	public void setLabelsMargin(int margin)
	{
		labelsMargin=margin;
		if (placeholders.containsKey("labelsmargin"))
		{
			placeholders.remove("labelsmargin");
		}
	}
	
	/**
	 * Set labels rotation flag (true for rotate labels, false for not rotating them).
	 * @param bRotateLabels Labels rotation flag 
	 */
	public void setRotateLabels(boolean bRotateLabels)
	{
		this.bRotateLabels=bRotateLabels;
		if (placeholders.containsKey("rotatelabels"))
		{
			placeholders.remove("rotatelabels");
		}
	}
	
	/**
	 * Chooses alternate rotation direction for labels.
	 * @param bRotateFlip If true, labels are rotated the other way around
	 */
	public void setRotateFlip(boolean bRotateFlip)
	{
		this.bRotateFlip=bRotateFlip;
		if (placeholders.containsKey("rotateflip"))
		{
			placeholders.remove("rotateflip");
		}
	}
	
	/**
	 * Set state flag that indicates if this bar chart is displayed (true) or not (false)
	 * @param display State flag that indicates if this bar chart is displayed or not
	 */
	@Override
	public void setDisplay(boolean display)
	{
		this.display=display;
		if (placeholders.containsKey("display"))
		{
			placeholders.remove("display");
		}
	}
	
	/**
	 * Get base co-ordinate for bars (expressed in Y co-ordinates for vertical bar charts and 
	 * X co-ordinates for horizontal bar charts).
	 * @return Base co-ordinate for bars
	 */
	protected Double getBase()
	{
		Double base=this.base;
		if (placeholders.containsKey("base"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rB=getQuestion().applyPlaceholders(placeholders.get("base"));
				if (!rB.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Double dBase=new Double(rB);
						base=dBase;
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for base: ");
						error.append(rB);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("base"));
				error.append(" for base can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return base;
	}
	
	/**
	 * Get start co-ordinate for first bar (expressed in X co-ordinates for vertical bar charts and 
	 * Y-coordinates for horizontal bar charts).
	 * @return Start co-ordinate for first bar
	 */
	protected Double getStart()
	{
		Double start=this.start;
		if (placeholders.containsKey("start"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rS=getQuestion().applyPlaceholders(placeholders.get("start"));
				if (!rS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Double dStart=new Double(rS);
						start=dStart;
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for start: ");
						error.append(rS);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("start"));
				error.append(" for start can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return start;
	}
	
	/**
	 * Get bar's width (expressed in X co-ordinates for vertical bar charts and Y-coordinates for 
	 * horizontal bar charts).
	 * @return Bar's width
	 */
	protected Double getWidth()
	{
		Double width=this.width;
		if (placeholders.containsKey("width"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rW=getQuestion().applyPlaceholders(placeholders.get("width"));
				if (!rW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Double dWidth=new Double(rW);
						width=dWidth;
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for width: ");
						error.append(rW);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("width"));
				error.append(" for width can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return width;
	}
	
	/**
	 * Get end co-ordinate for lasst bar (expressed in X co-ordinates for vertical bar charts and 
	 * Y-coordinates for horizontal bar charts).
	 * @return End co-ordinate for last bar
	 */
	protected Double getEnd()
	{
		Double end=this.end;
		if (placeholders.containsKey("end"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rE=getQuestion().applyPlaceholders(placeholders.get("end"));
				if (!rE.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Double dEnd=new Double(rE);
						end=dEnd;
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for end: ");
						error.append(rE);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("end"));
				error.append(" for end can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return end;
	}
	
	/**
	 * @return true if this is a vertical bar chart or false if this is an horizontal bar chart
	 */
	protected boolean isVertical()
	{
		boolean vertical=this.vertical;
		if (placeholders.containsKey("direction"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rD=getQuestion().applyPlaceholders(placeholders.get("direction"));
				if (!rD.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					if (rD.equals(VERTICAL))
					{
						vertical=true;
					}
					else if (rD.equals(HORIZONTAL))
					{
						vertical=false;
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected value for direction: ");
						error.append(rD);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("direction"));
				error.append(" for direction can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return vertical;
	}
	
	/**
	 * Get separation between bars (expressed in pixels).
	 * @return Separation between bars
	 */
	protected int getSeparation()
	{
		int separation=this.separation;
		if (placeholders.containsKey("separation"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rS=getQuestion().applyPlaceholders(placeholders.get("separation"));
				if (!rS.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Integer iSeparation=new Integer(rS);
						separation=iSeparation.intValue();
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for separation: ");
						error.append(rS);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("separation"));
				error.append(" for separation can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return separation;
	}
	
	/**
	 * Get number of defined bars.<br/><br/>
	 * Note that it can be called before initializing placeholders. 
	 * @return Number of defined bars
	 */
	protected int getNumberOfBars()
	{
		// Construct a list of bar indexes
		List<Integer> indexes=new ArrayList<Integer>();
		for (Map.Entry<Integer,Bar> bar:this.bars.entrySet())
		{
			indexes.add(bar.getKey());
		}
		
		// Add bar indexes from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				Integer index=null;
				String placeholderName=p.getKey();
				if (placeholderName.startsWith("value"))
				{
					index=new Integer(placeholderName.substring("value".length()));
				}
				else if (placeholderName.startsWith("label"))
				{
					index=new Integer(placeholderName.substring("label".length()));
				}
				else if (placeholderName.startsWith("barcolour"))
				{
					index=new Integer(placeholderName.substring("barcolour".length()));
				}
				else if (placeholderName.startsWith("labelcolour"))
				{
					index=new Integer(placeholderName.substring("labelcolour".length()));
				}
				else if (placeholderName.startsWith("labelfont"))
				{
					index=new Integer(placeholderName.substring("labelfont".length()));
				}
				if (index!=null && !indexes.contains(index))
				{
					indexes.add(index);
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		return indexes.size();
	}
	
	/**
	 * Get all bars of this bar chart sorted by their numbers
	 * @return All bars of this bar chart sorted by their numbers
	 */
	protected List<Bar> getBars()
	{
		List<Bar> bars=new ArrayList<Bar>();
		
		// Construct a list of bars with their indexes
		List<BarWithIndex> indexedBars=new ArrayList<BarWithIndex>();
		for (Map.Entry<Integer,Bar> bar:this.bars.entrySet())
		{
			BarWithIndex indexedBar=new BarWithIndex();
			indexedBar.index=bar.getKey().intValue();
			indexedBar.bar=bar.getValue();
			indexedBars.add(indexedBar);
		}
		
		// Add/update bars from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				Integer index=null;
				int attribute=-1;
				String placeholderName=p.getKey().toLowerCase();
				if (placeholderName.startsWith("value"))
				{
					index=new Integer(placeholderName.substring("value".length()));
					attribute=BAR_VALUE;
				}
				else if (placeholderName.startsWith("label"))
				{
					index=new Integer(placeholderName.substring("label".length()));
					attribute=BAR_LABEL;
				}
				else if (placeholderName.startsWith("barcolour"))
				{
					index=new Integer(placeholderName.substring("barcolour".length()));
					attribute=BAR_BARCOLOUR;
				}
				else if (placeholderName.startsWith("labelcolour"))
				{
					index=new Integer(placeholderName.substring("labelcolour".length()));
					attribute=BAR_LABELCOLOUR;
				}
				else if (placeholderName.startsWith("labelfont"))
				{
					index=new Integer(placeholderName.substring("labelfont".length()));
					attribute=BAR_LABELFONT;
				}
				if (index!=null)
				{
					BarWithIndex indexedBar=null;
					if (attribute==BAR_LABEL)
					{
						String label=p.getValue();
						if (getQuestion().isPlaceholdersInitialized())
						{
							label=getQuestion().applyPlaceholders(label);
						}
						indexedBar=new BarWithIndex();
						indexedBar.index=index.intValue();
						indexedBar.bar=new Bar();
						indexedBar.bar.label=label;
					}
					else if (getQuestion().isPlaceholdersInitialized())
					{
						String r=getQuestion().applyPlaceholders(p.getValue());
						if (!r.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							indexedBar=new BarWithIndex();
							indexedBar.index=index.intValue();
							indexedBar.bar=new Bar();
							switch (attribute)
							{
								case BAR_VALUE:
									try
									{
										Double value=new Double(r);
										indexedBar.bar.value=value.doubleValue();
									}
									catch (NumberFormatException e)
									{
										StringBuffer error=new StringBuffer();
										error.append("<barChart>: Unexpected number value for ");
										error.append(placeholderName);
										error.append(": ");
										error.append(r);
										throw new GraphFormatRuntimeException(error.toString());
									}
									break;
								case BAR_BARCOLOUR:
								case BAR_LABELCOLOUR:
									try
									{
										Color c=getWorld().convertColour(r);
										if (attribute==BAR_BARCOLOUR)
										{
											indexedBar.bar.barColour=c;
										}
										else if (attribute==BAR_LABELCOLOUR)
										{
											indexedBar.bar.labelColour=c;
										}
									}
									catch (GraphFormatException e)
									{
										throw new GraphFormatRuntimeException(e.getMessage(),e);
									}
									break;
								case BAR_LABELFONT:
									try
									{
										Font f=getWorld().convertFont(r);
										indexedBar.bar.labelFont=f;
									}
									catch (GraphFormatException e)
									{
										throw new GraphFormatRuntimeException(e.getMessage(),e);
									}
							}
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Placeholder ");
						error.append(p.getValue());
						error.append(" for ");
						error.append(placeholderName);
						error.append(" can not be replaced");
						error.append(" because placeholders have not been initialized");
						throw new GraphFormatRuntimeException(error.toString());
					}
					if (indexedBar!=null)
					{
						if (indexedBars.contains(indexedBar))
						{
							BarWithIndex auxIndexedBar=indexedBar;
							indexedBar=indexedBars.get(indexedBars.indexOf(auxIndexedBar));
							switch (attribute)
							{
								case BAR_VALUE:
									indexedBar.bar.value=auxIndexedBar.bar.value;
									break;
								case BAR_BARCOLOUR:
									indexedBar.bar.barColour=auxIndexedBar.bar.barColour;
									break;
								case BAR_LABEL:
									indexedBar.bar.label=auxIndexedBar.bar.label;
									break;
								case BAR_LABELCOLOUR:
									indexedBar.bar.labelColour=auxIndexedBar.bar.labelColour;
									break;
								case BAR_LABELFONT:
									indexedBar.bar.labelFont=auxIndexedBar.bar.labelFont;
							}
						}
						else
						{
							indexedBars.add(indexedBar);
						}
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		
		// Sort the list of bars with their indexes
		Collections.sort(indexedBars);
		
		// Constuct the list of bars ordered by their numbers
		for (BarWithIndex indexedBar:indexedBars)
		{
			bars.add(indexedBar.bar);
		}
		
		// If we not get bars we throw a GraphFormatRuntimeException
		if (bars.size()==0)
		{
			throw new GraphFormatRuntimeException("<barChart>: Must specify at least 1 bar");
		}
		
		return bars;
	}
	
	/**
	 * Get a numbered bar of this bar chart
	 * @param n Bar's number
	 * @return Numbered bar of this bar chart
	 */
	protected Bar getBar(int n)
	{
		Bar bar=bars.get(new Integer(n));
		StringBuffer placeholderValue=new StringBuffer("value");
		placeholderValue.append(n);
		if (placeholders.containsKey(placeholderValue.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rV=getQuestion().applyPlaceholders(placeholders.get(placeholderValue.toString()));
				if (!rV.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Double value=new Double(rV);
						if (bar==null)
						{
							bar=new Bar();
						}
						bar.value=value.doubleValue();
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for ");
						error.append(placeholderValue);
						error.append(": ");
						error.append(rV);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderValue.toString()));
				error.append(" for ");
				error.append(placeholderValue);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		StringBuffer placeholderLabel=new StringBuffer("label");
		placeholderLabel.append(n);
		if (placeholders.containsKey(placeholderLabel.toString()))
		{
			String rL=placeholderLabel.toString();
			if (getQuestion().isPlaceholdersInitialized())
			{
				rL=getQuestion().applyPlaceholders(rL);
			}
			if (bar==null)
			{
				bar=new Bar();
			}
			bar.label=rL;
		}
		StringBuffer placeholderBarColour=new StringBuffer("barcolour");
		placeholderBarColour.append(n);
		if (placeholders.containsKey(placeholderBarColour.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rC=getQuestion().applyPlaceholders(placeholders.get(placeholderBarColour.toString()));
				if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Color c=getWorld().convertColour(rC);
						if (bar==null)
						{
							bar=new Bar();
						}
						bar.barColour=c;
					}
					catch (GraphFormatException e)
					{
						throw new GraphFormatRuntimeException(e.getMessage(),e);
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderBarColour.toString()));
				error.append(" for ");
				error.append(placeholderBarColour);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		StringBuffer placeholderLabelColour=new StringBuffer("labelcolour");
		placeholderLabelColour.append(n);
		if (placeholders.containsKey(placeholderLabelColour.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rC=getQuestion().applyPlaceholders(placeholders.get(placeholderLabelColour.toString()));
				if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Color c=getWorld().convertColour(rC);
						if (bar==null)
						{
							bar=new Bar();
						}
						bar.labelColour=c;
					}
					catch (GraphFormatException e)
					{
						throw new GraphFormatRuntimeException(e.getMessage(),e);
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderLabelColour.toString()));
				error.append(" for ");
				error.append(placeholderLabelColour);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		StringBuffer placeholderLabelFont=new StringBuffer("labelfont");
		placeholderLabelFont.append(n);
		if (placeholders.containsKey(placeholderLabelFont.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rF=getQuestion().applyPlaceholders(placeholders.get(placeholderLabelFont.toString()));
				if (!rF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Font f=getWorld().convertFont(rF);
						if (bar==null)
						{
							bar=new Bar();
						}
						bar.labelFont=f;
					}
					catch (GraphFormatException e)
					{
						throw new GraphFormatRuntimeException(e.getMessage(),e);
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderLabelFont.toString()));
				error.append(" for ");
				error.append(placeholderLabelFont);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bar;
	}
	
	/**
	 * Get number of defined default bar colors.<br/><br/>
	 * Note that it can be called before initializing placeholders. 
	 * @return Number of defined default bar colors
	 */
	protected int getNumberOfDefaultBarColours()
	{
		// Construct a list of default bar colors indexes
		List<Integer> indexes=new ArrayList<Integer>();
		for (Map.Entry<Integer,Color> defaultBarColour:this.defaultBarColours.entrySet())
		{
			indexes.add(defaultBarColour.getKey());
		}
		
		// Add default bar colors indexes from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey();
				if (placeholderName.startsWith("defaultbarcolour"))
				{
					Integer index=new Integer(placeholderName.substring("defaultbarcolour".length()));
					if (!indexes.contains(index))
					{
						indexes.add(index);
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		return indexes.size();
	}
	
	/**
	 * Get all default bars colors of this bar chart sorted by their numbers
	 * @return All default bars colors of this bar chart sorted by their numbers
	 */
	protected List<Color> getDefaultBarColours()
	{
		List<Color> defaultBarColours=new ArrayList<Color>();
		
		// Construct a list of default bar colors with their indexes
		List<ColorWithIndex> indexedDefaultBarColours=new ArrayList<ColorWithIndex>();
		for (Map.Entry<Integer,Color> defaultBarColour:this.defaultBarColours.entrySet())
		{
			ColorWithIndex indexedDefaultBarColour=new ColorWithIndex();
			indexedDefaultBarColour.index=defaultBarColour.getKey().intValue();
			indexedDefaultBarColour.colour=defaultBarColour.getValue();
			indexedDefaultBarColours.add(indexedDefaultBarColour);
		}
		
		// Add/update default bar colors from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey().toLowerCase();
				if (placeholderName.startsWith("defaultbarcolour"))
				{
					Color c=null;
					int index=Integer.parseInt(placeholderName.substring("defaultbarcolour".length()));
					if (getQuestion().isPlaceholdersInitialized())
					{
						String rC=getQuestion().applyPlaceholders(p.getValue());
						if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								c=getWorld().convertColour(rC);
							}
							catch (GraphFormatException e)
							{
								throw new GraphFormatRuntimeException(e.getMessage(),e);
							}
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Placeholder ");
						error.append(p.getValue());
						error.append(" for ");
						error.append(placeholderName);
						error.append(" can not be replaced");
						error.append(" because placeholders have not been initialized");
						throw new GraphFormatRuntimeException(error.toString());
					}
					if (c!=null)
					{
						ColorWithIndex indexedDefaultBarColour=new ColorWithIndex();
						indexedDefaultBarColour.index=index;
						indexedDefaultBarColour.colour=null;
						if (indexedDefaultBarColours.contains(indexedDefaultBarColour))
						{
							indexedDefaultBarColour=indexedDefaultBarColours.get(
									indexedDefaultBarColours.indexOf(indexedDefaultBarColour));
							indexedDefaultBarColour.colour=c;
						}
						else
						{
							indexedDefaultBarColour.colour=c;
							indexedDefaultBarColours.add(indexedDefaultBarColour);
						}
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		
		// Sort the list of default bar colours with their indexes
		Collections.sort(indexedDefaultBarColours);
		
		// Constuct the list of default bar colours ordered by their numbers
		for (ColorWithIndex indexedDefaultBarColour:indexedDefaultBarColours)
		{
			defaultBarColours.add(indexedDefaultBarColour.colour);
		}
		
		// If we not get colors we return world's default color
		if (defaultBarColours.size()==0)
		{
			try
			{
				defaultBarColours.add(getWorld().convertColour("fg"));
			}
			catch (GraphFormatException e)
			{
			}
		}
		
		return defaultBarColours;
	}
	
	/**
	 * Get a numbered default bar color of this bar chart
	 * @param n Color's number
	 * @return Numbered default bar color of this bar chart
	 */
	protected Color getDefaultBarColour(int n)
	{
		Color defaultBarColour=defaultBarColours.get(new Integer(n));
		StringBuffer placeholderDefaultBarColour=new StringBuffer("defaultbarcolour");
		placeholderDefaultBarColour.append(n);
		if (placeholders.containsKey(placeholderDefaultBarColour.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rC=getQuestion().applyPlaceholders(placeholders.get(
						placeholderDefaultBarColour.toString()));
				if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Color c=getWorld().convertColour(rC);
						defaultBarColour=c;
					}
					catch (GraphFormatException e)
					{
						throw new GraphFormatRuntimeException(e.getMessage(),e);
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderDefaultBarColour.toString()));
				error.append(" for ");
				error.append(placeholderDefaultBarColour);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return defaultBarColour;
	}
	
	/**
	 * Get number of defined default label colors.<br/><br/>
	 * Note that it can be called before initializing placeholders. 
	 * @return Number of defined default label colors
	 */
	protected int getNumberOfDefaultLabelColours()
	{
		// Construct a list of default label colors indexes
		List<Integer> indexes=new ArrayList<Integer>();
		for (Map.Entry<Integer,Color> defaultLabelColour:this.defaultLabelColours.entrySet())
		{
			indexes.add(defaultLabelColour.getKey());
		}
		
		// Add default label colors indexes from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey();
				if (placeholderName.startsWith("defaultlabelcolour"))
				{
					Integer index=new Integer(placeholderName.substring("defaultlabelcolour".length()));
					if (!indexes.contains(index))
					{
						indexes.add(index);
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		return indexes.size();
	}
	
	/**
	 * Get all default labels colors of this bar chart sorted by their numbers
	 * @return All default labels colors of this bar chart sorted by their numbers
	 */
	protected List<Color> getDefaultLabelColours()
	{
		List<Color> defaultLabelColours=new ArrayList<Color>();
		
		// Construct a list of default label colors with their indexes
		List<ColorWithIndex> indexedDefaultLabelColours=new ArrayList<ColorWithIndex>();
		for (Map.Entry<Integer,Color> defaultLabelColour:this.defaultLabelColours.entrySet())
		{
			ColorWithIndex indexedDefaultLabelColour=new ColorWithIndex();
			indexedDefaultLabelColour.index=defaultLabelColour.getKey().intValue();
			indexedDefaultLabelColour.colour=defaultLabelColour.getValue();
			indexedDefaultLabelColours.add(indexedDefaultLabelColour);
		}
		
		// Add/update default label colors from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey().toLowerCase();
				if (placeholderName.startsWith("defaultlabelcolour"))
				{
					Color c=null;
					int index=Integer.parseInt(placeholderName.substring("defaultlabelcolour".length()));
					if (getQuestion().isPlaceholdersInitialized())
					{
						String rC=getQuestion().applyPlaceholders(p.getValue());
						if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								c=getWorld().convertColour(rC);
							}
							catch (GraphFormatException e)
							{
								throw new GraphFormatRuntimeException(e.getMessage(),e);
							}
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Placeholder ");
						error.append(p.getValue());
						error.append(" for ");
						error.append(placeholderName);
						error.append(" can not be replaced");
						error.append(" because placeholders have not been initialized");
						throw new GraphFormatRuntimeException(error.toString());
					}
					if (c!=null)
					{
						ColorWithIndex indexedDefaultLabelColour=new ColorWithIndex();
						indexedDefaultLabelColour.index=index;
						indexedDefaultLabelColour.colour=null;
						if (indexedDefaultLabelColours.contains(indexedDefaultLabelColour))
						{
							indexedDefaultLabelColour=indexedDefaultLabelColours.get(
									indexedDefaultLabelColours.indexOf(indexedDefaultLabelColour));
							indexedDefaultLabelColour.colour=c;
						}
						else
						{
							indexedDefaultLabelColour.colour=c;
							indexedDefaultLabelColours.add(indexedDefaultLabelColour);
						}
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		
		// Sort the list of default label colours with their indexes
		Collections.sort(indexedDefaultLabelColours);
		
		// Constuct the list of default label colours ordered by their numbers
		for (ColorWithIndex indexedDefaultLabelColour:indexedDefaultLabelColours)
		{
			defaultLabelColours.add(indexedDefaultLabelColour.colour);
		}
		
		// If we not get colors we return world's default color
		if (defaultLabelColours.size()==0)
		{
			try
			{
				defaultLabelColours.add(getWorld().convertColour("fg"));
			}
			catch (GraphFormatException e)
			{
			}
		}
		
		return defaultLabelColours;
	}
	
	/**
	 * Get a numbered default label color of this bar chart
	 * @param n Color's number
	 * @return Numbered default label color of this bar chart
	 */
	protected Color getDefaultLabelColour(int n)
	{
		Color defaultLabelColour=defaultLabelColours.get(new Integer(n));
		StringBuffer placeholderDefaultLabelColour=new StringBuffer("defaultlabelcolour");
		placeholderDefaultLabelColour.append(n);
		if (placeholders.containsKey(placeholderDefaultLabelColour.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rC=getQuestion().applyPlaceholders(placeholders.get(
						placeholderDefaultLabelColour.toString()));
				if (!rC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Color c=getWorld().convertColour(rC);
						defaultLabelColour=c;
					}
					catch (GraphFormatException e)
					{
						throw new GraphFormatRuntimeException(e.getMessage(),e);
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderDefaultLabelColour.toString()));
				error.append(" for ");
				error.append(placeholderDefaultLabelColour);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return defaultLabelColour;
	}
	
	/**
	 * Get number of defined default label fonts.<br/><br/>
	 * Note that it can be called before initializing placeholders. 
	 * @return Number of defined default label fonts
	 */
	protected int getNumberOfDefaultLabelFonts()
	{
		// Construct a list of default label fonts indexes
		List<Integer> indexes=new ArrayList<Integer>();
		for (Map.Entry<Integer,Font> defaultLabelFont:this.defaultLabelFonts.entrySet())
		{
			indexes.add(defaultLabelFont.getKey());
		}
		
		// Add default label fonts indexes from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey();
				if (placeholderName.startsWith("defaultlabelfont"))
				{
					Integer index=new Integer(placeholderName.substring("defaultlabelfont".length()));
					if (!indexes.contains(index))
					{
						indexes.add(index);
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		return indexes.size();
	}
	
	/**
	 * Get all default labels fonts of this bar chart sorted by their numbers
	 * @return All default labels fonts of this bar chart sorted by their numbers
	 */
	protected List<Font> getDefaultLabelFonts()
	{
		List<Font> defaultLabelFonts=new ArrayList<Font>();
		
		// Construct a list of default label fonts with their indexes
		List<FontWithIndex> indexedDefaultLabelFonts=new ArrayList<FontWithIndex>();
		for (Map.Entry<Integer,Font> defaultLabelFont:this.defaultLabelFonts.entrySet())
		{
			FontWithIndex indexedDefaultLabelFont=new FontWithIndex();
			indexedDefaultLabelFont.index=defaultLabelFont.getKey().intValue();
			indexedDefaultLabelFont.font=defaultLabelFont.getValue();
			indexedDefaultLabelFonts.add(indexedDefaultLabelFont);
		}
		
		// Add/update default label fonts from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey().toLowerCase();
				if (placeholderName.startsWith("defaultlabelfont"))
				{
					Font f=null;
					int index=Integer.parseInt(placeholderName.substring("defaultlabelfont".length()));
					if (getQuestion().isPlaceholdersInitialized())
					{
						String rF=getQuestion().applyPlaceholders(p.getValue());
						if (!rF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								f=getWorld().convertFont(rF);
							}
							catch (GraphFormatException e)
							{
								throw new GraphFormatRuntimeException(e.getMessage(),e);
							}
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Placeholder ");
						error.append(p.getValue());
						error.append(" for ");
						error.append(placeholderName);
						error.append(" can not be replaced");
						error.append(" because placeholders have not been initialized");
						throw new GraphFormatRuntimeException(error.toString());
					}
					if (f!=null)
					{
						FontWithIndex indexedDefaultLabelFont=new FontWithIndex();
						indexedDefaultLabelFont.index=index;
						indexedDefaultLabelFont.font=null;
						if (indexedDefaultLabelFonts.contains(indexedDefaultLabelFont))
						{
							indexedDefaultLabelFont=indexedDefaultLabelFonts.get(
									indexedDefaultLabelFonts.indexOf(indexedDefaultLabelFont));
							indexedDefaultLabelFont.font=f;
						}
						else
						{
							indexedDefaultLabelFont.font=f;
							indexedDefaultLabelFonts.add(indexedDefaultLabelFont);
						}
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		
		// Sort the list of default label fonts with their indexes
		Collections.sort(indexedDefaultLabelFonts);
		
		// Constuct the list of default label fonts ordered by their numbers
		for (FontWithIndex indexedDefaultLabelFont:indexedDefaultLabelFonts)
		{
			defaultLabelFonts.add(indexedDefaultLabelFont.font);
		}
		
		// If we not get fonts we return world's default font
		if (defaultLabelFonts.size()==0)
		{
			defaultLabelFonts.add(getWorld().getDefaultFont(true));
		}
		
		return defaultLabelFonts;
	}
	
	/**
	 * Get a numbered default label font of this bar chart
	 * @param n Font's number
	 * @return Numbered default label font of this bar chart
	 */
	protected Font getDefaultLabelFont(int n)
	{
		Font defaultLabelFont=defaultLabelFonts.get(new Integer(n));
		StringBuffer placeholderDefaultLabelFont=new StringBuffer("defaultlabelfont");
		placeholderDefaultLabelFont.append(n);
		if (placeholders.containsKey(placeholderDefaultLabelFont.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rF=getQuestion().applyPlaceholders(placeholders.get(
						placeholderDefaultLabelFont.toString()));
				if (!rF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Font f=getWorld().convertFont(rF);
						defaultLabelFont=f;
					}
					catch (GraphFormatException e)
					{
						throw new GraphFormatRuntimeException(e.getMessage(),e);
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get(placeholderDefaultLabelFont.toString()));
				error.append(" for ");
				error.append(placeholderDefaultLabelFont);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return defaultLabelFont;
	}
	
	/**
	 * Get labels position.
	 * @return Labels position
	 */
	protected int getLabelsPosition()
	{
		int position=labelsPosition;
		if (placeholders.containsKey("labelsposition"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rP=getQuestion().applyPlaceholders(placeholders.get("labelsposition"));
				if (!rP.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					if (rP.equals(BASE_LABEL_POSITION))
					{
						position=BASE_POSITION;
					}
					else if (rP.equals(VALUE_LABEL_POSITION))
					{
						position=VALUE_POSITION;
					}
					else if (rP.equals(INSIDE_LABEL_POSITION))
					{
						position=INSIDE_POSITION;
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected position value for labelsPosition: ");
						error.append(rP);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("labelsposition"));
				error.append(" for labelsPosition can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return position;
	}
	
	/**
	 * Get labels alignment.
	 * @return Labels alignment
	 */
	protected int getLabelsAlign()
	{
		int align=labelsAlign;
		if (placeholders.containsKey("labelsalign"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rA=getQuestion().applyPlaceholders(placeholders.get("labelsalign"));
				if (!rA.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					int alignMask=0;
					String[] alignValues=rA.split(" ");
					for (String alignValue:alignValues)
					{
						int alignBit=0;
						if (alignValue.equals(TOP_LABEL_ALIGNMENT))
						{
							alignBit=TOP_ALIGNMENT;
						}
						else if (alignValue.equals(MIDDLE_LABEL_ALIGNMENT))
						{
							alignBit=MIDDLE_ALIGNMENT;
						}
						else if (alignValue.equals(BOTTOM_LABEL_ALIGNMENT))
						{
							alignBit=BOTTOM_ALIGNMENT;
						}
						else if (alignValue.equals(LEFT_LABEL_ALIGNMENT))
						{
							alignBit=LEFT_ALIGNMENT;
						}
						else if (alignValue.equals(CENTER_LABEL_ALIGNMENT))
						{
							alignBit=CENTER_ALIGNMENT;
						}
						else if (alignValue.equals(RIGHT_LABEL_ALIGNMENT))
						{
							alignBit=RIGHT_ALIGNMENT;
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<barChart>: Unexpected alignment value for labelsAlign: ");
							error.append(alignValue);
							throw new GraphFormatRuntimeException(error.toString());
						}
						if ((alignBit & VERTICAL_ALIGNMENT_MASK)!=0 && 
								(alignMask & VERTICAL_ALIGNMENT_MASK)!=0 && 
								(alignMask & VERTICAL_ALIGNMENT_MASK)!=alignBit )
						{
							throw new GraphFormatRuntimeException(
									"<barChart> Can't specify more than one vertical alignment (top|middle|bottom).");
						}
						else if ((alignBit & HORIZONTAL_ALIGNMENT_MASK)!=0 && 
								(alignMask & HORIZONTAL_ALIGNMENT_MASK)!=0 && 
								(alignMask & HORIZONTAL_ALIGNMENT_MASK)!=alignBit )
						{
							throw new GraphFormatRuntimeException(
									"<barChart> Can't specify more than one horizontal alignment (left|center|right).");
						}
						alignMask=alignMask|alignBit;
					}
					if ((alignMask & VERTICAL_ALIGNMENT_MASK)!=0)
					{
						align=align & HORIZONTAL_ALIGNMENT_MASK;
					}
					if ((alignMask & HORIZONTAL_ALIGNMENT_MASK)!=0)
					{
						align=align & VERTICAL_ALIGNMENT_MASK;
					}
					align=align | alignMask;
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("labelsalign"));
				error.append(" for labelsAlign can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return align;
	}
	
	/**
	 * Get labels margin (expressed in pixels).
	 * @return Labels margin
	 */
	protected int getLabelsMargin()
	{
		int margin=labelsMargin;
		if (placeholders.containsKey("labelsmargin"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rM=getQuestion().applyPlaceholders(placeholders.get("labelsmargin"));
				if (!rM.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Integer iMargin=new Integer(rM);
						margin=iMargin.intValue();
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected number value for labelsMargin: ");
						error.append(rM);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("labelsmargin"));
				error.append(" for labelsMargin can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return margin;
	}
	
	/**
	 * Get labels rotation flag (true if labels are rotated, false otherwise).
	 * @return Labels rotation flag 
	 */
	protected boolean isRotateLabels()
	{
		boolean bRotateLabels=this.bRotateLabels;
		if (placeholders.containsKey("rotatelabels"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rR=getQuestion().applyPlaceholders(placeholders.get("rotatelabels"));
				if (!rR.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					if (rR.equals("yes"))
					{
						bRotateLabels=true;
					}
					else if (rR.equals("no"))
					{
						bRotateLabels=false;
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected boolean value for rotateLabels: ");
						error.append(rR);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("rotatelabels"));
				error.append(" for rotateLabels can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bRotateLabels;
	}
	
	/**
	 * Get rotation direction for labels (true for clockwise direction, false for counterclockwise 
	 * direction).
	 * @return Rotation direction for labels
	 */
	protected boolean isRotateFlip()
	{
		boolean bRotateFlip=this.bRotateFlip;
		if (placeholders.containsKey("rotateflip"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rF=getQuestion().applyPlaceholders(placeholders.get("rotateflip"));
				if (!rF.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					if (rF.equals("yes"))
					{
						bRotateFlip=true;
					}
					else if (rF.equals("no"))
					{
						bRotateFlip=false;
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<barChart>: Unexpected boolean value for rotateFlip: ");
						error.append(rF);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("rotateflip"));
				error.append(" for rotateFlip can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bRotateFlip;
	}
	
	/**
	 * Get state flag that indicates if this bar chart is displayed or not
	 * @return true if this bar chart is displayed, false otherwise
	 */
	protected boolean isDisplay()
	{
		boolean bDisplay=display;
		if (placeholders.containsKey("display"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String sDisplay=getQuestion().applyPlaceholders(placeholders.get("display"));
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
						error.append("<barChart>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<barChart>: Placeholder ");
				error.append(placeholders.get("display"));
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
