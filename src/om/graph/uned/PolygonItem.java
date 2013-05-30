package om.graph.uned;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import om.graph.GraphFormatException;
import om.graph.GraphItem;
import om.graph.GraphPoint;
import om.graph.GraphScalar;
import om.graph.World;
import om.stdcomponent.uned.CanvasComponent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 04-10-2011 - dballestin
/** Draws polygons in the graph space. */
public class PolygonItem extends GraphItem implements Displayable,Replaceable
{
	/** Standard question */
	private StandardQuestion sq;
	
	/** Polygon's vertexes */
	private Map<Integer,GraphPoint> vertexes=new HashMap<Integer,GraphPoint>();
	
	/** Fill color */
	private Color cFill=null;
	
	/** Line color */
	private Color cLine=null;
	
	/** Outline thickness */
	private int iLineWidth=-1;
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** State flag that indicates if this polygon is displayed or not */
	private boolean display=true;
	
	/** Utility class to order vertexes */
	private class VertexWithIndex implements Comparable<VertexWithIndex>
	{
		public int index;
		public GraphPoint vertex;
		
		@Override
		public int compareTo(VertexWithIndex o)
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
			if (obj!=null && obj instanceof VertexWithIndex)
			{
				isEquals=index==((VertexWithIndex)obj).index;
			}
			return isEquals;
		}
	}
	
	/**
	 * @param w coordinate system.
	 * @throws GraphFormatException
	 */
	public PolygonItem(World w) throws GraphFormatException
	{
		super(w);
	}
	
	@Override
	public void init() throws GraphFormatException
	{
		if (getNumberOfVertexes()<3)
		{
			throw new GraphFormatException("<polygon>: Must specify at least 3 vertexes");
		}
	}
	
	@Override
	public void paint(Graphics2D g2)
	{
		if (isDisplay())
		{
			// Work out the vertexes and convert to pixels
			List<GraphPoint> vertexes=getVertexes();
			List<Point> points=new ArrayList<Point>(vertexes.size());
			for (GraphPoint vertex:vertexes)
			{
				points.add(vertex.convert(getWorld()));
			}
			
			// Construct polygon shape
			GeneralPath polygon=new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());
			polygon.moveTo(points.get(0).x,points.get(0).y);
			for (int index=1;index<points.size();index++)
			{
				polygon.lineTo(points.get(index).x,points.get(index).y);
			}
			polygon.closePath();
			
			// Draw the fill
			if (getFillColour()!=null)
			{
				g2.setColor(getFillColour());
				g2.fill(polygon);
			}
			
			// Draw outline
			if (getLineWidth()>0)
			{
				g2.setStroke(new BasicStroke(getLineWidth()));
				g2.setColor(getLineColour());
				g2.draw(polygon);
			}
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
		if (attribute.equalsIgnoreCase("x"))
		{
			placeholders.put("x0",placeholder);
		}
		else if (attribute.equalsIgnoreCase("y"))
		{
			placeholders.put("y0",placeholder);
		}
		else
		{
			placeholders.put(attribute.toLowerCase(),placeholder);
		}
	}
	
	/**
	 * Set first vertex of polygon, X co-ordinate.
	 * @param gsX X co-ordinate
	 */
	public void setX(GraphScalar gsX)
	{
		setX(0,gsX);
	}
	
	/**
	 * Set numbered vertex of polygon, X co-ordinate.
	 * @param n Vertex's number
	 * @param gsX X co-ordinate
	 */
	public void setX(int n,GraphScalar gsX)
	{
		GraphPoint gp=null;
		Integer index=new Integer(n);
		if (vertexes.containsKey(index))
		{
			gp=vertexes.get(index);
		}
		else
		{
			gp=GraphPoint.ZERO;
		}
		gp=gp.newX(gsX);
		vertexes.put(index,gp);
		StringBuffer placeholderX=new StringBuffer();
		placeholderX.append("x");
		placeholderX.append(n);
		if (placeholders.containsKey(placeholderX.toString()))
		{
			placeholders.remove(placeholderX.toString());
		}
	}
	
	/**
	 * Set first vertex of polygon, Y co-ordinate.
	 * @param gsY Y co-ordinate
	 */
	public void setY(GraphScalar gsY)
	{
		setY(0,gsY);
	}
	
	/**
	 * Set numbered vertex of polygon, Y co-ordinate.
	 * @param n Vertex's number
	 * @param gsY Y co-ordinate
	 */
	public void setY(int n,GraphScalar gsY)
	{
		GraphPoint gp=null;
		Integer index=new Integer(n);
		if (vertexes.containsKey(index))
		{
			gp=vertexes.get(index);
		}
		else
		{
			gp=GraphPoint.ZERO;
		}
		gp=gp.newY(gsY);
		vertexes.put(index,gp);
		StringBuffer placeholderY=new StringBuffer();
		placeholderY.append("y");
		placeholderY.append(n);
		if (placeholders.containsKey(placeholderY.toString()))
		{
			placeholders.remove(placeholderY.toString());
		}
	}
	
	/**
	 * Sets fill colour.
	 * <p>
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c Fill colour
	 */
	public void setFillColour(Color c)
	{
		cFill=c;
		if (placeholders.containsKey("fillcolour"))
		{
			placeholders.remove("fillcolour");
		}
	}
	
	/**
	 * Sets outline colour. Calling this (except with null) also turns on the
	 * line in the first place.
	 * <p>
	 * Appropriate colours can be obtained from {@link World#convertColour(String)}.
	 * @param c New colour (set null for no outline)
	 */
	public void setLineColour(Color c)
	{
		cLine=c;
		if (c!=null && iLineWidth==-1 && !placeholders.containsKey("linewidth"))
		{
			iLineWidth=1;
		}
		if (placeholders.containsKey("linecolour"))
		{
			placeholders.remove("linecolour");
		}
	}
	
	/**
	 * Sets line width in pixels. Also turns on outline if it wasn't already,
	 * setting its colour to the colour constant 'fg'.
	 * @param i Line width
	 * @throws GraphFormatException If 'text' isn't defined
	 */
	public void setLineWidth(int i) throws GraphFormatException
	{
		iLineWidth=i;
		if (i>0 && cLine==null && !placeholders.containsKey("linecolour"))
		{
			cLine=getWorld().convertColour("fg");
		}
		if (placeholders.containsKey("linewidth"))
		{
			placeholders.remove("linewidth");
		}
	}
	
	/**
	 * Set state flag that indicates if this polygon is displayed (true) or not (false)
	 * @param display State flag that indicates if this polygon is displayed or not
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
	 * Get number of defined vertexes.<br/><br/>
	 * Note that it can be called before initializing placeholders. 
	 * @return Number of defined vertexes
	 */
	protected int getNumberOfVertexes()
	{
		// Construct a list of vertex indexes
		List<Integer> indexes=new ArrayList<Integer>();
		for (Map.Entry<Integer,GraphPoint> v:this.vertexes.entrySet())
		{
			indexes.add(v.getKey());
		}
		
		// Add vertex indexes from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey();
				if ((placeholderName.charAt(0)=='x' || placeholderName.charAt(0)=='y') && 
						(placeholderName.length()>1))
				{
					Integer index=new Integer(placeholderName.substring(1));
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
	 * Get all vertexes of this polygon sorted by their numbers
	 * @return All vertexes of this polygon sorted by their numbers
	 */
	protected List<GraphPoint> getVertexes()
	{
		List<GraphPoint> vertexes=new ArrayList<GraphPoint>();
		
		// Construct a list of vertexes with their indexes
		List<VertexWithIndex> indexedVertexes=new ArrayList<VertexWithIndex>();
		for (Map.Entry<Integer,GraphPoint> v:this.vertexes.entrySet())
		{
			VertexWithIndex indexedVertex=new VertexWithIndex();
			indexedVertex.index=v.getKey().intValue();
			indexedVertex.vertex=v.getValue();
			indexedVertexes.add(indexedVertex);
		}
		
		// Add/update vertexes from placeholders
		for (Map.Entry<String,String> p:placeholders.entrySet())
		{
			try
			{
				String placeholderName=p.getKey();
				if ((placeholderName.charAt(0)=='x' || placeholderName.charAt(0)=='y') && 
						(placeholderName.length()>1))
				{
					int index=Integer.parseInt(placeholderName.substring(1));
					GraphScalar value=null;
					if (getQuestion().isPlaceholdersInitialized())
					{
						String rP=getQuestion().applyPlaceholders(p.getValue());
						if (!rP.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
						{
							try
							{
								value=new GraphScalar(rP);
							}
							catch (NumberFormatException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<polygon>: Unexpected number value for ");
								error.append(placeholderName);
								error.append(": ");
								error.append(rP);
								throw new GraphFormatRuntimeException(error.toString());
							}
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<polygon>: Placeholder ");
						error.append(p.getValue());
						error.append(" for ");
						error.append(placeholderName);
						error.append(" can not be replaced");
						error.append(" because placeholders have not been initialized");
						throw new GraphFormatRuntimeException(error.toString());
					}
					VertexWithIndex indexedVertex=new VertexWithIndex();
					indexedVertex.index=index;
					indexedVertex.vertex=null;
					if (indexedVertexes.contains(indexedVertex))
					{
						indexedVertex=indexedVertexes.get(indexedVertexes.indexOf(indexedVertex));
						if (indexedVertex.vertex==null)
						{
							indexedVertex.vertex=GraphPoint.ZERO;
						}
						if (placeholderName.charAt(0)=='x')
						{
							indexedVertex.vertex=indexedVertex.vertex.newX(value);
						}
						else if (placeholderName.charAt(0)=='y')
						{
							indexedVertex.vertex=indexedVertex.vertex.newY(value);
						}
					}
					else
					{
						if (indexedVertex.vertex==null)
						{
							indexedVertex.vertex=GraphPoint.ZERO;
						}
						if (placeholderName.charAt(0)=='x')
						{
							indexedVertex.vertex=indexedVertex.vertex.newX(value);
						}
						else if (placeholderName.charAt(0)=='y')
						{
							indexedVertex.vertex=indexedVertex.vertex.newY(value);
						}
						indexedVertexes.add(indexedVertex);
					}
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		
		// Sort the list of vertexes with their indexes
		Collections.sort(indexedVertexes);
		
		// Constuct the list of vertexes ordered by their numbers
		for (VertexWithIndex indexedVertex:indexedVertexes)
		{
			vertexes.add(indexedVertex.vertex);
		}
		return vertexes;
	}
	
	/**
	 * Get a numbered vertex of this polygon
	 * @param n Vertex's number
	 * @return Numbered vertex of this polygon
	 */
	protected GraphPoint getVertex(int n)
	{
		GraphPoint vertex=vertexes.get(new Integer(n));
		StringBuffer placeholderX=new StringBuffer("x");
		placeholderX.append(n);
		if (placeholders.containsKey(placeholderX.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rX=getQuestion().applyPlaceholders(placeholders.get(placeholderX.toString()));
				if (!rX.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						GraphScalar gsX=new GraphScalar(rX);
						if (vertex==null)
						{
							vertex=GraphPoint.ZERO;
						}
						vertex=vertex.newX(gsX);
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<polygon>: Unexpected number value for ");
						error.append(placeholderX);
						error.append(": ");
						error.append(rX);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<polygon>: Placeholder ");
				error.append(placeholders.get(placeholderX.toString()));
				error.append(" for ");
				error.append(placeholderX);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		StringBuffer placeholderY=new StringBuffer("y");
		placeholderY.append(n);
		if (placeholders.containsKey(placeholderY.toString()))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rY=getQuestion().applyPlaceholders(placeholders.get(placeholderY.toString()));
				if (!rY.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						GraphScalar gsY=new GraphScalar(rY);
						if (vertex==null)
						{
							vertex=GraphPoint.ZERO;
						}
						vertex=vertex.newY(gsY);
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<polygon>: Unexpected number value for ");
						error.append(placeholderY);
						error.append(": ");
						error.append(rY);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<polygon>: Placeholder ");
				error.append(placeholders.get(placeholderY.toString()));
				error.append(" for ");
				error.append(placeholderY);
				error.append(" can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return vertex;
	}
	
	/**
	 * Get fill color
	 * @return Fill color
	 */
	protected Color getFillColour()
	{
		Color c=cFill;
		if (placeholders.containsKey("fillcolour"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rFC=getQuestion().applyPlaceholders(placeholders.get("fillcolour"));
				if (!rFC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						c=getWorld().convertColour(rFC);
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
				error.append("<polygon>: Placeholder ");
				error.append(placeholders.get("fillcolour"));
				error.append(" for fillColour can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return c;
	}
	
	/**
	 * Get outline color
	 * @return Outline color
	 */
	protected Color getLineColour()
	{
		Color c=cLine;
		if (placeholders.containsKey("linecolour"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rLC=getQuestion().applyPlaceholders(placeholders.get("linecolour"));
				if (!rLC.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						c=getWorld().convertColour(rLC);
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
				error.append("<polygon>: Placeholder ");
				error.append(placeholders.get("linecolour"));
				error.append(" for lineColour can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return c;
	}
	
	/**
	 * Get line width in pixels
	 * @return Line width in pixels
	 */
	protected int getLineWidth()
	{
		int width=iLineWidth;
		if (placeholders.containsKey("lineWidth"))
		{
			if (getQuestion().isPlaceholdersInitialized())
			{
				String rLW=getQuestion().applyPlaceholders(placeholders.get("linewidth"));
				if (!rLW.equals(CanvasComponent.PLACEHOLDER_NULL_VALUE))
				{
					try
					{
						Integer iWidth=new Integer(rLW);
						width=iWidth.intValue();
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<polygon>: Unexpected number value for lineWidth: ");
						error.append(rLW);
						throw new GraphFormatRuntimeException(error.toString());
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<polygon>: Placeholder ");
				error.append(placeholders.get("linewidth"));
				error.append(" for lineWidth can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return width;
	}
	
	/**
	 * Get state flag that indicates if this polygon is displayed or not
	 * @return true if this polygon is displayed, false otherwise
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
						error.append("<polygon>: Unexpected boolean value for display: ");
						error.append(sDisplay);
						throw new GraphFormatRuntimeException(error.toString());							
					}
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<polygon>: Placeholder ");
				error.append(placeholders.get("display"));
				error.append(" for display can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		return bDisplay;
	}
}
