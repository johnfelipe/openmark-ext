package om.stdcomponent.uned;

// UNED: 12-07-2011 - dballestin
/**
 * Components implementing this interface will be testable with om.helper.uned.Tester class 
 */
public interface Testable
{
	/** Property name for the test to test (string) */
	public static final String PROPERTY_TEST="test";
	
	/** Property name for the answer's components to test (String) */
	public static final String PROPERTY_ANSWER="answer";
	
	/** Property name for the minimum number of attempts to test (int) */
	public static final String PROPERTY_ATTEMPTSMIN="attemptsmin";
	
	/** Property name for the maximum number of attempts to test (int) */
	public static final String PROPERTY_ATTEMPTSMAX="attemptsmax";
	
	/** Property name for the minimum number of selected answers to test (int) */
	public static final String PROPERTY_SELECTEDANSWERSMIN="selectedanswersmin";
	
	/** Property name for the maximum number of selected answers to test (int) */
	public static final String PROPERTY_SELECTEDANSWERSMAX="selectedanswersmax";
	
	/** Property name for the minimum number of selected right answers to test (int) */
	public static final String PROPERTY_SELECTEDRIGHTANSWERSMIN="selectedrightanswersmin";
	
	/** Property name for the maximum number of selected right answers to test (int) */
	public static final String PROPERTY_SELECTEDRIGHTANSWERSMAX="selectedrightanswersmax";
	
	/** Property name for the minimum number of selected wrong answers to test (int) */
	public static final String PROPERTY_SELECTEDWRONGANSWERSMIN="selectedwronganswersmin";
	
	/** Property name for the maximum number of selected wrong answers to test (int) */
	public static final String PROPERTY_SELECTEDWRONGANSWERSMAX="selectedwronganswersmax";
	
	/** Property name for the minimum number of unselected answers to test (int) */
	public static final String PROPERTY_UNSELECTEDANSWERSMIN="unselectedanswersmin";
	
	/** Property name for the maximum number of unselected answers to test (int) */
	public static final String PROPERTY_UNSELECTEDANSWERSMAX="unselectedanswersmax";
	
	/** Property name for the minimum number of unselected right answers to test (int) */
	public static final String PROPERTY_UNSELECTEDRIGHTANSWERSMIN="unselectedrightanswersmin";
	
	/** Property name for the maximum number of unselected right answers to test (int) */
	public static final String PROPERTY_UNSELECTEDRIGHTANSWERSMAX="unselectedrightanswersmax";
	
	/** Property name for the minimum number of unselected wrong answers to test (int) */
	public static final String PROPERTY_UNSELECTEDWRONGANSWERSMIN="unselectedwronganswersmin";
	
	/** Property name for the maximum number of unselected wrong answers to test (int) */
	public static final String PROPERTY_UNSELECTEDWRONGANSWERSMAX="unselectedwronganswersmax";
	
	/** Property name for the minimum distance to the right answer to test (int) */
	public static final String PROPERTY_RIGHTDISTANCEMIN="rightdistancemin";
	
	/** Property name for the maximum distance to the right answer to test (int) */
	public static final String PROPERTY_RIGHTDISTANCEMAX="rightdistancemax";
	
	/** 
	 * Property name for the answer's type for display testing with editfield or advancedfield 
	 * components.<br/>
	 * If it is null (default value) then it is used the answertype property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_ANSWERTYPE="answertype";
	
	/** 
	 * Property name for considering the answer to be case sensitive (yes) or case insensitive (no)
	 * for display testing with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the casesensitive property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_CASESENSITIVE="casesensitive";
	
	/** 
	 * Property name for ignoring starting and ending whitespaces from answer for display testing 
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the trim property from the answer's component 
	 * (string)
	 */
	public static final String PROPERTY_TRIM="trim";
	
	/** 
	 * Property name for ignoring all whitespaces from answer for display testing with editfield 
	 * or advancedfield components.<br/>
	 * If it is null (default value) then it is used the strip property from the answer's component 
	 * (string)
	 */
	public static final String PROPERTY_STRIP="strip";
	
	/** 
	 * Property name for considering together whitespaces from answer as a single whitespace 
	 * for display testing with editfield or advancedfield components.<br/> 
	 * If it is null (default value) then it is used the singlespaces property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_SINGLESPACES="singlespaces";
	
	/** 
	 * Property name for considering new line characters as whitespaces for display testing 
	 * with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the newlinespace property from the 
	 * answer's component (string)<br/><br/>Note that trim, strip and singlespaces properties 
	 * are affected if this property is set to yes.
	 */
	public static final String PROPERTY_NEWLINESPACE="newlinespace";
	
	/**
	 * Property name for ignoring different text ocurrences (seperated by commas) from anwer 
	 * when testing with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the ignore property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_IGNORE="ignore";
	
	/**
	 * Property name for ignoring text ocurrences that match a regular expression.<br/>
	 * If it is null (default value) then it is used the ignoreregexp property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_IGNOREREGEXP="ignoreregexp";
	
	/**
	 * Property name for ignoring empty lines from answer for display testing with editfield 
	 * or advancedfield components.<br/> 
	 * If it is null (default value) then it is used the ignoreemptylines property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_IGNOREEMPTYLINES="ignoreemptylines";
	
	/**
	 * Property name for error tolerance used in numeric comparisons.<br/>
	 * If it is null (default value) then it is used the tolerance property from the 
	 * answer's component (string)
	 */
	public static final String PROPERTY_TOLERANCE="tolerance";
	
	/** @return Test's name */
	public String getTest();
	
	/**
	 * Set the test.
	 * @param test Test
	 */
	public void setTest(String test);
	
	/** 
	 * @return Answer's components to test separated by comma ',' (OR operator) or plus symbol '+' 
	 * (AND operator)
	 */
	public String getAnswer();
	
	/**
	 * Set the answer's components to test separated by comma ',' (OR operator) or plus symbol '+' 
	 * (AND operator), set it to null if you want to remove definition.
	 * @param answer Answer's components
	 */
	public void setAnswer(String answer);
	
	/** @return Minimum number of attempts to test */
	public int getAttemptsMin();
	
	/**
	 * Set the minimum number of attempts to test.
	 * @param attemptsMin Minimum number of attempts to test
	 */
	public void setAttemptsMin(int attemptsMin);
	
	/** @return Maximum number of attempts for display testing */
	public int getAttemptsMax();
	
	/**
	 * Set the maximum number of attempts to test.
	 * @param attemptsMax Maximum number of attempts to test
	 */
	public void setAttemptsMax(int attemptsMax);
	
	/** @return Minimum number of selected answers to test */
	public int getSelectedAnswersMin();
	
	/**
	 * Set the minimum number of selected answers to test.
	 * @param selectedAnswersMin Minimum number of selected answers to test 
	 */
	public void setSelectedAnswersMin(int selectedAnswersMin);
	
	/** @return Maximum number of selected answers to test */
	public int getSelectedAnswersMax();
	
	/**
	 * Set the maximum number of selected answers to test.
	 * @param selectedAnswersMax Maximum number of selected answers to test 
	 */
	public void setSelectedAnswersMax(int selectedAnswersMax);
	
	/** @return Minimum number of selected right answers to test */
	public int getSelectedRightAnswersMin();
	
	/**
	 * Set the minimum number of selected right answers to test.
	 * @param selectedRightAnswersMin Minimum number of selected right answers to test 
	 */
	public void setSelectedRightAnswersMin(int selectedRightAnswersMin);
	
	/** @return Maximum number of selected right answers to test */
	public int getSelectedRightAnswersMax();
	
	/**
	 * Set the maximum number of selected right answers to test.
	 * @param selectedRightAnswersMax Maximum number of selected right answers to test 
	 */
	public void setSelectedRightAnswersMax(int selectedRightAnswersMax);
	
	/** @return Minimum number of selected wrong answers to test */
	public int getSelectedWrongAnswersMin();
	
	/**
	 * Set the minimum number of selected wrong answers to test.
	 * @param selectedWrongAnswersMin Minimum number of selected wrong answers to test 
	 */
	public void setSelectedWrongtAnswersMin(int selectedWrongAnswersMin);
	
	/** @return Maximum number of selected wrong answers to test */
	public int getSelectedWrongAnswersMax();
	
	/**
	 * Set the maximum number of selected wrong answers to test.
	 * @param selectedWrongAnswersMax Maximum number of selected wrong answers to test 
	 */
	public void setSelectedWrongAnswersMax(int selectedWrongAnswersMax);
	
	/** @return Minimum number of unselected answers to test */
	public int getUnselectedAnswersMin();
	
	/**
	 * Set the minimum number of unselected answers to test.
	 * @param unselectedAnswersMin Minimum number of unselected answers to test 
	 */
	public void setUnselectedAnswersMin(int unselectedAnswersMin);
	
	/** @return Maximum number of unselected answers to test */
	public int getUnselectedAnswersMax();
	
	/**
	 * Set the maximum number of unselected answers to test.
	 * @param unselectedAnswersMax Maximum number of unselected answers to test 
	 */
	public void setUnselectedAnswersMax(int unselectedAnswersMax);
	
	/** @return Minimum number of unselected right answers to test */
	public int getUnselectedRightAnswersMin();
	
	/**
	 * Set the minimum number of unselected right answers to test.
	 * @param unselectedRightAnswersMin Minimum number of selected right answers to test 
	 */
	public void setUnselectedRightAnswersMin(int unselectedRightAnswersMin);
	
	/** @return Maximum number of unselected right answers to test */
	public int getUnselectedRightAnswersMax();
	
	/**
	 * Set the maximum number of unselected right answers to test.
	 * @param unselectedRightAnswersMax Maximum number of unselected right answers to test 
	 */
	public void setUnselectedRightAnswersMax(int unselectedRightAnswersMax);
	
	/** @return Minimum number of unselected wrong answers to test */
	public int getUnselectedWrongAnswersMin();
	
	/**
	 * Set the minimum number of unselected wrong answers to test.
	 * @param unselectedWrongAnswersMin Minimum number of unselected wrong answers to test 
	 */
	public void setUnselectedWrongtAnswersMin(int unselectedWrongAnswersMin);
	
	/** @return Maximum number of unselected wrong answers to test */
	public int getUnselectedWrongAnswersMax();
	
	/**
	 * Set the maximum number of unselected wrong answers to test.
	 * @param unselectedWrongAnswersMax Maximum number of unselected wrong answers to test 
	 */
	public void setUnselectedWrongAnswersMax(int unselectedWrongAnswersMax);
	
	/** @return Minimum distance to the right answer to test */
	public int getRightDistanceMin();
	
	/**
	 * Set the minimum distance to the right answer to test.
	 * @param rightDistanceMin Minimum distance to the right answer to test 
	 */
	public void setRightDistanceMin(int rightDistanceMin);
	
	/** @return Maximum distance to the right answer to test */
	public int getRightDistanceMax();
	
	/**
	 * Set the maximum distance to the right answer to test.
	 * @param rightDistanceMax Maximum distance to the right answer to test 
	 */
	public void setRightDistanceMax(int rightDistanceMax);
	
	/** 
	 * @return Answer's type to test with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the answertype property from the 
	 * answer's component
	 */
	public String getAnswerType();
	
	/**
	 * Set the answer's type to test with editfield or advancedfield components.<br/>
	 * If it is set to null (default value) then it will be used the answertype property from the 
	 * answer's component.
	 * @param answertype Answer's type to test or null to use the answertype property from the 
	 * answer's component
	 */
	public void setAnswerType(String answertype);
	
	/** 
	 * @return "yes" if considering the answer to be case sensitive to test with editfield 
	 * or advancedfield components, "no" if considering it to be case insensitive 
	 * or null if it is used the answertype property from the answer's component
	 */
	public String getCaseSensitive();
	
	/**
	 * Set if the answer is going to be considered case sensitive (true) or case insensitive (false)
	 * to test with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the casesensitive property from the 
	 * answer's component (string)
	 * @param casesensitive "yes" if the answer is going to be considered case sensitive to test 
	 * with editfield or advancedfield components, "no" if it is going to be considered 
	 * case insensitive or null to use the answertype property from the answer's component
	 */
	public void setCaseSensitive(String casesensitive);
	
	/** 
	 * @return "yes" if starting and ending whitespaces from answer are ignored to test 
	 * with editfield or advancedfield components, "no" if they are not ignored 
	 * or null if it is used the trim property from the answer's component
	 */
	public String getTrim();
	
	/**
	 * Set if starting and ending whitespaces from answer are ignored to test with editfield 
	 * or advancedfield components.<br/>
	 * If it is null (default value) then it is used the trim property from the answer's component 
	 * (string)
	 * @param trim "yes" if starting and ending whitespaces from answer are going to be ignored 
	 * to test with editfield or advancedfield components, "no" if they are not going to be ignored 
	 * or null to use the trim property from the answer's component
	 */
	public void setTrim(String trim);
	
	/** 
	 * @return "yes" if all whitespaces from answer are ignored to test with editfield 
	 * or advancedfield components, "no" if they are not ignored or null if it is used 
	 * the strip property from the answer's component
	 */
	public String getStrip();
	
	/**
	 * Set if all whitespaces from answer are ignored to test with editfield or advancedfield 
	 * components.<br/>
	 * If it is null (default value) then it is used the strip property from the answer's component 
	 * (string)
	 * @param strip "yes" if all whitespaces from answer are going to be ignored to test with 
	 * editfield or advancedfield components, "no" if they are not going to be ignored 
	 * or null to use the strip property from the answer's component
	 */
	public void setStrip(String strip);
	
	/** 
	 * @return "yes" if together whitespaces from answer are considered as single whitespaces 
	 * to test with editfield or advancedfield components, "no" if they are not considered 
	 * as single whitespaces or null if it is used the singlespaces property from 
	 * the answer's component
	 */
	public String getSingleSpaces();
	
	/**
	 * Set if together whitespaces from answer are considered as single whitespaces 
	 * to test with editfield or advancedfield components.<br/>
	 * If it is null (default value) then it is used the singlespaces property from the 
	 * answer's component (string)
	 * @param singlespaces "yes" if together whitespaces from answer are going to be considered as 
	 * single whitespaces to test with editfield or advancedfield components, 
	 * "no" if they are not going to be considered as single whitespaces 
	 * or null to use the singlespaces property from the answer's component
	 */
	public void setSingleSpaces(String singlespaces);
	
	/** 
	 * @return "yes" if new line characters from answer are considered as whitespaces 
	 * to test with editfield components, "no" if they are not considered as whitespaces 
	 * or null if it is used the newlinespace property from the answer's component
	 */
	public String getNewLineSpace();
	
	/**
	 * Set if new line characters from answer are considered as whitespaces to test with 
	 * editfield components.<br/>
	 * If it is null (default value) then it is used the newlinespace property from the 
	 * answer's component (string)
	 * @param newlinespace "yes" if new line characters from answer are going to be considered 
	 * as whitespaces to test with editfield or advancedfield components, 
	 * "no" if they are not going to be considered as whitespaces 
	 * or null to use the newlinespace property from the answer's component
	 */
	public void setNewLineSpace(String newlinespace);
	
	/**
	 * @return Text ocurrences (separated by commas) to ignore from answer
	 */
	public String getIgnore();
	
	/**
	 * Set text ocurrences (separated by commas) to ignore from answer.
	 * @param ignore Text ocurrences (separated by commas) to ignore from answer
	 */
	public void setIgnore(String ignore);
	
	/**
	 * @return Regular expression to ignore matched text ocurrences from answer
	 */
	public String getIgnoreRegExp();
	
	/**
	 * Set regular expression to ignore matched text ocurrences from answer.
	 * @param ignoreRegExp Regular expression to ignore matched text ocurrences from answer
	 */
	public void setIgnoreRegExp(String ignoreRegExp);
	
	/** 
	 * @return "yes" if empty lines from answer are ignored to test with editfield components, 
	 * "no" if they are not ignored or null if it is used the ignoreemptylines property 
	 * from the answer's component
	 */
	public String getIgnoreEmptyLines();
	
	/**
	 * Set if empty lines characters from answer are ignored to test with editfield components.<br/>
	 * If it is null (default value) then it is used the ignoreemptylines property from the 
	 * answer's component (string)
	 * @param ignoreemptylines "yes" if empty lines from answer are ignored to test with editfield 
	 * components, "no" if they are not going to be ignored 
	 * or null to use the ignoreemptylines property from the answer's component
	 */
	public void setIgnoreEmptyLines(String ignoreemptylines);
	
	/**
	 * @return Error tolerance used in numeric comparisons
	 */
	public String getTolerance();
	
	/**
	 * Set error tolerance to use in numeric comparisons
	 * @param tolerance Error tolerance to use in numeric comparisons
	 */
	public void setTolerance(String tolerance);
	
	/**
	 * Tests if this component match all conditions to be displayed.
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if this component match all conditions to be displayed, false otherwise
	 */
	public boolean test(String idAnswer);
	
	/**
	 * Tests if this component match all conditions to be displayed.
	 * @return true if this component match all conditions to be displayed, false otherwise
	 */
	public boolean test();
}
