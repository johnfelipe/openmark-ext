package om.stdcomponent.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.PMatch;
import om.helper.uned.AnswerChecking;
import om.helper.uned.NumericTester;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.helper.uned.NumericTester.NumericOptions;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

// UNED: 05-07-2011 - dballestin
/**
 * A text field that can allow the user to enter superscripts and subscripts,
 * or performs automatic subscript formatting for chemical formulae.<br/>
 * Subscript and superscript modes can be changed using linked checkboxes or by
 * using the up and down arrow keys.
 * <br/>
 * The <b>plain</b> text version of the control asks the user to type the sub and sup
 * tags themselves and in the chemical formula mode to ignore the formatting.
 * <br/><br/>
 * <h2>XML usage</h2>
 * &lt;advancedfield id='reaction' type='superscript' /&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates this control</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, 
 * like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>cols</td><td>(integer)</td><td>Number of columns (approx) to allow space for the component
 * </td></tr>
 * <tr><td>value</td><td>(string)</td><td>Current value of field. (See below) </td></tr>
 * <tr><td>type</td><td>(string; 'superscript' | 'subscript' | 'both' | 'chem')</td><td>Type of field.
 * </td></tr>
 * <tr><td>answerenabled</td><td>(boolean)</td><td>Specifies if this component is used as an answer 
 * component, by default yes</td></tr>
 * <tr><td>right</td><td>(string)</td><td>Specifies the right answer</td></tr>
 * <tr><td>answertype</td><td>(string)</td><td>Specifies the answer's type</td></tr>
 * <tr><td>casesensitive</td><td>(boolean)</td><td>Specifies if the answer is considered to be 
 * case sensitive (yes) or case insensitive (no), by default yes</td></tr>
 * <tr><td>trim</td><td>(boolean)</td><td>Specifies if starting and ending whitespaces from answer
 * are ignored (yes) or not (no), by default yes</td></tr>
 * <tr><td>strip</td><td>(boolean)</td><td>Specifies if all whitespaces from answer are ignored (yes)
 * or not (no), by default no</td></tr>
 * <tr><td>singlespaces</td><td>(boolean)</td><td>Specifies if together whitespaces from answer are 
 * considered as single whitespaces (yes) or not (no), by default yes</td></tr>
 * <tr><td>ignore</td><td>(string)</td><td>Specifies text ocurrences (separated by commas) 
 * to ignore from answer, by default null</td></tr>
 * <tr><td>ignoreregexp</td><td>(string)</td><td>Specifies a regular expression to ignore text 
 * ocurrences matching it, by default null</td></tr>
 * <tr><td>replaceanswerlinetags</td><td>(boolean)</td><td>Specifies if &lt;super&gt; and/or 
 * &lt;sub&gt; tags from answer's line are replaced by the labels from answersupertag and 
 * answersubtag properties followed by the text inside them (yes) or they are not replaced (no),
 * by default yes</td></tr>
 * <tr><td>superprefix</td><td>(string)</td><td>Specifies the string to prepend before the text 
 * inside the &lt;super&gt; tag from answer's line when replacing it, by default " to the power "
 * </td></tr>
 * <tr><td>supersuffix</td><td>(string)</td><td>Specifies the string to append after the text 
 * inside the &lt;super&gt; tag from answer's line when replacing it, by default a whitespace</td></tr>
 * <tr><td>subprefix</td><td>(string)</td><td>Specifies the string to prepend before the text 
 * inside the &lt;sub&gt; tag from answer's line when replacing it, by default " subscript "</td></tr>
 * <tr><td>subsuffix</td><td>(string)</td><td>Specifies the string to append after the text 
 * inside the &lt;sub&gt; tag from answer's line when replacing it, by default a whitespace</td></tr>
 * <tr><td>tolerance</td><td>(double)</td><td>Specifies error tolerance used in numeric comparisons,
 * by default 0.0</td></tr>
 * <tr><td>displayableright</td><td>(string)</td><td>Specifies an exact answer to be used for displaying 
 * when using "displayright" task with an advancedfield with an answertype different from "text", 
 * by default null.<br/><br/>
 * Note that even in that circumstances must be tested as right or it won't be displayed.</td></tr>
 * </table>
 * <br/>
 * If type='chem' is specified then there are no subscript or subscript checkboxes
 * and the text is automatically displayed formatted with subscripts appropriate to
 * chemical formulae eg "3H<sub>2</sub>O + 2CO<sub>2</sub>".
 * The value returned does <b>not</b> include the formatting tags in this case.
 * <br/>
 * For types other than 'chem', the value returned includes the
 * <b>sub</b> and <b>sup</b> elements for subscripts and superscripts.
 * Thus 10<sup>3</sup> is returned as '10&lt;sup&gt;3&lt;/sup&gt;'
 */
public class AdvancedFieldComponent extends om.stdcomponent.AdvancedFieldComponent implements Answerable
{
	/** Text answer's type. Simply compares the text entered by the user with the right attribute */
	public static final String ANSWERTYPE_TEXT="text";
	
	/** Regular expression's answer type. */
	public static final String ANSWERTYPE_REGEXP="regexp";
	
	/** 
	 * PMatch answer's type. It use the om.helper.PMatch class to compare the text entered by user
	 * with the right attribute
	 */ 
	public static final String ANSWERTYPE_PMATCH="pmatch";
	
	/** Numeric answer's type. */
	public static final String ANSWERTYPE_NUMERIC="numeric";
	
	/** Combination of numeric and PMatch answer's types */
	public static final String ANSWERTYPE_NUMPMATCH="numpmatch";
	
	/** Character used in test property to open a selector for answertype */
	private static final char ANSWERTYPE_SELECTOR_OPEN='['; 
	
	/** Character used in test property to close a selector for answertype */
	private static final char ANSWERTYPE_SELECTOR_CLOSE=']'; 
	
	/**
	 * Character used to separate the numeric and Pmatch pattern of expected answer in a 
	 * "numpmatch" test property
	 */
	private static final char ANSWER_NUMPMATCH_SEPARATOR=',';
	
	/** Property name for the right answer (string) */
	public static final String PROPERTY_RIGHT="right";
	
	/** Property name for the answer's type (string) */
	public static final String PROPERTY_ANSWERTYPE="answertype";
	
	/** 
	 * Property name for considering the answer to be case sensitive (yes) or case insensitive (no)
	 * (boolean)
	 */
	public static final String PROPERTY_CASESENSITIVE="casesensitive";
	
	/** 
	 * Property name for ignoring starting and ending whitespaces from answer when testing if it is right
	 * (boolean)
	 */
	public static final String PROPERTY_TRIM="trim";
	
	/** Property name for ignoring all whitespaces from answer when testing if it is right (boolean) */
	public static final String PROPERTY_STRIP="strip";
	
	/** 
	 * Property name for considering together whitespaces from answer as a single whitespace when 
	 * testing if it is right (boolean)
	 */
	public static final String PROPERTY_SINGLESPACES="singlespaces";
	
	/**
	 * Property name for ignoring different text ocurrences (seperated by commas) from anwer 
	 * when testing if it is right (string) 
	 */
	public static final String PROPERTY_IGNORE="ignore";
	
	/**
	 * Property name for ignoring text ocurrences that match a regular expression (string)
	 */
	public static final String PROPERTY_IGNOREREGEXP="ignoreregexp";
	
	/**
	 * Property name for replacing &lt;super&gt; and/or &lt;sub&gt; tags from answer's line by the
	 * labels from answersupertag and answersubtag properties followed by the text inside them (boolean)
	 */
	public static final String PROPERTY_REPLACEANSWERLINETAGS="replaceanswerlinetags";
	
	/**
	 * Property name for string to prepend before the text inside the &lt;super&gt; tag 
	 * from answer's line when replacing it
	 */
	public static final String PROPERTY_SUPERPREFIX="superprefix";
	
	/**
	 * Property name for string to append after the text inside the &lt;super&gt; tag 
	 * from answer's line when replacing it
	 */
	public static final String PROPERTY_SUPERSUFFIX="supersuffix";
	
	/**
	 * Property name for string to prepend before the text inside the &lt;sub&gt; tag 
	 * from answer's line when replacing it
	 */
	public static final String PROPERTY_SUBPREFIX="subprefix";
	
	/**
	 * Property name for string to append after the text inside the &lt;sub&gt; tag 
	 * from answer's line when replacing it
	 */
	public static final String PROPERTY_SUBSUFFIX="subsuffix";
	
	/**
	 * Property name for error tolerance used in numeric comparisons
	 */
	public static final String PROPERTY_TOLERANCE="tolerance";
	
	/**
	 * Property name for defining an exact answer to be used for displaying when using "displayright" task
	 * with an advancedfield with an answertype different from "text".<br/><br/>
	 * Note that even in that circumstances must be tested as right or it won't be displayed.
	 */
	public static final String PROPERTY_DISPLAYABLERIGHT="displayableright";
	
	/** 
	 * Default string to prepend before the text inside the &lt;super&gt; tag from answer's line 
	 * when replacing it
	 */
	private static final String DEFAULT_SUPERPREFIX=" to the power ";
	
	/** 
	 * Default string to append after the text inside the &lt;super&gt; tag from answer's line 
	 * when replacing it
	 */
	private static final String DEFAULT_SUPERSUFFIX=" ";
	
	/** 
	 * Default string to prepend before the text inside the &lt;sub&gt; tag from answer's line 
	 * when replacing it
	 * 
	 */
	private static final String DEFAULT_SUBPREFIX=" subscript ";
	
	/** 
	 * Default string to append after the text inside the &lt;sub&gt; tag from answer's line 
	 * when replacing it
	 */
	private static final String DEFAULT_SUBSUFFIX=" ";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_COLS,PROPERTY_LABEL,PROPERTY_TYPE,
		PROPERTY_ANSWERENABLED,PROPERTY_RIGHT,PROPERTY_ANSWERTYPE,PROPERTY_CASESENSITIVE,PROPERTY_TRIM,
		PROPERTY_STRIP,PROPERTY_SINGLESPACES,PROPERTY_IGNORE,PROPERTY_IGNOREREGEXP,
		PROPERTY_REPLACEANSWERLINETAGS,PROPERTY_SUPERPREFIX,PROPERTY_SUPERSUFFIX,PROPERTY_SUBPREFIX,
		PROPERTY_SUBSUFFIX,PROPERTY_TOLERANCE,PROPERTY_DISPLAYABLERIGHT
	};
	
	/** Regular expression restricting advancedfield types */
	private static final String PROPERTYRESTRICTION_TYPE="superscript|subscript|both|chem";
	
	/** Map with restrictions of properties that need to initialize placeholders */
	private static final Map<String,String> PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS;
	static
	{
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS=new HashMap<String,String>();
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_LANG,PROPERTYRESTRICTION_LANG);
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_TYPE,PROPERTYRESTRICTION_TYPE);
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
		
		defineString(PROPERTY_ANSWERTYPE);
		setString(PROPERTY_ANSWERTYPE,ANSWERTYPE_TEXT);
		
		defineBoolean(PROPERTY_CASESENSITIVE);
		setBoolean(PROPERTY_CASESENSITIVE,true);
		
		defineBoolean(PROPERTY_TRIM);
		setBoolean(PROPERTY_TRIM,true);
		
		defineBoolean(PROPERTY_STRIP);
		setBoolean(PROPERTY_STRIP,false);
		
		defineBoolean(PROPERTY_SINGLESPACES);
		setBoolean(PROPERTY_SINGLESPACES,true);
		
		defineString(PROPERTY_IGNORE);
		setString(PROPERTY_IGNORE,null);
		
		defineString(PROPERTY_IGNOREREGEXP);
		setString(PROPERTY_IGNOREREGEXP,null);
		
		defineBoolean(PROPERTY_REPLACEANSWERLINETAGS);
		setBoolean(PROPERTY_REPLACEANSWERLINETAGS,true);
		
		defineString(PROPERTY_SUPERPREFIX);
		setString(PROPERTY_SUPERPREFIX,DEFAULT_SUPERPREFIX);
		
		defineString(PROPERTY_SUPERSUFFIX);
		setString(PROPERTY_SUPERSUFFIX,DEFAULT_SUPERSUFFIX);
		
		defineString(PROPERTY_SUBPREFIX);
		setString(PROPERTY_SUBPREFIX,DEFAULT_SUBPREFIX);
		
		defineString(PROPERTY_SUBSUFFIX);
		setString(PROPERTY_SUBSUFFIX,DEFAULT_SUBSUFFIX);
		
		defineDouble(PROPERTY_TOLERANCE);
		setDouble(PROPERTY_TOLERANCE,0.0);
		
		defineString(PROPERTY_DISPLAYABLERIGHT);
		setString(PROPERTY_DISPLAYABLERIGHT,null);
	}
	
	@Override
	public boolean isAnswerEnabled()
	{
		try
		{
			return getBoolean(Answerable.PROPERTY_ANSWERENABLED);
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
			setBoolean(Answerable.PROPERTY_ANSWERENABLED,answerEnabled);
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
			if (right!=null) {
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
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Answer's type
	 */
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
	 * Sets the answer's type.
	 * @param answertype answer's type
	 */
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
	 * @return true if considering the answer to be case sensitive or false if is considered to be 
	 * case insensitive
	 */
	public boolean isCaseSensitive()
	{
		try
		{
			return getBoolean(PROPERTY_CASESENSITIVE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if the answer is going to be considered case sensitive (true) or case insensitive (false)
	 * @param casesensitive true if the answer is going to be considered case sensitive, 
	 * false if it is going to be considered case insensitive
	 */
	public void setCaseSensitive(boolean casesensitive)
	{
		try
		{
			setBoolean(PROPERTY_CASESENSITIVE,casesensitive);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if starting and ending whitespaces are ignored from answer when testing 
	 * if it is right, false otherwise
	 */
	public boolean isTrim()
	{
		try
		{
			return getBoolean(PROPERTY_TRIM);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if starting and ending whitespaces are going to be ignored from answer when testing 
	 * if it is right (true) or if they are not going to be ignored (false)
	 * @param trim true if starting and ending whitespaces are going to be ignored from answer when testing 
	 * if it is right, false otherwise
	 */
	public void setTrim(boolean trim)
	{
		try
		{
			setBoolean(PROPERTY_TRIM,trim);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if all whitespaces are ignored from answer when testing if it is right, 
	 * false otherwise
	 */
	public boolean isStrip()
	{
		try
		{
			return getBoolean(PROPERTY_STRIP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if all whitespaces are ignored from answer when testing if it is right (true) or 
	 * if they are not going to be ignored (false)
	 * @param strip true if all whitespaces are ignored from answer when testing if it is right,
	 * false otherwise
	 */
	public void setStrip(boolean strip)
	{
		try
		{
			setBoolean(PROPERTY_STRIP,strip);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if together whitespaces from answer are considered as a single whitespace 
	 * when testing if it is right, false otherwise
	 */
	public boolean isSingleSpaces()
	{
		try
		{
			return getBoolean(PROPERTY_SINGLESPACES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if together whitespaces from answer are considered to be a single whitespace 
	 * when testing if it is right (true) or if they are not considered to be a single whitespace (false)
	 * @param singlespaces true if if together whitespaces from answer are considered 
	 * as a single whitespace, false otherwise
	 */
	public void setSingleSpaces(boolean singlespaces)
	{
		try
		{
			setBoolean(PROPERTY_SINGLESPACES,singlespaces);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Text ocurrences (separated by commas) to ignore from answer
	 */
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
	 * Set text ocurrences (separated by commas) to ignore from answer.
	 * @param ignore Text ocurrences (separated by commas) to ignore from answer
	 */
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
	 * @return Regular expression to ignore text ocurrences that match it
	 */
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
	 * Set regular expression to ignore text ocurrences that match it.
	 * @param regExp Regular expression
	 */
	public void setIgnoreRegExp(String regExp)
	{
		try
		{
			setString(PROPERTY_IGNOREREGEXP,regExp);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if &lt;super&gt; and/or &lt;sub&gt; tags from answer's line are going to be
	 * replaced by the labels from answersupertag and answersubtag properties followed by the text 
	 * inside them, false if they are not going to be replaced
	 */
	public boolean isReplaceAnswerLineTags()
	{
		try
		{
			return getBoolean(PROPERTY_REPLACEANSWERLINETAGS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if &lt;super&gt; and/or &lt;sub&gt; tags from answer's line are going to be replaced by the
	 * labels from answersupertag and answersubtag properties followed by the text inside them (true) 
	 * or if they are not going to be replaced (false)
	 * @param replaceanswerlinetags true to replace &lt;super&gt; and/or &lt;sub&gt; tags from 
	 * answer's line by the labels from answersupertag and answersubtag properties followed 
	 * by the text inside them, false to not replace them
	 */
	public void setReplaceAnswerLineTags(boolean replaceanswerlinetags)
	{
		try
		{
			setBoolean(PROPERTY_REPLACEANSWERLINETAGS,replaceanswerlinetags);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return String to prepend before the text inside the &lt;super&gt; tag from answer's line 
	 * when replacing it
	 */
	public String getSuperPrefix()
	{
		try
		{
			return getString(PROPERTY_SUPERPREFIX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the string to prepend before the text inside the &lt;super&gt; tag from answer's line 
	 * when replacing it.
	 * @param superprefix String to prepend before the text inside the &lt;super&gt; tag from 
	 * answer's line when replacing it
	 */
	public void setSuperPrefix(String superprefix)
	{
		try
		{
			setString(PROPERTY_SUPERPREFIX,superprefix);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return String to append after the text inside the &lt;super&gt; tag from answer's line 
	 * when replacing it
	 */
	public String getSuperSuffix()
	{
		try
		{
			return getString(PROPERTY_SUPERSUFFIX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the string to append after the text inside the &lt;super&gt; tag from answer's line 
	 * when replacing it.
	 * @param supersuffix String to append after the text inside the &lt;super&gt; tag from 
	 * answer's line when replacing it
	 */
	public void setSuperSuffix(String supersuffix)
	{
		try
		{
			setString(PROPERTY_SUPERSUFFIX,supersuffix);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return String to prepend before the text inside the &lt;sub&gt; tag from answer's line 
	 * when replacing it
	 */
	public String getSubPrefix()
	{
		try
		{
			return getString(PROPERTY_SUBPREFIX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the string to prepend before the text inside the &lt;sub&gt; tag from answer's line 
	 * when replacing it.
	 * @param subprefix String to prepend before the text inside the &lt;sub&gt; tag from 
	 * answer's line when replacing it
	 */
	public void setSubPrefix(String subprefix)
	{
		try
		{
			setString(PROPERTY_SUBPREFIX,subprefix);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return String to append after the text inside the &lt;sub&gt; tag from answer's line 
	 * when replacing it
	 */
	public String getSubSuffix()
	{
		try
		{
			return getString(PROPERTY_SUBSUFFIX);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the string to append after the text inside the &lt;sub&gt; tag from answer's line 
	 * when replacing it.
	 * @param subsuffix String to append after the text inside the &lt;sub&gt; tag from 
	 * answer's line when replacing it
	 */
	public void setSubSuffix(String subsuffix)
	{
		try
		{
			setString(PROPERTY_SUBSUFFIX,subsuffix);
		}
		catch(OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Error tolerance used in numeric comparisons
	 */
	public double getTolerance()
	{
		try
		{
			return getDouble(PROPERTY_TOLERANCE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}

	/**
	 * Set error tolerance to use in numeric comparisons
	 * @param tolerance Error tolerance to use in numeric comparisons
	 */
	public void setTolerance(double tolerance)
	{
		try
		{
			setDouble(PROPERTY_TOLERANCE,tolerance);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Exact answer to be used for displaying when using "displayright" task with an advancedfield 
	 * with an answertype different from "text"
	 */
	public String getDisplayableRight()
	{
		try
		{
			String displayableRight=getString(PROPERTY_DISPLAYABLERIGHT);
			if (displayableRight!=null)
			{
				displayableRight=AnswerChecking.replaceEscapeChars(displayableRight);
			}
			return displayableRight;
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the exact answer to be used for displaying when using "displayright" task with an advancedfield 
	 * with an answertype different from "text".
	 * @param displayableRight Exact answer to be used for displaying when using "displayright" task 
	 * with an advancedfield with an answertype different from "text"
	 */
	public void setDisplayableRight(String displayableRight)
	{
		try
		{
			setString(PROPERTY_DISPLAYABLERIGHT,displayableRight);
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
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
		String value=null;
		if (StandardQuestion.containsPlaceholder(getValue()))
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			value=getValue();
			setValue(sq.newTemporalPlaceholder(value));
		}
		super.produceVisibleOutput(qc,bInit,bPlain);
		if (value!=null)
		{
			setValue(value);
			value=null;
		}
	}
	
	/**
	 * @param answerType Answer type selected for testing (including answer type's selector if exists)
	 * @return Same answer type but without selector 
	 */
	private String getOnlyAnswerType(String answerType)
	{
		String onlyAnswerType=answerType;
		if (getAnswerTypeSelector(answerType)!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=
					AnswerChecking.indexOfCharacter(answerType,ANSWERTYPE_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				onlyAnswerType=answerType.substring(0,iStartSelector);
			}
		}
		return onlyAnswerType;
	}
	
	/**
	 * Gets answer type's selector if defined, otherwise return null
	 * @param answerType Answer type selected for testing (including answer type's selector if exists)
	 * @return Answer type's selector if defined, null otherwise
	 */
	private String getAnswerTypeSelector(String answerType)
	{
		String answerTypeSelector=null;
		if (answerType!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=
					AnswerChecking.indexOfCharacter(answerType,ANSWERTYPE_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				iStartSelector++;
				int iEndSelector=AnswerChecking.indexOfCharacter(
						answerType,ANSWERTYPE_SELECTOR_CLOSE,iStartSelector,escapeSequences);
				if (iEndSelector!=-1)
				{
					answerTypeSelector=answerType.substring(iStartSelector,iEndSelector);
				}
			}
		}
		return answerTypeSelector;
	}
	
	/**
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return Preprocessed answer ready to test for rightness
	 */
	private String getPreprocessedAnswer(AnswerTestProperties overrideProperties)
	{
		String answer=getValue();
		
		// when a user type a text superscripted or subscripted and then deletes it an undesired
		// "<sup></sup>" or "<sub></sub>" string will remain in answer, next code will correct it
		try
		{
			String type=getString(PROPERTY_TYPE);
			if (type.equals("superscript") || type.equals("both"))
			{
				answer=answer.replace("<sup></sup>","");
			}
			if (type.equals("subscript") || type.equals("both"))
			{
				answer=answer.replace("<sub></sub>","");
			}
		}
		catch (OmDeveloperException e)
		{
		}
		
		if (overrideProperties.getStrip(isStrip()))
		{
			answer=AnswerChecking.stripWhitespace(answer,false);
		}
		else
		{
			if (overrideProperties.getTrim(isTrim()))
			{
				answer=AnswerChecking.trimWhitespace(answer,false);
			}
			if (overrideProperties.getSingleSpaces(isSingleSpaces()))
			{
				answer=AnswerChecking.singledWhitespace(answer,false);
			}
		}
		String ignore=overrideProperties.getIgnore(getIgnore());
		if (ignore!=null)
		{
			answer=AnswerChecking.removeTextOcurrences(answer,ignore);
		}
		String ignoreRegExp=overrideProperties.getIgnoreRegExp(getIgnoreRegExp());
		if (ignoreRegExp!=null)
		{
			answer=answer.replaceAll(ignoreRegExp,"");
		}
		return answer;
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type text.
	 * @param s String with the expected answer
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type text)
	 */
	private boolean isAnswerText(String s,AnswerTestProperties overrideProperties)
	{
		return overrideProperties.getCaseSensitive(isCaseSensitive())?
				getPreprocessedAnswer(overrideProperties).equals(s):
				getPreprocessedAnswer(overrideProperties).equalsIgnoreCase(s);
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type regexp.
	 * @param answer String with the user's answer
	 * @param s String with the expected answer's pattern
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type regexp)
	 */
	private boolean isAnswerRegExp(String answer,String s,AnswerTestProperties overrideProperties)
	{
		if (!overrideProperties.getCaseSensitive(isCaseSensitive()))
		{
			s=AnswerChecking.regExpToLowerCase(s);
			answer=answer.toLowerCase();
		}
		return Pattern.compile(s).matcher(answer).matches();
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type regexp.
	 * @param s String with the expected answer's pattern
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type text)
	 */
	private boolean isAnswerRegExp(String s,AnswerTestProperties overrideProperties)
	{
		return isAnswerRegExp(getPreprocessedAnswer(overrideProperties),s,overrideProperties);
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type pmatch.
	 * @param answer String with the user's answer
	 * @param s String with the expected answer's pattern
	 * @param options PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type pmatch)
	 */
	private boolean isAnswerPMatch(String answer,String s,String options,AnswerTestProperties overrideProperties)
	{
		boolean matching=false;
		if (!overrideProperties.getCaseSensitive(isCaseSensitive()))
		{
			s=s.toLowerCase();
			answer=answer.toLowerCase();
		}
		PMatch pmatch=new PMatch(answer);
		if (options==null || options.equals(""))
		{
			matching=pmatch.match(s);
		}
		else
		{
			matching=pmatch.match(options,s);
		}
		return matching;
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type pmatch.
	 * @param s String with the expected answer's pattern
	 * @param options PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type pmatch)
	 */
	private boolean isAnswerPMatch(String s,String options,AnswerTestProperties overrideProperties)
	{
		return isAnswerPMatch(getPreprocessedAnswer(overrideProperties),s,options,overrideProperties);
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the numeric value indicated.
	 * @param answer String with the user's answer
	 * @param s String with the expected number
	 * @param options Numeric options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the numeric value indicated,
	 * false otherwise (for answers of type numeric)
	 */
	private boolean isAnswerNumeric(String answer,String s,NumericOptions options,
			AnswerTestProperties overrideProperties)
	{
		String expectedString=s.trim();
		boolean testing=false;
		double expected=NumericTester.inputNumber(expectedString);
		
		// Transform from scientific input to e notation if needed
		if (options.scientificTransform)
		{
			answer=NumericTester.scientificNotationToE(answer);
		}
		double numAnswer=NumericTester.inputNumber(answer);
		
		// Precision
		int precision=-1;
		if (options.usePrecision)
		{
			precision=options.significantDigits;
			if (precision==-1)
			{
				precision=NumericTester.countNumberOfSignificantFiguresInString(expectedString);
			}
			else if (precision>0 && precision<100)
			{
				expected=NumericTester.toSigFigs(expected,precision);
			}
			if (precision>0 && precision<100)
			{
				numAnswer=NumericTester.toSigFigs(numAnswer,precision);
			}
		}
		
		// Checks
		if (!Double.isNaN(numAnswer) && !Double.isNaN(expected))
		{
			testing=true;
			if (options.scientificCheck)
			{
				testing=NumericTester.isScientificNotation(answer);
			}
			if (testing && options.numberCheck)
			{
				testing=NumericTester.range(
						numAnswer,expected,overrideProperties.getTolerance(getTolerance()));
			}
			if (testing && options.absoluteCheck)
			{
				double expectedAbs=StrictMath.abs(expected);
				double numAnswerAbs=StrictMath.abs(numAnswer);
				testing=NumericTester.range(
						numAnswerAbs,expectedAbs,overrideProperties.getTolerance(getTolerance()));
			}
			if (testing && options.significantDigitsCheck)
			{
				String expectedSignificantDigits=NumericTester.getSignificantFigures(expectedString);
				String numAnswerSignificantDigits=NumericTester.getSignificantFigures(answer);
				if (precision!=-1)
				{
					if (expectedSignificantDigits.startsWith("-") && 
							expectedSignificantDigits.length()-1>precision)
					{
						expectedSignificantDigits=expectedSignificantDigits.substring(0,precision+1);
					}
					else if (expectedSignificantDigits.length()>precision)
					{
						expectedSignificantDigits=expectedSignificantDigits.substring(0,precision);
					}
					if (numAnswerSignificantDigits.startsWith("-") && 
							numAnswerSignificantDigits.length()-1>precision)
					{
						numAnswerSignificantDigits=numAnswerSignificantDigits.substring(0,precision+1);
					}
					else if (numAnswerSignificantDigits.length()>precision)
					{
						numAnswerSignificantDigits=numAnswerSignificantDigits.substring(0,precision);
					}
				}
				testing=numAnswerSignificantDigits.equals(expectedSignificantDigits);
			}
			if (testing && options.exponentCheck)
			{
				int expectedExponent=NumericTester.getExponent(expectedString);
				int numAnswerExponent=NumericTester.getExponent(answer);
				testing=numAnswerExponent==expectedExponent;
			}
			if (testing && options.precissionCheck)
			{
				int expectedSignificantDigits=options.precissionCheckSignificantDigits;
				if (expectedSignificantDigits==-1)
				{
					expectedSignificantDigits=
							NumericTester.countNumberOfSignificantFiguresInString(expectedString);
				}
				int numAnswerSignificantDigits=NumericTester.countNumberOfSignificantFiguresInString(answer);
				switch (options.precissionCheckOp)
				{
					case NumericOptions.EQUAL_PRECISION:
						testing=numAnswerSignificantDigits==expectedSignificantDigits;
						break;
					case NumericOptions.LESS_PRECISION:
						testing=numAnswerSignificantDigits<expectedSignificantDigits;
						break;
					case NumericOptions.LESS_EQUAL_PRECISION:
						testing=numAnswerSignificantDigits<=expectedSignificantDigits;
						break;
					case NumericOptions.GREATER_PRECISION:
						testing=numAnswerSignificantDigits>expectedSignificantDigits;
						break;
					case NumericOptions.GREATER_EQUAL_PRECISION:
						testing=numAnswerSignificantDigits>=expectedSignificantDigits;
						break;
					default:
						testing=false;
				}
			}
		}
		return testing;
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the numeric value indicated.
	 * @param s String with the expected number
	 * @param options Numeric options as string
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the numeric value indicated,
	 * false otherwise (for answers of type numeric)
	 */
	private boolean isAnswerNumeric(String s,String options,AnswerTestProperties overrideProperties)
	{
		return isAnswerNumeric(getPreprocessedAnswer(overrideProperties),s,new NumericOptions(options),
				overrideProperties);
	}
	
	/**
	 * Get the numeric options from numpmatch options string
	 * @param options numpmatch options string
	 * @return Numeric options from numpmatch options string
	 */
	private String getNumericOptionsForNumPMatchOptions(String options)
	{
		String numericOptions="";
		if (options!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iFirstSeparator=
					AnswerChecking.indexOfCharacter(options,ANSWER_NUMPMATCH_SEPARATOR,escapeSequences);
			if (iFirstSeparator!=-1)
			{
				int iSecondSeparator=AnswerChecking.indexOfCharacter(
						options,ANSWER_NUMPMATCH_SEPARATOR,iFirstSeparator+1,escapeSequences);
				if (iSecondSeparator!=-1)
				{
					numericOptions=options.substring(0,iFirstSeparator);
				}
			}
		}
		return numericOptions;
	}
	
	/**
	 * Get the separator from numpmatch options string
	 * @param options numpmatch options string
	 * @return Separator from numpmatch options string
	 */
	private String getSeparatorForNumPMatchOptions(String options)
	{
		String separator="";
		if (options!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iFirstSeparator=
					AnswerChecking.indexOfCharacter(options,ANSWER_NUMPMATCH_SEPARATOR,escapeSequences);
			if (iFirstSeparator!=-1)
			{
				int iSecondSeparator=AnswerChecking.indexOfCharacter(
						options,ANSWER_NUMPMATCH_SEPARATOR,iFirstSeparator+1,escapeSequences);
				if (iSecondSeparator==-1)
				{
					separator=options.substring(0,iFirstSeparator);
				}
				else
				{
					separator=options.substring(iFirstSeparator+1,iSecondSeparator);
				}
			}
		}
		return AnswerChecking.replaceEscapeChars(separator);
	}
	
	/**
	 * Get the pmatch options from numpmatch options string
	 * @param options numpmatch options string
	 * @return pmatch options from numpmatch options string
	 */
	private String getPMatchOptionsForNumPMatchOptions(String options)
	{
		String pMatchOptions=options;
		if (options!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iFirstSeparator=
					AnswerChecking.indexOfCharacter(options,ANSWER_NUMPMATCH_SEPARATOR,escapeSequences);
			if (iFirstSeparator!=-1)
			{
				int iSecondSeparator=AnswerChecking.indexOfCharacter(
						options,ANSWER_NUMPMATCH_SEPARATOR,iFirstSeparator+1,escapeSequences);
				if (iSecondSeparator==-1)
				{
					pMatchOptions="";
					if (iFirstSeparator+1<options.length())
					{
						pMatchOptions=options.substring(iFirstSeparator+1);
					}
				}
				else
				{
					pMatchOptions="";
					if (iSecondSeparator+1<options.length())
					{
						pMatchOptions=options.substring(iSecondSeparator+1);
					}
				}
			}
		}
		return pMatchOptions;
	}
	
	/**
	 * Get index of the next character to the numeric part of the user's answer.<br/><br/>
	 * This function takes care if the user's answer is expected to be in scientific notation or not.
	 * @param answer User's answer
	 * @param nOpts Numeric options
	 * @return index of the next character to the numeric part of the user's answer
	 */
	private int getEndOfNumberPosition(String answer,NumericOptions nOpts)
	{
		int endOfNumberPosition=-1;
		if (answer!=null)
		{
			int iStart=0;
			char c='\0';
			if (iStart<answer.length())
			{
				c=answer.charAt(iStart);
			}
			while (c!='\0' && c!='.' && (c<'0' || c>'9'))
			{
				iStart++;
				if (iStart<answer.length())
				{
					c=answer.charAt(iStart);
				}
				else
				{
					c='\0';
				}
			}
			if (c!='\0')
			{
				endOfNumberPosition=iStart;
				while (c>='0' && c<='9')
				{
					endOfNumberPosition++;
					if (endOfNumberPosition<answer.length())
					{
						c=answer.charAt(endOfNumberPosition);
					}
					else
					{
						c='\0';
					}
				}
				if (c=='.')
				{
					endOfNumberPosition++;
					if (endOfNumberPosition<answer.length())
					{
						c=answer.charAt(endOfNumberPosition);
					}
					else
					{
						c='\0';
					}
					while (c>='0' && c<='9')
					{
						endOfNumberPosition++;
						if (endOfNumberPosition<answer.length())
						{
							c=answer.charAt(endOfNumberPosition);
						}
						else
						{
							c='\0';
						}
					}
				}
				if (nOpts.scientificTransform)
				{
					if (c!='\0')
					{
						StringBuffer mulDot10=new StringBuffer();
						mulDot10.append('\u00d7');
						mulDot10.append("10");
						String x10Start=answer.substring(endOfNumberPosition);
						if (x10Start.startsWith("x10") || x10Start.startsWith("X10") || 
								x10Start.startsWith("*10") || 
								x10Start.startsWith(mulDot10.toString()))
						{
							endOfNumberPosition+=3;
							boolean openSup=false;
							int i=endOfNumberPosition+5;
							if (i<answer.length())
							{
								String sOpen=answer.substring(endOfNumberPosition,i);
								if (sOpen.equalsIgnoreCase("<sup>"))
								{
									openSup=true;
								}
							}
							if (openSup)
							{
								i=answer.indexOf('<',i);
								if (i!=-1)
								{
									int j=i+6;
									if (j<answer.length())
									{
										String sClose=answer.substring(i,j);
										if (sClose.equalsIgnoreCase("</sup>"))
										{
											endOfNumberPosition=j;
										}
									}
								}
							}
						}
					}
				}
				else
				{
					if (c=='e' || c=='E')
					{
						int i=endOfNumberPosition+1;
						if (i<answer.length())
						{
							c=answer.charAt(i);
						}
						else
						{
							c='\0';
						}
						if (c=='+' || c=='-')
						{
							i++;
							if (i<answer.length())
							{
								c=answer.charAt(i);
							}
							else
							{
								c='\0';
							}
						}
						if (c>='0' && c<='9')
						{
							endOfNumberPosition=i;
							while (c>='0' && c<='9')
							{
								endOfNumberPosition++;
								if (endOfNumberPosition<answer.length())
								{
									c=answer.charAt(endOfNumberPosition);
								}
								else
								{
									c='\0';
								}
							}
						}
					}
				}
			}
			else
			{
				endOfNumberPosition=0;
			}
		}
		return endOfNumberPosition;
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type numpmatch.
	 * @param answer String with the user's answer
	 * @param s String with the expected number and pattern separated by ,
	 * @param options Number separator and PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type numpmatch)
	 */
	private boolean isAnswerNumPMatch(String answer,String s,String options,
			AnswerTestProperties overrideProperties)
	{
		boolean testing=false;
		if (s!=null)
		{
			NumericOptions numericOptions=new NumericOptions(getNumericOptionsForNumPMatchOptions(options));
			String separator=getSeparatorForNumPMatchOptions(options);
			String pMatchOptions=getPMatchOptionsForNumPMatchOptions(options);
			int iSeparator=s.indexOf(ANSWER_NUMPMATCH_SEPARATOR);
			if (iSeparator!=-1)
			{
				String numPart=s.substring(0,iSeparator);
				String patternPart="";
				if (iSeparator+1<s.length())
				{
					patternPart=s.substring(iSeparator+1);
				}
				int endAnswerPartNumber=getEndOfNumberPosition(answer,numericOptions);
				String userNumAnswer=endAnswerPartNumber>0?answer.substring(0,endAnswerPartNumber):"";
				String userNumSeparator="";
				String userPatternAnswer="";
				if (endAnswerPartNumber>=0 && endAnswerPartNumber+separator.length()<answer.length())
				{
					userNumSeparator=
							answer.substring(endAnswerPartNumber,endAnswerPartNumber+separator.length());
					userPatternAnswer=answer.substring(endAnswerPartNumber+separator.length());
				}
				if (userNumSeparator.equals(separator))
				{
					testing=isAnswerNumeric(userNumAnswer,numPart,numericOptions,overrideProperties);				
				}
				if (testing)
				{
					testing=isAnswerPMatch(userPatternAnswer,patternPart,pMatchOptions,overrideProperties);
				}
			}
		}
		return testing;
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received 
	 * for answers of type numpmatch.
	 * @param s String with the expected number and pattern separated by ,
	 * @param options Number separator and PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise (for answers of type numpmatch)
	 */
	private boolean isAnswerNumPMatch(String s,String options,AnswerTestProperties overrideProperties)
	{
		return isAnswerNumPMatch(getPreprocessedAnswer(overrideProperties),s,options,overrideProperties);
	}
	
	/**
	 * Returns if the answer typed in this advancedfield corresponds to the answer received.
	 * @param s String with the expected answer
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this advancedfield corresponds to the answer received,
	 * false otherwise
	 */
	public boolean isAnswer(String s,AnswerTestProperties overrideProperties)
	{
		boolean answer=false;
		if (s!=null)
		{
			String sAux=s.replace("\n","");
			String answerType=overrideProperties.getAnswerType(getAnswerType());
			if (answerType!=null)
			{
				String onlyAnswerType=getOnlyAnswerType(answerType);
				if (onlyAnswerType.equals(ANSWERTYPE_TEXT))
				{
					answer=isAnswerText(sAux,overrideProperties);
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_REGEXP))
				{
					answer=isAnswerRegExp(s,overrideProperties);
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_PMATCH))
				{
					String selector=getAnswerTypeSelector(answerType);
					answer=isAnswerPMatch(sAux,selector,overrideProperties);
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_NUMERIC))
				{
					String selector=getAnswerTypeSelector(answerType);
					answer=isAnswerNumeric(sAux,selector,overrideProperties);
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_NUMPMATCH))
				{
					String selector=getAnswerTypeSelector(answerType);
					answer=isAnswerNumPMatch(sAux,selector,overrideProperties);
				}
			}
		}
		return answer;
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
	 * @param tag Tag's name
	 * @return Open tag string
	 */
	private String getOpenTagText(String tag)
	{
		StringBuffer openTag=new StringBuffer();
		openTag.append('<');
		if (tag!=null)
		{
			openTag.append(tag);
		}
		openTag.append('>');
		return openTag.toString();
	}
	
	/**
	 * @param tag Tag's name
	 * @return Close tag string
	 */
	private String getCloseTagText(String tag)
	{
		StringBuffer openTag=new StringBuffer();
		openTag.append("</");
		if (tag!=null)
		{
			openTag.append(tag);
		}
		openTag.append('>');
		return openTag.toString();
	}
	
	/**
	 * @param s String
	 * @param tag Tag's name to match
	 * @param prefix String to prepend before every tag ocurrence matched
	 * @param suffix String to append after every tag ocurrence matched
	 * @return Same string but replacing tag ocurrences matched with the text inside them 
	 * but prepending the prefix and appending the suffix  
	 */
	private String replaceTaggedText(String s,String tag,String prefix,String suffix)
	{
		StringBuffer replacedString=null;
		if (s!=null)
		{
			String openTag=getOpenTagText(tag);
			String closeTag=getCloseTagText(tag);
			replacedString=new StringBuffer();
			int i=0;
			boolean replaceLoop=true;
			while (replaceLoop)
			{
				int iOpenTag=s.indexOf(openTag,i);
				if (iOpenTag==-1)
				{
					replacedString.append(s.substring(i));
					replaceLoop=false;
				}
				else
				{
					int iStartTaggedText=iOpenTag+openTag.length();
					if (iStartTaggedText<s.length())
					{
						int iCloseTag=s.indexOf(closeTag,iStartTaggedText);
						if (iCloseTag==-1)
						{
							replacedString.append(s.substring(i));
							replaceLoop=false;
						}
						else
						{
							replacedString.append(s.substring(i,iOpenTag));
							if (prefix!=null)
							{
								replacedString.append(prefix);
							}
							if (iCloseTag>iStartTaggedText)
							{
								replacedString.append(
									s.substring(iStartTaggedText,iCloseTag));
							}
							if (suffix!=null)
							{
								replacedString.append(suffix);
							}
							i=iCloseTag+closeTag.length();
							if (i >= s.length())
							{
								replaceLoop=false;
							}
						}
					}
					else
					{
						replacedString.append(s.substring(i));
						replaceLoop=false;
					}
				}
			}
		}
		return replacedString==null?null:replacedString.toString();
	}
	
	/**
	 * Returns answer line.<br/>
	 * It is the text typed in the editfield component after preprocessing for testing if it is right.
	 * @return Answer line
	 */
	@Override
	public String getAnswerLine()
	{
		String answerLine=getPreprocessedAnswer(new AnswerTestProperties());
		if (isReplaceAnswerLineTags())
		{
			try
			{
				String type=getString(PROPERTY_TYPE);
				if (type!=null)
				{
					if (type.equals("superscript") || type.equals("both"))
					{
						answerLine=replaceTaggedText(answerLine,"sup",getSuperPrefix(),getSuperSuffix());
					}
					if (type.equals("subscript") || type.equals("both"))
					{
						answerLine=replaceTaggedText(answerLine,"sub",getSubPrefix(),getSubSuffix());
					}
				}
			} catch (OmDeveloperException e)
			{
			}
		}
		return answerLine;
	}
	
	/**
	 * Returns if the answer to display when using "displayright" task with an advancedfield with a "regexp" 
	 * answertype is right (true) or not (false).
	 * @return true if the answer to display when using "displayright" task with an advancedfield 
	 * with a "regexp" answertype is right, false otherwise (for answers of type regexp)
	 */
	private boolean isDisplayableAnswerRegExp()
	{
		return isAnswerRegExp(getPreprocessedDisplayableRight(),getRight(),new AnswerTestProperties());
	}
	
	/**
	 * Returns if the answer to display when using "displayright" task with an advancedfield with a "pmatch" 
	 * answertype is right (true) or not (false).
	 * @param options PMatch options
	 * @return true if the answer to display when using "displayright" task with an advancedfield 
	 * with a "pmatch" answertype is right, false otherwise (for answers of type pmatch)
	 */
	private boolean isDisplayableAnswerPMatch(String options)
	{
		return isAnswerPMatch(getPreprocessedDisplayableRight(),getRight(),options,new AnswerTestProperties());
	}
	
	/**
	 * Returns if the answer to display when using "displayright" task with an advancedfield with a "numeric" 
	 * answertype is right (true) or not (false).
	 * @param options Numeric options as string
	 * @return true if the answer to display when using "displayright" task with an advancedfield 
	 * with a "numeric" answertype is right, false otherwise (for answers of type numeric)
	 */
	private boolean isDisplayableAnswerNumeric(String options)
	{
		return isAnswerNumeric(getPreprocessedDisplayableRight(),getRight(),new NumericOptions(options),
				new AnswerTestProperties());
	}
	
	
	/**
	 * Returns if the answer to display when using "displayright" task with an advancedfield 
	 * with a "numpmatch" answertype is right (true) or not (false).
	 * @param options Number separator and PMatch options
	 * @return true if the answer to display when using "displayright" task with an advancedfield 
	 * with a "numpmatch" answertype is right, false otherwise (for answers of type numpmatch)
	 */
	private boolean isDisplayableAnswerNumPMatch(String options)
	{
		return isAnswerNumPMatch(
				getPreprocessedDisplayableRight(),getRight(),options,new AnswerTestProperties());
	}
	
	/**
	 * Returns if the answer to display when using "displayright" task with an advancedfield 
	 * with an answertype different from "text" is right (true) or not (false).
	 * @return true if the answer to display when using "displayright" task with an advancedfield 
	 * with an answertype different from "text" is right, false otherwise
	 */
	public boolean isDisplayableAnswer()
	{
		boolean displayableAnswer=false;
		if (getDisplayableRight()!=null)
		{
			if (getAnswerType()!=null)
			{
				String onlyAnswerType=getOnlyAnswerType(getAnswerType());
				if (onlyAnswerType.equals(ANSWERTYPE_REGEXP))
				{
					displayableAnswer=isDisplayableAnswerRegExp();
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_PMATCH))
				{
					String selector=getAnswerTypeSelector(getAnswerType());
					displayableAnswer=isDisplayableAnswerPMatch(selector);
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_NUMERIC))
				{
					String selector=getAnswerTypeSelector(getAnswerType());
					displayableAnswer=isDisplayableAnswerNumeric(selector);
				}
				else if (onlyAnswerType.equals(ANSWERTYPE_NUMPMATCH))
				{
					String selector=getAnswerTypeSelector(getAnswerType());
					displayableAnswer=isDisplayableAnswerNumPMatch(selector);
				}
			}
		}
		return displayableAnswer;
	}
	
	/**
	 * @return Preprocessed "displayableright" answer ready to test for rightness
	 */
	private String getPreprocessedDisplayableRight()
	{
		String displayableRight=getDisplayableRight();
		try
		{
			String type=getString(PROPERTY_TYPE);
			if (type.equals("superscript") || type.equals("both"))
			{
				displayableRight=displayableRight.replace("<sup></sup>","");
			}
			if (type.equals("subscript") || type.equals("both"))
			{
				displayableRight=displayableRight.replace("<sub></sub>","");
			}
		}
		catch (OmDeveloperException e)
		{
		}
		if (isStrip())
		{
			displayableRight=AnswerChecking.stripWhitespace(displayableRight,false);
		}
		else
		{
			if (isTrim())
			{
				displayableRight=AnswerChecking.trimWhitespace(displayableRight,false);
			}
			if (isSingleSpaces())
			{
				displayableRight=AnswerChecking.singledWhitespace(displayableRight,false);
			}
		}
		if (getIgnore()!=null)
		{
			displayableRight=AnswerChecking.removeTextOcurrences(displayableRight,getIgnore());
		}
		if (getIgnoreRegExp()!=null)
		{
			displayableRight=displayableRight.replaceAll(getIgnoreRegExp(),"");
		}
		return displayableRight;
	}
}
