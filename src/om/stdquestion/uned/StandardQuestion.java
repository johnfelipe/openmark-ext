package om.stdquestion.uned;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import om.OmDeveloperException;
import om.OmException;
import om.OmUnexpectedException;
import om.graph.GraphFormatException;
import om.graph.GraphItem;
import om.graph.uned.World;
import om.helper.uned.AnswerChecking;
import om.helper.uned.Task;
import om.question.ActionParams;
import om.question.ActionRendering;
import om.question.InitParams;
import om.question.Rendering;
import om.stdcomponent.uned.AdvancedFieldComponent;
import om.stdcomponent.uned.AnswerComboComponent;
import om.stdcomponent.uned.Answerable;
import om.stdcomponent.uned.BoxComponent;
import om.stdcomponent.uned.CanvasComponent;
import om.stdcomponent.uned.CheckboxComponent;
import om.stdcomponent.uned.DragBoxComponent;
import om.stdcomponent.uned.DropBoxComponent;
import om.stdcomponent.uned.DropdownComponent;
import om.stdcomponent.uned.EditFieldComponent;
import om.stdcomponent.uned.JMEComponent;
import om.stdcomponent.uned.ParametersComponent;
import om.stdcomponent.uned.RadioBoxComponent;
import om.stdcomponent.uned.RandomComponent;
import om.stdcomponent.uned.ReplaceholderComponent;
import om.stdcomponent.uned.SummaryLineComponent;
import om.stdcomponent.uned.TaskComponent;
import om.stdcomponent.uned.TextComponent;
import om.stdcomponent.uned.VariableComponent;
import om.stdcomponent.uned.WordSelectComponent;
import om.stdquestion.QComponent;

import org.w3c.dom.Document;

import util.xml.XML;

// UNED: 21-06-2011 - dballestin
/**
 * Standard Om question with question components defined in initial XML.<br/><br/>
 * This version of StandardQuestion component uses om.stdquestion.uned.QComponentManager as the 
 * component manager so some new components and new versions of standard components are used.<br/><br/>
 * Moreover it add new methods useful to use with a generic class for questions.
 */
public class StandardQuestion extends om.stdquestion.StandardQuestion
{
	/** Default answer's identifier: +defaultanswer+ */
	public static String DEFAULT_ANSWER_ID="+defaultanswer+";
	
	/** Name of the custom action */
	public static final String CUSTOM_ACTION_NAME="actionCustom";
	
	/** Maximum length in characters of a question line */
	private static final int QUESTION_LINE_MAX_CHARS=255;
	
	/** Maximum length in characters of an answer line */
	private static final int ANSWER_LINE_MAX_CHARS=255;
	
	/** 
	 * Borders of temporal placeholders used to solve a problem that occurs when user types 
	 * a text matching an already defined placeholder in an editfield or advancedfield 
	 */
	private static final String TEMPORAL_PLACEHOLDER_BORDER="%_%";
	
	/** Question's parameters component */
	private ParametersComponent parameters=null;
	
	/** Question's counters */
	private QuestionCounters counters=null;
	
	/** List of answer's components identifiers */
	private List<String> idAnswers=null;
	
	/** "for" identifiers stack */
	private Stack<String> forIdAnswers=new Stack<String>();
	
	/** List of components excluded from testing for displaying at question time */
	private List<String> questionTimeTestingExceptions=new LinkedList<String>();
	
	/** 
	 * List of components (including subcomponents) excluded from testing for displaying 
	 * at question time
	 */
	private List<String> questionTimeTestingNestedExceptions=new LinkedList<String>();
	
	/** Which question attempt the user is on */
	private int iAttempt=0;
	
	/** Maximum number of attempts allowed (after that it tells you the answer) */
	private int iMaxAttempts=3;
	
	/** State flag to remember if last user's answer was right (true) or wrong (false) */
	private boolean right=false;
	
	/** State flag to remember if last user's anwer matches default answer (true) or not (false) */
	private boolean matchDefault=false;
	
	/** State flag to indicate if placeholders has been initialized */
	private boolean placeholdersInitialized=false;
	
	/** Map of all currently-set temporal placeholders */
	private Map<String,String> mPlaceholdersTmp=new HashMap<String,String>();
	
	/** Index of next temporal placeholder */
	private int placeholderTmpIndex=1;
	
	/**
	 * A class to group some question counters.
	 */
	public class QuestionCounters
	{
		private int selectableAnswers;
		private int selectableRightAnswers;
		private int selectableWrongAnswers;
		private int selectedAnswers;
		private int selectedRightAnswers;
		private int selectedWrongAnswers;
		private int unselectedAnswers;
		private int unselectedRightAnswers;
		private int unselectedWrongAnswers;
		
		public QuestionCounters(int selectableAnswers,int selectableRightAnswers,int selectableWrongAnswers)
		{
			this.selectableAnswers=selectableAnswers;
			this.selectableRightAnswers=selectableRightAnswers;
			this.selectableWrongAnswers=selectableWrongAnswers;
			reset();
		}
		
		/**
		 * Reset counters to their initial state.
		 */
		public void reset()
		{
			selectedAnswers=0;
			selectedRightAnswers=0;
			selectedWrongAnswers=0;
			unselectedAnswers=selectableAnswers;
			unselectedRightAnswers=selectableRightAnswers;
			unselectedWrongAnswers=selectableWrongAnswers;
		}
		
		/** 
		 * Number of selectable answers.<br/><br/>
		 * Note that it is not number of answer's components.<br/>
		 * It is more the maximum number of answer's components selectable by user at same time 
		 * in an attempt. 
		 * @return Number of selectable answers
		 */
		public int getSelectableAnswers()
		{
			return selectableAnswers;
		}
		
		/** 
		 * Number of selectable right answers.<br/><br/>
		 * Note that it is not the number of right answer's components.<br/>
		 * It is more the maximum number of right answer's components selectable by user at same time 
		 * in an attempt.
		 * @return Number of selectable right answers
		 */
		public int getSelectableRightAnswers()
		{
			return selectableRightAnswers;
		}
		
		/** 
		 * Number of selectable wrong answers.<br/><br/>
		 * Note that it is not the number of wrong answer's components.<br/>
		 * It is more the maximum number of wrong answer's components selectable by user at same time 
		 * in an attempt.
		 * @return Number of selectable wrong answers
		 */
		public int getSelectableWrongAnswers()
		{
			return selectableWrongAnswers;
		}
		
		/** Number of selected answers */
		public int getSelectedAnswers()
		{
			return selectedAnswers;
		}
		
		/** Number of selected right answers */
		public int getSelectedRightAnswers()
		{
			return selectedRightAnswers;
		}
		
		/** Number of selected wrong answers */
		public int getSelectedWrongAnswers()
		{
			return selectedWrongAnswers;
		}
		
		/** Number of unselected answers */
		public int getUnselectedAnswers()
		{
			return unselectedAnswers;
		}
		
		/** Number of unselected right answers */
		public int getUnselectedRightAnswers()
		{
			return unselectedRightAnswers;
		}
		
		/** Number of unselected wrong answers */
		public int getUnselectedWrongAnswers()
		{
			return unselectedWrongAnswers;
		}
		
		/** 
		 * Distance to the right answer, 0 means that this is the right answer<br/><br/>
		 * You can think about it as the number of errors in answer
		 */
		public int getRightDistance()
		{
			return selectedWrongAnswers>unselectedRightAnswers?selectedWrongAnswers:unselectedRightAnswers;
		}
		
		/**
		 * Select an answer counting the number of selected, unselected, right and wrong answers.
		 * @param right true if the selected answer is right, false if it is wrong
		 */
		public void selectAnswer(boolean right)
		{
			selectedAnswers++;
			unselectedAnswers--;
			if (right)
			{
				selectedRightAnswers++;
				unselectedRightAnswers--;
			}
			else
			{	
				selectedWrongAnswers++;
				unselectedWrongAnswers--;
			}
		}
		
		/**
		 * Select n answers counting the number of selected, unselected, right and wrong answers.
		 * @param n Number of answers
		 * @param right true if the selected answers are right, false if they are wrong
		 */
		public void selectAnswers(int n,boolean right)
		{
			selectedAnswers+=n;
			unselectedAnswers-=n;
			if (right)
			{
				selectedRightAnswers+=n;
				unselectedRightAnswers-=n;
			}
			else
			{	
				selectedWrongAnswers+=n;
				unselectedWrongAnswers-=n;
			}
		}
	}
	
	/**
	 * Initializes this standard Om question using om.stdquestion.uned.QComponentManager as the
	 * component manager.
	 */
	@Override
	public Rendering init(Document d,InitParams initParams) throws OmException
	{
		Rendering r=super.init(d,initParams,new QComponentManager(initParams));
		
		// Get max numbers of attempts allowed from parameters
		setMaxAttempts(getParameters().getMaxAttempts());
		
		return r;
	}
	
	@Override
	protected void init() throws OmException
	{
		// Canvasses need to be initializated and painted before rendering question
		for (CanvasComponent canvas:getCanvasses())
		{
			// First we need to initialize all graph items from all worlds in this canvas
			for (om.graph.World w:canvas.getWorlds())
			{
				for (GraphItem gIt:((World)w).getItems())
				{
					try
					{
						gIt.checkInit();
					}
					catch (GraphFormatException e)
					{
						throw new OmDeveloperException(e.getMessage(),e);
					}
				}
			}
			
			// Finally we paint canvas
			canvas.clear();
			canvas.repaint();
		}
	}
	
	@Override
	public void setPlaceholder(String sPlaceholder,String sValue)
	{
		super.setPlaceholder(sPlaceholder,sValue);
	}
	
	/**
	 * @return Map of all currently-set temporal placeholders
	 */
	protected Map<String, String> getTemporalPlaceholders()
	{
		return mPlaceholdersTmp;
	}
	
	/**
	 * Set a new temporal placeholder with the value passed and returns the 
	 * placeholder's name (including borders)
	 * @param value Value
	 * @return Placeholder's name (including borders)
	 */
	public String newTemporalPlaceholder(String value)
	{
		StringBuffer newPlaceholder=new StringBuffer();
		Random r=new Random();
		int maxrandom='z'-'a'+1;
		char c0=(char)('a'+r.nextInt(maxrandom));
		char c1=(char)('a'+r.nextInt(maxrandom));
		char c2=(char)('a'+r.nextInt(maxrandom));
		newPlaceholder.append(c0);
		newPlaceholder.append(c1);
		newPlaceholder.append(c2);
		newPlaceholder.append(placeholderTmpIndex);
		getTemporalPlaceholders().put(newPlaceholder.toString(),value);
		placeholderTmpIndex++;
		newPlaceholder.insert(0,TEMPORAL_PLACEHOLDER_BORDER);
		newPlaceholder.append(TEMPORAL_PLACEHOLDER_BORDER);
		return newPlaceholder.toString();
	}
	
	/**
	 * Fix temporal placeholders just before returning output.<br/><br/>
	 * @param r Output about to be returned
	 */
	protected void fixTemporalPlaceholders(Rendering r)
	{
		XML.replaceTokens(r.getXHTML(),TEMPORAL_PLACEHOLDER_BORDER,getTemporalPlaceholders());		
	}
	
	/**
	 * Clear map of all currently-set temporal placeholders and restart the index 
	 * of next temporal placeholder to 1
	 */
	protected void resetTemporalPlaceholders()
	{
		getTemporalPlaceholders().clear();
		placeholderTmpIndex=1;
	}
	
	@Override
	public synchronized ActionRendering action(ActionParams ap) throws OmException
	{
		setCurrentActionRendering(new ActionRendering());
		
		// Pass to document
		getDocument().action(ap);
		// Render current state
		if(!getCurrentActionRendering().isSessionEnd())
			getDocument().render(getCurrentActionRendering(),false);
		
		// Fix placeholders
		fixPlaceholders(getCurrentActionRendering());
		fixTemporalPlaceholders(getCurrentActionRendering());
		
		// Reset temporal placeholders
		resetTemporalPlaceholders();
		
		ActionRendering ar=getCurrentActionRendering();
		setCurrentActionRendering(null);
		return ar;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void callback(String sCallback) throws OmException {
		if (sCallback.equals(CUSTOM_ACTION_NAME))
		{
			if (!getCheckedCallbacks().contains(sCallback))
			{
				StringBuffer error=new StringBuffer();
				error.append("Error running callback ");
				error.append(CUSTOM_ACTION_NAME);
				error.append("(\"\"): checkCallback was not called");
				throw new OmDeveloperException(error.toString());
			}
			try
			{
				Class[] args={String.class};
				Object[] objs={""};
				Method m=getClass().getMethod(CUSTOM_ACTION_NAME,args);
				m.invoke(this,objs);
			}
			catch (Exception e)
			{
				super.callback(CUSTOM_ACTION_NAME);
			}
		}
		else
		{
			super.callback(sCallback);
		}
	}

	/**
	 * Calls a particular callback function. The function must previously have
	 * been checked using checkCallback().
	 * @param sCallback Name of callback
	 * @param custom Selector to use with custom action
	 * @throws OmException If the callback throws an exception or there was
	 *   an error calling it
	 */
	@SuppressWarnings("rawtypes")
	public void callback(String sCallback,String custom) throws OmException
	{
		if (sCallback.equals(CUSTOM_ACTION_NAME))
		{
			if (!getCheckedCallbacks().contains(sCallback))
			{
				StringBuffer error=new StringBuffer();
				error.append("Error running callback ");
				error.append(CUSTOM_ACTION_NAME);
				error.append("(\"");
				error.append(custom);
				error.append("\"): checkCallback was not called");
				throw new OmDeveloperException(error.toString());
			}
			Class[] args={String.class};
			Object[] objs={custom};
			Method m=null;
			try
			{
				m=getClass().getMethod(CUSTOM_ACTION_NAME,args);
			}
			catch (NoSuchMethodException e)
			{
				super.callback(CUSTOM_ACTION_NAME);
			}
			try
			{
				m.invoke(this,objs);
			}
			catch(InvocationTargetException e)
			{
				if (e.getCause() instanceof OmException)
				{
					throw (OmException)e.getCause();
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append("Exception in callback ");
					error.append(sCallback);
					error.append("(\"");
					error.append(custom);
					error.append("\")");
					throw new OmException(error.toString(),e.getCause());
				}
			}
			catch(Exception e)
			{
				throw new OmUnexpectedException(e);
			}
		}
		else
		{
			super.callback(sCallback);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void checkCallback(String sCallback) throws OmDeveloperException
	{
		if (sCallback.equals(CUSTOM_ACTION_NAME))
		{
			try
			{
	            Class[] args ={String.class}; 
				Method m=getClass().getMethod(sCallback,args);
				if(m.getReturnType()!=void.class)
					throw new OmDeveloperException(
						"Callback method "+sCallback+"(String custom) must return void");
				if(!Modifier.isPublic(m.getModifiers()))
					throw new OmDeveloperException(
						"Callback method "+sCallback+"(String custom) must be public");
				if(Modifier.isStatic(m.getModifiers()))
					throw new OmDeveloperException(
						"Callback method "+sCallback+"(String custom) may not be static");
				if(Modifier.isAbstract(m.getModifiers()))
					throw new OmDeveloperException(
						"Callback method "+sCallback+"(String custom) may not be abstract");

				getCheckedCallbacks().add(sCallback);
			}
			catch(NoSuchMethodException e)
			{
				super.checkCallback(sCallback);
			}
		}
		else
		{
			super.checkCallback(sCallback);
		}
	}
	
	@Override
	public QComponent getComponent(String sID) throws OmDeveloperException
	{
		return super.getComponent(sID);
	}

	/**
	 * Searches the tree for components of a given class.<br/>
	 * Returns an array of the components, in document order.<br/>
	 * You may cast the array to one of the specifed class.
	 * @param <T> The type of component to find.
	 * @param c Class to look for
	 * @return Array of components (zero-length if none)
	 */
	protected <T extends QComponent> List<T> getComponents(Class<T> c)
	{
		return getDocument().find(c);
	}
	
	/**
	 * Searches the tree for answer components.<br/>
	 * Returns an array of the answer components, in document order except radioboxes.<br/>
	 * Radioboxes are alse tried to be inserted in document order, but radioboxes of the same group
	 * are always inserted together, even if there are other answer components between them.
	 * @return Array of answer components (zero-length if none)
	 */
	public List<Answerable> getAnswerComponents()
	{
		List<Answerable> answerComponents=new ArrayList<Answerable>();
		List<String> radiogroups=new LinkedList<String>();
		for (QComponent qc:getComponents(QComponent.class))
		{
			if (qc instanceof Answerable)
			{
				Answerable qcAnswer=(Answerable)qc;
				if (qcAnswer.isAnswerEnabled())
				{
					if (qcAnswer instanceof RadioBoxComponent)
					{
						RadioBoxComponent radiobox=(RadioBoxComponent)qcAnswer;
						String radiogroup=null;
						try
						{
							radiogroup=radiobox.getString(RadioBoxComponent.PROPERTY_GROUP);
						}
						catch (OmDeveloperException e)
						{
							radiogroup=null;
						}
						if (!radiogroups.contains(radiogroup))
						{
							for (RadioBoxComponent radioboxGroup:getRadioBoxes(radiogroup))
							{
								answerComponents.add(radioboxGroup);
							}
							radiogroups.add(radiogroup);
						}
					}
					else
					{
						answerComponents.add(qcAnswer);
					}
				}
			}
		}
		return answerComponents;
	}
	
	public List<String> getAllAnswerIds()
	{
		List<String> allAnswerIds=new ArrayList<String>();
		for (Answerable qc:getAnswerComponents())
		{
			try
			{
				allAnswerIds.add(((QComponent)qc).getID());
			}
			catch (OmDeveloperException e)
			{
			}
		}
		return allAnswerIds;
	}
	
	/**
	 * Returns the parameters component for this question.<br/>
	 * The first time that this method is called for a question tris to find &lt;parameters&gt; 
	 * component in question.xml or creates a default one if it can't be found.<br/>
	 * In subsequent calls the same reference to parameters component is returned to assure that if
	 * properties are changed by program these changes will be effective. 
	 * @return Component Parameters component.
	 * @throws OmDeveloperException If it is not found and can't be created a default one
	 */
	public ParametersComponent getParameters() throws OmDeveloperException
	{
		if (parameters==null)
		{
			List<ParametersComponent> lParameters=getComponents(ParametersComponent.class);
			if (lParameters.isEmpty())
			{
				// default parameters
				parameters=new ParametersComponent(this);
				parameters.defineProperties();
			}
			else
			{
				// question.xml parameters
				parameters=lParameters.get(0);
			}
		}
		return parameters;
	}
	/**
	 * Returns a class with some question counters.
	 * @return class with some question counters
	 */
	public QuestionCounters getCounters()
	{
		return counters;
	}
	
	/**
	 * Set the class with some question values used for tests.
	 * @param counters Class with some question counters
	 */
	protected void setCounters(QuestionCounters counters)
	{
		this.counters=counters;
	}
	
	/**
	 * Returns the list of answer's components identifiers or null if user passed the question.<br/><br/>
	 * @return List of answer's components identifiers or null if user passed the question
	 */
	public List<String> getIdAnswers()
	{
		return idAnswers;
	}
	
	/**
	 * Set the list of answer's component identifiers to null.<br/><br/>
	 * Note that it is not the same than resetIdAnswers.<br/><br/>
	 * It is used to distinguish user passing the question versus an user response without answers selected.
	 */
	protected void setIdAnswersNull()
	{
		idAnswers=null;
	}
	
	/**
	 * Clear the list of answer's component identifiers
	 */
	protected void resetIdAnswers()
	{
		if (idAnswers==null)
		{
			idAnswers=new LinkedList<String>();
		}
		else
		{
			idAnswers.clear();
		}
	}
	
	/**
	 * Adds the identifier received to the list of answer's component identifiers
	 * @param idAnswer idAnswer Identifier of component selected for answer
	 */
	protected void addIdAnswer(String idAnswer)
	{
		if (idAnswers==null)
		{
			idAnswers=new LinkedList<String>();
		}
		idAnswers.add(idAnswer);
	}
	
	/**
	 * Push an answer component's identifier onto the top of the "for" identifiers stack
	 * @param forIdAnswer Component's identifier
	 */
	public void pushForIdAnswer(String forIdAnswer)
	{
		forIdAnswers.push(forIdAnswer);
	}
	
	/**
	 * Looks at the answer component's identifier at the top of the "for" identifiers stack 
	 * without removing it from the stack
	 * @return Answer component's identifier at the top of the "for" identifiers stack,
	 * null if "for" identifiers stack is empty
	 */
	public String peekForIdAnswer()
	{
		return forIdAnswers.isEmpty()?null:forIdAnswers.peek();
	}
	
	/**
	 * Removes the answer component's identifier at the top of the "for" identifiers stack 
	 * and returns it
	 * @return Answer component's identifier at the top of the "for" identifiers stack,
	 * null if "for" identifiers stack is empty
	 */
	public String popForIdAnswer()
	{
		return forIdAnswers.isEmpty()?null:forIdAnswers.pop();
	}
	
	/**
	 * Clear the "for" identifiers stack
	 */
	protected void resetForIdAnswers()
	{
		forIdAnswers.clear();
	}
	
	/**
	 * Returns the list of component's identifiers excluded from testing for displaying 
	 * at question time
	 * @return List of components excluded from testing for displaying at question time
	 */
	protected List<String> getQuestionTestingExceptions()
	{
		return questionTimeTestingExceptions;
	}
	
	/**
	 * Adds the identifier received to the list of component's identifiers excluded from testing 
	 * for displaying at question time
	 * @param exception Identifier of text component to exclude from testing for displaying 
	 * at question time
	 */
	protected void addQuestionTimeTestingException(String exception)
	{
		questionTimeTestingExceptions.add(exception);
	}
	
	/**
	 * Test if the component received match one of the components from the list of 
	 * component's identifiers excluded from testing for displaying at question time
	 * @param qc Component
	 * @return true if the component received match one of the components from the list of 
	 * component's identifiers excluded from testing for displaying at question time, false otherwise
	 * @throws OmDeveloperException
	 */
	public boolean isQuestionTimeTestingException(QComponent qc) throws OmDeveloperException
	{
		return qc.hasUserSetID() && questionTimeTestingExceptions.contains(qc.getID());
	}
	
	/**
	 * Clear the list of component's identifiers excluded from testing for displaying at question time
	 */
	protected void resetQuestionTestingExceptions()
	{
		questionTimeTestingExceptions.clear();
	}
	
	/**
	 * Returns the list of component's identifiers (including subcomponents) excluded from testing 
	 * for displaying at question time
	 * @return List of component's identifiers (including subcomponents) excluded from testing 
	 * for displaying at question time
	 */
	protected List<String> getQuestionTimeTestingNestedExceptions()
	{
		return questionTimeTestingNestedExceptions;
	}
	
	/**
	 * Adds the identifier received to the list of component's identifiers (including subcomponents) 
	 * excluded from testing for displaying at question time
	 * @param exception Identifier of component (including subcomponents) to exclude from testing 
	 * for displaying at question time
	 */
	protected void addQuestionTimeTestingNestedException(String exception)
	{
		questionTimeTestingNestedExceptions.add(exception);
	}
	
	/**
	 * Test if the component received match one of the components from the list of 
	 * component's identifiers (including subcomponents) excluded from testing for displaying 
	 * at question time
	 * @param qc Component
	 * @return true if the component received match one of the components from the list of 
	 * component's identifiers (including subcomponents) excluded from testing for displaying 
	 * at question time, false otherwise
	 * @throws OmDeveloperException
	 */
	public boolean isQuestionTimeTestingNestedException(QComponent qc) throws OmDeveloperException
	{
		return qc.hasUserSetID() && questionTimeTestingNestedExceptions.contains(qc.getID());
	}
	
	/**
	 * Clear the list of component's identifiers (including subcomponents) excluded from testing 
	 * for displaying at question time
	 */
	protected void resetQuestionTestingNestedExceptions()
	{
		questionTimeTestingNestedExceptions.clear();
	}
	
	/**
	 * @return Which question attempt the user is on
	 */
	public int getAttempt() {
		return iAttempt;
	}
	
	/**
	 * Set which question attempt the user is on
	 * @param iAttempt Question attempt (1 for first, 2 for second, etc.)
	 */
	protected void setAttempt(int iAttempt) {
		this.iAttempt = iAttempt;
	}
	
	/**
	 * Get the maximum number of attempts permitted. After that it will tell you the answer. 
	 * The default is 3.
	 * @return maximum number of attempts permitted
	 */
	public int getMaxAttempts() {
		return iMaxAttempts;
	}
	
	/**
	 * Set the maximum number of attempts permitted. After that it will tell you the answer. 
	 * The default is 3.
	 * @param iMaxAttempts Number of permitted attempts
	 */
	protected void setMaxAttempts(int iMaxAttempts)
	{
		this.iMaxAttempts=iMaxAttempts;
	}
	
	/**
	 * Get if last user's answer rightness
	 * @return true if last user's answer was right, false otherwise
	 */
	public boolean isRight()
	{
		return right;
	}
	
	/**
	 * Set last user's answer rightness
	 * @param right Last user's answer rightness (true: right, false: wrong)
	 */
	protected void setRight(boolean right)
	{
		this.right=right;
	}
	
	/**
	 * Get if last user's anwer matches default answer
	 * @return true if last user's answer matches default answer, false otherwise
	 */
	public boolean isMatchDefault()
	{
		return matchDefault;
	}
	
	/**
	 * Set if last user's answer matches default answer
	 * @param matchDefault true if last user's answer matches default answer, false otherwise 
	 */
	protected void setMatchDefault(boolean matchDefault)
	{
		this.matchDefault=matchDefault;
	}
	
	/* State flag to detect if placeholders has been initialized */
	
	/**
	 * Get state flag to indicate if placeholders has been initialized
	 */
	public boolean isPlaceholdersInitialized()
	{
		return placeholdersInitialized;
	}
	
	/**
	 * Set state flag to indicate if placeholders has been 
	 * initialized
	 * @param placeholdersInitialized State flag to indicate if placeholders has been 
	 * initialized
	 */
	protected void setPlaceholdersInitialized(boolean placeholdersInitialized)
	{
		this.placeholdersInitialized = placeholdersInitialized;
	}

	/**
	 * Searches the tree for radiobox components.<br/>
	 * Returns an array of the radiobox components, in document order.<br/>
	 * @return Array of radiobox components
	 */
	protected List<RadioBoxComponent> getRadioBoxes()
	{
		return getComponents(RadioBoxComponent.class);
	}
	
	/**
	 * Searches the tree for checkbox components.<br/>
	 * Returns an array of the checkbox components, in document order.<br/>
	 * @return Array of checkbox components
	 */
	protected List<CheckboxComponent> getCheckboxes()
	{
		return getComponents(CheckboxComponent.class);
	}
	
	/**
	 * Searches the tree for editfield components.<br/>
	 * Returns an array of the editfield components, in document order.<br/>
	 * @return Array of editfield components
	 */
	protected List<EditFieldComponent> getEditFields()
	{
		return getComponents(EditFieldComponent.class);
	}
	
	/**
	 * Searches the tree for advancedfield components.<br/>
	 * Returns an array of the advancedfield components, in document order.<br/>
	 * @return Array of advancedfield components
	 */
	protected List<AdvancedFieldComponent> getAdvancedFields()
	{
		return getComponents(AdvancedFieldComponent.class);
	}
	
	/**
	 * Searches the tree for dropdown components.<br/>
	 * Returns an array of the dropdown components, in document order.<br/>
	 * @return Array of dropdown components
	 */
	protected List<DropdownComponent> getDropdowns()
	{
		return getComponents(DropdownComponent.class);
	}
	
	/**
	 * Searches the tree for dropbox components.<br/>
	 * Returns an array of the dropbox components, in document order.<br/>
	 * @return Array of dropbox components
	 */
	protected List<DropBoxComponent> getDropBoxes()
	{
		return getComponents(DropBoxComponent.class);
	}
	
	/**
	 * Searches the tree for dropboxes components that belongs to a group.<br/>
	 * Returns an array of the dropbox components that belongs to a group, in document order.<br/>
	 * @param dropGroup Group
	 * @return Array of dropbox components that belongs to a group
	 */
	public List<DropBoxComponent> getDropBoxes(String dropGroup)
	{
		List<DropBoxComponent> dropBoxes=new ArrayList<DropBoxComponent>();
		if (dropGroup!=null)
		{
			for (DropBoxComponent dropBox:getDropBoxes())
			{
				try
				{
					if (dropGroup.equals(dropBox.getString(DropBoxComponent.PROPERTY_GROUP)))
					{
						dropBoxes.add(dropBox);
					}
					
				}
				catch (OmDeveloperException e)
				{
				}
			}
		}
		return dropBoxes;
	}
	
	/**
	 * Searches the tree for dragbox components.<br/>
	 * Returns an array of the dragbox components, in document order.<br/>
	 * @return Array of dragbox components
	 */
	protected List<DragBoxComponent> getDragBoxes()
	{
		return getComponents(DragBoxComponent.class);
	}
	
	/**
	 * Searches the tree for dragboxes components that belongs to a group.<br/>
	 * Returns an array of the dragbox components that belongs to a group, in document order.<br/>
	 * @param dragGroup Group
	 * @return Array of dragbox components that belongs to a group
	 */
	public List<DragBoxComponent> getDragBoxes(String dragGroup)
	{
		List<DragBoxComponent> dragBoxes=new ArrayList<DragBoxComponent>();
		if (dragGroup!=null)
		{
			for (DragBoxComponent dragBox:getDragBoxes())
			{
				try
				{
					if (dragGroup.equals(dragBox.getString(DropBoxComponent.PROPERTY_GROUP)))
					{
						dragBoxes.add(dragBox);
					}
					
				}
				catch (OmDeveloperException e)
				{
				}
			}
		}
		return dragBoxes;
	}
	
	/**
	 * Searches the tree for wordselect components.<br/>
	 * Returns an array of the wordselect components, in document order.<br/>
	 * @return Array of wordselect components
	 */
	protected List<WordSelectComponent> getWordSelects()
	{
		return getComponents(WordSelectComponent.class);
	}
	
	/**
	 * Searches the tree for jme components.<br/>
	 * Returns an array of the jme components, in document order.<br/>
	 * @return Array of jme components
	 */
	protected List<JMEComponent> getJMEs()
	{
		return getComponents(JMEComponent.class);
	}
	
	/**
	 * Searches the tree for text components.<br/>
	 * Returns an array of the text components, in document order.<br/>
	 * @return Array of text components
	 */
	protected List<TextComponent> getTexts()
	{
		return getComponents(TextComponent.class);
	}
	
	/**
	 * Returns an array of the radio groups, in document order.<br/>
	 * @return Array of radio groups
	 */
	protected List<String> getRadioBoxesGroups()
	{
		List<String> radioBoxesGroups = new ArrayList<String>();
		for (RadioBoxComponent radioBox:getRadioBoxes())
		{
			String radioBoxGroup=null;
			try
			{
				radioBoxGroup=radioBox.getString(RadioBoxComponent.PROPERTY_GROUP);
			}
			catch (OmDeveloperException e)
			{
				radioBoxGroup=null;
			}
			if (radioBoxGroup!=null)
			{
				if (!radioBoxesGroups.contains(radioBoxGroup))
				{
					radioBoxesGroups.add(radioBoxGroup);
				}
			}
		}
		return radioBoxesGroups;
	}
	
	/**
	 * Searches the tree for radiobox components that belongs to a group.<br/>
	 * Returns an array of the radiobox components that belongs to a group, in document order.<br/>
	 * @param radioGroup Group
	 * @return Array of radiobox components that belongs to a group
	 */
	public List<RadioBoxComponent> getRadioBoxes(String radioGroup)
	{
		List<RadioBoxComponent> radioBoxes = new ArrayList<RadioBoxComponent>();
		if (radioGroup!=null)
		{
			for (RadioBoxComponent radioBox:getRadioBoxes())
			{
				try
				{
					if (radioGroup.equals(radioBox.getString(RadioBoxComponent.PROPERTY_GROUP)))
					{
						radioBoxes.add(radioBox);
					}
				}
				catch (OmDeveloperException e)
				{
				}
			}
		}
		return radioBoxes;
	}
	
	/**
	 * Searches the tree for replaceholder components.<br/>
	 * Returns an array of the replaceholder components, in document order.<br/>
	 * @return Array of replaceholder components
	 */
	public List<ReplaceholderComponent> getReplaceholders()
	{
		return getComponents(ReplaceholderComponent.class);
	}
	
	/**
	 * Searches the tree for random components.<br/>
	 * Returns an array of the random components, in document order.<br/>
	 * @return Array of random components
	 */
	public List<RandomComponent> getRandoms()
	{
		return getComponents(RandomComponent.class);
	}
	
	/**
	 * Searches the tree for variable components.<br/>
	 * Returns an array of the variable components, in document order.<br/>
	 * @return Array of variable components
	 */
	public List<VariableComponent> getVariables()
	{
		return getComponents(VariableComponent.class);
	}
	
	/**
	 * @return Return an array with all variable components identifiers
	 */
	public List<String> getVariablesIds()
	{
		List<String> variablesIds=new ArrayList<String>();
		for (VariableComponent variable:getVariables())
		{
			try
			{
				variablesIds.add(variable.getID());
			}
			catch (OmDeveloperException e)
			{
			}
		}
		return variablesIds;
	}
	
	/**
	 * Searches the tree for answercombo components.<br/>
	 * Returns an array of the answercombo components, in document order.<br/>
	 * @return Array of answercombo components
	 */
	protected List<AnswerComboComponent> getAnswerCombos()
	{
		return getComponents(AnswerComboComponent.class);
	}
	
	/**
	 * Searches the tree for canva components.<br/>
	 * Returns an array of the canvas components, in document order.<br/>
	 * @return Array of canvas components
	 */
	public List<CanvasComponent> getCanvasses()
	{
		return getComponents(CanvasComponent.class);
	}
	
	/**
	 * @return Return an array with all answercombo components identifiers (including the special
	 * identifier +defaultanswer+ for the default answer despite it is not referred to 
	 * any answercombo component)
	 */
	public List<String> getAnswerCombosIds()
	{
		List<String> answerCombosIds=new ArrayList<String>();
		answerCombosIds.add(DEFAULT_ANSWER_ID);
		for (AnswerComboComponent answercombo:getAnswerCombos())
		{
			try
			{
				answerCombosIds.add(answercombo.getID());
			}
			catch (OmDeveloperException e)
			{
			}
		}
		return answerCombosIds;
	}
	
	/**
	 * Searches the tree for radiobox components used as answer components.<br/>
	 * Returns an array of the radiobox components used as answer components, in document order.<br/>
	 * @return Array of radiobox components used as answer components
	 */
	public List<RadioBoxComponent> getAnswerRadioBoxes()
	{
		List<RadioBoxComponent> answerRadioBoxes=new ArrayList<RadioBoxComponent>();
		for (RadioBoxComponent radiobox:getRadioBoxes())
		{
			if (radiobox.isAnswerEnabled())
			{
				answerRadioBoxes.add(radiobox);
			}
		}
		return answerRadioBoxes;
	}
	
	/**
	 * Searches the tree for checkbox components used as answer components.<br/>
	 * Returns an array of the checkbox components used as answer components, in document order.<br/>
	 * @return Array of checkbox components used as answer components
	 */
	public List<CheckboxComponent> getAnswerCheckboxes()
	{
		List<CheckboxComponent> answerCheckboxes=new ArrayList<CheckboxComponent>();
		for (CheckboxComponent checkbox:getCheckboxes())
		{
			if (checkbox.isAnswerEnabled())
			{
				answerCheckboxes.add(checkbox);
			}
		}
		return answerCheckboxes;
	}
	
	/**
	 * Searches the tree for editfield components used as answer components.<br/>
	 * Returns an array of the editfield components used as answer components, in document order.<br/>
	 * @return Array of editfield components used as answer components
	 */
	public List<EditFieldComponent> getAnswerEditFields()
	{
		List<EditFieldComponent> answerEditFields=new ArrayList<EditFieldComponent>();
		for (EditFieldComponent editfield:getEditFields())
		{
			if (editfield.isAnswerEnabled())
			{
				answerEditFields.add(editfield);
			}
		}
		return answerEditFields;
	}
	
	/**
	 * Searches the tree for advancedfield components used as answer components.<br/>
	 * Returns an array of the advancedfield components used as answer components, 
	 * in document order.<br/>
	 * @return Array of advancedfield components used as answer components
	 */
	public List<AdvancedFieldComponent> getAnswerAdvancedFields()
	{
		List<AdvancedFieldComponent> answerAdvancedFields=new ArrayList<AdvancedFieldComponent>();
		for (AdvancedFieldComponent advancedfield:getAdvancedFields())
		{
			if (advancedfield.isAnswerEnabled())
			{
				answerAdvancedFields.add(advancedfield);
			}
		}
		return answerAdvancedFields;
	}
	
	/**
	 * Searches the tree for dropdown components used as answer components.<br/>
	 * Returns an array of the dropdown components used as answer components, in document order.<br/>
	 * @return Array of dropdown components used as answer components
	 */
	public List<DropdownComponent> getAnswerDropdowns()
	{
		List<DropdownComponent> answerDropdowns=new ArrayList<DropdownComponent>();
		for (DropdownComponent dropdown:getDropdowns())
		{
			if (dropdown.isAnswerEnabled())
			{
				answerDropdowns.add(dropdown);
			}
		}
		return answerDropdowns;
	}
	
	/**
	 * Searches the tree for dropbox components used as answer components.<br/>
	 * Returns an array of the dropbox components used as answer components, in document order.<br/>
	 * @return Array of dropbox components used as answer components
	 */
	public List<DropBoxComponent> getAnswerDropBoxes()
	{
		List<DropBoxComponent> answerDropBoxes=new ArrayList<DropBoxComponent>();
		for (DropBoxComponent dropbox:getDropBoxes())
		{
			if (dropbox.isAnswerEnabled())
			{
				answerDropBoxes.add(dropbox);
			}
		}
		return answerDropBoxes;
	}
	
	/**
	 * Searches the tree for wordselect components used as answer components.<br/>
	 * Returns an array of the wordselect components used as answer components, in document order.<br/>
	 * @return Array of wordselect components used as answer components
	 */
	public List<WordSelectComponent> getAnswerWordSelects()
	{
		List<WordSelectComponent> answerWordSelects=new ArrayList<WordSelectComponent>();
		for (WordSelectComponent wordselect:getWordSelects())
		{
			if (wordselect.isAnswerEnabled())
			{
				answerWordSelects.add(wordselect);
			}
		}
		return answerWordSelects;
	}
	
	/**
	 * Searches the tree for jme components used as answer components.<br/>
	 * Returns an array of the jme components used as answer components, in document order.<br/>
	 * @return Array of jme components used as answer components
	 */
	public List<JMEComponent> getAnswerJMEs()
	{
		List<JMEComponent> answerJMEs=new ArrayList<JMEComponent>();
		for (JMEComponent jme:getJMEs())
		{
			if (jme.isAnswerEnabled())
			{
				answerJMEs.add(jme);
			}
		}
		return answerJMEs;
	}
	
	/**
	 * Searches the tree for variable components used as answer components.<br/>
	 * Returns an array of the variable components used as answer components, in document order.<br/>
	 * @return Array of variable components used as answer components
	 */
	public List<VariableComponent> getAnswerVariables()
	{
		List<VariableComponent> variables=new ArrayList<VariableComponent>();
		for (VariableComponent variable:getVariables())
		{
			if (variable.isAnswerEnabled())
			{
				variables.add(variable);
			}
		}
		return variables;
	}
	
	/**
	 * Returns an array of the radio groups used as answer components, in document order.<br/>
	 * @return Array of radio groups used as answer components
	 */
	protected List<String> getAnswerRadioBoxesGroups()
	{
		List<String> answerRadioBoxesGroups = new ArrayList<String>();
		for (RadioBoxComponent answerRadioBox:getAnswerRadioBoxes())
		{
			String answerRadioBoxGroup=null;
			try
			{
				answerRadioBoxGroup=answerRadioBox.getString(RadioBoxComponent.PROPERTY_GROUP);
			}
			catch (OmDeveloperException e)
			{
				answerRadioBoxGroup=null;
			}
			if (answerRadioBoxGroup!=null)
			{
				if (!answerRadioBoxesGroups.contains(answerRadioBoxGroup))
				{
					answerRadioBoxesGroups.add(answerRadioBoxGroup);
				}
			}
		}
		return answerRadioBoxesGroups;
	}
	
	/**
	 * Tests if the string received probably contains one or more placeholders.<br/><br/>
	 * Although applyPlaceholders method can be used safely in a string without placeholders it is faster
	 * trying to avoid applying it when possible and this method will help to achieve it.
	 * @param s String
	 * @return true if the string received probably contains one or more placeholders, false otherwise
	 */
	public static boolean containsPlaceholder(String s)
	{
		boolean found=false;
		int iFirst=s.indexOf("__");
		if (iFirst!=-1 && iFirst+2<s.length())
		{
			int iSecond=s.indexOf("__",iFirst+2);
			found=iSecond!=-1;
		}
		return found;
	}
	
	/**
	 * Gets the string which represents the text of a component.<br/>
	 * This method look in further components inside this component for text.<br/>
	 * Only strings belonging to text components are considered text and added to the final string.
	 * @param qc Component
	 * @return String which represents the text of a component
	 */
	private String getAllText(QComponent qc)
	{
		StringBuffer text=new StringBuffer();
		for (Object o:qc.getChildren())
		{
			if (o instanceof QComponent)
			{
				text.append(getAllText((QComponent)o));
			}
			else if (qc instanceof TextComponent && o instanceof String)
			{
				text.append((String)o);
			}
		}
		return text.toString();
	}
	
	/**
	 * Get first text line from the first text component found inside received component, trimming
	 * starting and ending whitespaces and replacing each occurrence of together whitespaces by a single
	 * one.
	 * @param qc Component
	 * @return First text line from the first text component found inside received component
	 */
	private String getFirstLine(QComponent qc)
	{
		String line=getAllText(qc);
		if (isPlaceholdersInitialized() && containsPlaceholder(line))
		{
			line=applyPlaceholders(line);
		}
		boolean searchStart=true;
		int iStart=0;
		while (searchStart && iStart<line.length())
		{
			char c=line.charAt(iStart);
			if (Character.isWhitespace(c) || c=='\n' || c=='\r')
			{
				iStart++;
			}
			else
			{
				searchStart=false;
			}
		}
		if (searchStart)
		{
			line="";
		}
		else
		{
			boolean searchEnd=true;
			int iEnd=iStart+1;
			while (searchEnd && iEnd<line.length())
			{
				char c=line.charAt(iEnd);
				if (c=='\n' || c=='\r')
				{
					searchEnd=false;
				}
				else
				{
					iEnd++;
				}
			}
			line=AnswerChecking.singledWhitespace(line.substring(iStart,iEnd).trim(),false);
		}
		return line;
	}
	
	/**
	 * Get tag name from received component
	 * @param qc Component
	 * @return Tag name from received component
	 * @throws OmDeveloperException
	 */
	public String getComponentTagName(QComponent qc) throws OmDeveloperException
	{
		String sTagName=null;
		Class<?> cComponent=qc.getClass();
		try
		{
			Method m=cComponent.getMethod("getTagName",new Class[]{});
			sTagName=(String)m.invoke(null);
		}
		catch(ClassCastException cce)
		{
			StringBuffer error=new StringBuffer(cComponent.getName());
			error.append(".getTagName() did not return a String");
			throw new OmDeveloperException(error.toString());
		}
		catch(NoSuchMethodException e)
		{
			StringBuffer error=new StringBuffer(cComponent.getName());
			error.append(" does not contain public String getTagName() method");
			throw new OmDeveloperException(error.toString());
		}
		catch(IllegalAccessException e)
		{
			StringBuffer error=new StringBuffer(cComponent.getName());
			error.append(".getTagName() method could not be accessed");
			throw new OmDeveloperException(error.toString());
		}
		catch(InvocationTargetException e)
		{
			StringBuffer error=new StringBuffer(cComponent.getName());
			error.append(".getTagName() method threw an exception");
			throw new OmDeveloperException(error.toString());
		}
		return sTagName;
	}
	
	/**
	 * Get a default question line with the first text line of the first text component inside
	 * inputbox component.
	 * @return Default question line
	 */
	public String getDefaultQuestionLine()
	{
		String questionline="";
		try
		{
			questionline=getFirstLine(getComponent("inputbox"));
		}
		catch (OmDeveloperException e)
		{
			questionline="";
		}
		return questionline;
	}
	
	/**
	 * Get question line from questionLine property of 'inputbox' box or get default question line
	 * if that is not defined.
	 * @return Question line
	 */
	public String getQuestionLine()
	{
		String questionLine=null;
		List<SummaryLineComponent> questionLines=new ArrayList<SummaryLineComponent>();
		for (SummaryLineComponent summaryLineComponent:getComponents(SummaryLineComponent.class))
		{
			if (summaryLineComponent.getType().equals(SummaryLineComponent.TYPE_QUESTIONLINE))
			{
				questionLines.add(summaryLineComponent);
			}
		}
		for (SummaryLineComponent questionLineC:questionLines)
		{
			String questionLineSC=questionLineC.getSummaryLine();
			if (questionLineSC!=null)
			{
				questionLine=questionLineSC;
				break;
			}
		}
		if (questionLine==null)
		{
			QComponent inputbox=null;
			try
			{
				inputbox=getComponent("inputbox");
			}
			catch (OmDeveloperException e)
			{
				inputbox=null;
			}
			if (inputbox!=null && inputbox instanceof BoxComponent)
			{
				questionLine=((BoxComponent)inputbox).getQuestionLine();
			}
			if (questionLine==null)
			{
				questionLine=getDefaultQuestionLine();
			}
		}
		return questionLine;
	}
	
	/**
	 * Set question line for summary.
	 * @param questionLine Question line
	 * @throws OmDeveloperException
	 */
	protected void setQuestionLine(String questionLine) throws OmDeveloperException
	{
		if (questionLine!=null && getParameters().isQuestionLine())
		{
			if (questionLine.length()<=QUESTION_LINE_MAX_CHARS)
			{
				getResults().setQuestionLine(questionLine);
			}
			else
			{
				boolean actionOk=false;
				String longQuestionLine=getParameters().getLongQuestionLine();
				if (longQuestionLine!=null)
				{
					// "void", "voidtosummary"
					if (longQuestionLine.equals(ParametersComponent.LONGLINE_VOID) || 
							longQuestionLine.equals(ParametersComponent.LONGLINE_VOIDTOSUMMARY))
					{
						actionOk=true;
					}
					
					// "clip", "cliptosummary"
					if (longQuestionLine.equals(ParametersComponent.LONGLINE_CLIP) ||
							longQuestionLine.equals(ParametersComponent.LONGLINE_CLIPTOSUMMARY))
					{
						String questionLineClipped=questionLine.substring(0,QUESTION_LINE_MAX_CHARS);
						getResults().setQuestionLine(questionLineClipped);
						actionOk=true;
					}
					
					// "clipword", "clipwordtosummary"
					if (longQuestionLine.equals(ParametersComponent.LONGLINE_CLIPWORD) ||
							longQuestionLine.equals(ParametersComponent.LONGLINE_CLIPWORDTOSUMMARY))
					{
						String questionLineClipped=
								AnswerChecking.clipWord(questionLine," ...",QUESTION_LINE_MAX_CHARS);
						getResults().setQuestionLine(questionLineClipped);
						actionOk=true;
					}
					
					// "voidtosummary", "cliptosummary", "clipwordtosummary"
					if (longQuestionLine.equals(ParametersComponent.LONGLINE_VOIDTOSUMMARY) ||
							longQuestionLine.equals(ParametersComponent.LONGLINE_CLIPTOSUMMARY) ||
							longQuestionLine.equals(ParametersComponent.LONGLINE_CLIPWORDTOSUMMARY))
					{
						StringBuffer questionSummaryLine=new StringBuffer("Question: ");
						questionSummaryLine.append(questionLine);
						appendSummaryLine(questionSummaryLine.toString());
						actionOk=true;
					}
				}
				if (!actionOk)
				{
					StringBuffer error=new StringBuffer();
					error.append("Question line must be <= ");
					error.append(QUESTION_LINE_MAX_CHARS);
					error.append(" characters long");
					throw new OmDeveloperException(error.toString());					
				}
			}
		}
	}
	
	/**
	 * Get default answer line to use when user passes the question.<br/><br/>
	 * By default it returns "Passed".<br/><br/>
	 * Override it in subclasses if you want to change that message.
	 * @return Default answer line to use when user passes the question
	 */
	protected String getDefaultPassedAnswerLine()
	{
		return "Passed";
	}
	
	/**
	 * Get default answer line to use when user response has no answers selected.<br><br/>
	 * By default it returns "No answers selected."<br/><br/>
	 * Override it in subclasses if you want to change that message.
	 * @return Default answer line to use when user response has no answers selected
	 */
	protected String getDefaultNoAnswersSelectedAnswerLine()
	{
		return "No answers selected";
	}
	
	/**
	 * Get a default answer line with the first text line of the first text component inside
	 * component with received id
	 * @param idAnswer Identifier of the answer's component selected by user
	 * @return Default answer line
	 */
	public String getDefaultAnswerLine(String idAnswer)
	{
		String answerLine=null;
		if (idAnswer==null)
		{
			if (idAnswers==null)
			{
				answerLine=getDefaultPassedAnswerLine();
			}
			else
			{
				answerLine=getDefaultNoAnswersSelectedAnswerLine();
			}
		}
		else
		{
			try
			{
				answerLine=getFirstLine(getComponent(idAnswer));
			}
			catch (OmDeveloperException e)
			{
				answerLine=null;
			}
		}
		return answerLine;
	}
	
	/**
	 * Get a default answer line constructed with the information from answer components used 
	 * in this user's attempt
	 * @return Default answer line
	 */
	public String getDefaultAnswerLine()
	{
		StringBuffer answerLine=null;
		if (idAnswers==null)
		{
			answerLine=new StringBuffer(getDefaultPassedAnswerLine());
		}
		else if (idAnswers.isEmpty())
		{
			answerLine=new StringBuffer(getDefaultNoAnswersSelectedAnswerLine());
		}
		else if (idAnswers.size()==1)
		{
			String answerLine1=getDefaultAnswerLine(idAnswers.get(0));
			if (answerLine1!=null)
			{
				answerLine=new StringBuffer(answerLine1);
			}
		}
		else
		{
			boolean insertComma=false;
			answerLine=new StringBuffer();
			for (String idAnswer:idAnswers)
			{
				String firstTextLine=null;
				try
				{
					firstTextLine=getFirstLine(getComponent(idAnswer));
				}
				catch (OmDeveloperException e)
				{
					firstTextLine=null;
				}
				if (firstTextLine!=null)
				{
					if (insertComma)
					{
						answerLine.append(", ");
					}
					else
					{
						insertComma=true;
					}
					answerLine.append(firstTextLine);
				}
			}
		}
		return answerLine==null?null:answerLine.toString();
	}
	
	/**
	 * Get answer line from answerLine property of component with received id or get default answer
	 * line for that component
	 * @param idAnswer idAnswer Identifier of component selected for answer
	 * @return Answer line
	 * @throws OmDeveloperException
	 */
	public String getAnswerLine(String idAnswer) throws OmDeveloperException
	{
		String answerLine=null;
		QComponent qc=null;
		if (idAnswer==null && idAnswers==null)
		{
			try
			{
				qc=getComponent("answerbox");
			}
			catch (OmDeveloperException e)
			{
				qc=null;
			}
		}
		else if (idAnswer!=null)
		{
			try
			{
				qc=getComponent(idAnswer);
			}
			catch (OmDeveloperException e)
			{
				qc=null;
			}
		}
		if (qc!=null)
		{
			if (qc instanceof Answerable)
			{
				answerLine=((Answerable)qc).getAnswerLine();
			}
			else if (qc instanceof BoxComponent)
			{
				answerLine=((BoxComponent)qc).getAnswerLine();
			}
		}
		if (answerLine==null && getParameters().isDefaultAnswerLine())
		{
			answerLine=getDefaultAnswerLine(idAnswer);
		}
		return answerLine;
	}
	
	/**
	 * Set answer line for summary
	 * @param answerLine Answer line
	 * @throws OmDeveloperException
	 */
	protected void setAnswerLine(String answerLine) throws OmDeveloperException
	{
		if (answerLine!=null && getParameters().isAnswerLine())
		{
			if (answerLine.length()<=ANSWER_LINE_MAX_CHARS)
			{
				getResults().setAnswerLine(answerLine);
			}
			else
			{
				boolean actionOk=false;
				String longAnswerLine=getParameters().getLongAnswerLine();
				if (longAnswerLine!=null)
				{
					// "void", "voidtosummary"
					if (longAnswerLine.equals(ParametersComponent.LONGLINE_VOID) || 
							longAnswerLine.equals(ParametersComponent.LONGLINE_VOIDTOSUMMARY))
					{
						actionOk=true;
					}
					
					// "clip", "cliptosummary"
					if (longAnswerLine.equals(ParametersComponent.LONGLINE_CLIP) ||
							longAnswerLine.equals(ParametersComponent.LONGLINE_CLIPTOSUMMARY))
					{
						String answerLineClipped=answerLine.substring(0,ANSWER_LINE_MAX_CHARS);
						getResults().setAnswerLine(answerLineClipped);
						actionOk=true;
					}
					
					// "clipword", "clipwordtosummary"
					if (longAnswerLine.equals(ParametersComponent.LONGLINE_CLIPWORD) ||
							longAnswerLine.equals(ParametersComponent.LONGLINE_CLIPWORDTOSUMMARY))
					{
						String answerLineClipped=
								AnswerChecking.clipWord(answerLine," ...",ANSWER_LINE_MAX_CHARS);
						getResults().setAnswerLine(answerLineClipped);
						actionOk=true;
					}
					
					// "voidtosummary", "cliptosummary", "clipwordtosummary"
					if (longAnswerLine.equals(ParametersComponent.LONGLINE_VOIDTOSUMMARY) ||
							longAnswerLine.equals(ParametersComponent.LONGLINE_CLIPTOSUMMARY) ||
							longAnswerLine.equals(ParametersComponent.LONGLINE_CLIPWORDTOSUMMARY))
					{
						StringBuffer answerSummaryLine=new StringBuffer("Answer: ");
						answerSummaryLine.append(answerLine);
						appendSummaryLine(answerSummaryLine.toString());
						actionOk=true;
					}
				}
				if (!actionOk)
				{
					StringBuffer error=new StringBuffer();
					error.append("Answer line must be <= ");
					error.append(ANSWER_LINE_MAX_CHARS);
					error.append(" characters long");
					throw new OmDeveloperException(error.toString());					
				}
			}
		}
	}
	
	/**
	 * Get answer line from answerLine property of component with received id or get default answer
	 * line for that component
	 * @return Answer line
	 * @throws OmDeveloperException
	 */
	public String getAnswerLine() throws OmDeveloperException
	{
		StringBuffer answerLine=null;
		List<SummaryLineComponent> answerLines=new ArrayList<SummaryLineComponent>();
		for (SummaryLineComponent summaryLineComponent:getComponents(SummaryLineComponent.class))
		{
			if (summaryLineComponent.getType().equals(SummaryLineComponent.TYPE_ANSWERLINE))
			{
				answerLines.add(summaryLineComponent);
			}
		}
		for (SummaryLineComponent answerLineC:answerLines)
		{
			String answerLineSC=answerLineC.getSummaryLine();
			if (answerLineSC!=null)
			{
				answerLine=new StringBuffer(answerLineSC);
				break;
			}
		}
		if (answerLine==null)
		{
			if (idAnswers==null || idAnswers.isEmpty())
			{
				String answerLine0=getAnswerLine((String)null);
				if (answerLine0!=null)
				{
					answerLine=new StringBuffer(answerLine0);
				}
			}
			else if (idAnswers.size()==1)
			{
				String answerLine1=getAnswerLine(idAnswers.get(0));
				if (answerLine1!=null)
				{
					answerLine=new StringBuffer(answerLine1);
				}
			}
			else
			{
				boolean insertComma=false;
				QComponent qc=null;
				answerLine=new StringBuffer();
				for (String idAnswer:idAnswers)
				{
					String answerLineC=null;
					try
					{
						qc=getComponent(idAnswer);
					}
					catch (OmDeveloperException e)
					{
						qc=null;
					}
					if (qc!=null && qc instanceof Answerable)
					{
						answerLineC=((Answerable)qc).getAnswerLine();
					}
					if (answerLineC==null && getParameters().isDefaultAnswerLine())
					{
						answerLineC=getDefaultAnswerLine(idAnswer);
					}
					if (answerLineC!=null)
					{
						if (insertComma)
						{
							answerLine.append(", ");
						}
						else
						{
							insertComma=true;
						}
						answerLine.append(answerLineC);
					}
				}
			}
		}
		return answerLine==null?null:answerLine.toString();
	}
	
	/**
	 * Get default action summary line to use when user passes the question.<br/><br/>
	 * By default it returns default answer line to use when user passes the question.<br/><br/>
	 * Override it in subclasses if you want to change that message.
	 * @return Default action summary line to use when user passes the question
	 */
	protected String getDefaultPassedActionSummaryLine()
	{
		return getDefaultPassedAnswerLine();
	}
	
	/**
	 * Get a default action summary line showing attempt, rightness or not and the component
	 * selected
	 * @param idAnswer Identifier of component selected for answer
	 * @return Default action summary line
	 */
	public String getDefaultActionSummaryLine(String idAnswer)
	{
		StringBuffer actionSummaryLine=new StringBuffer();
		if (idAnswer==null)
		{
			actionSummaryLine.append(getDefaultPassedActionSummaryLine());
		}
		else
		{
			actionSummaryLine.append("Attempt ");
			actionSummaryLine.append(getAttempt());
			actionSummaryLine.append(". ");
			actionSummaryLine.append(isRight()?"Right":"Wrong");
			actionSummaryLine.append(" answer.");
			try
			{
				QComponent qcAnswer=getComponent(idAnswer);
				String tagName=getComponentTagName(getComponent(idAnswer));
				if (qcAnswer instanceof RadioBoxComponent || qcAnswer instanceof CheckboxComponent)
				{
					actionSummaryLine.append(" Selected ");
					actionSummaryLine.append(tagName);
					actionSummaryLine.append(": ");
					actionSummaryLine.append(idAnswer);
				}
				else if (qcAnswer instanceof Answerable)
				{
					actionSummaryLine.append(' ');
					actionSummaryLine.append(tagName);
					actionSummaryLine.append(' ');
					actionSummaryLine.append(idAnswer);
					actionSummaryLine.append(": ");
					actionSummaryLine.append(((Answerable)qcAnswer).getAnswerLine());
				}
			}
			catch (OmDeveloperException e)
			{
				if (idAnswer!=null)
				{
					actionSummaryLine.append(" Selected answer: ");
					actionSummaryLine.append(idAnswer);
				}
			}
		}
		return actionSummaryLine.toString();
	}
	
	/**
	 * Get a default action summary line showing attempt, rightness or not and the component
	 * selected
	 * @return Default action summary line
	 */
	public String getDefaultActionSummaryLine()
	{
		StringBuffer actionSummaryLine=new StringBuffer();
		if (idAnswers==null)
		{
			actionSummaryLine.append(getDefaultPassedActionSummaryLine());
		}
		else if (idAnswers.isEmpty())
		{
			actionSummaryLine.append("Attempt ");
			actionSummaryLine.append(getAttempt());
			actionSummaryLine.append(". ");
			actionSummaryLine.append(isRight()?"Right":"Wrong");
			actionSummaryLine.append(" answer. ");
			actionSummaryLine.append(getDefaultNoAnswersSelectedAnswerLine());
			actionSummaryLine.append('.');
		}
		else if (idAnswers.size()==1)
		{
			actionSummaryLine.append(getDefaultActionSummaryLine(idAnswers.get(0)));
		}
		else
		{
			actionSummaryLine.append("Attempt ");
			actionSummaryLine.append(getAttempt());
			actionSummaryLine.append(". ");
			actionSummaryLine.append(isRight()?"Right":"Wrong");
			actionSummaryLine.append(" answer. Selected answers: ");
			boolean insertComma=false;
			for (String idAnswer:idAnswers)
			{
				if (insertComma)
				{
					actionSummaryLine.append(", ");
				}
				else
				{
					insertComma=true;
				}
				try
				{
					QComponent qcAnswer=getComponent(idAnswer);
					if (qcAnswer instanceof RadioBoxComponent || qcAnswer instanceof CheckboxComponent)
					{
						actionSummaryLine.append(idAnswer);
					}
					else if (qcAnswer instanceof Answerable)
					{
						actionSummaryLine.append(idAnswer);
						actionSummaryLine.append(": ");
						actionSummaryLine.append(((Answerable)qcAnswer).getAnswerLine());
					}
				}
				catch (OmDeveloperException e)
				{
					actionSummaryLine.append(idAnswer);
				}
			}
		}
		return actionSummaryLine.toString();
	}
	
	/**
	 * Get all action summary lines to add in this attempt.<br/>
	 * If question.xml has not &lt;summaryline&gt; components gets default action summary line.
	 * @return All action summary lines to add in this attempt
	 * @throws OmDeveloperException
	 */
	public List<String> getSummaryLines() throws OmDeveloperException
	{
		List<String> summaryLines=new ArrayList<String>();
		List<SummaryLineComponent> summaryLineComponents=new ArrayList<SummaryLineComponent>();
		for (SummaryLineComponent summaryLineComponent:getComponents(SummaryLineComponent.class))
		{
			if (summaryLineComponent.getType().equals(SummaryLineComponent.TYPE_SUMMARYLINE))
			{
				summaryLineComponents.add(summaryLineComponent);
			}
		}
		if (summaryLineComponents.isEmpty() && getParameters().isDefaultSummaryLine())
		{
			summaryLines.add(getDefaultActionSummaryLine());
		}
		else
		{
			for (SummaryLineComponent summaryLineComponent:summaryLineComponents)
			{
				String summaryLine=summaryLineComponent.getSummaryLine();
				if (summaryLine!=null)
				{
					summaryLines.add(summaryLine);
				}
			}
		}
		return summaryLines;
	}
	
	/**
	 * Appends a new line to the summary
	 * @param summaryLine Summary line to append
	 * @throws OmDeveloperException
	 */
	protected void appendSummaryLine(String summaryLine) throws OmDeveloperException
	{
		if (summaryLine!=null && getParameters().isSummaryLine())
		{
			getResults().appendActionSummary(summaryLine);
		}
	}
	
	/**
	 * Get all tasks to be executed.
	 * @param when Name that indicates if we want this task to be executed at this moment
	 * @return All tasks to be executed
	 */
	public List<Task> getTasks(String when)
	{
		List<Task> tasks=new ArrayList<Task>();
		List<TaskComponent> taskComponents=getComponents(TaskComponent.class);
		for (TaskComponent taskComponent:taskComponents)
		{
			if (taskComponent.test(when))
			{
				tasks.add(new Task(taskComponent));
			}
		}
		return tasks;
	}
	
	/**
	 * Initialize random components.
	 * @param randoms List of random components
	 */
	protected void initializeRandoms(List<RandomComponent> randoms)
	{
		RandomComponent.resetRandoms();
		for (RandomComponent random:randoms)
		{
			random.generate();
		}
	}
	
	/**
	 * Reset placeholders from a list of replaceholders components
	 * @param replaceholders List of replaceholder components
	 * @throws OmException
	 */
	protected void resetPlaceholders(List<ReplaceholderComponent> replaceholders)
			throws OmException
	{
		for (ReplaceholderComponent replaceholder:replaceholders)
		{
			setPlaceholder(replaceholder.getPlaceholder(),replaceholder.getReplacementValue());
		}
	}
	
	/**
	 * Initialize placeholders from a list of replaceholders components
	 * @param replaceholders List of replaceholder components
	 * @throws OmException
	 */
	protected void initializePlaceholders(List<ReplaceholderComponent> replaceholders)
			throws OmException
	{
		resetPlaceholders(replaceholders);
		setPlaceholdersInitialized(true);
	}
	
	/**
	 * Initialize counters.
	 */
	protected void initializeCounters()
	{
		int selectableAnswers=getAnswerRadioBoxesGroups().size();
		int selectableRightAnswers=selectableAnswers;
		int selectableWrongAnswers=selectableAnswers;
		List<CheckboxComponent> checkboxes=getAnswerCheckboxes();
		selectableAnswers+=checkboxes.size();
		for (CheckboxComponent checkbox:checkboxes)
		{
			if (checkbox.isRight())
			{
				selectableRightAnswers++;
			}
			else
			{
				selectableWrongAnswers++;
			}
		}
		List<EditFieldComponent> editfields=getAnswerEditFields();
		selectableAnswers+=editfields.size();
		selectableRightAnswers+=editfields.size();
		selectableWrongAnswers+=editfields.size();
		List<AdvancedFieldComponent> advancedfields=getAnswerAdvancedFields();
		selectableAnswers+=advancedfields.size();
		selectableRightAnswers+=advancedfields.size();
		selectableWrongAnswers+=advancedfields.size();
		List<DropdownComponent> dropdowns=getAnswerDropdowns();
		selectableAnswers+=dropdowns.size();
		selectableRightAnswers+=dropdowns.size();
		selectableWrongAnswers+=dropdowns.size();
		List<DropBoxComponent> dropboxes=getAnswerDropBoxes();
		selectableAnswers+=dropboxes.size();
		selectableRightAnswers+=dropboxes.size();
		selectableWrongAnswers+=dropboxes.size();
		List<WordSelectComponent> wordselects=getAnswerWordSelects();
		for (WordSelectComponent wordselect:wordselects)
		{
			int words=wordselect.getWordCount();
			int rightWords=wordselect.getTotalSWWords();
			selectableAnswers+=words;
			selectableRightAnswers+=rightWords;
			selectableWrongAnswers+=words-rightWords;
		}
		List<JMEComponent> jmes=getAnswerJMEs();
		selectableAnswers+=jmes.size();
		selectableRightAnswers+=jmes.size();
		selectableWrongAnswers+=jmes.size();
		List<VariableComponent> variables=getAnswerVariables();
		selectableAnswers+=variables.size();
		selectableRightAnswers+=variables.size();
		selectableWrongAnswers+=variables.size();
		setCounters(
				new QuestionCounters(selectableAnswers,selectableRightAnswers,selectableWrongAnswers));
	}
	
	/**
	 * Process default answer constructing list of selected answers (identifiers) and returns true 
	 * if the default answer is right or false if it is wrong.<br/><br/> 
	 * Also counts hits and failures if indicated and counters have been initialized.
	 * @param useCounters Whether to use or not counters
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	protected boolean processDefaultAnswer(boolean useCounters,int attempt) throws OmDeveloperException
	{
  		boolean right=true;
  		boolean checkedRadiogroup=false;
  		String radiogroup=null;
		resetIdAnswers();
		if (useCounters && getCounters()!=null)
		{
			getCounters().reset();
		}
  		for (Answerable qc:getAnswerComponents())
  		{
  			if (qc instanceof RadioBoxComponent)
  			{
  				RadioBoxComponent radiobox=(RadioBoxComponent)qc;
  				String radioboxGroup=null;
  				try
  				{
  					radioboxGroup=radiobox.getString(RadioBoxComponent.PROPERTY_GROUP);
  				}
  				catch (OmDeveloperException e)
  				{
  					radioboxGroup=null;
  				}
  				if (radiogroup==null)
  				{
  					radiogroup=radioboxGroup;
  					checkedRadiogroup=false;
  				}
  				else if (!radiogroup.equals(radioboxGroup))
  				{
  					right=right && checkedRadiogroup;
  					radiogroup=radioboxGroup;
  					checkedRadiogroup=false;
  				}
  				if (!checkedRadiogroup && radiobox.isChecked())
  				{
  					addIdAnswer(radiobox.getID());
  					right=right && radiobox.isRight();
  					if (useCounters && getCounters()!=null)
  					{
  						getCounters().selectAnswer(radiobox.isRight());
  					}
  					checkedRadiogroup=true;
  				}
  			}
  			else
  			{
  				if (radiogroup!=null)
  				{
  					right=right && checkedRadiogroup;
  					radiogroup=null;
  				}
  				if (qc instanceof CheckboxComponent)
  				{
  					CheckboxComponent checkbox=(CheckboxComponent)qc;
  					if (checkbox.isChecked())
  					{
  						addIdAnswer(checkbox.getID());
  						right=right && checkbox.isRight();
  						if (useCounters && getCounters()!=null)
  						{
  							getCounters().selectAnswer(checkbox.isRight());
  						}
  					}
  					else
  					{
  						right=right && !checkbox.isRight();
  					}
  				}
  				else if (qc instanceof WordSelectComponent)
  				{
  					WordSelectComponent wordselect=(WordSelectComponent)qc;
  		  			int wordsSelected=wordselect.getTotalWordsSelected();
  		  			int rightWordsSelected=wordselect.getTotalSWWordsSelected();
  		  			addIdAnswer(wordselect.getID());
  		  			right=right && wordselect.isRight();
  		  			if (useCounters && getCounters()!=null)
  		  			{
  	  		  			getCounters().selectAnswers(rightWordsSelected,true);
  	  		  			getCounters().selectAnswers(wordsSelected-rightWordsSelected,false);
  		  			}
  				}
  				else
  				{
  					addIdAnswer(((QComponent)qc).getID());
  					right=right && qc.isRight();
  					if (useCounters && getCounters()!=null)
  					{
  						getCounters().selectAnswer(qc.isRight());
  					}
  				}
  			}
  		}
  		if (radiogroup!=null)
  		{
			right=right && checkedRadiogroup;
  		}
  		return right;
	}
	
	/**
	 * This is a simple right/wrong flag default implementation.
	 * @return True if the user is right; false if they are wrong.
	 * @throws OmDeveloperException
	 */
	protected boolean isRightDefault() throws OmDeveloperException
	{
		boolean notAnswercombosRight=true;
		String defaultAnswer=getParameters().getDefaultAnswer();
		if (defaultAnswer.equals(ParametersComponent.DEFAULT_ANSWER_RIGHT) && isMatchDefault())
		{
			return true;
		}
		for (AnswerComboComponent answercombo:getAnswerCombos())
		{
			if (answercombo.isRight())
			{
				if (answercombo.test())
				{
					return true;
				}
				else
				{
					notAnswercombosRight=false;
				}
			}
		}
		if (notAnswercombosRight && defaultAnswer.equals(ParametersComponent.DEFAULT_ANSWER_DEFAULT))
		{
			return isMatchDefault();
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Reset variable components
	 * @param variables List of variable components
	 */
	protected void resetVars(List<VariableComponent> variables)
	{
		for (VariableComponent variable:variables)
		{
			variable.resetValue();
		}
	}
}
