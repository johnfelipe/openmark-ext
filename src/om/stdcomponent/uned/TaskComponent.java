package om.stdcomponent.uned;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.graph.GraphItem;
import om.graph.uned.Displayable;
import om.graph.uned.World;
import om.helper.uned.AnswerChecking;
import om.helper.uned.Tester;
import om.stdcomponent.uned.CanvasComponent.Marker;
import om.stdcomponent.uned.CanvasComponent.MarkerLine;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

// UNED: 12-07-2011 - dballestin
/**
 * This is a component used to define some tasks needed to execute during the question.<br/>
 * It can be declared everywhere inside question.xml and may not contain other content.<br/><br/>
 * <h2>Tasks</h2>
 * <table border="1">
 * <tr><th>Task</th><th>Description</th><th>Parameters</th></tr>
 * <tr><td>clear</td><td>Clear content from parameter components.</td><td>List of components 
 * (separated by ,) to clear.<br/>If no present all answer components will be cleared (if possible)
 * </td></tr>
 * <tr><td>clearwrong</td><td>Clear content from parameter components if they are wrong.</td>
 * <td>List of components (separated by ,) to clear if they are wrong.<br/>If no present all 
 * wrong answer components will be cleared (if possible)</td></tr>
 * <tr><td>displayright</td><td>Display right answer in parameter components.</td><td>
 * List of components (separated by ,) to set to display the right answer.<br/>If no present all 
 * answer components will be set to display the right answer (if possible)</td></tr>
 * <tr><td>highlighthits</td><td>Highlight hits in &lt;wordselect&gt; components.</td>
 * <td>List of components (separated by ,) to highlight hits.<br/>If no present all answer components 
 * will be highlighted hits (if possible)
 * </td></tr>
 * <tr><td>highlightright</td><td>Highlight right words in &lt;wordselect&gt; components.</td>
 * <td>List of components (separated by ,) to highlight right things.<br/>If no present all 
 * answer components will be highlighted right things (if possible)
 * </td></tr>
 * <tr><td>highlighthitsandright</td><td>Highlight hits and right words in &lt;wordselect&gt; 
 * components.</td><td>List of components (separated by ,) to highlight hits and right things.<br/>
 * If no present all answer components will be highlighted hits and right things (if possible)
 * </td></tr>
 * <tr><td>clearhighlight</td><td>Clear all highlighting in &lt;wordselect&gt; components</td>
 * <td>List of components (separated by ,) to clear highlighting.<br/>If no present all 
 * answer components will be cleared highlighting (if possible)
 * </td></tr>
 * <tr><td>displaygraph</td><td>Display indicated graph items at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>idCanvas</i>,<i>idWorld</i>,<i>idGraphItem1</i>,...,<i>idGraphItemn</i>
 * <br/>If no graph items are indicated all graph items of the indicated canvas and world are displayed.
 * <br/>If no world is indicated graph items from all worlds of the indicated canvas are displayed.
 * <br/>If no canvas is indicated graph items from all canvasses within the expected worlds are displayed.
 * </td></tr>
 * <tr><td>hidegraph</td><td>Hide indicated graph items at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>idCanvas</i>,<i>idWorld</i>,<i>idGraphItem1</i>,...,<i>idGraphItemn</i>
 * <br/>If no graph items are indicated all graph items of the indicated canvas and world are hidden.
 * <br/>If no world is indicated graph items from all worlds of the indicated canvas are hidden.
 * <br/>If no canvas is indicated graph items from all canvasses within the expected worlds are hidden.
 * </td></tr>
 * <tr><td>displayhidegraph</td><td>Display or hide indicated graph items at a 
 * &lt;canvas&gt; component</td><td>Expected syntax is: <i>displayOrHide</i>,<i>idCanvas</i>,
 * <i>idWorld</i>,<i>idGraphItem1</i>,...,<i>idGraphItemn</i>
 * <br/><i>displayOrHide</i> can be "yes" for displaying, "no" for hiding or a reference to a 
 * &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component with value "yes" or "no". 
 * It also allows placeholders if they are initialized.
 * <br/>If no graph items are indicated all graph items of the indicated canvas and world are displayed.
 * <br/>If no world is indicated graph items from all worlds of the indicated canvas are displayed.
 * <br/>If no canvas is indicated graph items from all canvasses within the expected worlds are displayed.
 * </td></tr>
 * <tr><td>displaymarker</td><td>Display indicated markers at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>idCanvas</i>,<i>indexMarker1</i>,...,<i>indexMarkern</i>
 * <br/>If no markers are indicated all markers of the indicated canvas are displayed.
 * <br/>If no canvas is indicated markers from all canvasses are displayed.</td></tr>
 * <tr><td>hidemarker</td><td>Hide indicated graph items at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>idCanvas</i>,<i>indexMarker1</i>,...,<i>indexMarkern</i>
 * <br/>If no markers are indicated all markers of the indicated canvas are hidden.
 * <br/>If no canvas is indicated markers from all canvasses are hidden.</td></tr>
 * <tr><td>displayhidemarker</td><td>Display or hide indicated markers at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>displayOrHide</i>,<i>idCanvas</i>,<i>indexMarker1</i>,...,<i>indexMarkern</i>
 * <br/><i>displayOrHide</i> can be "yes" for displaying, "no" for hiding or a reference to a 
 * &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component with value "yes" or "no". 
 * It also allows placeholders if they are initialized.
 * <br/>If no markers are indicated all markers of the indicated canvas are displayed.
 * <br/>If no canvas is indicated markers from all canvasses are displayed.</td></tr>
 * <tr><td>displaymarkerline</td><td>Display indicated markerlines at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>idCanvas</i>,<i>indexMarkerLine1</i>,...,<i>indexMarkerLinen</i>
 * <br/>If no markerlines are indicated all markerlines of the indicated canvas are displayed.
 * <br/>If no canvas is indicated markerlines from all canvasses are displayed.</td></tr>
 * <tr><td>hidemarkerline</td><td>Hide indicated graph items at a &lt;canvas&gt; component</td>
 * <td>Expected syntax is: <i>idCanvas</i>,<i>indexMarker1</i>,...,<i>indexMarkern</i>
 * <br/>If no markerlines are indicated all markerlines of the indicated canvas are hidden.
 * <br/>If no canvas is indicated markerlines from all canvasses are hidden.</td></tr>
 * <tr><td>displayhidemarkerline</td><td>Display or hide indicated markerlines at a 
 * &lt;canvas&gt; component</td><td>Expected syntax is: 
 * <i>displayOrHide</i>,<i>idCanvas</i>,<i>indexMarkerLine1</i>,...,<i>indexMarkerLinen</i>
 * <br/><i>displayOrHide</i> can be "yes" for displaying, "no" for hiding or a reference to a 
 * &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component with value "yes" or "no". 
 * It also allows placeholders if they are initialized.
 * <br/>If no markerlines are indicated all markerlines of the indicated canvas are displayed.
 * <br/>If no canvas is indicated markerlines from all canvasses are displayed.</td></tr>
 * <tr><td>resetvar</td><td>Reset &lt;random&gt;, &lt;replaceholder&gt; and/or &lt;variable&gt; components
 * </td><td>List of &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; components (separated by ,) 
 * to reset.<br/><br/>
 * It is possible to reference all &lt;random&gt; components with ++allrandoms++, 
 * all &lt;replaceholder&gt; components with ++allreplaceholders++, 
 * all &lt;variable&gt; components with ++allvariables++ or all together with ++all++</td></tr>
 * <tr><td>setvariable</td><td>Set a new value for a &lt;variable&gt; component</td>
 * <td>Identifier of &lt;variable&gt; component to set followed by a , and the new value to set.<br/>
 * Value can be a string literal (starting with @) or a reference to a &lt;random&gt;, 
 * &lt;replaceholder&gt; or &lt;variable&gt; component.<br/>
 * New value is always set as a string literal, even if that value has been got from a reference.</td></tr>
 * <tr><td>enable</td><td>Enable components</td><td>List of component's identifiers separated by
 * ,</td></tr>
 * <tr><td>disable</td><td>Enable components</td><td>List of component's identifiers separated by
 * ,</td></tr>
 * <tr><td>setenabled</td><td>Enable or disable components</td><td>A <i>displayOrHide</i> boolean
 * value followed by , and a list of component's identifiers also separated by,
 * <br/><i>displayOrHide</i> can be "yes" for enabling, "no" for disabling or a reference to a 
 * &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component with value "yes" or "no". 
 * It also allows placeholders if they are initialized.
 * </td></tr>
 * <tr><td>setproperty</td><td>Set new value to a property of a component</td>
 * <td>Identifier of component followed by , and the property's name and followed by , and the new value. 
 * It also allows placeholders if they are initialized.
 * </td></tr>
 * </table>
 * <br/>
 * <h2>When</h2>
 * <table border="1">
 * <tr><th>When</th><th>Description</th></tr>
 * <tr><td>start</td><td>This task will be executed at question's start, before first user attempt
 * to answer the question.</td></tr>
 * <tr><td>startattempt</td><td>This task will be executed at start of every user attempt to answer 
 * the question.</td></tr>
 * <tr><td>endattempt</td><td>This task will be executed at end of every user attempt to answer 
 * the question.</td></tr>
 * <tr><td>end</td><td>This task will be executed at question's end, after all user attempts to answer
 * the question.</td></tr>
 * <tr><td>custom[idCustom]</td><td>This task will be executed at execution of the custom action identified by
 * the selector (idCustom).</td></tr>
 * <tr><td>hint</td><td>This task will be executed when showing a hint (used by om.helper.uned.SimpleQuestion2).
 * </td></tr>
 * </table>
 * <br/>
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
 * <h2>XML usage</h2>
 * &lt;task task="<i>task's name</i>"<br/>
 * when="<b>start</b>|<b>startattempt</b>|<b>endattempt</b>|<b>end</b>|
 * <b>custom[</b><i>custom's action name</i><b>]</b>|<b>hint</b>"<br/> 
 * parameters="<i>task's parameters</i>" ... /&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, like the 
 * HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>task</td><td>(string)</td><td>Specifies the task's name to execute (view <b>Tasks</b>).
 * </td></tr>
 * <tr><td>when</td><td>(string)</td><td>Specifies when we want this task to execute (view <b>When</b>).
 * </td></tr>
 * <tr><td>parameters</td><td>(string)</td><td>Specifies the parameters used by the task.</td></tr>
 * <tr><td>test</td><td>(string)</td><td>Specifies the test to execute this task (see <b>Tests</b>).
 * </td></tr>
 * <tr><td>answer</td><td>(string)</td><td>Specifies the answer's components for testing if execute
 * or not this task.<br/> <br/>
 * This is a list with answer's component ids separated by comma ',' (OR operator) or plus symbol '+' 
 * (AND operator).<br/><br/>Moreover answer's component ids can include a selector using with the format
 * "id[selector]" used in some answer's components (e.g. editfields) to specify a single answer value
 * valid for them."</td></tr>
 * <tr><td>attemptsmin</td><td>(int)</td><td>Specifies the minimum number of attempts to execute
 * this task</td></tr>
 * <tr><td>attemptsmax</td><td>(int)</td><td>Specifies the maximum number of attempts to execute
 * this task</td></tr>
 * <tr><td>selectedanswersmin</td><td>(int)</td><td>Specifies the minimum number of selected answers 
 * to execute this task</td></tr>
 * <tr><td>selectedanswersmax</td><td>(int)</td><td>Specifies the maximum number of selected answers 
 * to execute this task</td></tr>
 * <tr><td>selectedrightanswersmin</td><td>(int)</td><td>Specifies the minimum number of selected right 
 * answers to execute this task</td></tr>
 * <tr><td>selectedrightanswersmax</td><td>(int)</td><td>Specifies the maximum number of selected right 
 * answers to execute this task</td></tr>
 * <tr><td>selectedwronganswersmin</td><td>(int)</td><td>Specifies the minimum number of selected wrong 
 * answers to execute this task</td></tr>
 * <tr><td>selectedwronganswersmax</td><td>(int)</td><td>Specifies the maximum number of selected wrong 
 * answers to execute this task</td></tr>
 * <tr><td>unselectedanswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * answers to execute this task</td></tr>
 * <tr><td>unselectedanswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * answers to execute this task</td></tr>
 * <tr><td>unselectedrightanswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * right answers to execute this task</td></tr>
 * <tr><td>unselectedrightanswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * right answers to execute this task</td></tr>
 * <tr><td>unselectedwronganswersmin</td><td>(int)</td><td>Specifies the minimum number of unselected 
 * wrong answers to execute this task</td></tr>
 * <tr><td>unselectedwronganswersmax</td><td>(int)</td><td>Specifies the maximum number of unselected 
 * wrong answers to execute this task</td></tr>
 * <tr><td>rightdistancemin</td><td>(int)</td><td>Specifies the minimum distance to the right answer 
 * to execute this task</td></tr>
 * <tr><td>rightdistancemax</td><td>(int)</td><td>Specifies the maximum distance to the right answer 
 * to execute this task</td></tr>
 * <tr><td>answertype</td><td>(string)</td><td>Specifies the answer's type to execute this task with 
 * editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the answertype property from the answer's component
 * </td></tr>
 * <tr><td>casesensitive</td><td>(string)</td><td>Specifies if the answer is considered to be 
 * case sensitive (yes) or case insensitive (no) to execute this task with editfield or advancedfield 
 * components.<br/>
 * If it is null (default value) then it is used the casesensitive property from the answer's component
 * </td></tr>
 * <tr><td>trim</td><td>(string)</td><td>Specifies if starting and ending whitespaces from answer are
 * ignored to execute this task with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the trim property from the answer's component</td></tr>
 * <tr><td>strip</td><td>(string)</td><td>Specifies if all whitespaces from answer are ignored 
 * to execute this task with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the strip property from the answer's component</td></tr>
 * <tr><td>singlespaces</td><td>(string)</td><td>Specifies if together whitespaces from answer 
 * are considered as a single whitespace to execute this task with editfield or advancedfield 
 * components.<br/>
 * If it is null (default value) then it is used the singlespaces property from the answer's component
 * </td></tr>
 * <tr><td>newlinespace</td><td>(string)</td><td>Specifies if new line characters are considered 
 * as whitespaces to execute this task with editfield or advancedfield components.<br/>
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
 * to execute this task with editfield or advancedfield components.<br/>
 * If it is null (default value) then it is used the ignoreemptylines property from the 
 * answer's component</td></tr>
 * <tr><td>tolerance</td><td>(string)</td><td>Specifies error tolerance used in numeric comparisons,
 * by default null</td></tr>
 * </table>
 */
public class TaskComponent extends QComponent implements Testable
{
	/** Task name for clearing answer components */
	public static final String TASK_CLEAR="clear";
	
	/** Task name for clearing answer components if they are wrong */
	public static final String TASK_CLEARWRONG="clearwrong";
	
	/** Task name for displaying right answer in answer components */
	public static final String TASK_DISPLAYRIGHT="displayright";
	
	/** Task name for highlighting hits in &lt;wordselect&gt; components */
	public static final String TASK_HIGHLIGHTHITS="highlighthits";
	
	/** Task name for highlighting right words in &lt;wordselect&gt; components */
	public static final String TASK_HIGHLIGHTRIGHT="highlightright";
	
	/** Task name for highlighting hits and right words in &lt;wordselect&gt; components */
	public static final String TASK_HIGHTLIGHTHITSANDRIGHT="highlighthitsandright";
	
	/** Task name for clear all highlighting in &lt;wordselect&gt; components */
	public static final String TASK_CLEARHIGHLIGHT="clearhighlight";
	
	/** Task name for displaying some graph items at a &lt;canvas&gt; component */
	public static final String TASK_DISPLAYGRAPH="displaygraph";
	
	/** Task name for hiding some graph items at a &lt;canvas&gt; component */
	public static final String TASK_HIDEGRAPH="hidegraph";
	
	/** Task name for displaying or hiding some graph items at a &lt;canvas&gt; component */
	public static final String TASK_DISPLAYHIDEGRAPH="displayhidegraph";
	
	/** Task name for displaying some markers at a &lt;canvas&gt; component */
	public static final String TASK_DISPLAYMARKER="displaymarker";
	
	/** Task name for hiding some markers at a &lt;canvas&gt; component */
	public static final String TASK_HIDEMARKER="hidemarker";
	
	/** Task name for displaying or hiding some markers at a &lt;canvas&gt; component */
	public static final String TASK_DISPLAYHIDEMARKER="displayhidemarker";
	
	/** Task name for displaying some marker lines at a &lt;canvas&gt; component */
	public static final String TASK_DISPLAYMARKERLINE="displaymarkerline";
	
	/** Task name for hiding some marker lines at a &lt;canvas&gt; component */
	public static final String TASK_HIDEMARKERLINE="hidemarkerline";
	
	/** Task name for displaying some marker lines at a &lt;canvas&gt; component */
	public static final String TASK_DISPLAYHIDEMARKERLINE="displayhidemarkerline";
	
	/** Task name to reset &lt;random&gt;, &lt;replaceholder&gt; and/or &lt;variable&gt; components */
	public static final String TASK_RESETVAR="resetvar";
	
	/** Task name to set a new value for a &lt;variable&gt; component */
	public static final String TASK_SETVARIABLE="setvariable";
	
	/** Task name to enable components */
	public static final String TASK_ENABLE="enable";
	
	/** Task name to disable components */
	public static final String TASK_DISABLE="disable";
	
	/** Task name to enable or disable components */
	public static final String TASK_SETENABLED="setenabled";
	
	/** Task name for setting a new value to a property of a component */
	public static final String TASK_SETPROPERTY="setproperty";
	
	/** 
	 * Name to indicate that the task will be executed at question's start, before first user attempt
	 * to answer the question
	 */
	public static final String WHEN_START="start";
	
	/** 
	 * Name to indicate that the task will be executed at start of every user attempt to answer 
	 * the question
	 */
	public static final String WHEN_STARTATTEMPT="startattempt";
	
	/** 
	 * Name to indicate that the task will be executed at end of every user attempt to answer 
	 * the question
	 */
	public static final String WHEN_ENDATTEMPT="endattempt";
	
	/** 
	 * Name to indicate that the task will be executed at question's end, after all user attempts 
	 * to answer the question
	 */
	public static final String WHEN_END="end";
	
	/**
	 * Name to indicate that the task will be executed from custom action if selector matches the
	 * custom arguments
	 */
	public static final String WHEN_CUSTOM="custom";
	
	/** 
	 * Name to indicate that the task will be executed when showing a hint 
	 * (used by om.helper.uned.SimpleQuestion2)
	 */
	public static final String WHEN_HINT="hint";
	
	/** Property name for the task's name to execute (string) */
	public static final String PROPERTY_TASK="task";
	
	/** Property name for declaring when we want this task to execute (String) */
	public static final String PROPERTY_WHEN="when";
	
	/** Property name for parameters used by the task (string) */ 
	public static final String PROPERTY_PARAMETERS="parameters";
	
	/** Character used as separator between parameters */
	private static final char PARAMETER_COMPONENTS_SEPARATOR=',';
	
	/** 
	 * Reserved word to reference all &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt;
	 * components
	 */
	private static final String VARS_ALL="++all++";
	
	/** Reserved word to reference all &lt;random&gt; components */
	private static final String VARS_ALLRANDOMS="++allrandoms++";
	
	/** Reserved word to reference all &lt;random&gt; components */
	private static final String VARS_ALLREPLACEHOLDERS="++allreplaceholders++";
	
	/** Reserved word to reference all &lt;random&gt; components */
	private static final String VARS_ALLVARIABLES="++allrandoms++";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_TASK,PROPERTY_WHEN,PROPERTY_PARAMETERS,
		PROPERTY_TEST,PROPERTY_ANSWER,PROPERTY_ATTEMPTSMIN,PROPERTY_ATTEMPTSMAX,PROPERTY_SELECTEDANSWERSMIN,
		PROPERTY_SELECTEDANSWERSMAX,PROPERTY_SELECTEDRIGHTANSWERSMIN,PROPERTY_SELECTEDRIGHTANSWERSMAX,
		PROPERTY_SELECTEDWRONGANSWERSMIN,PROPERTY_SELECTEDWRONGANSWERSMAX,PROPERTY_UNSELECTEDANSWERSMIN,
		PROPERTY_UNSELECTEDANSWERSMAX,PROPERTY_UNSELECTEDRIGHTANSWERSMIN,PROPERTY_UNSELECTEDRIGHTANSWERSMAX,
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
	
	/** @return Tag name (introspected; this may be replaced by a 1.5 annotation) */
	public static String getTagName()
	{
		return "task";
	}
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineString(PROPERTY_TASK);
		
		defineString(PROPERTY_WHEN);
		
		defineString(PROPERTY_PARAMETERS);
		setString(PROPERTY_PARAMETERS,null);
		
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
	
	/** @return Task's name to execute */
	public String getTask()
	{
		try
		{
			return getString(PROPERTY_TASK);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the task's name to execute
	 * @param task Task's name to execute
	 */
	public void setTask(String task)
	{
		try
		{
			setString(PROPERTY_TASK,task);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return Name than indicates when it is going to be executed this task */
	public String getWhen()
	{
		try
		{
			return getString(PROPERTY_WHEN);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the name for declaring when we want this task to execute
	 * @param when Name for declaring when we want this task to execute
	 */
	public void setWhen(String when)
	{
		try
		{
			setString(PROPERTY_WHEN,when);
		} catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** @return String with parameters used by the task */
	public String getParameters()
	{
		try
		{
			return getString(PROPERTY_PARAMETERS);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the parameters used by the task.
	 * @param parameters String with parameters used by the task
	 */
	public void setParameters(String parameters)
	{
		try
		{
			setString(PROPERTY_PARAMETERS,parameters);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	
	/** @return Test to execute this task */
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
	 * Set the test to execute this task.
	 * @param test Test to execute this task
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
	
	/** @return Answer's components to execute this task separated by comma ',' 
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
	 * Set the answer's components to execute this task separated by comma ',' 
	 * (OR operator) or plus symbol '+' (AND operator), set it to null if you want 
	 * to remove definition.
	 * @param answer Answer's components to execute this task
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
	
	/** @return Minimum number of attempts to execute this task */
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
	 * Set the minimum number of attempts to execute this task.
	 * @param attemptsMin Minimum number of attempts to execute this task
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
	
	/** @return Maximum number of attempts to execute this task */
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
	 * Set the maximum number of attempts to execute this task.
	 * @param attemptsMax Maximum number of attempts to execute this task
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
	
	/** @return Minimum number of selected answers to execute this task */
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
	 * Set the minimum number of selected answers to execute this task.
	 * @param selectedAnswersMin Minimum number of selected answers to execute this task
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
	
	/** @return Maximum number of selected answers to execute this task */
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
	 * Set the maximum number of selected answers to execute this task.
	 * @param selectedAnswersMax Maximum number of selected answers to execute this task
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
	
	/** @return Minimum number of selected right answers to execute this task */
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
	 * Set the minimum number of selected right answers to execute this task.
	 * @param selectedRightAnswersMin Minimum number of selected right answers to execute this task
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
	
	/** @return Maximum number of selected right answers to execute this task */
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
	 * Set the maximum number of selected right answers to execute this task.
	 * @param selectedRightAnswersMax Maximum number of selected right answers to execute this task
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
	
	/** @return Minimum number of selected wrong answers to execute this task */
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
	 * Set the minimum number of selected wrong answers to execute this task.
	 * @param selectedWrongAnswersMin Minimum number of selected wrong answers to execute this task
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
	
	/** @return Maximum number of selected wrong answers to execute this task */
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
	 * Set the maximum number of selected wrong answers to execute this task.
	 * @param selectedWrongAnswersMax Maximum number of selected wrong answers to execute this task
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
	
	/** @return Minimum number of unselected answers to execute this task */
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
	 * Set the minimum number of unselected answers to execute this task.
	 * @param unselectedAnswersMin Minimum number of unselected answers to execute this task
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
	
	/** @return Maximum number of unselected answers to execute this task */
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
	 * Set the maximum number of unselected answers to execute this task.
	 * @param unselectedAnswersMax Maximum number of unselected answers to execute this task
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
	
	/** @return Minimum number of unselected right answers to execute this task */
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
	 * Set the minimum number of unselected right answers to execute this task.
	 * @param unselectedRightAnswersMin Minimum number of selected right answers to execute this task
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
	
	/** @return Maximum number of unselected right answers to execute this task */
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
	 * Set the maximum number of unselected right answers to execute this task.
	 * @param unselectedRightAnswersMax Maximum number of unselected right answers to execute this task
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
	
	/** @return Minimum number of unselected wrong answers to execute this task */
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
	 * Set the minimum number of unselected wrong answers to execute this task.
	 * @param unselectedWrongAnswersMin Minimum number of unselected wrong answers to execute this task
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
	
	/** @return Maximum number of unselected wrong answers to execute this task */
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
	 * Set the maximum number of unselected wrong answers to execute this task.
	 * @param unselectedWrongAnswersMax Maximum number of unselected wrong answers to execute this task
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
	
	/** @return Minimum distance to the right answer to execute this task */
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
	 * Set the minimum distance to the right answer to execute this task.
	 * @param rightDistanceMin Minimum distance to the right answer to execute this task
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
	
	/** @return Maximum distance to the right answer to execute this task */
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
	 * Set the maximum distance to the right answer to execute this task.
	 * @param rightDistanceMax Maximum distance to the right answer to execute this task
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
	 * @return Answer's type to execute this task with editfield or advancedfield 
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
	 * Set the answer's type to execute this task with editfield or advancedfield components.<br/>
	 * If it is set to null (default value) then it will be used the answertype property from the 
	 * answer's component.
	 * @param answertype Answer's type to execute this task or null to use the answertype property from 
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
	 * @return "yes" if considering the answer to be case sensitive to execute this task with editfield 
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
	 * to execute this task with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the casesensitive property from the 
	 * answer's component (string)
	 * @param casesensitive "yes" if the answer is going to be considered case sensitive 
	 * to execute this task with editfield or advancedfield components, "no" if it is going 
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
	 * @return "yes" if starting and ending whitespaces from answer are ignored to execute this task 
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
	 * Set if starting and ending whitespaces from answer are ignored to execute this task 
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the trim property from the answer's component 
	 * (string)
	 * @param trim "yes" if starting and ending whitespaces from answer are going to be ignored 
	 * to execute this task with editfield or advancedfield components, 
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
	 * @return "yes" if all whitespaces from answer are ignored to execute this task with 
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
	 * Set if all whitespaces from answer are ignored to execute this task with editfield or 
	 * advancedfield components.<br/>
	 * If it is null (default value) then it is used the strip property from the answer's component 
	 * (string)
	 * @param strip "yes" if all whitespaces from answer are going to be ignored to execute this task 
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
	 * to execute this task with editfield or advancedfield components, 
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
	 * to execute this task with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the singlespaces property from the 
	 * answer's component (string)
	 * @param singlespaces "yes" if together whitespaces from answer are going to be considered as 
	 * single whitespaces to execute this task with editfield or advancedfield components, 
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
	 * to execute this task with editfield or advancedfield components, 
	 * "no" if they are not considered as whitespaces or null if it is used the newlinespace property 
	 * from the answer's component
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
	 * Set if new line characters from answer are considered as whitespaces to execute this task 
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the newlinespace property from the 
	 * answer's component (string)
	 * @param newlinespace "yes" if new line characters from answer are going to be considered as 
	 * whitespaces to execute this task with editfield or advancedfield components, 
	 * "no" if they are not going to be considered as whitespaces or null to use the newlinespace 
	 * property from the answer's component
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
	 * @return "yes" if empty lines from answer are ignored to execute this task with editfield 
	 * or advancedfield components, "no" if they are not ignored or null if it is used 
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
	 * Set if empty lines characters from answer are ignored to execute this task with editfield or 
	 * advancedfield components.<br/>
	 * If it is null (default value) then it is used the ignoreemptylines property from the 
	 * answer's component (string)
	 * @param ignoreemptylines "yes" if empty lines from answer are ignored to execute this task 
	 * with editfield or advancedfield components, "no" if they are not going to be ignored 
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
		if (!placeholders.containsKey(PROPERTY_TASK))
		{
			requiredAttributes.add(PROPERTY_TASK);
		}
		if (!placeholders.containsKey(PROPERTY_WHEN))
		{
			requiredAttributes.add(PROPERTY_WHEN);
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
			throw new OmFormatException("<task> may not contain other content");
		}
	}

	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
	}
	
	/**
	 * @return Array with answer components, if parameter property is null it will return all
	 * answer components
	 */
	private List<Answerable> getAnswerComponents()
	{
		List<Answerable> answerComponents=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		if (parameters==null)
		{
			answerComponents=sq.getAnswerComponents();
		}
		else
		{
			answerComponents=new ArrayList<Answerable>();
			List<String> idComponents=AnswerChecking.split(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			for (String idComponent:idComponents)
			{
				idComponent=AnswerChecking.replaceEscapeChars(idComponent);
				try
				{
					QComponent qc=sq.getComponent(idComponent);
					if (qc instanceof Answerable)
					{
						Answerable a=(Answerable)qc;
						if (a.isAnswerEnabled())
						{
							answerComponents.add((Answerable)qc);
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Component with id '");
							error.append(idComponent);
							error.append("' is not enabled to use as an answer component");
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Component with id '");
						error.append(idComponent);
						error.append("' is not an answer component");
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(idComponent);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
			}
		}
		return answerComponents;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Boolean value of argument or null if there is no a valid argument 
	 */
	private Boolean getBooleanArgument(String parameters)
	{
		Boolean b=null;
		if (parameters!=null && !parameters.equals(""))
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			String sArg=null;
			int iEndBooleanArg=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndBooleanArg==-1)
			{
				sArg=AnswerChecking.replaceEscapeChars(parameters);
			}
			else
			{
				sArg=AnswerChecking.replaceEscapeChars(parameters.substring(0,iEndBooleanArg));
			}
			QComponent qc=null;
			try
			{
				qc=sq.getComponent(sArg);
			}
			catch (OmDeveloperException e)
			{
				qc=null;
			}
			String sValue=null;
			if (qc!=null)
			{
				if (qc instanceof RandomComponent)
				{
					sValue=((RandomComponent)qc).getValue();
				}
				else if (qc instanceof ReplaceholderComponent)
				{
					try
					{
						sValue=((ReplaceholderComponent)qc).getReplacementValue();
					}
					catch (OmException e)
					{
						sValue=null;
					}
				}
				else if (qc instanceof VariableComponent)
				{
					sValue=((VariableComponent)qc).getValue();
				}
			}
			if (sValue==null)
			{
				sValue=sArg;
			}
			if (sValue.equals("yes"))
			{
				b=Boolean.TRUE;
			}
			else if (sValue.equals("no"))
			{
				b=Boolean.FALSE;
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Unexpected boolean value: ");
				error.append(sValue);
				throw new TaskFormatRuntimeException(error.toString());
			}
		}
		return b;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Identifier of canvas where some graph items are going to be displayed or
	 * hidden (with tasks <b>displaygraph</b> or <b>hidegraph</b>) 
	 * or null if it is not present in parameters
	 */
	private String getCanvasToDisplayOrHide(String parameters)
	{
		String canvasId=null;
		if (parameters!=null && !parameters.equals(""))
		{
			int iEndCanvasId=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndCanvasId==-1)
			{
				canvasId=AnswerChecking.replaceEscapeChars(parameters);
			}
			else
			{
				canvasId=AnswerChecking.replaceEscapeChars(parameters.substring(0,iEndCanvasId));
			}
			if (canvasId.equals(""))
			{
				canvasId=null;
			}
		}
		return canvasId;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Identifier of world where some graph items are going to be displayed or
	 * hidden (with tasks <b>displaygraph</b> or <b>hidegraph</b>) 
	 * or null if it is not present in parameters
	 */
	private String getWorldToDisplayOrHide(String parameters)
	{
		String worldId=null;
		if (parameters!=null)
		{
			int iStartWorldId=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			if (iStartWorldId!=-1)
			{
				int iEndWorldId=AnswerChecking.indexOfCharacter(parameters,
						PARAMETER_COMPONENTS_SEPARATOR,iStartWorldId,AnswerChecking.getEscapeSequences());
				if (iEndWorldId>iStartWorldId)
				{
					worldId=AnswerChecking.replaceEscapeChars(
							parameters.substring(iStartWorldId,iEndWorldId));
				}
				else
				{
					worldId=AnswerChecking.replaceEscapeChars(parameters.substring(iStartWorldId));
				}
				if (worldId.equals(""))
				{
					worldId=null;
				}
			}
		}
		return worldId;
	}
	
	/**
	 * @param parameters Parameters
	 * @return List of identifiers of graph items that are going to be displayed or
	 * hidden (with tasks <b>displaygraph</b> or <b>hidegraph</b>) 
	 * or null if they are not present in parameters
	 */
	private List<String> getGraphItemsToDisplayOrHide(String parameters)
	{
		List<String> graphItems=null;
		if (parameters!=null)
		{
			List<String> idGraphItems=AnswerChecking.split(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (idGraphItems.size()>2)
			{
				graphItems=new ArrayList<String>();
				for (int i=2;i<idGraphItems.size();i++)
				{
					graphItems.add(AnswerChecking.replaceEscapeChars(idGraphItems.get(i)));
				}
			}
		}
		return graphItems;
	}
	
	/**
	 * @param parameters Parameters
	 * @return List of indexes of markers or markerlines that are going to be displayed or
	 * hidden (with tasks <b>displaymarker</b>, <b>displaymarkerline</b>, <b>hidemarker</b> 
	 * or <b>hidemarkerline</b>) or null if they are not present in parameters
	 */
	private List<String> getMarkersOrMarkerLinesToDisplayOrHide(String parameters)
	{
		List<String> markers=null;
		if (parameters!=null)
		{
			List<String> indexesMarkers=AnswerChecking.split(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (indexesMarkers.size()>1)
			{
				markers=new ArrayList<String>();
				for (int i=1;i<indexesMarkers.size();i++)
				{
					markers.add(AnswerChecking.replaceEscapeChars(indexesMarkers.get(i)));
				}
			}
		}
		return markers;
	}
	
	/**
	 * @return &lt;random&gt; components referenced from parameters
	 */
	private List<RandomComponent> getRandomComponentsFromParameters() 
	{
		List<RandomComponent> randoms=new ArrayList<RandomComponent>();
		String parameters=getParameters();
		if (parameters!=null)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			List<String> parametersArr=
					AnswerChecking.split(parameters,PARAMETER_COMPONENTS_SEPARATOR,
					AnswerChecking.getEscapeSequences());
			for (String parameter:parametersArr)
			{
				parameter=AnswerChecking.replaceEscapeChars(parameter);
				if (parameter.equals(VARS_ALL) || parameter.equals(VARS_ALLRANDOMS))
				{
					randoms=sq.getRandoms();
					break;
				}
				else if (!parameters.equals(VARS_ALLREPLACEHOLDERS) && 
						!parameters.equals(VARS_ALLVARIABLES))
				{
					try
					{
						QComponent qc=sq.getComponent(parameter);
						if (qc instanceof RandomComponent)
						{
							if (!randoms.contains(qc))
							{
								randoms.add((RandomComponent)qc);
							}
						}
						else if (!(qc instanceof ReplaceholderComponent) && 
								!(qc instanceof VariableComponent))
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Component with id '");
							error.append(parameter);
							error.append("' is not a random, replaceholder or variable component");
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					catch (OmDeveloperException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Can't find a component with id: ");
						error.append(parameter);
						throw new TaskFormatRuntimeException(error.toString(),e);
					}
				}
			}
		}
		return randoms;
	}
	
	/**
	 * @return &lt;replaceholder&gt; components referenced from parameters
	 */
	private List<ReplaceholderComponent> getReplaceholderComponentsFromParameters()
	{
		List<ReplaceholderComponent> replaceholders=new ArrayList<ReplaceholderComponent>();
		String parameters=getParameters();
		if (parameters!=null)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			List<String> parametersArr=
				AnswerChecking.split(parameters,PARAMETER_COMPONENTS_SEPARATOR,
						AnswerChecking.getEscapeSequences());
			for (String parameter:parametersArr)
			{
				parameter=AnswerChecking.replaceEscapeChars(parameter);
				if (parameter.equals(VARS_ALL) || parameter.equals(VARS_ALLREPLACEHOLDERS))
				{
					replaceholders=sq.getReplaceholders();
					break;
				}
				else if (!parameter.equals(VARS_ALLRANDOMS) && 
						!parameter.equals(VARS_ALLVARIABLES))
				{
					try
					{
						QComponent qc=sq.getComponent(parameter);
						if (qc instanceof ReplaceholderComponent)
						{
							if (!replaceholders.contains(qc))
							{
								replaceholders.add((ReplaceholderComponent)qc);
							}
						}
						else if (!(qc instanceof RandomComponent) && 
								!(qc instanceof VariableComponent))
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Component with id '");
							error.append(parameter);
							error.append("' is not a random, replaceholder or variable component");
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					catch (OmDeveloperException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Can't find a component with id: ");
						error.append(parameter);
						throw new TaskFormatRuntimeException(error.toString(),e);
					}
				}
			}
		}
		return replaceholders;
	}
	
	/**
	 * @return &lt;variable&gt; components referenced from parameters
	 */
	private List<VariableComponent> getVariableComponentsFromParameters()
	{
		List<VariableComponent> variables=new ArrayList<VariableComponent>();
		String parameters=getParameters();
		if (parameters!=null)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			List<String> parametersArr=AnswerChecking.split(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			for (String parameter:parametersArr)
			{
				parameter=AnswerChecking.replaceEscapeChars(parameter);
				if (parameter.equals(VARS_ALL) || parameter.equals(VARS_ALLVARIABLES))
				{
					variables=sq.getVariables();
					break;
				}
				else if (!parameter.equals(VARS_ALLRANDOMS) && 
						!parameter.equals(VARS_ALLREPLACEHOLDERS))
				{
					try
					{
						QComponent qc=sq.getComponent(parameter);
						if (qc instanceof VariableComponent)
						{
							if (!variables.contains(qc))
							{
								variables.add((VariableComponent)qc);
							}
						}
						else if (!(qc instanceof RandomComponent) && 
								!(qc instanceof ReplaceholderComponent))
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Component with id '");
							error.append(parameter);
							error.append("' is not a random, replaceholder or variable component");
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					catch (OmDeveloperException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Can't find a component with id: ");
						error.append(parameter);
						throw new TaskFormatRuntimeException(error.toString(),e);
					}
				}
			}
		}
		return variables;
	}
	
	/**
	 * @return &lt;variable&gt; component to set from parameters
	 */
	private VariableComponent getVariableToSet()
	{
		VariableComponent variable=null;
		String parameters=getParameters();
		if (parameters==null || parameters.equals(""))
		{
			throw new TaskFormatRuntimeException(
					"<task>: Missing required variable identifier and value in parameters");
		}
		else
		{
			String variableId=null;
			int iEndVariableId=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndVariableId!=-1)
			{
				variableId=AnswerChecking.replaceEscapeChars(parameters.substring(0,iEndVariableId));
			}
			if (variableId==null)
			{
				StringBuffer error=new StringBuffer();
				error.append(
						"<task>: Missing , to separate variable identifier and value in parameters: ");
				error.append(parameters);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				StandardQuestion sq=(StandardQuestion)getQuestion();
				QComponent qc=null;
				try
				{
					qc=sq.getComponent(variableId);
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(variableId);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				if (qc instanceof VariableComponent)
				{
					variable=(VariableComponent)qc;
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(variableId);
					error.append("' is not a variable component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
		}
		return variable;
	}
	
	/**
	 * Get the value to set a &lt;variable&gt; component.<br/><br/>
	 * That value can be a string literal (starts with @) or a reference to the value of a 
	 * &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component.<br/><br/>
	 * Note that this value is get as a string literal even if it is got from a 
	 * &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer component.
	 * @return Value to set a &lt;variable&gt; component
	 */
	private String getValueToSet()
	{
		String value=null;
		String parameters=getParameters();
		if (parameters==null || parameters.equals(""))
		{
			throw new TaskFormatRuntimeException(
					"<task>: Missing required variable identifier and value in parameters");
		}
		else
		{
			int iStartValue=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			if (iStartValue<=0)
			{
				StringBuffer error=new StringBuffer();
				error.append(
						"<task>: Missing , to separate variable identifier and value in parameters: ");
				error.append(parameters);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else if (iStartValue>=parameters.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Missing required variable value in parameters: ");
				error.append(parameters);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				String sValue=AnswerChecking.replaceEscapeChars(parameters.substring(iStartValue));
				if (sValue.charAt(0)=='@')
				{
					value=sValue.substring(1);
				}
				else
				{
					StandardQuestion sq=(StandardQuestion)getQuestion();
					QComponent qc=null;
					try
					{
						qc=sq.getComponent(sValue);
					}
					catch (OmDeveloperException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Can't find a component with id: ");
						error.append(sValue);
						throw new TaskFormatRuntimeException(error.toString(),e);
					}
					if (qc instanceof RandomComponent)
					{
						value=((RandomComponent)qc).getValue();
					}
					else if (qc instanceof ReplaceholderComponent)
					{
						try
						{
							value=((ReplaceholderComponent)qc).getReplacementValue();
						}
						catch (OmException e)
						{
							throw new OmUnexpectedException(e.getMessage(),e);
						}
					}
					else if (qc instanceof VariableComponent)
					{
						value=((VariableComponent)qc).getValue();
					}
					else if (qc instanceof Answerable)
					{
						value=((Answerable)qc).getAnswerLine();
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Component with id '");
						error.append(sValue);
						error.append("' is not a random, replaceholder, variable or answer component");
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
			}
		}
		return value;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Get list of components from parameters
	 */
	private List<QComponent> getComponentsFromParameters(String parameters)
	{
		List<QComponent> components=new ArrayList<QComponent>();
		if (parameters!=null)
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			List<String> parametersArr=AnswerChecking.split(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (parametersArr.isEmpty())
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Missing required component identifier in parameters: ");
				error.append(getParameters());
				throw new TaskFormatRuntimeException(error.toString());
			}
			for (String parameter:parametersArr)
			{
				parameter=AnswerChecking.replaceEscapeChars(parameter);
				QComponent qc=null;
				try
				{
					qc=sq.getComponent(parameter);
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(parameter);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				components.add(qc);
			}
		}
		return components;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Get a component from parameters (first argument)
	 */
	private QComponent getComponentFromParameters(String parameters)
	{
		QComponent qc=null;
		if (parameters==null || parameters.equals(""))
		{
			StringBuffer error=new StringBuffer();
			error.append("<task>: Missing required component identifier in parameters: ");
			error.append(getParameters());
			throw new TaskFormatRuntimeException(error.toString());
		}
		else
		{
			String componentId=null;
			int iEndComponentId=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndComponentId!=-1)
			{
				componentId=AnswerChecking.replaceEscapeChars(parameters.substring(0,iEndComponentId));
			}
			if (componentId==null)
			{
				StringBuffer error=new StringBuffer();
				error.append(
						"<task>: Missing , to separate component identifier and rest of arguments in parameters: ");
				error.append(getParameters());
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				StandardQuestion sq=(StandardQuestion)getQuestion();
				try
				{
					qc=sq.getComponent(componentId);
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(componentId);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
			}
		}
		return qc;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Get propety's name from parameters (second argument)
	 */
	private String getPropertyFromParameters(String parameters)
	{
		String property=null;
		if (parameters==null || parameters.equals(""))
		{
			StringBuffer error=new StringBuffer();
			error.append("<task>: Missing required component identifier in parameters: ");
			error.append(getParameters());
			throw new TaskFormatRuntimeException(error.toString());
		}
		else
		{
			int iStartProperty=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			if (iStartProperty<=0)
			{
				StringBuffer error=new StringBuffer();
				error.append(
						"<task>: Missing , to separate component identifier and property name in parameters: ");
				error.append(getParameters());
				throw new TaskFormatRuntimeException(error.toString());
			}
			else if (iStartProperty>=parameters.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Missing required property name in parameters: ");
				error.append(getParameters());
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				int iEndProperty=AnswerChecking.indexOfCharacter(parameters,
						PARAMETER_COMPONENTS_SEPARATOR,iStartProperty,AnswerChecking.getEscapeSequences());
				if (iEndProperty>iStartProperty)
				{
					property=AnswerChecking.replaceEscapeChars(
							parameters.substring(iStartProperty,iEndProperty));
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append(
							"<task>: Missing , to separate property name and value in parameters: ");
					error.append(getParameters());
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
		}
		return property;
	}
	
	/**
	 * @param parameters Parameters
	 * @return Get propety's value from parameters (third argument)
	 */
	private String getValueFromParameters(String parameters)
	{
		String value=null;
		if (parameters==null || parameters.equals(""))
		{
			StringBuffer error=new StringBuffer();
			error.append("<task>: Missing required component identifier in parameters: ");
			error.append(getParameters());
			throw new TaskFormatRuntimeException(error.toString());
		}
		else
		{
			int iStartProperty=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			if (iStartProperty<=0)
			{
				StringBuffer error=new StringBuffer();
				error.append(
						"<task>: Missing , to separate component identifier and property name in parameters: ");
				error.append(getParameters());
				throw new TaskFormatRuntimeException(error.toString());
			}
			else if (iStartProperty>=parameters.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Missing required property name in parameters: ");
				error.append(getParameters());
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				int iStartValue=AnswerChecking.indexOfCharacter(parameters,PARAMETER_COMPONENTS_SEPARATOR,
						iStartProperty,AnswerChecking.getEscapeSequences())+1;
				if (iStartValue<=0)
				{
					StringBuffer error=new StringBuffer();
					error.append(
							"<task>: Missing , to separate property name and value in parameters: ");
					error.append(getParameters());
					throw new TaskFormatRuntimeException(error.toString());
				}
				else if (iStartValue>=parameters.length())
				{
					StringBuffer error=new StringBuffer();
					error.append(
							"<task>: Missing required property value in parameters: ");
					error.append(getParameters());
					throw new TaskFormatRuntimeException(error.toString());
				}
				else
				{
					String sValue=AnswerChecking.replaceEscapeChars(parameters.substring(iStartValue));
					if (sValue.charAt(0)=='@')
					{
						value=sValue.substring(1);
					}
					else
					{
						StandardQuestion sq=(StandardQuestion)getQuestion();
						QComponent qc=null;
						try
						{
							qc=sq.getComponent(sValue);
						}
						catch (OmDeveloperException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Can't find a component with id: ");
							error.append(sValue);
							throw new TaskFormatRuntimeException(error.toString(),e);
						}
						if (qc instanceof RandomComponent)
						{
							value=((RandomComponent)qc).getValue();
						}
						else if (qc instanceof ReplaceholderComponent)
						{
							try
							{
								value=((ReplaceholderComponent)qc).getReplacementValue();
							}
							catch (OmException e)
							{
								throw new OmUnexpectedException(e.getMessage(),e);
							}
						}
						else if (qc instanceof VariableComponent)
						{
							value=((VariableComponent)qc).getValue();
						}
						else if (qc instanceof Answerable)
						{
							value=((Answerable)qc).getAnswerLine();
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Component with id '");
							error.append(sValue);
							error.append("' is not a random, replaceholder, variable or answer component");
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
				}
			}
		}
		return value;
	}
	
	/**
	 * Clear component's value.<br/><br/>
	 * Note that &lt;jme&gt; components are not supported, so this task is ignored if you use it with one.
	 * @param a Answerable Component
	 */
	private void clear(Answerable a)
	{
		if (a instanceof RadioBoxComponent)
		{
			RadioBoxComponent radiobox=(RadioBoxComponent)a;
			radiobox.setChecked(false);
		}
		else if (a instanceof CheckboxComponent)
		{
			CheckboxComponent checkbox=(CheckboxComponent)a;
			checkbox.setChecked(false);
		}
		else if (a instanceof EditFieldComponent)
		{
			EditFieldComponent editfield=(EditFieldComponent)a;
			editfield.setValue("");
		}
		else if (a instanceof AdvancedFieldComponent)
		{
			AdvancedFieldComponent advancedfield=(AdvancedFieldComponent)a;
			advancedfield.setValue("");
		}
		else if (a instanceof DropdownComponent)
		{
			DropdownComponent dropdown=(DropdownComponent)a;
			List<String> options=dropdown.getAvailableOptions();
			if (!options.isEmpty())
			{
				dropdown.setSelected(options.get(0));
			}
		}
		else if (a instanceof DropBoxComponent)
		{
			DropBoxComponent dropbox=(DropBoxComponent)a;
			dropbox.clear();
		}
		else if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.clearSelection();
		}
		else if (a instanceof VariableComponent)
		{
			VariableComponent variable=(VariableComponent)a;
			variable.resetValue();
		}
	}
	
	/**
	 * Clear wrong component's value
	 * @param a Answerable Component
	 */
	private void clearWrong(Answerable a)
	{
		if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.clearWrongSelection();
		}
		else if (!a.isRight())
		{
			clear(a);
		}
	}
	
	/**
	 * Get a right dragboxes combo for dropboxes received.
	 * @param dropboxes Dropboxes
	 * @param dragboxes Available dragboxes
	 * @return Right dragboxes combo for dropboxes received
	 */
	private Map<DropBoxComponent,DragBoxComponent> getRightDropBoxCombo(
			List<DropBoxComponent> dropboxes,List<DragBoxComponent> dragboxes)
	{
		Map<DropBoxComponent,DragBoxComponent> rightDropBoxCombo=null;
		DropBoxComponent dropBox=dropboxes.get(0);
		String right=dropBox.getRight();
		if (right!=null)
		{
			String[] rightDragboxIds=right.split(DropBoxComponent.RIGHT_OR_SYMBOL);
			for (String rightDragBoxId:rightDragboxIds)
			{
				List<DropBoxComponent> dropboxesRemaining=new LinkedList<DropBoxComponent>();
				for (int i=1;i<dropboxes.size();i++)
				{
					dropboxesRemaining.add(dropboxes.get(i));
				}
				Map<DropBoxComponent,DragBoxComponent> rightDropBoxComboRemaining=null;
				if (rightDragBoxId.equals(DropBoxComponent.EMPTY_DRAGBOX_ID))
				{
					if (dropboxesRemaining.isEmpty())
					{
						rightDropBoxCombo=new LinkedHashMap<DropBoxComponent,DragBoxComponent>();
						rightDropBoxCombo.put(dropBox,null);
					}
					else
					{	
						rightDropBoxComboRemaining=getRightDropBoxCombo(dropboxesRemaining,dragboxes);
						if (rightDropBoxComboRemaining!=null)
						{
							rightDropBoxCombo=new LinkedHashMap<DropBoxComponent,DragBoxComponent>();
							rightDropBoxCombo.put(dropBox,null);
							for (Map.Entry<DropBoxComponent,DragBoxComponent> 
									rightResult:rightDropBoxComboRemaining.entrySet())
							{
								rightDropBoxCombo.put(rightResult.getKey(),rightResult.getValue());
							}
						}
					}
				}
				else
				{
					StandardQuestion sq=(StandardQuestion)getQuestion();
					try
					{
						QComponent qRight=sq.getComponent(rightDragBoxId);
						if (qRight instanceof DragBoxComponent)
						{
							if (dragboxes.contains(qRight))
							{
								List<DragBoxComponent> dragboxesRemaining=null;
								DragBoxComponent rightDragBox=(DragBoxComponent)qRight;
								if (rightDragBox.getBoolean(DragBoxComponent.PROPERTY_INFINITE))
								{
									dragboxesRemaining=dragboxes;
								}
								else
								{
									dragboxesRemaining=new LinkedList<DragBoxComponent>();
									for (DragBoxComponent dragbox:dragboxes)
									{
										if (!rightDragBox.equals(dragbox))
										{
											dragboxesRemaining.add(dragbox);
										}
									}
								}
								if (dropboxesRemaining.isEmpty())
								{
									rightDropBoxCombo=
											new LinkedHashMap<DropBoxComponent,DragBoxComponent>();
									rightDropBoxCombo.put(dropBox,rightDragBox);
								}
								else
								{	
									rightDropBoxComboRemaining=
											getRightDropBoxCombo(dropboxesRemaining,dragboxesRemaining);
									if (rightDropBoxComboRemaining!=null)
									{
										rightDropBoxCombo=
												new LinkedHashMap<DropBoxComponent,DragBoxComponent>();
										rightDropBoxCombo.put(dropBox,rightDragBox);
										for (Map.Entry<DropBoxComponent,DragBoxComponent> 
												rightResult:rightDropBoxComboRemaining.entrySet())
										{
											rightDropBoxCombo.put(rightResult.getKey(),
													rightResult.getValue());
										}
									}
								}
							}
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Component with id '");
							error.append(rightDragBoxId);
							error.append("' found in the 'right' attribute of the dropbox '");
							error.append(dropBox.getID());
							error.append("' is not a dragbox component");
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					catch (OmDeveloperException e)
					{
						try
						{
							StringBuffer error=new StringBuffer();
							error.append("<task>: Can't find a component with the id '");
							error.append(rightDragBoxId);
							error.append("' found in the 'right' attribute of the dropbox '");
							error.append(dropBox.getID());
							error.append('\'');
							throw new TaskFormatRuntimeException(error.toString(),e);
						}
						catch (OmDeveloperException e2)
						{
							throw new OmUnexpectedException(e.getMessage(),e2);
						}
					}
				}
			}
		}
		return rightDropBoxCombo;
	}
	
	/**
	 * Display right value for received component if possible.<br/><br/>
	 * Dropboxes are a special case because they can't be treated singly, so they will be treated
	 * in group.<br/><br/>
	 * &lt;jme&gt; and &lt;variable&gt; components are not supported by this task 
	 * so this task will be ignored with them
	 * @param a Answerable Component
	 */
	private void displayRight(Answerable a)
	{
		if (a instanceof RadioBoxComponent)
		{
			RadioBoxComponent radiobox=(RadioBoxComponent)a;
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (radiobox.isChecked() && !radiobox.isRight())
			{
				try
				{
					for (RadioBoxComponent r:
							sq.getRadioBoxes(radiobox.getString(RadioBoxComponent.PROPERTY_GROUP)))
					{
						if (!r.isChecked() && r.isRight())
						{
							radiobox.setChecked(false);
							r.setChecked(true);
							break;
						}
					}
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
			else if (!radiobox.isChecked() && radiobox.isRight())
			{
				try
				{
					for (RadioBoxComponent r:
							sq.getRadioBoxes(radiobox.getString(RadioBoxComponent.PROPERTY_GROUP)))
					{
						if (r.isChecked() && !r.isRight())
						{
							r.setChecked(false);
							radiobox.setChecked(true);
							break;
						}
					}
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
		}
		else if (a instanceof CheckboxComponent)
		{
			CheckboxComponent checkbox=(CheckboxComponent)a;
			checkbox.setChecked(checkbox.isRight());
		}
		else if (a instanceof EditFieldComponent)
		{
			EditFieldComponent editfield=(EditFieldComponent)a;
			if (!editfield.isRight())
			{
				if (editfield.getAnswerType().equals(EditFieldComponent.ANSWERTYPE_TEXT))
				{
					editfield.setValue(editfield.getRight());
				}
				else if (editfield.isDisplayableAnswer())
				{
					editfield.setValue(editfield.getDisplayableRight());
				}
			}
		}
		else if (a instanceof AdvancedFieldComponent)
		{
			AdvancedFieldComponent advancedfield=(AdvancedFieldComponent)a;
			if (!advancedfield.isRight())
			{
				if (advancedfield.getAnswerType().equals(AdvancedFieldComponent.ANSWERTYPE_TEXT))
				{
					advancedfield.setValue(advancedfield.getRight());
				}
				else if (advancedfield.isDisplayableAnswer())
				{
					advancedfield.setValue(advancedfield.getDisplayableRight());
				}
			}
		}
		else if (a instanceof DropdownComponent)
		{
			DropdownComponent dropdown=(DropdownComponent)a;
			if (!dropdown.isRight())
			{
				List<String> rightOptions=dropdown.getRightOptions();
				if (!rightOptions.isEmpty())
				{
					dropdown.setSelected(rightOptions.get(0));
				}
			}
		}
		else if (a instanceof DropBoxComponent)
		{
			// Dropboxes can't be processed individually for finding right value, 
			// because it's possible that two different dropboxes of the same group have a shared dragbox
			// in their list of right dragboxes identifiers and if we use that dragbox with one of them 
			// can occur that we can't use it with the other, so to get a right combination with
			// all dropboxes with a right dragbox dropped on it's not so simple.
			// We will need to use backtracking to solve this problem.
			DropBoxComponent dropbox=(DropBoxComponent)a;
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (!dropbox.isRight())
			{
				try
				{
					// Get the group
					String dropGroup=dropbox.getString(DropBoxComponent.PROPERTY_GROUP);
					
					// Get dropboxes from that group
					List<DropBoxComponent> dropboxesGroup=sq.getDropBoxes(dropGroup);
					
					// Get dropboxes from that group included in parameters
					List<DropBoxComponent> dropboxes=new LinkedList<DropBoxComponent>();
					List<Answerable> parameterComponents=getAnswerComponents();
					for (DropBoxComponent db:dropboxesGroup)
					{
						if (parameterComponents.contains(db))
						{
							dropboxes.add(db);
						}
					}
					
					// Get dragboxes available, we need to remove dragboxes dragged to other dropboxes
					// from the same group but not included in parameters
					List<DragBoxComponent> dragboxes=sq.getDragBoxes(dropGroup);
					for (DropBoxComponent db:dropboxesGroup)
					{
						if (!dropboxes.contains(db))
						{
							String value=db.getValue();
							if (value!=null && !value.equals(""))
							{
								QComponent qValue=sq.getComponent(value);
								if (qValue!=null && qValue instanceof DragBoxComponent)
								{
									DragBoxComponent dragbox=(DragBoxComponent)qValue;
									if (!dragbox.getBoolean(DragBoxComponent.PROPERTY_INFINITE))
									{
										dragboxes.remove(dragbox);
									}
								}
							}
						}
					}
					
					// Get right dragboxes combo for dropboxes from that group included in parameters
					Map<DropBoxComponent,DragBoxComponent> rightDropBoxCombo=
							getRightDropBoxCombo(dropboxes,dragboxes);
					if (rightDropBoxCombo!=null)
					{
						// Display right dragbox for every dropbox in the combo
						for (Map.Entry<DropBoxComponent,DragBoxComponent> 
								rightResult:rightDropBoxCombo.entrySet())
						{
							DropBoxComponent drop=rightResult.getKey();
							DragBoxComponent dragbox=rightResult.getValue();
							if (dragbox==null)
							{
								drop.clear();
							}
							else
							{
								drop.setValue(dragbox.getID());
							}
						}
					}
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
		}
		else if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.displayRightSelection();
		}
	}
	
	/**
	 * Highlight hits in received components if possible.<br/><br/>
	 * For the time being it is implemented only for &lt;wordselect&gt; components.
	 * @param a Answerable Component
	 */
	private void highlightHits(Answerable a)
	{
		if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.highlightCorrectSWWords();
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			QComponent qc=(QComponent)a;
			try
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'highlighthits' task is only supported for <wordselect> and ");
				if (qc.hasUserSetID())
				{
					error.append('\'');
					error.append(qc.getID());
					error.append("' is a <");
					
				}
				else
				{
					error.append("not for a <");
				}
				error.append(sq.getComponentTagName(qc));
				error.append(">");
				throw new TaskFormatRuntimeException(error.toString());
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'highlighthits' task is only supported for <wordselect>");
				throw new OmUnexpectedException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Highlight right things in received components if possible.<br/><br/>
	 * For the time being it is implemented only for &lt;wordselect&gt; components.
	 * @param a Answerable Component
	 */
	private void highlightRight(Answerable a)
	{
		if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.secondHilightSWWords();
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			QComponent qc=(QComponent)a;
			try
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'highlightright' task is only supported for <wordselect> and ");
				if (qc.hasUserSetID())
				{
					error.append('\'');
					error.append(qc.getID());
					error.append("' is a <");
					
				}
				else
				{
					error.append("not for a <");
				}
				error.append(sq.getComponentTagName(qc));
				error.append(">");
				throw new TaskFormatRuntimeException(error.toString());
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'highlightright' task is only supported for <wordselect>");
				throw new OmUnexpectedException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Highlight hits and right things in received components if possible.<br/><br/>
	 * For the time being it is implemented only for &lt;wordselect&gt; components.
	 * @param a Answerable Component
	 */
	private void highlightHitsAndRight(Answerable a)
	{
		if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.highlightCorrectandUnselectedSWWords();
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			QComponent qc=(QComponent)a;
			try
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'highlighthitsandright' task is only supported for <wordselect> and ");
				if (qc.hasUserSetID())
				{
					error.append('\'');
					error.append(qc.getID());
					error.append("' is a <");
					
				}
				else
				{
					error.append("not for a <");
				}
				error.append(sq.getComponentTagName(qc));
				error.append(">");
				throw new TaskFormatRuntimeException(error.toString());
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'highlighthitsandright' task is only supported for <wordselect>");
				throw new OmUnexpectedException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Clear all highlighting in received components if possible.<br/><br/>
	 * For the time being it is implemented only for &lt;wordselect&gt; components.
	 * @param a Answerable Component
	 */
	private void clearHighlight(Answerable a)
	{
		if (a instanceof WordSelectComponent)
		{
			WordSelectComponent wordselect=(WordSelectComponent)a;
			wordselect.clearHighlight();
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			QComponent qc=(QComponent)a;
			try
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'clearhighlight' task is only supported for <wordselect> and ");
				if (qc.hasUserSetID())
				{
					error.append('\'');
					error.append(qc.getID());
					error.append("' is a <");
					
				}
				else
				{
					error.append("not for a <");
				}
				error.append(sq.getComponentTagName(qc));
				error.append(">");
				throw new TaskFormatRuntimeException(error.toString());
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> 'clearhighlight' task is only supported for <wordselect>");
				throw new OmUnexpectedException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Display some graph items from a world in a &lt;canvas&gt; component.<br/><br/>
	 * If there are no graph items present in parameters all graph items from this world
	 * will be displayed.
	 * @param world World
	 * @param parameters Parameters
	 * @return true if at least one graph item has been displayed, false otherwise
	 */
	private boolean displayGraphItems(World world,String parameters)
	{
		boolean display=false;
		List<String> graphItems=getGraphItemsToDisplayOrHide(parameters);
		if (graphItems==null)
		{
			for (GraphItem graphItem:world.getItems())
			{
				if (graphItem instanceof Displayable)
				{
					((Displayable)graphItem).setDisplay(true);
					display=true;
				}
			}
		}
		else
		{
			for (String gIt:graphItems)
			{
				try
				{
					GraphItem graphItem=world.getItem(gIt);
					if (graphItem instanceof Displayable)
					{
						((Displayable)graphItem).setDisplay(true);
						display=true;
					}
				}
				catch (IllegalArgumentException e)
				{
				}
			}
		}
		return display;
	}
	
	/**
	 * Hide some graph items from a world in a &lt;canvas&gt; component.<br/><br/>
	 * If there are no graph items present in parameters all graph items from this world
	 * will be hidden.
	 * @param world World
	 * @param parameters Parameters
	 * @return true if at least one graph item has been hidden, false otherwise
	 */
	private boolean hideGraphItems(World world,String parameters)
	{
		boolean hide=false;
		List<String> graphItems=getGraphItemsToDisplayOrHide(parameters);
		if (graphItems==null)
		{
			for (GraphItem graphItem:world.getItems())
			{
				if (graphItem instanceof Displayable)
				{
					((Displayable)graphItem).setDisplay(false);
					hide=true;
				}
			}
		}
		else
		{
			for (String gIt:graphItems)
			{
				try
				{
					GraphItem graphItem=world.getItem(gIt);
					if (graphItem instanceof Displayable)
					{
						((Displayable)graphItem).setDisplay(false);
						hide=true;
					}
				}
				catch (IllegalArgumentException e)
				{
				}
			}
		}
		return hide;
	}
	
	/**
	 * Display some graph items from a &lt;canvas&gt; component.<br/><br/>
	 * If there are no graph items present in parameters all graph items from this canvas
	 * inside the world present in parameters will be displayed.<br/><br/>
	 * If the world is not present in parameters the graph items indicated will be displayed
	 * in all worlds from this canvas.
	 * @param canvas Canvas
	 * @param parameters Parameters
	 */
	private void displayGraphItems(CanvasComponent canvas,String parameters)
	{
		boolean display=false;
		String world=getWorldToDisplayOrHide(parameters);
		if (world==null)
		{
			for (om.graph.World w:canvas.getWorlds())
			{
				display=display || displayGraphItems((World)w,parameters);
			}
		}
		else
		{
			try
			{
				display=display || displayGraphItems((World)canvas.getWorld(world),parameters);
			}
			catch (OmDeveloperException e)
			{
			}
		}
		if (display)
		{
			try
			{
				canvas.repaint();
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Hide some graph items from a &lt;canvas&gt; component.<br/><br/>
	 * If there are no graph items present in parameters all graph items from this canvas
	 * inside the world present in parameters will be hidden.<br/><br/>
	 * If the world is not present in parameters the graph items indicated will be hidden
	 * in all worlds from this canvas.
	 * @param canvas Canvas
	 * @param parameters Parameters
	 */
	private void hideGraphItems(CanvasComponent canvas,String parameters)
	{
		boolean hide=false;
		String world=getWorldToDisplayOrHide(parameters);
		if (world==null)
		{
			for (om.graph.World w:canvas.getWorlds())
			{
				hide=hide || hideGraphItems((World)w,parameters);
			}
		}
		else
		{
			try
			{
				hide=hide || hideGraphItems((World)canvas.getWorld(world),parameters);
			}
			catch (OmDeveloperException e)
			{
			}
		}
		if (hide)
		{
			try
			{
				canvas.repaint();
			}
			catch (OmDeveloperException e)
			{
			}
		}
	}
	
	/**
	 * Display some markers from a &lt;canvas&gt; component.<br/><br/>
	 * If there are no markers present in parameters all markers from this canvas 
	 * will be displayed.
	 * @param canvas Canvas
	 * @param parameters Parameters
	 */
	private void displayMarkers(CanvasComponent canvas,String parameters)
	{
		boolean display=false;
		List<String> markers=getMarkersOrMarkerLinesToDisplayOrHide(parameters);
		if (markers==null)
		{
			for (om.stdcomponent.CanvasComponent.Marker m:canvas.getMarkers())
			{
				((Marker)m).setDisplay(true);
				display=true;
			}
		}
		else
		{
			for (String iM:markers)
			{
				try
				{
					int indexMarker=Integer.parseInt(iM);
					if (indexMarker>=0 && indexMarker<canvas.getMarkers().size())
					{
						Marker m=(Marker)canvas.getMarkers().get(indexMarker);
						m.setDisplay(true);
						display=true;
					}
				}
				catch (NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task> Marker's index '");
					error.append(iM);
					error.append("' is not a valid number");
					throw new TaskFormatRuntimeException(error.toString(),nfe);
				}
			}
		}
		if (display)
		{
			try
			{
				canvas.repaint();
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Hide some markers from a &lt;canvas&gt; component.<br/><br/>
	 * If there are no markers present in parameters all markers from this canvas
	 * will be hidden.
	 * @param canvas Canvas
	 * @param parameters Parameters
	 */
	private void hideMarkers(CanvasComponent canvas,String parameters)
	{
		boolean hide=false;
		List<String> markers=getMarkersOrMarkerLinesToDisplayOrHide(parameters);
		if (markers==null)
		{
			for (om.stdcomponent.CanvasComponent.Marker m:canvas.getMarkers())
			{
				((Marker)m).setDisplay(false);
				hide=true;
			}
		}
		else
		{
			for (String iM:markers)
			{
				try
				{
					int indexMarker=Integer.parseInt(iM);
					if (indexMarker>=0 && indexMarker<canvas.getMarkers().size())
					{
						Marker m=(Marker)canvas.getMarkers().get(indexMarker);
						m.setDisplay(false);
						hide=true;
					}
				}
				catch (NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task> Marker's index '");
					error.append(iM);
					error.append("' is not a valid number");
					throw new TaskFormatRuntimeException(error.toString(),nfe);
				}
			}
		}
		if (hide)
		{
			try
			{
				canvas.repaint();
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Display some markerlines from a &lt;canvas&gt; component.<br/><br/>
	 * If there are no markerlines present in parameters all markerlines from this canvas 
	 * will be displayed.
	 * @param canvas Canvas
	 * @param parameters Parameters
	 */
	private void displayMarkerLines(CanvasComponent canvas,String parameters)
	{
		boolean display=false;
		List<String> markerlines=getMarkersOrMarkerLinesToDisplayOrHide(parameters);
		if (markerlines==null)
		{
			for (om.stdcomponent.CanvasComponent.MarkerLine ml:canvas.getLines())
			{
				((MarkerLine)ml).setDisplay(true);
				display=true;
			}
		}
		else
		{
			for (String iML:markerlines)
			{
				try
				{
					int indexMarkerLine=Integer.parseInt(iML);
					if (indexMarkerLine>=0 && indexMarkerLine<canvas.getLines().size())
					{
						MarkerLine ml=(MarkerLine)canvas.getLines().get(indexMarkerLine);
						ml.setDisplay(true);
						display=true;
					}
				}
				catch (NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task> Marker line's index '");
					error.append(iML);
					error.append("' is not a valid number");
					throw new TaskFormatRuntimeException(error.toString(),nfe);
				}
			}
		}
		if (display)
		{
			try
			{
				canvas.repaint();
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Hide some markerlines from a &lt;canvas&gt; component.<br/><br/>
	 * If there are no markerlines present in parameters all markerlines from this canvas
	 * will be hidden.
	 * @param canvas Canvas
	 * @param parameters Parameters
	 */
	private void hideMarkerLines(CanvasComponent canvas,String parameters)
	{
		boolean hide=false;
		List<String> markerlines=getMarkersOrMarkerLinesToDisplayOrHide(parameters);
		if (markerlines==null)
		{
			for (om.stdcomponent.CanvasComponent.MarkerLine ml:canvas.getLines())
			{
				((MarkerLine)ml).setDisplay(false);
				hide=true;
			}
		}
		else
		{
			for (String iML:markerlines)
			{
				try
				{
					int indexMarkerLine=Integer.parseInt(iML);
					if (indexMarkerLine>=0 && indexMarkerLine<canvas.getLines().size())
					{
						MarkerLine ml=(MarkerLine)canvas.getLines().get(indexMarkerLine);
						ml.setDisplay(false);
						hide=true;
					}
				}
				catch (NumberFormatException nfe)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task> Marker line's index '");
					error.append(iML);
					error.append("' is not a valid number");
					throw new TaskFormatRuntimeException(error.toString(),nfe);
				}
			}
		}
		if (hide)
		{
			try
			{
				canvas.repaint();
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Reset a &lt;random&gt; component.<br/><br/>
	 * It means that a new random value will be generated for this &lt;random&gt; component.
	 * @param random &lt;random&gt; component
	 */
	private void resetRandom(RandomComponent random)
	{
		random.generate();
	}
	
	/**
	 * Reset a &lt;replaceholder&gt; component.<br/><br/>
	 * It means that the placeholder referenced by the &lt;replaceholder&gt; component will be set
	 * with the new replacement value of the &lt;replaceholder&gt; component.
	 * @param replaceholder &lt;replaceholder&gt; component
	 */
	private void resetReplaceholder(ReplaceholderComponent replaceholder)
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String replaceholderValue=null;
		try
		{
			replaceholderValue=replaceholder.getReplacementValue();
		}
		catch (OmException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
		sq.setPlaceholder(replaceholder.getPlaceholder(),replaceholderValue);
	}
	
	/**
	 * Reset a &lt;variable&gt; component.<br/><br/>
	 * It means that the next time that this &lt;variable&gt; component will be referenced its value
	 * will be recalculated. 
	 * @param variable &lt;variable&gt; component
	 */
	private void resetVariable(VariableComponent variable)
	{
		variable.resetValue();
	}
	
	/**
	 * Enables a component
	 * @param qc Component
	 */
	private void enableComponent(QComponent qc)
	{
		try
		{
			qc.setBoolean(PROPERTY_ENABLED,true);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Disables a component
	 * @param qc Component
	 */
	private void disableComponent(QComponent qc)
	{
		try
		{
			qc.setBoolean(PROPERTY_ENABLED,false);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * @param value Value as string
	 * @return Integer value or null if value is not a valid integer
	 */
	private Integer getIntegerValue(String value)
	{
		Integer iValue=null;
		try
		{
			iValue=new Integer(value);
		}
		catch (NumberFormatException e)
		{
			iValue=null;
		}
		return iValue;
	}
	
	/**
	 * @param value Value as string
	 * @return true if value is "yes", false if value is "no", null otherwise
	 */
	private Boolean getBooleanValue(String value)
	{
		return value.equals("yes")?Boolean.TRUE:value.equals("no")?Boolean.FALSE:null;
	}
	
	/**
	 * @param value Value as string
	 * @return Double value or null if value is not a valid double
	 */
	private Double getDoubleValue(String value)
	{
		Double dValue=null;
		try
		{
			dValue=new Double(value);
		}
		catch (NumberFormatException e)
		{
			dValue=null;
		}
		return dValue;
	}
	
	/**
	 * Sets new value to a component's property.<br/><br/>
	 * Note that at the moment it is only implemented for string, integer, boolean and double properties.
	 * @param qc Component
	 * @param property Property's name
	 * @param value New value for property
	 */
	private void setProperty(QComponent qc,String property,String value)
	{
		if (qc.isPropertyDefined(property))
		{
			// Get property's type
			Class<?> propertyType=null;
			try
			{
				propertyType=qc.getPropertyType(property);
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
			// At the moment we only know to set string, integer, boolean and double properties
			if (propertyType==String.class || propertyType==Integer.class || 
					propertyType==Boolean.class || propertyType==Double.class)
			{
				// Try to find a public setter to set property
				StringBuffer setterName=new StringBuffer("set");
				setterName.append(property);
				Method[] methods=qc.getClass().getMethods();
				Method setter=null;
				for (int iMethod=0;iMethod<methods.length;iMethod++)
				{
					if (methods[iMethod].getName().equalsIgnoreCase(setterName.toString()))
					{
						// Check modifiers; ignore non-public, or static, methods
						int iMod=methods[iMethod].getModifiers();
						if (!Modifier.isPublic(iMod) || Modifier.isStatic(iMod))
						{
							continue;
						}
						
						// Ignore methods that don't take a single parameter
						Class<?>[] ac=methods[iMethod].getParameterTypes();
						if (ac.length!=1)
						{
							continue;
						}
						
						// Ignore methods that its argument is not one from the expected types
						if (propertyType==String.class && ac[0]!=String.class)
						{
							continue;
						}
						else if (propertyType==Integer.class && !ac[0].getName().equals("int"))
						{
							continue;
						}
						else if (propertyType==Boolean.class && !ac[0].getName().equals("boolean"))
						{
							continue;
						}
						else if (propertyType==Double.class && !ac[0].getName().equals("double"))
						{
							continue;
						}
						
						setter=methods[iMethod];
						break;
					}
				}
				if (setter==null)
				{
					// Not found setter, so we will set property with setString, setInteger, setBoolean
					// or setDouble method
					if (propertyType==String.class)
					{
						try
						{
							qc.setString(property,value);
						}
						catch (OmDeveloperException e)
						{
							throw new OmUnexpectedException(e.getMessage(),e);
						}
					}
					else if (propertyType==Integer.class)
					{
						Integer iValue=getIntegerValue(value);
						if (iValue==null)
						{
							try
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of the component '");
								error.append(qc.getID());
								error.append("' with the value '");
								error.append(value);
								error.append("' because it is expected an integer value");
								throw new TaskFormatRuntimeException(error.toString());
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of referenced component with the value '");
								error.append(value);
								error.append("' because it is expected an integer value");
								throw new TaskFormatRuntimeException(error.toString(),e);
							}
						}
						else
						{
							try
							{
								qc.setInteger(property,iValue.intValue());
							}
							catch (OmDeveloperException e)
							{
								throw new OmUnexpectedException(e.getMessage(),e);
							}
						}
					}
					else if (propertyType==Boolean.class)
					{
						Boolean bValue=getBooleanValue(value);
						if (bValue==null)
						{
							try
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of the component '");
								error.append(qc.getID());
								error.append("' with the value '");
								error.append(value);
								error.append("' because it is expected a boolean value (yes|no)");
								throw new TaskFormatRuntimeException(error.toString());
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of referenced component with the value '");
								error.append(value);
								error.append("' because it is expected a boolean value (yes|no)");
								throw new TaskFormatRuntimeException(error.toString(),e);
							}
						}
						else
						{
							try
							{
								qc.setBoolean(property,bValue.booleanValue());
							}
							catch (OmDeveloperException e)
							{
								throw new OmUnexpectedException(e.getMessage(),e);
							}
						}
					}
					else if (propertyType==Double.class)
					{
						Double dValue=getDoubleValue(value);
						if (dValue==null)
						{
							try
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of the component '");
								error.append(qc.getID());
								error.append("' with the value '");
								error.append(value);
								error.append("' because it is expected a double value");
								throw new TaskFormatRuntimeException(error.toString());
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of referenced component with the value '");
								error.append(value);
								error.append("' because it is expected a double value");
								throw new TaskFormatRuntimeException(error.toString(),e);
							}
						}
						else
						{
							try
							{
								qc.setDouble(property,dValue.doubleValue());
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
					// Found setter, so we will use it to set property
					Object[] params=null;
					if (propertyType==String.class)
					{
						params=new Object[]{value};
					}
					else if (propertyType==Integer.class)
					{
						Integer iValue=getIntegerValue(value);
						if (iValue==null)
						{
							try
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of the component '");
								error.append(qc.getID());
								error.append("' with the value '");
								error.append(value);
								error.append("' because it is expected an integer value");
								throw new TaskFormatRuntimeException(error.toString());
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of referenced component with the value '");
								error.append(value);
								error.append("' because it is expected an integer value");
								throw new TaskFormatRuntimeException(error.toString(),e);
							}
						}
						else
						{
							params=new Object[]{iValue};
						}
					}
					else if (propertyType==Boolean.class)
					{
						Boolean bValue=getBooleanValue(value);
						if (bValue==null)
						{
							try
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of the component '");
								error.append(qc.getID());
								error.append("' with the value '");
								error.append(value);
								error.append("' because it is expected a boolean value (yes|no)");
								throw new TaskFormatRuntimeException(error.toString());
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of referenced component with the value '");
								error.append(value);
								error.append("' because it is expected a boolean value (yes|no)");
								throw new TaskFormatRuntimeException(error.toString(),e);
							}
						}
						else
						{
							params=new Object[]{bValue};
						}
					}
					else if (propertyType==Double.class)
					{
						Double dValue=getDoubleValue(value);
						if (dValue==null)
						{
							try
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of the component '");
								error.append(qc.getID());
								error.append("' with the value '");
								error.append(value);
								error.append("' because it is expected a double value");
								throw new TaskFormatRuntimeException(error.toString());
							}
							catch (OmDeveloperException e)
							{
								StringBuffer error=new StringBuffer();
								error.append("<task> Can't set property '");
								error.append(property);
								error.append("' of referenced component with the value '");
								error.append(value);
								error.append("' because it is expected a double value");
								throw new TaskFormatRuntimeException(error.toString(),e);
							}
						}
						else
						{
							params=new Object[]{dValue};
						}
					}
					try
					{
						setter.invoke(qc,params);
					}
					catch (Throwable t)
					{
						throw new OmUnexpectedException(t.getMessage(),t);
					}
				}
			}
			else
			{
				try
				{
					StringBuffer error=new StringBuffer();
					error.append("<task> Can't set property '");
					error.append(property);
					error.append("' of the component '");
					error.append(qc.getID());
					error.append("' because its type is not supported by 'setproperty' task. ");
					error.append("Supported property types are 'string','integer','boolean' and 'double'");
					throw new TaskFormatRuntimeException(error.toString());
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task> Can't set property '");
					error.append(property);
					error.append("' of referenced component because its type is not supported by 'setproperty' task. ");
					error.append("Supported property types are 'string','integer','boolean' and 'double'");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
		}
		else
		{
			try
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> Component '");
				error.append(qc.getID());
				error.append("' doesn't have the property '");
				error.append(property);
				error.append('\'');
				throw new TaskFormatRuntimeException(error.toString());
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task> Referenced component doesn't have the property '");
				error.append(property);
				error.append('\'');
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "clear" task.
	 */
	private void executeClear()
	{
		for (Answerable a:getAnswerComponents())
		{
			clear(a);
		}
	}
	
	/**
	 * Execute "clearwrong" task
	 */
	private void executeClearWrong()
	{
		for (Answerable a:getAnswerComponents())
		{
			clearWrong(a);
		}
	}
	
	/**
	 * Execute "displayright" task
	 */
	private void executeDisplayRight()
	{
		for (Answerable a:getAnswerComponents())
		{
			
			displayRight(a);
		}
	}
	
	/**
	 * Execute "highlighthits" task
	 */
	private void executeHighlightHits()
	{
		for (Answerable a:getAnswerComponents())
		{
			
			highlightHits(a);
		}
	}
	
	/**
	 * Execute "highlightright" task
	 */
	private void executeHighlightRight()
	{
		for (Answerable a:getAnswerComponents())
		{
			highlightRight(a);
		}
	}
	
	/**
	 * Execute "highlighthitsandright" task
	 */
	private void executeHighlightHitsAndRight()
	{
		for (Answerable a:getAnswerComponents())
		{
			highlightHitsAndRight(a);
		}
	}
	
	/**
	 * Execute "clearhighlight" task
	 */
	private void executeClearHighlight()
	{
		for (Answerable a:getAnswerComponents())
		{
			clearHighlight(a);
		}
	}
	
	/**
	 * Execute "displaygraph" task
	 */
	private void executeDisplayGraph()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		String canvas=getCanvasToDisplayOrHide(parameters);
		if (canvas==null)
		{
			for (CanvasComponent c:sq.getCanvasses())
			{
				displayGraphItems(c,parameters);
			}
		}
		else
		{
			try
			{
				QComponent qc=sq.getComponent(canvas);
				if (qc instanceof CanvasComponent)
				{
					displayGraphItems((CanvasComponent)qc,parameters);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(canvas);
					error.append("' is not a canvas component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Can't find a component with id: ");
				error.append(canvas);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "hidegraph" task
	 */
	private void executeHideGraph()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		String canvas=getCanvasToDisplayOrHide(parameters);
		if (canvas==null)
		{
			for (CanvasComponent c:sq.getCanvasses())
			{
				hideGraphItems(c,parameters);
			}
		}
		else
		{
			try
			{
				QComponent qc=sq.getComponent(canvas);
				if (qc instanceof CanvasComponent)
				{
					hideGraphItems((CanvasComponent)qc,parameters);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(canvas);
					error.append("' is not a canvas component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Can't find a component with id: ");
				error.append(canvas);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "displayhidegraph" task
	 */
	private void executeDisplayHideGraph()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		Boolean display=getBooleanArgument(parameters);
		if (display!=null)
		{
			int iNextParams=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1; 
			parameters=parameters.substring(iNextParams);
			String canvas=getCanvasToDisplayOrHide(parameters);
			if (canvas==null)
			{
				for (CanvasComponent c:sq.getCanvasses())
				{
					if (display.booleanValue())
					{
						displayGraphItems(c,parameters);
					}
					else
					{
						hideGraphItems(c,parameters);
					}
				}
			}
			else
			{
				try
				{
					QComponent qc=sq.getComponent(canvas);
					if (qc instanceof CanvasComponent)
					{
						if (display.booleanValue())
						{
							displayGraphItems((CanvasComponent)qc,parameters);
						}
						else
						{
							hideGraphItems((CanvasComponent)qc,parameters);
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Component with id '");
						error.append(canvas);
						error.append("' is not a canvas component");
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(canvas);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
			}
		}
	}
	
	/**
	 * Execute "displaymarker" task
	 */
	private void executeDisplayMarker()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		String canvas=getCanvasToDisplayOrHide(parameters);
		if (canvas==null)
		{
			for (CanvasComponent c:sq.getCanvasses())
			{
				displayMarkers(c,parameters);
			}
		}
		else
		{
			try
			{
				QComponent qc=sq.getComponent(canvas);
				if (qc instanceof CanvasComponent)
				{
					displayMarkers((CanvasComponent)qc,parameters);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(canvas);
					error.append("' is not a canvas component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Can't find a component with id: ");
				error.append(canvas);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "hidemarker" task
	 */
	private void executeHideMarker()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		String canvas=getCanvasToDisplayOrHide(parameters);
		if (canvas==null)
		{
			for (CanvasComponent c:sq.getCanvasses())
			{
				hideMarkers(c,parameters);
			}
		}
		else
		{
			try
			{
				QComponent qc=sq.getComponent(canvas);
				if (qc instanceof CanvasComponent)
				{
					hideMarkers((CanvasComponent)qc,parameters);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(canvas);
					error.append("' is not a canvas component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Can't find a component with id: ");
				error.append(canvas);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "displayhidemarker" task
	 */
	private void executeDisplayHideMarker()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		Boolean display=getBooleanArgument(parameters);
		if (display!=null)
		{
			int iNextParams=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1; 
			parameters=parameters.substring(iNextParams);
			String canvas=getCanvasToDisplayOrHide(parameters);
			if (canvas==null)
			{
				for (CanvasComponent c:sq.getCanvasses())
				{
					if (display.booleanValue())
					{
						displayMarkers(c,parameters);
					}
					else
					{
						hideMarkers(c,parameters);
					}
				}
			}
			else
			{
				try
				{
					QComponent qc=sq.getComponent(canvas);
					if (qc instanceof CanvasComponent)
					{
						if (display.booleanValue())
						{
							displayMarkers((CanvasComponent)qc,parameters);
						}
						else
						{
							hideMarkers((CanvasComponent)qc,parameters);
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Component with id '");
						error.append(canvas);
						error.append("' is not a canvas component");
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(canvas);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
			}
		}
	}
	
	/**
	 * Execute "displaymarkerline" task
	 */
	private void executeDisplayMarkerLine()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		String canvas=getCanvasToDisplayOrHide(parameters);
		if (canvas==null)
		{
			for (CanvasComponent c:sq.getCanvasses())
			{
				displayMarkerLines(c,parameters);
			}
		}
		else
		{
			try
			{
				QComponent qc=sq.getComponent(canvas);
				if (qc instanceof CanvasComponent)
				{
					displayMarkerLines((CanvasComponent)qc,parameters);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(canvas);
					error.append("' is not a canvas component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Can't find a component with id: ");
				error.append(canvas);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "hidemarkerline" task
	 */
	private void executeHideMarkerLine()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		String canvas=getCanvasToDisplayOrHide(parameters);
		if (canvas==null)
		{
			for (CanvasComponent c:sq.getCanvasses())
			{
				hideMarkerLines(c,parameters);
			}
		}
		else
		{
			try
			{
				QComponent qc=sq.getComponent(canvas);
				if (qc instanceof CanvasComponent)
				{
					hideMarkerLines((CanvasComponent)qc,parameters);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Component with id '");
					error.append(canvas);
					error.append("' is not a canvas component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("<task>: Can't find a component with id: ");
				error.append(canvas);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "displayhidemarkerline" task
	 */
	private void executeDisplayHideMarkerLine()
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String parameters=getParameters();
		Boolean display=getBooleanArgument(parameters);
		if (display!=null)
		{
			int iNextParams=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1; 
			parameters=parameters.substring(iNextParams);
			String canvas=getCanvasToDisplayOrHide(parameters);
			if (canvas==null)
			{
				for (CanvasComponent c:sq.getCanvasses())
				{
					if (display.booleanValue())
					{
						displayMarkerLines(c,parameters);
					}
					else
					{
						hideMarkerLines(c,parameters);
					}
				}
			}
			else
			{
				try
				{
					QComponent qc=sq.getComponent(canvas);
					if (qc instanceof CanvasComponent)
					{
						if (display.booleanValue())
						{
							displayMarkerLines((CanvasComponent)qc,parameters);
						}
						else
						{
							hideMarkerLines((CanvasComponent)qc,parameters);
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<task>: Component with id '");
						error.append(canvas);
						error.append("' is not a canvas component");
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("<task>: Can't find a component with id: ");
					error.append(canvas);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
			}
		}
	}
	
	/**
	 * Execute "resetvar" task<br/><br/>
	 * IMPORTANT: Resetting order: first &lt;random&gt; components will be reseted first, 
	 * then &lt;variable&gt; components and finally &lt;replaceholder&gt; components.
	 */
	private void executeResetVar()
	{
		// Reset <random> components
		for (RandomComponent random:getRandomComponentsFromParameters())
		{
			resetRandom(random);
		}
		
		// Reset <variable> components
		for (VariableComponent variable:getVariableComponentsFromParameters())
		{
			resetVariable(variable);
		}
		
		// Reset <replaceholder> components
		for (ReplaceholderComponent replaceholder:getReplaceholderComponentsFromParameters())
		{
			resetReplaceholder(replaceholder);
		}
	}
	
	/**
	 * Execute "setvariable" task<br/><br/>
	 */
	private void executeSetVariable()
	{
		VariableComponent variable=getVariableToSet();
		String value=getValueToSet();
		StringBuffer literalValue=new StringBuffer();
		literalValue.append('@');
		literalValue.append(value);
		variable.setSet(literalValue.toString());
		variable.resetValue();
	}
	
	/**
	 * Execute "enable" task
	 */
	private void executeEnable()
	{
		String parameters=getParameters();
		for (QComponent qc:getComponentsFromParameters(parameters))
		{
			enableComponent(qc);
		}
	}
	
	/**
	 * Execute "disable" task
	 */
	private void executeDisable()
	{
		String parameters=getParameters();
		for (QComponent qc:getComponentsFromParameters(parameters))
		{
			disableComponent(qc);
		}
	}
	
	/**
	 * Execute "setenabled" task
	 */
	private void executeSetEnabled()
	{
		String parameters=getParameters();
		Boolean enable=getBooleanArgument(parameters);
		if (enable!=null)
		{
			int iNextParams=AnswerChecking.indexOfCharacter(
					parameters,PARAMETER_COMPONENTS_SEPARATOR,AnswerChecking.getEscapeSequences())+1; 
			parameters=parameters.substring(iNextParams);
			for (QComponent qc:getComponentsFromParameters(parameters))
			{
				if (enable.booleanValue())
				{
					enableComponent(qc);
				}
				else
				{
					disableComponent(qc);
				}
			}
		}
	}
	
	/**
	 * Execute "setproperty" task
	 */
	private void executeSetProperty()
	{
		String parameters=getParameters();
		setProperty(getComponentFromParameters(parameters),getPropertyFromParameters(parameters),
				getValueFromParameters(parameters));
	}
	
	/** 
	 *  Execute this task.<br/>
	 *  This method only support standard tasks, no custom ones.
	 */
	public void execute()
	{
		String task=getTask();
		if (task!=null)
		{
			if (task.equals(TASK_CLEAR))
			{
				executeClear();
			}
			else if (task.equals(TASK_CLEARWRONG))
			{
				executeClearWrong();
			}
			else if (task.equals(TASK_DISPLAYRIGHT))
			{
				executeDisplayRight();
			}
			else if (task.equals(TASK_HIGHLIGHTHITS))
			{
				executeHighlightHits();
			}
			else if (task.equals(TASK_HIGHLIGHTRIGHT))
			{
				executeHighlightRight();
			}
			else if (task.equals(TASK_HIGHTLIGHTHITSANDRIGHT))
			{
				executeHighlightHitsAndRight();
			}
			else if (task.equals(TASK_CLEARHIGHLIGHT))
			{
				executeClearHighlight();
			}
			else if (task.equals(TASK_DISPLAYGRAPH))
			{
				executeDisplayGraph();
			}
			else if (task.equals(TASK_HIDEGRAPH))
			{
				executeHideGraph();
			}
			else if (task.equals(TASK_DISPLAYHIDEGRAPH))
			{
				executeDisplayHideGraph();
			}
			else if (task.equals(TASK_DISPLAYMARKER))
			{
				executeDisplayMarker();
			}
			else if (task.equals(TASK_HIDEMARKER))
			{
				executeHideMarker();
			}
			else if (task.equals(TASK_DISPLAYHIDEMARKER))
			{
				executeDisplayHideMarker();
			}
			else if (task.equals(TASK_DISPLAYMARKERLINE))
			{
				executeDisplayMarkerLine();
			}
			else if (task.equals(TASK_HIDEMARKERLINE))
			{
				executeHideMarkerLine();
			}
			else if (task.equals(TASK_DISPLAYHIDEMARKERLINE))
			{
				executeDisplayHideMarkerLine();
			}
			else if (task.equals(TASK_RESETVAR))
			{
				executeResetVar();
			}
			else if (task.equals(TASK_SETVARIABLE))
			{
				executeSetVariable();
			}
			else if (task.equals(TASK_ENABLE))
			{
				executeEnable();
			}
			else if (task.equals(TASK_DISABLE))
			{
				executeDisable();
			}
			else if (task.equals(TASK_SETENABLED))
			{
				executeSetEnabled();
			}
			else if (task.equals(TASK_SETPROPERTY))
			{
				executeSetProperty();
			}
		}
	}
	
	/**
	 * Tests if this task component match all conditions to be executed.
	 * @param when Name that indicates if we want this task to be executed at this moment
	 * @return true if this task component match all conditions to be executed, false otherwise
	 */
	@Override
	public boolean test(String when)
	{
		return getWhen().equals(when) && Tester.testAll((StandardQuestion)getQuestion(),this);
	}
	
	/**
	 * Tests if this task component match all conditions to be executed (without testing "when").
	 * @return true if this task component match all conditions to be executed, false otherwise
	 */
	@Override
	public boolean test()
	{
		return Tester.testAll((StandardQuestion)getQuestion(),this);
	}
}
