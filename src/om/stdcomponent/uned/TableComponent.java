package om.stdcomponent.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.helper.Shuffler;
import om.helper.uned.Tester;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import util.xml.XML;

// UNED: 07-12-2011 - dballestin
/**
 * This is a component to layout components as a table.<br/><br/>
 * The table component can only contain &lt;row> and &lt;title> elements directly.<br/><br/>
 * Rows can only contain &lt;t> (text) elements (each defines a cell), but these can contain things such as
 * images, text input areas and also formatting elements such as  &lt;centre> <br/><br/>
 * The title is placed as a string a the top of the table and is set
 * to be the caption for improved accessibillity.<br/><br/>
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
 * <h2>Example XML usage</h2>
 * &lt;table cols='2' rows='3' left='1' head='1' &gt; <br/>
 * &lt;title>This is a table of fruit colours&lt;/title><br/>
 * &lt;row>&lt;t>Fruit&lt;/t>&lt;t>Colour&lt;/t>&lt;/row><br/>
 * &lt;row>&lt;t>Apple&lt;/t>&lt;t>red   &lt;/t>&lt;/row><br/>
 * &lt;row>&lt;t>Lemon&lt;/t>&lt;t>yellow&lt;/t>&lt;/row><br/>
 * &lt;/table>
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>cols</td><td>(integer)</td><td>total number of columns in the table</td></tr>
 * <tr><td>rows</td><td>(integer)</td><td>total number of rows in the table</td></tr>
 * <tr><td>head</td><td>(integer)</td><td>number of header rows</td></tr>
 * <tr><td>foot</td><td>(integer)</td><td>number of footer rows</td></tr>
 * <tr><td>left</td><td>(integer)</td><td>number of label columns on left</td></tr>
 * <tr><td>right</td><td>(integer)</td><td>number of label columns on right</td></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the HTML lang attribute. 
 * For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>borders</td><td>(boolean)</td><td>Specifies if table and cell borders are displayed, 
 * by default yes.</td></tr>
 * <tr><td>backgroundenabled</td><td>(boolean)</td><td>Specifies if table background is displayed, 
 * by default yes.</td></tr>
 * <tr><td>shufflerows</td><td>(boolean)</td><td>If true, randomises order of rows, by default false.
 * </td></tr>
 * <tr><td>shufflecols</td><td>(boolean)</td><td>If true, randomises order of cols, by default false.
 * </td></tr>
 * <tr><td>fixedrows</td><td>(string)</td><td>Comma-separated list of fixed rows not affected by
 * shufflerows property.</td></tr>
 * <tr><td>fixedcols</td><td>(string)</td><td>Comma-separated list of fixed cols not affected by
 * shufflecols property.</td></tr>
 * <tr><td>randomgrouprows</td><td>(string)</td><td>Identifier of the &lt;random&gt; component or 
 * name of group used as seed for random number generator for rows or null if we are using default seed
 * </td></tr>
 * <tr><td>randomgroupcols</td><td>(string)</td><td>Identifier of the &lt;random&gt; component or 
 * name of group used as seed for random number generator for cols or null if we are using default seed
 * </td></tr>
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
 */
public class TableComponent extends om.stdcomponent.TableComponent implements Testable
{
	/** Property name for columns for table (integer) */
	public static final String PROPERTY_COLS="cols";
	
	/** Property name for rows for table (integer) */
	public static final String PROPERTY_ROWS="rows";
	
	/** Property name for number of header rows (integer) */
	public static final String PROPERTY_HEAD="head";
	
	/** Property name for number of footer rows (integer) */
	public static final String PROPERTY_FOOT="foot";
	
	/** Property name for number of label columns on left (integer) */
	public static final String PROPERTY_LEFT="left";
	
	/** Property name for number of label columns on right (integer) */
	public static final String PROPERTY_RIGHT="right";
	
	
	/** Property name for displaying table and cells borders (boolean) */
	public static final String PROPERTY_BORDER="border";
	
	/** Property name for displaying table background (boolean) */
	public static final String PROPERTY_BACKGROUNDENABLED="backgroundenabled";
	
	/** Boolean property: whether or not to shuffle rows */
	public final static String PROPERTY_SHUFFLEROWS="shufflerows";
	
	/** Boolean property: whether or not to shuffle cols */
	public final static String PROPERTY_SHUFFLECOLS="shufflecols";
	
	/** 
	 * Property name with a comma-separated list of fixed rows not affected by shufflerows property 
	 * (string).
	 */
	public static final String PROPERTY_FIXEDROWS="fixedrows";
	
	/** 
	 * Property name with a comma-separated list of fixed cols not affected by shufflecols property 
	 * (string).
	 */
	public static final String PROPERTY_FIXEDCOLS="fixedcols";
	
	/**
	 * Property name with the identifier of the &lt;random&gt; component or the name of group used 
	 * as seed for random number generator for rows or null if we are using default seed (string).
	 */
	public static final String PROPERTY_RANDOMGROUPROWS="randomgrouprows";
	
	/**
	 * Property name with the identifier of the &lt;random&gt; component or the name of group used 
	 * as seed for random number generator for cols or null if we are using default seed (string).
	 */
	public static final String PROPERTY_RANDOMGROUPCOLS="randomgroupcols";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_COLS,PROPERTY_ROWS,PROPERTY_HEAD,
		PROPERTY_FOOT,PROPERTY_LEFT,PROPERTY_RIGHT,PROPERTY_BORDER,PROPERTY_BACKGROUNDENABLED,
		PROPERTY_SHUFFLEROWS,PROPERTY_SHUFFLECOLS,PROPERTY_FIXEDROWS,PROPERTY_FIXEDCOLS,
		PROPERTY_RANDOMGROUPROWS,PROPERTY_RANDOMGROUPCOLS,PROPERTY_TEST,PROPERTY_ANSWER,PROPERTY_ATTEMPTSMIN,
		PROPERTY_ATTEMPTSMAX,PROPERTY_SELECTEDANSWERSMIN,PROPERTY_SELECTEDANSWERSMAX,
		PROPERTY_SELECTEDRIGHTANSWERSMIN,PROPERTY_SELECTEDRIGHTANSWERSMAX,PROPERTY_SELECTEDWRONGANSWERSMIN,
		PROPERTY_SELECTEDWRONGANSWERSMAX,PROPERTY_UNSELECTEDANSWERSMIN,PROPERTY_UNSELECTEDANSWERSMAX,
		PROPERTY_UNSELECTEDRIGHTANSWERSMIN,PROPERTY_UNSELECTEDRIGHTANSWERSMAX,
		PROPERTY_UNSELECTEDWRONGANSWERSMIN,PROPERTY_UNSELECTEDWRONGANSWERSMAX,PROPERTY_RIGHTDISTANCEMIN,
		PROPERTY_RIGHTDISTANCEMAX,PROPERTY_ANSWERTYPE,PROPERTY_CASESENSITIVE,PROPERTY_TRIM,PROPERTY_STRIP,
		PROPERTY_SINGLESPACES,PROPERTY_NEWLINESPACE,PROPERTY_IGNORE,PROPERTY_IGNOREREGEXP,
		PROPERTY_IGNOREEMPTYLINES,PROPERTY_TOLERANCE
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
	
	/** Array of table elements (columns etc) after shuffle */
	private ArrayList<TableRow> shuffleTableElements;
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineBoolean(PROPERTY_BORDER);
		setBoolean(PROPERTY_BORDER,true);
		
		defineBoolean(PROPERTY_BACKGROUNDENABLED);
		setBoolean(PROPERTY_BACKGROUNDENABLED,true);
		
		defineBoolean(PROPERTY_SHUFFLEROWS);
		setBoolean(PROPERTY_SHUFFLEROWS,false);
		
		defineBoolean(PROPERTY_SHUFFLECOLS);
		setBoolean(PROPERTY_SHUFFLECOLS,false);
		
		defineString(PROPERTY_FIXEDROWS);
		setString(PROPERTY_FIXEDROWS,null);
		
		defineString(PROPERTY_FIXEDCOLS);
		setString(PROPERTY_FIXEDCOLS,null);
		
		defineString(PROPERTY_RANDOMGROUPROWS);
		setString(PROPERTY_RANDOMGROUPROWS,null);
		
		defineString(PROPERTY_RANDOMGROUPCOLS);
		setString(PROPERTY_RANDOMGROUPCOLS,null);
		
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
	
	/** @return true if the table and cells borders are displayed, false otherwise */
	public boolean isBorder()
	{
		try
		{
			return getBoolean(PROPERTY_BORDER);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if the table and cells borders are displayed (true) or not (false).
	 * @param bBorders true to display table and cells borders, false otherwise
	 */
	public void setBorder(boolean bBorder)
	{
		try
		{
			setBoolean(PROPERTY_BORDER,bBorder);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return true if the table background displayed, false otherwise */
	public boolean isBackgroundEnabled()
	{
		try
		{
			return getBoolean(PROPERTY_BACKGROUNDENABLED);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if the table background is displayed (true) or not (false).
	 * @param bBorders true to display table background, false otherwise
	 */
	public void setBackgroundEnabled(boolean bBackgroundEnabled)
	{
		try
		{
			setBoolean(PROPERTY_BACKGROUNDENABLED,bBackgroundEnabled);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return true if rows will be displayed in a random order, false otherwise */
	public boolean isShuffleRows()
	{
		try
		{
			return getBoolean(PROPERTY_SHUFFLEROWS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 *  Set the flag that indicates that rows will be displayed in a random order (true) or not (false).
	 *  @param shuffleRows true to indicate that rows will be displayed in a random order, false otherwise
	 */
	public void setShuffleRows(boolean shuffleRows)
	{
		try
		{
			setBoolean(PROPERTY_SHUFFLEROWS,shuffleRows);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return true if cols will be displayed in a random order, false otherwise */
	public boolean isShuffleCols()
	{
		try
		{
			return getBoolean(PROPERTY_SHUFFLECOLS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 *  Set the flag that indicates that cols will be displayed in a random order (true) or not (false).
	 *  @param shuffleColss true to indicate that cols will be displayed in a random order, false otherwise
	 */
	public void setShuffleColss(boolean shuffleCols)
	{
		try
		{
			setBoolean(PROPERTY_SHUFFLECOLS,shuffleCols);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Comma-separated list of fixed rows not affected by shufflerows property
	 */
	public String getFixedRows()
	{
		try
		{
			String fixedRows=getString(PROPERTY_FIXEDROWS);
			if (placeholders.containsKey(PROPERTY_FIXEDROWS))
			{
				checkFixedPositions(PROPERTY_FIXEDROWS,fixedRows);
			}
			return fixedRows;
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the comma-separated list of fixed rows not affected by shufflerows property.
	 * @param fixedRows Comma-separated list of fixed rows not affected by shufflerows property
	 */
	public void setFixedRows(String fixedRows)
	{
		try
		{
			checkFixedPositions(PROPERTY_FIXEDROWS,fixedRows);
			setString(PROPERTY_FIXEDROWS,fixedRows);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Comma-separated list of fixed rows not affected by shufflecols property
	 */
	public String getFixedCols()
	{
		try
		{
			String fixedCols=getString(PROPERTY_FIXEDCOLS);
			if (placeholders.containsKey(PROPERTY_FIXEDCOLS))
			{
				checkFixedPositions(PROPERTY_FIXEDCOLS,fixedCols);
			}
			return fixedCols;
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the comma-separated list of fixed cols not affected by shufflecols property.
	 * @param fixedCols Comma-separated list of fixed cols not affected by shufflecols property
	 */
	public void setFixedCols(String fixedCols)
	{
		try
		{
			checkFixedPositions(PROPERTY_FIXEDCOLS,fixedCols);
			setString(PROPERTY_FIXEDCOLS,fixedCols);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Checks the comma-separated list of fixed positions not affected by shuffle property and throws 
	 * an OmFormatException exception if it is invalid.
	 * @param fixedPositions Comma-separated list of fixed positions not affected by shuffle property
	 * @throws OmFormatException
	 */
	private void checkFixedPositions(String propertyName,String fixedPositions) throws OmFormatException
	{
		if (fixedPositions!=null)
		{
			String[] positions=fixedPositions.split(",");
			for (String position:positions)
			{
				StringBuffer error=new StringBuffer("<table>: property '");
				error.append(propertyName);
				error.append(" has an invalid value");
				try
				{
					int pos=Integer.parseInt(position);
					if (pos<0)
					{
						throw new OmFormatException(error.toString());
					}
				}
				catch (NumberFormatException e)
				{
					throw new OmFormatException(error.toString());
				}
			}
		}
	}
	
	/**
	 * @return Identifier of the &lt;random&gt; component or name of group used as seed 
	 * for random number generator for rows or null if we are using default seed
	 */
	public String getRandomGroupRows()
	{
		try
		{
			return getString(PROPERTY_RANDOMGROUPROWS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set identifier of the &lt;random&gt; component or name of group to use as seed for 
	 * random number generator for rows or null to use default seed.
	 * @param randomGroup Identifier of the &lt;random&gt; component or name of group to use as seed 
	 * for random number generator for rows or null to use default seed
	 */
	public void setRandomGroupRows(String randomGroupRows)
	{
		try
		{
			setString(PROPERTY_RANDOMGROUPROWS,randomGroupRows);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Identifier of the &lt;random&gt; component or name of group used as seed 
	 * for random number generator for cols or null if we are using default seed
	 */
	public String getRandomGroupCols()
	{
		try
		{
			return getString(PROPERTY_RANDOMGROUPCOLS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set identifier of the &lt;random&gt; component or name of group to use as seed for 
	 * random number generator for cols or null to use default seed.
	 * @param randomGroup Identifier of the &lt;random&gt; component or name of group to use as seed 
	 * for random number generator for cols or null to use default seed
	 */
	public void setRandomGroupCols(String randomGroupCols)
	{
		try
		{
			setString(PROPERTY_RANDOMGROUPCOLS,randomGroupCols);
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
		setTableElements(new ArrayList<TableRow>(10));
		for (Node nChild=eThis.getFirstChild();nChild!=null;nChild=nChild.getNextSibling())
		{
			if (nChild instanceof Element)
			{
				Element e=(Element)nChild;
				if(e.getTagName().equals("row"))
				{
					TableRow tr=new TableRow();
					buildRow(tr,e);
					getTableElements().add(tr);
				}
				else if (e.getTagName().equals("title"))
				{
					setTitle(getQDocument().build(this,e,"t"));
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<table> may only contain <row> and <title> tags, found <");
					error.append(e.getTagName());
					error.append(">");
					throw new OmFormatException(error.toString());
				}
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
	 * Default implementation of this method adds checks for 'fixedrows' and 'fixedcols' properties.<br/><br/>
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	protected void initializeSpecific(Element eThis) throws OmException
	{
		// We add a check for 'fixedrows' property for its value
		addSpecificCheck(
				PROPERTY_FIXEDROWS,
				new PropertyCheck()
				{
					@Override
					public void check(Object value) throws OmDeveloperException
					{
						checkFixedPositions(PROPERTY_FIXEDROWS,(String)value);
					}
				});
		
		// We add a check for 'fixedcols' property for its value
		addSpecificCheck(
				PROPERTY_FIXEDCOLS,
				new PropertyCheck()
				{
					@Override
					public void check(Object value) throws OmDeveloperException
					{
						checkFixedPositions(PROPERTY_FIXEDCOLS,(String)value);
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
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
		setRows(getInteger(PROPERTY_ROWS));
		setCols(getInteger(PROPERTY_COLS));
		setHead(getIntegerIfDefined(PROPERTY_HEAD,0));
		setFoot(getIntegerIfDefined(PROPERTY_FOOT,0));
		setLeft(getIntegerIfDefined(PROPERTY_LEFT,0));
		setRight(getIntegerIfDefined(PROPERTY_RIGHT,0));
		
		// Generate a shuffled table if needed
		if (getShuffleTableElements()==null || getShuffleTableElements().size()!=getTableElements().size())
		{
			generateShuffleTableElements(getTableElements(),isShuffleRows(),isShuffleCols());
		}
		
		// We use this stringbuffer to add dinamically styles
		StringBuffer style=new StringBuffer();
		
		Element eTable=qc.createElement("table");
		if (isBorder())
		{
			// I am not sure if it is useful because table borders are controlled with css styles
			// but I will leave it when borders are displayed
			eTable.setAttribute("border","1");
		}
		else
		{
			style.append("border:0px;");
		}
		if (isBackgroundEnabled())
		{
			style.append(convertCSS("background:%%COLOUR:innerbg%%;"));
		}
		if (style.length()>0)
		{
			eTable.setAttribute("style",style.toString());
			style.setLength(0);
		}
		eTable.setAttribute("cellpadding","4");
		addLangAttributes(eTable);
		qc.addInlineXHTML(eTable);
		int iTableElement = 0;
		if (getTitle()!=null) // title set
		{
			// create caption from title tag
			Element eTitle=XML.createChild(eTable,"caption");
			qc.setParent(eTitle);
			getTitle().produceOutput(qc,bInit,bPlain);
			qc.unsetParent();
		}
		Element eTBody=XML.createChild(eTable,"tbody");
		for (int iRow=0;iRow<getRows();iRow++) // create rows
		{
			TableRow tr=getShuffleTableElements().get(iTableElement++);
			Element eRow=XML.createChild(eTBody,"tr");
			Element eCell;
			for(int iCell=0;iCell<getCols();iCell++) // create cells
			{
				if (iRow<getHead()) // column heading
				{
					eCell = XML.createChild(eRow,"th");
					if (!isBorder())
					{
						style.append("border:0px;");
					}
					if (isBackgroundEnabled())
					{
						style.append(convertCSS("background:%%COLOUR:innerbghi%%;"));
					}
					if (style.length()>0)
					{
						eCell.setAttribute("style",style.toString());
						style.setLength(0);
					}
					eCell.setAttribute("scope","col");
				}
				else if (iCell < getLeft()) // row label
				{
					eCell = XML.createChild(eRow,"th");
					if (!isBorder())
					{
						style.append("border:0px;");
					}
					if (isBackgroundEnabled())
					{
						style.append(convertCSS("background:%%COLOUR:innerbghi%%;"));
					}
					if (style.length()>0)
					{
						eCell.setAttribute("style",style.toString());
						style.setLength(0);
					}
					eCell.setAttribute("scope","row");
					if (iRow>=(getRows()-getFoot()))
					{
						eCell.setAttribute("class","foot");
					}
					else
					{
						eCell.setAttribute("class","left");
					}
				}
				else // td cells
				{
					eCell=XML.createChild(eRow,"td");
					if (!isBorder())
					{
						style.append("border:0px;");
					}
					if (iRow>=(getRows()-getFoot()))
					{
						eCell.setAttribute("class","foot");
						if (isBackgroundEnabled())
						{
							style.append(convertCSS("background:%%COLOUR:innerbghi%%;"));
						}
					}
					else if (iCell>=(getCols()-getRight()))
					{
						eCell.setAttribute("class","right");
						if (isBackgroundEnabled())
						{
							style.append(convertCSS("background:%%COLOUR:innerbghi%%;"));
						}
					}
					if (style.length()>0)
					{
						eCell.setAttribute("style",style.toString());
						style.setLength(0);
					}
				}
				qc.setParent(eCell);
				if (iCell<tr.aqcCells.length)
				{
					tr.aqcCells[iCell].produceOutput(qc,bInit,bPlain);
				}
				qc.unsetParent();
			}
		}
	}
	
	/**
	 * @return Array of table elements (columns etc) after shuffle
	 */
	protected ArrayList<TableRow> getShuffleTableElements()
	{
		return shuffleTableElements;
	}
	
	/**
	 * Set array of table elements (columns etc) after shuffle.
	 * @param tableElements Array of table elements (columns etc) after shuffle
	 */
	protected void setShuffleTableElements(ArrayList<TableRow> shuffleTableElements)
	{
		this.shuffleTableElements=shuffleTableElements;
	}
	
	/**
	 * Initialize shufflable table elements and shuffles them if desired
	 * @param tableElements Unshuffled table elements
	 * @param bShuffleRows true to shuffle rows, false otherwise
	 * @param bShuffleCols true to shuffle cols, false otherwise
	 */
	protected void generateShuffleTableElements(ArrayList<TableRow> tableElements,boolean bShuffleRows,
		boolean bShuffleCols)
	{
		// We need to generate a shuffle map for rows
		int[] shufflerRowsMap=new int[getRows()];
		String fixedRows=getFixedRows();
		if (!bShuffleRows || fixedRows==null)
		{
			for (int i=0;i<shufflerRowsMap.length;i++)
			{
				shufflerRowsMap[i]=i;
			}
			
			// Shuffle rows if desired
			if (bShuffleRows)
			{
				// Get new RNG (means it's repeatable, and doesn't affect question's
				// other use of random numbers; does mean that two lists of same length
				// in same question will be shuffled in same way).
				// Note that this is true only if they share the same random group for rows
				Random random=getRandomGroupRows()==null?
					getQuestion().getRandom():getQuestion().getRandom(getRandomGroup(getRandomGroupRows()));
				Shuffler.shuffle(shufflerRowsMap,random);
			}
		}
		else
		{
			// First we get a list with fixed rows as integers
			List<Integer> fixedRowsList=new ArrayList<Integer>();
			String[] fixedRowsArr=fixedRows.split(",");
			for (String fixedRow:fixedRowsArr)
			{
				int row=Integer.parseInt(fixedRow);
				if (row<getRows() && !fixedRowsList.contains(Integer.valueOf(row)))
				{
					fixedRowsList.add(new Integer(row));
				}
			}
			
			// Init a shuffle map for rows without fixed rows (unshuffled)
			int[] notFixedShuffleRowsMap=new int[getRows()-fixedRowsList.size()];
			int iFixed=0;
			for (int i=0;i<getRows();i++)
			{
				if (!fixedRowsList.contains(Integer.valueOf(i)))
				{
					notFixedShuffleRowsMap[iFixed]=i;
					iFixed++;
				}
			}
			
			// Shuffle map for rows without fixed rows
			Random random=getRandomGroupRows()==null?
				getQuestion().getRandom():getQuestion().getRandom(getRandomGroup(getRandomGroupRows()));
			Shuffler.shuffle(notFixedShuffleRowsMap,random);
			
			// Get a shuffle map for rows with all rows
			int iNotFixed=0;
			for (int i=0;i<getRows();i++)
			{
				if (fixedRowsList.contains(Integer.valueOf(i)))
				{
					shufflerRowsMap[i]=i;
				}
				else
				{
					shufflerRowsMap[i]=notFixedShuffleRowsMap[iNotFixed];
					iNotFixed++;
				}
			}
		}
		
		// We need also to generate a shuffle map for cols
		int[] shufflerColsMap=new int[getCols()];
		String fixedCols=getFixedCols();
		if (!bShuffleCols || fixedCols==null)
		{
			for (int i=0;i<shufflerColsMap.length;i++)
			{
				shufflerColsMap[i]=i;
			}
			
			// Shuffle cols if desired
			if (bShuffleCols)
			{
				// Get new RNG (means it's repeatable, and doesn't affect question's
				// other use of random numbers; does mean that two lists of same length
				// in same question will be shuffled in same way).
				// Note that this is true only if they share the same random group for cols
				Random random=getRandomGroupCols()==null?
					getQuestion().getRandom():getQuestion().getRandom(getRandomGroup(getRandomGroupCols()));
				Shuffler.shuffle(shufflerColsMap,random);
			}
		}
		else
		{
			// First we get a list with fixed cols as integers
			List<Integer> fixedColsList=new ArrayList<Integer>();
			String[] fixedColsArr=fixedCols.split(",");
			for (String fixedCol:fixedColsArr)
			{
				int col=Integer.parseInt(fixedCol);
				if (col<getCols() && !fixedColsList.contains(Integer.valueOf(col)))
				{
					fixedColsList.add(new Integer(col));
				}
			}
			
			// Init a shuffle map for cols without fixed cols (unshuffled)
			int[] notFixedShuffleColsMap=new int[getCols()-fixedColsList.size()];
			int iFixed=0;
			for (int i=0;i<getCols();i++)
			{
				if (!fixedColsList.contains(Integer.valueOf(i)))
				{
					notFixedShuffleColsMap[iFixed]=i;
					iFixed++;
				}
			}
			
			// Shuffle map for cols without fixed cols
			Random random=getRandomGroupCols()==null?
				getQuestion().getRandom():getQuestion().getRandom(getRandomGroup(getRandomGroupCols()));
			Shuffler.shuffle(notFixedShuffleColsMap,random);
			
			// Get a shuffle map for cols with all cols
			int iNotFixed=0;
			for (int i=0;i<getCols();i++)
			{
				if (fixedColsList.contains(Integer.valueOf(i)))
				{
					shufflerColsMap[i]=i;
				}
				else
				{
					shufflerColsMap[i]=notFixedShuffleColsMap[iNotFixed];
					iNotFixed++;
				}
			}
		}
		
		// Now we need to generate the shuffled table
		setShuffleTableElements(new ArrayList<TableRow>());
		for (int iRow=0;iRow<getRows();iRow++)
		{
			TableRow shuffleRow=new TableRow();
			shuffleRow.aqcCells=new QComponent[getCols()];
			TableRow row=tableElements.get(shufflerRowsMap[iRow]);
			for (int iCol=0;iCol<getCols();iCol++)
			{
				shuffleRow.aqcCells[iCol]=row.aqcCells[shufflerColsMap[iCol]];
			}
			getShuffleTableElements().add(shuffleRow);
		}
	}
	
	/**
	 * @param randomGroup Identifier of the &lt;random&gt; component or name of the random group
	 * @return Random group the referenced &lt;random&gt; component or name of random group unchanged if no
	 * &lt;random&gt; component is referenced 
	 */
	private String getRandomGroup(String randomGroup) {
		QComponent qc=null;
		try
		{
			qc=getQDocument().find(randomGroup);
		}
		catch (OmDeveloperException e)
		{
			qc=null;
		}
		if (qc!=null && qc instanceof RandomComponent)
		{
			randomGroup=((RandomComponent)qc).getGroup();
		}
		return randomGroup;
	}
	
	/**
	 * Tests if this table component match all conditions to be displayed.
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if this table component match all conditions to be displayed, false otherwise
	 */
	@Override
	public boolean test(String idAnswer)
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this,idAnswer);
	}
	
	/**
	 * Tests if this table component match all conditions to be displayed.
	 * @return true if this table component match all conditions to be displayed, false otherwise
	 */
	@Override
	public boolean test()
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this);
	}
}
