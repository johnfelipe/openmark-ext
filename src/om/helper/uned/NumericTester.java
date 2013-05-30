package om.helper.uned;

import java.util.Map;

import om.OmDeveloperException;
import om.OmException;
import om.OmUnexpectedException;
import om.helper.AnswerChecking;
import om.stdcomponent.uned.Answerable;
import om.stdcomponent.uned.RandomComponent;
import om.stdcomponent.uned.ReplaceholderComponent;
import om.stdcomponent.uned.VariableComponent;
import om.stdquestion.QComponent;
import om.stdquestion.uned.StandardQuestion;

//UNED: 16-08-2011 - dballestin
/**
 * This is a class to do testing with numbers.<br/>
 */
public class NumericTester
{
	/** 
	 * Character used in numeric equations as plus operator and also as positive sign in numbers 
	 * and exponents
	 */
	private final static char PLUS_SYMBOL='+';
	
	/** 
	 * Character used in numeric equations as minus operator and also as negative sign in numbers 
	 * and exponents
	 */
	private final static char MINUS_SYMBOL='-';
	
	/** Character used in numeric equations as multiplication operator */
	private final static char MUL_SYMBOL='*';
	
	/** Character used in numeric equations as division operator */
	private final static char DIV_SYMBOL='/';
	
	/** Character used in numaric equations as power operator */
	private final static char POWER_SYMBOL='^';
	
	/** Character used to open a group of operations inside a numeric equation */
	private final static char LPAREN_SYMBOL='(';
	
	/** Character used to close a group of operations inside a numeric equation */
	private final static char RPAREN_SYMBOL=')';
	
	/** Symbol used as equality operator with numeric equations */
	private final static String EQUAL_SYMBOL="==";
	
	/** Symbol used as inequality operator with numeric equations */
	private final static String NOTEQUAL_SYMBOL="!=";
	
	/** Symbol used as less comparison operator with numeric equations */
	private final static String LESS_SYMBOL="<";
	
	/** Symbol used as less or equal comparison operator with numeric equations */
	private final static String LESSEQUAL_SYMBOL="<=";
	
	/** Symbol used as greater comparison operator with numeric equations */
	private final static String GREATER_SYMBOL=">";
	
	/** Symbol used as greater or equal comparison operator with numeric equations */
	private final static String GREATEREQUAL_SYMBOL=">=";
	
	/** Starting character of equality operator */
	private final static char PRE_CMP_OP_EQUAL='=';
	
	/** Starting character of inequality operator */
	private final static char PRE_CMP_OP_NOTEQUAL='!';
	
	/** Starting character of less comparison operators */
	private final static char PRE_CMP_OP_LESS_EQUAL='<';
	
	/** Starting character of greater comparison operators */
	private final static char PRE_CMP_OP_GREATER_EQUAL='>';
	
	/** Character representing first decimal digit: 0 */
	private final static char DIGIT_0='0';
	
	/** Character representing last decimal digit: 9 */
	private final static char DIGIT_9='9';
	
	/** Character representing first letter */
	private final static char LETTER_A='A';
	
	/** Character representing last letter */
	private final static char LETTER_Z='Z';
	
	/** Character used to separate integer and decimal part of a number */
	private final static char DOT='.';
	
	/** Character used to declare a exponent (lower case) */
	private final static char EXPONENT_L='e';
	
	/** Character used to declare a exponent (upper case) */
	private final static char EXPONENT_U='E';
	
	/** Character used to represent then end of parsed string */
	private final static char EOF_CHAR='\0';
	
	/** Multiplication centered dot */
	public final static char MUL_DOT='\u00d7';
	
	/** Internal index used during numeric equations parsing */
	private static int equationIndex=-1;
	
	/**
	 * Utility class to parse a string with numeric testing options
	 */
	public static class NumericOptions
	{
		public final static int LESS_PRECISION=-1;
		public final static int LESS_EQUAL_PRECISION=-2;
		public final static int EQUAL_PRECISION=0;
		public final static int GREATER_PRECISION=1;
		public final static int GREATER_EQUAL_PRECISION=2;
		
		private boolean foundCheckNumber;
		
		public boolean scientificTransform;
		public boolean usePrecision;
		public int significantDigits;
		
		public boolean numberCheck;
		public boolean scientificCheck;
		public boolean absoluteCheck;
		public boolean significantDigitsCheck;
		public boolean exponentCheck;
		public boolean precissionCheck;
		public int precissionCheckOp;
		public int precissionCheckSignificantDigits;
		
		/**
		 * @param options String with numeric testing options
		 */
		public NumericOptions(String options)
		{
			// default values
			foundCheckNumber=false;
			
			scientificTransform=false;
			usePrecision=false;
			significantDigits=-1;
			
			numberCheck=true;
			scientificCheck=false;
			absoluteCheck=false;
			significantDigitsCheck=false;
			exponentCheck=false;
			precissionCheck=false;
			precissionCheckOp=EQUAL_PRECISION;
			precissionCheckSignificantDigits=-1;
			
			// parse string with numeric testing options
			if (options!=null)
			{
				int i=0;
				while (i<options.length())
				{
					char c=options.charAt(i);
					if (c=='c')
					{
						if (i+1<options.length())
						{
							c=options.charAt(i+1);
							if (c=='n')
							{
								i++;
								foundCheckNumber=true;
								numberCheck=true;
							}
							else if (c=='s')
							{
								i++;
								scientificCheck=true;
								numberCheck=foundCheckNumber;
							}
							else if (c=='a')
							{
								i++;
								absoluteCheck=true;
								numberCheck=foundCheckNumber;
							}
							else if (c=='d')
							{
								i++;
								significantDigitsCheck=true;
								numberCheck=foundCheckNumber;
							}
							else if (c=='e')
							{
								i++;
								exponentCheck=true;
								numberCheck=foundCheckNumber;
							}
							else if (c=='p' || c=='l' || c=='g')
							{
								boolean readN=false;
								if (c=='p')
								{
									i++;
									precissionCheck=true;
									precissionCheckOp=EQUAL_PRECISION;
									precissionCheckSignificantDigits=-1;
									numberCheck=foundCheckNumber;
									readN=true;
								}
								else if (c=='l')
								{
									if (i+2<options.length())
									{
										c=options.charAt(i+2);
										if (c=='p')
										{
											i+=2;
											precissionCheck=true;
											precissionCheckOp=LESS_PRECISION;
											precissionCheckSignificantDigits=-1;
											numberCheck=foundCheckNumber;
											readN=true;
										}
										else if (c=='e')
										{
											if (i+3<options.length())
											{
												c=options.charAt(i+3);
												if (c=='p')
												{
													i+=3;
													precissionCheck=true;
													precissionCheckOp=LESS_EQUAL_PRECISION;
													precissionCheckSignificantDigits=-1;
													numberCheck=foundCheckNumber;
													readN=true;
												}
											}
										}
									}
								}
								else if (c=='g')
								{
									if (i+2<options.length())
									{
										c=options.charAt(i+2);
										if (c=='p')
										{
											i+=2;
											precissionCheck=true;
											precissionCheckOp=GREATER_PRECISION;
											precissionCheckSignificantDigits=-1;
											numberCheck=foundCheckNumber;
											readN=true;
										}
										else if (c=='e')
										{
											if (i+3<options.length())
											{
												c=options.charAt(i+3);
												if (c=='p')
												{
													i+=3;
													precissionCheck=true;
													precissionCheckOp=GREATER_EQUAL_PRECISION;
													precissionCheckSignificantDigits=-1;
													numberCheck=foundCheckNumber;
													readN=true;
												}
											}
										}
									}
								}
								if (readN)
								{
									if (i+1<options.length())
									{
										c=options.charAt(i+1);
										if (c>=DIGIT_0 && c<=DIGIT_9)
										{
											StringBuffer n=new StringBuffer();
											i++;
											while (c>=DIGIT_0 && c<=DIGIT_9)
											{
												n.append(c);
												if (i+1<options.length())
												{
													i++;
													c=options.charAt(i);
												}
												else
												{
													c=EOF_CHAR;
												}
											}
											i--;
											precissionCheckSignificantDigits=
													Integer.parseInt(n.toString());
										}
									}
								}
							}
						}
					}
					else if (c=='s')
					{
						scientificTransform=true;
					}
					else if (c=='p')
					{
						usePrecision=true;
						significantDigits=-1;
						if (i+1<options.length())
						{
							c=options.charAt(i+1);
							if (c>=DIGIT_0 && c<=DIGIT_9)
							{
								StringBuffer n=new StringBuffer();
								i++;
								while (c>=DIGIT_0 && c<=DIGIT_9)
								{
									n.append(c);
									if (i+1<options.length())
									{
										i++;
										c=options.charAt(i);
									}
									else
									{
										c=EOF_CHAR;
									}
								}
								i--;
								significantDigits=Integer.parseInt(n.toString());
							}
						}
					}
					i++;
				}
			}
		}
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.<br/><br/>
	 * Uses 0.0 as error tolerance in comparison.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input) throws OmDeveloperException
	{
		return expressionComparison(input,0.0,null,null);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.<br/><br/>
	 * Uses 0.0 as error tolerance in comparison.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param sq Standard question
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,StandardQuestion sq) throws OmDeveloperException
	{
		return expressionComparison(input,0.0,null,sq);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.<br/><br/>
	 * Uses 0.0 as error tolerance in comparison.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param variables Map with variables that can be used inside expressions
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,Map<String,Double> variables) 
			throws OmDeveloperException
	{
		return expressionComparison(input,0.0,variables,null);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.<br/><br/>
	 * Uses 0.0 as error tolerance in comparison.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param variables Map with variables that can be used inside expressions
	 * @param sq Standard question
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		return expressionComparison(input,0.0,variables,sq);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param tolerance Error tolerance used in comparison
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,double tolerance) throws OmDeveloperException
	{
		return expressionComparison(input,tolerance,null,null);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param tolerance Error tolerance used in comparison
	 * @param sq Standard question
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,double tolerance,StandardQuestion sq) 
			throws OmDeveloperException
	{
		return expressionComparison(input,tolerance,null,sq);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param tolerance Error tolerance used in comparison
	 * @param variables Map with variables that can be used inside expressions
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,double tolerance,Map<String,Double> variables) 
			throws OmDeveloperException
	{
		return expressionComparison(input,tolerance,variables,null);
	}
	
	/**
	 * Parse a string with two expressions separated by a comparison operator and returns a boolean
	 * as comparison result.
	 * @param input Input with the two expressions separated by a comparison operator
	 * @param tolerance Error tolerance used in comparison
	 * @param variables Map with variables that can be used inside expressions
	 * @param sq Standard question
	 * @return Comparison result after parsing expressions
	 * @throws OmDevoleperException Error parsing expressions
	 */
	public static boolean expressionComparison(String input,double tolerance,Map<String,Double> variables,
			StandardQuestion sq) throws OmDeveloperException
	{
		boolean testing=false;
		equationIndex=0;
		double eq1=expression(input,variables,sq);
		String cmpOp=comparisonOperator(input);
		double eq2=expression(input,variables,sq);
		
		if (equationIndex<input.length())
		{
			equationIndex=-1;
			throw new OmUnexpectedException("Expression comparison format error: unexpected characters at end");
		}
		else if (!Double.isNaN(eq1) && !Double.isNaN(eq2))
		{
			if (cmpOp.equals(EQUAL_SYMBOL))
			{
				if (eq1>eq2)
				{
					testing=eq1-eq2<=tolerance;
				}
				else
				{
					testing=eq2-eq1<=tolerance;
				}
			}
			else if (cmpOp.equals(NOTEQUAL_SYMBOL))
			{
				if (eq1>eq2)
				{
					testing=eq1-eq2>tolerance;
				}
				else
				{
					testing=eq2-eq1>tolerance;
				}
			}
			else if (cmpOp.equals(LESS_SYMBOL))
			{
				testing=eq1<eq2+tolerance;
			}
			else if (cmpOp.equals(LESSEQUAL_SYMBOL))
			{
				testing=eq1<=eq2+tolerance;
			}
			else if (cmpOp.equals(GREATER_SYMBOL))
			{
				testing=eq1>eq2-tolerance;
			}
			else if (cmpOp.equals(GREATEREQUAL_SYMBOL))
			{
				testing=eq1>=eq2-tolerance;
			}
		}
		equationIndex=-1;
		return testing;
	}
	
	/**
	 * Parse a string with an expression and returns its numeric value as a double.
	 * @param input Input with the expression
	 * @return Numeric value of expression as a double
	 * @throws OmDevoleperException Error parsing expression
	 */
	public static double expression(String input) throws OmDeveloperException
	{
		return expression(input,null,null);
	}
	
	/**
	 * Parse a string with an expression and returns its numeric value as a double.
	 * @param input Input with the expression
	 * @param sq Standard question
	 * @return Numeric value of expression as a double
	 * @throws OmDevoleperException Error parsing expression
	 */
	public static double expression(String input,StandardQuestion sq) throws OmDeveloperException
	{
		return expression(input,null,sq);
	}
	
	/**
	 * Parse a string with an expression and returns its numeric value as a double.
	 * @param input Input with the expression
	 * @param variables Map with variables that can be used inside the expression
	 * @return Numeric value of expression as a double
	 * @throws OmDevoleperException Error parsing expression
	 */
	public static double expression(String input,Map<String,Double> variables) throws OmDeveloperException
	{
		return expression(input,variables,null);
	}
	
	/**
	 * Parse a string with an expression and returns its numeric value as a double.
	 * @param input Input with the expression
	 * @param variables Map with variables that can be used inside the expression
	 * @param sq Standard question
	 * @return Numeric value of expression as a double
	 * @throws OmDevoleperException Error parsing expression
	 */
	public static double expression(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		boolean testAdditions=true; 
		boolean resetEquationIndexAtEnd=false;
		if (equationIndex==-1)
		{
			equationIndex=0;
			resetEquationIndexAtEnd=true;
		}
		double numEq=mulExpression(input,variables,sq);
		while (testAdditions)
		{
			if (equationIndex<input.length())
			{
				char c=input.charAt(equationIndex);
				if (c==PLUS_SYMBOL)
				{
					equationIndex++;
					double mulEq=mulExpression(input,variables,sq);
					if (Double.isNaN(numEq) || Double.isNaN(mulEq))
					{
						numEq=Double.NaN;
					}
					else
					{
						numEq+=mulEq;
					}
				}
				else if (c==MINUS_SYMBOL)
				{
					equationIndex++;
					double mulEq=mulExpression(input,variables,sq);
					if (Double.isNaN(numEq) || Double.isNaN(mulEq))
					{
						numEq=Double.NaN;
					}
					else
					{
						numEq-=mulEq;
					}
				}
				else
				{
					testAdditions=false;
				}
			}
			else
			{
				testAdditions=false;
			}
		}
		if (resetEquationIndexAtEnd)
		{
			equationIndex=-1;
		}
		return numEq;
	}
	
	/**
	 * Parse a string with an expression (without + and - operators, except inside parenthesis) 
	 * and returns its numeric value as a double.
	 * @param input Input with an expression (without + and - operators, except inside parenthesis)
	 * @param variables Map with variables that can be used inside the expression
	 * @param sq Standard question
	 * @return Numeric value of an expression (without + and - operators, except inside parenthesis) 
	 * as a double
	 * @throws OmDevoleperException Error parsing expression (without + and - operators, 
	 * except inside parenthesis)
	 */
	private static double mulExpression(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		boolean testFactors=true;
		double mulEq=powerExpression(input,variables,sq);
		while (testFactors)
		{
			if (equationIndex<input.length())
			{
				char c=input.charAt(equationIndex);
				if (c==MUL_SYMBOL)
				{
					equationIndex++;
					double unAt=powerExpression(input,variables,sq);
					if (Double.isNaN(mulEq) || Double.isNaN(unAt))
					{
						mulEq=Double.NaN;
					}
					else
					{
						mulEq*=unAt;
					}
				}
				else if (c==DIV_SYMBOL)
				{
					equationIndex++;
					double unAt=powerExpression(input,variables,sq);
					if (Double.isNaN(mulEq) || Double.isNaN(unAt))
					{
						mulEq=Double.NaN;
					}
					else
					{
						mulEq/=unAt;
					}
				}
				else
				{
					testFactors=false;
				}
			}
			else
			{
				testFactors=false;
			}
		}
		return mulEq;
	}
	
	
	/**
	 * Parse a string with an expression (without +, -, * and / operators, except inside parenthesis) 
	 * and returns its numeric value as a double.
	 * @param input Input with an expression (without +, -, * and / operators, except inside parenthesis)
	 * @param variables Map with variables that can be used inside the expression
	 * @param sq Standard question
	 * @return Numberic value of expression (without +, -, * and / operators, except inside parenthesis) 
	 * as a double
	 * @throws OmDevoleperException Error parsing expression (without +, -, * and / operators, 
	 * except inside parenthesis)
	 */
	private static double powerExpression(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		double powerEq=unaryAtom(input,variables,sq);
		if (equationIndex<input.length())
		{
			char c=input.charAt(equationIndex);
			if (c==POWER_SYMBOL)
			{
				equationIndex++;
				double exponentEq=powerExpression(input,variables,sq);
				if (Double.isNaN(powerEq) || Double.isNaN(exponentEq))
				{
					powerEq=Double.NaN;
				}
				else
				{
					powerEq=StrictMath.pow(powerEq,exponentEq);
				}
			}
		}
		return powerEq;
	}
	
	/**
	 * Parse a string with a signed numeric atom (variable, number or parenthesized expression) and 
	 * returns its numeric value as a double.
	 * @param input Imput with a signed numeric atom (variable, number or parenthesized expression)
	 * @param variables Map with variables that can be used inside the expression
	 * @param sq Standard question
	 * @return Numeric value of signed numeric atom (variable, number or parenthesized expression) as a double
	 * @throws OmDevoleperException Error parsing signed numeric atom (variable, number or parenthesized
	 * expression)
	 */
	private static double unaryAtom(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		boolean isNegativeAtom=false;
		if (equationIndex<input.length())
		{
			char c=input.charAt(equationIndex);
			if (c==PLUS_SYMBOL)
			{
				equationIndex++;
			}
			else if (c==MINUS_SYMBOL)
			{
				equationIndex++;
				isNegativeAtom=true;
			}
		}
		double unAt=atom(input,variables,sq);
		if (isNegativeAtom && !Double.isNaN(unAt))
		{
			unAt=-unAt;
		}
		return unAt;
	}
	
	/**
	 * Parse a string with a numeric atom (variable, number or parenthesized expression) and returns its 
	 * numeric value as a double.
	 * @param input Input with a numeric atom (variable, number or parenthesized expression)
	 * @param variables Map with variables that can be used inside the expression
	 * @param sq Standard question
	 * @return Numeric value of numeric atom (variable, number or parenthesized expression) as a double
	 * @throws OmDevoleperException Error parsing numeric atom (variable, number or parenthesized expression)
	 */
	private static double atom(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		double at=0.0;
		if (equationIndex<input.length())
		{
			char c=input.charAt(equationIndex);
			char cUpper=Character.toUpperCase(c);
			if ((cUpper>=LETTER_A && cUpper<=LETTER_Z) || c=='_')
			{
				at=variable(input,variables,sq);
			}
			else if ((c>=DIGIT_0 && c<=DIGIT_9) || c==DOT)
			{
				at=floatLiteral(input);
			}
			else if (c==LPAREN_SYMBOL)
			{
				equationIndex++;
				at=expression(input,variables);
				if (equationIndex<input.length())
				{
					c=input.charAt(equationIndex);
					if (c==RPAREN_SYMBOL)
					{
						equationIndex++;
					}
					else
					{
						equationIndex=-1;
						throw new OmUnexpectedException("Expression format error: expected ) not found");
					}
				}
				else
				{
					equationIndex=-1;
					throw new OmUnexpectedException("Expression format error: expected ) not found");
				}
			}
			else
			{
				equationIndex=-1;
				throw new OmUnexpectedException("Expression format error: expected atom not found");
			}
		}
		else
		{
			equationIndex=-1;
			throw new OmUnexpectedException("Expression format error: expected atom not found");
		}
		return at;
	}
	
	/**
	 * Parse a string with a variable and returns its numeric value as a double.
	 * @param input Input with a variable
	 * @param variables Map with variables
	 * @param sq Standard question
	 * @return Numeric value of numeric atom (variable, number or parenthesized expression) as a double
	 * @throws OmDevoleperException Error parsing numeric atom (variable, number or parenthesized expression)
	 */
	private static double variable(String input,Map<String,Double> variables,StandardQuestion sq) 
			throws OmDeveloperException
	{
		double v=0.0;
		StringBuffer variableString=new StringBuffer();
		char c=input.charAt(equationIndex);
		char cUpper=Character.toUpperCase(c);
		while ((cUpper>=LETTER_A && cUpper<=LETTER_Z) || c=='_' || (c>=DIGIT_0 && c<=DIGIT_9))
		{
			variableString.append(c);
			equationIndex++;
			if (equationIndex<input.length())
			{
				c=input.charAt(equationIndex);
				cUpper=Character.toUpperCase(c);
			}
			else
			{
				c=EOF_CHAR;
				cUpper=EOF_CHAR;
			}
		}
		if (variables!=null && variables.containsKey(variableString.toString()))
		{
			v=variables.get(variableString.toString());
		}
		else if (sq!=null)
		{
			QComponent qc=null;
			try
			{
				qc=sq.getComponent(variableString.toString());
				if (qc instanceof RandomComponent)
				{
					v=Double.parseDouble(((RandomComponent)qc).getValue());
				}
				else if (qc instanceof ReplaceholderComponent)
				{
					v=Double.parseDouble(((ReplaceholderComponent)qc).getReplacementValue());
				}
				else if (qc instanceof VariableComponent)
				{
					v=Double.parseDouble(((VariableComponent)qc).getValue());
				}
				else if (qc instanceof Answerable)
				{
					v=Double.parseDouble(((Answerable)qc).getAnswerLine());
				}
				else
				{
					throw new OmUnexpectedException("Expression format error: variable's name not recognized");
				}
			}
			catch (NumberFormatException nfe)
			{
				v=Double.NaN;
			}
			catch (OmException oe)
			{
				equationIndex=-1;
				throw new OmUnexpectedException("Expression format error: variable's name not recognized");
			}
		}
		else
		{
			equationIndex=-1;
			throw new OmUnexpectedException("Expression format error: variable's name not recognized");
		}
		return v;
	}
	
	/**
	 * Parse a string with a decimal number (with optional exponent) and returns its numeric value as 
	 * a double.
	 * @param input Input with a decimal number (with optional exponent)
	 * @return Numeric value of decimal number (with optional exponent) as a double
	 * @throws OmDevoleperException Error parsing decimal number (with optional exponent)
	 */
	private static double floatLiteral(String input) throws OmDeveloperException
	{
		StringBuffer floatString=new StringBuffer();
		char c=input.charAt(equationIndex);
		if (c>=DIGIT_0 && c<=DIGIT_9)
		{
			while (c>=DIGIT_0 && c<=DIGIT_9)
			{
				floatString.append(c);
				equationIndex++;
				if (equationIndex<input.length())
				{
					c=input.charAt(equationIndex);
				}
				else
				{
					c=EOF_CHAR;
				}
			}
			if (c==DOT)
			{
				floatString.append(DOT);
				equationIndex++;
				if (equationIndex<input.length())
				{
					c=input.charAt(equationIndex);
				}
				else
				{
					c=EOF_CHAR;
				}
				while (c>=DIGIT_0 && c<=DIGIT_9)
				{
					floatString.append(c);
					equationIndex++;
					if (equationIndex<input.length())
					{
						c=input.charAt(equationIndex);
					}
					else
					{
						c=EOF_CHAR;
					}
				}
				if (c==EXPONENT_L || c==EXPONENT_U)
				{
					floatString.append(exponent(input));
				}
			}
			else if (c==EXPONENT_L || c==EXPONENT_U)
			{
				floatString.append(exponent(input));
			}
		}
		else if (c==DOT)
		{
			floatString.append(DOT);
			equationIndex++;
			if (equationIndex<input.length())
			{
				c=input.charAt(equationIndex);
			}
			else
			{
				c=EOF_CHAR;
			}
			while (c>=DIGIT_0 && c<=DIGIT_9)
			{
				floatString.append(c);
				equationIndex++;
				if (equationIndex<input.length())
				{
					c=input.charAt(equationIndex);
				}
				else
				{
					c=EOF_CHAR;
				}
			}
			if (c==EXPONENT_L || c==EXPONENT_U)
			{
				floatString.append(exponent(input));
			}
		}
		return parseNumber(floatString.toString()); 
	}
	
	/**
	 * Parse a string with the exponent part of a decimal number and returns its string representation.
	 * @param input Input with exponent part of a decimal number
	 * @return String representation of exponent part of a decimal number
	 * @throws OmDevoleperException Error parsing exponent part of a decimal number
	 */
	private static String exponent(String input) throws OmDeveloperException
	{
		StringBuffer exp=new StringBuffer();
		char c=input.charAt(equationIndex);
		if (c==EXPONENT_L || c==EXPONENT_U)
		{
			exp.append(c);
			equationIndex++;
			if (equationIndex<input.length())
			{
				c=input.charAt(equationIndex);
			}
			else
			{
				c=EOF_CHAR;
			}
		}
		else
		{
			equationIndex=-1;
			throw new OmUnexpectedException("Expression format error: mismatched input expecting a number");
		}
		if (c==PLUS_SYMBOL || c==MINUS_SYMBOL)
		{
			exp.append(c);
			equationIndex++;
			if (equationIndex<input.length())
			{
				c=input.charAt(equationIndex);
			}
			else
			{
				c=EOF_CHAR;
			}
		}
		if (c>=DIGIT_0 && c<=DIGIT_9)
		{
			while (c>=DIGIT_0 && c<=DIGIT_9)
			{
				exp.append(c);
				equationIndex++;
				if (equationIndex<input.length())
				{
					c=input.charAt(equationIndex);
				}
				else
				{
					c=EOF_CHAR;
				}
			}
		}
		else
		{
			equationIndex=-1;
			throw new OmUnexpectedException("Expression format error: mismatched input expecting a number");
		}
		return exp.toString();
	}
	
	/**
	 * Parse a string with a comparison operator and returns it
	 * @param input Input with a comparison operator
	 * @return Comparison operator
	 * @throws OmDevoleperException Error parsing comparison operator
	 */
	private static String comparisonOperator(String input) throws OmDeveloperException
	{
		String cmpOp=null;
		if (equationIndex<input.length())
		{
			char c=input.charAt(equationIndex);
			switch (c)
			{
				case PRE_CMP_OP_EQUAL:
					if (equationIndex+1<input.length())
					{
						cmpOp=input.substring(equationIndex,equationIndex+2);
						if (cmpOp.equals(EQUAL_SYMBOL))
						{
							equationIndex+=2;
						}
						else
						{
							cmpOp=null;
						}
					}
					break;
				case PRE_CMP_OP_NOTEQUAL:
					if (equationIndex+1<input.length())
					{
						cmpOp=input.substring(equationIndex,equationIndex+2);
						if (cmpOp.equals(NOTEQUAL_SYMBOL))
						{
							equationIndex+=2;
						}
						else
						{
							cmpOp=null;
						}
					}
					break;
				case PRE_CMP_OP_LESS_EQUAL:
					if (equationIndex+1<input.length())
					{
						cmpOp=input.substring(equationIndex,equationIndex+2);
						if (cmpOp.equals(LESSEQUAL_SYMBOL))
						{
							equationIndex+=2;
						}
						else
						{
							cmpOp=LESS_SYMBOL;
							equationIndex++;
						}
					}
					else
					{
						cmpOp=LESS_SYMBOL;
						equationIndex++;
					}
					break;
				case PRE_CMP_OP_GREATER_EQUAL:
					if (equationIndex+1<input.length())
					{
						cmpOp=input.substring(equationIndex,equationIndex+2);
						if (cmpOp.equals(GREATEREQUAL_SYMBOL))
						{
							equationIndex+=2;
						}
						else
						{
							cmpOp=GREATER_SYMBOL;
							equationIndex++;
						}
					}
			}
		}
		if (cmpOp==null)
		{
			equationIndex=-1;
			throw new OmUnexpectedException("Expression comparison format error: expected comparison operator not found");
		}
		return cmpOp;
	}
	
	/**
	 * Get the significant figures of a decimal number ignoring decimal point.<br/><br/>
	 * Note that if the number is negative the - unary operator will be included in result.
     * @param number Decimal number as a string
	 * @return Significant figures of a decimal number ignoring decimal point
	 */
	public static String getSignificantFigures(String number)
	{
		StringBuffer sigFigs=new StringBuffer();
		if (number!=null)
		{
			boolean isNegative=false;
			int i=0;
			char c=number.charAt(i);
			
			// Test if the number is negative
			if (c==MINUS_SYMBOL)
			{
				isNegative=false;
			}
			
			// Ignore initial zeros
			while (c==DIGIT_0)
			{
				i++;
				if (i<number.length())
				{
					c=number.charAt(i);
				}
				else
				{
					c=EOF_CHAR;
				}
			}
			
			// Get significant figures
			int numZeros=0;
			while (c!=EOF_CHAR && c!=EXPONENT_L && c!=EXPONENT_U)
			{
				if (c>DIGIT_0 && c<=DIGIT_9)
				{
					if (isNegative && sigFigs.length()==0)
					{
						sigFigs.append(MINUS_SYMBOL);
					}
					for (int j=0;j<numZeros;j++)
					{
						sigFigs.append(DIGIT_0);
					}
					sigFigs.append(c);
					numZeros=0;
				}
				else if (c==DIGIT_0)
				{
					numZeros++;
				}
				i++;
				if (i<number.length())
				{
					c=number.charAt(i);
				}
				else
				{
					c=EOF_CHAR;
				}
			}
		}
		return sigFigs.toString();
	}
	
    /**
     * Returns the number of significant figures of a number
     * @param string Decimal number as a string
     * @return Number of significant figures of a decimal number
     */
    public static int countNumberOfSignificantFiguresInString(String string)
    {
    	String sigFigs=getSignificantFigures(string);
    	int countSigFigs=sigFigs.length();
    	if (countSigFigs>0 && sigFigs.charAt(0)==MINUS_SYMBOL)
    	{
    		countSigFigs--;
    	}
        return countSigFigs;
    }
	
    /**
     * Returns the exponent of a decimal number taking care of zeroes
     * @param number Decimal number as a string
     * @return Exponent of a decimal number taking care of zeroes
     */
    public static int getExponent(String number)
    {
    	int exponent=0;
    	if (number!=null)
    	{
    		int iEndSigFigs=number.indexOf(EXPONENT_L);
    		if (iEndSigFigs==-1)
    		{
    			iEndSigFigs=number.indexOf(EXPONENT_U);
    		}
    		if (iEndSigFigs==-1)
    		{
    			iEndSigFigs=number.length()-1;
    		}
    		else
    		{
    			int i=iEndSigFigs+1;
    			if (i<number.length())
    			{
    				char c=number.charAt(i);
    				boolean isNegative=false;
    				StringBuffer eValue=new StringBuffer();
    				if (c==PLUS_SYMBOL)
    				{
    					i++;
    					if (i<number.length())
    					{
    						c=number.charAt(i);
    					}
    					else
    					{
    						c=EOF_CHAR;
    					}
    				}
    				else if (c==MINUS_SYMBOL)
    				{
    					isNegative=true;
    					i++;
    					if (i<number.length())
    					{
    						c=number.charAt(i);
    					}
    					else
    					{
    						c=EOF_CHAR;
    					}
    				}
    				while (c>=DIGIT_0 && c<=DIGIT_9)
    				{
    					eValue.append(c);
    					i++;
    					if (i<number.length())
    					{
    						c=number.charAt(i);
    					}
    					else
    					{
    						c=EOF_CHAR;
    					}
    				}
    				exponent=Integer.parseInt(eValue.toString());
    				if (isNegative)
    				{
    					exponent=-exponent;
    				}
    			}
    			iEndSigFigs--;
    		}
    		int i=iEndSigFigs;
    		int numZeros=0;
    		char c=EOF_CHAR;
    		if (i>=0 && i<number.length())
    		{
    			c=number.charAt(i);
    		}
    		while (c==DIGIT_0)
    		{
    			numZeros++;
    			i--;
    			if (i>=0)
    			{
    				c=number.charAt(i);
    			}
    			else
    			{
    				c=EOF_CHAR;
    			}
    		}
    		if (c==DOT)
    		{
    			numZeros=0;
    			i--;
    			if (i>=0)
    			{
    				c=number.charAt(i);
    			}
    			else
    			{
    				c=EOF_CHAR;
    			}
    			while (c==DIGIT_0)
    			{
        			numZeros++;
        			i--;
        			if (i>=0)
        			{
        				c=number.charAt(i);
        			}
        			else
        			{
        				c=EOF_CHAR;
        			}
    			}
    		}
    		if (c!=EOF_CHAR)
    		{
    			if (numZeros==0)
    			{
    				i=0;
    				if (i<number.length())
    				{
    					c=number.charAt(i);
    				}
    				else
    				{
    					c=EOF_CHAR;
    				}
    				while (c==DIGIT_0)
    				{
    					i++;
        				if (i<number.length())
        				{
        					c=number.charAt(i);
        				}
        				else
        				{
        					c=EOF_CHAR;
        				}
    				}
    				if (c==DOT)
    				{
    					numZeros=1;
    					i++;
        				if (i<number.length())
        				{
        					c=number.charAt(i);
        				}
        				else
        				{
        					c=EOF_CHAR;
        				}
    					while (c==DIGIT_0)
    					{
    						numZeros++;
            				if (i<number.length())
            				{
            					c=number.charAt(i);
            				}
            				else
            				{
            					c=EOF_CHAR;
            				}
    					}
    					exponent-=numZeros;
    				}
    			}
    			else
    			{
    				exponent+=numZeros;
    			}
    		}
    	}
    	return exponent;
    }
    
    /**
     * Parses a string to get a number
     * @param str The initial string
     * @return The number or Double.NaN if no number entered
     **/
    public static double parseNumber(String str)
    {
    	double ldbl;
    	try 
    	{
    		ldbl=Double.parseDouble(str);
    	}
    	catch (NumberFormatException nfe)
    	{
    		// If they typed something that wasn't a number, just treat as wrong answer
    		ldbl=Double.NaN;
    	}
    	return ldbl;
    }
	
    ///////////////////////////////////////////////////////////////////////////
	// Utility functions to work with numbers copied from samples.shared.Helper
    ///////////////////////////////////////////////////////////////////////////
	
    /**
     *  Extracts the first number from a sequence of characters<br/>
     *  Assumes scientificNotationToE() has been called first<br/><br/>
     *  Allows the returned string to be parsed as a double by Double.parseDouble( String string )
     *  @param str The string to be parsed
     *  @return If the string is abc1.23e-7ms<sup>-1<sup/> then 1.23e-7 is extracted.
     *  If no number is found an empty string is returned.
     **/
    public static String extractNumber(String str)
    {
        boolean	done;
        boolean	decimalFound=false;
        boolean	eFound=false;
        char ch;
        int	sptr;
        int	length;
        int startOfNumber=0,endOfNumber=0,exponentPosition;
        StringBuffer sb=new StringBuffer(64);
        
        length=str.length();
        
        if (length == 0)
        {
        	return(sb.toString());
        }

        // ok string contains characters; look for start of number

        // are there any non-numerics at start of string
    	sptr=-1;
    	done=false;
    	do
    	{
    		++sptr;
    	   	ch=str.charAt(sptr);
    		// check for number
    	   	if (Character.isDigit(ch))
    	   	{
    			done=true;
    	    	startOfNumber=sptr;
    			// check if preceeded by '.'
    			if (startOfNumber>0)
    			{
    				ch=str.charAt(startOfNumber-1);
    				if (ch=='.')
    				{
    					// move pointer into string back by one;
    					--startOfNumber;
    					decimalFound=true;
    				}
    			}
    			// check if preceeded by + or -
    			if (startOfNumber>0)
    			{
    				ch=str.charAt(startOfNumber-1);
    				if ((ch=='+') || (ch=='-'))
    				{
    					// move pointer into string back by one;
    					--startOfNumber;
    				}
    			}
    	   	}
    	}
    	while (!done && (sptr<(length-1)));

    	if (!done) // no numeric found; return empty string
    	{	
    	   	return sb.toString();
    	}

    	// is first character also last character
    	if (sptr==length-1)
    	{
    		endOfNumber=length;
    	}
    	else
    	{
    		done=false;
    		do
    		{
    			++sptr;
    			ch=str.charAt(sptr);
    			// check for not a number number
    			if (!Character.isDigit(ch))
    			{
    				done=true;
    				// end of number now found unless character is . or E or e
    				// is it the decimal point?
    				if (!decimalFound)
    				{
    					if (ch=='.')
    					{
    						decimalFound=true;
    						done=false;
    					}
    				}
    				if (!eFound)
    				{
    					if ((ch=='e') || (ch=='E'))
    					{
    						// for this to be an exponent we must have the sequence
    						// e<digit>, e+<digit> or e-<digit>
    						exponentPosition=sptr;
							++sptr;
							
							// UNED: 23-08-2011 - dballestin
							// Added condition to avoid an IndexOutOfBoundsException to be
							// thrown if letter e is the last character of the string
							if (sptr<length)
							{
								ch=str.charAt(sptr);
							}
							
    						if ((ch=='+') || (ch=='-'))
    						{
    							++sptr;
        						ch=str.charAt(sptr);
    						}
    						if (Character.isDigit(ch))
    						{
    							eFound=true;
    							done=false;
    						}
    						else
    						{
    							// set sptr back to pointing at letter e which is now seen
    							// to be just a letter
    							sptr=exponentPosition;
    						}
    					}
    				}
    			}
    		}
    		while (!done && (sptr<length-1));
    		
    		if (done)
    		{
    			endOfNumber=sptr;
    		}
    		else // last character in string is a digit; endOfNumber needs to point one beyond
    		{
    			endOfNumber=length;
    		}
    	}
    	
    	if (endOfNumber==length)
    	{
			sb.append(str.substring(startOfNumber));
    	}
		else
		{
			sb.append(str.substring(startOfNumber,endOfNumber));
		}
	   	return sb.toString();
    }
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Obtains a number from a string
     * @param str Initial string
     * @return Number or Double.NaN if no number entered
     **/

    public static double inputNumber(String str)
    {
    	return parseNumber(extractNumber(str));
    }
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     *  Obtains a number, which may be in scientific notation(x10<sup>exp</sup>, from a string
     *  @param str The initial string
     *  @return The number or Double.NaN if no number entered
     **/
    public static double inputScientificNumber(String str)
    {
    	double dbla;
    	String rsp;

		rsp=scientificNotationToE(str);
		dbla=inputNumber(rsp);

		return dbla;
    }
    ///////////////////////////////////////////////////////////////////////////
	
    /**
     * To be in scientific notation a number must be in the form
     * 1E2, 1e2, 1.00E2, 1.00e2 not forgeting minus numbers<br/><br/>
     * Numbers of the sort 10E1 or 10.0e1 are not scientific notation as is 100 etc<br/><br/>
     * Note that the number part of the input should already have been isolated
     * and scientific input of the type *10 etc converted to E notation before
     * being passed to this method.
     * @param numberStr The string to be checked.
     * @return True if a number in scientific notation is present
     **/
    public static boolean isScientificNotation(String numberStr)
    {
    	boolean scientific=false;

    	if (numberStr!=null && numberStr.length()>= 3)
    	{
    		int plusMinusAtStart=0;
    	    if (numberStr.charAt(0)=='+' || numberStr.charAt(0)=='-')
    	    {
    	    	plusMinusAtStart=1;
    	    }
    	    
    	    char current=' ';
    	    int eFoundPoint=0;
    	    boolean eFound=false;
    	    
    	    //first character or first character after any + or - symbols must be a number
    	    //between 1 and 9 inclusive
    	    current=numberStr.charAt(0+plusMinusAtStart);
    	    if (Character.isDigit(current) && current!='0')
    	    {
    	    	//next character after the first digit must be . e or E
    	        current=numberStr.charAt(1+plusMinusAtStart);
    	        if (current=='E' || current== 'e')
    	        {
    	        	eFound=true;
    	            eFoundPoint=2+plusMinusAtStart;
    	        }
    	        else if (current=='.' && numberStr.length()>=4)
    	        {
    	        	// if there is a stop, next character must be a digit or an e
    	            current=numberStr.charAt(2+plusMinusAtStart);
    	            if (Character.isDigit(current) || current=='E' || current=='e')
    	            {
    	                eFoundPoint=2+plusMinusAtStart;
    	                while (!eFound && eFoundPoint<numberStr.length())
    	                {
    	                	current=numberStr.charAt(eFoundPoint++);
    	                	if (current=='E' || current=='e')
    	                	{
    	                		eFound=true;
    	                	}
    	                }
    	            }
    	        }
    	        // if number is of the type 1e 1.e or 1.0E then check to see if there
    	        // is  + - or digit after
    	        if (eFound)
    	        {
    	        	current=numberStr.charAt(eFoundPoint);
    	            if ((eFoundPoint<numberStr.length()) && Character.isDigit(current))
    	            {
    	            	scientific=true;
    	            }
    	            else if (eFoundPoint<(numberStr.length()-1))
    	            {
    	            	if ((current=='+') || (current=='-'))
    	            	{
    	            		current=numberStr.charAt((eFoundPoint+1));
    	                    if (Character.isDigit(current))
    	                    {
    	                    	scientific=true;
    	                    }
    	                }
    	            }
    	        }
    	    }
    	}
    	return scientific;
    }
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Writes a number in scientific notation to a specified number of significant figures
     * @param dbl Number
     * @param sf Significant figures
     * @return Number as a string in scientific notation
     **/
    public static String outputScientificNumber(double dbl,int sf)
    {
    	boolean	positive=true;
    	int	i,length;
    	double ldbl;
    	String format,lstr;
    	
    	ldbl=dbl;
    	if (ldbl<0)
    	{
    		positive=false;
    		ldbl=-ldbl;
    	}
    	// create E format in form 0.#[#]E0"
    	format="0.";
    	for (i=1;i<sf;i++)
    	{
    		format=format+"#";
    	}
    	format=format+"E0";
    	
    	java.text.DecimalFormat df=new java.text.DecimalFormat(format);
    	
    	lstr=df.format(ldbl);
    	length=lstr.length();
    	i=lstr.indexOf("E");
    	
    	StringBuffer output=new StringBuffer();
    	if (!positive)
    	{
    		output.append('-');
    	}
    	output.append(lstr.substring(0,i));
    	output.append(' ');
    	output.append(MUL_DOT);
    	output.append(" 10^{");
    	output.append(lstr.substring(i+1,length));
    	output.append('}');
    	
    	return output.toString();
    }
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Checks if a number is in a given range
     * @param testAnswer Number to be checked
     * @param target Center of the range
     * @param tolerance Tolerance either side of the center
     * @return true if within range, false otherwise
     **/
    public static boolean range(double testAnswer, double target, double tolerance)
    {
    	return testAnswer>=target-tolerance && testAnswer<=target+tolerance;
    }
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks if a number is of the correct value apart from its magnitude.
     * Routine checks up and down by factors of 10^1 to 10^10 and 10^-1 to 10^-10
     * respectively.
     * @param testAnswer Number to be checked
     * @param target Center of the correct range
     * @param tolerance Tolerance either side of the center
     * @return true if within range modified by a factor of 10, false otherwise
     **/
    public static boolean rangeButWrongFactorOf10(double testAnswer, double target, double tolerance)
    {
    	int i;
    	double ltarget,ltolerance;
    	
    	// check powers of ten 10^10 up
    	for (i=1;i<=10;++i)
    	{
    		ltarget=target*Math.pow(10.0,(double)i);
    		ltolerance=tolerance*Math.pow(10.0,(double)i);
        	if (testAnswer>=ltarget-ltolerance && testAnswer<=ltarget+ltolerance)
        	{
           		return true;
           	}
    	}
    	// check powers of ten 10^10 down
    	for (i=1;i<=10;++i)
    	{
    		ltarget=target/Math.pow(10.0,(double)i);
    		ltolerance=tolerance/Math.pow(10.0,(double)i);
        	if (testAnswer>=ltarget-ltolerance && testAnswer<=ltarget+ltolerance)
        	{
           		return true;
           	}
    	}
    	
   		return false;
    }
    ///////////////////////////////////////////////////////////////////////////
	
    /**
     *  Converts advanced field superscript format into E notation.<br/><br/>
     *  If the input string contains "x10<sup>+-n</sup>" or "*10<sup>+-n</sup>"
     *  indicating a power of 10 followed by a superscript
     *  replaces with "E+-n"
     *  otherwise just return the original string.<br/><br/>
     *  Allows the returned string to be parsed as a double by Double.parseDouble( String string )
     *  @param str The string to be parsed
     *  @return Same string but with "x10<sup>+-n</sup>" occurrences replaced with "E+-n"
     **/
    public static String scientificNotationToE(String str)
    {
      	char ch;
        int	sptr,start;
        String lstr;
        StringBuffer sb=new StringBuffer(64);
        
        lstr=AnswerChecking.stripWhitespace(str);
        sptr=-1;
        start=-1;
        
    	while (start==-1 && sptr<lstr.length()-3)
    	{
        	++sptr;
        	StringBuffer mulDot10=new StringBuffer();
        	mulDot10.append(MUL_DOT);
        	mulDot10.append("10");
            if (lstr.substring(sptr,sptr+3).equalsIgnoreCase("x10") ||
            		lstr.substring(sptr,sptr+3).equalsIgnoreCase("*10") ||
            		lstr.substring(sptr,sptr+3).equalsIgnoreCase(mulDot10.toString()))
            {
            	// found x10
            	start=sptr;
            }
        }
        if (start==-1) // no x10 found
        {
        	return str;
        }
        
		// copy the string up to the x10
		sb.append(lstr.substring(0,start));
		
		// if end of line reached there is no exponent assume 1 meant
    	if (start+3==lstr.length())
    	{
    		sb.append("E1");
    		// and return
    		return sb.toString();
    	}

    	// if the next character is a number (as in x100) then this is not in
    	// scientific notation and no change is made
    	sptr=start+3;
    	ch=lstr.charAt(sptr);
    	if (Character.isDigit(ch))
    	{
    		return lstr;
    	}
    	
    	if (ch=='<')
    	{
    		// start to look for superscript
        	// does <sup>+-nn</sup> follow?
    		
    		if (lstr.length()>sptr+5)
    		{
    			if (lstr.substring(sptr,sptr+5).equalsIgnoreCase("<sup>"))
    			{
        			// add an 'E'
    				sb.append("E");
    				// add all chars up to </sup
    				sptr=sptr + 5;
    				do
    				{
    					sb.append(lstr.substring(sptr,sptr+1));
    					++sptr;
    				}
    				while (sptr!=lstr.length() && lstr.charAt(sptr)!='<');
    				if (lstr.length()>=sptr+6)
    				{
    					// check for </sup>
    					if (lstr.substring(sptr,sptr+6).equalsIgnoreCase("</sup>"))
    					{
    						// hooray - proper formatting
    						sptr=sptr+6;
    						// and copy rest of string
    						sb.append(lstr.substring(sptr));
    						return(sb.toString());
    					}
    				}
    				else
    				{
    					// a confused string; return original
    					return str;
    				}
    			}
    			else
    			{
    				// not normal x10<sup>n</sup> formatting; return original
    				return str;
    			}
      		}
    		else
    		{
				// not normal x10<sup>n</sup> formatting; return original
    			return str;
    		}
    	}

    	// who knows what follows; could be units
    	// replace "x10" by E1
    	sb.append("E1");
    	// and copy the rest
    	sb.append(lstr.substring(sptr));
    	// and return
    	return sb.toString();
    }
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns a number to the given number of significant figures
     * @param answer Answer to be queried
     * @param number Number of significant figures
     * @return Rounded number
     */
    public static double toSigFigs(final double answer,final int number)
    {
        double sigFigsValue=answer;
        
        boolean isNegative=false;
        boolean isZero=false;
        // First find the absolute value of the correct answer
        if (sigFigsValue<0.0)
        {
            sigFigsValue*=-1;
            isNegative=true;
        }
        else if (sigFigsValue==0.0)
        {
            isZero=true;
        }
        
        // use exponent to keep track of transformation
        int exponent=0;
        // Next we transform the correct answer to an integer (transform) in
        // the range 0 <= transform < 10 to the power of the number of significant figures
        double upperLimit=StrictMath.pow(10.0,number);
        // if our transform is greater than the range divide by ten until it is.
        // counting the divisions in exponent
        while (sigFigsValue>upperLimit && !isZero)
        {
            sigFigsValue/=10;
            exponent++;
        }
        // if correct answer is zero then do nothing otherwise multiply by ten
        // until it is greater than 10 to the number of significant figures - 1
        // counting the divions (as negative numbers) in the exponent variable
        double lowerLimit=StrictMath.pow(10.0,number-1);
        while (sigFigsValue<lowerLimit && !isZero)
        {
            sigFigsValue*=10;
            exponent--;
        }
        // now transform should be either 0 or in the range
        // lowerLimit < transform < upperLimit
        // exponent should hold the number of division required (negative divisions
        // are multiplications) to achieve this.
        
        sigFigsValue+=0.50000001;
        sigFigsValue=StrictMath.floor(sigFigsValue);
        //System.err.println("sigfigsvalue = "+sigFigsValue);
        //System.err.println("Exponent = "+exponent);
        sigFigsValue*=StrictMath.pow(10.0,exponent);
        if (isNegative)
        {
        	sigFigsValue*=-1;
        }
        return sigFigsValue;
    }
    ///////////////////////////////////////////////////////////////////////////
}
