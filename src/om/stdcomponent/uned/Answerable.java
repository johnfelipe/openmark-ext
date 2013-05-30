package om.stdcomponent.uned;

import om.helper.uned.AnswerChecking.AnswerTestProperties;

// UNED: 20-07-2011 - dballestin
/**
 * Components implementing this interface can be used as answer components. 
 */
public interface Answerable
{
	/** 
	 * Property name for enable/disable answerable components for being used as answer components 
	 * (boolean)
	 */
	public static final String PROPERTY_ANSWERENABLED="answerenabled";
	
	/**
	 * @return true if this component is used as an answer component, 
	 * false if it is not used as an answer component
	 */
	public boolean isAnswerEnabled();
	
	/**
	 * Set if this component is going to be used as an answer component (true) or not (false).
	 * @param answerEnabled true to use this component as an answer component, 
	 * false to not use it as an answer component
	 */
	public void setAnswerEnabled(boolean answerEnabled);
	
	/**
	 * Returns if the answer selected in this answer component corresponds with the expected answer.
	 * @param s String with the expected answer
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of editfield and advancedfield components for testing answer purposes
	 * @return true if the answer selected in this answer component corresponds to the expected answer
	 * false otherwise
	 */
	public boolean isAnswer(String s,AnswerTestProperties overrideProperties);
	
	/**
	 * Returns answer rightness.
	 * @return true if the answer is right, false if it is wrong
	 */
	public boolean isRight();
	
	/**
	 * Returns answer line.<br/>
	 * @return Answer line
	 */
	public String getAnswerLine();
}
