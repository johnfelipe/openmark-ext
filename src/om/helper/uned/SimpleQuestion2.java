package om.helper.uned;

import java.util.ArrayList;
import java.util.List;

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

// UNED: 07-10-2011
/**
 * This version of SimpleQuestion2 class extends from om.stdquestion.uned.StandardQuestion 
 * and so uses om.stdquestion.uned.QComponentManager as the component manager.<br/><br/>
 * It tries to emulate functionality of samples.mu120.lib.SimpleQuestion2 class but with 
 * some differences to work well with the new properties of overridden components.<br/><br/>
 * Be careful that this class is not able to resolve by itself if the answer is right nor
 * to generate correct feedback, scores or summaries so if you derive directly from this class 
 * you will have to program those functionalities yourself.<br/><br/>
 * If you want to use a generic class with all that functionalities implemented extend from
 * GenericQuestion2 class from this package instead.<br/><br/>
 * Base class for simple questions that have an input box and an answer box
 * (simultaneously visible) and match certain other 'standard' requirements in
 * their XML.
 * <p>
 * Specifically, the XML must contain the following component IDs:
 * <ul>
 * <li>inputbox - box that contains controls into which user enters their
 * answers</li>
 * <li>answerbox - box that contains information about the answer, e.g. was it
 * right or not</li>
 * <li>wrong - component within answerbox, shown only if answer is wrong and 
 * user not solicited hint</li>
 * <li>still - component within answerbox, shown only if answer is wrong, user not solicited hint
 * and it's not the first time</li>
 * <li>right - component within answerbox, shown only if answer is right</li>
 * <li>pass - component within answerbox, shown only if user passed</li>
 * <li>feedback - component within answerbox, shown only if question has not ended and 
 * user not solicited hint (like wrong but not shown at end of question)</li>
 * <li>reference - component within answerbox, shown only if answer is wrong and question has ended</li>
 * <li>hints - children text components are also considered possible hints, only one of them 
 * can be shown and only if hint, hint1, hint2, ..., hintn components are not shown and their testing 
 * conditions are satisfied. If several components can satisfy these conditions only first of them is shown</li>
 * </ul>
 * There are also some optional component IDs with an special functionality:
 * <ul>
 * <li>hint - component within answerbox, shown only if user solicited hint and its testing conditions 
 * (if they exist) are satisfied. If shown "Hints" button will be disabled so no more hints will be shown</li>
 * <li>hint1, hint2, ..., hintn - components within answerbox, shown only if user solicited
 * hint, hint component is not shown, we are at attempt 1, 2, ..., n (according to the component's identifier) 
 * and their testing conditions (if they exist) are satisfied</li>
 * </ul>
 * There should be examples matching this pattern in the documentation.
 */
public abstract class SimpleQuestion2 extends StandardQuestion
{
    /** true once you get a wrong answer, to cater for the 'hint, wrong, ...' sequence */
    private boolean bPreviouslyWrong = false;
    
    /** If true, question ends when you click OK */
    private boolean bEndNext = false;
    
	/** Max marks, or 0 if no scoring */
	private int iMaxMarks=0;
    
	/** List of components excluded from testing for displaying */
	private final static String[] TEST_EXCEPTIONS=
			{"answer","wrong","still","right","pass","feedback"};
	
	/** List of components excluded from testing for displaying including subcomponents */
	private final static String[] TEST_EXCEPTIONS_NESTED={"hints"};
    
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
			// Ignore error, leave iMaxMarks at default value: 3
			iMaxMarks=3;
		}
        
		// Initialize testing exceptions
        for (String exception:TEST_EXCEPTIONS)
        {
			addQuestionTimeTestingException(exception);
        }
        for (String exception:getHintsTestExceptions())
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
        checkAnswer(false,false);
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
		
		// Now we get the action summary lines for passing
		for (String summaryLine:getSummaryLines())
		{
			getResults().appendActionSummary(summaryLine);
		}
		
		// Decrease current attempt restoring last value
		setAttempt(getAttempt()-1);
		
		// Calls checkAnswer for further processing
		checkAnswer(true,false);
    }
    
    /**
     * Callback that Om calls when the user clicks the 'Hint' button.
     * Treat 'Hint' as a wrong answer for feedback and scoring.
     * @throws OmDeveloperException
     */
    public void actionHint() throws OmDeveloperException
    {
    	//TODO be careful with next line... perhaps setIdAnswersNull() be more correct or not??? 
		// Reset answers
		resetIdAnswers();
		
		// Increase temporarily current attempt
		setAttempt(getAttempt()+1);
		
		// Now we get the action summary lines for hint
		for (String summaryLine:getHintSummaryLines())
		{
			getResults().appendActionSummary(summaryLine);
		}
		
		// Decrease current attempt restoring last value
		setAttempt(getAttempt()-1);
		
		// Calls checkAnswer for further processing
        checkAnswer(false,true);
    }

    /**
     * Callback that Om calls when the user clicks the 'OK' button after seeing
     * the response to their answer. This either ends the question, or hides the
     * answer box and re-enables the input box for another attempt.
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
            if (iAttemptsLeft == 1)
            {
            	setProgressInfo("This is your last attempt.");
            }
            else
            {
            	setProgressInfo("You have "+ iAttemptsLeft+" attempts left.");
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
     * @param bHint True if the user solicited tip
     * @throws OmDeveloperException
     */
    private void checkAnswer(boolean bPass,boolean bHint) throws OmDeveloperException
    {
    	// Do additional initializations before checking rightness
    	doBeforeCheckingProcessing(bPass,bHint);
        
        // Get the four basic states
        if (!bPass && !bHint)
        {
			setMatchDefault(processDefaultAnswer(getAttempt()));
        	setRight(isRight(getAttempt()));
        }
        boolean bWrong=!isRight()&& !bPass && !bHint;
        
		// Do additional processing after checking rightness
		doAfterCheckingProcessing(isRight(),bWrong,bPass,bHint,getAttempt());
        
        // OK now show/hide the basic 'you were wrong' bit
        getComponent("wrong").setDisplay(bWrong);
        getComponent("still").setDisplay(bWrong && bPreviouslyWrong);
        bPreviouslyWrong=bPreviouslyWrong || bWrong;
        getComponent("right").setDisplay(isRight());
        getComponent("pass").setDisplay(bPass);
        
        // Optionally provide a hint out of the feedback block
        if (bHint)
        {
        	//Do "hint" tasks
    		for (Task task:getTasks(TaskComponent.WHEN_HINT))
    		{
    			doTask(task);
    		}
    		
        	// Provide a hint out of the feedback block
            provideHint(getAttempt());
        }
        
        // Should we end next time?
		bEndNext=isRight() || (getAttempt()==getMaxAttempts()) || bPass;
		
        // If so, show answer, a possible reference, and update score
        getComponent("hints").setDisplay(!bEndNext && bHint);
        getComponent("feedback").setDisplay(!bEndNext && !bHint);
        getComponent("answer").setDisplay(bEndNext);
        getComponent("reference").setDisplay(bWrong && bEndNext);
        if (bEndNext)
        {
        	//Do "end" tasks
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
            catch (OmDeveloperException ode)
            {
            	// Ignore, just means question.xml hasn't been update
            	log("Warning: question.xml should be updated to make OK and Next buttons work");
            }
        }
        else
        {
        	//Do "endattempt" tasks
    		for (Task task:getTasks(TaskComponent.WHEN_ENDATTEMPT))
    		{
    			doTask(task);
    		}
        }
        
        // Give overriders a chance
        doAdditionalAnswerProcessing(isRight(),bWrong,bPass,bHint,getAttempt());
        
        // Hide hintButton, if its there, for the final attempt
        if (getAttempt()==getMaxAttempts()-1)
        {
            try
            {
            	getComponent("hintButton").setDisplay(false);
            }
            catch (OmDeveloperException omde)
            {
            }
        }
        
        // Clear progress info (looks bad esp. in plain mode)
        setProgressInfo("");
    }
    
	/**
	 * Override this method if you want to do any additional initializations
	 * before checking answer.<br/><br/> 
	 * Default implementation increments feedback level, disable input, show answer and 
	 * reset identifier of feedback component.
	 * @param bPass True if the user passed rather than submitting an answer
     * @param bHint True if the user solicited tip
	 * @throws OmDeveloperException
	 */
	protected void doBeforeCheckingProcessing(boolean bPass,boolean bHint) throws OmDeveloperException
	{
		// Increment feedback level
		setAttempt(getAttempt()+1);
		
        // Disable input, show answer
        getComponent("inputbox").setEnabled(false);
        getComponent("inputbox").setBoolean(BoxComponent.PROPERTY_PLAINHIDE,true);
        getComponent("answerbox").setDisplay(true);
        
        // Hide all hints
        hideHints();
	}
	
	/**
	 * Override this method if you want to do any additional processing with
	 * the user's answer. It is called after checking rightness.<br/><br/>
	 * As it is executed before handling feedback and sending results it is a good place to select
	 * the feedback component to display and to construct answer and summary lines.<br/><br/>
	 * Default implementation does nothing.
	 * @param right True if user was right
	 * @param wrong True if user was wrong
	 * @param pass True if user passed
	 * @param hint True if user solicited hint
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	protected void doAfterCheckingProcessing(boolean right,boolean wrong,boolean pass,boolean hint,int attempt) 
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
	 * Hide components used as hints.
	 */
	protected void hideHints()
	{
		// First hide hint component
        try
        {
        	getComponent("hint").setDisplay(false);
        }
        catch (OmDeveloperException omde)
        {
        }
        
        // Second hide hint1, hint2, ... components
        for (int i=1;i<getMaxAttempts();i++)
        {
        	StringBuffer hintAttempt=new StringBuffer();
        	hintAttempt.append("hint");
        	hintAttempt.append(i);
        	try
        	{
        		getComponent(hintAttempt.toString()).setDisplay(false);
        	}
        	catch (OmDeveloperException omde)
        	{
        	}
        }
        
        // Finally hide hints component and their candidate children for tips
        try
        {
        	QComponent qcHints=getComponent("hints");
        	qcHints.setDisplay(false);
        	for (QComponent qc:qcHints.getComponentChildren())
        	{
    			if (qc instanceof Testable && !(qc instanceof AnswerComboComponent) && 
    					!(qc instanceof SummaryLineComponent) && !(qc instanceof TaskComponent) && 
    					qc.hasUserSetID())
    			{
    				qc.setDisplay(false);
    			}
        	}
        } catch (OmDeveloperException omde)
        {
        }
	}
	
    /**
     * Provide hints.<br/><br/>
     * There can be a hint called 'hint' that displays on pressing the hint button on an attempt if it satisfy 
     * its testing conditions.<br/><br/>
     * This hint is displayed once only before last attempt and disables 'Hint' button.<br/><br/>
     * Alternatively several hints can be provided called 'hint1', 'hint2', etc. that can be displayed on 
     * the attempt we are (except last attempt) if satisfies their testing conditions.<br/><br/>
     * Finally, if previous tests fail, every child component of 'hints' component (excluding "hint", "hint1", 
     * "hint2", ... components) is tested and if we find one that satisfies its testing conditions it is choosen 
     * as the hint to show.
	 * @param iAttempt Question attempt (1 for first, 2 for second, etc.)
     */
    protected void provideHint(int iAttempt) throws OmDeveloperException
    {
    	boolean searchHint=true;
    	
    	// Fist try to show hint component (when this hint is shown no more hints will be shown)
       	try
       	{
       		QComponent qc=getComponent("hint");
       		if (qc instanceof Testable)
       		{
       			if (((Testable)qc).test())
       			{
           			getComponent("hintButton").setDisplay(false); // once you see it that's it!
           			setFeedbackID("hint");
           			searchHint=false;
       			}
       		}
       		else
       		{
       			getComponent("hintButton").setDisplay(false); // once you see it that's it!
       			setFeedbackID("hint");
       			searchHint=false;
       		}
       	}
       	catch (OmDeveloperException omde)
       	{
       	}
    	
    	// Second try to show hint<iAttempt> component
    	if (searchHint)
    	{
        	StringBuffer idHintAttempt=new StringBuffer();
        	idHintAttempt.append("hint");
        	idHintAttempt.append(iAttempt);
        	try
        	{
        		QComponent qc=getComponent(idHintAttempt.toString());
        		if (qc instanceof Testable)
        		{
        			if (((Testable)qc).test())
        			{
            			setFeedbackID(idHintAttempt.toString());
            			searchHint=false;
        			}
        		}
        		else
        		{
        			setFeedbackID(idHintAttempt.toString());
        			searchHint=false;
        		}
        	}
        	catch (OmDeveloperException omde)
        	{
        	}
    	}
    	
    	// Finally try to show a hint from children components of hints component 
    	// (excluding "hint", "hint1", "hint2", ... components)
    	if (searchHint)
    	{
    		QComponent qcHints=getComponent("hints");
    		for (QComponent qc:qcHints.getComponentChildren())
    		{
    			if (qc instanceof Testable && !(qc instanceof AnswerComboComponent) && 
    					!(qc instanceof SummaryLineComponent) && !(qc instanceof TaskComponent) && 
    					qc.hasUserSetID())
    			{
    				String id=qc.getID();
    				boolean testing=true;
    				if (id.equals("hint"))
    				{
    					testing=false;
    				}
    				else if (id.startsWith("hint"))
    				{
    					testing=false;
    					int i="hint".length();
    					while (!testing && i<id.length())
    					{
    						if (Character.isDigit(id.charAt(i)))
    						{
    							i++;
    						}
    						else
    						{
    							testing=true;
    						}
    					}
    				}
    				if (testing && ((Testable)qc).test())
    				{
    					setFeedbackID(id);
    					break;
    				}
    			}
    		}
    	}
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
		if (iMaxMarks<=0)
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
     * This is quite different from SimpleQuestion1.
     * We simply switch on a message for display, i.e. feedback.
     * Initially all feedback is switched off.
     * Hints are also stored within the feedback XML block.
     * @param sID ID for the QComponent message
     */
    protected void setFeedbackID(String sID) throws OmDeveloperException
    {
        QComponent qc=getComponent(sID);
        if (qc!=null)
        {
            qc.setDisplay(true);
        }
    }
    
	/**
	 * Override this method if you want to do any additional processing with
	 * the user's answer. This is called after all default processing.
	 * @param bRight True if user was right
	 * @param bWrong True if user were wrong
	 * @param bPass True if user passed
     * @param bHint True if user solicited tip
	 * @param iAttempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
    protected void doAdditionalAnswerProcessing(boolean bRight,boolean bWrong,boolean bPass,
    		boolean bHint,int iAttempt) throws OmDeveloperException
    {
        // Default does nothing
    }
    
	/**
	 * Get default answer line to use when user passes the question.<br/><br/>
	 * In this implementation it returns an empty string.<br/><br/>
	 * Override it in subclasses if you want to change that message.
	 * @return Default answer line to use when user passes the question
	 */
    @Override
	protected String getDefaultPassedAnswerLine()
	{
		return "";
	}
    
	/**
	 * Get default action summary line to use when user passes the question.<br/><br/>
	 * In this implementation it returns the attempt's number and the message "Passed".<br/><br/>
	 * Override it in subclasses if you want to change that message.
	 * @return Default action summary line to use when user passes the question
	 */
	protected String getDefaultPassedActionSummaryLine()
	{
		StringBuffer summaryLine=new StringBuffer();
		summaryLine.append("Attempt ");
		summaryLine.append(getAttempt()+1);
		summaryLine.append(": Passed");
		return summaryLine.toString();
	}
	
	/**
	 * Get default action summary line to use when user clicks "Hint" button.<br/><br/>
	 * In this implementation it returns the attempt's number and the message "Hint".<br/><br/>
	 * Override it in subclasses if you want to change that message.
	 * @return Default action summary line to use when user clicks "Hint" button
	 */
	protected String getDefaultHintActionSummaryLine()
	{
		StringBuffer summaryLine=new StringBuffer();
		summaryLine.append("Attempt ");
		summaryLine.append(getAttempt()+1);
		summaryLine.append(": Hint");
		return summaryLine.toString();
	}
	
	/**
	 * Get all action summary lines to add for a hint.<br/>
	 * If question.xml has not &lt;summaryline&gt; components with type="hintsummaryline" gets default action 
	 * summary line for hints.
	 * @return All action summary lines to add for a hint
	 * @throws OmDeveloperException
	 */
	protected List<String> getHintSummaryLines() throws OmDeveloperException
	{
		List<String> summaryLines=new ArrayList<String>();
		List<SummaryLineComponent> summaryLineComponents=new ArrayList<SummaryLineComponent>();
		for (SummaryLineComponent summaryLineComponent:getComponents(SummaryLineComponent.class))
		{
			if (summaryLineComponent.getType().equals(SummaryLineComponent.TYPE_HINTSUMMARYLINE))
			{
				summaryLineComponents.add(summaryLineComponent);
			}
		}
		if (summaryLineComponents.isEmpty() && getParameters().isDefaultSummaryLine())
		{
			summaryLines.add(getDefaultHintActionSummaryLine());
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
	 * @return List of tip components that can be outside from hints but they are also excluded 
	 * from being tested while rendering
	 */
	private List<String> getHintsTestExceptions()
	{
		List<String> hintsTestExceptions=new ArrayList<String>();
		for (QComponent qc:getComponents(QComponent.class))
		{
			String id="";
			try
			{
				id=qc.getID();
			}
			catch (OmDeveloperException e)
			{
				id="";
			}
			if (id.equals("hint"))
			{
				hintsTestExceptions.add("hint");
			}
			else if (id.startsWith("hint"))
			{
				boolean isHint=true;
				for (int i="hint".length();isHint && i< id.length(); i++)
				{
					if (!Character.isDigit(id.charAt(i)))
					{
						isHint=false;
					}
				}
				if (isHint)
				{
					int hintAttempt=-1;
					try
					{
						hintAttempt=Integer.parseInt(id.substring("hint".length()));
						if (hintAttempt>0 && hintAttempt<getMaxAttempts())
						{
							hintsTestExceptions.add(id);
						}
					}
					catch (NumberFormatException e)
					{
					}
				}
			}
		}
		return hintsTestExceptions;
	}
}
