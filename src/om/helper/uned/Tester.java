package om.helper.uned;

import java.util.List;
import java.util.Map;

import om.OmDeveloperException;
import om.helper.uned.AnswerChecking;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.stdcomponent.uned.AnswerComboComponent;
import om.stdcomponent.uned.Answerable;
import om.stdcomponent.uned.RandomComponent;
import om.stdcomponent.uned.Testable;
import om.stdcomponent.uned.VariableComponent;
import om.stdquestion.QComponent;
import om.stdquestion.uned.StandardQuestion;
import om.stdquestion.uned.StandardQuestion.QuestionCounters;

// UNED: 12-07-2011 - dballestin
/**
 * This is a class to do testing for components.<br/>
 * It is used to avoid code duplication because several components need to do the same tests.
 */
public class Tester
{
	/** Test that always is true */
	public static final String TEST_TRUE="true";
	
	/** Test that is true if user selected right answer */
	public static final String TEST_RIGHT="right";
	
	/** Test that is true if user not selected right answer (selected wrong answer or passed) */
	public static final String TEST_NOTRIGHT="notright";
	
	/** Test that is true if user selected wrong answer */
	public static final String TEST_WRONG="wrong";
	
	/** Test that is true if user not selected wrong answer (selected right answer or passed) */
	public static final String TEST_NOTWRONG="notwrong";
	
	/** Test that is true if user passed the question */
	public static final String TEST_PASSED="passed";
	
	/** Test that is true if user not passed the question (selected right or wrong answer) */
	public static final String TEST_NOTPASSED="notpassed";
	
	/** Test that allows testing value of a random component */
	public static final String TEST_RANDOM="random";
	
	/** Test that allows testing a numeric comparison */
	public static final String TEST_NUMCMP="numcmp";
	
	/** Property name for the test for display testing (string) */
	public static final String PROPERTY_TEST="test";
	
	/** Symbol used as OR operator in answer property: ',' */
	private static final char ANSWER_OR_SYMBOL=',';
	
	/** Symbol used as AND operator in answer property: '+' (UNICODE: '\u002B') */
	private static final char ANSWER_AND_SYMBOL='+';
	
	/** Symbol used as NOT operator in answer property: '!' */
	private static final char ANSWER_NOT_SYMBOL='!';
	
	/** 
	 * Character used in test property to open a selector used for testing a single answer
	 * component
	 */
	private static final char TEST_SELECTOR_OPEN='['; 
	
	/** 
	 * Character used in test property to close a selector used for testing a single answer
	 * component
	 */
	private static final char TEST_SELECTOR_CLOSE=']'; 
	
	/**
	 * Character used in test property to separate a component identifier of an expression from a
	 * selector
	 */
	private static final char TEST_SELECTOR_COLON=':';
	
	/** Symbol used as OR operator in a selector's expression of test property: ',' */
	private static final String TEST_SELECTOR_OR_SYMBOL=",";
	
	/** 
	 * Character used in answer identifiers to open a selector used for selecting a single answer
	 * in components that allow several answers from user (e.g. editfields)
	 */
	private static final char ANSWER_SELECTOR_OPEN='['; 
	
	/** 
	 * Character used in answer identifiers to close a selector used for selecting a single answer
	 * in components that allow several answers from user (e.g. editfields)
	 */
	private static final char ANSWER_SELECTOR_CLOSE=']'; 
	
	/**
	 * @param idAnswer Identifier of component selected for answer (including answer selector 
	 * if exists)
	 * @return Same identifier but without selector 
	 */
	private static String getOnlyId(String idAnswer)
	{
		String onlyId=idAnswer;
		if (getAnswerSelector(idAnswer)!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=
					AnswerChecking.indexOfCharacter(idAnswer,ANSWER_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				onlyId=idAnswer.substring(0,iStartSelector);
			}
		}
		if (onlyId.length()>0 && onlyId.charAt(0)==ANSWER_NOT_SYMBOL)
		{
			if (onlyId.length()>1)
			{
				onlyId=onlyId.substring(1);
			}
			else
			{
				onlyId="";
			}
		}
		return AnswerChecking.replaceEscapeChars(onlyId);
	}
	
	/**
	 * Gets attribute's selector if defined, otherwise return null.<br/><br/>
	 * Note that there are availabe some escape sequences in the attribute's selector:<br/>
	 * <table border="1">
	 * <tr><th>Escape sequence</th><th>Replaced character</th></tr>
	 * <tr><td>\,</td><td>,</td></tr>
	 * <tr><td>\?</td><td>?</td></tr>
	 * <tr><td>\:</td><td>:</td></tr>
	 * <tr><td>\[</td><td>[</td></tr>
	 * <tr><td>\]</td><td>]</td></tr>
	 * <tr><td>\n</td><td>New line character</td></tr>
	 * <tr><td>\(</td><td>(</td></tr>
	 * <tr><td>\)</td><td>)</td></tr>
	 * <tr><td>\{</td><td>{</td></tr>
	 * <tr><td>\}</td><td>}</td></tr>
	 * <tr><td>\!</td><td>!</td></tr>
	 * <tr><td>\#</td><td>#</td></tr>
	 * <tr><td>\&</td><td>&</td></tr>
	 * <tr><td>\"</td><td>"</td></tr>
	 * <tr><td>\\</td><td>\</td></tr>
	 * </table>
	 * @return Attribute's selector if defined, null otherwise
	 */
	private static String getAnswerSelector(String idAnswer)
	{
		String answerSelector=null;
		Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
		if (idAnswer!=null)
		{
			int iStartSelector=
					AnswerChecking.indexOfCharacter(idAnswer,ANSWER_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1 && iStartSelector+1<idAnswer.length())
			{
				iStartSelector++;
				int iEndSelector=AnswerChecking.indexOfCharacter(
						idAnswer,ANSWER_SELECTOR_CLOSE,iStartSelector,escapeSequences);
				if (iEndSelector!=-1)
				{
					answerSelector=idAnswer.substring(iStartSelector,iEndSelector);
				}
			}
		}
		return answerSelector==null?null:AnswerChecking.replaceEscapeChars(answerSelector);
	}
	
	/**
	 * @param sq Standard question
	 * @param qc Testable component
	 * @return Utility class instance that overrides some properties of editfield or advancedfield
	 * components with values from this component, used for testing answer purposes
	 */
	private static AnswerTestProperties getAnswerOverrideProperties(StandardQuestion sq,Testable qc) {
		AnswerTestProperties override=null;
		if (qc==null)
		{
			override=new AnswerTestProperties();
		}
		else
		{
			override=new AnswerTestProperties(qc.getAnswerType(),qc.getCaseSensitive(),qc.getTrim(),
					qc.getStrip(),qc.getSingleSpaces(),qc.getNewLineSpace(),qc.getIgnore(),
					qc.getIgnoreRegExp(),qc.getIgnoreEmptyLines(),qc.getTolerance());
		}
		return override;
	}
	
	/**
	 * Tests if the identifier of component selected for answer (including answer selector if exists) 
	 * match the conditions of this component.
	 * @param sq Standard question
	 * @param qc Testable component
	 * @param idAnswer Identifier of component selected for answer (including answer selector if exists)
	 * @return true if the identifier of component selected for answer (including answer selector 
	 * if exists) match the conditions of this component, false otherwise
	 */
	public static boolean testAnswer(StandardQuestion sq,Testable qc,String idAnswer)
	{
		boolean testing=true;
		String answer=null;
		if (qc!=null)
		{
			answer=qc.getAnswer();
		}
		if (answer!=null)
		{
			List<String> orAnswers=AnswerChecking.splitButNotInsideSelectors(
					answer,ANSWER_OR_SYMBOL,AnswerChecking.getEscapeSequences());
			testing=false;
			for (String orAnswer:orAnswers)
			{
				List<String> andAnswers=AnswerChecking.splitButNotInsideSelectors(
						orAnswer,ANSWER_AND_SYMBOL,AnswerChecking.getEscapeSequences());
				boolean andTesting=true;
				for (String andAnswer:andAnswers)
				{
					boolean notTesting=andAnswer.length()>0 && andAnswer.charAt(0)==ANSWER_NOT_SYMBOL;
					String onlyIdAnswer=getOnlyId(andAnswer);
					if (idAnswer!=null && idAnswer.equals(onlyIdAnswer))
					{
						try
						{
							QComponent qcAnswer=sq.getComponent(onlyIdAnswer);
							if (qcAnswer instanceof Answerable)
							{
								String answerSelector=getAnswerSelector(andAnswer);
								if (answerSelector!=null)
								{
									boolean isAnswer=
											((Answerable)qcAnswer).isAnswer(answerSelector,
											getAnswerOverrideProperties(sq,qc));
									if (isAnswer && notTesting)
									{
										andTesting=false;
										break;
									}
									else if (!isAnswer && !notTesting)
									{
										andTesting=false;
										break;
									}
								}
								else if (notTesting)
								{
									andTesting=false;
									break;
								}
							}
						}
						catch (OmDeveloperException e)
						{
							andTesting=false;
								break;
						}
					}
					else if (sq.getVariablesIds().contains(onlyIdAnswer))
					{
						try
						{
							VariableComponent variable=(VariableComponent)sq.getComponent(onlyIdAnswer);
							
							String valueSelector=getAnswerSelector(andAnswer);
							if (valueSelector!=null)
							{
								boolean isValue=
										variable.isValue(valueSelector,getAnswerOverrideProperties(sq,qc));
								if (isValue && notTesting)
								{
									andTesting=false;
									break;
								}
								else if (!isValue && !notTesting)
								{
									andTesting=false;
									break;
								}
							}
							else if (notTesting)
							{
								andTesting=false;
								break;
							}
						}
						catch (OmDeveloperException e)
						{
							andTesting=false;
								break;
						}
					}
					else if (sq.getAnswerCombosIds().contains(onlyIdAnswer))
					{
						if (onlyIdAnswer.equals(StandardQuestion.DEFAULT_ANSWER_ID))
						{
							if (notTesting && sq.isMatchDefault())
							{
								andTesting=false;
								break;
							}
							else if (!notTesting && !sq.isMatchDefault())
							{
								andTesting=false;
								break;
							}
						}
						else
						{
							try
							{
								QComponent qcAnswerCombo=sq.getComponent(onlyIdAnswer);
								if (qcAnswerCombo instanceof AnswerComboComponent)
								{
									AnswerComboComponent answercombo=
											(AnswerComboComponent)qcAnswerCombo;
									if (notTesting && answercombo.test())
									{
										andTesting=false;
										break;
									}
									else if (!notTesting && !answercombo.test())
									{
										andTesting=false;
										break;
									}
								}
							}
							catch (OmDeveloperException e)
							{
								andTesting=false;
								break;
							}
						}
					}
					else if (!notTesting)
					{
						andTesting=false;
						break;
					}
				}
				if (andTesting)
				{
					testing=true;
					break;
				}
			}
		}
		return testing;
	}
	
	/**
	 * Tests if the components selected for answer match the conditions of this component.
	 * @param sq Standard question
	 * @param qc Testable component
	 * @param idAnswers List with identifiers of the answer's components selected 
	 * (including answer selector if exists)
	 * @return true if the components selected for answer match the conditions of this component, 
	 * false otherwise
	 */
	public static boolean testAnswer(StandardQuestion sq,Testable qc,List<String> idAnswers)
	{
		boolean testing=true;
		String answer=null;
		if (qc!=null)
		{
			answer=qc.getAnswer();
		}
		if (answer!=null)
		{
			if (idAnswers!=null && idAnswers.size()==1)
			{
				testing=testAnswer(sq,qc,idAnswers.get(0));
			}
			else
			{
				List<String> orAnswers=AnswerChecking.splitButNotInsideSelectors(
						answer,ANSWER_OR_SYMBOL,AnswerChecking.getEscapeSequences());
				testing=false;
				for (String orAnswer:orAnswers)
				{
					List<String> andAnswers=AnswerChecking.splitButNotInsideSelectors(
							orAnswer,ANSWER_AND_SYMBOL,AnswerChecking.getEscapeSequences());
					boolean andTesting=true;
					for (String andAnswer:andAnswers)
					{
						boolean notTesting=andAnswer.length()>0 && andAnswer.charAt(0)==ANSWER_NOT_SYMBOL;
						String onlyIdAnswer=getOnlyId(andAnswer);
						if (idAnswers!=null && idAnswers.contains(onlyIdAnswer))
						{
							try
							{
								QComponent qcAnswer=sq.getComponent(onlyIdAnswer);
								if (qcAnswer instanceof Answerable)
								{
									String answerSelector=getAnswerSelector(andAnswer);
									if (answerSelector!=null)
									{
										boolean isAnswer=
											((Answerable)qcAnswer).isAnswer(answerSelector,
											getAnswerOverrideProperties(sq,qc));
										if (isAnswer && notTesting)
										{
											andTesting=false;
											break;
										}
										else if (!isAnswer && !notTesting)
										{
											andTesting=false;
											break;
										}
									}
									else if (notTesting)
									{
										andTesting=false;
										break;
									}
								}
							} catch (OmDeveloperException e)
							{
								andTesting=false;
								break;
							}
						}
						else if (sq.getVariablesIds().contains(onlyIdAnswer))
						{
							try
							{
								VariableComponent variable=(VariableComponent)sq.getComponent(onlyIdAnswer);
								
								String valueSelector=getAnswerSelector(andAnswer);
								if (valueSelector!=null)
								{
									boolean isValue=
											variable.isValue(valueSelector,getAnswerOverrideProperties(sq,qc));
									if (isValue && notTesting)
									{
										andTesting=false;
										break;
									}
									else if (!isValue && !notTesting)
									{
										andTesting=false;
										break;
									}
								}
								else if (notTesting)
								{
									andTesting=false;
									break;
								}
							}
							catch (OmDeveloperException e)
							{
								andTesting=false;
									break;
							}
						}
						else if (sq.getAnswerCombosIds().contains(onlyIdAnswer))
						{
							if (onlyIdAnswer.equals(StandardQuestion.DEFAULT_ANSWER_ID))
							{
								if (notTesting && sq.isMatchDefault())
								{
									andTesting=false;
									break;
								}
								else if (!notTesting && !sq.isMatchDefault())
								{
									andTesting=false;
									break;
								}
							}
							else
							{
								try
								{
									QComponent qcAnswerCombo=sq.getComponent(onlyIdAnswer);
									if (qcAnswerCombo instanceof AnswerComboComponent)
									{
										AnswerComboComponent answercombo=
												(AnswerComboComponent)qcAnswerCombo;
										if (notTesting && answercombo.test())
										{
											andTesting=false;
											break;
										}
										else if (!notTesting && !answercombo.test())
										{
											andTesting=false;
											break;
										}
									}
								}
								catch (OmDeveloperException e)
								{
									andTesting=false;
									break;
								}
							}
						}
						else if (!notTesting)
						{
							andTesting=false;
							break;
						}
					}
					if (andTesting)
					{
						testing=true;
						break;
					}
				}
			}
		}
		return testing;
	}
	
	/**
	 * Tests if values of counters match the conditions of this component.
	 * @param sq Standard question
	 * @param qc Testable component
	 * @return true if the values of counters match the conditions of this component, false otherwise
	 */
	public static boolean testCounters(StandardQuestion sq,Testable qc)
	{
		boolean testing=true;
		QuestionCounters counters=sq.getCounters();
		if (qc!=null && counters!=null)
		{
			testing=counters.getSelectedAnswers()>=qc.getSelectedAnswersMin() &&
					counters.getSelectedAnswers()<=qc.getSelectedAnswersMax() &&
					counters.getSelectedRightAnswers()>=qc.getSelectedRightAnswersMin() &&
					counters.getSelectedRightAnswers()<=qc.getSelectedRightAnswersMax() &&
					counters.getSelectedWrongAnswers()>=qc.getSelectedWrongAnswersMin() &&
					counters.getSelectedWrongAnswers()<=qc.getSelectedWrongAnswersMax() &&
					counters.getUnselectedAnswers()>=qc.getUnselectedAnswersMin() &&
					counters.getUnselectedAnswers()<=qc.getUnselectedAnswersMax() &&
					counters.getUnselectedRightAnswers()>=qc.getUnselectedRightAnswersMin() &&
					counters.getUnselectedRightAnswers()<=qc.getUnselectedRightAnswersMax() &&
					counters.getUnselectedWrongAnswers()>=qc.getUnselectedWrongAnswersMin() &&
					counters.getUnselectedWrongAnswers()<=qc.getUnselectedWrongAnswersMax() &&
					counters.getRightDistance()>=qc.getRightDistanceMin() &&
					counters.getRightDistance()<=qc.getRightDistanceMax();
		}
		return testing;
	}
	
	/**
	 * @param test Test selected for testing (including test's selector if exists)
	 * @return Same test but without selector 
	 */
	private static String getOnlyTest(String test)
	{
		String onlyTest=test;
		if (getTestSelector(test)!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=AnswerChecking.indexOfCharacter(test,TEST_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				onlyTest=test.substring(0,iStartSelector);
			}
		}
		return onlyTest;
	}
	
	/**
	 * Gets test's selector if defined, otherwise return null.<br/><br/>
	 * <b>IMPORTANT:</b> This method has been implemented to allow using characters '[' and ']' 
	 * inside the selector, so if a new '[' character is found next ']' character it is considered 
	 * to close that '[' character and it is not considered the end of selector. However 
	 * it is not able to match nested ocurrences of '[' and ']' characters.
	 * @param test Test selected for testing (including test's selector if exists)
	 * @return Test's selector if defined, null otherwise
	 */
	private static String getTestSelector(String test)
	{
		String testSelector=null;
		Map<Character, String> escapeSequences=AnswerChecking.getEscapeSequences();
		if (test!=null)
		{
			int iStartSelector=AnswerChecking.indexOfCharacter(test,TEST_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				iStartSelector++;
				boolean searchEndSelector=true;
				int iEndSelector=AnswerChecking.indexOfCharacter(
						test,TEST_SELECTOR_CLOSE,iStartSelector,escapeSequences);
				int i=iStartSelector;
				while (iEndSelector!=-1 && searchEndSelector)
				{
					int iOtherStartSelector=
							AnswerChecking.indexOfCharacter(test,TEST_SELECTOR_OPEN,i,escapeSequences);
					if (iOtherStartSelector!=-1 && iOtherStartSelector<iEndSelector)
					{
						int iEndSelectorTmp=iEndSelector;
						i=iEndSelector+1;
						iEndSelector=
								AnswerChecking.indexOfCharacter(test,TEST_SELECTOR_CLOSE,i,escapeSequences);
						if (iEndSelector==-1)
						{
							iEndSelector=iEndSelectorTmp;
							searchEndSelector=false;
						}
					}
					else
					{
						searchEndSelector=false;
					}
				}
				if (iEndSelector!=-1)
				{
					testSelector=test.substring(iStartSelector,iEndSelector);
				}
			}
		}
		return testSelector;
	}
	
	/**
	 * Get random component's identifier from selector to use with "random" test
	 * @param selector Selector
	 * @return Random component's identifier from selector to use with "random" test
	 */
	private static String getTestComponentId(String selector)
	{
		String componentId=selector;
		if (getTestExpressionId(selector)!=null)
		{
			componentId=selector.substring(0,selector.indexOf(TEST_SELECTOR_COLON));
		}
		return componentId;
	}
	
	/**
	 * Get expression with values for random component to use with "random" test
	 * @param selector Selector
	 * @return Expression with values for random component to use with "random" test
	 */
	private static String getTestExpressionId(String selector)
	{
		StringBuffer testExpression=null;
		if (selector!=null)
		{
			int iColon=selector.indexOf(TEST_SELECTOR_COLON);
			if (iColon>0 && iColon<selector.length()-1)
			{
				testExpression=new StringBuffer(selector.substring(iColon+1));
			}
		}
		return testExpression==null?null:testExpression.toString();
	}
	
	/**
	 * @param sq Standard question
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if the user's answer is right for the answer's component, false if not
	 */
	private static boolean isRight(StandardQuestion sq,String idAnswer)
	{
		boolean right=false;
		List<String> idAnswers=sq.getIdAnswers();
		if (idAnswers!=null && idAnswers.contains(idAnswer))
		{
			QComponent qc=null;
			try {
				qc=sq.getComponent(idAnswer); 
			} catch (OmDeveloperException ode) {
				qc=null;
			}
			if (qc!=null && qc instanceof Answerable)
			{
				right=((Answerable)qc).isRight();
			}
		}
		return right;
	}
	
	/**
	 * @param sq Standard question
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if the user's answer is wrong for the answer's component, false if not
	 */
	private static boolean isWrong(StandardQuestion sq,String idAnswer)
	{
		boolean wrong=false;
		List<String> idAnswers=sq.getIdAnswers();
		if (idAnswers!=null && idAnswers.contains(idAnswer))
		{
			QComponent qc=null;
			try {
				qc = sq.getComponent(idAnswer);
			} catch (OmDeveloperException ode) {
				qc=null;
			}
			if (qc!=null && qc instanceof Answerable)
			{
				wrong=!((Answerable)qc).isRight();
			}
		}
		return wrong;
	}
	
	/**
	 * Tests if random component's value is one of the received list of values. 
	 * @param sq Standard question
	 * @param randomId Random component's identifier
	 * @param randomValues List of values
	 * @return true if random component's value is one of the received list of values,
	 * false otherwise
	 */
	private static boolean testRandom(StandardQuestion sq,String randomId,String randomValues)
	{
		boolean testing=false;
		try
		{
			QComponent qc=sq.getComponent(randomId);
			if (qc instanceof RandomComponent)
			{
				String randomValue=((RandomComponent)qc).getValue();
				String[] orRandomValues=randomValues.split(TEST_SELECTOR_OR_SYMBOL);
				for (String orRandomValue:orRandomValues)
				{
					if (randomValue.equals(orRandomValue))
					{
						testing=true;
						break;
					}
				}
			}
		}
		catch (OmDeveloperException e)
		{
			testing=false;
		}
		return testing;
	}
	
	/**
	 * Tests if this component match test property conditions
	 * @param sq Standard question
	 * @param qc Testable component
	 * @return true if this component match test property conditions, false otherwise 
	 */
	public static boolean testPropertyTest(StandardQuestion sq,Testable qc)
	{
		boolean testing=true;
		List<String> idAnswers=sq.getIdAnswers();
		if (qc!=null)
		{
			String test=qc.getTest();
			if (test==null)
			{
				testing=false;
			}
			else
			{
				String onlyTest=getOnlyTest(test);
				if (onlyTest.equals(TEST_TRUE))
				{
					testing=true;
				}
				else if (onlyTest.equals(TEST_RIGHT))
				{
					String selector=getTestSelector(test);
					if (selector==null)
					{
						testing=sq.isRight() && idAnswers!=null;
					}
					else
					{
						testing=isRight(sq,selector);
					}
				}
				else if (onlyTest.equals(TEST_NOTRIGHT))
				{
					String selector=getTestSelector(test);
					if (selector==null)
					{
						testing=!sq.isRight() || idAnswers==null;
					}
					else
					{
						testing=!isRight(sq,selector);
					}
				}
				else if (onlyTest.equals(TEST_WRONG))
				{
					String selector=getTestSelector(test);
					if (selector==null)
					{
						testing=!sq.isRight() && idAnswers!=null;
					}
					else
					{
						testing=isWrong(sq,selector);
					}
				}
				else if (onlyTest.equals(TEST_NOTWRONG))
				{
					String selector=getTestSelector(test);
					if (selector==null)
					{
						testing=sq.isRight() || idAnswers==null;
					}
					else
					{
						testing=!isWrong(sq,selector);
					}
				}
				else if (onlyTest.equals(TEST_PASSED))
				{
					testing=idAnswers==null;
				}
				else if (onlyTest.equals(TEST_NOTPASSED))
				{
					testing=idAnswers!=null;
				}
				else if (onlyTest.equals(TEST_RANDOM))
				{
					String selector=getTestSelector(test);
					testing=false;
					if (selector!=null)
					{
						String randomValues=getTestExpressionId(selector);
						if (randomValues!=null)
						{
							String randomId=getTestComponentId(selector);
							testing=testRandom(sq,randomId,randomValues);
						}
					}
				}
				else if (onlyTest.equals(TEST_NUMCMP))
				{
					String selector=getTestSelector(test);
					testing=false;
					if (selector!=null)
					{
						try
						{
							String numericComp=AnswerChecking.stripWhitespace(selector);
							String tolerance=qc.getTolerance();
							if (tolerance==null)
							{
								testing=NumericTester.expressionComparison(numericComp,sq);
							}
							else
							{
								double toleranceNum=NumericTester.inputNumber(tolerance);
								if (toleranceNum!=Double.NaN)
								{
									testing=NumericTester.expressionComparison(
											numericComp,toleranceNum,sq);
								}
							}
						}
						catch (OmDeveloperException e)
						{
							testing=false;
						}
					}
				}
			}
		}
		return testing;
	}
	
	/**
	 * Test if this component match attempt conditions
	 * @param sq Standard question
	 * @param qc Testable component
	 * @return true if this component match attempt conditions, false otherwise
	 */
	public static boolean testAttempt(StandardQuestion sq,Testable qc)
	{
		boolean testing=false;
		if (qc!=null)
		{
			testing=sq.getAttempt()>=qc.getAttemptsMin() && sq.getAttempt()<=qc.getAttemptsMax();
		}
		return testing;
	}
	
	/**
	 * Tests if this component match all conditions.
	 * @param sq Standard question
	 * @param qc Testable component
	 * @param idAnswer Identifier of component selected for answer
	 * @return true if this component match all conditions, false otherwise
	 */
	public static boolean testAll(StandardQuestion sq,Testable qc,String idAnswer)
	{
		return testAttempt(sq,qc) && testPropertyTest(sq,qc) && testCounters(sq,qc) && 
				testAnswer(sq,qc,idAnswer); 
	}
	
	/**
	 * Tests if this component match all conditions.
	 * @param sq Standard question
	 * @param qc Testable component
	 * @return true if this component match all conditions, false otherwise
	 */
	public static boolean testAll(StandardQuestion sq,Testable qc)
	{
		return testAttempt(sq,qc) && testPropertyTest(sq,qc) && testCounters(sq,qc) && 
				testAnswer(sq,qc,sq.getIdAnswers()); 
	}
}
