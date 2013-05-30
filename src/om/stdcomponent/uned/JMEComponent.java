package om.stdcomponent.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.uned.AnswerChecking;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.question.ActionParams;
import om.stdquestion.QComponent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

// UNED: 29-07-2011 - dballestin
/**
 * Inserts a pushbutton that users can press to open the JME (Java Molecular
 * Editor) component. A popup window will appear, containing the JME itself plus
 * 'OK' button.
 * <p>
 * If the user clicks 'OK', the internal SMILES value is updated and
 * (if there is an action set) the form will be submitted.
 * <p>
 * The popup remains visible during one question. If the user begins a new
 * question (or restarts the same one) it will vanish. It will also vanish if they
 * navigate to another website.
 * <p>
 * Note that the JME applet itself is not OU software, but apparently we have
 * an agreement to use it (for free, I think; it is normally free only for
 * noncommercial use). <a href="http://www.molinspiration.com/jme/">JME website</a>.<br/><br/>
 * <h2>XML usage</h2>
 * &lt;jme id='myjme' action='actionSubmit'/&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates all children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>label</td><td>(string)</td><td>Label for button (default is 'Draw molecule')</td></tr>
 * <tr><td>action</td><td>(string)</td><td>Name of method in question class that
 * is called after user clicks OK. Optional, default is to not submit form, but
 * you probably want to do it.</td></tr>
 * <tr><td>answerenabled</td><td>(boolean)</td><td>Specifies if this component is used as an answer 
 * component, by default yes</td></tr>
 * <tr><td>right</td><td>(string)</td><td>Specifies the right answer</td></tr>
 * <tr><td>ignorewedgebounds</td><td>(boolean)</td><td>Specifies if wedge bounds are ignored for 
 * testing purposes, by default no.<br/><br/>
 * Note that even if this property has been set to yes, wedge bounds will be still present in answerline, 
 * except if removewedgebounds property has been also set to yes.</td></tr>
 * <tr><td>removewedgebounds</td><td>(boolean)</td><td>Specifies if wedge bounds are removed from SMILES 
 * or not, by default no.<br/><br/>
 * Note that even if this property has been set to yes, wedge bounds will be used for testing purposes,
 * except if ignorewedgebounds property has been also set to yes.</td></tr>
 * <tr><td>custom</td><td>(string)</td><td>Selector used with custom action</td></tr>
 * </table>
 */
public class JMEComponent extends om.stdcomponent.JMEComponent implements Answerable
{
	/** Property name for the right answer (string) */
	public static final String PROPERTY_RIGHT="right";
	
	/** Property name to set if wedge bounds are ignored for testing purposes */
	public static final String PROPERTY_IGNOREWEDGEBOUNDS="ignorewedgebounds";
	
	/** Property name to set if wedge bounds are removed from SMILES or not */
	public static final String PROPERTY_REMOVEWEDGEBOUNDS="removewedgebounds";
	
	/** Property name for selector used with custom action (string) */
	public static final String PROPERTY_CUSTOM="custom";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_LABEL,PROPERTY_ACTION,
		PROPERTY_ANSWERENABLED,PROPERTY_RIGHT,PROPERTY_IGNOREWEDGEBOUNDS,PROPERTY_REMOVEWEDGEBOUNDS,
		PROPERTY_CUSTOM
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
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineBoolean(Answerable.PROPERTY_ANSWERENABLED);
		setBoolean(Answerable.PROPERTY_ANSWERENABLED,true);
		
		defineString(PROPERTY_RIGHT);
		setString(PROPERTY_RIGHT,null);
		
		defineBoolean(PROPERTY_IGNOREWEDGEBOUNDS);
		setBoolean(PROPERTY_IGNOREWEDGEBOUNDS,false);
		
		defineBoolean(PROPERTY_REMOVEWEDGEBOUNDS);
		setBoolean(PROPERTY_REMOVEWEDGEBOUNDS,false);
		
		defineString(PROPERTY_CUSTOM);
		setString(PROPERTY_CUSTOM,"");
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
	
	/** 
	 * @return Right answer
	 */
	public String getRight()
	{
		try
		{
			String right=getString(PROPERTY_RIGHT);
			if (right!=null)
			{
				right=AnswerChecking.replaceEscapeChars(right);
			}
			return right;
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the right answer.
	 * @param right Right answer
	 */
	public void setRight(String right)
	{
		try
		{
			setString(PROPERTY_RIGHT,right);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return true if wedge bounds are ignored for testing purposes, false otherwise
	 */
	public boolean isIgnoreWedgeBounds()
	{
		try
		{
			return getBoolean(PROPERTY_IGNOREWEDGEBOUNDS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if wedge bounds are ignored for testing purposes (true) or not (false)
	 * @param ignoreWedgeBounds Indicates if wedge bounds are going to be ignored for testing purposes (true) 
	 * or not (false)
	 */
	public void setIgnoreWedgeBounds(boolean ignoreWedgeBounds)
	{
		try
		{
			setBoolean(PROPERTY_IGNOREWEDGEBOUNDS,ignoreWedgeBounds);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return true if wedge bounds are removed from SMILES, false otherwise
	 */
	public boolean isRemoveWedgeBounds()
	{
		try
		{
			return getBoolean(PROPERTY_REMOVEWEDGEBOUNDS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if wedge bounds are removed from SMILES (true) or not (false)
	 * @param removeWedgeBounds Indicates if wedge bounds are going to be removed from SMILES (true) 
	 * or not (false)
	 */
	public void setRemoveWedgeBounds(boolean removeWedgeBounds)
	{
		try
		{
			setBoolean(PROPERTY_REMOVEWEDGEBOUNDS,removeWedgeBounds);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Selector used with custom action
	 */
	public String getCustom()
	{
		try
		{
			return getString(PROPERTY_CUSTOM);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the selector to use with custom action
	 * @param custom Selector to use with custom action
	 */
	public void setCustom(String custom)
	{
		try
		{
			setString(PROPERTY_CUSTOM,custom);
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
	 * Default implementation of this method generate a random token used to check when user goes to different 
	 * window and add a check for 'action' property for checking that a callback method is present and correctly 
	 * defined for its value.<br/><br/>
	 * If you override this method and want this default functionality still to be performed you need to include 
	 * 'super.initializeSpecific(eThis)' call inside your implementation.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	protected void initializeSpecific(Element eThis) throws OmException
	{
		//Generate random token used to check when user goes to different window
		StringBuffer token=new StringBuffer("t");
		token.append(getQuestion().getRandom().nextInt());
		token.append(getID().hashCode());
		sToken=token.toString();
		
		// We add a check for 'action' property for checking that a callback method is present and correctly defined 
		// for its value
		addSpecificCheck(
				PROPERTY_ACTION,
				new PropertyCheck()
				{
					@Override
					public void check(Object value) throws OmDeveloperException
					{
						getQuestion().checkCallback((String)value);
					}
				});
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
	
	@Override
	protected void formCallAction(String newValue,ActionParams ap) throws OmException
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		sq.callback(getString(PROPERTY_ACTION),getCustom());
	}
	
	/**
	 * Returns if the SMILES typed in this jme component corresponds to the SMILES received from 
	 * JME Mollecular Editor.
	 * @param s String with the expected SMILES
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the SMILES typed in this jme component corresponds to the SMILES received from 
	 * JME Mollecular Editor, false otherwise
	 */
	@Override
	public boolean isAnswer(String s,AnswerTestProperties overrideProperties)
	{
		return s!=null && s.equals(isIgnoreWedgeBounds()?getSMILESWithoutWedgeBounds():getSMILES());
	}
	
	/**
	 * Returns answer rightness.
	 * @return true if the answer is right, false if it is wrong
	 */
	@Override
	public boolean isRight()
	{
		return isAnswer(getRight(),new AnswerTestProperties());
	}
	
	/**
	 * Returns answer line.<br/>
	 * It is the SMILES received from JME Mollecular Editor.
	 * @return Answer line
	 */
	@Override
	public String getAnswerLine()
	{
		return isRemoveWedgeBounds()?getSMILESWithoutWedgeBounds():getSMILES();
	}
	
	// Code based on removeWedgeBounds() method from samples.twodresponse.jme.jmeQuestion
	/**
	 * Remove wedge bonds from a drawn structure.<br/><br/>
	 * It is applicable when stereochemistry is not important to the answer
	 * @return SMILES string but without wedge bounds
	 */
	private String getSMILESWithoutWedgeBounds()
	{
		String sSmiles=getSMILES();
		StringBuffer temp1=new StringBuffer();
		int atIndex=sSmiles.lastIndexOf('@');
		int sqIndex;
		int counter=0;
		while (atIndex>-1)
		{
			counter++;
			temp1.setLength(0);
			// check for @@ and remove @ character(s) from string
			if (sSmiles.charAt(atIndex-1)=='@')
			{
				temp1.append(sSmiles.substring(0,atIndex-1));
			}
			else
			{
				temp1.append(sSmiles.substring(0,atIndex));
			}
			temp1.append(sSmiles.substring(atIndex+1));
			sSmiles=temp1.toString();
			
			// search from @ for next ]
			sqIndex=sSmiles.indexOf(']',atIndex-1);
			temp1.setLength(0);
			temp1.append(sSmiles.substring(0,sqIndex));
			temp1.append(sSmiles.substring(sqIndex+1));
			sSmiles=temp1.toString();
			
			// search back from @ for first [
			sqIndex=sSmiles.lastIndexOf('[',atIndex);
			temp1.setLength(0);
			temp1.append(sSmiles.substring(0,sqIndex));
			temp1.append(sSmiles.substring(sqIndex+1));
			sSmiles=temp1.toString();
			
			atIndex=sSmiles.lastIndexOf('@');
		}
		return sSmiles;
	}
}
