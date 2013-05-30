package om.stdcomponent.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.stdquestion.QComponent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;
import util.xml.XML;

// UNED: 14-07-2011 - dballestin
/**
 * <p>A paragraph of plain text with selectable words.</p>
 * <p>Users of this component may wish to award partial marks if substantial numbers of words have to be found.
 * For example to recognise that students may have got close to the correct answer, i.e. getting 19 words out
 * of 20 on the last attempt, a score of 0 which SimpleQuestion1 would give seems harsh. Whereas using the
 * partial scoring method, depending on when they got to the 19 they would score something perhaps 40 - 60%.</p>
 * <p>OU authors should see omqstns\src\fels\lib\partialquestion1 and the nearby questions that use it such as 
 * omqstns\src\felsSMH\q01.<br/><br/>
 * <h2>XML usage</h2>
 * <pre>&lt;wordselect id="paragraph_1"&gt;
 * 	Some text that is not part of the answer 
 *  &lt;sw id="answer_1"&gt; required answer words &lt;/sw&gt;
 * 	Some more text then 
 * 	&lt;sw&gt; other required answer words auto id generation&lt;/sw&gt;
 * 	 etc...
 * &lt;/wordselect&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates all children</td></tr>
 * <tr><td>answerenabled</td><td>(boolean)</td><td>Specifies if this component is used as an answer 
 * component, by default yes</td></tr>
 * <tr><td>answerwordseparator</td><td>(string)</td><td>Separator between words for answer line</td></tr>
 * </table>
 */
public class WordSelectComponent extends om.stdcomponent.WordSelectComponent implements Answerable
{
	/** Property name for separator between words for answer line */
	public final static String PROPERTY_ANSWERWORDSEPARATOR="answerwordseparator";
	
	/** Default separator between words for answer line */
	public static final String DEFAULT_ANSWERWORDSEPARATOR=", ";
	
	/** Symbol used as OR operator in answer property: ',' */
	private static final String ANSWER_OR_SYMBOL=",";
	
	/** Symbol used as AND operator in answer property: '+' (UNICODE: '\u002B') */
	private static final String ANSWER_AND_SYMBOL="\\u002B";
	
	/** Symbol used as AND operator in answer property: '!' (UNICODE: '\u0021') */
	private static final String ANSWER_NOT_SYMBOL="\\u0021";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_ANSWERENABLED,PROPERTY_ANSWERWORDSEPARATOR
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
	
	private Map<String,String> replaceholderWordBlocks;
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		super.defineProperties();
		
		defineBoolean(PROPERTY_ANSWERENABLED);
		setBoolean(PROPERTY_ANSWERENABLED,true);
		
		defineString(PROPERTY_ANSWERWORDSEPARATOR);
		setString(PROPERTY_ANSWERWORDSEPARATOR,DEFAULT_ANSWERWORDSEPARATOR);
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
	
	/** @return Separator between word for answer line */
	public String getAnswerWordSeparator()
	{
		try
		{
			return getString(PROPERTY_ANSWERWORDSEPARATOR);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the separator between word for answer line.
	 * @param answerWordSeparator Separator between word for answer line
	 */
	public void setAnswerWordSeparator(String answerWordSeparator)
	{
		try
		{
			setString(PROPERTY_ANSWERWORDSEPARATOR,answerWordSeparator);
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
		StringBuffer sbText=new StringBuffer();
		int idCounter=1;
		replaceholderWordBlocks=new LinkedHashMap<String,String>();
		
		for(Node n=eThis.getFirstChild();n!=null;n=n.getNextSibling())
		{
			if (n instanceof Element)
			{
				Element e=(Element)n;
				if (e.getTagName().equals("sw"))
				{
					if (sbText.length()>0)
					{
						String id=Integer.toString(idCounter++);
						WordBlock wb=new WordBlock(sbText.toString(),id,getWordCount(),false, false);
						getWordBlocks().add(wb);
						getWordsById().put(id,wb);
						
						sbText.setLength(0);
					}
					String id;
					if (e.hasAttribute("id"))
					{
						id=e.getAttribute("id");
					} else
					{
						id=Integer.toString(idCounter++);
					}
					
					boolean right=true;
					if (e.hasAttribute("right"))
					{
						String rightValue=e.getAttribute("right");
						if (rightValue.equals("no"))
						{
							right=false;
						}
						else if (StandardQuestion.containsPlaceholder(rightValue))
						{
							replaceholderWordBlocks.put(id,rightValue);
							right=false;
						}
					}
					
					WordBlock wb=new WordBlock(XML.getText(e),id,getWordCount(),right,
							e.hasAttribute("highlight"));
					getWordBlocks().add(wb);
					getWordsById().put(id,wb);
				}
				else
				{
					throw new OmDeveloperException("<wordselect> can only contain <sw> tags");
				}
			}
			else if (n instanceof Text)
			{
				sbText.append(n.getNodeValue());
			}
		}
		if (sbText.length()>0)
		{
			String id=Integer.toString(idCounter++);
			WordBlock wb=new WordBlock(sbText.toString(),id,getWordCount(),false,false);
			getWordBlocks().add(wb);
			getWordsById().put(id,wb);
			
			sbText.setLength(0);
		}
	}
	
	/**
	 * Get number of words
	 * @return Number of words
	 */
	@Override
	public int getWordCount()
	{
		return super.getWordCount();
	}
	
	@Override
	public int getTotalWordsSelected()
	{
		int count=0;
		for (WordBlock wb:getWordBlocks())
		{
			for (Word w:wb.words)
			{
				if (w.selected)
				{
					count++;
				}
			}
		}
		return count;
	}
	
	@Override
	public int getTotalSWWordsSelected()
	{
		int count=0;
		for (WordBlock wb:getWordBlocks())
		{
			if (isWordBlockRightSW(wb))
			{
				for (Word w:wb.words)
				{
					if (w.selected)
					{
						count++;
					}
				}
			}
		}
		return count;
	}
	
	@Override
	public int getTotalSWWords()
	{
		int count=0;
		for (WordBlock wb:getWordBlocks())
		{
			if (isWordBlockRightSW(wb))
			{
				count+=wb.words.size();
			}
		}
		return count;
	}
	
	@Override
	public boolean getIsCorrect()
	{
		boolean correct=true;
		for (WordBlock wb:getWordBlocks())
		{
			if (isWordBlockRightSW(wb))
			{
				correct=getIsCorrect(wb.id);
			}
			else
			{
				for (Word w:wb.words)
				{
					if (w.selected)
					{
						correct=false;
						break;
					}
				}
			}
			if (!correct) {
				break;
			}
		}
		return correct;
	}
	
	/**
	 * @param swId the id of the block of words
	 * @return true only if all the words within an sw tag of the given id have been selected 
	 */
	public boolean getIsCorrect(String swId) {
		boolean correct=true;
		WordBlock wb=getWordsById().get(swId);
		for (Word w:wb.words)
		{
			if (!w.selected)
			{
				correct=false;
				break;
			}
		}
		return correct;
	}
	
	/**
	 * @param answer List of expected answer with sw tag ids separated with , (OR operator) 
	 * and + (AND operator) and also allows ! (NOT operator) before sw tag ids
	 * @return true if the answer is as expected, false otherwise 
	 */
	public boolean isAnswer(String answer)
	{
		boolean testing=false;
		if (answer!=null)
		{
			String[] orAnswers=answer.split(ANSWER_OR_SYMBOL);
			for (String orAnswer:orAnswers)
			{
				String[] andAnswers=orAnswer.split(ANSWER_AND_SYMBOL);
				boolean andTesting=true;
				for (String andAnswer:andAnswers)
				{
					boolean isNot=andAnswer.startsWith(ANSWER_NOT_SYMBOL);
					if (isNot)
					{
						andAnswer=andAnswer.substring(ANSWER_NOT_SYMBOL.length());
					}
					boolean isCorrect=getIsCorrect(andAnswer);
					if ((isNot && isCorrect) || (!isNot && !isCorrect))
					{
						andTesting=false;
						break;
					}
				}
				if (andTesting)
				{
					testing=true;
					break;
				}
			}
		}
		return testing;
	}
	
	@Override
	public boolean isAnswer(String s,AnswerTestProperties overrideProperties)
	{
		return isAnswer(s);
	}
	
	/**
	 * Returns  true if all the words within the sw tags have been selected 
	 * and no other words are selected, false otherwise.<br/><br/>
	 * @return true only if all the words within the sw tags have been selected 
	 * and no other words are selected 
	 */
	@Override
	public boolean isRight()
	{
		return getIsCorrect();
	}
	
	/**
	 * Clear all the selected wrong words.
	 */
	public void clearWrongSelection()
	{
		for (WordBlock wb:getWordBlocks())
		{
			if (!isWordBlockRightSW(wb))
			{
				clearSelection(wb.id);
			}
		}
	}
	
	/**
	 * Select all right words and unselect wrong ones.
	 */
	public void displayRightSelection()
	{
		for (WordBlock wb : getWordBlocks())
		{
			if (isWordBlockRightSW(wb))
			{
				for (Word w:wb.words)
				{
					w.selected=true;
				}
			}
			else
			{
				clearSelection(wb.id);
			}
		}
	}
	
	/**
	 * Clear all hightlighted words contained within all the sw tags
	 */
	public void clearHighlight()
	{
		for (WordBlock wb:getWordBlocks())
		{
			clearHighlight(wb.id);
		}
	}
	
	/**
	 * Clear all hightlighted words contained within an sw tag of the given id.
	 * @param swId the id of the block of words to clear highlight.
	 */
	public void clearHighlight(String swId)
	{
		WordBlock wb=getWordsById().get(swId);
		wb.isSecondHighlighted=false;
		for (Word w:wb.words)
		{
			w.isThirdHilight=false;
		}
	}
	
	@Override
	public void secondHilightSWWords()
	{
		for (WordBlock wb:getWordBlocks())
		{
			if (isWordBlockRightSW(wb))
			{
				secondHilightSWWords(wb.id);
			}
		}
	}
	
	@Override
	public void highlightCorrectSWWords()
	{
		for (WordBlock wb:getWordBlocks())
		{
			if (isWordBlockRightSW(wb))
			{
				highlightCorrectSWWords(wb.id);
			}
		}
	}
	
	/**
	 * Returns answer line.<br/>
	 * It is the display text from selected option or empty string if there is no selected option.
	 * @return Answer line
	 */
	@Override
	public String getAnswerLine()
	{
		StringBuffer answerline=new StringBuffer();
		boolean insertSeparator=false;
		for (WordBlock wb:getWordBlocks())
		{
			for (Word w:wb.words)
			{
				if (w.selected)
				{
					if (insertSeparator)
					{
						answerline.append(getAnswerWordSeparator());
					}
					else
					{
						insertSeparator=true;
					}
					answerline.append(w.word);
				}
			}
		}
		return answerline.toString(); 
	}
	
	
	/**
	 * Checks if wordblock is a right SW 
	 * @param wb
	 * @return true if wordblock is a right SW, false otherwise
	 */
	private boolean isWordBlockRightSW(WordBlock wb)
	{
		boolean right=wb.isSW;
		if (!right && replaceholderWordBlocks.containsKey(wb.id))
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			String rightValue=replaceholderWordBlocks.get(wb.id);
			if (sq.isPlaceholdersInitialized() && StandardQuestion.containsPlaceholder(rightValue))
			{
				rightValue=sq.applyPlaceholders(rightValue);
			}
			right=!rightValue.equals("no");
		}
		return right;
	}
}
