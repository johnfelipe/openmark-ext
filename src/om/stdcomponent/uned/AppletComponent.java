package om.stdcomponent.uned;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.uned.Tester;
import om.question.ActionParams;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

// UNED: 29-07-2011 - dballestin
/**
 * Inserts a pushbutton that users can press to open the an applet specified in the &lt;applet&gt; tag
 * component. A popup window will appear, containing the applet itself plus
 * 'OK' button.
 * <p>
 * If the user clicks 'OK', the string returned by the function answer() in the applet is updated and
 * (if there is an action set) the form will be submitted.
 * <p>
 * The popup remains visible during one question. If the user begins a new
 * question (or restarts the same one) it will vanish. It will also vanish if they
 * navigate to another website.
 * <p>
 * Tests can be defined with properties. They include answer's component id (answerid), 
 * minimum number of attempts needed (attemptsmin), maximum number of attempts allowed (attemptsmax)
 * and some other tests defined with test property:<br/><br/>
 * <h2>Tests</h2>
 * <table border="1">
 * <tr><th>Test</th><th>Description</th></tr>
 * <tr><td>true</td><td><b>(default)</b> Always pass the test</td></tr>
 * <tr><td>right</td><td>User selected right answer</td></tr>
 * <tr><td>notright</td><td>User not selected right answer (selected wrong answer or passed)</td></tr>
 * <tr><td>wrong</td><td>User selected wrong answer</td></tr>
 * <tr><td>notwrong</td><td>User not selected wrong answer (selected right answer or passed)</td></tr>
 * <tr><td>passed</td><td>User passed the question</td></tr>
 * <tr><td>notpassed</td><td>User not passed the question (selected right or wrong answer)</td></tr>
 * <tr><td>random</td><td>Allows testing value of a random component</td></tr>
 * <tr><td>numcmp</td><td>Allows testing numeric comparisons</td></tr>
 * </table>
 * <br/>
 * <h2>XML usage</h2>
 * &lt;applet id='myapp' action='actionSubmit'/&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates all children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>label</td><td>(string)</td><td>Label for button (default is 'Run Applet')</td></tr>
 * <tr><td>action</td><td>(string)</td><td>Name of method in question class that
 * is called after user clicks OK. Optional, default is to not submit form, but
 * you probably want to do it.</td></tr>
 * <tr><td>filePath</td><td>(string)</td><td>Path of applet jar file. 
 * (may be relative path including /; may not be absolute path beginning /)
 * Applet jar should be packaged with question jar.</td></tr>
 * <tr><td>className</td><td>(string)</td><td>Name of main applet class.</td></tr>
 * <tr><td>params</td><td>(string)</td><td>Parameter string for the applet. eg. params="param1=xxx,param2=yyy".
 * "params" is a predefined parameter in applet component, use getParameter("params") in your applet 
 * to get the parameter string and split that into individual paramaters.</td></tr>
 * <tr><td>width</td><td>(string)</td><td>Width of the applet window.</td></tr>
 * <tr><td>height</td><td>(string)</td><td>Height of the applet window.</td></tr>
 * <tr><td>custom</td><td>(string)</td><td>Selector used with custom action</td></tr>
 * <tr><td>test</td><td>(string)</td><td>Specifies the test for display testing (see <b>Tests</b>).
 * </td></tr>
 * <tr><td>answer</td><td>(string)</td><td>Specifies the answer's components for display testing.
 * <br/><br/> 
 * This is a list with answer's component ids separated by comma ',' (OR operator) or plus symbol '+' 
 * (AND operator).<br/><br/>
 * Moreover answer's component ids can include a selector using with the format
 * "id[selector]" used in some answer's components (e.g. editfields) to specify a single answer value
 * valid for them."</td></tr>
 * <tr><td>attemptsmin</td><td>(int)</td><td>Specifies the minimum number of attempts for display 
 * testing</td></tr>
 * <tr><td>attemptsmax</td><td>(int)</td><td>Specifies the maximum number of attempts for display 
 * testing</td></tr>
 * <tr><td>selectedanswersmin</td><td>(int)</td><td>Specifies the minimum number of selected answers 
 * for display testing</td></tr>
 * <tr><td>selectedanswersmax</td><td>(int)</td><td>Specifies the maximum number of selected answers 
 * for display testing</td></tr>
 * <tr><td>selectedrightanswersmin</td><td>(int)</td><td>Specifies the minimum number of selected right 
 * answers for display testing</td></tr>
 * <tr><td>selectedrightanswersmax</td><td>(int)</td><td>Specifies the maximum number of selected right 
 * answers for display testing</td></tr>
 * <tr><td>selectedwronganswersmin</td><td>(int)</td><td>Specifies the minimum number of selected wrong 
 * answers for display testing</td></tr>
 * <tr><td>selectedwronganswersmax</td><td>(int)</td><td>Specifies the maximum number of selected wrong 
 * answers for display testing</td></tr>
 * <tr><td>unselectedanswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * answers for display testing</td></tr>
 * <tr><td>unselectedanswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * answers for display testing</td></tr>
 * <tr><td>unselectedrightanswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * right answers for display testing</td></tr>
 * <tr><td>unselectedrightanswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * right answers for display testing</td></tr>
 * <tr><td>unselectedwronganswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * wrong answers for display testing</td></tr>
 * <tr><td>unselectedwronganswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * wrong answers for display testing</td></tr>
 * <tr><td>rightdistancemin</td><td>(int)</td><td>Specifies the minimum distance to the right answer 
 * for display testing</td></tr>
 * <tr><td>rightdistancemax</td><td>(int)</td><td>Specifies the maximum distance to the right answer 
 * for display testing</td></tr>
 * <tr><td>answertype</td><td>(string)</td><td>Specifies the answer's type for display testing with 
 * editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the answertype property from the answer's component
 * </td></tr>
 * <tr><td>casesensitive</td><td>(string)</td><td>Specifies if the answer is considered to be 
 * case sensitive (yes) or case insensitive (no) for display testing with editfield or advancedfield 
 * components.<br/>
 * If it is null (default value) then it is used the casesensitive property from the answer's component
 * </td></tr>
 * <tr><td>trim</td><td>(string)</td><td>Specifies if starting and ending whitespaces from answer are
 * ignored for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the trim property from the answer's component</td></tr>
 * <tr><td>strip</td><td>(string)</td><td>Specifies if all whitespaces from answer are ignored 
 * for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the strip property from the answer's component</td></tr>
 * <tr><td>singlespaces</td><td>(string)</td><td>Specifies if together whitespaces from answer 
 * are considered as a single whitespace for display testing with editfield or advancedfield 
 * components.<br/>
 * If it is null (default value) then it is used the singlespaces property from the answer's component
 * </td></tr>
 * <tr><td>newlinespace</td><td>(string)</td><td>Specifies if new line characters are considered 
 * as whitespaces for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the newlinespace property from the answer's component. 
 * <br/><br/>
 * Note that trim, strip and singlespaces properties are affected if this property is set to yes.
 * </td></tr>
 * <tr><td>ignore</td><td>(string)</td><td>Specifies text ocurrences (separated by commas) 
 * to ignore from answer for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the ignore property from the answer's component
 * </td></tr>
 * <tr><td>ignoreregexp</td><td>(string)</td><td>Specifies a regular expression to ignore text 
 * ocurrences matching it.<br/>
 * If it is null (default value) then it is used the ignoreregexp property from the answer's component
 * </td></tr>
 * <tr><td>ignoreemptylines</td><td>(string)</td><td>Specifies if empty lines from answer are ignored 
 * for display testing with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the ignoreemptylines property from the 
 * answer's component</td></tr>
 * <tr><td>tolerance</td><td>(string)</td><td>Specifies error tolerance used in numeric comparisons,
 * by default null</td></tr>
 * </table>
 *
 */
public class AppletComponent extends om.stdcomponent.AppletComponent implements Testable
{
	/** Property name for selector used with custom action (string) */
	public static final String PROPERTY_CUSTOM="custom";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_TEST,PROPERTY_ANSWER,PROPERTY_ATTEMPTSMIN,
		PROPERTY_ATTEMPTSMAX,PROPERTY_SELECTEDANSWERSMIN,PROPERTY_SELECTEDANSWERSMAX,
		PROPERTY_SELECTEDRIGHTANSWERSMIN,PROPERTY_SELECTEDRIGHTANSWERSMAX,PROPERTY_SELECTEDWRONGANSWERSMIN,
		PROPERTY_SELECTEDWRONGANSWERSMAX,PROPERTY_UNSELECTEDANSWERSMIN,PROPERTY_UNSELECTEDANSWERSMAX,
		PROPERTY_UNSELECTEDRIGHTANSWERSMIN,PROPERTY_UNSELECTEDRIGHTANSWERSMAX,
		PROPERTY_UNSELECTEDWRONGANSWERSMIN,PROPERTY_UNSELECTEDWRONGANSWERSMAX,PROPERTY_RIGHTDISTANCEMIN,
		PROPERTY_RIGHTDISTANCEMAX,PROPERTY_ANSWERTYPE,PROPERTY_CASESENSITIVE,PROPERTY_TRIM,PROPERTY_STRIP,
		PROPERTY_SINGLESPACES,PROPERTY_NEWLINESPACE,PROPERTY_IGNORE,PROPERTY_IGNOREREGEXP,
		PROPERTY_IGNOREEMPTYLINES,PROPERTY_TOLERANCE,PROPERTY_LABEL,PROPERTY_ACTION,PROPERTY_FILEPATH,
		PROPERTY_CLASSNAME,PROPERTY_PARAMS,PROPERTY_WIDTH,PROPERTY_HEIGHT,PROPERTY_CUSTOM
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
		
		defineString(PROPERTY_CUSTOM);
		setString(PROPERTY_CUSTOM,"");
		
		defineString(PROPERTY_TEST);
		setString(PROPERTY_TEST,Tester.TEST_TRUE);
		
		defineString(PROPERTY_ANSWER);
		setString(PROPERTY_ANSWER,null);
		
		defineInteger(PROPERTY_ATTEMPTSMIN);
		setInteger(PROPERTY_ATTEMPTSMIN,0);
		
		defineInteger(PROPERTY_ATTEMPTSMAX);
		setInteger(PROPERTY_ATTEMPTSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_SELECTEDANSWERSMIN);
		setInteger(PROPERTY_SELECTEDANSWERSMIN,0);
		
		defineInteger(PROPERTY_SELECTEDANSWERSMAX);
		setInteger(PROPERTY_SELECTEDANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN);
		setInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN,0);
		
		defineInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX);
		setInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_SELECTEDWRONGANSWERSMIN);
		setInteger(PROPERTY_SELECTEDWRONGANSWERSMIN,0);
		
		defineInteger(PROPERTY_SELECTEDWRONGANSWERSMAX);
		setInteger(PROPERTY_SELECTEDWRONGANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_UNSELECTEDANSWERSMIN);
		setInteger(PROPERTY_UNSELECTEDANSWERSMIN,0);
		
		defineInteger(PROPERTY_UNSELECTEDANSWERSMAX);
		setInteger(PROPERTY_UNSELECTEDANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN);
		setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN,0);
		
		defineInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX);
		setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN);
		setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN,0);
		
		defineInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX);
		setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX,Integer.MAX_VALUE);
		
		defineInteger(PROPERTY_RIGHTDISTANCEMIN);
		setInteger(PROPERTY_RIGHTDISTANCEMIN,0);
		
		defineInteger(PROPERTY_RIGHTDISTANCEMAX);
		setInteger(PROPERTY_RIGHTDISTANCEMAX,Integer.MAX_VALUE);
		
		defineString(PROPERTY_ANSWERTYPE);
		setString(PROPERTY_ANSWERTYPE,null);
		
		defineString(PROPERTY_CASESENSITIVE);
		setString(PROPERTY_CASESENSITIVE,null);
		
		defineString(PROPERTY_TRIM);
		setString(PROPERTY_TRIM,null);
		
		defineString(PROPERTY_STRIP);
		setString(PROPERTY_STRIP,null);
		
		defineString(PROPERTY_SINGLESPACES);
		setString(PROPERTY_SINGLESPACES,null);
		
		defineString(PROPERTY_NEWLINESPACE);
		setString(PROPERTY_NEWLINESPACE,null);
		
		defineString(PROPERTY_IGNORE);
		setString(PROPERTY_IGNORE,null);
		
		defineString(PROPERTY_IGNOREREGEXP);
		setString(PROPERTY_IGNOREREGEXP,null);
		
		defineString(PROPERTY_IGNOREEMPTYLINES);
		setString(PROPERTY_IGNOREEMPTYLINES,null);
		
		defineString(PROPERTY_TOLERANCE);
		setString(PROPERTY_TOLERANCE,null);
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
	
	/** @return Test for display testing */
	@Override
	public String getTest()
	{
		try
		{
			return getString(PROPERTY_TEST);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the test for display testing.
	 * @param test Test for display testing
	 */
	@Override
	public void setTest(String test)
	{
		try
		{
			setString(PROPERTY_TEST,test);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Answer's components for display testing separated by comma ',' 
	 * (OR operator) or plus symbol '+' (AND operator)
	 */
	@Override
	public String getAnswer()
	{
		try
		{
			return getString(PROPERTY_ANSWER);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the answer's components for display testing separated by comma ',' (OR operator) 
	 * or plus symbol '+' (AND operator), set it to null if you want to remove definition.
	 * @param answer Answer's components for display testing
	 */
	@Override
	public void setAnswer(String answer)
	{
		try
		{
			setString(PROPERTY_ANSWER,answer);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of attempts for display testing */
	@Override
	public int getAttemptsMin()
	{
		try
		{
			return getInteger(PROPERTY_ATTEMPTSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of attempts for display testing.
	 * @param attemptsMin Minimum number of attempts for display testing
	 */
	@Override
	public void setAttemptsMin(int attemptsMin)
	{
		try
		{
			setInteger(PROPERTY_ATTEMPTSMIN,attemptsMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of attempts for display testing */
	@Override
	public int getAttemptsMax()
	{
		try
		{
			return getInteger(PROPERTY_ATTEMPTSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of attempts for display testing.
	 * @param attemptsMax Maximum number of attempts for display testing
	 */
	@Override
	public void setAttemptsMax(int attemptsMax)
	{
		try
		{
			setInteger(PROPERTY_ATTEMPTSMAX,attemptsMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of selected answers for display testing */
	@Override
	public int getSelectedAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of selected answers for display testing.
	 * @param selectedAnswersMin Minimum number of selected answers for display testing 
	 */
	@Override
	public void setSelectedAnswersMin(int selectedAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDANSWERSMIN,selectedAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of selected answers for display testing */
	@Override
	public int getSelectedAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of selected answers for display testing.
	 * @param selectedAnswersMax Maximum number of selected answers for display testing 
	 */
	@Override
	public void setSelectedAnswersMax(int selectedAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDANSWERSMAX,selectedAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of selected right answers for display testing */
	@Override
	public int getSelectedRightAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of selected right answers for display testing.
	 * @param selectedRightAnswersMin Minimum number of selected right answers for display testing 
	 */
	@Override
	public void setSelectedRightAnswersMin(int selectedRightAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDRIGHTANSWERSMIN,selectedRightAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of selected right answers for display testing */
	@Override
	public int getSelectedRightAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of selected right answers for display testing.
	 * @param selectedRightAnswersMax Maximum number of selected right answers for display testing 
	 */
	@Override
	public void setSelectedRightAnswersMax(int selectedRightAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDRIGHTANSWERSMAX,selectedRightAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of selected wrong answers for display testing */
	@Override
	public int getSelectedWrongAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDWRONGANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of selected wrong answers for display testing.
	 * @param selectedWrongAnswersMin Minimum number of selected wrong answers for display testing 
	 */
	@Override
	public void setSelectedWrongtAnswersMin(int selectedWrongAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDWRONGANSWERSMIN,selectedWrongAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of selected wrong answers for display testing */
	@Override
	public int getSelectedWrongAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_SELECTEDWRONGANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of selected wrong answers for display testing.
	 * @param selectedWrongAnswersMax Maximum number of selected wrong answers for display testing 
	 */
	@Override
	public void setSelectedWrongAnswersMax(int selectedWrongAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_SELECTEDWRONGANSWERSMAX,selectedWrongAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of unselected answers for display testing */
	@Override
	public int getUnselectedAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of unselected answers for display testing.
	 * @param unselectedAnswersMin Minimum number of unselected answers for display testing 
	 */
	@Override
	public void setUnselectedAnswersMin(int unselectedAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDANSWERSMIN,unselectedAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of unselected answers for display testing */
	@Override
	public int getUnselectedAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of unselected answers for display testing.
	 * @param unselectedAnswersMax Maximum number of unselected answers for display testing 
	 */
	@Override
	public void setUnselectedAnswersMax(int unselectedAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDANSWERSMAX,unselectedAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of unselected right answers for display testing */
	@Override
	public int getUnselectedRightAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of unselected right answers for display testing.
	 * @param unselectedRightAnswersMin Minimum number of selected right answers for display testing 
	 */
	@Override
	public void setUnselectedRightAnswersMin(int unselectedRightAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMIN,unselectedRightAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of unselected right answers for display testing */
	@Override
	public int getUnselectedRightAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of unselected right answers for display testing.
	 * @param unselectedRightAnswersMax Maximum number of unselected right answers for display testing 
	 */
	@Override
	public void setUnselectedRightAnswersMax(int unselectedRightAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDRIGHTANSWERSMAX,unselectedRightAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum number of unselected wrong answers for display testing */
	@Override
	public int getUnselectedWrongAnswersMin()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum number of unselected wrong answers for display testing.
	 * @param unselectedWrongAnswersMin Minimum number of unselected wrong answers for display testing 
	 */
	@Override
	public void setUnselectedWrongtAnswersMin(int unselectedWrongAnswersMin)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMIN,unselectedWrongAnswersMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum number of unselected wrong answers for display testing */
	@Override
	public int getUnselectedWrongAnswersMax()
	{
		try
		{
			return getInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum number of unselected wrong answers for display testing.
	 * @param unselectedWrongAnswersMax Maximum number of unselected wrong answers for display testing 
	 */
	@Override
	public void setUnselectedWrongAnswersMax(int unselectedWrongAnswersMax)
	{
		try
		{
			setInteger(PROPERTY_UNSELECTEDWRONGANSWERSMAX,unselectedWrongAnswersMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Minimum distance to the right answer for display testing */
	@Override
	public int getRightDistanceMin()
	{
		try
		{
			return getInteger(PROPERTY_RIGHTDISTANCEMIN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the minimum distance to the right answer for display testing.
	 * @param rightDistanceMin Minimum distance to the right answer for display testing 
	 */
	@Override
	public void setRightDistanceMin(int rightDistanceMin)
	{
		try
		{
			setInteger(PROPERTY_RIGHTDISTANCEMIN,rightDistanceMin);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Maximum distance to the right answer for display testing */
	@Override
	public int getRightDistanceMax()
	{
		try
		{
			return getInteger(PROPERTY_RIGHTDISTANCEMAX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the maximum distance to the right answer for display testing.
	 * @param rightDistanceMax Maximum distance to the right answer for display testing 
	 */
	@Override
	public void setRightDistanceMax(int rightDistanceMax)
	{
		try
		{
			setInteger(PROPERTY_RIGHTDISTANCEMAX,rightDistanceMax);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Answer's type for display testing with editfield or advancedfield 
	 * components.<br/>
	 * If it is null (default value) then it is used the answertype property from the 
	 * answer's component
	 */
	@Override
	public String getAnswerType()
	{
		try
		{
			return getString(PROPERTY_ANSWERTYPE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the answer's type for display testing with editfield or advancedfield components.<br/>
	 * If it is set to null (default value) then it will be used the answertype property from the 
	 * answer's component.
	 * @param answertype Answer's type for display testing or null to use the answertype property from 
	 * the answer's component
	 */
	@Override
	public void setAnswerType(String answertype)
	{
		try
		{
			setString(PROPERTY_ANSWERTYPE,answertype);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if considering the answer to be case sensitive for display testing with editfield 
	 * or advancedfield components, "no" if it is considered to be case insensitive 
	 * or null if it is used the answertype property from the answer's component
	 */
	@Override
	public String getCaseSensitive()
	{
		try
		{
			return getString(PROPERTY_CASESENSITIVE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if the answer is going to be considered case sensitive (true) or case insensitive (false)
	 * for display testing with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the casesensitive property from the 
	 * answer's component (string)
	 * @param casesensitive "yes" if the answer is going to be considered case sensitive 
	 * for display testing with editfield or advancedfield components, "no" if it is going 
	 * to be considered case insensitive or null to use the answertype property from 
	 * the answer's component
	 */
	@Override
	public void setCaseSensitive(String casesensitive)
	{
		try
		{
			setString(PROPERTY_CASESENSITIVE,casesensitive);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if starting and ending whitespaces from answer are ignored for display testing 
	 * with editfield or advancedfield components, "no" if they are not ignored 
	 * or null if it is used the trim property from the answer's component
	 */
	@Override
	public String getTrim()
	{
		try
		{
			return getString(PROPERTY_TRIM);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if starting and ending whitespaces from answer are ignored for display testing 
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the trim property from the answer's component 
	 * (string)
	 * @param trim "yes" if starting and ending whitespaces from answer are going to be ignored 
	 * for display testing with editfield or advancedfield components, 
	 * "no" if they are not going to be ignored or null to use the trim property from the 
	 * answer's component
	 */
	@Override
	public void setTrim(String trim)
	{
		try
		{
			setString(PROPERTY_TRIM,trim);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if all whitespaces from answer are ignored for display testing with 
	 * editfield or advancedfield components, "no" if they are not ignored 
	 * or null if it is used the strip property from the answer's component
	 */
	@Override
	public String getStrip()
	{
		try
		{
			return getString(PROPERTY_STRIP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if all whitespaces from answer are ignored for display testing with editfield or 
	 * advancedfield components.<br/>
	 * If it is null (default value) then it is used the strip property from the answer's component 
	 * (string)
	 * @param strip "yes" if all whitespaces from answer are going to be ignored for display testing 
	 * with editfield or advancedfield components, "no" if they are not going to be ignored 
	 * or null to use the strip property from the answer's component
	 */
	@Override
	public void setStrip(String strip)
	{
		try
		{
			setString(PROPERTY_STRIP,strip);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if together whitespaces from answer are considered as single whitespaces 
	 * for display testing with editfield or advancedfield components, 
	 * "no" if they are not considered as single whitespaces or null if it is used 
	 * the singlespaces property from the answer's component
	 */
	@Override
	public String getSingleSpaces()
	{
		try
		{
			return getString(PROPERTY_SINGLESPACES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if together whitespaces from answer are considered as single whitespaces 
	 * for display testing with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the singlespaces property from the 
	 * answer's component (string)
	 * @param singlespaces "yes" if together whitespaces from answer are going to be considered as 
	 * single whitespaces for display testing with editfield or advancedfield components, 
	 * "no" if they are not going to be considered as single whitespaces or null to use 
	 * the singlespaces property from the answer's component
	 */
	@Override
	public void setSingleSpaces(String singlespaces)
	{
		try
		{
			setString(PROPERTY_SINGLESPACES,singlespaces);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if new line characters from answer are considered as whitespaces 
	 * for display testing with editfield components, "no" if they are not considered as whitespaces 
	 * or null if it is used the newlinespace property from the answer's component
	 */
	@Override
	public String getNewLineSpace()
	{
		try
		{
			return getString(PROPERTY_NEWLINESPACE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if new line characters from answer are considered as whitespaces for display testing 
	 * with editfield components.<br/>
	 * If it is null (default value) then it is used the newlinespace property from the 
	 * answer's component (string)
	 * @param newlinespace "yes" if new line characters from answer are going to be considered as 
	 * whitespaces for display testing with editfield components, "no" if they are not going 
	 * to be considered as whitespaces or null to use the newlinespace property from 
	 * the answer's component
	 */
	@Override
	public void setNewLineSpace(String newlinespace)
	{
		try
		{
			setString(PROPERTY_NEWLINESPACE,newlinespace);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Text ocurrences (separated by commas) to ignore from answer for answer checking 
	 * with editfield or advancedfield components
	 */
	@Override
	public String getIgnore()
	{
		try
		{
			return getString(PROPERTY_IGNORE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set text ocurrences (separated by commas) to ignore from answer for answer checking
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the ignore property from the 
	 * answer's component (string)
	 * @param ignore Text ocurrences (separated by commas) to ignore from answer
	 */
	@Override
	public void setIgnore(String ignore)
	{
		try
		{
			setString(PROPERTY_IGNORE,ignore);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Regular expression to ignore matched text ocurrences from answer for answer checking 
	 * with editfield or advancedfield components
	 */
	@Override
	public String getIgnoreRegExp()
	{
		try
		{
			return getString(PROPERTY_IGNOREREGEXP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set regular expression to ignore matched text ocurrences from answer for answer checking
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the ignoreregexp property from the 
	 * answer's component (string)
	 * @param ignoreRegExp Regular expression to ignore matched text ocurrences from answer
	 */
	@Override
	public void setIgnoreRegExp(String ignoreRegExp)
	{
		try
		{
			setString(PROPERTY_IGNOREREGEXP,ignoreRegExp);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return "yes" if empty lines from answer are ignored for display testing with 
	 * editfield components, "no" if they are not ignored or null if it is used 
	 * the ignoreemptylines property from the answer's component
	 */
	@Override
	public String getIgnoreEmptyLines()
	{
		try
		{
			return getString(PROPERTY_IGNOREEMPTYLINES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set if empty lines characters from answer are ignored for display testing with 
	 * editfield components.<br/>
	 * If it is null (default value) then it is used the ignoreemptylines property from the 
	 * answer's component (string)
	 * @param ignoreemptylines "yes" if empty lines from answer are ignored for display testing 
	 * with editfield components, "no" if they are not going to be ignored 
	 * or null to use the ignoreemptylines property from the answer's component
	 */
	@Override
	public void setIgnoreEmptyLines(String ignoreemptylines)
	{
		try
		{
			setString(PROPERTY_IGNOREEMPTYLINES,ignoreemptylines);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public String getTolerance()
	{
		try
		{
			return getString(PROPERTY_TOLERANCE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public void setTolerance(String tolerance)
	{
		try
		{
			setString(PROPERTY_TOLERANCE,tolerance);
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
		return super.getRequiredAttributes();
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
		// Generate random token used to check when user goes to different window
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
	protected void formCallAction(String sValue,ActionParams ap) throws OmException
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		sq.callback(getString(PROPERTY_ACTION),getCustom());
	}
	
	@Override
	public void produceOutput(QContent qc, boolean bInit, boolean bPlain) throws OmException
	{
		boolean isVisible=isDisplayed();
		if (isVisible)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			boolean questionTimeTesting=!sq.isQuestionTimeTestingException(this) && 
					!sq.isQuestionTimeTestingNestedException(this);
			if (questionTimeTesting)
			{
				for (QComponent qcAncestor:getAncestors()) {
					if (sq.isQuestionTimeTestingNestedException(qcAncestor))
					{
						questionTimeTesting=false;
						break;
					}
				}
			}
			if (questionTimeTesting)
			{
				String idAnswer=sq.peekForIdAnswer();
				isVisible=idAnswer==null?test():test(idAnswer);
			}
		}
		if (isVisible)
		{
			produceVisibleOutput(qc,bInit,bPlain);
		}
	}
	
	/**
	 * Tests if this applet component match all conditions to be displayed.
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if this applet component match all conditions to be displayed, false otherwise
	 */
	@Override
	public boolean test(String idAnswer)
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this,idAnswer);
	}
	
	/**
	 * Tests if this applet component match all conditions to be displayed.
	 * @return true if this applet component match all conditions to be displayed, false otherwise
	 */
	@Override
	public boolean test()
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this);
	}
}
