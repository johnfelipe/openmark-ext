package om.stdcomponent.uned;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.uned.AnswerChecking;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;
import util.xml.XML;

// UNED: 18-07-2011 - dballestin
/**
 * This is a component to declare replacements for placeholders so they can be used by generic questions
 * without need of coding.<br/>
 * <h2>XML usage</h2>
 * &lt;replaceholder placeholder="..." set="@<b>value</b>"/&gt;<br/><br/>
 * or<br/><br/>
 * &lt;replaceholder placeholder="..." set="#1"&gt;<br/>
 * &nbsp;&nbsp;&lt;option value="value1"&gt;<br/>
 * &nbsp;&nbsp;&lt;option id="id2" value="<b>value2</b>"&gt;<br/>
 * &nbsp;&nbsp;...<br/>
 * &nbsp;&nbsp;&lt;option value="valuen"&gt;<br/>
 * &lt;/replaceholder&gt;<br/><br/>
 * or<br/><br/>
 * &lt;replaceholder placeholder="..." set="id1"&gt;<br/>
 * &nbsp;&nbsp;&lt;option id="id1" value="<b>value1</b>"&gt;<br/>
 * &nbsp;&nbsp;&lt;option id="id2" value="value2"&gt;<br/>
 * &nbsp;&nbsp;...<br/>
 * &nbsp;&nbsp;&lt;option id="idn" value="valuen"&gt;<br/>
 * &lt;/replaceholder&gt;<br/>
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, 
 * like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>placeholder</td><td>(string)</td><td>Specifies the placeholder's name used for replacing.
 * </td></tr>
 * <tr><td>set</td><td>(string)</td><td>Specifies the setting value to replace placeholder.</td></tr>
 * </table>
 */
public class ReplaceholderComponent extends QComponent
{
	/** Property name for placeholder's name used for replacing (string) */
	public static final String PROPERTY_PLACEHOLDER="placeholder";
	
	/** Property name for setting value to replace placeholder */
	public static final String PROPERTY_SET="set";
	
	/** 
	 * Default value of "value" property if this &lt;replaceholder&gt; has at least one &lt;option&gt; tag
	 * declared inside
	 */
	private static final String DEFAULT_VALUE_WITH_OPTION="#0";
	
	/** 
	 * Default value of "value" property if this &lt;replaceholder&gt; has no &lt;option&gt; tags
	 * declared inside
	 */
	private static final String DEFAULT_VALUE_WITHOUT_OPTION="@";
	
	/** Symbol used at start of "value" property to indicate a literal value for replacement */
	private static final String SYMBOL_LITERAL="@";
	
	/** Symbol used at start of "value" property to indicate a numeric index to select the replacement **/
	private static final String SYMBOL_INDEX="#";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG
	};
	
	/** Map with restrictions of properties that need to initialize placeholders */
	private static final Map<String,String> PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS;
	static
	{
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS=new HashMap<String,String>();
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_LANG,PROPERTYRESTRICTION_LANG);
	}
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** Specific properties checks */
	private Map<String,PropertyCheck> checks=new HashMap<String,PropertyCheck>();
	
	/** @return Tag name (introspected; this may be replaced by a 1.5 annotation) */
	public static String getTagName()
	{
		return "replaceholder";
	}
	
	/**
	 * Utility class to define a possible replacement value 
	 */
	protected static class Option
	{
		public String id;
		public String value;
		
		@Override
		public boolean equals(Object obj)
		{
			boolean isEquals=false;
			if (obj!=null && obj instanceof Option)
			{
				if (id==null)
				{
					isEquals=((Option)obj).id==null;
				}
				else
				{
					isEquals=id.equals(((Option)obj).id);
				}
			}
			return isEquals;
		}
	}
	
	/** List of Option */
	private List<Option> lOptions=new LinkedList<Option>();
	
	/**
	 * Get list of Option
	 * @return List of Option
	 */
	protected List<Option> getOptions()
	{
		return lOptions;
	}
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineString(PROPERTY_PLACEHOLDER);
		
		defineString(PROPERTY_SET);
		setString(PROPERTY_SET,DEFAULT_VALUE_WITHOUT_OPTION);
	}
	
	/**
	 * @return Placeholder's name used for replacing
	 */
	public String getPlaceholder()
	{
		try
		{
			return getString(PROPERTY_PLACEHOLDER);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the placeholder's name used for replacing
	 * @param placeholder Placeholder's name used for replacing
	 */
	public void setPlaceholder(String placeholder)
	{
		try
		{
			setString(PROPERTY_PLACEHOLDER,placeholder);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Value to replace placeholder
	 */
	public String getSet()
	{
		try
		{
			return getString(PROPERTY_SET);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the value to replace placeholder
	 * @param set Value to replace placeholder
	 */
	public void setSet(String set)
	{
		try
		{
			setString(PROPERTY_SET,set);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public String getString(String sName) throws OmDeveloperException
	{
		String sValue=null;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			sValue=super.getString(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				sValue=sq.applyPlaceholders(placeholder);
			}
			else
			{
				sValue=placeholder;
			}
			
			// Check properties with restrictions
			if (PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.containsKey(sName))
			{
				String restriction=PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.get(sName);
				if (!sValue.matches(restriction))
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' has an invalid value ");
					error.append(sValue);
					throw new OmFormatException(error.toString());
				}
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(sValue);
			}
		}
		return sValue;
	}
	
	@Override
	public String setString(String sName,String sValue) throws OmDeveloperException
	{
		boolean isOldValueNull=false;
		String sOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			sOldValue=getString(sName);
			isOldValueNull=sOldValue==null;
		}
		String sOldAux=super.setString(sName,sValue);
		placeholders.remove(sName);
		return isOldValueNull?null:sOldValue==null?sOldAux:sOldValue;
	}
	
	@Override
	public int getInteger(String sName) throws OmDeveloperException
	{
		int iValue=-1;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			iValue=super.getInteger(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				try
				{
					iValue=Integer.parseInt(placeholderReplaced);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' is not a valid integer");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Integer(iValue));
			}
		}
		return iValue;
	}
	
	@Override
	public int setInteger(String sName,int iValue) throws OmDeveloperException
	{
		Integer iOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			iOldValue=new Integer(getInteger(sName));
		}
		int iOldAux=super.setInteger(sName,iValue);
		placeholders.remove(sName);
		return iOldValue==null?iOldAux:iOldValue.intValue();
	}
	
	@Override
	public boolean getBoolean(String sName) throws OmDeveloperException
	{
		boolean bValue=false;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			bValue=super.getBoolean(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				if (placeholderReplaced.equals("yes"))
				{
					bValue=true;
				}
				else if (placeholderReplaced.equals("no"))
				{
					bValue=false;
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' must be either 'yes' or 'no'");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Boolean(bValue));
			}
		}
		return bValue;
	}
	
	@Override
	public boolean setBoolean(String sName,boolean bValue) throws OmDeveloperException
	{
		Boolean bOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			bOldValue=new Boolean(getBoolean(sName));
		}
		boolean bOldAux=super.setBoolean(sName,bValue);
		placeholders.remove(sName);
		return bOldValue==null?bOldAux:bOldValue.booleanValue();
	}
	
	@Override
	public double getDouble(String sName) throws OmDeveloperException
	{
		double dValue=0.0;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			dValue=super.getDouble(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				try
				{
					dValue=Double.parseDouble(placeholderReplaced);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' is not a valid double");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Double(dValue));
			}
		}
		return dValue;
	}
	
	@Override
	public double setDouble(String sName,double dValue) throws OmDeveloperException
	{
		Double dOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			dOldValue=new Double(getDouble(sName));
		}
		double dOldAux=super.setDouble(sName,dValue);
		placeholders.remove(sName);
		return dOldValue==null?dOldAux:dOldValue.doubleValue();
	}
	
	/**
	 * Get list of required attribute names; applied by setPropertiesFrom() that have not been set with
	 * a placeholder<br/><br/>
	 * <b>IMPORTANT</b>: Note that only required properties that have not been set with a placeholder are
	 * included in the list.<br/><br/>
	 * The reason for doing it that way is because properties set with placeholders has been removed from XML 
	 * before calling setPropertiesFrom() to avoid a format error, but still setPropertiesFrom() will throw 
	 * an error if any of the required properties from this list are not present in XML, so in order 
	 * to avoid that error we need to return only required attributes that have not been set with a 
	 * placeholder.<br/><br/>
	 * If you want to override this method you must be careful when including properties that allow 
	 * placeholders.
	 * @return List of required attribute names; applied by setPropertiesFrom() that have not been set with
	 * a placeholder
	 */
	@Override
	protected String[] getRequiredAttributes()
	{
		return new String[]
		{
			PROPERTY_PLACEHOLDER
		};
	}
	
	@Override
	public void init(QComponent parent,QDocument qd,Element eThis,boolean bImplicit) throws OmException
	{
		Map<String,String> removedAttributes=new HashMap<String,String>();
		
		// First we need to define and set 'id' before calling super.init
		if (!bImplicit && eThis.hasAttribute(PROPERTY_ID))
		{
			String id=eThis.getAttribute(PROPERTY_ID);
			
			// First we need to define 'id' property
			defineString(PROPERTY_ID,PROPERTYRESTRICTION_ID);
			
			// As 'id' property doesn't allow placeholders we can set it using superclass method
			super.setString(PROPERTY_ID,id);
			
			// We remove attribute 'id' before calling setPropertiesFrom method to avoid setting it again
			eThis.removeAttribute(PROPERTY_ID);
			removedAttributes.put(PROPERTY_ID,id);
		}
		
		// We do this trick to call initChildren method and initialize placeholders 
		// before calling setPropertiesFrom method
		super.init(parent,qd,eThis,true);
		
		// Initialize placeholders needed
		if (!bImplicit)
		{
			for (String property:PROPERTIES_TO_INITIALIZE_PLACEHOLDERS)
			{
				if (eThis.hasAttribute(property))
				{
					String propertyValue=eThis.getAttribute(property);
					if (StandardQuestion.containsPlaceholder(propertyValue))
					{
						// We add a placeholder for this property
						placeholders.put(property,propertyValue);
						
						// We set this property with some value (null for example) to achieve that OM considers 
						// that it is set.
						// We need to do this because overriding isPropertySet method from 
						// om.stdquestion.QComponent it is not enough to achieve it because checkSetProperty 
						// private method from same class does the same check without calling it
						Class<?> type=getPropertyType(property);
						if (type.equals(String.class))
						{
							super.setString(property,null);
						}
						else if (type.equals(Integer.class))
						{
							super.setInteger(property,-1);
						}
						else if (type.equals(Double.class))
						{
							super.setDouble(property,0.0);
						}
						else if (type.equals(Boolean.class))
						{
							super.setBoolean(property,false);
						}
						
						// We remove attribute before calling setPropertiesFrom method to avoid a format error
						eThis.removeAttribute(property);
						removedAttributes.put(property,propertyValue);
					}
				}
			}
			setPropertiesFrom(eThis);
			
			// After calling setPropertiesFrom method we need to set again removed attributes
			for (Map.Entry<String,String> removedAttribute:removedAttributes.entrySet())
			{
				eThis.setAttribute(removedAttribute.getKey(),removedAttribute.getValue());
			}
		}
		
		// Specific initializations
		initializeSpecific(eThis);
		
		// Do now specific checks on properties set without using placeholders
		for (Map.Entry<String,PropertyCheck> check:checks.entrySet())
		{
			if (!placeholders.containsKey(check.getKey()))
			{
				Object value=null;
				Class<?> type=getPropertyType(check.getKey());
				if (type.equals(String.class))
				{
					value=super.getString(check.getKey());
				}
				else if (type.equals(Integer.class))
				{
					value=new Integer(super.getInteger(check.getKey()));
				}
				else if (type.equals(Double.class))
				{
					value=new Double(super.getDouble(check.getKey()));
				}
				else if (type.equals(Boolean.class))
				{
					value=new Boolean(super.getBoolean(check.getKey()));
				}
				check.getValue().check(value);
			}
		}
	}
	
	/**
	 * Does nothing.<br/><br/>
	 * Note that this method has been declared 'final' because we don't want it to be overriden.<br/><br/>
	 * The reason for doing that is that om.stdcomponent.uned components don't allow to check properties
	 * from this method, because properties aren't still defined due to initializations changes to support
	 * placeholders.<br/><br/>
	 * If you need specific initializations you can override initializeSpecific method instead.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	@Override
	protected final void initSpecific(Element eThis) throws OmException
	{
	}
	
	/**
	 * Carries out initialisation specific to component.<br/><br/>
	 * Override this method if you need any.<br/><br/>
	 * However if you need to check some properties instead of checking them inside directly it is recommended 
	 * that you call "addSpecificCheck" method once for every property to be checked with an implementation of the
	 * PropertyCheck interface providing desired check.<br/><br/>
	 * The reason to doing it that way is to be sure that checks will work well even if you set a property using
	 * placeholders.<br/><br/>
	 * Default implementation of this method does nothing.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	protected void initializeSpecific(Element eThis) throws OmException
	{
	}
	
	/**
	 * Add a specific check for some property.<br/><br/>
	 * Obviously property must be defined before calling this method.<br/><br/>
	 * It is recommended that you invoke this method from initializeSpecific method onece for every property with
	 * specific checks.
	 * @param propertyName Property's name
	 * @param propertyCheck Property's check
	 * @throws OmException
	 */
	protected void addSpecificCheck(String propertyName,PropertyCheck propertyCheck) throws OmException
	{
		if (!isPropertyDefined(propertyName))
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: property '");
			error.append(propertyName);
			error.append("' has not been defined");
			throw new OmDeveloperException(error.toString());
		}
		if (propertyCheck==null)
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: property check implementation for '");
			error.append(propertyName);
			error.append("' has not been provided");
			throw new OmDeveloperException(error.toString());
		}
		checks.put(propertyName,propertyCheck);
	}
	
	/**
	 * Initialises children based on the given XML element.
	 * <br/><br/>
	 * Note that om.stdcomponent.uned components don't allow to check properties from this method, because properties 
	 * aren't still defined due to initializations changes to support placeholders.<br/><br/>
	 * If you override this method and you really need to check properties you need to move that checkings to 
	 * other method (some good candidates depending on your needs are: initializeSpecific, produceVisibleOutput or 
	 * init method from question class if you are overriding generic question class).
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error initialising the children
	 */
	@Override
	protected void initChildren(Element eThis) throws OmException
	{
		Element[] aeOptions=XML.getChildren(eThis);
		for(int i=0;i<aeOptions.length;i++)
		{
			if (!aeOptions[i].getTagName().equals("option"))
			{
				throw new OmFormatException("<replaceholder>: May only include <option>s");
			}
			if (!aeOptions[i].hasAttribute("value"))
			{
				throw new OmFormatException("<option>: Must include value=");
			}
			String id=aeOptions[i].hasAttribute("id")?aeOptions[i].getAttribute("id"):null;
			addOption(id,aeOptions[i].getAttribute("value"));
		}
		if (aeOptions.length>0)
		{
			setString(PROPERTY_SET,DEFAULT_VALUE_WITH_OPTION);
		}
	}
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
	}
	
	/**
	 * Adds an option to the replaceholder.
	 * @param id Identifier for option
	 * @param value Value for option
	 * @throws OmException
	 */
	public void addOption(String id,String value) throws OmException
	{
		Option o=new Option();
		o.id=id;
		o.value=value;
		lOptions.add(o);
	}
	
	/**
	 * @param i Index
	 * @return Replacement value for option at specified index
	 * @throws OmException
	 */
	private String getIndexValue(int i) throws OmException
	{
		String indexValue=null;
		if (i>=0)
		{
			if (getOptions().size()>i)
			{
				indexValue=getOptions().get(i).value;
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<replaceholder>: Index at value property exceeds available options: ");
				error.append(SYMBOL_INDEX);
				error.append(i);
				if (getOptions().size()>0)
				{
					error.append(" , maximum available index: ");
					error.append(SYMBOL_INDEX);
					error.append(getOptions().size()-1);
				}
				throw new OmDeveloperException(error.toString());					
			}
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append("<replaceholder>: Missing or not numeric index at value property: ");
			error.append(SYMBOL_INDEX);
			error.append(i);
			throw new OmDeveloperException(error.toString());					
		}
		return indexValue;
	}
	
	/**
	 * @return Replacement value for placeholder
	 * @throws OmException
	 */
	public String getReplacementValue() throws OmException
	{
		String replacementValue=null;
		String set=getSet();
		Option opt=new Option();
		opt.id=AnswerChecking.replaceEscapeChars(set);
		if (set.equals(SYMBOL_LITERAL))
		{
			replacementValue="";
		}
		else if (set.startsWith(SYMBOL_LITERAL))
		{
			replacementValue=AnswerChecking.replaceEscapeChars(set.substring(SYMBOL_LITERAL.length()));
		}
		else if (set.startsWith(SYMBOL_INDEX))
		{
			int index=-1;
			try
			{
				index=Integer.parseInt(set.substring(SYMBOL_INDEX.length()));
			}
			catch (Exception e)
			{
				index=-1;
			}
			replacementValue=getIndexValue(index);
		}
		else if (getOptions().contains(opt))
		{
			replacementValue=getOptions().get(getOptions().indexOf(opt)).value;
		}
		else
		{
			boolean valueLiteral=false;
			if (set.endsWith(SYMBOL_LITERAL) && 
					!AnswerChecking.endWithSequenceEscapeCharacter(set,AnswerChecking.getEscapeSequences()))
			{
				valueLiteral=true;
				set=set.substring(0,set.length()-SYMBOL_LITERAL.length());
			}
			set=AnswerChecking.replaceEscapeChars(set);
			QComponent qc=null;
			try
			{
				qc=getQDocument().find(set);
			}
			catch (OmDeveloperException e)
			{
				qc=null;
			}
			String value=null;
			if (qc!=null)
			{
				if (qc instanceof RandomComponent)
				{
					value=((RandomComponent)qc).getValue();
				}
				else if (qc instanceof VariableComponent)
				{
					value=((VariableComponent)qc).getValue();
				}
			}
			if (value==null)
			{
				StringBuffer error=new StringBuffer();
				error.append("<replaceholder>: 'set' value invalid: ");
				error.append(getSet());
				throw new OmDeveloperException(error.toString());
			}
			else
			{
				if (valueLiteral)
				{
					replacementValue=value;
				}
				else
				{
					opt.id=value;
					if (getOptions().contains(opt))
					{
						replacementValue=getOptions().get(getOptions().indexOf(opt)).value;
					}
					else
					{
						int index=-1;
						try
						{
							index=Integer.parseInt(value);
						}
						catch (Exception e)
						{
							index=-1;
						}
						replacementValue=getIndexValue(index);
					}
				}
			}
		}
		return replacementValue;
	}
}
