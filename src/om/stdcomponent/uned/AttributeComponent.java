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
import om.helper.uned.NumericTester;
import om.helper.uned.Tester;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;
import om.stdquestion.uned.StandardQuestion.QuestionCounters;

// UNED: 11-07-2011 - dballestin
/**
 * This is a component used to declare an attribute displayed to user as some text according to
 * its value.
 * <br/><br/>
 * <h2>Attributes</h2>
 * <table border="1">
 * <tr><th>Attribute</th><th>Description</th></tr>
 * <tr><td>attempt</td><td>Attempt number</td></tr>
 * <tr><td>maxattempts</td><td>Maximum number of attempts allowed</td></tr>
 * <tr><td>questionline</td><td>Question line</td></tr>
 * <tr><td>defaultquestionline</td><td>Default question line</td></tr>
 * <tr><td>answerid</td><td>Answer's component id</td></tr>
 * <tr><td>answertagname</td><td>Answer's component tag name</td></tr>
 * <tr><td>answerline</td><td>Answer line</td></tr>
 * <tr><td>answernum</td>Number's value of answer<td></td></tr>
 * <tr><td>defaultanswerline</td><td>Default answer line</td></tr>
 * <tr><td>defaultsummaryline</td><td>Default summary line</td></tr>
 * <tr><td>selectableanswers</td><td>Number of selectable answers.<br/><br/>
 * Note that it is not number of answer's components.<br/>
 * It is more the maximum number of answer's components selectable by user at same time 
 * in an attempt.</td></tr>
 * <tr><td>selectablerightanswers</td><td>Number of selectable right answers.<br/><br/>
 * Note that it is not the number of right answer's components.<br/>
 * It is more the maximum number of right answer's components selectable by user at same time 
 * in an attempt.</td></tr>
 * <tr><td>selectablewronganswers</td><td>Number of selectable wrong answers.<br/><br/>
 * Note that it is not the number of wrong answer's components.<br/>
 * It is more the maximum number of wrong answer's components selectable by user at same time 
 * in an attempt.</td></tr>
 * <tr><td>selectedanswers</td><td>Number of selected answers</td></tr>
 * <tr><td>selectedrightanswers</td><td>Number of selected right answers</td></tr>
 * <tr><td>selectedwronganswers</td><td>Number of selected wrong answers</td></tr>
 * <tr><td>unselectedanswers</td><td>Number of unselected answers</td></tr>
 * <tr><td>unselectedrightanswers</td><td>Number of unselected right answers</td></tr>
 * <tr><td>unselectedwronganswers</td><td>Number of unselected wrong answers</td></tr>
 * <tr><td>rightdistance</td><td>Distance to the right answer, 0 means that this is 
 * the right answer.<br/><br/>You can think about it as the number of errors in answer</td></tr>
 * <tr><td>right</td><td>Answer rightness: "right" or "wrong"</td></tr>
 * <tr><td>isright</td><td>"yes" if answer is right, "no" if it is wrong</td></tr>
 * <tr><td>isrightbit</td><td>"1" if answer is right, "0" if it is wrong</td></tr>
 * <tr><td>iswrong</td><td>"yes" if answer is wrong, "no" if it is right</td></tr>
 * <tr><td>iswrongbit</td><td>"1" if answer is wrong, "0" if it is right</td></tr>
 * <tr><td>random</td><td>Value of a random component</td></tr>
 * <tr><td>randomindex</td><td>Index of a random component</td></tr>
 * <tr><td>if</td><td>Some value if a condition is matched and optionally other 
 * if it is not matched</td></tr>
 * <tr><td>ifanswered</td><td>Some value if a component is in the answer and optionally other
 * if it is not</td></tr>
 * <tr><td>answermap</td><td>Mapped text value corresponding to an answer key</td></tr>
 * <tr><td>variable</td><td>Value of a variable or a random component</td></tr>
 * </table>
 * <br/>
 * <h2>XML usage</h2>
 * &lt;attribute attribute="..." ... /&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, 
 * like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>attribute</td><td>(string)</td><td>Specifies attribute name (see <b>Attributes</b>).
 * </td></tr>
 * </table>
 */
public class AttributeComponent extends QComponent
{
	/** Attribute name for getting the attempt number */
	public static final String ATTRIBUTE_ATTEMPT="attempt";
	
	/** Attribute name for getting the maximum number of attempts allowed */
	public static final String ATTRIBUTE_MAXATTEMPTS="maxattempts";
	
	/** Attribute name for getting the question line */
	public static final String ATTRIBUTE_QUESTIONLINE="questionline";
	
	/** Attribute name for getting the default question line */
	public static final String ATTRIBUTE_DEFAULTQUESTIONLINE="defaultquestionline";
	
	/** Attribute name for getting the answer's component id */
	public static final String ATTRIBUTE_ANSWERID="answerid";
	
	/** Attribute name for getting the answer's component tag name */
	public static final String ATTRIBUTE_ANSWERTAGNAME="answertagname";
	
	/** Attribute name for getting the answer line */
	public static final String ATTRIBUTE_ANSWERLINE="answerline";
	
	/** Attribute name for getting the number's value of answer */
	public static final String ATTRIBUTE_ANSWERNUM="answernum";
	
	/** Attribute name for getting the default answer line */
	public static final String ATTRIBUTE_DEFAULTANSWERLINE="defaultanswerline";
	
	/** Attribute name for getting the default summary line */
	public static final String ATTRIBUTE_DEFAULTSUMMARYLINE="defaultsummaryline";
	
	/** Attribute name for getting the number of selectable answers */
	public static final String ATTRIBUTE_SELECTABLEANSWERS="selectableanswers";
	
	/** Attribute name for getting the number of selectable right answers */
	public static final String ATTRIBUTE_SELECTABLERIGHTANSWERS="selectablerightanswers";
	
	/** Attribute name for getting the number of selectable wrong answers */
	public static final String ATTRIBUTE_SELECTABLEWRONGANSWERS="selectablewronganswers";
	
	/** Attribute name for getting the number of selected answers */
	public static final String ATTRIBUTE_SELECTEDANSWERS="selectedanswers";
	
	/** Attribute name for getting the number of selected right answers */
	public static final String ATTRIBUTE_SELECTEDRIGHTANSWERS="selectedrightanswers";
	
	/** Attribute name for getting the number of selected wrong answers */
	public static final String ATTRIBUTE_SELECTEDWRONGANSWERS="selectedwronganswers";
	
	/** Attribute name for getting the number of unselected answers */
	public static final String ATTRIBUTE_UNSELECTEDANSWERS="unselectedanswers";
	
	/** Attribute name for getting the number of unselected right answers */
	public static final String ATTRIBUTE_UNSELECTEDRIGHTANSWERS="unselectedrightanswers";
	
	/** Attribute name for getting the number of unselected wrong answers */
	public static final String ATTRIBUTE_UNSELECTEDWRONGANSWERS="unselectedwronganswers";
	
	/** Attribute name for getting the distance to the right answer */
	public static final String ATTRIBUTE_RIGHTDISTANCE="rightdistance";
	
	/** Attribute name for getting answer rightness (right/wrong) */
	public static final String ATTRIBUTE_RIGHT="right";
	
	/** Attribute name for getting if answer is right (yes/no) */
	public static final String ATTRIBUTE_ISRIGHT="isright";
	
	/** Attribute name for getting if answer is right (1/0) */
	public static final String ATTRIBUTE_ISRIGHTBIT="isrightbit";
	
	/** Attribute name for getting if answer is wrong (1/0) */
	public static final String ATTRIBUTE_ISWRONG="iswrong";
	
	/** Attribute name for getting if answer is wrong (1/0) */
	public static final String ATTRIBUTE_ISWRONGBIT="iswrongbit";
	
	/** Attibute name for getting value of a random component */
	public static final String ATTRIBUTE_RANDOM="random";
	
	/** Attibute name for getting index of a random component */
	public static final String ATTRIBUTE_RANDOMINDEX="randomindex";
	
	/** Attribute name for getting a mapped text value corresponding to an answer key */
	public static final String ATTRIBUTE_ANSWERMAP="answermap";
	
	/** 
	 * Attribute name for getting some value if a condition is matched and optionally other if 
	 * it is not matched
	 */
	public static final String ATTRIBUTE_IF="if";
	
	/**
	 * Attribute name for getting some value if a component is in the answer and optionally other if
	 * it is not
	 */
	public static final String ATTRIBUTE_IFANSWERED="ifanswered";
	
	/**
	 * Attribute name for getting the value of a variable component
	 */
	public static final String ATTRIBUTE_VARIABLE="variable";
	
	/** Property name for the attribute name (string) */
	public static final String PROPERTY_ATTRIBUTE="attribute";
	
	/** 
	 * Character used in some properties (answerid, answertagname, answerline, defaultanswerline)
	 * to open a selector used for selecting a single value from property
	 */
	public static final char SELECTOR_OPEN='['; 
	
	/** 
	 * Character used in some properties (answerid, answertagname, answerline, defaultanswerline)
	 * to close a selector used for selecting a single value from property
	 */
	public static final char SELECTOR_CLOSE=']'; 
	
	/**
	 * Character used in selector of an if attribute to separate condition and true value
	 */
	private static final char THEN_SYMBOL='?';
	
	
	/**
	 * Character used in selector of an if attribute to separate true value and else value
	 */
	private static final char ELSE_SYMBOL=':';
	
	/**
	 * Character used in selector of an answermap attribute to separate answer identifiers and 
	 * their mapped text values
	 */
	private static final char COLON_SYMBOL=':';
	
	/**
	 * Character used as OR operator to separate answer identifiers or mapped text values of an
	 * answermap attribute
	 */
	private static final char ANSWERMAP_OR_SYMBOL=',';
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_ATTRIBUTE
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
		return "attribute";
	}
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineString(PROPERTY_ATTRIBUTE);
	}
	
	/** @return Attribute including selector if exists */
	public String getAttribute()
	{
		try
		{
			return getString(PROPERTY_ATTRIBUTE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the attribute with selector included if exists.
	 * @param attribute Attribute name with selector included if exists
	 */
	public void setAttribute(String attribute)
	{
		try
		{
			setString(PROPERTY_ATTRIBUTE,attribute);
		}
		catch(OmDeveloperException e)
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
		if (!placeholders.containsKey(PROPERTY_ATTRIBUTE))
		{
			requiredAttributes.add(PROPERTY_ATTRIBUTE);
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
		if (eThis.getFirstChild()!=null)
		{
			throw new OmFormatException("<attribute> may not include children");
		}
	}
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String idAnswer=sq.peekForIdAnswer();
		String attributeValue=idAnswer==null?getAttributeValue():getAttributeValue(idAnswer);
		qc.addInlineXHTML(qc.getOutputDocument().createTextNode(attributeValue));
		qc.addTextEquivalent(attributeValue);
	}
	
	/**
	 * Gets the attribute name without selector from "attribute" property
	 * @return Attribute name without selector from "attribute" property
	 */
	private String getAttributeName()
	{
		return getAttributeName(getAttribute());
	}
	
	/**
	 * Gets the attribute name without selector from attribute received
	 * @param attribute Attribute
	 * @return Attribute name without selector from attribute received
	 */
	private String getAttributeName(String attribute)
	{
		String attributeName=attribute;
		Map<Character, String> escapeSequences=AnswerChecking.getEscapeSequences();
		int iStartSelector=AnswerChecking.indexOfCharacter(attribute,SELECTOR_OPEN,escapeSequences);
		if (iStartSelector!=-1)
		{
			attributeName=attribute.substring(0,iStartSelector);
		}
		return AnswerChecking.replaceEscapeChars(attributeName);
	}
	
	/**
	 * Gets attribute's selector if defined from "attribute" property, otherwise return null.<br/><br/>
	 * <b>IMPORTANT:</b> This method has been implemented to allow using characters '[' and ']' 
	 * inside the selector, so if a new '[' character is found next ']' character it is considered 
	 * to close that '[' character and it is not considered the end of selector. However 
	 * it is not able to match nested ocurrences of '[' and ']' characters.
	 * @return Attribute's selector if defined, null otherwise
	 */
	private String getSelector()
	{
		String selector=null;
		String attribute=getAttribute();
		if (attribute!=null)
		{
			selector=getSelector(attribute);
		}
		return selector;
	}
	
	/**
	 * Gets attribute's selector if defined from attribute received, otherwise return null.<br/><br/>
	 * <b>IMPORTANT:</b> This method has been implemented to allow using characters '[' and ']' 
	 * inside the selector, so if a new '[' character is found next ']' character it is considered 
	 * to close that '[' character and it is not considered the end of selector. However 
	 * it is not able to match nested ocurrences of '[' and ']' characters.
	 * @param attribute Attribute
	 * @return Attribute's selector if defined, null otherwise
	 */
	private String getSelector(String attribute)
	{
		String selector=null;
		Map<Character, String> escapeSequences=AnswerChecking.getEscapeSequences();
		if (attribute!=null)
		{
			int iStartSelector=AnswerChecking.indexOfCharacter(attribute,SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				iStartSelector++;
				boolean searchEndSelector=true;
				int iEndSelector=AnswerChecking.indexOfCharacter(
						attribute,SELECTOR_CLOSE,iStartSelector,escapeSequences);
				int i=iStartSelector;
				while (iEndSelector!=-1 && searchEndSelector)
				{
					int iOtherStartSelector=
							AnswerChecking.indexOfCharacter(attribute,SELECTOR_OPEN,i,escapeSequences);
					if (iOtherStartSelector!=-1 && iOtherStartSelector<iEndSelector)
					{
						int iEndSelectorTmp=iEndSelector;
						i=iEndSelector+1;
						iEndSelector=
								AnswerChecking.indexOfCharacter(attribute,SELECTOR_CLOSE,i,escapeSequences);
						if (iEndSelector==-1)
						{
							iEndSelector=iEndSelectorTmp;
							searchEndSelector=false;
						}
					}
					else
					{
						searchEndSelector=false;
					}
				}
				if (iEndSelector!=-1)
				{
					selector=attribute.substring(iStartSelector,iEndSelector);
				}
			}
		}
		return selector;
	}
	
	/**
	 * Gets testable component used to test condition
	 * @param ifCondition If condition
	 * @return Testable component used to test condition
	 */
	private Testable getTestableCondition(String ifCondition)
	{
		Testable testableCondition=null;
		if (ifCondition!=null)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			QComponent qc=null;
			try
			{
				qc=sq.getComponent(ifCondition);
			}
			catch (OmDeveloperException e)
			{
				qc=null;
			}
			if (qc!=null && qc instanceof Testable)
			{
				testableCondition=(Testable)qc;
			}
		}
		return testableCondition;
	}
	
	/**
	 * Gets condition for "if" attribute
	 * @param selector Selector
	 * @return Condition for "if" attribute
	 */
	private String getIfCondition(String selector)
	{
		String ifCondition=null;
		int iEndCondition=selector.indexOf(THEN_SYMBOL);
		if (iEndCondition>=0)
		{
			ifCondition=selector.substring(0,iEndCondition);
		}
		return ifCondition;
	}
	
	/**
	 * Gets value for "if" attribute if condition is true 
	 * @param selector Selector
	 * @return Value for "if" attribute if condition is true
	 */
	private String getThenValue(String selector)
	{
		String thenValue="";
		Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
		int iThen=AnswerChecking.indexOfCharacter(selector,THEN_SYMBOL,escapeSequences);
		if (iThen!=-1 && iThen+1<selector.length())
		{
			int iEndThen=AnswerChecking.indexOfCharacter(selector,ELSE_SYMBOL,iThen+1,escapeSequences);
			if (iEndThen==-1)
			{
				thenValue=selector.substring(iThen+1);
			}
			else
			{
				thenValue=selector.substring(iThen+1,iEndThen);
			}
		}
		return AnswerChecking.replaceEscapeChars(thenValue);
	}
	
	/**
	 * Gets value for "if" attribute if condition is false 
	 * @param selector Selector
	 * @return Value for "if" attribute if condition is false
	 */
	private String getElseValue(String selector)
	{
		String elseValue="";
		Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
		int iThen=AnswerChecking.indexOfCharacter(selector,THEN_SYMBOL,escapeSequences);
		if (iThen!=-1 && iThen+1<selector.length())
		{
			int iElse=AnswerChecking.indexOfCharacter(selector,ELSE_SYMBOL,iThen+1,escapeSequences);
			if (iElse!=-1 && iElse+1<selector.length())
			{
				elseValue=selector.substring(iElse+1);
			}
		}
		return AnswerChecking.replaceEscapeChars(elseValue);
	}
	
	/**
	 * Gets list of answer identifiers for "answermap" attribute
	 * @param selector Selector
	 * @return List of answer identifiers for "answermap" attribute
	 */
	private String getAnswerMapIdentifiers(String selector)
	{
		String answerMapIdentifiers=null;
		Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
		int iEndAnswerMapIdentifiers=
				AnswerChecking.indexOfCharacter(selector,COLON_SYMBOL,escapeSequences);
		if (iEndAnswerMapIdentifiers>0)
		{
			answerMapIdentifiers=selector.substring(0,iEndAnswerMapIdentifiers);
		}
		return answerMapIdentifiers;
		//return answerMapIdentifiers==null?null:AnswerChecking.replaceEscapeChars(answerMapIdentifiers);
	}
	
	/**
	 * Gets list of answer mapped text values for "answermap" attribute
	 * @param selector Selector
	 * @return List of answer mapped text values for "answermap" attribute
	 */
	private String getAnswerMapValues(String selector)
	{
		String answerMapValues=null;
		Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
		int iStartAnswerMapValues=AnswerChecking.indexOfCharacter(selector,COLON_SYMBOL,escapeSequences);
		if (iStartAnswerMapValues!=-1 && iStartAnswerMapValues+1<selector.length())
		{
			answerMapValues=selector.substring(iStartAnswerMapValues+1);
		}
		return answerMapValues;
		//return answerMapValues==null?null:AnswerChecking.replaceEscapeChars(answerMapValues);
	}
	
	/**
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if the user's answer is right for the answer's component, false if not
	 */
	private boolean isRight(String idAnswer)
	{
		boolean right=false;
		StandardQuestion q=(StandardQuestion)getQuestion();
		List<String> idAnswers=q.getIdAnswers();
		if (idAnswers!=null && idAnswers.contains(idAnswer))
		{
			QComponent qc=null;
			try
			{
				qc=q.getComponent(idAnswer);
			}
			catch (OmDeveloperException ode)
			{
				qc=null;
			}
			if (qc!=null && qc instanceof Answerable)
			{
				right=((Answerable)qc).isRight();
			}
		}
		return right;
	}
	
	/**
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if the user's answer is wrong for the answer's component, false if not
	 */
	private boolean isWrong(String idAnswer)
	{
		boolean wrong=false;
		StandardQuestion q=(StandardQuestion)getQuestion();
		List<String> idAnswers=q.getIdAnswers();
		if (idAnswers!=null && idAnswers.contains(idAnswer))
		{
			QComponent qc=null;
			try
			{
				qc = q.getComponent(idAnswer);
			}
			catch (OmDeveloperException ode)
			{
				qc=null;
			}
			if (qc!=null && qc instanceof Answerable)
			{
				wrong=!((Answerable)qc).isRight();
			}
		}
		return wrong;
	}
	
	/**
	 * Gets attribute value from attribute that no depends on answer's component identifiers<br/><br/>
	 * Used to avoid code duplication.
	 * @return Attribute value
	 */
	private String getNoAnswerAttributeValue()
	{
		StringBuffer attributeValue=new StringBuffer();
		StandardQuestion q=(StandardQuestion)getQuestion();
		String attributeName=getAttributeName();
		if (attributeName.equals(ATTRIBUTE_ATTEMPT))
		{
			attributeValue.append(q.getAttempt());
		}
		else if (attributeName.equals(ATTRIBUTE_MAXATTEMPTS))
		{
			attributeValue.append(q.getMaxAttempts());
		}
		else if (attributeName.equals(ATTRIBUTE_QUESTIONLINE))
		{
			attributeValue.append(q.getQuestionLine());
		}
		else if (attributeName.equals(ATTRIBUTE_DEFAULTQUESTIONLINE))
		{
			attributeValue.append(q.getDefaultQuestionLine());
		}
		else if (attributeName.equals(ATTRIBUTE_SELECTABLEANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'selectableanswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getSelectableAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_SELECTABLERIGHTANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'selectablerightanswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getSelectableRightAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_SELECTABLEWRONGANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'selectablewronganswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getSelectableWrongAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_SELECTEDANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'selectedanswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getSelectedAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_SELECTEDRIGHTANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'selectedrightanswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getSelectedRightAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_SELECTEDWRONGANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'selectedwronganswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getSelectedWrongAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_UNSELECTEDANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'unselectedanswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getUnselectedAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_UNSELECTEDRIGHTANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'unselectedrightanswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getUnselectedRightAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_UNSELECTEDWRONGANSWERS))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'unselectedwronganswers' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getUnselectedWrongAnswers());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_RIGHTDISTANCE))
		{
			QuestionCounters counters=q.getCounters();
			if (counters==null)
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Can't get 'rightdistance' because counters have not been initialized");
				throw new AttributeFormatRuntimeException(error.toString());
			}
			else
			{
				attributeValue.append(counters.getRightDistance());
			}
		}
		else if (attributeName.equals(ATTRIBUTE_RANDOM))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				try
				{
					QComponent qc=q.getComponent(selector);
					if (qc instanceof RandomComponent)
					{
						attributeValue.append(((RandomComponent)qc).getValue());
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(getTagName());
						error.append(">: Component with id '");
						error.append(selector);
						error.append("' is not a random component");
						throw new AttributeFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: Can't find a component with id: ");
					error.append(selector);
					throw new AttributeFormatRuntimeException(error.toString());
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_RANDOMINDEX))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				try
				{
					QComponent qc=q.getComponent(selector);
					if (qc instanceof RandomComponent)
					{
						attributeValue.append(((RandomComponent)qc).getIndex());
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(getTagName());
						error.append(">: Component with id '");
						error.append(selector);
						error.append("' is not a random component");
						throw new AttributeFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: Can't find a component with id: ");
					error.append(selector);
					throw new AttributeFormatRuntimeException(error.toString());
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_VARIABLE))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				try
				{
					QComponent qc=q.getComponent(selector);
					if (qc instanceof VariableComponent)
					{
						attributeValue.append(((VariableComponent)qc).getValue());
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(getTagName());
						error.append(">: Component with id '");
						error.append(selector);
						error.append("' is not a variable component");
						throw new AttributeFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: Can't find a component with id: ");
					error.append(selector);
					throw new AttributeFormatRuntimeException(error.toString());
				}
			}
		}
		else
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: Unrecognized attribute's name: ");
			error.append(attributeName);
			throw new AttributeFormatRuntimeException(error.toString());
		}
		return attributeValue.toString();
	}
	
	/**
	 * Gets attribute value
	 * @param idAnswer Identifier of component selected for answer
	 * @return Attribute value
	 */
	protected String getAttributeValue(String idAnswer)
	{
		StringBuffer attributeValue=new StringBuffer();
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String attributeName=getAttributeName();
		if (attributeName.equals(ATTRIBUTE_ANSWERID))
		{
			if (idAnswer!=null)
			{
				String selector=getSelector();
				if (selector==null)
				{
					attributeValue.append(idAnswer);
				}
				else
				{
					attributeValue.append(selector);
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERTAGNAME))
		{
			if (idAnswer!=null)
			{
				String selector=getSelector();
				if (selector==null)
				{
					try
					{
						String tagName=sq.getComponentTagName(sq.getComponent(idAnswer));
						attributeValue.append(tagName);
					}
					catch (OmDeveloperException e)
					{
						throw new OmUnexpectedException(e.getMessage(),e);
					}
				}
				else
				{
					try
					{
						String tagName=sq.getComponentTagName(sq.getComponent(selector));
						attributeValue.append(tagName);
					}
					catch (OmDeveloperException e)
					{
						throw new OmUnexpectedException(e.getMessage(),e);
					}
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERLINE))
		{
			String selector=getSelector();
			if (selector==null)
			{
				try
				{
					String answerline=sq.getAnswerLine(idAnswer);
					attributeValue.append(answerline);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
			else
			{
				try
				{
					String answerline=sq.getAnswerLine(selector);
					attributeValue.append(answerline);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERNUM))
		{
			String selector=getSelector();
			if (selector==null)
			{
				String answerline=null;
				try
				{
					answerline=sq.getAnswerLine(idAnswer);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
				if (answerline!=null)
				{
					attributeValue.append(NumericTester.extractNumber(answerline));
				}
			}
			else
			{
				String answerline=null;
				try
				{
					answerline=sq.getAnswerLine(selector);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
				if (answerline!=null)
				{
					attributeValue.append(NumericTester.extractNumber(answerline));
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_DEFAULTANSWERLINE))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.getDefaultAnswerLine(idAnswer));
			}
			else
			{
				attributeValue.append(sq.getDefaultAnswerLine(selector));
			}
		}
		else if (attributeName.equals(ATTRIBUTE_DEFAULTSUMMARYLINE))
		{
			attributeValue.append(sq.getDefaultActionSummaryLine(idAnswer));
		}
		else if (attributeName.equals(ATTRIBUTE_RIGHT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(isRight(idAnswer)?"right":isWrong(idAnswer)?"wrong":"");
			}
			else
			{
				attributeValue.append(isRight(selector)?"right":isWrong(selector)?"wrong":"");
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISRIGHT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(isRight(idAnswer)?"yes":"no");
			}
			else
			{
				attributeValue.append(isRight(selector)?"yes":"no");
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISRIGHTBIT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(isRight(idAnswer)?'1':'0');
			}
			else
			{
				attributeValue.append(isRight(selector)?'1':'0');
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISWRONG))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(isWrong(idAnswer)?"yes":"no");
			}
			else
			{
				attributeValue.append(isWrong(selector)?"yes":"no");
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISWRONGBIT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(isWrong(idAnswer)?'1':'0');
			}
			else
			{
				attributeValue.append(isWrong(selector)?'1':'0');
			}
		}
		else if (attributeName.equals(ATTRIBUTE_IF))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				Testable testableCondition=getTestableCondition(getIfCondition(selector));
				if (testableCondition!=null)
				{
					if (Tester.testAll(sq,testableCondition,idAnswer))
					{
						attributeValue.append(getThenValue(selector));
					}
					else
					{
						attributeValue.append(getElseValue(selector));
					}
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_IFANSWERED))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				String ifCondition=getIfCondition(selector);
				if (ifCondition!=null)
				{
					String onlyIdAnswer=getAttributeName(ifCondition);
					boolean testing=false;
					if (onlyIdAnswer==null || onlyIdAnswer.equals(""))
					{
						onlyIdAnswer=idAnswer;
					}
					List<String> idAnswers=sq.getIdAnswers();
					if (onlyIdAnswer!=null && idAnswers!=null && idAnswers.contains(onlyIdAnswer))
					{
						QComponent qcAnswer=null;
						try
						{
							qcAnswer=sq.getComponent(onlyIdAnswer);
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append('<');
							error.append(getTagName());
							error.append(">: Can't find a component with id: ");
							error.append(onlyIdAnswer);
							throw new AttributeFormatRuntimeException(error.toString(),e);
						}
						if (qcAnswer instanceof Answerable)
						{
							String answerSelector=getSelector(ifCondition);
							if (answerSelector==null)
							{
								testing=true;
							}
							else
							{
								testing=((Answerable)qcAnswer).isAnswer(
										answerSelector,new AnswerTestProperties());
							}
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append('<');
							error.append(getTagName());
							error.append(">: Component with id '");
							error.append(onlyIdAnswer);
							error.append("' is not an answer component");
							throw new AttributeFormatRuntimeException(error.toString());
						}
					}
					if (testing)
					{
						attributeValue.append(getThenValue(selector));
					}
					else
					{
						attributeValue.append(getElseValue(selector));
					}
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERMAP))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				String mapAnswerIds=getAnswerMapIdentifiers(selector);
				String mapAnswerValues=getAnswerMapValues(selector);
				if (mapAnswerIds!=null && mapAnswerValues!=null)
				{
					List<String> orMapAnswersIds=AnswerChecking.split(
							mapAnswerIds,ANSWERMAP_OR_SYMBOL,AnswerChecking.getEscapeSequences());
					List<String> orMapAnswersValues=AnswerChecking.split(
							mapAnswerValues,ANSWERMAP_OR_SYMBOL,AnswerChecking.getEscapeSequences());
					if (orMapAnswersIds.size()!=orMapAnswersValues.size())
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(getTagName());
						error.append(">: 'answermap' has a different number of answer's identifiers and values");
						error.append(selector);
						throw new AttributeFormatRuntimeException(error.toString());
					}
					for (int i=0;i<orMapAnswersIds.size();i++)
					{
						String orAnswerId=orMapAnswersIds.get(i);
						String onlyIdAnswer=getAttributeName(orAnswerId);
						boolean testing=false;
						if (onlyIdAnswer.equals(idAnswer))
						{
							QComponent qcAnswer=null;
							try
							{
								qcAnswer=sq.getComponent(onlyIdAnswer);
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append('<');
								error.append(getTagName());
								error.append(">: Can't find a component with id: ");
								error.append(onlyIdAnswer);
								throw new AttributeFormatRuntimeException(error.toString(),e);
							}
							if (qcAnswer instanceof Answerable)
							{
								String answerSelector=getSelector(orAnswerId);
								if (answerSelector==null)
								{
									testing=true;
								}
								else
								{
									testing=((Answerable)qcAnswer).isAnswer(
											answerSelector,new AnswerTestProperties());
								}
							}
							else
							{
								StringBuffer error=new StringBuffer();
								error.append('<');
								error.append(getTagName());
								error.append(">: Component with id '");
								error.append(onlyIdAnswer);
								error.append("' is not an answer component");
								throw new AttributeFormatRuntimeException(error.toString());
							}
						}
						if (testing)
						{
							attributeValue.append(
								AnswerChecking.replaceEscapeChars(orMapAnswersValues.get(i)));
						}
					}
				}
			}
		}
		else
		{
			attributeValue.append(getNoAnswerAttributeValue());
		}
		return attributeValue.toString();
	}
	
	/**
	 * Gets attribute value
	 * @return Attribute value
	 */
	protected String getAttributeValue()
	{
		StringBuffer attributeValue=new StringBuffer();
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String attributeName=getAttributeName();
		if (attributeName.equals(ATTRIBUTE_ANSWERID))
		{
			String selector=getSelector();
			if (selector==null)
			{
				List<String> idAnswers=sq.getIdAnswers();
				if (idAnswers!=null)
				{
					for (String idAnswer:idAnswers)
					{
						if (attributeValue.length()>0)
						{
							attributeValue.append(", ");
						}
						attributeValue.append(idAnswer);
					}
				}
			}
			else
			{
				attributeValue.append(selector);
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERTAGNAME))
		{
			String selector=getSelector();
			if (selector==null)
			{
				List<String> idAnswers=sq.getIdAnswers();
				if (idAnswers!=null)
				{
					for (String idAnswer:idAnswers)
					{
						if (attributeValue.length()>0)
						{
							attributeValue.append(", ");
						}
						try
						{
							String tagName=sq.getComponentTagName(sq.getComponent(idAnswer));
							attributeValue.append(tagName);
						}
						catch (OmDeveloperException e)
						{
							throw new OmUnexpectedException(e.getMessage(),e);
						}
					}
				}
			}
			else
			{
				try
				{
					String tagName=sq.getComponentTagName(sq.getComponent(selector));
					attributeValue.append(tagName);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERLINE))
		{
			String selector=getSelector();
			if (selector==null)
			{
				try
				{
					String answerline=sq.getAnswerLine();
					attributeValue.append(answerline);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
			else
			{
				try
				{
					String answerline=sq.getAnswerLine(selector);
					attributeValue.append(answerline);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERNUM))
		{
			String selector=getSelector();
			if (selector==null)
			{
				String answerline=null;
				try
				{
					answerline=sq.getAnswerLine();
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
				if (answerline!=null)
				{
					attributeValue.append(NumericTester.extractNumber(answerline));
				}
			}
			else
			{
				String answerline=null;
				try
				{
					answerline=sq.getAnswerLine(selector);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
				if (answerline!=null)
				{
					attributeValue.append(NumericTester.extractNumber(answerline));
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_DEFAULTANSWERLINE))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.getDefaultAnswerLine());
			}
			else
			{
				attributeValue.append(sq.getDefaultAnswerLine(selector));
			}
		}
		else if (attributeName.equals(ATTRIBUTE_DEFAULTSUMMARYLINE))
		{
			attributeValue.append(sq.getDefaultActionSummaryLine());
		}
		else if (attributeName.equals(ATTRIBUTE_RIGHT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.isRight()?"right":"wrong");
			}
			else
			{
				attributeValue.append(isRight(selector)?"right":isWrong(selector)?"wrong":"");
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISRIGHT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.isRight()?"yes":"no");
			}
			else
			{
				attributeValue.append(isRight(selector)?"yes":"no");
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISRIGHTBIT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.isRight()?'1':'0');
			}
			else
			{
				attributeValue.append(isRight(selector)?'1':'0');
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISWRONG))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.isRight()?"no":"yes");
			}
			else
			{
				attributeValue.append(isWrong(selector)?"yes":"no");
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ISWRONGBIT))
		{
			String selector=getSelector();
			if (selector==null)
			{
				attributeValue.append(sq.isRight()?'0':'1');
			}
			else
			{
				attributeValue.append(isWrong(selector)?'1':'0');
			}
		}
		else if (attributeName.equals(ATTRIBUTE_IF))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				Testable testableCondition=getTestableCondition(getIfCondition(selector));
				if (testableCondition!=null)
				{
					if (Tester.testAll(sq,testableCondition))
					{
						attributeValue.append(getThenValue(selector));
					}
					else
					{
						attributeValue.append(getElseValue(selector));
					}
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_IFANSWERED))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				String ifCondition=getIfCondition(selector);
				if (ifCondition!=null)
				{
					String onlyIdAnswer=getAttributeName(ifCondition);
					boolean testing=false;
					List<String> idAnswers=sq.getIdAnswers();
					if (onlyIdAnswer!=null && idAnswers!=null && idAnswers.contains(onlyIdAnswer))
					{
						QComponent qcAnswer=null;
						try
						{
							qcAnswer=sq.getComponent(onlyIdAnswer);
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append('<');
							error.append(getTagName());
							error.append(">: Can't find a component with id: ");
							error.append(onlyIdAnswer);
							throw new AttributeFormatRuntimeException(error.toString(),e);
						}
						if (qcAnswer instanceof Answerable)
						{
							String answerSelector=getSelector(ifCondition);
							if (answerSelector==null)
							{
								testing=true;
							}
							else
							{
								testing=((Answerable)qcAnswer).isAnswer(
										answerSelector,new AnswerTestProperties());
							}
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append('<');
							error.append(getTagName());
							error.append(">: Component with id '");
							error.append(onlyIdAnswer);
							error.append("' is not an answer component");
							throw new AttributeFormatRuntimeException(error.toString());
						}
					}
					if (testing)
					{
						attributeValue.append(getThenValue(selector));
					}
					else
					{
						attributeValue.append(getElseValue(selector));
					}
				}
			}
		}
		else if (attributeName.equals(ATTRIBUTE_ANSWERMAP))
		{
			String selector=getSelector();
			if (selector!=null)
			{
				String mapAnswerIds=getAnswerMapIdentifiers(selector);
				String mapAnswerValues=getAnswerMapValues(selector);
				if (mapAnswerIds!=null && mapAnswerValues!=null)
				{
					List<String> orMapAnswersIds=AnswerChecking.split(
							mapAnswerIds,ANSWERMAP_OR_SYMBOL,AnswerChecking.getEscapeSequences());
					List<String> orMapAnswersValues=AnswerChecking.split(
							mapAnswerValues,ANSWERMAP_OR_SYMBOL,AnswerChecking.getEscapeSequences());
					if (orMapAnswersIds.size()!=orMapAnswersValues.size())
					{
						StringBuffer error=new StringBuffer();
						error.append('<');
						error.append(getTagName());
						error.append(">: 'answermap' has a different number of answer's identifiers and values");
						error.append(selector);
						throw new AttributeFormatRuntimeException(error.toString());
					}
					for (int i=0;i<orMapAnswersIds.size();i++)
					{
						String orAnswerId=orMapAnswersIds.get(i);
						String onlyIdAnswer=getAttributeName(orAnswerId);
						boolean testing=false;
						List<String> idAnswers=sq.getIdAnswers();
						if (onlyIdAnswer!=null && idAnswers!=null && idAnswers.contains(onlyIdAnswer))
						{
							QComponent qcAnswer=null;
							try
							{
								qcAnswer=sq.getComponent(onlyIdAnswer);
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append('<');
								error.append(getTagName());
								error.append(">: Can't find a component with id: ");
								error.append(onlyIdAnswer);
								throw new AttributeFormatRuntimeException(error.toString(),e);
							}
							if (qcAnswer instanceof Answerable)
							{
								String answerSelector=getSelector(orAnswerId);
								if (answerSelector==null)
								{
									
									testing=true;
								}
								else
								{
									testing=((Answerable)qcAnswer).isAnswer(
											answerSelector,new AnswerTestProperties());
								}
							}
							else
							{
								StringBuffer error=new StringBuffer();
								error.append('<');
								error.append(getTagName());
								error.append(">: Component with id '");
								error.append(onlyIdAnswer);
								error.append("' is not an answer component");
								throw new AttributeFormatRuntimeException(error.toString());
							}
						}
						if (testing)
						{
							attributeValue.append(
								AnswerChecking.replaceEscapeChars(orMapAnswersValues.get(i)));
						}
					}
				}
			}
		}
		else
		{
			attributeValue.append(getNoAnswerAttributeValue());
		}
		return attributeValue.toString();
	}
}
