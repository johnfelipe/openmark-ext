package om.helper.uned;

import om.OmDeveloperException;
import om.OmException;

// UNED: 07-07-2011 - dballestin
/**
 * Generic base class for simple questions that have an input box and an answer box
 * (simultaneously visible) and match certain other 'standard' requirements
 * in their XML.<br/><br/>
 * This class implements a generic functionality to be able to resolve by itself if the answer 
 * is right and to generate correct feedback, scores or summaries.
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
 *   returns something other than null.
 * </ul>
 * There should be examples matching this pattern in the documentation.
 */
public class GenericQuestion extends SimpleQuestion1
{
	@Override
	protected void init() throws OmException
	{
		// Randoms
		initializeRandoms(getRandoms());
		
		// Placeholders
		initializePlaceholders(getReplaceholders());
		
		// Counters
		initializeCounters();
		
		// Call super.init() to make om.stdquestion.uned.SimpleQuestion1 initializations 
		// before rendering question
		super.init();
	}
	
	/**
	 * Override this method to change the default answer checking implementation.<br/><br/>
	 * Process default answer constructing list of selected answers (identifiers), 
	 * counting hits and failures and returning true if the default answer is right or false
	 * if it is wrong.
	 * @param iAttempt Attempt number (1 is first attempt)
	 * @return true if the default answer is right, false if it is wrong
	 * @throws OmDeveloperException
	 */
	@Override
	protected boolean processDefaultAnswer(int iAttempt) throws OmDeveloperException
	{
		return processDefaultAnswer(true,iAttempt);
	}
	
	@Override
	protected boolean isRight(int iAttempt) throws OmDeveloperException
	{
		return isRightDefault();
	}
	
	/**
	 * Override this method if you want to do any additional initializations
	 * before checking answer.<br/><br/> 
	 * This implementation, in addition to om.helper.uned.SimpleQuestion1 initializations, 
	 * reset variable components and recalculate replaceholder values to apply possible changes.
	 * @param bPass True if the user passed rather than submitting an answer
	 * @throws OmDeveloperException
	 */
	@Override
	protected void doBeforeCheckingProcessing(boolean bPass) throws OmDeveloperException
	{
		// Call super.doBeforeCheckingProcessing(bPass) to make om.helper.uned.SimpleQuestion1 
		// initializations 
		super.doBeforeCheckingProcessing(bPass);
		
		// Reset variables
		resetVars(getVariables());
		
		// Recalculate replaceholders
		try
		{
			resetPlaceholders(getReplaceholders());
		}
		catch (OmDeveloperException ode)
		{
			throw ode;
		}
		catch (OmException oe)
		{
			throw new OmDeveloperException(oe.getMessage(),oe);
		}
	}
	
	/**
	 * Override this method if you want to do any additional processing with
	 * the user's answer. It is called after checking rightness.<br/><br/>
	 * As it is executed before handling feedback and sending results it is a good place to select
	 * the feedback component to display and to construct answer and summary lines.<br/><br/>
	 * This implementation selects the feedback component to display and constructs answer and 
	 * summary lines.
	 * @param right True if user was right
	 * @param wrong True if they were wrong
	 * @param pass True if they passed
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	@Override
	protected void doAfterCheckingProcessing(boolean right,boolean wrong,boolean pass,int attempt)
			throws OmDeveloperException
	{
		// Feedback
		if (wrong)
		{
       		String feedbackId=findFeedbackID();
       		if (feedbackId!=null)
       		{
       			setFeedbackID(feedbackId);
       		}
		}
		
		// Question line and answer line
		if (right || pass || attempt>=getMaxAttempts())
		{
	  		setQuestionLine(getQuestionLine());
       		setAnswerLine(getAnswerLine());
		}
		
  		// Summary lines
		if (!pass)
		{
	  		for (String summaryLine:getSummaryLines())
	  		{
	  			appendSummaryLine(summaryLine);
	  		}
		}
	}
	
	/**
	 * Override this method if you want to implement new custom tasks.<br/><br/>
	 * This implementation only execute standard tasks.
	 * @param task Task
	 * @throws OmDeveloperException
	 */
	@Override
	protected void doTask(Task task) throws OmDeveloperException
	{
		// Execute standard tasks
		task.execute();
	}
}
