package om.helper.uned;

import om.OmDeveloperException;
import om.OmException;

// UNED: 07-10-2011 - dballestin
/**
 * Generic base class for simple questions that have an input box and an answer box
 * (simultaneously visible) and match certain other 'standard' requirements
 * in their XML.<br/><br/>
 * This class implements a generic functionality to be able to resolve by itself if the answer 
 * is right and to generate correct scores or summaries.
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
public class GenericQuestion2 extends SimpleQuestion2
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
		
		// Call super.init() to make om.stdquestion.uned.SimpleQuestion2 initializations 
		// before rendering question
		super.init();
	}
	
	/**
	 * Override this method to change the default answer checking implementation.<br/><br/>
	 * Process default answer constructing list of selected answers (identifiers), 
	 * counting hits and failures and returning true if the default answer is right or false
	 * if it is wrong.
	 * @param attempt Attempt number (1 is first attempt)
	 * @return true if the default answer is right, false if it is wrong
	 * @throws OmDeveloperException
	 */
	@Override
	protected boolean processDefaultAnswer(int attempt) throws OmDeveloperException
	{
		return processDefaultAnswer(true,attempt);
	}
	
	@Override
	protected boolean isRight(int attempt) throws OmDeveloperException
	{
		return isRightDefault();
	}
	
	/**
	 * Override this method if you want to do any additional initializations
	 * before checking answer.<br/><br/> 
	 * This implementation, in addition to om.helper.uned.SimpleQuestion2 initializations, 
	 * reset variable components and recalculate replaceholder values to apply possible changes.
	 * @param bPass True if the user passed rather than submitting an answer
     * @param bHint True if the user solicited tip
	 * @throws OmDeveloperException
	 */
	@Override
	protected void doBeforeCheckingProcessing(boolean bPass,boolean bHint) throws OmDeveloperException
	{
		// Call super.doBeforeCheckingProcessing(bPass) to make om.helper.uned.SimpleQuestion1 
		// initializations 
		super.doBeforeCheckingProcessing(bPass,bHint);
		
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
	 * As it is executed before sending results it is a good place to construct answer and summary lines.
	 * <br/><br/>
	 * This implementation constructs answer and summary lines.
	 * @param right True if user was right
	 * @param wrong True if user was wrong
	 * @param pass True if user passed
	 * @param hint True if user solicited hint
	 * @param attempt Attempt number (1 is first attempt)
	 * @throws OmDeveloperException
	 */
	@Override
	protected void doAfterCheckingProcessing(boolean right,boolean wrong,boolean pass,boolean hint,
			int attempt) throws OmDeveloperException
	{
		// Question line and answer line
		if (right || pass || attempt>=getMaxAttempts())
		{
	  		setQuestionLine(getQuestionLine());
       		setAnswerLine(getAnswerLine());
		}
		
  		// Summary lines
		if (!pass && !hint)
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
