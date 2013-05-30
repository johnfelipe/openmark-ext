package om.helper.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// UNED: 29-08-2011 - dballestin
/**
 * Static utility routines designed to help in checking user answers.
 */
public abstract class AnswerChecking extends om.helper.AnswerChecking
{
	/**
	 * Escape sequence character
	 */
	private final static char SEQUENCE_ESCAPE_CHARACTER='\\';
	
	/** 
	 * Character used to open a selector
	 */
	private static final char SELECTOR_OPEN='['; 
	
	/** 
	 * Character used to close a selector
	 */
	private static final char SELECTOR_CLOSE=']'; 
	
	/**
	 * Utility class used by text and summaryline components to override some properties from
	 * editfield or advancedfield components when testing them 
	 */
	public static class AnswerTestProperties
	{
		private String answertype;
		private String casesensitive;
		private String trim;
		private String strip;
		private String singlespaces;
		private String newlinespace;
		private String ignore;
		private String ignoreregexp;
		private String ignoreemptylines;
		private String tolerance;
		
		/**
		 * Creates an instance that don't override any properties from editfield or advancedfield 
		 * components
		 */
		public AnswerTestProperties() {
			this(null,null,null,null,null,null,null,null,null,null);
		}
		
		/**
		 * Creates an instance that override properties from editfield or advanvancedfield components
		 * with the values received.<br/><br/>
		 * Note that null or unexpected values don't override that properties
		 * @param answertype Answer's type or null to not override this property
		 * @param casesensitive "yes" to consider the the answer case sensitive, 
		 * "no" to consider it case insensitive or null to not override this property
		 * @param trim "yes" to ignore starting and ending whitespaces from answer, 
		 * "no" to not ignore them or null to not override this property 
		 * @param strip "yes" to ignore all whitespaces from answer, "no" to not ignore them
		 * or null to not override this property
		 * @param singlespaces "yes" to consider together whitespaces from answer are as single 
		 * whitespaces, "no" to not consider them as single whitespaces or null to not override this
		 * property
		 * @param newlinespace "yes" to consider new line characters from answer are as whitespaces,
		 * "no" to not consider them as single whitespaces or null to not override this property.<br/>
		 * Note that trim, strip and singlespaces properties are affected if this property 
		 * is set to "yes".
		 * @param ignore Text ocurrences (separated by commas) to ignore from answer
		 * or null to not override this property
		 * @param ignoreregexp Regular expression to ignore matched text ocurrences from answer
		 * or null to not override this property
		 * @param ignoreemptylines "yes" to ignore empty lines from answer, "no" to not ignore them
		 * or null to not override this property
		 * @param tolerance Error tolerance used in numeric comparisons or null to not override 
		 * this property
		 */
		public AnswerTestProperties(String answertype,String casesensitive,String trim,String strip,
				String singlespaces,String newlinespace,String ignore,String ignoreregexp,
				String ignoreemptylines,String tolerance) {
			
			this.answertype=answertype;
			this.casesensitive=casesensitive;
			this.trim=trim;
			this.strip=strip;
			this.singlespaces=singlespaces;
			this.newlinespace=newlinespace;
			this.ignore=ignore;
			this.ignoreregexp=ignoreregexp;
			this.ignoreemptylines=ignoreemptylines;
			this.tolerance=tolerance;
		}
		
		/**
		 * @param defaultAnswerType Not overridden answer's type
		 * @return Overridden answer's type
		 */
		public String getAnswerType(String defaultAnswerType)
		{
			return answertype==null?defaultAnswerType:answertype;
		}
		
		/**
		 * @param defaultCaseSensitive Not overridden casesensitive property's value<br/>
		 * @return Overridden casesensitive property's value
		 */
		public boolean getCaseSensitive(boolean defaultCaseSensitive)
		{
			return casesensitive==null?defaultCaseSensitive:casesensitive.equals("yes")?
					true:casesensitive.equals("no")?false:defaultCaseSensitive;
		}
		
		/**
		 * @param defaultTrim Not overridden trim property's value<br/>
		 * @return Overridden trim property's value
		 */
		public boolean getTrim(boolean defaultTrim)
		{
			return trim==null?defaultTrim:trim.equals("yes")?true:trim.equals("no")?false:defaultTrim;
		}
		
		/**
		 * @param defaultStrip Not overridden strip property's value<br/>
		 * @return Overridden strip property's value
		 */
		public boolean getStrip(boolean defaultStrip)
		{
			return strip==null?defaultStrip:strip.equals("yes")?true:strip.equals("no")?
					false:defaultStrip;
		}
		
		/**
		 * @param defaultSingleSpaces Not overridden singlespaces property's value<br/>
		 * @return Overridden singlespaces property's value
		 */
		public boolean getSingleSpaces(boolean defaultSingleSpaces)
		{
			return singlespaces==null?defaultSingleSpaces:singlespaces.equals("yes")?
					true:singlespaces.equals("no")?false:defaultSingleSpaces;
		}
		
		/**
		 * @param defaultNewLineSpace Not overridden newlinespace property's value<br/>
		 * @return Overridden newlinespace property's value
		 */
		public boolean getNewLineSpace(boolean defaultNewLineSpace)
		{
			return newlinespace==null?defaultNewLineSpace:newlinespace.equals("yes")?
					true:newlinespace.equals("no")?false:defaultNewLineSpace;
		}
		
		/**
		 * @param defaultIgnore Not overridden ignore property's value<br/>
		 * @return Overridden ignore property's value
		 */
		public String getIgnore(String defaultIgnore)
		{
			return ignore==null?defaultIgnore:ignore;
		}
		
		/**
		 * @param defaultIgnoreRegExp Not overridden ignoreregexp property's value<br/>
		 * @return Overridden ignoreregexp property's value
		 */
		public String getIgnoreRegExp(String defaultIgnoreRegExp)
		{
			return ignoreregexp==null?defaultIgnoreRegExp:ignoreregexp;
		}
		
		/**
		 * @param defaultIgnoreEmptyLines Not overridden ignoreemptylines property's value<br/>
		 * @return Overridden ignoreemptylines property's value
		 */
		public boolean getIgnoreEmptyLines(boolean defaultIgnoreEmptyLines)
		{
			return ignoreemptylines==null?defaultIgnoreEmptyLines:ignoreemptylines.equals("yes")?
					true:ignoreemptylines.equals("no")?false:defaultIgnoreEmptyLines;
		}
		
		/**
		 * @param defaultTolerance Not overridden tolerance property's value<br/> 
		 * @return Overridden tolerance property's value
		 */
		public double getTolerance(double defaultTolerance)
		{
			return tolerance==null?defaultTolerance:NumericTester.inputNumber(tolerance);
		}
	}
	
	/**
	 * Returns the same string but without starting and ending whitespaces.
	 * @param s String
	 * @param newlinespace true if new line characters are considered whitespaces, false otherwise
	 * @return Same string but without starting and ending whitespaces
	 */
	public static String trimWhitespace(String s, boolean newlinespace)
	{
		StringBuffer result=new StringBuffer();
		if (newlinespace)
		{
 			boolean searchStart=true;
  			int iStart=0;
  			while (searchStart && iStart<s.length())
  			{
  				char c=s.charAt(iStart);
  				if (Character.isWhitespace(c) || c=='\n' || c=='\r')
  				{
  					iStart++;
  				}
  				else
  				{
  					searchStart=false;
  				}
			}
  			if (!searchStart)
  			{
  				boolean searchEnd=true;
  				int iEnd=s.length()-1;
  				while (searchEnd && iEnd>=iStart)
  				{
  					char c=s.charAt(iEnd);
  					if (Character.isWhitespace(c) || c=='\n' || c=='\r')
  					{
  						iEnd--;
  					}
  					else
  					{
  						searchEnd = false;
  					}
  				}
  				result.append(s.substring(iStart, iEnd+1));
  			}
		}
		else
		{
			result.append(s.trim());
		}
		return result.toString();
	}
	
	/**
	 * Strips all whitespace characters from string (not just at start and end).
	 * @param s String
	 * @param newlinespace true if new line characters are considered whitespaces, false otherwise
	 * @return Same string but without any whitespace
	 */
	public static String stripWhitespace(String s, boolean newlinespace)
	{
		StringBuffer result = new StringBuffer();
		if (newlinespace)
		{
			for (char c:s.toCharArray())
			{
				if (!Character.isWhitespace(c) && c!='\n' && c!='\r')
				{
					result.append(c);
				}
			}
		}
		else
		{
			result.append(stripWhitespace(s));
		}
		return result.toString();
	}
	
	/**
	 * Returns the same string but replacing every occurrence of several together whitespaces 
	 * with a single whitespace.
	 * @param s String
	 * @param newlinespace true if new line characters are considered whitespaces, false otherwise
	 * @return Same string but replacing every occurrence of several together whitespaces 
	 * with a single whitespace
	 */
	public static String singledWhitespace(String s, boolean newlinespace)
	{
		StringBuffer result=new StringBuffer();
		for(int i=0;i<s.length();i++)
		{
			char c=s.charAt(i);
			if (Character.isWhitespace(c) || (newlinespace && (c=='\n' || c=='\r')))
			{
				boolean searchNotWhitespace=true;
				boolean insertNewLine=c=='\n' || c=='\r';
				int iNotWhiteSpace=i;
				while (searchNotWhitespace && iNotWhiteSpace<s.length())
				{
					char c2=s.charAt(iNotWhiteSpace);
					if (Character.isWhitespace(c2))
					{
						iNotWhiteSpace++;
					}
					else if (newlinespace && (c2=='\n' || c2=='\r'))
					{
						iNotWhiteSpace++;
						insertNewLine=true;
					}
					else
					{
						searchNotWhitespace=false;
					}
				}
				if (!searchNotWhitespace)
				{
					i=iNotWhiteSpace-1;
				}
				if (insertNewLine)
				{
					result.append('\n');
				}
				else
				{
					result.append(' ');
				}
			}
			else
			{
				result.append(c);
			}
		}
		return result.toString();
	}
	
	/**
	 * Returns the same string but removing empty lines.
	 * @param s String
	 * @return Same string but removing empty lines
	 */
	public static String removeEmptyLines(String s)
	{
		StringBuffer result=new StringBuffer();
		for(int i=0;i<s.length();i++)
		{
			char c=s.charAt(i);
			if (c=='\n' || c=='\r')
			{
				boolean searchNotNewLine=true;
				int iNotNewLine=i;
				while (searchNotNewLine && iNotNewLine<s.length())
				{
					char c2=s.charAt(iNotNewLine);
					if (c2=='\n' || c2=='\r')
					{
						iNotNewLine++;
					}
					else
					{
						searchNotNewLine=false;
					}
				}
				if (!searchNotNewLine)
				{
					i=iNotNewLine-1;
				}
				result.append('\n');
			}
			else
			{
				result.append(c);
			}
		}
		return result.toString();
	}
	
	/**
	 * Returns the same string but removing the text ocurrences (separated by commas) indicated.<br/><br/>
	 * Note that there are availabe some escape sequences in the text ocurrences (not in the string):<br/>
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
	 * <tr><td>\&amp;</td><td>&amp;</td></tr>
	 * <tr><td>\"</td><td>"</td></tr>
	 * <tr><td>\\</td><td>\</td></tr>
	 * </table>
	 * @param s String
	 * @param textOcurrences Text ocurrences (separated by commas)
	 * @return Same string but removing the text ocurrences (separated by commas) indicated
	 */
	public static String removeTextOcurrences(String s,String textOcurrences)
	{
		String result=s;
		Map<Character,String> escapeSequences=new HashMap<Character,String>();
		escapeSequences.put(new Character('n'),"\n");
		escapeSequences.put(new Character('?'),"?");
		escapeSequences.put(new Character(':'),":");
		escapeSequences.put(new Character('['),"[");
		escapeSequences.put(new Character(']'),"]");
		escapeSequences.put(new Character('('),"(");
		escapeSequences.put(new Character(')'),")");
		escapeSequences.put(new Character('{'),"{");
		escapeSequences.put(new Character('}'),"}");
		escapeSequences.put(new Character('!'),"!");
		escapeSequences.put(new Character('#'),"#");
		escapeSequences.put(new Character('&'),"&");
		escapeSequences.put(new Character('\"'),"\"");
		escapeSequences.put(new Character('\\'),"\\");
		String[] txtOcs=textOcurrences.split(",");
		boolean newTextOcurrence=true;
		StringBuffer textOcurrence=null;
		for (int i=0;i<txtOcs.length;i++)
		{
			String txtOc=replaceEscapeChars(txtOcs[i],escapeSequences);
			boolean appendComma=false;
			if (endWithSequenceEscapeCharacter(txtOcs[i],escapeSequences))
			{
				txtOc=txtOc.substring(0,txtOc.length()-1);
				appendComma=true;
			}
			if (newTextOcurrence)
			{
				textOcurrence=new StringBuffer(txtOc);
			}
			else
			{
				textOcurrence.append(txtOc);
			}
			if (appendComma)
			{
				textOcurrence.append(',');
				newTextOcurrence=false;
			}
			else
			{
				newTextOcurrence=true;
				result=result.replace(textOcurrence.toString(),"");
			}
		}
		return result;
	}
	
	/**
	 * Returns the same string but clipped to a maximum of <i>lMax</i> characters without cutting any
	 * word and appending the string received <i>sAppend</i>.
	 * @param s String
	 * @param sAppend String to append
	 * @param lMax Maximum characters of clipped string
	 * @return Same string but clipped to a maximum of <i>lMax</i> characters without cutting any
	 * word and appending the string received <i>sAppend</i>
	 */
	public static String clipWord(String s,String sAppend,int lMax)
	{
		StringBuffer sClipWord=new StringBuffer();
		boolean searchLastWhiteSpace=true;
		int iLastWhiteSpace=lMax-sAppend.length()-1;
		while (searchLastWhiteSpace && iLastWhiteSpace>0)
		{
			char c=s.charAt(iLastWhiteSpace);
			if (Character.isWhitespace(c))
			{
				searchLastWhiteSpace=false;
			}
			else
			{
				iLastWhiteSpace--;
			}
		}
		if (searchLastWhiteSpace)
		{
			sClipWord.append(sAppend);
		}
		else
		{
			boolean searchClipWord=true;
			int iClipWord=iLastWhiteSpace-1;
			while (searchClipWord && iClipWord>0)
			{
				char c=s.charAt(iClipWord);
				if (Character.isWhitespace(c))
				{
					iClipWord--;
				}
				else
				{
					searchClipWord=false;
				}
			}
			if (!searchClipWord)
			{
				sClipWord.append(s.substring(0,iClipWord+1));
			}
			sClipWord.append(sAppend);
		}
		return sClipWord.toString();
	}
	
	/**
	 * Get a map with the following escape sequences:<br/>
	 * <table border="1">
	 * <tr><th>Escape sequence</th><th>Replaced character</th></tr>
	 * <tr><td>\,</td><td>,</td></tr>
	 * <tr><td>\.</td><td>.</td></tr>
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
	 * <tr><td>\&amp;</td><td>&amp;</td></tr>
	 * <tr><td>\"</td><td>"</td></tr>
	 * <tr><td>\\</td><td>\</td></tr>
	 * </table>
	 * @return Map with escape sequences
	 */
	public static Map<Character,String> getEscapeSequences()
	{
		Map<Character,String> escapeSequences=new HashMap<Character, String>();
		escapeSequences.put(new Character(','),",");
		escapeSequences.put(new Character('.'),".");
		escapeSequences.put(new Character('?'),"?");
		escapeSequences.put(new Character(':'),":");
		escapeSequences.put(new Character('['),"[");
		escapeSequences.put(new Character(']'),"]");
		escapeSequences.put(new Character('n'),"\n");
		escapeSequences.put(new Character('('),"(");
		escapeSequences.put(new Character(')'),")");
		escapeSequences.put(new Character('{'),"{");
		escapeSequences.put(new Character('}'),"}");
		escapeSequences.put(new Character('!'),"!");
		escapeSequences.put(new Character('#'),"#");
		escapeSequences.put(new Character('&'),"&");
		escapeSequences.put(new Character('\"'),"\"");
		escapeSequences.put(new Character('\\'),"\\");
		return escapeSequences;
	}
	
	/**
	 * Get a map with the following escape sequences:<br/>
	 * <table border="1">
	 * <tr><th>Escape sequence</th><th>Replaced character</th></tr>
	 * <tr><td>\,</td><td>,</td></tr>
	 * <tr><td>\.</td><td>.</td></tr>
	 * <tr><td>\?</td><td>?</td></tr>
	 * <tr><td>\:</td><td>:</td></tr>
	 * <tr><td>\[</td><td>[</td></tr>
	 * <tr><td>\]</td><td>]</td></tr>
	 * <tr><td>\n</td><td>(ignore this escape sequence)</td></tr>
	 * <tr><td>\(</td><td>(</td></tr>
	 * <tr><td>\)</td><td>)</td></tr>
	 * <tr><td>\{</td><td>{</td></tr>
	 * <tr><td>\}</td><td>}</td></tr>
	 * <tr><td>\!</td><td>!</td></tr>
	 * <tr><td>\#</td><td>#</td></tr>
	 * <tr><td>\&amp;</td><td>&amp;</td></tr>
	 * <tr><td>\"</td><td>"</td></tr>
	 * <tr><td>\\</td><td>\</td></tr>
	 * </table>
	 * @return Map with escape sequences
	 */
	public static Map<Character,String> getEscapeSequencesIgnoreNewLine()
	{
		Map<Character,String> escapeSequences=new HashMap<Character, String>();
		escapeSequences.put(new Character(','),",");
		escapeSequences.put(new Character('.'),".");
		escapeSequences.put(new Character('?'),"?");
		escapeSequences.put(new Character(':'),":");
		escapeSequences.put(new Character('['),"[");
		escapeSequences.put(new Character(']'),"]");
		escapeSequences.put(new Character('n'),"");
		escapeSequences.put(new Character('('),"(");
		escapeSequences.put(new Character(')'),")");
		escapeSequences.put(new Character('{'),"{");
		escapeSequences.put(new Character('}'),"}");
		escapeSequences.put(new Character('!'),"!");
		escapeSequences.put(new Character('#'),"#");
		escapeSequences.put(new Character('&'),"&");
		escapeSequences.put(new Character('\"'),"\"");
		escapeSequences.put(new Character('\\'),"\\");
		return escapeSequences;
	}
	
	/**
	 * Replace escape sequences characters in a string.
	 * @param s String
	 * @param escapeSequences Map with the escape sequences
	 * @return String with escape sequences characters replaced
	 */
	public static String replaceEscapeChars(String s,Map<Character,String> escapeSequences)
	{
		StringBuffer replaced=new StringBuffer();
		int i=0;
		while (i<s.length())
		{
			char c=s.charAt(i);
			if (c==SEQUENCE_ESCAPE_CHARACTER)
			{
				if (i+1<s.length())
				{
					Character c2=new Character(s.charAt(i+1));
					if (escapeSequences.containsKey(c2))
					{
						replaced.append(escapeSequences.get(c2));
						i++;
					}
					else
					{
						replaced.append(SEQUENCE_ESCAPE_CHARACTER);
					}
				}
				else
				{
					replaced.append(SEQUENCE_ESCAPE_CHARACTER);
				}
			}
			else
			{
				replaced.append(c);
			}
			i++;
		}
		return replaced.toString();
	}
	
	/**
	 * Replace some escape sequences characters in a string, using the following escape sequences:
	 * <br/><br/>
	 * <table border="1">
	 * <tr><th>Escape sequence</th><th>Replaced character</th></tr>
	 * <tr><td>\,</td><td>,</td></tr>
	 * <tr><td>\.</td><td>.</td></tr>
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
	 * <tr><td>\&amp;</td><td>&amp;</td></tr>
	 * <tr><td>\"</td><td>"</td></tr>
	 * <tr><td>\\</td><td>\</td></tr>
	 * </table>
	 * @param s String
	 * @return String with some escape sequences characters replaced
	 */
	public static String replaceEscapeChars(String s)
	{
		return replaceEscapeChars(s,getEscapeSequences());
	}
	
	/**
	 * Replace some escape sequences characters in a string, using the following escape sequences:
	 * <br/><br/>
	 * <table border="1">
	 * <tr><th>Escape sequence</th><th>Replaced character</th></tr>
	 * <tr><td>\,</td><td>,</td></tr>
	 * <tr><td>\.</td><td>.</td></tr>
	 * <tr><td>\?</td><td>?</td></tr>
	 * <tr><td>\:</td><td>:</td></tr>
	 * <tr><td>\[</td><td>[</td></tr>
	 * <tr><td>\]</td><td>]</td></tr>
	 * <tr><td>\n</td><td>(ignore this escape sequence)</td></tr>
	 * <tr><td>\(</td><td>(</td></tr>
	 * <tr><td>\)</td><td>)</td></tr>
	 * <tr><td>\{</td><td>{</td></tr>
	 * <tr><td>\}</td><td>}</td></tr>
	 * <tr><td>\!</td><td>!</td></tr>
	 * <tr><td>\#</td><td>#</td></tr>
	 * <tr><td>\&amp;</td><td>&amp;</td></tr>
	 * <tr><td>\"</td><td>"</td></tr>
	 * <tr><td>\\</td><td>\</td></tr>
	 * </table>
	 * @param s String
	 * @return String with some escape sequences characters replaced
	 */
	public static String replaceEscapeCharsIgnoreNewLine(String s)
	{
		return replaceEscapeChars(s,getEscapeSequencesIgnoreNewLine());
	}
	
	/**
	 * Get the index of a character in the string taking care of escape sequences.
	 * @param s String
	 * @param ch Character to search
	 * @param escapeSequences  Map with the escape sequences
	 * @return Index of a character in the string taking care of escape sequences or -1 if it is not found
	 */
	public static int indexOfCharacter(String s,char ch,Map<Character,String> escapeSequences)
	{
		return indexOfCharacter(s,ch,0,escapeSequences);
	}
	
	/**
	 * Get the index of a character in the string from the indicated index taking care of escape sequences.
	 * @param s String
	 * @param ch Character to search
	 * @param fromIndex Index of character in the string from which we start the search
	 * @param escapeSequences  Map with the escape sequences
	 * @return Index of a character in the string from the indicated index taking care of escape sequences
	 * or -1 if it is not found
	 */
	public static int indexOfCharacter(String s,char ch,int fromIndex,Map<Character,String> escapeSequences)
	{
		int index=-1;
		if (fromIndex<s.length())
		{
			boolean searchIndex=escapeSequences.containsKey(new Character(ch));
			index=s.indexOf(ch,fromIndex);
			while (index!=-1  && searchIndex)
			{
				if (index>0 && endWithSequenceEscapeCharacter(s.substring(0,index),escapeSequences))
				{
					if (index+1<s.length())
					{
						index=s.indexOf(ch,index+1);
					}
					else
					{
						index=-1;
					}
				}
				else
				{
					searchIndex=false;
				}
			}
		}
		return index;
	}
	
	/**
	 * Checks if the last character of the string is the escape sequence character, taking account other
	 * possible sequence escapes.
	 * @param s String
	 * @param escapeSequences Map with the escape sequences
	 * @return true if the last character of the string is the escape sequence character, false otherwise
	 */
	public static boolean endWithSequenceEscapeCharacter(String s,Map<Character,String> escapeSequences)
	{
		boolean ok=false;
		int i=0;
		while (i<s.length())
		{
			char c=s.charAt(i);
			if (c==SEQUENCE_ESCAPE_CHARACTER)
			{
				if (i+1<s.length())
				{
					Character c2=new Character(s.charAt(i+1));
					if (escapeSequences.containsKey(c2))
					{
						i++;
					}
				}
				else
				{
					ok=true;
				}
			}
			i++;
		}
		return ok;
	}
	
	/**
	 * Split a string by a separator character taking care of escape sequences.
	 * @param s String
	 * @param separator Separator character
	 * @param escapeSequences Map with the escape sequences
	 * @return List of splitted strings
	 */
	public static List<String> split(String s,char separator,Map<Character,String> escapeSequences)
	{
		List<String> splitted=new ArrayList<String>();
		StringBuffer token=new StringBuffer();
		for (int i=0;i<s.length();i++)
		{
			char c=s.charAt(i);
			if (c==separator && !endWithSequenceEscapeCharacter(s.substring(0,i),escapeSequences))
			{
				splitted.add(token.toString());
				token=new StringBuffer();
			}
			else
			{
				token.append(c);
			}
		}
		splitted.add(token.toString());
		return splitted;
	}
	
	/**
	 * Split a string by a separator character but without splitting selectors, even if they contain
	 * separator characters.<br/><br/>
	 * It also takes care of escape sequences.
	 * @param s String
	 * @param separator Separator character
	 * @param escapeSequences Map with the escape sequences
	 * @return List of splitted strings
	 */
	public static List<String> splitButNotInsideSelectors(String s,char separator,
			Map<Character,String> escapeSequences)
	{
		List<String> splitted=new ArrayList<String>();
		StringBuffer token=new StringBuffer();
		boolean selectorOpened=false;
		boolean selectorClosed=false;
		for (int i=0;i<s.length();i++)
		{
			char c=s.charAt(i);
			if (c==SELECTOR_OPEN)
			{
				if (!selectorClosed)
				{
					selectorOpened=true;
				}
				token.append(SELECTOR_OPEN);
			}
			else if (c==SELECTOR_CLOSE)
			{
				if (selectorOpened)
				{
					selectorClosed=true;
				}
				selectorOpened=false;
				token.append(SELECTOR_CLOSE);
			}
			else if (c==separator && !endWithSequenceEscapeCharacter(s.substring(0,i),escapeSequences))
			{
				if (!selectorOpened)
				{
					splitted.add(token.toString());
					token=new StringBuffer();
				}
				else
				{
					token.append(c);
				}
			}
			else
			{
				token.append(c);
			}
		}
		splitted.add(token.toString());
		return splitted;
	}
	
	/**
	 * Returns a lower case version of a regular expression taking care of some special constructions.
	 * @param regExp Regular expression
	 * @return Lower case version of a regular expression taking care of some special constructions
	 */
	public static String regExpToLowerCase(String regExp)
	{
		StringBuffer lowercaseRegExp=new StringBuffer();
		int i=0;
		while (i<regExp.length())
		{
			char c=regExp.charAt(i);
			if (c=='\\')
			{
				lowercaseRegExp.append('\\');
				i++;
				if (i<regExp.length())
				{
					c=regExp.charAt(i);
					lowercaseRegExp.append(c);
					if (i+1<lowercaseRegExp.length() && regExp.charAt(i+1)=='{')
					{
						int j=lowercaseRegExp.indexOf("}",i+1);
						if (j!=-1)
						{
							lowercaseRegExp.append(lowercaseRegExp.substring(i+1,j+1));
							i=j;
						}
					}
				}
			}
			else
			{
				lowercaseRegExp.append(Character.toLowerCase(c));
			}
			i++;
		}
		return lowercaseRegExp.toString();
	}
}
