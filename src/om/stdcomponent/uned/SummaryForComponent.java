package om.stdcomponent.uned;

import java.util.List;

import om.OmException;
import om.helper.uned.AnswerChecking;
import om.stdquestion.QContent;
import om.stdquestion.uned.StandardQuestion;

// UNED: 27-06-2011 - dballestin
/**
 * This is a component to be used inside a &lt;summaryline&gt; to generate text for several answers.<br/>
 * It can be declared everywhere inside question.xml and can contain other content.<br/>
 * Contained text and &lt;summaryattribute&gt; components define the template to generate the text
 * to display.<br/>
 * The text displayed is generated repeating the template for every answer passed and replacing
 * &lt;summaryattribute&gt; components with the value associated to each answer in every iteration.
 * <br/>
 * <h2>XML usage</h2>
 * &lt;summaryfor&gt;...&lt;/summaryfor&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, 
 * like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>separator</td><td>(string)</td><td>Specifies the separator string between iterations, 
 * by default ", "</td></tr>
 * </table>
 */
public class SummaryForComponent extends ForComponent
{
	/** @return Tag name (introspected; this may be replaced by a 1.5 annotation) */
	public static String getTagName()
	{
		return "summaryfor";
	}
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
	}
	
	/**
	 * Removes starting and ending whitespaces and newline characters ('\n','\r') from passed string.
	 * <br/>
	 * Moreover, all whitespaces and newline characters ('\n','\r') found inside string are
	 * replaced by single whitespace.
	 * @param s String
	 * @return String without starting and ending whitespaces and newline characters and with
	 * whitespaces and newline characters inside the string replaced by single whitespace.
	 */
	private String getStringElement(String s)
	{
		StandardQuestion sq=(StandardQuestion)getQuestion();
		String sReplaced=s;
		if (sq.isPlaceholdersInitialized() && StandardQuestion.containsPlaceholder(sReplaced))
		{
			sReplaced=sq.applyPlaceholders(sReplaced);
		}
		return AnswerChecking.singledWhitespace(
				AnswerChecking.trimWhitespace(sReplaced,true),true).replace('\n',' ');
	}
	
	/**
	 * Get the part of summary "for component" that corresponds to element 
	 * @param element Element
	 * @param idAnswer Identifier of component selected for answer
	 * @return Part of summary "for component" that corresponds to element
	 */
	private String getPartOfFor(Object element,String idAnswer)
	{
		StringBuffer partOfLine=new StringBuffer();
		if (element instanceof String)
		{
			partOfLine.append(getStringElement((String)element));
		}
		else if (element instanceof SummaryAttributeComponent)
		{
			partOfLine.append(((SummaryAttributeComponent)element).getAttributeValue(idAnswer));
		}
		return partOfLine.toString();
	}
	
	/**
	 * Tests if the element is a string and has starting whitespaces or newline characters.
	 * @param element Element
	 * @return true if the element is a string and has starting whitespaces or newline characters,
	 * false otherwise
	 */
	private boolean hasStartingWhitespace(Object element)
	{
		boolean startingWhitespace=false;
		if (element instanceof String && element!=null && ((String)element).length()>=0)
		{
			char c=((String)element).charAt(0);
			startingWhitespace=Character.isWhitespace(c) || c=='\n' || c=='\r';
		}
		return startingWhitespace;
	}
	
	/**
	 * Tests if the element is a string and has ending whitespaces or newline characters.
	 * @param insertingWhitespace true if last element has ending whitespaces or 
	 * newline characters, false otherwise
	 * @param element Element
	 * @param strElem String representation of element
	 * @return true if the element is a string and has ending whitespaces or newline characters,
	 * false otherwise
	 */
	private boolean hasEndingWhitespace(boolean insertingWhitespace,Object element,String strElem)
	{
		boolean endingWhitespace=false;
		if (element!=null)
		{
			if ((element instanceof String) && ((String)element).length()>=0)
			{
				char c=((String)element).charAt(((String)element).length()-1);
				endingWhitespace=Character.isWhitespace(c) || c=='\n' || c=='\r';
			}
			else if (element instanceof SummaryAttributeComponent)
			{
				if (strElem!=null && strElem.length()==0)
				{
					endingWhitespace=insertingWhitespace;
				}
			}
		}
		return endingWhitespace;
	}
	
	/**
	 * Get summary "for component" as string for a single answer (no iterations needed).
	 * @param idAnswer Identifier of component selected for answer
	 * @return Summary for component as string for a single answer (no iterations needed)
	 */
	private String getSummaryFor(String idAnswer)
	{
		StringBuffer summaryFor=new StringBuffer();
		boolean insertingWhitespace=false;
		for (Object element:getChildren())
		{
			String partOfFor=getPartOfFor(element,idAnswer);
			if (!partOfFor.equals(""))
			{
				insertingWhitespace|=hasStartingWhitespace(element);
				if (insertingWhitespace && summaryFor.length()>0)
				{
					summaryFor.append(' ');
				}
				summaryFor.append(partOfFor);
			}
			insertingWhitespace=hasEndingWhitespace(insertingWhitespace,element,partOfFor.toString());
		}
		return summaryFor.toString();
	}
	
	/**
	 * Get summary "for component" as string iterating over the list of answers received.
	 * @return Summary "for component" as string iterating over the list of answers received
	 */
	public String getSummaryFor()
	{
		StringBuffer summaryFor=new StringBuffer();
		StandardQuestion sq=(StandardQuestion)getQuestion();
		boolean insertSeparator=false;
		List<String> itAnswers=null;
		if (getType().equals(TYPE_ID_ANSWERS))
		{
			itAnswers=sq.getIdAnswers();
		}
		else if (getType().equals(TYPE_ALL_ID_ANSWERS))
		{
			itAnswers=sq.getAllAnswerIds();
		}
		if (itAnswers!=null)
		{
			for (String idAnswer:itAnswers)
			{
				if (insertSeparator)
				{
					summaryFor.append(getSeparator());
				}
				else
				{
					insertSeparator=true;
				}
				summaryFor.append(getSummaryFor(idAnswer));
			}
		}
		return summaryFor.toString();
	}
}
