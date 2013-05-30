package om.graph.uned;

import om.stdquestion.uned.StandardQuestion;

// UNED: 24-08-2011 - dballestin
/**
 * Graph items implementing this interface allow to use placeholder in their attributes, with exception
 * of ID attribute
 */
public interface Replaceable
{
	/**
	 * @return Get standard question
	 */
	public StandardQuestion getQuestion();
	
	/**
	 * Set standard question
	 * @param sq Standard question
	 */
	public void setQuestion(StandardQuestion sq);
	
	/**
	 * Set placeholder for an attribute
	 * @param attribute Attribute
	 * @param placeholder Placeholder
	 * @throws AttributePlaceholderNotImplementedException
	 */
	public void setAttributePlaceholder(String attribute,String placeholder) 
			throws AttributePlaceholderNotImplementedException;
}
