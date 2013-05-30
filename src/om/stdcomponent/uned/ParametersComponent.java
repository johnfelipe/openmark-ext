package om.stdcomponent.uned;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

// UNED: 21-06-2011 - dballestin
/**
 * This is a component to define some parameters about the question.<br/>
 * It can be declared only once inside question.xml and may not contain other content.<br/>
 * <h2>XML usage</h2>
 * &lt;parameters&gt;&lt;/parameters&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>maxAttempts</td><td>(int)</td><td>Specifies the question's max number of attempts allowed.</td></tr>
 * <tr><td>questionline</td><td>(boolean)</td><td>Specifies if it is going to be generated (yes) 
 * or not (no) a question line for summary.</td></tr>
 * <tr><td>answerline</td><td>(boolean)</td><td>Specifies if it is going to be generated (yes) 
 * or not (no) text for answers.</td></tr>
 * <tr><td>summaryline</td><td>(boolean)</td><td>Specifies if they are going to be generated 
 * (yes) or not (no) summary lines for summary</td></tr>
 * <tr><td>defaultquestionline</td><td>(boolean)</td><td>Specifies if it is going to be generated (yes) 
 * or not (no) a default question line for summary if no questionline property is present at 
 * "inputbox" box.</td></tr>
 * <tr><td>defaultanswerline</td><td>(boolean)</td><td>Specifies if it is going to be generated (yes) 
 * or not (no) a default text for answers if no answerline property is present at answer component 
 * or the default text "Passed" if the user passed the question and no answerline property 
 * is present at "answerbox" box.</td></tr>
 * <tr><td>defaultsummaryline</td><td>(boolean)</td><td>Specifies if they are going to be generated 
 * (yes) or not (no) default summary lines for summary if there are no &lt;summaryline&gt; 
 * components present</td></tr>
 * <tr><td>defaultanswer</td><td>(boolean)</td><td>Specifies default answer rightness.<br/><br/>
 * It can be "right", "wrong" or "default" (right answer will be right if there are no right 
 * &lt;answercombo&gt; components or wrong otherwise).</td></tr>
 * </table>
 */
public class ParametersComponent extends QComponent
{
	/** 
	 * Action's name for not sending question or answer line if it is longer than allowed 
	 * (&gt; 255 characters). 
	 */
	public static final String LONGLINE_VOID="void";
	
	/**
	 * Action's name for clipping question or answer line if it is longer than allowed 
	 * (&gt; 255 characters).<br/><br/>
	 * Clipping it is done in the 255th character even if it is in the middle of a word.
	 */
	public static final String LONGLINE_CLIP="clip";
	
	/**
	 * Action's name for clipping question or answer line if it is longer than allowed 
	 * (&gt; 255 characters).<br/><br/>
	 * Clipping it is done without cutting any word and appending string " ...".
	 */
	public static final String LONGLINE_CLIPWORD="clipword";
	
	/** 
	 * Action's name for not sending question or answer line if it is longer than allowed 
	 * (&gt; 255 characters).<br/><br/>
	 * Moreover, full question or answer line is send as a summary line.
	 */
	public static final String LONGLINE_VOIDTOSUMMARY="voidtosummary";
	
	/**
	 * Action's name for clipping question line or answer line if it is longer than allowed 
	 * (&gt; 255 characters).<br/><br/>
	 * Clipping it is done in the 255th character even if it is in the middle of a word.<br/><br/>
	 * Moreover, full question or answer line is send as a summary line.
	 */
	public static final String LONGLINE_CLIPTOSUMMARY="cliptosummary";
	
	/**
	 * Action's name for clipping question line or answer line if it is longer than allowed 
	 * (&gt; 255 characters).<br/><br/>
	 * Clipping it is done without cutting any word and appending string " ...".<br/><br/>
	 * Moreover, full question or answer line is send as a summary line.
	 */
	public static final String LONGLINE_CLIPWORDTOSUMMARY="clipwordtosummary";
	
	/**
	 * "defaultanswer" property's value indicating that default answer is right.
	 */
	public static final String DEFAULT_ANSWER_RIGHT="right";
	
	/**
	 * "defaultanswer" property's value indicating that default answer is wrong.
	 */
	public static final String DEFAULT_ANSWER_WRONG="wrong";
	
	/**
	 * "defaultanswer" property's value indicating that default answer is right 
	 * if there are no right &lt;answer&lt; components and wrong otherwise.
	 */
	public static final String DEFAULT_ANSWER_DEFAULT="default";
	
	/** Property name for the question's max number of attempts allowed (int) */
	public static final String PROPERTY_MAXATTEMPTS="maxattempts";
	
	/**
	 * Property name for generating (yes) or not (no) a question line for summary,
	 * by default yes (boolean).
	 */
	public static final String PROPERTY_QUESTIONLINE="questionline";
	
	/**
	 * Property name for generating (yes) or not (no) text for answers, by default yes (boolean).
	 * <br/>
	 * Moreover, if this property is yes and user pass the question it is generated an answer line
	 * with the text of answerline property at "answerbox" box.
	 */
	public static final String PROPERTY_ANSWERLINE="answerline";
	
	/**
	 * Property name for generating (yes) or not (no) summary lines for summary, 
	 * by default yes (boolean).
	 */
	public static final String PROPERTY_SUMMARYLINE="summaryline";
	
	/**
	 * Property name for generating (yes) or not (no) a default question line for summary
	 * if no questionline property is present at "inputbox" box, by default yes (boolean).
	 */
	public static final String PROPERTY_DEFAULTQUESTIONLINE="defaultquestionline";
	
	/**
	 * Property name for generating (yes) or not (no) a default answer text for answers
	 * if no answerline property is present at answer component, by default yes (boolean).<br/>
	 * Moreover, if this property is yes and user pass the question it is generated a default answer line
	 * with the text "Passed" if no answerline property is present at "answerbox" box.
	 */
	public static final String PROPERTY_DEFAULTANSWERLINE="defaultanswerline";
	
	/**
	 * Property name for generating (yes) or not (no) default summary lines for summary 
	 * if there are no &lt;summaryline&gt; components present, by default yes (boolean).
	 */
	public static final String PROPERTY_DEFAULTSUMMARYLINE="defaultsummaryline";
	
	/**
	 * Property name for the action's name to execute if the question line generated is longer than
	 * allowed (&gt; 255 characters), by default "clipword" (string)
	 * 
	 */
	public static final String PROPERTY_LONGQUESTIONLINE="longquestionline";
	
	/**
	 * Property name for the action's name to execute if the question line generated is longer than
	 * allowed (&gt; 255 characters), by default "clipword" (string)
	 * 
	 */
	public static final String PROPERTY_LONGANSWERLINE="longanswerline";
	
	/**
	 * Property name for rightness of default answer, by default "default" (string).<br/><br/>
	 * It can be "right", "wrong" or "default" (default answer will be right if 
	 * there are no right &lt;answercombo&gt; components or wrong otherwise).
	 */
	public static final String PROPERTY_DEFAULTANSWER="defaultanswer";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_DEFAULTANSWER
	};
	
	/** Regular expression restricting 'defaultanswer' property value */
	private static final String PROPERTYRESTRICTION_DEFAULTANSWER="right|wrong|default";
	
	/** Map with restrictions of properties that need to initialize placeholders */
	private static final Map<String,String> PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS;
	static
	{
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS=new HashMap<String,String>();
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_LANG,PROPERTYRESTRICTION_LANG);
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(
				PROPERTY_DEFAULTANSWER,PROPERTYRESTRICTION_DEFAULTANSWER);
	}
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** Specific properties checks */
	private Map<String,PropertyCheck> checks=new HashMap<String,PropertyCheck>();
	
	/** Standard question, needed if component is not present in question.xml */
	protected StandardQuestion sq;
	
	/** Default constructor */
	public ParametersComponent()
	{
		this.sq=null;
	}
	
	/** Constructor used at om.stdquestion.uned.StandardQuestion to create a default
	 * parameter component
	 * @param sq Standard question
	 */
	public ParametersComponent(StandardQuestion sq)
	{
		this.sq=sq;
	}
	
	/** @return Tag name (introspected; this may be replaced by a 1.5 annotation) */
	public static String getTagName()
	{
		return "parameters";
	}
	
	@Override
	public void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineInteger(PROPERTY_MAXATTEMPTS);
		setInteger(PROPERTY_MAXATTEMPTS, 3);
		
		defineBoolean(PROPERTY_QUESTIONLINE);
		setBoolean(PROPERTY_QUESTIONLINE,true);
		
		defineBoolean(PROPERTY_ANSWERLINE);
		setBoolean(PROPERTY_ANSWERLINE,true);
		
		defineBoolean(PROPERTY_SUMMARYLINE);
		setBoolean(PROPERTY_SUMMARYLINE,true);
		
		defineBoolean(PROPERTY_DEFAULTQUESTIONLINE);
		setBoolean(PROPERTY_DEFAULTQUESTIONLINE,true);
		
		defineBoolean(PROPERTY_DEFAULTANSWERLINE);
		setBoolean(PROPERTY_DEFAULTANSWERLINE,true);
		
		defineBoolean(PROPERTY_DEFAULTSUMMARYLINE);
		setBoolean(PROPERTY_DEFAULTSUMMARYLINE,true);
		
		defineString(PROPERTY_LONGQUESTIONLINE);
		setString(PROPERTY_LONGQUESTIONLINE,LONGLINE_CLIPWORD);
		
		defineString(PROPERTY_LONGANSWERLINE);
		setString(PROPERTY_LONGANSWERLINE,LONGLINE_CLIPWORD);
		
		defineString(PROPERTY_DEFAULTANSWER,PROPERTYRESTRICTION_DEFAULTANSWER);
		setString(PROPERTY_DEFAULTANSWER,DEFAULT_ANSWER_DEFAULT);
	}
	
	/** @return Question's max number of attempts allowed */
	public int getMaxAttempts()
	{
		try
		{
			return getInteger(PROPERTY_MAXATTEMPTS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the question's max number of attempts allowed
	 * @param maxAttempts Question's max number of attempts allowed
	 */
	public void setMaxAttempts(int maxAttempts)
	{
		try
		{
			setInteger(PROPERTY_MAXATTEMPTS,maxAttempts);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return true if it is generated a question line for summary, false otherwise */
	public boolean isQuestionLine()
	{
		try
		{
			return getBoolean(PROPERTY_QUESTIONLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if it is going to be generated (true) or not (false) a question line for summary.
	 * @param questionLine true to indicate that it is going to be generated a question line for 
	 * summary, false if it is not going to be generated
	 */
	public void setQuestionLine(boolean questionLine)
	{
		try
		{
			setBoolean(PROPERTY_QUESTIONLINE,questionLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return true if it is generated an answer text for answers, false otherwise */
	public boolean isAnswerLine()
	{
		try
		{
			return getBoolean(PROPERTY_ANSWERLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if it is going to be generated (true) or not (false) a text for answers.
	 * @param answerLine true to indicate that it is going to be generated a text for answers, 
	 * false if it is not going to be generated
	 */
	public void setAnswerLine(boolean answerLine)
	{
		try
		{
			setBoolean(PROPERTY_ANSWERLINE,answerLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return true if they are going to be generated summary lines for summary, false otherwise */
	public boolean isSummaryLine()
	{
		try
		{
			return getBoolean(PROPERTY_SUMMARYLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if they are going to be generated (true) or not (false) summary lines for summary.
	 * @param summaryLine true to indicate that they are going to be generated summary lines 
	 * for summary, false if they are not going to be generated
	 */
	public void setSummaryLine(boolean summaryLine)
	{
		try
		{
			setBoolean(PROPERTY_SUMMARYLINE,summaryLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return true if it is going to be generated a default question line for summary 
	 * if no questionline property is present at "inputbox" box, false otherwise
	 */
	public boolean isDefaultQuestionLine()
	{
		try
		{
			return getBoolean(PROPERTY_DEFAULTQUESTIONLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if it is going to be generated (true) or not (false) a default question line for summary
	 * if no questionline property is present at "inputbox" box.
	 * @param defaultQuestionLine true to indicate that it is going to be generated a default 
	 * question line for summary if no questionline property is present at "inputbox" box, 
	 * false if it is not going to be generated
	 */
	public void setDefaultQuestionLine(boolean defaultQuestionLine)
	{
		try
		{
			setBoolean(PROPERTY_DEFAULTQUESTIONLINE,defaultQuestionLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return true if it is going to be generated a default text for answers if no answerline property 
	 * is present at answer component or the default text "Passed" if the user passed the question and
	 * no answerline property is present at "answerbox" box, false otherwise
	 */
	public boolean isDefaultAnswerLine()
	{
		try
		{
			return getBoolean(PROPERTY_DEFAULTANSWERLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if it is going to be generated (true) or not (false) a default text for answers
	 * if no answerline property is present at answer component or the default text "Passed"
	 * if the user passed the question and no answerline property is present at "answerbox" box.
	 * @param defaultAnswerLine true to indicate that it is going to be generated a default text 
	 * for answers if no answerline property is present at answer components, 
	 * false if it is not going to be generated
	 */
	public void setDefaultAnswerLine(boolean defaultAnswerLine)
	{
		try
		{
			setBoolean(PROPERTY_DEFAULTANSWERLINE,defaultAnswerLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return true if they are going to be generated default summary lines if there are 
	 * no &lt;summaryline&gt; components present, false otherwise
	 */
	public boolean isDefaultSummaryLine()
	{
		try
		{
			return getBoolean(PROPERTY_DEFAULTSUMMARYLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if they are going to be generated (true) or not (false) default summary lines for summary
	 * if there are no &lt;summaryline&gt; components present.
	 * @param defaultSummaryLine true to indicate that they are going to be generated 
	 * default summary lines for summary if there are no &lt;summaryline&gt; components present, 
	 * false if they are not going to be generated
	 */
	public void setDefaultSummaryLine(boolean defaultSummaryLine)
	{
		try
		{
			setBoolean(PROPERTY_DEFAULTSUMMARYLINE,defaultSummaryLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Action's name to execute if the question line generated is longer than allowed 
	 * (&gt; 255 characters)
	 */
	public String getLongQuestionLine()
	{
		try
		{
			return getString(PROPERTY_LONGQUESTIONLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set action's name to execute if the question line generated is longer than allowed 
	 * (&gt; 255 characters)
	 * @param longQuestionLine Action's name to execute if the question line generated is longer 
	 * than allowed (&gt; 255 characters)
	 */
	public void setLongQuestionLine(String longQuestionLine)
	{
		try
		{
			setString(PROPERTY_LONGQUESTIONLINE,longQuestionLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Action's name to execute if the answer line generated is longer than allowed 
	 * (&gt; 255 characters)
	 */
	public String getLongAnswerLine()
	{
		try
		{
			return getString(PROPERTY_LONGANSWERLINE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set action's name to execute if the answer line generated is longer than allowed 
	 * (&gt; 255 characters)
	 * @param longAnswerLine Action's name to execute if the answer line generated is longer 
	 * than allowed (&gt; 255 characters)
	 */
	public void setLongAnswerLine(String longAnswerLine)
	{
		try
		{
			setString(PROPERTY_LONGANSWERLINE,longAnswerLine);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Rightness of default answer
	 */
	public String getDefaultAnswer()
	{
		try
		{
			return getString(PROPERTY_DEFAULTANSWER);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set rightness of default answer.<br/><br/>
	 * It can be "right", "wrong" or "default" (default answer will be right if there are no right 
	 * &lt;answercombo&gt; components or wrong otherwise).
	 * @param defaultAnswer Rightness of default answer
	 */
	public void setDefaultAnswer(String defaultAnswer)
	{
		try
		{
			setString(PROPERTY_DEFAULTANSWER,defaultAnswer);
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
		return new String[] {};
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
	 * Default implementation of this method check that this is the unique <parameters> component declared.
	 * <br/><br/>
	 * If you override this method and want this default functionality still to be performed you need to include 
	 * 'super.initializeSpecific(eThis)' call inside your implementation.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	protected void initializeSpecific(Element eThis) throws OmException
	{
		// We need to check that this is the unique <parameters> component declared
		if (getQDocument().find(ParametersComponent.class).size()>1)
		{
			throw new OmFormatException("<parameters> can be declared only once inside question.xml");
		}
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
		if(eThis.getFirstChild()!=null)
		{
			throw new OmFormatException("<parameters> may not include children");
		}
	}
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
	}

	@Override
	protected om.stdquestion.StandardQuestion getQuestion()
	{
		if (this.sq==null)
		{
			this.sq=(StandardQuestion)super.getQuestion();
		}
		return this.sq;
	}
}
