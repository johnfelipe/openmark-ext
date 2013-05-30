package om.stdcomponent.uned;

import org.w3c.dom.Element;

import om.OmException;
import om.OmFormatException;
import om.stdquestion.QContent;

// UNED: 22-06-2011 - dballestin
/**
 * This is a component used to declare an attribute inside a &lt;summaryline&gt; component.<br/>
 * When the summary line that contains this component pass its tests, this component is replaced
 * inside the builded summary line by the value of the attribute selected with attribute property:
 * <br/><br/>
 * <h2>Attributes</h2>
 * <table border="1">
 * <tr><th>Attribute</th><th>Description</th></tr>
 * <tr><td>attempt</td><td>Attempt number</td></tr>
 * <tr><td>maxattempts</td><td>Maximum number of attempts allowed</td></tr>
 * <tr><td>questionline</td><td>Question line</td></tr>
 * <tr><td>defaultquestionline</td><td>Default question line</td></tr>
 * <tr><td>answerid</td><td>Answer's component id</td></tr>
 * <tr><td>answertagname</td><td>Answer's component tag name</td></tr>
 * <tr><td>answerline</td><td>Answer line</td></tr>
 * <tr><td>defaultanswerline</td><td>Default answer line</td></tr>
 * <tr><td>defaultsummaryline</td><td>Default summary line</td></tr>
 * <tr><td>selectableanswers</td><td>Number of selectable answers.<br/><br/>
 * Note that it is not number of answer's components.<br/>
 * It is more the maximum number of answer's components selectable by user at same time 
 * in an attempt.</td></tr>
 * <tr><td>selectablerightanswers</td><td>Number of selectable right answers.<br/><br/>
 * Note that it is not the number of right answer's components.<br/>
 * It is more the maximum number of right answer's components selectable by user at same time 
 * in an attempt.</td></tr>
 * <tr><td>selectablewronganswers</td><td>Number of selectable wrong answers.<br/><br/>
 * Note that it is not the number of wrong answer's components.<br/>
 * It is more the maximum number of wrong answer's components selectable by user at same time 
 * in an attempt.</td></tr>
 * <tr><td>selectedanswers</td><td>Number of selected answers</td></tr>
 * <tr><td>selectedrightanswers</td><td>Number of selected right answers</td></tr>
 * <tr><td>selectedwronganswers</td><td>Number of selected wrong answers</td></tr>
 * <tr><td>unselectedanswers</td><td>Number of unselected answers</td></tr>
 * <tr><td>unselectedrightanswers</td><td>Number of unselected right answers</td></tr>
 * <tr><td>unselectedwronganswers</td><td>Number of unselected wrong answers</td></tr>
 * <tr><td>rightdistance</td><td>Distance to the right answer, 0 means that this is 
 * the right answer.<br/><br/>You can think about it as the number of errors in answer</td></tr>
 * <tr><td>right</td><td>Answer rightness: "right" or "wrong"</td></tr>
 * <tr><td>isright</td><td>"yes" if answer is right, "no" if it is wrong</td></tr>
 * <tr><td>isrightbit</td><td>"1" if answer is right, "0" if it is wrong</td></tr>
 * <tr><td>iswrong</td><td>"yes" if answer is wrong, "no" if it is right</td></tr>
 * <tr><td>iswrongbit</td><td>"1" if answer is wrong, "0" if it is right</td></tr>
 * <tr><td>random</td><td>Value of a random component</td></tr>
 * <tr><td>randomindex</td><td>Index of a random component</td></tr>
 * <tr><td>if</td><td>Some value if a condition is matched and optionally other 
 * if it is not matched</td></tr>
 * <tr><td>ifanswered</td><td>Some value if a component is in the answer and optionally other
 * if it is not</td></tr>
 * <tr><td>answermap</td><td>Mapped text value corresponding to an answer key</td></tr>
 * <tr><td>variable</td><td>Value of a variable or a random component</td></tr>
 * </table>
 * <br/>
 * <h2>XML usage</h2>
 * &lt;summaryattribute attribute="..." ... /&gt;
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, 
 * like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>attribute</td><td>(string)</td><td>Specifies attribute name (see <b>Attributes</b>).
 * </td></tr>
 * </table>
 */
public class SummaryAttributeComponent extends AttributeComponent
{
	/** @return Tag name (introspected; this may be replaced by a 1.5 annotation) */
	public static String getTagName()
	{
		return "summaryattribute";
	}
	
	@Override
	protected void initChildren(Element eThis) throws OmException
	{
		if (eThis.getFirstChild()!=null)
		{
			throw new OmFormatException("<summaryattribute> may not include children");
		}
	}
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
	}
}
