package om.graph.uned;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import om.graph.GraphFormatException;
import om.graph.GraphItem;
import om.graph.GraphRange;
import om.graph.GraphScalar;
import om.stdquestion.uned.StandardQuestion;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import util.xml.XML;
import util.xml.XMLException;

// UNED: 24-08-2011 - dballestin
/**
 * Represents a co-ordinate system for use in a graph.<br/><br/>
 * This version of &lt;world&gt; allows to use placeholders with its attributes
 */
public class World extends om.graph.World
{
	/** Attribute name for world identifier */
	public final static String ATTRIBUTE_ID="id";
	
	/** Attribute name for pixel X co-ordinate in target context */
	public final static String ATTRIBUTE_PX="px";
	
	/** Attribute name for pixel Y co-ordinate in target context */
	public final static String ATTRIBUTE_PY="py";
	
	/** Attribute name for width in pixels in target context */
	public final static String ATTRIBUTE_PW="pw";
	
	/** Attribute name for height in pixels in target context */
	public final static String ATTRIBUTE_PH="ph";
	
	/** Attribute name for left extent of world co-ordinates */
	public final static String ATTRIBUTE_XLEFT="xleft";
	
	/** Attribute name for right extent of world co-ordinates */
	public final static String ATTRIBUTE_XRIGHT="xright";
	
	/** Attribute name for top extent of world coordinate */
	public final static String ATTRIBUTE_YTOP="ytop";
	
	/** Attribute name for bottom extent of world coordinate */
	public final static String ATTRIBUTE_YBOTTOM="ybottom";
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** Standard question */
	private StandardQuestion sq;
	
	/**
	 * Constructs from XML.
	 * <p>
	 * XML syntax for worlds is:<br/>
	 * &lt;world id="w1" px="20" py="10" pw="260" ph="280"
	 * xleft="0.0" ybottom="-1.0" xright="10.0" ytop="1.0"/&gt;
	 * <p>
	 * May contain all other graph items, which will be constructed and added.
	 * @param cs Source for colour constants
	 * @param e &lt;world&gt; element
	 * @param sq Standard question
	 * @throws GraphFormatException If anything's wrong with the XML
	 */
	public World(Context cs,Element e,StandardQuestion sq) throws GraphFormatException
	{
		setQuestion(sq);
		initWorld(cs,e);
	}
	
	@Override
	protected void setupAttributes(Element e) throws GraphFormatException
	{
		try
		{
			setID(XML.getRequiredAttribute(e,ATTRIBUTE_ID));
			
			String sPX=XML.getRequiredAttribute(e,ATTRIBUTE_PX);
			if (StandardQuestion.containsPlaceholder(sPX))
			{
				placeholders.put(ATTRIBUTE_PX,sPX);
			}
			else
			{
				try
				{
					setPX(Integer.parseInt(sPX));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute px: ");
					error.append(sPX);
					throw new GraphFormatException(error.toString());
				}
			}
			String sPY=XML.getRequiredAttribute(e,ATTRIBUTE_PY);
			if (StandardQuestion.containsPlaceholder(sPY))
			{
				placeholders.put(ATTRIBUTE_PY,sPY);
			}
			else
			{
				try
				{
					setPY(Integer.parseInt(sPY));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute py: ");
					error.append(sPY);
					throw new GraphFormatException(error.toString());
				}
			}
			String sPW=XML.getRequiredAttribute(e,ATTRIBUTE_PW);
			if (StandardQuestion.containsPlaceholder(sPW))
			{
				placeholders.put(ATTRIBUTE_PW,sPW);
			}
			else
			{
				try
				{
					setPW(Integer.parseInt(sPW));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute pw: ");
					error.append(sPW);
					throw new GraphFormatException(error.toString());
				}
			}
			String sPH=XML.getRequiredAttribute(e,ATTRIBUTE_PH);
			if (StandardQuestion.containsPlaceholder(sPH))
			{
				placeholders.put(ATTRIBUTE_PH,sPH);
			}
			else
			{
				try
				{
					setPH(Integer.parseInt(sPH));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute ph: ");
					error.append(sPH);
					throw new GraphFormatException(error.toString());
				}
			}
			
			String sXLeft=XML.getRequiredAttribute(e,ATTRIBUTE_XLEFT);
			if (StandardQuestion.containsPlaceholder(sXLeft))
			{
				placeholders.put(ATTRIBUTE_XLEFT,sXLeft);
			}
			else
			{
				try
				{
					setLeftX(Double.parseDouble(sXLeft));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute xleft: ");
					error.append(sXLeft);
					throw new GraphFormatException(error.toString());
				}
			}
			String sXRight=XML.getRequiredAttribute(e,ATTRIBUTE_XRIGHT);
			if (StandardQuestion.containsPlaceholder(sXRight))
			{
				placeholders.put(ATTRIBUTE_XRIGHT,sXRight);
			}
			else
			{
				try
				{
					setRightX(Double.parseDouble(sXRight));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute xright: ");
					error.append(sXRight);
					throw new GraphFormatException(error.toString());
				}
			}
			String sYTop=XML.getRequiredAttribute(e,ATTRIBUTE_YTOP);
			if (StandardQuestion.containsPlaceholder(sYTop))
			{
				placeholders.put(ATTRIBUTE_YTOP,sYTop);
			}
			else
			{
				try
				{
					setTopY(Double.parseDouble(sYTop));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute ytop: ");
					error.append(sYTop);
					throw new GraphFormatException(error.toString());
				}
			}
			String sYBottom=XML.getRequiredAttribute(e,ATTRIBUTE_YBOTTOM);
			if (StandardQuestion.containsPlaceholder(sYBottom))
			{
				placeholders.put(ATTRIBUTE_YBOTTOM,sYBottom);
			}
			else
			{
				try
				{
					setBottomY(Double.parseDouble(sYBottom));
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute ybottom: ");
					error.append(sYBottom);
					throw new GraphFormatException(error.toString());
				}
			}
		}
		catch (XMLException xe)
		{
			StringBuffer error=new StringBuffer();
			error.append("<world>: Format error - ");
			error.append(xe.getMessage());
			throw new GraphFormatException(error.toString());
		}
	}
	
	@Override
	public void add(GraphItem gi) throws GraphFormatException
	{
		if (gi.getWorld()!=this)
		{	
			throw new GraphFormatException("Graph item does not belong to this <world>");
		}
		// Initialize item only if placeholders have been initialized
		if (getQuestion().isPlaceholdersInitialized())
		{
			gi.checkInit();
		}
		getItems().add(gi);
		if (gi instanceof Replaceable)
		{
			((Replaceable)gi).setQuestion(getQuestion());
		}
	}
	
	@Override
	public void add(GraphItem gi,int iPos) throws GraphFormatException
	{
		if (gi.getWorld()!=this)
		{
			throw new GraphFormatException("Graph item does not belong to this <world>");
		}
		// Initialize item only if placeholders have been initialized
		if (getQuestion().isPlaceholdersInitialized())
		{
			gi.checkInit();
		}
		getItems().add(iPos,gi);
		if (gi instanceof Replaceable)
		{
			((Replaceable)gi).setQuestion(getQuestion());
		}
	}
	
	/**
	 * Converts a width expressed in graph co-ordinates to pixel co-ordinates.
	 * @param dW Width expressed in graph co-ordinates
	 * @return Width expressed in pixel co-ordinates
	 */
	public int convertWidth(double dW)
	{
		double dProportion=dW/Math.abs(getRightX()-getLeftX());
		return (int)(dProportion*getPW()+0.5);
	}
	
	/**
	 * Converts a width expressed in pixel co-ordinates to graph co-ordinates.
	 * @param iW Width expressed in pixel co-ordinates
	 * @return Width expressed in graph co-ordinates
	 */
	public double convertWidthBack(int iW)
	{
		return (double)(iW)/(double)getPW()*Math.abs(getRightX()-getLeftX());
	}
	
	/**
	 * Converts an height expressed in graph co-ordinates to pixel co-ordinates.
	 * @param dH Height expressed in graph co-ordinates
	 * @return Height expressed in pixel co-ordinates
	 */
	public int convertHeight(double dH)
	{
		double dProportion=dH/Math.abs(getBottomY()-getTopY());
		return (int)(dProportion*getPH()+0.5);
	}
	
	/**
	 * Converts an height expressed in pixel co-ordinates to graph co-ordinates.
	 * @param iH Height expressed in pixel co-ordinates
	 * @return Height expressed in graph co-ordinates
	 */
	public double convertHeightBack(int iH)
	{
		return (double)(iH)/(double)getPH()*Math.abs(getBottomY()-getTopY());
	}
	
	@Override
	protected GraphItem construct(Element e) throws GraphFormatException
	{
		String sTag=e.getTagName();
		
		// Find class in om.graph.uned package
		Class<? extends GraphItem> c;
		try
		{
			StringBuffer className=new StringBuffer();
			className.append("om.graph.uned.");
			className.append(sTag.substring(0,1).toUpperCase());
			className.append(sTag.substring(1));
			className.append("Item");
			c=Class.forName(className.toString()).asSubclass(GraphItem.class);
		}
		catch(ClassNotFoundException e1)
		{
			// If class does not exists in om.graph.uned package we construct the graph item 
			// with the superclass implementation (searching class in om.graph package)
			return super.construct(e);
		}
		
		// Get constructor
		Constructor<? extends GraphItem> cConstructor;
		try
		{
			cConstructor=c.getConstructor(new Class[]{om.graph.World.class});
		}
		catch(NoSuchMethodException e2)
		{
			StringBuffer error=new StringBuffer();
			error.append("Couldn't find constructor taking World parameter for: ");
			error.append(sTag);
			throw new GraphFormatException(error.toString());
		}
		
		// Construct item
		GraphItem giNew;
		try
		{
			giNew=cConstructor.newInstance(new Object[]{this});
		}
		catch(Throwable t)
		{
			StringBuffer error=new StringBuffer();
			error.append("Failed to construct graph item <");
			error.append(sTag);
			error.append('>');
			throw new GraphFormatException(error.toString(),t);
		}
		
		// Set each attribute
		NamedNodeMap nnm=e.getAttributes();
		Method[] am=c.getMethods();
		attrloop: for(int iAttribute=0;iAttribute<nnm.getLength();iAttribute++)
		{
			Attr a=(Attr)nnm.item(iAttribute);
			String sName=a.getName();
			String sValue=a.getValue();
			StringBuffer sMethod=new StringBuffer();
			sMethod.append("set");
			sMethod.append(sName);
			StringBuffer sMethodWithIndex=null;
			int numberFromName=-1;
			String onlyName=getOnlyName(sName);
			if (onlyName!=null)
			{
				sMethodWithIndex=new StringBuffer();
				sMethodWithIndex.append("set");
				sMethodWithIndex.append(onlyName);
				numberFromName=getNumberFromName(sName);
			}
			
			// Find method
			Method method=null;
			Method methodWithIndex=null;
			Class<?> cParam=null;
			for (int iMethod=0;iMethod<am.length;iMethod++)
			{
				if (am[iMethod].getName().equalsIgnoreCase(sMethod.toString()))
				{
					// Check modifiers; ignore non-public, or static, methods
					int iMod=am[iMethod].getModifiers();
					if (!Modifier.isPublic(iMod) || Modifier.isStatic(iMod))
					{
						continue;
					}
					
					// Ignore methods that don't take a single parameter
					Class<?>[] ac=am[iMethod].getParameterTypes();
					if (ac.length!=1)
					{
						continue;
					}
					cParam=ac[0];
					method=am[iMethod];
					break;
				}
			}
			if (method==null)
			{
				for (int iMethod=0;iMethod<am.length;iMethod++)
				{
					if (am[iMethod].getName().equalsIgnoreCase(sMethodWithIndex.toString()))
					{
						// Check modifiers; ignore non-public, or static, methods
						int iMod=am[iMethod].getModifiers();
						if (!Modifier.isPublic(iMod) || Modifier.isStatic(iMod))
						{
							continue;
						}
						
						// Ignore methods that don't take two parameters
						Class<?>[] ac=am[iMethod].getParameterTypes();
						if (ac.length!=2)
						{
							continue;
						}
						
						// Ignore methods that its first argument is not an int (primitive type)
						if (!ac[0].getName().equals("int"))
						{
							continue;
						}
						cParam=ac[1];
						methodWithIndex=am[iMethod];
						break;
					}
				}
			}
			if (cParam!=null)
			{
				// OK this is the right method
				
				// Take care of placeholders but only if class implements Replaceable interface
				boolean needToSetAttribute=true;
				if (giNew instanceof Replaceable)
				{
					((Replaceable)giNew).setQuestion(getQuestion());
					if (StandardQuestion.containsPlaceholder(sValue))
					{
						try
						{
							((Replaceable)giNew).setAttributePlaceholder(sName,sValue);
							needToSetAttribute=false;
						}
						catch (AttributePlaceholderNotImplementedException apnie)
						{
							needToSetAttribute=true;
						}
					}
				}
				
				// Now convert/validate the parameter if needed
				if (needToSetAttribute)
				{
					Object oParam;
					try
					{
						if (cParam == int.class)
						{
							oParam=new Integer(sValue);
						}
						else if(cParam == double.class)
						{
							oParam=new Double(sValue);
						}
						else if (cParam == String.class)
						{
							oParam=sValue;
						}
						else if (cParam == boolean.class)
						{
							if (sValue.equals("yes"))
							{
								oParam=Boolean.TRUE;
							}
							else if(sValue.equals("no"))
							{
								oParam=Boolean.FALSE;
							}
							else
							{
								StringBuffer error=new StringBuffer();
								error.append('<');
								error.append(sTag);
								error.append(">: Unexpected boolean value for ");
								error.append(sName);
								error.append(": ");
								error.append(sValue);
								throw new GraphFormatException(error.toString());
							}
						}
						else if(cParam == GraphScalar.class)
						{
							oParam=new GraphScalar(sValue);
						}
						else if(cParam == GraphRange.class)
						{
							oParam=new GraphRange(sValue);
						}
						else if(cParam == Color.class)
						{
							oParam=convertColour(sValue);
						}
						else if(cParam==Font.class)
						{
							oParam=convertFont(sValue);
						}
						else
						{
							continue; // Ignore this method after all!
						}
					}
					catch(NumberFormatException nfe)
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(sTag);
						error.append(">: Unexpected number value for ");
						error.append(sName);
						error.append(": ");
						error.append(sValue);
						throw new GraphFormatException(error.toString());
					}
					
					try
					{
						// Woo! All done. Now call method.
						if (method!=null)
						{
							method.invoke(giNew,new Object[]{oParam});
						}
						else if (methodWithIndex!=null)
						{
							methodWithIndex.invoke(giNew,new Object[]{new Integer(numberFromName),oParam});
						}
					}
					catch (Throwable t)
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(sTag);
						error.append(">: Error setting attribute ");
						error.append(sName);
						error.append(" to: ");
						error.append(sValue);
						throw new GraphFormatException(error.toString(),t);
					}
				}
				
				// OK, onto next attribute
				continue attrloop;
			}
			
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(sTag);
			error.append(">: Attribute ");
			error.append(sName);
			error.append(" does not exist.");
			throw new GraphFormatException(error.toString());
		}
		
		// We don't initilize graph item now because placeholders must be resolved first
		//giNew.checkInit();
		return giNew;
	}
	
	/**
	 * Get the name without the number from the end part of the received name or null if there is no number
	 * @param name Name
	 * @return Name without the number from the end part of the received name or null if there is no number
	 */
	private String getOnlyName(String name)
	{
		String onlyName=null;
		if (getNumberFromName(name)!=-1)
		{
			int i=name.length()-2;
			while (i>=0 && Character.isDigit(name.charAt(i)))
			{
				i--;
			}
			if (i>=0)
			{
				onlyName=name.substring(0,i+1);
			}
		}
		return onlyName;
	}
	
	/**
	 * Get the number from the end part of the received name or -1 if there is no number
	 * @param name Name
	 * @return Number from the end part of the received name or -1 if there is no number
	 */
	private int getNumberFromName(String name)
	{
		int number=-1;
		if (name!=null && Character.isDigit(name.charAt(name.length()-1)))
		{
			int i=name.length()-2;
			while (i>=0 && Character.isDigit(name.charAt(i)))
			{
				i--;
			}
			number=Integer.parseInt(name.substring(i+1));
		}
		return number;
	}
	
	@Override
	public List<GraphItem> getItems()
	{
		return super.getItems();
	}

	/**
	 * @return Standard question
	 */
	protected StandardQuestion getQuestion()
	{
		return sq;
	}
	
	/**
	 * Set standard question
	 * @param sq Standard question
	 */
	protected void setQuestion(StandardQuestion sq)
	{
		this.sq = sq;
	}
	
	@Override
	protected int getPX()
	{
		int px=0;
		if (placeholders.containsKey(ATTRIBUTE_PX))
		{
			String placeholderPX=placeholders.get(ATTRIBUTE_PX);
			if (sq.isPlaceholdersInitialized())
			{
				String rPX=sq.applyPlaceholders(placeholderPX);
				try
				{
					px=Integer.parseInt(rPX);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute px: ");
					error.append(rPX);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderPX);
				error.append(" for px can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			px=super.getPX();
		}
		return px;
	}
	
	@Override
	protected int getPY()
	{
		int py=0;
		if (placeholders.containsKey(ATTRIBUTE_PY))
		{
			String placeholderPY=placeholders.get(ATTRIBUTE_PY);
			if (sq.isPlaceholdersInitialized())
			{
				String rPY=sq.applyPlaceholders(placeholderPY);
				try
				{
					py=Integer.parseInt(rPY);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute py: ");
					error.append(rPY);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderPY);
				error.append(" for py can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			py=super.getPY();
		}
		return py;
	}
	
	@Override
	protected int getPW()
	{
		int pw=0;
		if (placeholders.containsKey(ATTRIBUTE_PW))
		{
			String placeholderPW=placeholders.get(ATTRIBUTE_PW);
			if (sq.isPlaceholdersInitialized())
			{
				String rPW=sq.applyPlaceholders(placeholderPW);
				try
				{
					pw=Integer.parseInt(rPW);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute pw: ");
					error.append(rPW);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderPW);
				error.append(" for pw can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			pw=super.getPW();
		}
		return pw;
	}
	
	@Override
	protected int getPH()
	{
		int ph=0;
		if (placeholders.containsKey(ATTRIBUTE_PH))
		{
			String placeholderPH=placeholders.get(ATTRIBUTE_PH);
			if (sq.isPlaceholdersInitialized())
			{
				String rPH=sq.applyPlaceholders(placeholderPH);
				try
				{
					ph=Integer.parseInt(rPH);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute ph: ");
					error.append(rPH);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderPH);
				error.append(" for ph can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			ph=super.getPH();
		}
		return ph;
	}
	
	@Override
	public double getLeftX()
	{
		double xLeft=0.0;
		if (placeholders.containsKey(ATTRIBUTE_XLEFT))
		{
			String placeholderXLeft=placeholders.get(ATTRIBUTE_XLEFT);
			if (sq.isPlaceholdersInitialized())
			{
				String rXLeft=sq.applyPlaceholders(placeholderXLeft);
				try
				{
					xLeft=Double.parseDouble(rXLeft);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute xleft: ");
					error.append(rXLeft);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderXLeft);
				error.append(" for xleft can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			xLeft=super.getLeftX();
		}
		return xLeft;
	}
	
	@Override
	public double getRightX()
	{
		double xRight=0.0;
		if (placeholders.containsKey(ATTRIBUTE_XRIGHT))
		{
			String placeholderXRight=placeholders.get(ATTRIBUTE_XRIGHT);
			if (sq.isPlaceholdersInitialized())
			{
				String rXRight=sq.applyPlaceholders(placeholderXRight);
				try
				{
					xRight=Double.parseDouble(rXRight);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute xright: ");
					error.append(rXRight);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderXRight);
				error.append(" for xright can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			xRight=super.getRightX();
		}
		return xRight;
	}
	
	@Override
	public double getTopY()
	{
		double yTop=0.0;
		if (placeholders.containsKey(ATTRIBUTE_YTOP))
		{
			String placeholderYTop=placeholders.get(ATTRIBUTE_YTOP);
			if (sq.isPlaceholdersInitialized())
			{
				String rYTop=sq.applyPlaceholders(placeholderYTop);
				try
				{
					yTop=Double.parseDouble(rYTop);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute ytop: ");
					error.append(rYTop);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderYTop);
				error.append(" for ytop can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			yTop=super.getTopY();
		}
		return yTop;
	}
	
	@Override
	public double getBottomY()
	{
		double yBottom=0.0;
		if (placeholders.containsKey(ATTRIBUTE_YBOTTOM))
		{
			String placeholderYBottom=placeholders.get(ATTRIBUTE_YBOTTOM);
			if (sq.isPlaceholdersInitialized())
			{
				String rYBottom=sq.applyPlaceholders(placeholderYBottom);
				try
				{
					yBottom=Double.parseDouble(rYBottom);
				}
				catch(NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<world>: Invalid number in attribute ybottom: ");
					error.append(rYBottom);
					throw new GraphFormatRuntimeException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<world>: Placeholder ");
				error.append(placeholderYBottom);
				error.append(" for ybottom can not be replaced");
				error.append(" because placeholders have not been initialized");
				throw new GraphFormatRuntimeException(error.toString());
			}
		}
		else
		{
			yBottom=super.getBottomY();
		}
		return yBottom;
	}
	
	/**
	 * @param attributeName Attribute name
	 * @return Placeholder for attribute requested
	 */
	protected String getPlaceholder(String attributeName)
	{
		return placeholders.get(attributeName);
	}
}
