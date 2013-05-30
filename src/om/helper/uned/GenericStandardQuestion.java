package om.helper.uned;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmUnexpectedException;
import om.question.InitParams;
import om.question.Rendering;
import om.question.Results;
import om.stdcomponent.uned.Answerable;
import om.stdcomponent.uned.BoxComponent;
import om.stdcomponent.uned.RandomComponent;
import om.stdcomponent.uned.ReplaceholderComponent;
import om.stdcomponent.uned.SummaryLineComponent;
import om.stdcomponent.uned.TaskComponent;
import om.stdcomponent.uned.TaskFormatRuntimeException;
import om.stdcomponent.uned.VariableComponent;
import om.stdquestion.QComponent;
import om.stdquestion.uned.StandardQuestion;
import util.xml.XML;

//UNED: 25-10-2011 - dballestin
/**
 * Generic base class for questions.<br/><br/>
 * This class provides support for new components and properties defined to be able to make generic
 * questions but trying to make no assumptions about components used inside question.xml<br/><br/>
 * This is perfect for advanced questions that not fit well into other generic questions requirements, 
 * but on the other hand it does not use by default a lot of useful features used in those other generic 
 * questions, so nearly all interaction with user (and even some internal operations) must be achieved 
 * with help of tasks (the solution taken to avoid Java coding).<br/><br/>
 * The advantage of this class is that allows you to define question's behaviour with great precission
 * but at the cost of requiring tasks for every little thing you need to do.<br/><br/>
 * <b>Special tasks</b> (standard tasks from {@link TaskComponent} are also available)
 * <table border="1">
 * <tr><th>Task</th><th>Description</th><th>Parameters</th></tr>
 * <tr><td>initializerandoms</td><td>Initializes &lt;random&gt; components.</td>
 * <td>List of &lt;random&gt; components to initialize or ++all++ to initialize all of them</td></tr>
 * <tr><td>initializeplaceholders</td><td>Initializes placeholders from &lt;replaceholder&gt; 
 * components.</td><td>List of &lt;replaceholder&gt; components to initialize or ++all++ 
 * to initialize all of them</td></tr>
 * <tr><td>uninitializeplaceholders</td><td>Uninitializes placeholders.</td><td>&nbsp;</td></tr>
 * <tr><td>setplaceholder</td><td>Sets the value of a placeholder.</td><td>Placeholder information. 
 * It expects 2 values separated by a comma: first placeholder's name and second placeholder's value. 
 * Each value can be an identifier of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; 
 * or answer component or even a string literal if they start with @</td></tr>
 * <tr><td>removeplaceholder</td><td>Removes a placeholder.</td><td>Identifier of placeholder to remove. 
 * It can be an identifier of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer component 
 * or even a string literal if it start with @</td></tr>
 * <tr><td>setcounters</td><td>Initialize counters with the information indicated.<br/><br/>
 * Note that these counters will not be used in default answer processing.</td>
 * <td>Counters initialization information. It expects 3 values separated by commas: 
 * first the number of selectable answers, second the number of selectable right answers 
 * and finally the number of selectable wrong answers. Each value can be an identifier of a &lt;random&gt;, 
 * &lt;replaceholder&gt;, &lt;variable&gt; or answer component or even an integer literal 
 * if they start with @</td></tr>
 * <tr><td>initializecounters</td><td>Initializes counters so that they can be used 
 * for default answer processing.</td><td>&nbsp;</td></tr>
 * <tr><td>resetcounters</td><td>Reset counters to their initial state.</td><td>&nbsp;</td></tr>
 * <tr><td>updatecounters</td><td>Update counters increasing number of selected right answers 
 * or selected wrong answers (and obviously decreasing the number of unselected ones).</td>
 * <td>Counters update information. Consists of an optional value indicating the number of answers 
 * to update in a single step followed by a , and a required boolean value that indicates 
 * if we are updating right (yes) or wrong (no) answers</td></tr>
 * <tr><td>setattempt</td><td>Sets which question attempt the user is on.</td>
 * <td>Attempt. Must be the identifier of a &lt;random&gt;, &lt;replaceholder&gt;,&lt;variable&gt;, 
 * answer component or an integer literal if it starts with @</td></tr>
 * <tr><td>increaseattempt</td><td>Increases "attempt".</td><td>&nbsp;</td></tr>
 * <tr><td>decreaseattempt</td><td>Decreases "attempt".</td><td>&nbsp;</td></tr>
 * <tr><td>setmaxattempts</td><td>Sets the maximum number of attempts permitted.</td>
 * <td>Number of permitted attempts. Must be the identifier of a &lt;random&gt;, &lt;replaceholder&gt;, 
 * &lt;variable&gt;, answer component or an integer literal if it starts with @</td></tr>
 * <tr><td>setprogressinfo</td><td>Sets a brief information on progress through question 
 * that will be displayed alongside the question.<br/><br/>
 * Progress information is retained between actions if it is set to null in future ones.</td>
 * <td>Brief information on progress through question. Can be the identifier of a &lt;random&gt;, 
 * &lt;replaceholder&gt;, &lt;variable&gt;, answer component or a literal constant if it starts with @
 * </td></tr>
 * <tr><td>resetquestiontestingexceptions</td><td>Clear the list of component's identifiers excluded 
 * from testing for displaying at question time.</td><td>&nbsp;</td></tr>
 * <tr><td>addquestiontimetestingexception</td><td>Adds the identifier received to the list of 
 * component's identifiers excluded from testing for displaying at question time.</td>
 * <td>Identifier of text component to exclude from testing for displaying at question time</td></tr>
 * <tr><td>resetquestiontestingnestedexceptions</td><td>Clear the list of component's identifiers 
 * (including subcomponents) excluded from testing for displaying at question time.</td><td>&nbsp;</td></tr>
 * <tr><td>addquestiontimetestingnestedexception</td><td>Adds the identifier received to the list 
 * of component's identifiers (including subcomponents) excluded from testing for displaying 
 * at question time.</td><td>Identifier of component (including subcomponents) to exclude from testing 
 * for displaying at question time</td></tr>
 * <tr><td>end</td><td>Causes the question to end.</td><td>&nbsp;</td></tr>
 * <tr><td>setidanswersnull</td><td>Set the list of answer's component identifiers to null.</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>resetidanswers</td><td>Clear the list of answer's component identifiers.</td><td>&nbsp;</td></tr>
 * <tr><td>addidanswer</td><td>Adds the identifier received to the list 
 * of answer's components identifiers.</td><td>Identifier of answer component to add to the list 
 * of answer's components identifiers</td></tr>
 * <tr><td>setmatchdefault</td><td>yes if last user's answer matches default answer, no if not matches or 
 * can be also the identifier of a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; 
 * containing one of that values (yes, no) or an answer component in which case it is tested 
 * and result used to set if last user's answer matches default answer</td><td>&nbsp;</td></tr>
 * <tr><td>setright</td><td>Set answer rightness.</td><td>yes if last user's answer is right, 
 * no if it is wrong or can be also the identifier of a &lt;random&gt;, &lt;replaceholder&gt; 
 * or &lt;variable&gt; containing one of that values (yes, no) or an answer component in which case 
 * it is tested and result used to answer rightness</td></tr>
 * <tr><td>processdefaultanswer</td><td>Process default answer and set rightness with its result.<br/><br/>
 * Note that this answer processing do not take care of &lt;answercombo&gt; components.</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>processanswer</td><td>Process answer and check rightness taking care of 
 * &lt;answercombo&gt; components.</td><td>&nbsp;</td></tr>
 * <tr><td>setscore</td><td>Sets the question numerical result (on specified score axis) 
 * and also set number of attempts taken to get question right if specified.</td>
 * <td>Score information string. Consists of optional score axis followed by : then the required marks 
 * and optionally followed by , and attempts taken to get question right (marks and attempts 
 * can be the identifier of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt;, answer component 
 * or an integer literal if they start with @)</td></tr>
 * <tr><td>setdefaultscore</td><td>Sets the default question numerical result.</td><td>&nbsp;</td></tr>
 * <tr><td>setquestionline</td><td>Sets question line for summary.</td><td>Question line. 
 * Can be the identifier of a box component to get its questionLine property value or the identifier 
 * of a summaryline (works even if its type is not questionline) or identifier of a &lt;random&gt;, 
 * &lt;replaceholder&gt; or &lt;variable&gt; component or even a string literal if it start with @</td></tr>
 * <tr><td>processquestionline</td><td>Search and set a valid question line for summary.</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>setanswerline</td><td>Sets answer line for summary.</td><td>Answer line. 
 * Can be the identifier of a box component or a component enabled as answer component 
 * to get its answerLine property value or the identifier of a summaryline (works even if its type 
 * is not answerline) or identifier of a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component 
 * or even a string literal if it start with @</td></tr>
 * <tr><td>processanswerline</td><td>Search and set a valid answer line for summary.</td><td>&nbsp;</td></tr>
 * <tr><td>appendsummaryline</td><td>Appends a new line to the summary.</td>
 * <td>Summary line. Can be the identifier of a summaryline (works even if its type is not summaryline) 
 * or identifier of a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component 
 * or even a string literal if it start with @</td></tr>
 * <tr><td>processsummarylines</td><td>Search and append summary lines to summary.</td><td>&nbsp;</td></tr>
 * <tr><td>setcustomresult</td><td>Sets a custom question result.</td>
 * <td>Custom result information. It expects 2 values separated by a comma: first custom result's name 
 * and second custom result's value. Each value can be an identifier of a &lt;random&gt;, 
 * &lt;replaceholder&gt;, &lt;variable&gt; or answer component or even a string literal if they start with @
 * </td></tr>
 * <tr><td>sendresults</td><td>Causes the information in the Results object to be sent to the test navigator 
 * and stored.</td><td>&nbsp;</td></tr>
 * </table>
 * <p/>
 * <b>IMPORTANT:</b><br/><br/>
 * When using this class as a base for your questions you mast take following things in consideration:
 * <ol>
 * <li>'attempts' concept not exist although you can emulate that concept with tasks</li>
 * <li>No 'attempts' means that answer processing is not invoked by default, but you can invoke it 
 * with tasks.</li>
 * <li>If you need to test answer components their identifiers must be added to the users 
 * answers identifiers list (or tests will be wrong), you can achieve it invoking answer processing 
 * or you can use tasks that allow you to specifically add desired identifiers to that list.</li>
 * <li>Obviously no 'attempts' also means that random, replaceholders and/or variables components 
 * must be manually reset with tasks if you need it.</li>
 * <li>You have no special identifiers for components with an special behaviour, for example using
 * 'wrong' as identifier of a text component not means that it will be shown on wrong answer, instead
 * you must use tests.</li>
 * <li>Rightness at start is always wrong and no answer components are added to the answer's list,
 * that is done in the answer processing and as said before you must call it explicitly with a task
 * to work.</li>
 * <li>As another consequence of no 'attempts', so 'startattempt', 'endattempt' and/or 'end' tasks are
 * not called by default.</li>
 * <li>Only functional tasks are 'start' for tasks to execute at start of question and obviously
 * custom tasks</li>
 * <li>Summary is not generated by default, but you can generate it with tasks.</li>
 * <li>Counters by default are not initialized so they don't work in tests except if you initialize
 * them with the help of a task.</li>
 * <li>Question only ends with help of a task, if you have not defined an ending task the question
 * never ends.</li>
 * <li>As another consequence of no 'End question' concept setting score and/or sending results 
 * to navigator are not done by default and you must use tasks to do them.</li>
 * <li>Some special tasks (not usable in other generic question classes) have been defined so you
 * can resolve the above commented things.</li>
 * </ol>
 * As a final note it is recommended to use other generic questions classes whenever possible and 
 * use this one only when the behaviour you want for question it is really impossible or very dificult 
 * to achieve with the others.<br/><br/>
 * Although it is possible to emulate other generic question classes behaviour with this class, 
 * doing that can be complex and will result in a confusing question.xml file.
 */
public class GenericStandardQuestion extends StandardQuestion
{
	/** Default character used as separator in task's parameters */
	private final char DEFAULT_SEPARATOR=',';
	
	/** Default character used as prefix for literals */
	private final char DEFAULT_LITERAL_PREFIX='@';
	
	/** Default reserved word to reference all valid components for a task */
	private static final String DEFAULT_ALL="++all++";
	
	/** Character used as separator between axis identifier and marks in a score information string */
	private final char AXIS_SCORE_SEPARATOR=':';
	
	/** Rendering information for this generic question */
	private Rendering r;
	
	/** Max marks, or 0 if no scoring */
	private int iMaxMarks=0;
	
	/** Flag to distinguish default and not default counters*/
	private boolean defaultCounters=true;
	
	@Override
	public Rendering init(Document d,InitParams ip) throws OmException
	{
		// Initialize rendering information
		setRenderingInformation(super.init(d,ip));
		
		// Initialize iMaxMarks
		try
		{
			Element eScoring=XML.getChild(d.getDocumentElement(),"scoring");
			iMaxMarks=Integer.parseInt(XML.getText(eScoring,"marks"));
		}
		catch(Exception e)
		{
			// Ignore error, leave iMaxMarks at default value: 3
			iMaxMarks=3;
		}
		return r;
	}
	
	@Override
	protected void init() throws OmException
	{
		// Do "start" tasks
		for (Task task:getTasks(TaskComponent.WHEN_START))
		{
			doTask(task);
		}
		
		// Call super.init() to make om.stdquestion.uned.StandardQuestion initializations 
		// before rendering question
		super.init();
	}
	
	/**
	 * @return Rendering information for this generic question
	 */
	protected Rendering getRenderingInformation()
	{
		return r;
	}
	
	/**
	 * Set a new rendering information for this generic question 
	 * @param r
	 */
	protected void setRenderingInformation(Rendering r)
	{
		this.r=r;
	}
	
	/**
	 * Get random components from parameters or all of them if parameters is ++all++
	 * @param parameters List of &lt;random&gt; components or null or ++all++ to get all of them
	 * @return &lt;random&gt; components from parameters or all of them if parameters is ++all++
	 */
	private List<RandomComponent> getRandomComponentsFromParameters(String parameters)
	{
		List<RandomComponent> randoms=null;
		if (parameters==null || parameters.equals(""))
		{
			randoms=new ArrayList<RandomComponent>();
		}
		else if (AnswerChecking.replaceEscapeChars(parameters).equals(DEFAULT_ALL))
		{
			randoms=getRandoms();
		}
		else
		{
			randoms=new ArrayList<RandomComponent>();
			List<String> idRandoms=
				AnswerChecking.split(parameters,DEFAULT_SEPARATOR,AnswerChecking.getEscapeSequences());
			for (String idRandom:idRandoms)
			{
				QComponent qc=null;
				try 
				{
					qc=getComponent(AnswerChecking.replaceEscapeChars(idRandom));
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Can't find a component with id: ");
					error.append(idRandom);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				if (qc instanceof RandomComponent)
				{
					randoms.add((RandomComponent)qc);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Component with id '");
					error.append(idRandom);
					error.append("' is not a random component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
		}
		return randoms;
	}
	
	/**
	 * Get replaceholder components from parameters or all of them if parameters is ++all++
	 * @param parameters List of &lt;replaceholder&gt; components or null or ++all++ 
	 * to get all of them
	 * @return &lt;replaceholder&gt; components from parameters or all of them if parameters is ++all++
	 */
	private List<ReplaceholderComponent> getReplaceholderComponentsFromParameters(String parameters)
	{
		List<ReplaceholderComponent> replaceholders=null;
		if (parameters==null || parameters.equals(""))
		{
			replaceholders=new ArrayList<ReplaceholderComponent>();
		}
		else if (AnswerChecking.replaceEscapeChars(parameters).equals(DEFAULT_ALL))
		{
			replaceholders=getReplaceholders();
		}
		else
		{
			replaceholders=new ArrayList<ReplaceholderComponent>();
			List<String> idReplaceholders=
				AnswerChecking.split(parameters,DEFAULT_SEPARATOR,AnswerChecking.getEscapeSequences());
			for (String idReplaceholder:idReplaceholders)
			{
				QComponent qc=null;
				try 
				{
					qc=getComponent(AnswerChecking.replaceEscapeChars(idReplaceholder));
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Can't find a component with id: ");
					error.append(idReplaceholder);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				if (qc instanceof ReplaceholderComponent)
				{
					replaceholders.add((ReplaceholderComponent)qc);
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Component with id '");
					error.append(idReplaceholder);
					error.append("' is not a replaceholder component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
		}
		return replaceholders;
	}
	
	/**
	 * @param id Identifier of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer
	 * component
	 * @return  Value a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer component 
	 * or null if that component not exists or it's not an instance of one of them
	 */
	private String getComponentValue(String id)
	{
		String value=null;
		QComponent qc=null;
		id=AnswerChecking.replaceEscapeChars(id);
		try
		{
			qc=getComponent(id);
		}
		catch (OmDeveloperException e)
		{
			StringBuffer error=new StringBuffer();
			error.append("Can't find a component with id: ");
			error.append(id);
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
			error.append("Component with id '");
			error.append(id);
			error.append("' is not a random, replaceholder, variable or answer component");
			throw new TaskFormatRuntimeException(error.toString());
		}
		return value;
	}
	
	/**
	 * @param id Identifier of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer
	 * component
	 * @return Result of testing answer component if it is an answer component (enabled for answer)
	 * or if it is a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component return
	 * true if value is "yes" or false if it is "no", otherwise throws an OmDeveloperException
	 */
	private boolean getComponentBooleanValue(String id)
	{
		boolean bValue=false;
		String value=null;
		QComponent qc=null;
		id=AnswerChecking.replaceEscapeChars(id);
		try
		{
			qc=getComponent(id);
		}
		catch (OmDeveloperException e)
		{
			StringBuffer error=new StringBuffer();
			error.append("Can't find a component with id: ");
			error.append(id);
			throw new TaskFormatRuntimeException(error.toString(),e);
		}
		if (qc instanceof Answerable && ((Answerable)qc).isAnswerEnabled())
		{
			bValue=((Answerable)qc).isRight();
		}
		else
		{
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
					value="";
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
				error.append("Component with id '");
				error.append(id);
				error.append("' is not a random, replaceholder, variable or answer component");
				throw new TaskFormatRuntimeException(error.toString());
			}
			if (value.equals("yes"))
			{
				bValue=true;
			}
			else if (value.equals("no"))
			{
				bValue=false;
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("Value of component id ");
				error.append(id);
				error.append(" is not a valid boolean value (yes, no)");
				throw new TaskFormatRuntimeException(error.toString());
			}
		}
		return bValue;
	}
	
	/**
	 * Callback that Om calls when the "actionCustom" action is invoked.<br/><br/>
	 * Note that this callback receives a string with a selector used to test which tasks to execute.
	 * @param custom Selector
	 */
	public void actionCustom(String custom) throws OmException
	{
		// Do "custom" tasks
		StringBuffer whenCustom=new StringBuffer(TaskComponent.WHEN_CUSTOM);
		if (custom!=null && !custom.equals(""))
		{
			whenCustom.append('[');
			whenCustom.append(custom);
			whenCustom.append(']');
		}
		for (Task task:getTasks(whenCustom.toString()))
		{
			doTask(task);
		}
	}
	
	/**
	 * Override this method to change the default answer processing implementation.<br/><br/>
	 * Process default answer constructing list of selected answers (identifiers) and returns true 
	 * if the default answer is right or false if it is wrong.<br/><br/> 
	 * Also counts hits and failures if counters have been initialized.
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	protected boolean processDefaultAnswer(int attempt) throws OmDeveloperException
	{
		return processDefaultAnswer(defaultCounters,attempt);
	}
	
	/**
	 * Override this method to check whether the user's answer is correct or not.
	 * <p>
	 * This is a simple right/wrong flag.
	 * @param attempt Question attempt (1 for first, 2 for second, etc.)
	 * @return True if the user is right; false if they are wrong.
	 * @throws OmDeveloperException
	 */
	protected boolean isRight(int attempt) throws OmDeveloperException
	{
		return isRightDefault();
	}
	
	/**
	 * Override this method to change the scoring. The method should call getResults().setScore.
	 * <p>
	 * Default scoring is out of 3 points: 3 for correct answer first time,
	 * 2 second time, 1 third time, 0 for fail or pass.
	 * @param bRight True if user was right (rather than passing/failing)
	 * @param bPass True if user passed on question
	 * @param iAttempt Attempt number (1 = first attempt)
	 */
	protected void setScore(boolean bRight,boolean bPass,int iAttempt) throws OmDeveloperException
	{
		if (iMaxMarks<=0)
		{
			throw new OmDeveloperException(
					"Cannot set score on question: invalid <scoring> defined in question.xml");
		}
		if (bRight)
		{
			getResults().setScore(Math.max(0,iMaxMarks+1-iAttempt),iAttempt);
		}
		else
		{
			getResults().setScore(0,bPass?Results.ATTEMPTS_PASS:Results.ATTEMPTS_WRONG);
		}
	}
	
	/**
	 * Override this method to change the answer processing implementation.<br/><br/>
	 * First do default answer processing and then checks rightness taking care of 
	 * &lt;answercombo&gt; components.
	 * @throws OmDeveloperException
	 */
	protected void processAnswer() throws OmDeveloperException
	{
		setMatchDefault(processDefaultAnswer(getAttempt()));
		setRight(isRight(getAttempt()));
	}
	
	/**
	 * Execute "initializerandoms" task that initializes &lt;random&gt; components.
	 * @param parameters List of &lt;random&gt; components to initialize or ++all++ to initialize
	 * all of them 
	 */
	protected void executeInitializeRandoms(String parameters)
	{
		initializeRandoms(getRandomComponentsFromParameters(parameters));
	}
	
	/**
	 * Execute "initializeplaceholders" task that initializes placeholders from &lt;replaceholder&gt; 
	 * components.
	 * @param parameters List of &lt;replaceholder&gt; components to initialize or ++all++ 
	 * to initialize all of them
	 */
	protected void executeInitializePlaceholders(String parameters)
	{
		getPlaceholders().clear();
		try
		{
			initializePlaceholders(getReplaceholderComponentsFromParameters(parameters));
		}
		catch (OmException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Execute "uninitializeplaceholders" task that uninitializes placeholders.
	 */
	protected void executeUninitializePlaceholders()
	{
		setPlaceholdersInitialized(false);
	}
	
	/**
	 * Execute "setplaceholder" task that sets the value of a placeholder.
	 * @param placeholder Placeholder information. It expects 2 values separated by a comma: 
	 * first placeholder's name and second placeholder's value. Each value can be an identifier 
	 * of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer component or even 
	 * a string literal if they start with @
	 */
	protected void executeSetPlaceholder(String placeholder)
	{
		if (placeholder==null || placeholder.equals(""))
		{
			throw new TaskFormatRuntimeException(
					"Missing required placeholder's name and value separated by , in parameters");
		}
		else
		{
			int iEndPlaceholderName=AnswerChecking.indexOfCharacter(
					placeholder,DEFAULT_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndPlaceholderName==-1)
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing , to separate placeholder's name and value in parameters: ");
				error.append(placeholder);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else if (iEndPlaceholderName+1>=placeholder.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing required placeholder's value in parameters: ");
				error.append(placeholder);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				String placeholderName=placeholder.substring(0,iEndPlaceholderName);
				if (placeholderName.charAt(0)==DEFAULT_LITERAL_PREFIX)
				{
					if (placeholderName.length()>1)
					{
						placeholderName=AnswerChecking.replaceEscapeChars(placeholderName.substring(1));
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("Missing required placeholder's name in parameters: ");
						error.append(placeholder);
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				else
				{
					placeholderName=getComponentValue(placeholderName);
				}
				String placeholderValue=placeholder.substring(iEndPlaceholderName+1);
				if (placeholderValue.charAt(0)==DEFAULT_LITERAL_PREFIX)
				{
					if (placeholderValue.length()>1)
					{
						placeholderValue=AnswerChecking.replaceEscapeChars(placeholderValue.substring(1));
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("Missing required placeholder's value in parameters: ");
						error.append(placeholder);
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				else
				{
					placeholderValue=getComponentValue(placeholderValue);
				}
				setPlaceholder(placeholderName,placeholderValue);
			}
		}
	}
	
	/**
	 * Execute "removeplaceholder" task that removes a placeholder.
	 * @param placeholder Identifier of placeholder to remove. It can be an identifier of a &lt;random&gt;, 
	 * &lt;replaceholder&gt;, &lt;variable&gt; or answer component or even a string literal 
	 * if it start with @
	 */
	protected void executeRemovePlaceholder(String placeholder)
	{
		if (placeholder==null || placeholder.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required placeholder's name in parameters");
		}
		else
		{
			getPlaceholders().remove(AnswerChecking.replaceEscapeChars(placeholder));
		}
	}
	
	/**
	 * Execute "setcounters" task that initialize counters with the information indicated.<br/><br/>
	 * Note that these counters will not be used in default answer processing.
	 * @param countersInfo Counters initialization information. It expects 3 values separated by commas: 
	 * first the number of selectable answers, second the number of selectable right answers and finally 
	 * the number of selectable wrong answers. Each value can be an identifier of a &lt;random&gt;, 
	 * &lt;replaceholder&gt;, &lt;variable&gt; or answer component or even an integer literal if they
	 * start with @
	 */
	protected void executeSetCounters(String countersInfo)
	{
		if (countersInfo==null || countersInfo.equals(""))
		{
			throw new TaskFormatRuntimeException(
					"Missing required numbers of selectable total, right and wrong answers separated by , in parameters");
		}
		else
		{
			int iEndSelectableAnswers=AnswerChecking.indexOfCharacter(
					countersInfo,DEFAULT_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndSelectableAnswers==-1)
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing , to separate numbers of selectable total and right answers in parameters: ");
				error.append(countersInfo);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else if (iEndSelectableAnswers+1>=countersInfo.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing required numbers of selectable right and wrong answers separated by , in parameters: ");
				error.append(countersInfo);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				int iEndSelectableRightAnswers=AnswerChecking.indexOfCharacter(countersInfo,
						DEFAULT_SEPARATOR,iEndSelectableAnswers+1,AnswerChecking.getEscapeSequences());
				if (iEndSelectableRightAnswers==-1)
				{
					StringBuffer error=new StringBuffer();
					error.append("Missing , to separate numbers of selectable right and wrong answers in parameters: ");
					error.append(countersInfo);
					throw new TaskFormatRuntimeException(error.toString());
				}
				else if (iEndSelectableRightAnswers+1>=countersInfo.length())
				{
					StringBuffer error=new StringBuffer();
					error.append("Missing required numbers of selectable wrong answers in parameters: ");
					error.append(countersInfo);
					throw new TaskFormatRuntimeException(error.toString());
				}
				else
				{
					String sSelectableAnswers=countersInfo.substring(0,iEndSelectableAnswers);
					if (sSelectableAnswers.charAt(0)==DEFAULT_LITERAL_PREFIX)
					{
						if (sSelectableAnswers.length()>1)
						{
							sSelectableAnswers=
									AnswerChecking.replaceEscapeChars(sSelectableAnswers.substring(1));
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("Missing required numbers of selectable answers in parameters: ");
							error.append(countersInfo);
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					else
					{
						sSelectableAnswers=getComponentValue(sSelectableAnswers);
					}
					String sSelectableRightAnswers=
							countersInfo.substring(iEndSelectableAnswers+1,iEndSelectableRightAnswers);
					if (sSelectableRightAnswers.charAt(0)==DEFAULT_LITERAL_PREFIX)
					{
						if (sSelectableRightAnswers.length()>1)
						{
							sSelectableRightAnswers=
								AnswerChecking.replaceEscapeChars(sSelectableRightAnswers.substring(1));
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("Missing required numbers of selectable right answers in parameters: ");
							error.append(countersInfo);
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					else
					{
						sSelectableRightAnswers=getComponentValue(sSelectableRightAnswers);
					}
					String sSelectableWrongAnswers=
							countersInfo.substring(iEndSelectableRightAnswers+1);
					if (sSelectableWrongAnswers.charAt(0)==DEFAULT_LITERAL_PREFIX)
					{
						if (sSelectableWrongAnswers.length()>1)
						{
							sSelectableWrongAnswers=
								AnswerChecking.replaceEscapeChars(sSelectableWrongAnswers.substring(1));
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("Missing required numbers of selectable wrong answers in parameters: ");
							error.append(countersInfo);
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					else
					{
						sSelectableWrongAnswers=getComponentValue(sSelectableWrongAnswers);
					}
					Integer selectableAnswers=null;
					if (sSelectableAnswers!=null)
					{
						try
						{
							selectableAnswers=new Integer(sSelectableAnswers);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("Value for number of selectable answers is not a valid number: ");
							error.append(sSelectableAnswers);
							throw new TaskFormatRuntimeException(error.toString(),e);
						}
					}
					Integer selectableRightAnswers=null;
					if (sSelectableRightAnswers!=null)
					{
						try
						{
							selectableRightAnswers=new Integer(sSelectableRightAnswers);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("Value for number of selectable right answers is not a valid number: ");
							error.append(sSelectableRightAnswers);
							throw new TaskFormatRuntimeException(error.toString(),e);
						}
					}
					Integer selectableWrongAnswers=null;
					if (sSelectableWrongAnswers!=null)
					{
						try
						{
							selectableWrongAnswers=new Integer(sSelectableWrongAnswers);
						}
						catch (NumberFormatException e)
						{
							StringBuffer error=new StringBuffer();
							error.append("Value for number of selectable wrong answers is not a valid number: ");
							error.append(sSelectableWrongAnswers);
							throw new TaskFormatRuntimeException(error.toString(),e);
						}
					}
					setCounters(new QuestionCounters(
							selectableAnswers.intValue(),selectableRightAnswers.intValue(),
							selectableWrongAnswers.intValue()));
					defaultCounters=false;
				}
			}
		}
	}
	
	/**
	 * Execute "initializecounters" task that initializes counters so that they can be used 
	 * for default answer processing.
	 */
	protected void executeInitializeCounters()
	{
		initializeCounters();
		defaultCounters=true;
	}
	
	/**
	 * Execute "resetcounters" task that reset counters to their initial state.
	 */
	protected void executeResetCounters()
	{
		if (getCounters()==null)
		{
			throw new TaskFormatRuntimeException(
					"Can't reset counters because they have not been initialized");
		}
		else
		{
			getCounters().reset();
		}
	}
	
	/**
	 * Execute "updatecounters" task that update counters increasing number of selected right answers or
	 * selected wrong answers (and obviously decreasing the number of unselected ones).
	 * @param updateInfo Counters update information. Consists of an optional value indicating the 
	 * number of answers to update in a single step followed by a , and a required boolean value that
	 * indicates if we are updating right (yes) or wrong (no) answers. 
	 */
	protected void executeUpdateCounters(String updateInfo)
	{
		if (getCounters()==null)
		{
			throw new TaskFormatRuntimeException(
					"Can't update counters because they have not been initialized");
		}
		else if (updateInfo==null || updateInfo.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required 'right' boolean value in parameters");
		}
		else
		{
			Integer numberAnswers=null;
			int iStartRightFlag=0;
			int iEndNumberAnswers=AnswerChecking.indexOfCharacter(
					updateInfo,DEFAULT_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndNumberAnswers!=-1)
			{
				String sNumberAnswers=updateInfo.substring(0,iEndNumberAnswers);
				if (sNumberAnswers.charAt(0)==DEFAULT_LITERAL_PREFIX)
				{
					if (sNumberAnswers.length()>1)
					{
						sNumberAnswers=AnswerChecking.replaceEscapeChars(sNumberAnswers.substring(1));
					}
					else
					{
						sNumberAnswers="";
					}
				}
				else
				{
					sNumberAnswers=getComponentValue(sNumberAnswers);
				}
				try
				{
					numberAnswers=new Integer(sNumberAnswers);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Value of selectable answers to update is not a valid number: ");
					error.append(sNumberAnswers);
					throw new TaskFormatRuntimeException(error.toString());
				}
				iStartRightFlag=iEndNumberAnswers+1;
			}
			if (iStartRightFlag>=updateInfo.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing required 'right' boolean value in parameters: ");
				error.append(updateInfo);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				String sRightFlag=updateInfo.substring(iStartRightFlag);
				Boolean rightFlag=null;
				try
				{
					rightFlag=new Boolean(getComponentBooleanValue(sRightFlag));
				}
				catch (TaskFormatRuntimeException e)
				{
					if (sRightFlag.equals("yes"))
					{
						rightFlag=Boolean.TRUE;
					}
					else if (sRightFlag.equals("no"))
					{
						rightFlag=Boolean.FALSE;
					}
					else
					{
						throw e;
					}
				}
				if (numberAnswers==null)
				{
					getCounters().selectAnswer(rightFlag.booleanValue());
				}
				else
				{
					getCounters().selectAnswers(numberAnswers.intValue(),rightFlag.booleanValue());
				}
			}
		}
	}
	
	/**
	 * Execute "setattempt" task that sets which question attempt the user is on.
	 * @param attempt Attempt. Must be the identifier of a &lt;random&gt;, &lt;replaceholder&gt;,
	 * &lt;variable&gt;, answer component or an integer literal if it starts with @
	 */
	protected void executeSetAttempt(String attempt)
	{
		if (attempt==null || attempt.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required 'attempt' integer value in parameters");
		}
		else
		{
			if (attempt.charAt(0)==DEFAULT_LITERAL_PREFIX)
			{
				if (attempt.length()>1)
				{
					attempt=AnswerChecking.replaceEscapeChars(attempt.substring(1));
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Missing required 'attempt' integer value in parameters: ");
					error.append(attempt);
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			else
			{
				attempt=getComponentValue(attempt);
			}
			try
			{
				int iAttempt=Integer.parseInt(attempt);
				setAttempt(iAttempt);
			}
			catch (NumberFormatException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("'attempt' value is not a valid number: ");
				error.append(attempt);
				throw new TaskFormatRuntimeException(error.toString());
			}
		}
	}
	
	/**
	 * Execute "increaseattempt" task that increases "attempt".
	 */
	protected void executeIncreaseAttempt()
	{
		setAttempt(getAttempt()+1);
	}
	
	/**
	 * Execute "decreaseattempt" task that decreases "attempt".
	 */
	protected void executeDecreaseAttempt()
	{
		setAttempt(getAttempt()-1);
	}
	
	/**
	 * Execute "setmaxattempts" task that sets the maximum number of attempts permitted.
	 * @param maxAttempts Number of permitted attempts. Must be the identifier of a &lt;random&gt;, 
	 * &lt;replaceholder&gt;, &lt;variable&gt;, answer component or an integer literal 
	 * if it starts with @
	 */
	protected void executeSetMaxAttempts(String maxAttempts)
	{
		if (maxAttempts==null || maxAttempts.equals(""))
		{
			throw new TaskFormatRuntimeException(
					"Missing required 'maxAttempts' integer value in parameters");
		}
		else
		{
			if (maxAttempts.charAt(0)==DEFAULT_LITERAL_PREFIX)
			{
				if (maxAttempts.length()>1)
				{
					maxAttempts=AnswerChecking.replaceEscapeChars(maxAttempts.substring(1));
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Missing required 'maxAttempts' integer value in parameters: ");
					error.append(maxAttempts);
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			else
			{
				maxAttempts=getComponentValue(maxAttempts);
			}
			try
			{
				int iMaxAttempts=Integer.parseInt(maxAttempts);
				setAttempt(iMaxAttempts);
			}
			catch (NumberFormatException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("'maxAttempts' value is not a valid number: ");
				error.append(maxAttempts);
				throw new TaskFormatRuntimeException(error.toString());
			}
		}
	}
	
	/**
	 * Execute "setprogressinfo" task that sets a brief information on progress through question 
	 * that will be displayed alongside the question.<br/><br/>
	 * Progress information is retained between actions if it is set to null in future ones.
	 * @param sProgressInfo Brief information on progress through question. Can be the identifier 
	 * of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt;, answer component or a literal
	 * constant if it starts with @
	 */
	protected void executeSetProgressInfo(String sProgressInfo)
	{
		if (sProgressInfo==null || sProgressInfo.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required progress information in parameters");
		}
		if (sProgressInfo!=null && !sProgressInfo.equals(""))
		{
			if (sProgressInfo.charAt(0)==DEFAULT_LITERAL_PREFIX)
			{
				if (sProgressInfo.length()>1)
				{
					sProgressInfo=
							AnswerChecking.replaceEscapeCharsIgnoreNewLine(sProgressInfo.substring(1));
				}
				else
				{
					sProgressInfo="";
				}
			}
			else
			{
				sProgressInfo=getComponentValue(sProgressInfo);
			}
		}
		getRenderingInformation().setProgressInfo(sProgressInfo);
	}
	
	/**
	 * Execute "resetquestiontestingexceptions" task that clear the list of component's identifiers 
	 * excluded from testing for displaying at question time.
	 */
	protected void executeResetQuestionTestingExceptions()
	{
		resetQuestionTestingExceptions();
	}
	
	/**
	 * Execute "addquestiontimetestingexception" task that adds the identifier received to the list 
	 * of component's identifiers excluded from testing for displaying at question time.
	 * @param exception Identifier of text component to exclude from testing for displaying 
	 * at question time
	 */
	protected void executeAddQuestionTimeTestingException(String exception)
	{
		if (exception==null || exception.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required component's identifier in parameters");
		}
		else
		{
			exception=AnswerChecking.replaceEscapeChars(exception);
			try
			{
				getComponent(exception);
				addQuestionTimeTestingException(exception);
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("Can't find a component with id: ");
				error.append(exception);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "resetquestiontestingnestedexceptions" task that clear the list of 
	 * component's identifiers (including subcomponents) excluded from testing for displaying 
	 * at question time.
	 */
	protected void executeResetQuestionTestingNestedExceptions()
	{
		resetQuestionTestingNestedExceptions();
	}
	
	/**
	 * Execute "eaddquestiontimetestingnestedexception" task that adds the identifier received 
	 * to the list of component's identifiers (including subcomponents) excluded from testing 
	 * for displaying at question time.
	 * @param exception Identifier of component (including subcomponents) to exclude from testing 
	 * for displaying at question time
	 */
	protected void executeAddQuestionTimeTestingNestedException(String exception)
	{
		if (exception==null || exception.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required component's identifier in parameters");
		}
		else
		{
			exception=AnswerChecking.replaceEscapeChars(exception);
			try
			{
				getComponent(exception);
				addQuestionTimeTestingNestedException(exception);
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("Can't find a component with id: ");
				error.append(exception);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
		}
	}
	
	/**
	 * Execute "end" task that causes the question to end.
	 */
	protected void executeEnd()
	{
		try
		{
			end();
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Execute "setidanswersnull" task that set the list of answer's component identifiers to null.
	 */
	protected void executeSetIdAnswersNull()
	{
		setIdAnswersNull();
	}
	
	/**
	 * Execute "resetidanswers" task that clear the list of answer's component identifiers.
	 */
	protected void executeResetIdAnswers()
	{
		resetIdAnswers();
	}
	
	/**
	 * Execute "addidanswer" task that adds the identifier received to the list 
	 * of answer's components identifiers.
	 * @param id Identifier of answer component to add to the list of answer's components identifiers
	 */
	protected void executeAddIdAnswer(String id)
	{
		if (id==null || id.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required component's identifier in parameters");
		}
		else
		{
			id=AnswerChecking.replaceEscapeChars(id);
			QComponent qc=null;
			try
			{
				qc=getComponent(id);
			}
			catch (OmDeveloperException e)
			{
				StringBuffer error=new StringBuffer();
				error.append("Can't find a component with id: ");
				error.append(id);
				throw new TaskFormatRuntimeException(error.toString(),e);
			}
			if (qc instanceof Answerable)
			{
				addIdAnswer(AnswerChecking.replaceEscapeChars(id));
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append("Component with id '");
				error.append(id);
				error.append("' is not an answer component");
				throw new TaskFormatRuntimeException(error.toString());
			}
		}
	}
	
	/**
	 * Execute "setmatchdefault" task that set if last user's answer matches default answer.
	 * @param matchDefault yes if last user's answer matches default answer, no if not matches or
	 * can be also the identifier of a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; 
	 * containing one of that values (yes, no) or an answer component in which case it is tested and
	 * result used to set if last user's answer matches default answer
	 */
	protected void executeSetMatchDefault(String matchDefault)
	{
		Boolean matchDefaultValue=null;
		try
		{
			matchDefaultValue=new Boolean(getComponentBooleanValue(matchDefault));
		}
		catch (TaskFormatRuntimeException e)
		{
			if (matchDefault.equals("yes"))
			{
				matchDefaultValue=Boolean.TRUE;
			}
			else if (matchDefault.equals("no"))
			{
				matchDefaultValue=Boolean.FALSE;
			}
			else
			{
				throw e;
			}
		}
		setMatchDefault(matchDefaultValue.booleanValue());
	}
	
	/**
	 * Execute "setright" task that set answer rightness.
	 * @param right yes if last user's answer is right, no if it is wrong or
	 * can be also the identifier of a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; 
	 * containing one of that values (yes, no) or an answer component in which case it is tested and
	 * result used to answer rightness
	 */
	protected void executeSetRight(String right)
	{
		Boolean rightValue=null;
		try
		{
			rightValue=new Boolean(getComponentBooleanValue(right));
		}
		catch (TaskFormatRuntimeException e)
		{
			if (right.equals("yes"))
			{
				rightValue=Boolean.TRUE;
			}
			else if (right.equals("no"))
			{
				rightValue=Boolean.FALSE;
			}
			else
			{
				throw e;
			}
		}
		setRight(rightValue.booleanValue());
	}
	
	/**
	 * Execute "processdefaultanswer" task that process default answer and set rightness with its
	 * result.<br/><br/>
	 * Note that this answer processing do not take care of &lt;answercombo&gt; components.
	 */
	protected void executeProcessDefaultAnswer()
	{
		try
		{
			setMatchDefault(processDefaultAnswer(getAttempt()));
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
		setRight(isMatchDefault());
	}
	
	/**
	 * Execute "processanswer" task that process answer and check rightness taking care of 
	 * &lt;answercombo&gt; components.
	 */
	protected void executeProcessAnswer()
	{
		try
		{
			processAnswer();
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Execute "setscore" task that sets the question numerical result (on specified score axis)
	 * and also set number of attempts taken to get question right if specified.
	 * @param scoreInfo Score information string. Consists of optional score axis followed by : then
	 * the required marks and optionally followed by , and attempts taken to get question right
	 * (marks and attempts can be the identifier of a &lt;random&gt;, &lt;replaceholder&gt;, 
	 * &lt;variable&gt;, answer component or an integer literal if they start with @)
	 */
	protected void executeSetScore(String scoreInfo)
	{
		if (scoreInfo==null || scoreInfo.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required marks in parameters");
		}
		else
		{
			String sAxis=null;
			int iStartMarks=0;
			int iEndAxis=AnswerChecking.indexOfCharacter(
					scoreInfo,AXIS_SCORE_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndAxis!=-1)
			{
				sAxis=AnswerChecking.replaceEscapeChars(scoreInfo.substring(0,iEndAxis));
				iStartMarks=iEndAxis+1;
			}
			if (iStartMarks>=scoreInfo.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing required marks in parameters: ");
				error.append(scoreInfo);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				String sMarks=null;
				String sAttempts=null;
				int iEndMarks=AnswerChecking.indexOfCharacter(
						scoreInfo,DEFAULT_SEPARATOR,iStartMarks,AnswerChecking.getEscapeSequences())+1;
				if (iEndMarks==-1)
				{
					sMarks=scoreInfo.substring(iStartMarks);
					if (sMarks.charAt(0)==DEFAULT_LITERAL_PREFIX)
					{
						if (sMarks.length()>1)
						{
							sMarks=sMarks.substring(1);
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("Missing required marks in parameters: ");
							error.append(scoreInfo);
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					else
					{
						sMarks=getComponentValue(sMarks);
					}
				}
				else
				{
					sMarks=scoreInfo.substring(iStartMarks,iEndMarks);
					if (sMarks.charAt(0)==DEFAULT_LITERAL_PREFIX)
					{
						if (sMarks.length()>1)
						{
							sMarks=sMarks.substring(1);
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("Missing required marks in parameters: ");
							error.append(scoreInfo);
							throw new TaskFormatRuntimeException(error.toString());
						}
					}
					else
					{
						sMarks=getComponentValue(sMarks);
					}
					sAttempts="";
					if (iEndMarks+1<scoreInfo.length())
					{
						sAttempts=scoreInfo.substring(iEndMarks+1);
						if (sAttempts.charAt(0)==DEFAULT_LITERAL_PREFIX && sAttempts.length()>1)
						{
							sAttempts=sAttempts.substring(1);
						}
						else
						{
							sAttempts=getComponentValue(sAttempts);
						}
					}
				}
				Integer marks=null;
				try
				{
					marks=new Integer(sMarks);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Value of marks is not a valid number: ");
					error.append(sMarks);
					throw new TaskFormatRuntimeException(error.toString());
				}
				Integer attempts=null;
				if (sAttempts!=null)
				{
					try
					{
						attempts=new Integer(sAttempts);
					}
					catch (NumberFormatException e)
					{
						StringBuffer error=new StringBuffer();
						error.append("'attempts' value is not a valid number: ");
						error.append(sAttempts);
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				try
				{
					getResults().setScore(sAxis,marks.intValue());
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
				if (attempts==null)
				{
					getResults().setAttempts(getAttempt());
				}
				else
				{
					getResults().setAttempts(attempts.intValue());
				}
			}
		}
	}
	
	/**
	 * Execute "setdefaultscore" task that sets the default question numerical result.
	 */
	protected void executeSetDefaultScore()
	{
		try
		{
			setScore(isRight(),getIdAnswers()==null,getAttempt());
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Execute "setquestionline" task that sets question line for summary.
	 * @param questionLine Question line. Can be the identifier of a box component to get its 
	 * questionLine property value or the identifier of a summaryline (works even if its type 
	 * is not questionline) or identifier of a &lt;random&gt;, &lt;replaceholder&gt;
	 * or &lt;variable&gt; component or even a string literal if it start with @
	 */
	protected void executeSetQuestionLine(String questionLine)
	{
		if (questionLine==null || questionLine.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required question line in parameters");
		}
		else
		{
			if (questionLine.charAt(0)==DEFAULT_LITERAL_PREFIX)
			{
				if (questionLine.length()>1)
				{
					questionLine=
							AnswerChecking.replaceEscapeCharsIgnoreNewLine(questionLine.substring(1));
				}
				else
				{
					questionLine="";
				}
			}
			else
			{
				QComponent qc=null;
				try
				{
					qc=getComponent(AnswerChecking.replaceEscapeCharsIgnoreNewLine(questionLine));
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Can't find a component with id: ");
					error.append(questionLine);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				if (qc instanceof BoxComponent)
				{
					questionLine=((BoxComponent)qc).getQuestionLine();
					if (questionLine==null)
					{
						questionLine=getDefaultQuestionLine();
					}
				}
				else if (qc instanceof SummaryLineComponent)
				{
					questionLine=((SummaryLineComponent)qc).getSummaryLine();
				}
				else if (qc instanceof RandomComponent)
				{
					questionLine=((RandomComponent)qc).getValue();
				}
				else if (qc instanceof ReplaceholderComponent)
				{
					try
					{
						questionLine=((ReplaceholderComponent)qc).getReplacementValue();
					}
					catch (OmException e)
					{
						throw new OmUnexpectedException(e.getMessage(),e);
					}
				}
				else if (qc instanceof VariableComponent)
				{
					questionLine=((VariableComponent)qc).getValue();
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Component with id '");
					error.append(questionLine);
					error.append("' is not a box, summaryline, random, replaceholder or variable component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			try
			{
				setQuestionLine(questionLine);
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Execute "processquestionline" task that search and set a valid question line for summary.
	 */
	protected void executeProcessQuestionLine()
	{
		try
		{
			setQuestionLine(getQuestionLine());
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Execute "setanswerline" task that sets answer line for summary.
	 * @param answerLine Answer line. Can be the identifier of a box component or a component 
	 * enabled as answer component to get its answerLine property value 
	 * or the identifier of a summaryline (works even if its type is not answerline) 
	 * or identifier of a &lt;random&gt;, &lt;replaceholder&gt; or &lt;variable&gt; component 
	 * or even a string literal if it start with @
	 */
	protected void executeSetAnswerLine(String answerLine)
	{
		if (answerLine==null || answerLine.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required answer line in parameters");
		}
		else
		{
			if (answerLine.charAt(0)==DEFAULT_LITERAL_PREFIX)
			{
				if (answerLine.length()>1)
				{
					answerLine=AnswerChecking.replaceEscapeCharsIgnoreNewLine(answerLine.substring(1));
				}
				else
				{
					answerLine="";
				}
			}
			else
			{
				QComponent qc=null;
				try
				{
					qc=getComponent(AnswerChecking.replaceEscapeCharsIgnoreNewLine(answerLine));
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Can't find a component with id: ");
					error.append(answerLine);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				if (qc instanceof BoxComponent)
				{
					answerLine=((BoxComponent)qc).getAnswerLine();
					if (answerLine==null)
					{
						answerLine=getDefaultAnswerLine();
					}
				}
				else if (qc instanceof Answerable && ((Answerable)qc).isAnswerEnabled())
				{
					answerLine=((Answerable)qc).getAnswerLine();
				}
				else if (qc instanceof SummaryLineComponent)
				{
					answerLine=((SummaryLineComponent)qc).getSummaryLine();
				}
				else if (qc instanceof RandomComponent)
				{
					answerLine=((RandomComponent)qc).getValue();
				}
				else if (qc instanceof ReplaceholderComponent)
				{
					try
					{
						answerLine=((ReplaceholderComponent)qc).getReplacementValue();
					}
					catch (OmException e)
					{
						throw new OmUnexpectedException(e.getMessage(),e);
					}
				}
				else if (qc instanceof VariableComponent)
				{
					answerLine=((VariableComponent)qc).getValue();
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Component with id '");
					error.append(answerLine);
					error.append("' is not a box, summaryline, random, replaceholder, variable or enabled answer component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			try
			{
				setAnswerLine(answerLine);
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Execute "processanswerline" task that search and set a valid answer line for summary.
	 */
	protected void executeProcessAnswerLine()
	{
		try
		{
			setAnswerLine(getAnswerLine());
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Execute "appendsummaryline" task that appends a new line to the summary.
	 * @param summaryLine Summary line. Can be the identifier of a summaryline (works even 
	 * if its type is not summaryline) or identifier of a &lt;random&gt;, &lt;replaceholder&gt; 
	 * or &lt;variable&gt; component or even a string literal if it start with @
	 */
	protected void executeAppendSummaryLine(String summaryLine)
	{
		if (summaryLine==null || summaryLine.equals(""))
		{
			throw new TaskFormatRuntimeException("Missing required summary line in parameters");
		}
		else
		{
			if (summaryLine.charAt(0)==DEFAULT_LITERAL_PREFIX)
			{
				if (summaryLine.length()>1)
				{
					summaryLine=AnswerChecking.replaceEscapeCharsIgnoreNewLine(summaryLine.substring(1));
				}
				else
				{
					summaryLine="";
				}
			}
			else
			{
				QComponent qc=null;
				try
				{
					qc=getComponent(AnswerChecking.replaceEscapeCharsIgnoreNewLine(summaryLine));
				}
				catch (OmDeveloperException e)
				{
					StringBuffer error=new StringBuffer();
					error.append("Can't find a component with id: ");
					error.append(summaryLine);
					throw new TaskFormatRuntimeException(error.toString(),e);
				}
				if (qc instanceof SummaryLineComponent)
				{
					summaryLine=((SummaryLineComponent)qc).getSummaryLine();
				}
				else if (qc instanceof RandomComponent)
				{
					summaryLine=((RandomComponent)qc).getValue();
				}
				else if (qc instanceof ReplaceholderComponent)
				{
					try
					{
						summaryLine=((ReplaceholderComponent)qc).getReplacementValue();
					}
					catch (OmException e)
					{
						summaryLine=null;
					}
				}
				else if (qc instanceof VariableComponent)
				{
					summaryLine=((VariableComponent)qc).getValue();
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Component with id '");
					error.append(summaryLine);
					error.append("' is not a summaryline, random, replaceholder or variable component");
					throw new TaskFormatRuntimeException(error.toString());
				}
			}
			try
			{
				appendSummaryLine(summaryLine);
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Execute "processsummarylines" task that search and append summary lines to summary.
	 */
	protected void executeProcessSummaryLines()
	{
		List<String> summaryLines=null;
		try
		{
			summaryLines=getSummaryLines();
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
		for (String summaryLine:summaryLines)
		{
			try
			{
				appendSummaryLine(summaryLine);
			}
			catch (OmDeveloperException e)
			{
				throw new OmUnexpectedException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Execute "setcustomresult" task that sets a custom question result.
	 * @param customResult Custom result information. It expects 2 values separated by a comma: 
	 * first custom result's name and second custom result's value. Each value can be an identifier 
	 * of a &lt;random&gt;, &lt;replaceholder&gt;, &lt;variable&gt; or answer component or even 
	 * a string literal if they start with @
	 */
	protected void executeSetCustomResult(String customResult)
	{
		if (customResult==null || customResult.equals(""))
		{
			throw new TaskFormatRuntimeException(
					"Missing required custom result's name and value separated by , in parameters");
		}
		else
		{
			int iEndCustomResultName=AnswerChecking.indexOfCharacter(
					customResult,DEFAULT_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndCustomResultName==-1)
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing , to separate custom result's name and value in parameters: ");
				error.append(customResult);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else if (iEndCustomResultName+1>=customResult.length())
			{
				StringBuffer error=new StringBuffer();
				error.append("Missing required custom result's value in parameters: ");
				error.append(customResult);
				throw new TaskFormatRuntimeException(error.toString());
			}
			else
			{
				String customResultName=customResult.substring(0,iEndCustomResultName);
				if (customResultName.charAt(0)==DEFAULT_LITERAL_PREFIX)
				{
					if (customResultName.length()>1)
					{
						customResultName=AnswerChecking.replaceEscapeChars(customResultName.substring(1));
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("Missing required custom result's name in parameters: ");
						error.append(customResult);
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				else
				{
					customResultName=getComponentValue(customResultName);
				}
				String customResultValue=customResult.substring(iEndCustomResultName+1);
				if (customResultValue.charAt(0)==DEFAULT_LITERAL_PREFIX)
				{
					if (customResultValue.length()>1)
					{
						customResultValue=
								AnswerChecking.replaceEscapeChars(customResultValue.substring(1));
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("Missing required custom result's value in parameters: ");
						error.append(customResult);
						throw new TaskFormatRuntimeException(error.toString());
					}
				}
				else
				{
					customResultValue=getComponentValue(customResultValue);
				}
				try
				{
					getResults().setCustomResult(customResultName,customResultValue);
				}
				catch (OmDeveloperException e)
				{
					throw new OmUnexpectedException(e.getMessage(),e);
				}
			}
		}
	}
	
	/**
	 * Execute "sendresults" task that causes the information in the Results object to be sent 
	 * to the test navigator and stored.
	 */
	protected void executeSendResults()
	{
		try
		{
			sendResults();
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e.getMessage(),e);
		}
	}
	
	/**
	 * Override this method if you want to implement new custom tasks.<br/><br/>
	 * This implementation execute standard tasks and some new special tasks required to emulate
	 * some functionality implemented by default in other generic question classes.
	 * @param task Task
	 */
	protected void doTask(Task task)
	{
		if (task.getTask().equals("initializerandoms"))
		{
			// Execute "initializerandoms" task
			executeInitializeRandoms(task.getParameters());
		}
		else if (task.getTask().equals("initializeplaceholders"))
		{
			// Execute "initializeplaceholders" task
			executeInitializePlaceholders(task.getParameters());
		}
		else if (task.getTask().equals("uninitializeplaceholders"))
		{
			// Execute "uninitializeplaceholders" task
			executeUninitializePlaceholders();
		}
		else if (task.getTask().equals("setplaceholder"))
		{
			// Execute "setplaceholder" task
			executeSetPlaceholder(task.getParameters());
		}
		else if (task.getTask().equals("removeplaceholder"))
		{
			// Execute "removeplaceholder" task
			executeRemovePlaceholder(task.getParameters());
		}
		else if (task.getTask().equals("setcounters"))
		{
			// Execute "setcounters" task
			executeSetCounters(task.getParameters());
		}
		else if (task.getTask().equals("initializecounters"))
		{
			// Execute "initializecounters" task
			executeInitializeCounters();
		}
		else if (task.getTask().equals("resetcounters"))
		{
			// Execute "resetcounters" task
			executeResetCounters();
		}
		else if (task.getTask().equals("updatecounters"))
		{
			// Execute "updatecounters" task
			executeUpdateCounters(task.getParameters());
		}
		else if (task.getTask().equals("setattempt"))
		{
			// Execute "setattempt" task
			executeSetAttempt(task.getParameters());
		}
		else if (task.getTask().equals("increaseattempt"))
		{
			// Execute "increaseattempt" task
			executeIncreaseAttempt();
		}
		else if (task.getTask().equals("decreaseattempt"))
		{
			// Execute "decreaseattempt" task
			executeDecreaseAttempt();
		}
		else if (task.getTask().equals("setmaxattempts"))
		{
			// Execute "setmaxattempts" task
			executeSetMaxAttempts(task.getParameters());
		}
		else if (task.getTask().equals("setprogressinfo"))
		{
			// Execute "setprogressinfo" task
			executeSetProgressInfo(task.getParameters());
		}
		else if (task.getTask().equals("resetquestiontestingexceptions"))
		{
			// Execute "resetquestiontestingexceptions" task
			executeResetQuestionTestingExceptions();
		}
		else if (task.getTask().equals("addquestiontimetestingexception"))
		{
			// Execute "addquestiontimetestingexception" task
			executeAddQuestionTimeTestingException(task.getParameters());
		}
		else if (task.getTask().equals("resetquestiontestingnestedexceptions"))
		{
			// Execute "resetquestiontestingnestedexceptions"
			executeResetQuestionTestingNestedExceptions();
		}
		else if (task.getTask().equals("addquestiontimetestingnestedexception"))
		{
			// Execute "addquestiontimetestingnestedexception" task
			executeAddQuestionTimeTestingNestedException(task.getParameters());
		}
		else if (task.getTask().equals("end"))
		{
			// Execute "end" task
			executeEnd();
		}
		else if (task.getTask().equals("setidanswersnull"))
		{
			// Execute "setidanswersnull"
			executeSetIdAnswersNull();
		}
		else if (task.getTask().equals("resetidanswers"))
		{
			// Execute "resetidanswers"
			executeResetIdAnswers();
		}
		else if (task.getTask().equals("addidanswer"))
		{
			// Execute "addidanswer"
			executeAddIdAnswer(task.getParameters());
		}
		else if (task.getTask().equals("setmatchdefault"))
		{
			// Execute "setmatchdefault" task
			executeSetMatchDefault(task.getParameters());
		}
		else if (task.getTask().equals("setright"))
		{
			// Execute "setright" task
			executeSetRight(task.getParameters());
		}
		else if (task.getTask().equals("processdefaultanswer"))
		{
			// Execute "processdefaultanswer" task
			executeProcessDefaultAnswer();
		}
		else if (task.getTask().equals("processanswer"))
		{
			// Execute "processanswer" task
			executeProcessAnswer();
		}
		else if (task.getTask().equals("setscore"))
		{
			// Execute "setscore" task
			executeSetScore(task.getParameters());
		}
		else if (task.getTask().equals("setdefaultscore"))
		{
			// Execute "setdefaultscore" task
			executeSetDefaultScore();
		}
		else if (task.getTask().equals("setquestionline"))
		{
			// Execute "setquestionline" task
			executeSetQuestionLine(task.getParameters());
		}
		else if (task.getTask().equals("processquestionline"))
		{
			// Execute "processquestionline" task
			executeProcessQuestionLine();
		}
		else if (task.getTask().equals("setanswerline"))
		{
			// Execute "setanswerline" task
			executeSetAnswerLine(task.getParameters());
		}
		else if (task.getTask().equals("processanswerline"))
		{
			// Execute "processanswerline" task
			executeProcessAnswerLine();
		}
		else if (task.getTask().equals("appendsummaryline"))
		{
			// Execute "appendsummaryline" task
			executeAppendSummaryLine(task.getParameters());
		}
		else if (task.getTask().equals("processsummarylines"))
		{
			// Execute "processsummarylines" task
			executeProcessSummaryLines();
		}
		else if (task.getTask().equals("setcustomresult"))
		{
			// Execute "setcustomresult"
			executeSetCustomResult(task.getParameters());
		}
		else if (task.getTask().equals("sendresults"))
		{
			// Execute "sendresults" task
			executeSendResults();
		}
		else
		{
			// Execute standard tasks
			task.execute();
		}
	}
}
