package om.stdcomponent.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.question.ActionParams;
import om.stdquestion.QComponent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

import org.w3c.dom.Element;

import util.xml.XML;

// UNED: 06-07-2011 - dballestin
/**
 * A component that produces a dropdown box with several available options.<br/><br/>
 * <h2>XML usage</h2><br/>
 * &lt;dropdown&gt;<br/>
 * &nbsp;&nbsp;&lt;option display="A wrong choice" value="x"/&gt;<br/>
 * &nbsp;&nbsp;&lt;option display="Another wrong choice" value="y" right="no"/&gt;<br/>
 * &nbsp;&nbsp;&lt;option display="A right choice" value="z" right="yes"/&gt;<br/>
 * &lt;/dropdown&gt;
 * <p>
 * Note that the options are <em>not</em> question components; you can only use plain text,
 * except on right attribute that allow placeholders for a boolean value (yes|no).
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates this control</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>selected</td><td>(string)</td><td>Value ID of the selected entry</td></tr>
 * <tr><td>answerenabled</td><td>(boolean)</td><td>Specifies if this component is used as an answer 
 * component, by default yes</td></tr>
 * </table>
 */
public class DropdownComponent extends om.stdcomponent.DropdownComponent implements Answerable
{
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_ANSWERENABLED
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
	
	/** List of right Options */
	private List<String> rightOptions;
	
	/** Map of options with a placeholder in their "right" attribute */
	private Map<String,String> replaceholderOptions;
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineBoolean(PROPERTY_ANSWERENABLED);
		setBoolean(PROPERTY_ANSWERENABLED,true);
	}
	
	@Override
	public boolean isAnswerEnabled()
	{
		try
		{
			return getBoolean(PROPERTY_ANSWERENABLED);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public void setAnswerEnabled(boolean answerEnabled)
	{
		try
		{
			setBoolean(PROPERTY_ANSWERENABLED,answerEnabled);
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
		List<String> requiredAttributes=new ArrayList<String>();
		for (String requiredAttribute:super.getRequiredAttributes())
		{
			if (!placeholders.containsKey(requiredAttribute))
			{
				requiredAttributes.add(requiredAttribute);
			}
		}
		return requiredAttributes.toArray(new String[0]);
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
		
		// We do this trick to initialize placeholders before calling setPropertiesFrom method
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
		super.initChildren(eThis);
		rightOptions=new LinkedList<String>();
		replaceholderOptions=new LinkedHashMap<String,String>();
		Element[] options=XML.getChildren(eThis);
		for (Element option:options)
		{
			if (option.hasAttribute("right"))
			{
				String rightValue=option.getAttribute("right");
				if (rightValue.equals("yes"))
				{
					rightOptions.add(option.getAttribute("value"));
				}
				else if (StandardQuestion.containsPlaceholder(rightValue))
				{
					replaceholderOptions.put(option.getAttribute("value"),rightValue);
				}
			}
		}
	}
	
	@Override
	protected void formSetValue(String sValue,ActionParams ap) throws OmException
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		for (Option o:getOptions())
		{
			String display=o.sDisplay;
			if (sq.isPlaceholdersInitialized() && StandardQuestion.containsPlaceholder(display))
			{
				display=sq.applyPlaceholders(display);
			}
			if (display.equals(sValue))
			{
				setString(PROPERTY_SELECTED,o.sValue);
				return;
			}
		}
		StringBuffer error=new StringBuffer();
		error.append("Unexpected dropdown value: ");
		error.append(sValue);
		throw new OmException(error.toString());
	}
	
	/**
	 * @return List of all existing options
	 */
	public List<String> getAvailableOptions()
	{
		List<String> availableOptions=new ArrayList<String>(getOptions().size());
		for (Option option:getOptions())
		{
			availableOptions.add(option.sValue);
		}
		return availableOptions;
	}
	
	/**
	 * @return List of right existing options
	 */
	public List<String> getRightOptions()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		List<String> rightOptions=new ArrayList<String>(this.rightOptions);
		for (Map.Entry<String,String> replaceholderOption:replaceholderOptions.entrySet())
		{
			String rightValue=replaceholderOption.getValue();
			if (sq.isPlaceholdersInitialized())
			{
				rightValue=sq.applyPlaceholders(rightValue);
			}
			if (rightValue.equals("yes"))
			{
				rightOptions.add(replaceholderOption.getKey());
			}
		}
		return rightOptions;
	}
	
	/**
	 * @return List of wrong existing options
	 */
	public List<String> getWrongOptions() {
		StandardQuestion sq=(StandardQuestion)getQuestion();
		List<String> wrongOptions = new ArrayList<String>(getOptions().size()-rightOptions.size());
		for (Option option:getOptions())
		{
			if (!rightOptions.contains(option.sValue))
			{
				if (replaceholderOptions.containsKey(option.sValue))
				{
					String rightValue=replaceholderOptions.get(option.sValue);
					if (sq.isPlaceholdersInitialized())
					{
						rightValue=sq.applyPlaceholders(rightValue);
					}
					if (!rightValue.equals("yes"))
					{
						wrongOptions.add(option.sValue);
					}
				}
				else
				{
					wrongOptions.add(option.sValue);
				}
			}
		}
		return wrongOptions;
	}
	
	/**
	 * Adds an option to the dropdown.
	 * @param sValue Value for option
	 * @param sDisplay Display text of option
	 * @param right true if this option is right, false if it is wrong
	 * @throws OmException
	 */
	public void addOption(String sValue,String sDisplay,boolean right) throws OmException
	{
		addOption(sValue,sDisplay);
		if (right && !rightOptions.contains(sValue))
		{
			rightOptions.add(sValue);
		}
		if (replaceholderOptions.containsKey(sValue))
		{
			replaceholderOptions.remove(sValue);
		}
	}
	
	/**
	 * @param sValue Value for option
	 * @return true if this option is right, false if it is wrong
	 * @throws OmException
	 */
	public boolean isRigthOption(String sValue) throws OmException
	{
		boolean ok=false;
		for (Option option:getOptions())
		{
			if (option.sValue==sValue)
			{
				ok=true;
				break;
			}
		}
		if (!ok)
		{
			StringBuffer error=new StringBuffer();
			error.append("Not a valid dropdown option: ");
			error.append(sValue);
			throw new OmDeveloperException(error.toString());
		}
		ok=rightOptions.contains(sValue);
		if (!ok && replaceholderOptions.containsKey(sValue))
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			String rightValue=replaceholderOptions.get(sValue);
			if (sq.isPlaceholdersInitialized())
			{
				rightValue=sq.applyPlaceholders(rightValue);
			}
			ok=rightValue.equals("yes");
		}
		return ok;
	}
	
	/**
	 * Sets rightness for an existing option
	 * @param sValue Value for option
	 * @param right true if this option is right, false if it is wrong
	 * @throws OmException
	 */
	public void setRightOption(String sValue,boolean right) throws OmException
	{
		boolean ok=false;
		for (Option option:getOptions())
		{
			if (option.sValue==sValue)
			{
				ok=true;
				break;
			}
		}
		if (!ok)
		{
			StringBuffer error=new StringBuffer();
			error.append("Not a valid dropdown option: ");
			error.append(sValue);
			throw new OmDeveloperException(error.toString());
		}
		if (right && !rightOptions.contains(sValue))
		{
			rightOptions.add(sValue);
		}
		else if (!right && rightOptions.contains(sValue))
		{
			rightOptions.remove(sValue);
		}
		if (replaceholderOptions.containsKey(sValue))
		{
			replaceholderOptions.remove(sValue);
		}
	}
	
	/**
	 * Get display text of option
	 * @param sValue Value for option
	 * @return Display text of option
	 * @throws OmException
	 */
	public String getDisplayOption(String sValue) throws OmException
	{
		boolean ok=false;
		Option option=null;
		for (Iterator<Option> it=getOptions().iterator();it.hasNext();)
		{
			option=it.next();
			if (option.sValue==sValue)
			{
				ok=true;
				break;
			}
		}
		if (!ok)
		{
			StringBuffer error=new StringBuffer();
			error.append("Not a valid dropdown option: ");
			error.append(sValue);
			throw new OmDeveloperException(error.toString());
		}
		return option.sDisplay;
	}
	
	/**
	 * Sets rightness for all existing options to wrong
	 */
	public void clearRightOptions()
	{
		rightOptions.clear();
	}
	
	/**
	 * @param sValue Value for option
	 * @return true if this option is selected, false if it is not selected or if it doesn't exists
	 */
	public boolean isSelected(String sValue)
	{
		return sValue==null?getSelected()==null:sValue.equals(getSelected());
	}
	
	@Override
	public boolean isAnswer(String s,AnswerTestProperties overrideProperties)
	{
		return isSelected(s);
	}
	
	/**
	 * @return true if selected option is right, false if it is wrong or if there is no selected option
	 */
	@Override
	public boolean isRight()
	{
		boolean right=false;
		try
		{
			right=isRigthOption(getSelected());
		}
		catch (OmException e)
		{
			right=false;
		}
		return right;
	}
	
	/**
	 * Returns answer line.<br/>
	 * It is the display text from selected option or empty string if there is no selected option.
	 * @return Answer line
	 */
	@Override
	public String getAnswerLine()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String answerline="";
		try
		{
			answerline=getDisplayOption(getSelected());
			if (sq.isPlaceholdersInitialized() && StandardQuestion.containsPlaceholder(answerline))
			{
				answerline=sq.applyPlaceholders(answerline);
			}
		}
		catch (OmException e)
		{
			answerline="";
		}
		return answerline; 
	}
}
