package om.qengine.uned;

import om.OmException;
import om.qengine.OmService;
import om.qengine.QuestionCache;
import om.question.Question;

/**
 * Question engine Web service.<br/><br/>
 * This contains all public methods of the question engine.<br/><br/>
 * Actual work is deferred to the {@link om.question} package.<br/><br/>
 * This is an special implementation for UNED that try to close question sessions on errors ocurred 
 * at question initialization (errors from <i>start</i> method of <i>om.qengine.OmService</i> class).
 */
public class UnedOmService extends OmService
{
	/**
	 * This method is invoked on error from <i>start</i> method of <i>om.qengine.OmService</i> class.<br/><br/>
	 * This implementation try to close question session if the question is initialized.<br/><br/>
	 * You can override this method within subclasses if you need a different behaviour.
	 * @param qc Question cache
	 * @param q Question
	 */
	@Override
	protected void handleQuestionStartError(QuestionCache qc,Question q)
	{
		if (q!=null)
		{
			try
			{
				qc.returnQuestion(q);
			}
			catch (OmException oe)
			{
			}
		}
	}
}
