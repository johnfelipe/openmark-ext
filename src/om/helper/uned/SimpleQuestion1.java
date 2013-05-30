package om.helper.uned;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.question.InitParams;
import om.question.Rendering;
import om.question.Results;
import om.stdcomponent.BoxComponent;
import om.stdcomponent.uned.AnswerComboComponent;
import om.stdcomponent.uned.SummaryLineComponent;
import om.stdcomponent.uned.TaskComponent;
import om.stdcomponent.uned.Testable;
import om.stdquestion.QComponent;
import om.stdquestion.uned.StandardQuestion;
import util.xml.XML;

// UNED: 21-06-2011 - dballestin
/**
 * This version of SimpleQuestion1 class extends from om.stdquestion.uned.StandardQuestion 
 * and so uses om.stdquestion.uned.QComponentManager as the component manager.<br/><br/>
 * It tries to emulate functionality of om.helper.SimpleQuestion1 class but with some differences
 * to work well with the new properties of overridden components.<br/><br/>
 * Be careful that this class is not able to resolve by itself if the answer is right nor
 * to generate correct feedback, scores or summaries so if you derive directly from this class 
 * you will have to program those functionalities yourself.<br/><br/>
 * If you want to use a generic class with all that functionalities implemented extend from
 * GenericQuestion class from this package instead.<br/><br/>
 * Base class for simple questions that have an input box and an answer box
 * (simultaneously visible) and match certain other 'standard' requirements
 * in their XML.
 * <p>
 * Specifically, the XML must contain the following component IDs:
 * <ul>
 * <li>inputbox - box that contains controls into which user enters their
 *   answers</li>
 * <li>answerbox - box that contains information about the answer, e.g. was
 *   it right or not</li>
 * <li>wrong - component within answerbox, shown only if answer is wrong</li>
 * <li>still - component within answerbox, shown only if answer is wrong and
 *   it's not the first time</li>
 * <li>right - component within answerbox, shown only if answer is right</li>
 * <li>pass - component within answerbox, shown only if user passed</li>
 * <li>feedback - component within answerbox, shown only if getFeedbackID()
 *   returns something other than null.</li>
 * </ul>
 * There should be examples matching this pattern in the documentation.
 */
public abstract class SimpleQuestion1 extends StandardQuestion
{
	/** If true, question ends when you click OK */
	private boolean bEndNext=false;
	
	/** ID used for feedback */
	private String sFeedbackID;
	
	/** Max marks, or 0 if no scoring */
	private int iMaxMarks=0;
	
	/** List of components excluded from testing for displaying */
	private final static String[] TEST_EXCEPTIONS={"answer","wrong","still","right","pass"};
	
	/** List of components excluded from testing for displaying including subcomponents */
	private final static String[] TEST_EXCEPTIONS_NESTED={"feedback"};
	
	@Override
	public Rendering init(Document d,InitParams ip) throws OmException
	{
		Rendering r=super.init(d,ip);
		
		if (getMaxAttempts()==1)
		{
			r.setProgressInfo("You have only one attempt.");
		}
		else
		{
			r.setProgressInfo("You have "+getMaxAttempts()+" attempts.");
		}
		
		// Initialize iMaxMarks
		try
		{
			Element eScoring=XML.getChild(d.getDocumentElement(),"scoring");
			iMaxMarks=Integer.parseInt(XML.getText(eScoring,"marks"));
		}
		catch(Exception e)
		{
			// Ignore, leave iMaxMarks at 0
		}
		
		// Initialize testing exceptions
		for (String exception:TEST_EXCEPTIONS)
		{
			addQuestionTimeTestingException(exception);
		}
		for (String exception:TEST_EXCEPTIONS_NESTED)
		{
			addQuestionTimeTestingNestedException(exception);
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
	 * Callback that Om calls when the user clicks the 'Submit' button to enter
	 * their answer. Calls checkAnswer for further processing.
	 * @throws OmException
	 */
	public void actionSubmit() throws OmException
	{
		checkAnswer(false);
	}
	
	/**
	 * Callback that Om calls when the user clicks the 'Give Up' button to enter
	 * their answer. Calls checkAnswer for further processing.
	 * @throws OmException
	 */
	public void actionGiveUp() throws OmException
	{
		// Set answers to null (passing)
		setIdAnswersNull();
		
		// Increase temporarily current attempt
		setAttempt(getAttempt()+1);

		// Now we get the action summary lines for passing (we need to increase temporarily current attempt)
		
		for (String summaryLine:getSummaryLines())
		{
			getResults().appendActionSummary(summaryLine);
		}
		
		// Decrease current attempt restoring last value
		setAttempt(getAttempt()-1);
		
		// Calls checkAnswer for further processing
		checkAnswer(true);
	}
	
	/**
	 * Callback that Om calls when the user clicks the 'OK' button after seeing
	 * the response to their answer. This either ends the question, or hides
	 * the answer box and re-enables the input box for another attempt.
	 * @throws OmException
	 */
	public void actionOK() throws OmException
	{
		if (bEndNext)
		{
			end();
		}
		else
		{
			// Do "startattempt" tasks
			for (Task task:getTasks(TaskComponent.WHEN_STARTATTEMPT))
			{
				doTask(task);
			}
			
			int iAttemptsLeft=(getMaxAttempts()-getAttempt()+1);
			if (iAttemptsLeft==1)
			{
				setProgressInfo("This is your last attempt.");
			}
			else
			{
				setProgressInfo("You have "+iAttemptsLeft+" attempts left.");
			}
			
			getComponent("answerbox").setDisplay(false);
			getComponent("inputbox").setBoolean(BoxComponent.PROPERTY_PLAINHIDE,false);
			getComponent("inputbox").setEnabled(true);
		}
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
	 * Handles the framework around checking the user's answer.
	 * @param bPass True if the user passed rather than submitting an answer
	 * @throws OmDeveloperException
	 */
	private void checkAnswer(boolean bPass) throws OmDeveloperException
	{
		// Do additional initializations before checking rightness
		doBeforeCheckingProcessing(bPass);
		
		// Get the three basic states
		if (!bPass)
		{
			setMatchDefault(processDefaultAnswer(getAttempt()));
			setRight(isRight(getAttempt()));
		}
		boolean bWrong=!isRight() && !bPass;
		
		// Do additional processing after checking rightness
		doAfterCheckingProcessing(isRight(),bWrong,bPass,getAttempt());
		
		// OK now show/hide the basic 'you were wrong' bit
		getComponent("wrong").setDisplay(bWrong);
		getComponent("still").setDisplay(bWrong && getAttempt()>1);
		getComponent("right").setDisplay(isRight());
		getComponent("pass").setDisplay(bPass);
		
		// Now handle feedback
		QComponent qcFeedback=getComponent("feedback");
		if(bWrong)
		{
			qcFeedback.setDisplay(sFeedbackID!=null);
			if (sFeedbackID!=null && !sFeedbackID.equals(""))
			{
				QComponent[] aqc=qcFeedback.getComponentChildren();
				for(int i=0;i<aqc.length;i++)
				{
					if(aqc[i].hasUserSetID())
					{
						aqc[i].setDisplay(aqc[i].getID().equals(sFeedbackID));
					}
				}
			}
		}
		else
		{
			qcFeedback.setDisplay(false);
		}
		
		// Should we end next time?
		bEndNext=isRight() || (getAttempt()==getMaxAttempts()) || bPass;
		
		// If so, show answer and update score
		getComponent("answer").setDisplay(bEndNext);
		if (bEndNext)
		{
			// Do "end" tasks
			for (Task task:getTasks(TaskComponent.WHEN_END))
			{
				doTask(task);
			}
			
			setScore(isRight(),bPass,getAttempt());
			sendResults();
			try
			{
				getComponent("ok").setDisplay(false);
				getComponent("next").setDisplay(true);
			}
			catch(OmDeveloperException ode)
			{
				// Ignore, just means question.xml hasn't been update
				log("Warning: question.xml should be updated to make OK and Next buttons work");
			}
		}
		else
		{
			// Do "endattempt" tasks
			for (Task task:getTasks(TaskComponent.WHEN_ENDATTEMPT))
			{
				doTask(task);
			}
		}
		
		// Give overriders a chance
		doAdditionalAnswerProcessing(isRight(),bWrong,bPass,getAttempt());
		
		// Clear progress info (looks bad esp. in plain mode)
		setProgressInfo("");
	}
	
	/**
	 * Override this method if you want to do any additional initializations
	 * before checking answer.<br/><br/> 
	 * Default implementation increments feedback level, disable input, show answer and 
	 * reset identifier of feedback component.
	 * @param bPass True if the user passed rather than submitting an answer
	 * @throws OmDeveloperException
	 */
	protected void doBeforeCheckingProcessing(boolean bPass) throws OmDeveloperException
	{
		// Increment feedback level
		setAttempt(getAttempt()+1);
		
		// Disable input, show answer
		getComponent("inputbox").setEnabled(false);
		getComponent("inputbox").setBoolean(BoxComponent.PROPERTY_PLAINHIDE,true);
		getComponent("answerbox").setDisplay(true);
		
		// Reset identifier of feedback component
		setFeedbackID(null);
	}
	
	/**
	 * Override this method if you want to do any additional processing with
	 * the user's answer. It is called after checking rightness.<br/><br/>
	 * As it is executed before handling feedback and sending results it is a good place to select
	 * the feedback component to display and to construct answer and summary lines.<br/><br/>
	 * Default implementation does nothing.
	 * @param right True if user was right
	 * @param wrong True if they were wrong
	 * @param pass True if they passed
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	protected void doAfterCheckingProcessing(boolean right,boolean wrong,boolean pass,int attempt)
			throws OmDeveloperException
	{
	}
	
	/**
	 * Override this method to define a default answer checking implementation.
	 * @param attempt Attempt number (1 is first attempt)
	 * @return true if the default answer is right, false if it is wrong
	 * @throws OmDeveloperException
	 */
	protected abstract boolean processDefaultAnswer(int attempt) throws OmDeveloperException;
	
	/**
	 * Override this method to check whether the user's answer is correct or not.
	 * <p>
	 * This is a simple right/wrong flag. To provide feedback as to how they were
	 * wrong, you should call setFeedbackID() [probably only for wrong answers
	 * and certain attempts].
	 * @param attempt Question attempt (1 for first, 2 for second, etc.)
	 * @return True if the user is right; false if they are wrong.
	 * @throws OmDeveloperException
	 */
	protected abstract boolean isRight(int attempt) throws OmDeveloperException;
	
	/**
	 * Override this method if you want to implement tasks.<br/><br/>
	 * Default implementation ignore tasks.
	 * @param task Task
	 * @throws OmDeveloperException
	 */
	protected void doTask(Task task) throws OmDeveloperException
	{
	}
	
	/**
	 * Override this method to change the scoring. The method should call
	 * getResults().setScore.
	 * <p>
	 * Default scoring is out of 3 points: 3 for correct answer first time,
	 * 2 second time, 1 third time, 0 for fail or pass.
	 * @param bRight True if user was right (rather than passing/failing)
	 * @param bPass True if user passed on question
	 * @param iAttempt Attempt number (1 = first attempt)
	 */
	protected void setScore(boolean bRight,boolean bPass,int iAttempt) throws OmDeveloperException
	{
		if (iMaxMarks==0)
		{
			throw new OmDeveloperException(
					"Cannot set score on question: <scoring> not defined in question.xml");
		}
		else if (iMaxMarks<0)
		{
			throw new OmDeveloperException(
					"Cannot set score on question: invalid <scoring> defined in question.xml");
		}
		if(!bRight)
		{
			getResults().setScore(0,bPass?Results.ATTEMPTS_PASS:Results.ATTEMPTS_WRONG);
		}
	  	else
	  	{
	  		getResults().setScore(Math.max(0,iMaxMarks+1-iAttempt),iAttempt);
	  	}
	}
	
	/**
	 * Sets the feedback ID for current question attempt. Should be called from
	 * within isRight().
	 * <p>
	 * Feedback works as follows:
	 * <ul>
	 * <li>You must have a component with id 'feedback'. This component will
	 *   be hidden if this method returns null, and shown otherwise.</li>
	 * <li>If you return "" then no further action is taken. Otherwise you must
	 *   have multiple components with IDs, directly inside the 'feedback'
	 *   component. These will all be hidden except the one named by the
	 *   return value. Components which don't have IDs will be unaffected.</li>
	 * </ul>
	 * @param sID ID of component to use for feedback. May be null if there is
	 *   no feedback this time, or an empty string to display just the 'feedback'
	 *   component without messing with its children
	 */
	protected void setFeedbackID(String sID)
	{
		sFeedbackID=sID;
	}
	
	/**
	 * Searches an appropriate component to use for feedback, returning the identifier of that component 
	 * if it is found. 
	 * @return Identifier of an appropiate component to use for feedback or null if not found
	 * @throws OmDeveloperException
	 */
	protected String findFeedbackID() throws OmDeveloperException
	{
		String id=null;
		QComponent qcFeedback=getComponent("feedback");
		for (QComponent qc:qcFeedback.getComponentChildren())
		{
			if (qc instanceof Testable && !(qc instanceof AnswerComboComponent) && 
					!(qc instanceof SummaryLineComponent) && !(qc instanceof TaskComponent) && qc.hasUserSetID())
			{
				if (((Testable)qc).test())
				{
					id=qc.getID();
					break;
				}
			}
		}
		return id;
	}
	
	/**
	 * Override this method if you want to do any additional processing with
	 * the user's answer. This is called after all default processing.
	 * @param bRight True if user was right
	 * @param bWrong True if they were wrong
	 * @param bPass True if they passed
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	protected void doAdditionalAnswerProcessing(boolean bRight,boolean bWrong,boolean bPass,
			int attempt) throws OmDeveloperException
	{
		// Default does nothing
	}
}
