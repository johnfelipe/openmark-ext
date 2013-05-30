package om.stdcomponent.uned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import om.OmDeveloperException;
import om.OmException;
import om.OmFormatException;
import om.OmUnexpectedException;
import om.graph.World;
import om.helper.PMatch;
import om.helper.uned.AnswerChecking;
import om.helper.uned.NumericTester;
import om.helper.uned.AnswerChecking.AnswerTestProperties;
import om.helper.uned.NumericTester.NumericOptions;
import om.stdcomponent.CanvasComponent.Marker;
import om.stdquestion.QComponent;
import om.stdquestion.QContent;
import om.stdquestion.QDocument;
import om.stdquestion.uned.StandardQuestion;

// UNED: 08-09-2011 - dballestin
/**
 * This is a component to declare a variable available to use with replaceholder, attribute,
 * summaryattributes components (and perhaps even in testing)<br/><br/>
 * It can be used as an answer component setting answerenabled="yes" but by default 
 * it is not considered one.<br/><br/>
 * <b>IMPORTANT:</b> Think catiously before setting a variable as an answer component. If its value 
 * depends on value of another answer component perhaps it is not a good idea. Generally a variable can
 * be a good candidate for an answer component if its value can be changed by user with help of
 * non-tipically answer components (for example buttons) and &lt;task&gt; components and its value
 * is a good representative of the answer or a part of it.
 * <h2>XML usage</h2>
 * &lt;variable id="..." set="@<b>value</b>"/&gt;<br/><br/>
 * or<br/><br/>
 * &lt;variable id="..." set="<b>id</b>"/&gt;<br/><br/>
 * or<br/><br/>
 * &lt;variable id="..." set="<b>id</b>[transformations]"/&gt;<br/><br/>
 * Note that <b>id</b> can be the identifier of an answer component (answerline), &lt;random&gt;, 
 * &lt;replaceholder&gt; or even other &lt;variable&gt;<br/><br/>
 * Moreover you can use transformations.<br/>
 * <h2>Transformations</h2>
 * <table border="1">
 * <tr><th>Transformation code</th><th>Effect</th></tr>
 * <tr><td>t</td><td>Trim the string</td></tr>
 * <tr><td>x</td><td>Strip whitespaces from string</td></tr>
 * <tr><td>o</td><td>Replace adjacent whitespaces from string with a sigle whitespace</td></tr>
 * <tr><td>l</td><td>Transform all letters to lowercase</td></tr>
 * <tr><td>u</td><td>Transform all letters to uppercase</td></tr>
 * <tr><td>r<i>v1</i>,<i>v2</i></td><td>Replaces ocurrences of <i>v1</i> with <i>v2</i></td></tr>
 * <tr><td>i<i>v</i></td><td>Inserts <i>v</i> at the beginning of the string</td></tr>
 * <tr><td>i<i>v</i>,#<i>pos</i></td><td>Inserts <i>v</i> before character at position <i>pos</i> 
 * of the string</td></tr>
 * <tr><td>i<i>v1</i>,<i>v2</i></td><td>Inserts <i>v1</i> before first ocurrence of <i>v2</i>
 * </td></tr>
 * <tr><td>i<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Inserts <i>v1</i> before first ocurrence 
 * of <i>v2</i> from position <i>pos</i></td></tr>
 * <tr><td>a<i>v</i></td><td>Appends <i>v</i> to string</td></tr>
 * <tr><td>a<i>v</i>,#<i>pos</i></td><td>Appends <i>v</i> after character at position <i>pos</i> 
 * of the string</td></tr>
 * <tr><td>a<i>v1</i>,<i>v2</i></td><td>Appends <i>v1</i> after first ocurrence of <i>v2</i>
 * </td></tr>
 * <tr><td>a<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Appends <i>v1</i> after first ocurrence 
 * of <i>v2</i> from position <i>pos</i></td></tr>
 * <tr><td>d#<i>pos</i></td><td>Delete characters from position <i>pos</i> (inclusive) 
 * to the end of the string</td></tr>
 * <tr><td>d#<i>pos1</i>,#<i>pos2</i></td><td>Delete characters from position <i>pos1</i> 
 * (inclusive) to position <i>pos2</i> (exclusive)</td></tr>
 * <tr><td>d<i>v</i></td><td>Delete characters from first ocurrence of <i>v</i> (inclusive) 
 * to the end of the string</td></tr>
 * <tr><td>d(<i>v</i></td><td>Delete characters from first ocurrence of <i>v</i> (exclusive) 
 * to the end of the string</td></tr>
 * <tr><td>d{<i>v</i></td><td>Delete characters from first ocurrence of <i>v</i> (inclusive) 
 * to the end of the string</td></tr>
 * <tr><td>d<i>v</i>,#<i>pos</i></td><td>Delete characters from first ocurrence of <i>v</i> 
 * from position <i>pos</i> (inclusive) to the end of the string</td></tr>
 * <tr><td>d(<i>v</i>,#<i>pos</i></td><td>Delete characters from first ocurrence of <i>v</i> 
 * from position <i>pos</i> (exclusive) to the end of the string</td></tr>
 * <tr><td>d{<i>v</i>,#<i>pos</i></td><td>Delete characters from first ocurrence of <i>v</i> 
 * from position <i>pos</i> (inclusive) to the end of the string</td></tr>
 * <tr><td>d<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>d(<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (exclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>d{<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>d)<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>d}<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (inclusive)</td></tr>
 * <tr><td>d()<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (exclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>d{)<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>d(}<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (exclusive) to next ocurrence of <i>v2</i> (inclusive)</td></tr>
 * <tr><td>d{}<i>v1</i>,<i>v2</i></td><td>Delete characters from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (inclusive)</td></tr>
 * <tr><td>d<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>d(<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (exclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>d{<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>d)<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>d}<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (inclusive)
 * </td></tr>
 * <tr><td>d()<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (exclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>d{)<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>d(}<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (exclusive) to next ocurrence of <i>v2</i> (inclusive)
 * </td></tr>
 * <tr><td>d{}<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Delete characters from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (inclusive)
 * </td></tr>
 * <tr><td>s#<i>pos</i></td><td>Get substring from position <i>pos</i> (inclusive) of the string
 * </td></tr>
 * <tr><td>s#<i>pos1</i>,#<i>pos2</i></td><td>Get substring from position <i>pos1</i> 
 * (inclusive) to position <i>pos2</i> (exclusive) of the string</td></tr>
 * <tr><td>s<i>v</i></td><td>Get substring from first ocurrence of <i>v</i> (inclusive) 
 * to the end of the string</td></tr>
 * <tr><td>s(<i>v</i></td><td>Get substring from first ocurrence of <i>v</i> (exclusive) 
 * to the end of the string</td></tr>
 * <tr><td>s{<i>v</i></td><td>Get substring from first ocurrence of <i>v</i> (inclusive) 
 * to the end of the string</td></tr>
 * <tr><td>s<i>v</i>,#<i>pos</i></td><td>Get substring from first ocurrence of <i>v</i> 
 * from position <i>pos</i> (inclusive) to the end of the string</td></tr>
 * <tr><td>s(<i>v</i>,#<i>pos</i></td><td>Get substring from first ocurrence of <i>v</i> 
 * from position <i>pos</i> (exclusive) to the end of the string</td></tr>
 * <tr><td>s{<i>v</i>,#<i>pos</i></td><td>Get substring from first ocurrence of <i>v</i> 
 * from position <i>pos</i> (inclusive) to the end of the string</td></tr>
 * <tr><td>s<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>s(<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (exclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>s{<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>s)<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>s}<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (inclusive)</td></tr>
 * <tr><td>s()<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (exclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>s{)<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (exclusive)</td></tr>
 * <tr><td>s(}<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (exclusive) to next ocurrence of <i>v2</i> (inclusive)</td></tr>
 * <tr><td>s{}<i>v1</i>,<i>v2</i></td><td>Get substring from first ocurrence of <i>v1</i> 
 * (inclusive) to next ocurrence of <i>v2</i> (inclusive)</td></tr>
 * <tr><td>s<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>s(<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (exclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>s{<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>s)<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>s}<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (inclusive)
 * </td></tr>
 * <tr><td>s()<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (exclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>s{)<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (exclusive)
 * </td></tr>
 * <tr><td>s(}<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (exclusive) to next ocurrence of <i>v2</i> (inclusive)
 * </td></tr>
 * <tr><td>s{}<i>v1</i>,<i>v2</i>,#<i>pos</i></td><td>Get substring from first ocurrence 
 * of <i>v1</i> from position <i>pos</i> (inclusive) to next ocurrence of <i>v2</i> (inclusive)
 * </td></tr>
 * <tr><td>c<i>v</i></td><td>Count number of ocurrences of <i>v</i></td></tr>
 * <tr><td>c<i>v1</i>,<i>v2</i></td><td>Count number of ocurrences of <i>v1</i>, but excluding 
 * from counting ocurrences of the strings inside <i>v2</i>.<br/><br/>
 * Syntax for <i>v2</i> consists in a list of strings separated by commas</td></tr>
 * <tr><td>e<i>v1</i>,<i>v2</i></td><td>If the value is equal to string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>.</td></tr>
 * <tr><td>e<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value is equal to string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>, otherwise value is changed to the value of <i>v3</i>.
 * </td></tr>
 * <tr><td>m<i>v1</i>,<i>v2</i></td><td>If the value is not equal to string value of <i>v1</i> value is 
 * changed to the value of <i>v2</i>.</td></tr>
 * <tr><td>m<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value is not equal to string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>, otherwise value is changed to the value of <i>v3</i>.
 * </td></tr>
 * <tr><td>k<i>v1</i>,<i>v2</i></td><td>If the value is less than string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>.</td></tr>
 * <tr><td>k<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value is less than string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>, otherwise value is changed to the value of <i>v3</i>.
 * </td></tr>
 * <tr><td>ke<i>v1</i>,<i>v2</i></td><td>If the value is less or equal than string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>.</td></tr>
 * <tr><td>ke<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value is less or equal than string value of 
 * <i>v1</i> value is changed to the value of <i>v2</i>, otherwise value is changed 
 * to the value of <i>v3</i>.</td></tr>
 * <tr><td>g<i>v1</i>,<i>v2</i></td><td>If the value is greater than string value of <i>v1</i> value 
 * is changed to the value of <i>v2</i>.</td></tr>
 * <tr><td>g<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value is greater than string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>, otherwise value is changed to the value of <i>v3</i>.
 * </td></tr>
 * <tr><td>ge<i>v1</i>,<i>v2</i></td><td>If the value is greater or equal than string value of <i>v1</i> 
 * value is changed to the value of <i>v2</i>.</td></tr>
 * <tr><td>ge<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value is greater or equal than string value of 
 * <i>v1</i> value is changed to the value of <i>v2</i>, otherwise value is changed 
 * to the value of <i>v3</i>.</td></tr>
 * <tr><td>ni<i>v</i></td><td>Check if value is a valid integer and if it is then changes value
 * to the value of <i>v</i>, if it is not then value remains intact</td></tr>
 * <tr><td>ni!<i>v</i></td><td>Check if value is a valid integer and if it is not then changes value
 * to the value of <i>v</i>, if it is then value remains intact</td></tr>
 * <tr><td>ni<i>v1</i>,<i>v2</i></td><td>Check if value is a valid integer and if it is then changes 
 * value to the value of <i>v1</i> and if it is not then changes value to the value of  <i>v2</i>
 * </td></tr>
 * <tr><td>nf<i>v</i></td><td>Check if value is a valid double and if it is then changes value
 * to the value of <i>v</i>, if it is not then value remains intact</td></tr>
 * <tr><td>nf!<i>v</i></td><td>Check if value is a valid double and if it is not then changes value
 * to the value of <i>v</i>, if it is then value remains intact</td></tr>
 * <tr><td>nf<i>v1</i>,<i>v2</i></td><td>Check if value is a valid double and if it is then changes 
 * value to the value of <i>v1</i> and if it is not then changes value to the value of <i>v2</i>
 * </td></tr>
 * <tr><td>na<i>v</i></td><td>Add double value of <i>v</i> to the value supposing it is also an double.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>ns<i>v</i></td><td>Substract double value of <i>v</i> to the value supposing it is also 
 * an double.<br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nm<i>v</i></td><td>Multiply double value of <i>v</i> to the value supposing it is also 
 * an double.<br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nd<i>v</i></td><td>Divide double value of <i>v</i> to the value supposing it is also 
 * an double.<br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nai<i>v</i></td><td>Add integer value of <i>v</i> to the value supposing it is also 
 * an integer.<br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nsi<i>v</i></td><td>Substract integer value of <i>v</i> to the value supposing it is also 
 * an integer.<br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nmi<i>v</i></td><td>Multiply integer value of <i>v</i> to the value supposing it is also 
 * an integer.<br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>ndi<i>v</i></td><td>Divide integer value of <i>v</i> to the value supposing it is also 
 * an integer.<br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>ne<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid double) is equal to
 * double value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>ne<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid double) 
 * is equal to double value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nn<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid double) is not equal to
 * double value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nn<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid double) 
 * is not equal to double value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nl<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid double) is less than 
 * double value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nl<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid double) 
 * is less than double value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nle<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid double) 
 * is less or equal than  double value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nle<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid double) 
 * is less or equal than double value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>ng<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid double) is greater than 
 * double value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>ng<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid double) 
 * is greater than double value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nge<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid double) 
 * is greater or equal than double value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nge<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid double) 
 * is greater or equal than double value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid doubles the value remains intact</td></tr>
 * <tr><td>nei<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid integer) is equal to
 * integer value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nei<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid integer) 
 * is equal to integer value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nni<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid integer) is not equal to
 * integer value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nni<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid integer) is 
 * not equal to integer value of <i>v1</i> value is changed to the value of <i>v2</i>, otherwise value 
 * is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nli<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid integer) is less than 
 * integer value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not validintegers the value remains intact</td></tr>
 * <tr><td>nli<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid integer) is 
 * less than integer value of <i>v1</i> value is changed to the value of <i>v2</i>, otherwise value 
 * is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nlei<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid integer) 
 * is less or equal than integer value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nlei<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid integer) 
 * is less or equal than integer value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>ngi<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid integer) 
 * is greater than integer value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>ngi<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid integer) 
 * is greater than integer value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>ngei<i>v1</i>,<i>v2</i></td><td>If the value (supposing it is a valid integer) 
 * is greater or equal than integer value of <i>v1</i> value is changed to the value of <i>v2</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>ngei<i>v1</i>,<i>v2</i>,<i>v3</i></td><td>If the value (supposing it is a valid integer) 
 * is greater or equal than integer value of <i>v1</i> value is changed to the value of <i>v2</i>, 
 * otherwise value is changed to the value of <i>v3</i>.
 * <br/><br/>If some of those values are not valid integers the value remains intact</td></tr>
 * <tr><td>nt</td><td>Convert the value, supposing it is a double, to the closest lower integer value.
 * <br/><br/>If the value is not a valid double the value remains intact</td></tr>
 * <tr><td>nr</td><td>Convert the value, supposing it is a double, to the closest integer value 
 * (can be lower or greater).<br/><br/>If the value is not a valid double the value remains intact
 * </td></tr>
 * <tr><td>nc</td><td>Convert the value, supposing it is a double, to the closest greater integer value.
 * <br/><br/>If the value is not a valid double the value remains intact</td></tr>
 * </table>
 * <br/>
 * Note that <i>v</i>, <i>v1</i>, <i>v2</i> and <i>v3</i> can be string literals 
 * (within quotes: <i>"string literal"</i>) 
 * or references to random, replaceholder, answer components or even other variable components
 * (id followed by the symbol @ at end).<br/><br/>
 * By the other hand #<i>pos</i>, #<i>pos1</i> and #<i>pos2</i> 
 * are always positive integer constants: #<i>positive_integer_value</i><br/><br/>
 * Placeholders can be used inside set attribute, they are replaced before parsing transformations
 * string.<br/><br/>
 * <h2>Properties</h2>
 * <table border="1">
 * <tr><th>Property</th><th>Values</th><th>Effect</th></tr>
 * <tr><td>id</td><td>(string)</td><td>Specifies unique ID</td></tr>
 * <tr><td>display</td><td>(boolean)</td><td>Includes in/removes from output</td></tr>
 * <tr><td>enabled</td><td>(boolean)</td><td>Activates/deactivates children</td></tr>
 * <tr><td>lang</td><td>(string)</td><td>Specifies the language of the content, 
 * like the HTML lang attribute. For example 'en' = English, 'el' - Greek, ...</td></tr>
 * <tr><td>answerenabled</td><td>(boolean)</td><td>Specifies if this component is used as an answer 
 * component, by default no</td></tr>
 * <tr><td>right</td><td>(string)</td><td>Specifies the right answer</td></tr>
 * <tr><td>set</td><td>(string)</td><td>Specifies the setting value to set the variable</td></tr>
 * <tr><td>valuetype</td><td>(string)</td><td>Specifies the value's type</td></tr>
 * <tr><td>casesensitive</td><td>(boolean)</td><td>Specifies if the value is considered to be 
 * case sensitive (yes) or case insensitive (no), by default yes</td></tr>
 * <tr><td>trim</td><td>(boolean)</td><td>Specifies if starting and ending whitespaces from value
 * are ignored (yes) or not (no), by default yes</td></tr>
 * <tr><td>strip</td><td>(boolean)</td><td>Specifies if all whitespaces from value are ignored (yes)
 * or not (no), by default no</td></tr>
 * <tr><td>singlespaces</td><td>(boolean)</td><td>Specifies if together whitespaces from value are 
 * considered as single whitespaces (yes) or not (no), by default yes</td></tr>
 * <tr><td>newlinespace</td><td>(boolean)</td><td>Specifies if new line characters from value are 
 * considered as whitespaces (yes) or not (no), by default yes.<br/><br/>
 * Note that trim, strip and singlespaces properties are affected if this property is set to yes.
 * </td></tr>
 * <tr><td>ignore</td><td>(string)</td><td>Specifies text ocurrences (separated by commas) 
 * to ignore from value, by default null</td></tr>
 * <tr><td>ignoreregexp</td><td>(string)</td><td>Specifies a regular expression to ignore text 
 * ocurrences matching it, by default null</td></tr>
 * <tr><td>ignoreemptylines</td><td>(boolean)</td><td>Specifies if empty lines from value are 
 * ignored (yes) or not (no), by default yes</td></tr>
 * <tr><td>tolerance</td><td>(double)</td><td>Specifies error tolerance used in numeric comparisons,
 * by default 0.0</td></tr>
 * </table>
 * <br/><br/>
 * <b>DEVELOPERS NOTES:</b><br/><br/>
 * When using variables be careful because order of evaluation it is not necessarily 
 * the same of declaration.<br/><br/>
 * Instead a variable is evaluated when it is needed to resolve a test, usually in conditions at 
 * testable components (for example &lt;text&gt; components) but a variable can reference 
 * other variables and in that case those variables will be evaluated and their values used 
 * to resolve evaluation.<br/><br/>
 * When a variable already evaluated is referenced it return last evaluated result 
 * instead of evaluating itself again.<br/><br/> 
 * It is possible that a variable makes a reference to itself inside the transformation string, 
 * in that case instead of evaluating itself recursively (infinite loop), uses the value 
 * of the variable before transformations in any reference to itself and only after all evaluation 
 * is over applies the result as its new value.<br/><br/>
 * <b>IMPORTANT:</b> However it is not valid that a variable references itself 
 * outside the transformation string and doing it will throw an exception.<br/><br/>
 * A variable can be manually reseted from a java question class calling its resetValue() method 
 * and forcing it to evaluate itself again.<br/><br/>
 * But if you don't want to create your own java question class you can reset a variable 
 * using the task "resetvar" of &lt;task&gt; component.<br/><br/>
 * It is important to know that om.helper.uned.GenericQuestion class resets automatically all variables 
 * before evualuating rightness in each user's attempt to resolve the question.<br/><br/>
 * It is also possible to change a variable's value using the task "setvariable" 
 * of &lt;task&gt; component.
 */
public class VariableComponent extends QComponent implements Answerable
{
	/** 
	 * Text value's type. It simply compare the value of the variable component with the expected value
	 */
	public static final String VALUETYPE_TEXT="text";
	
	/** Regular expression's value type. */
	public static final String VALUETYPE_REGEXP="regexp";
	
	/** 
	 * PMatch value's type. It use the om.helper.PMatch class to compare the value 
	 * of the variable component with the expected value
	 */ 
	public static final String VALUETYPE_PMATCH="pmatch";
	
	/** Numeric value's type. */
	public static final String VALUETYPE_NUMERIC="numeric";
	
	/** Combination of numeric and PMatch value's types */
	public static final String VALUETYPE_NUMPMATCH="numpmatch";
	
	/** Character used in test property to open a selector for valuetype */
	private static final char VALUETYPE_SELECTOR_OPEN='['; 
	
	/** Character used in test property to close a selector for valuetype */
	private static final char VALUETYPE_SELECTOR_CLOSE=']'; 
	
	/**
	 * Character used to separate the numeric and Pmatch pattern of expected value in a 
	 * "numpmatch" test property
	 */
	private static final char VALUE_NUMPMATCH_SEPARATOR=',';
	
	/** 
	 * Default value of "set" property
	 */
	private static final String DEFAULT_SET="@";
	
	/** Identifier to get a literal attribute value */
	private static final String LITERAL_ATTRIBUTE_ID="attribute.";
	
	/**
	 * Character used to separate canvas, world, marker index and "x" or "y" to get x or y co-ordinate
	 * in that order.
	 * <br/><br/>
	 * World identifier is optional. If you use it co-ordinates will be converted to fit in that world,
	 * but if you don't use it you will get pixel co-ordinates.<br/><br/>
	 * <b>Examples</b>:<br/><br/>
	 * <table border="1">
	 * <tr><td><i>canvas1.w.0.x</i></td><td>x co-ordinate of the marker <i>0</i> 
	 * of canvas <i>canvas1</i>, expressed in the co-ordinate system of world <i>w</i></td></tr>
	 * <tr><td><i>canvas2.1.y</i></td><td>y co-ordinate of the marker <i>1</i> 
	 * of canvas <i>canvas2</i>, expressed in pixels</td></tr>
	 * </table>
	 */
	public static final char MARKER_IDENTIFIER_SEPARATOR='.';
	
	/**
	 * Name of "x" marker's attribute used to retrieve its X co-ordinate. 
	 */
	public static final String MARKER_ATTRIBUTE_X="x";
	
	/**
	 * Name of "y" marker's attribute used to retrieve its X co-ordinate. 
	 */
	public static final String MARKER_ATTRIBUTE_Y="y";
	
	/** 
	 * Character used within "set" property to open a transformation string used 
	 * to apply several tranformations to the value of the variable 
	 */
	private static final char SET_TRANSFORMATIONS_OPEN='['; 
	
	/** 
	 * Character used within "set" property to close a transformation string used 
	 * to apply several tranformations to the value of the variable 
	 */
	private static final char SET_TRANSFORMATIONS_CLOSE=']'; 
	
	/** Numeric code for comparison operator == */
	private static final int CMP_EQUAL=0;
	
	/** Numeric code for comparison operator != */
	private static final int CMP_NOT_EQUAL=1;
	
	/** Numeric code for comparison operator < */
	private static final int CMP_LESS=2;
	
	/** Numeric code for comparison operator <= */
	private static final int CMP_LESS_EQUAL=3;
	
	/** Numeric code for comparison operator > */
	private static final int CMP_GREATER=4;
	
	/** Numeric code for comparison operator >= */
	private static final int CMP_GREATER_EQUAL=5;
	
	/** Property name for the right answer (string) */
	public static final String PROPERTY_RIGHT="right";
	
	/** Property name for setting value to set the variable */
	public static final String PROPERTY_SET="set";
	
	/** Property name for the value's type (string) */
	public static final String PROPERTY_VALUETYPE="valuetype";
	
	/** 
	 * Property name for considering the answer to be case sensitive (yes) or case insensitive (no)
	 * (boolean)
	 */
	public static final String PROPERTY_CASESENSITIVE="casesensitive";
	
	/** 
	 * Property name for ignoring starting and ending whitespaces from answer when testing 
	 * if it is right (boolean)
	 */
	public static final String PROPERTY_TRIM="trim";
	
	/** Property name for ignoring all whitespaces from answer when testing if it is right (boolean) */
	public static final String PROPERTY_STRIP="strip";
	
	/** 
	 * Property name for considering together whitespaces from answer as a single whitespace when 
	 * testing if it is right (boolean)
	 */
	public static final String PROPERTY_SINGLESPACES="singlespaces";
	
	/** 
	 * Property name for considering new line characters as whitespaces when testing 
	 * if it is right (boolean)<br/><br/>
	 * Note that trim, strip and singlespaces properties are affected if this property is set to yes.
	 */
	public static final String PROPERTY_NEWLINESPACE="newlinespace";
	
	/**
	 * Property name for ignoring different text ocurrences (seperated by commas) from anwer 
	 * when testing if it is right (string) 
	 */
	public static final String PROPERTY_IGNORE="ignore";
	
	/**
	 * Property name for ignoring text ocurrences that match a regular expression (string)
	 */
	public static final String PROPERTY_IGNOREREGEXP="ignoreregexp";
	
	/**
	 * Property name for ignoring empty lines from answer when testing if it is right (boolean)
	 */
	public static final String PROPERTY_IGNOREEMPTYLINES="ignoreemptylines";
	
	/**
	 * Property name for error tolerance used in numeric comparisons
	 */
	public static final String PROPERTY_TOLERANCE="tolerance";
	
	/**
	 * Properties that need to initialize placeholders
	 */
	private static final String[] PROPERTIES_TO_INITIALIZE_PLACEHOLDERS=
	{
		PROPERTY_DISPLAY,PROPERTY_ENABLED,PROPERTY_LANG,PROPERTY_ANSWERENABLED,PROPERTY_RIGHT,
		PROPERTY_SET,PROPERTY_VALUETYPE,PROPERTY_CASESENSITIVE,PROPERTY_TRIM,PROPERTY_STRIP,
		PROPERTY_SINGLESPACES,PROPERTY_IGNORE,PROPERTY_IGNOREREGEXP,PROPERTY_IGNOREEMPTYLINES,
		PROPERTY_TOLERANCE
	};
	
	/** Map with restrictions of properties that need to initialize placeholders */
	private static final Map<String,String> PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS;
	static
	{
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS=new HashMap<String,String>();
		PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.put(PROPERTY_LANG,PROPERTYRESTRICTION_LANG);
	}
	
	/** Placeholders */
	private Map<String,String> placeholders=new HashMap<String,String>();
	
	/** Specific properties checks */
	private Map<String,PropertyCheck> checks=new HashMap<String,PropertyCheck>();
	
	/** Transformations to apply */
	private Transformations transformations=null;
	
	/** Saved value */
	private String value=null;
	
	/** @return Tag name (introspected; this may be replaced by a 1.5 annotation) */
	public static String getTagName()
	{
		return "variable";
	}
	
	/**
	 * Utility class to do a transformation 
	 */
	protected abstract class Transformation
	{
		public abstract String transform(String s);
	}
	
	/**
	 * Utility class to do a "trim" transformation 
	 */
	protected class Trim extends Transformation
	{
		@Override
		public String transform(String s)
		{
			return s.trim();
		}
	}
	
	/**
	 * Utility class to do a "strip" transformation 
	 */
	protected class Strip extends Transformation
	{
		@Override
		public String transform(String s)
		{
			return AnswerChecking.stripWhitespace(s);
		}
	}
	
	/**
	 * Utility class to do a "singlespace" transformation 
	 */
	protected class SingleSpace extends Transformation
	{
		@Override
		public String transform(String s)
		{
			return AnswerChecking.singledWhitespace(s,false);
		}
	}
	
	/**
	 * Utility class to do a "lowercase" transformation 
	 */
	protected class LowerCase extends Transformation
	{
		@Override
		public String transform(String s)
		{
			return s.toLowerCase();
		}
	}
	
	/**
	 * Utility class to do an "uppercase" transformation 
	 */
	protected class UpperCase extends Transformation
	{
		@Override
		public String transform(String s)
		{
			return s.toUpperCase();
		}
	}
	
	/**
	 * Utility class to do a "replace" transformation 
	 */
	protected class Replace extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		
		public Replace(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
		}
		
		@Override
		public String transform(String s)
		{
			return s.replace(v1.get(),v2.get());
		}
	}
	
	/**
	 * Utility class to do an "insert" transformation 
	 */
	protected class Insert extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private int pos;
		
		public Insert(TransformationArgument v)
		{
			this.v1=v;
			this.v2=null;
			this.pos=0;
		}
		
		public Insert(TransformationArgument v,int pos)
		{
			this.v1=v;
			this.v2=null;
			this.pos=pos;
		}
		
		public Insert(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.pos=0;
		}
		
		public Insert(TransformationArgument v1,TransformationArgument v2,int pos)
		{
			this.v1=v1;
			this.v2=v2;
			this.pos=pos;
		}
		
		@Override
		public String transform(String s)
		{
			StringBuffer value=new StringBuffer(s);
			if (v2==null)
			{
				if (pos>=s.length())
				{
					value.append(v1.get());
				}
				else
				{
					value.insert(pos,v1.get());
				}
			}
			else
			{
				if (pos<s.length())
				{
					int insertPos=s.indexOf(v2.get(),pos);
					if (insertPos!=-1)
					{
						value.insert(insertPos,v1.get());
					}
				}
			}
			return value.toString();
		}
	}
	
	/**
	 * Utility class to do an "append" transformation 
	 */
	protected class Append extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private int pos;
		
		public Append(TransformationArgument v)
		{
			this.v1=v;
			this.v2=null;
			this.pos=-1;
		}
		
		public Append(TransformationArgument v,int pos)
		{
			this.v1=v;
			this.v2=null;
			this.pos=pos;
		}
		
		public Append(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.pos=-1;
		}
		
		public Append(TransformationArgument v1,TransformationArgument v2,int pos)
		{
			this.v1=v1;
			this.v2=v2;
			this.pos=pos;
		}
		
		@Override
		public String transform(String s)
		{
			StringBuffer value=new StringBuffer(s);
			if (v2==null)
			{
				if (pos==-1 || pos>=s.length()-1)
				{
					value.append(v1.get());
				}
				else
				{
					value.insert(pos+1,v1.get());
				}
			}
			else
			{
				if (pos<s.length())
				{
					String s2=v2.get();
					int insertPos=s.indexOf(s2,pos);
					if (insertPos!=-1)
					{
						insertPos+=s2.length();
						if (insertPos>=s.length())
						{
							value.append(v1.get());
						}
						else
						{
							value.insert(insertPos,v1.get());
						}
					}
				}
			}
			return value.toString();
		}
	}
	
	/**
	 * Utility class to do a "delete" transformation 
	 */
	protected class Delete extends Transformation
	{
		private int pos1;
		private int pos2;
		private TransformationArgument v1;
		private TransformationArgument v2;
		private boolean leftInclusive;
		private boolean rightInclusive;
		
		public Delete(int pos)
		{
			this.pos1=pos;
			this.pos2=-1;
			this.v1=null;
			this.v2=null;
			this.leftInclusive=true;
			this.rightInclusive=false;
		}
		
		public Delete(int pos1,int pos2)
		{
			this.pos1=pos1;
			this.pos2=pos2;
			this.v1=null;
			this.v2=null;
			this.leftInclusive=true;
			this.rightInclusive=false;
		}
		
		public Delete(TransformationArgument v,boolean inclusive)
		{
			this.pos1=0;
			this.pos2=-1;
			this.v1=v;
			this.v2=null;
			this.leftInclusive=inclusive;
			this.rightInclusive=false;
		}
		
		public Delete(TransformationArgument v,int pos,boolean inclusive)
		{
			this.pos1=pos;
			this.pos2=-1;
			this.v1=v;
			this.v2=null;
			this.leftInclusive=inclusive;
			this.rightInclusive=false;
		}
		
		public Delete(TransformationArgument v1,TransformationArgument v2,boolean leftInclusive,
				boolean rightInclusive)
		{
			this.pos1=0;
			this.pos2=-1;
			this.v1=v1;
			this.v2=v2;
			this.leftInclusive=leftInclusive;
			this.rightInclusive=rightInclusive;
		}
		
		public Delete(TransformationArgument v1,TransformationArgument v2,int pos,
				boolean leftInclusive,boolean rightInclusive)
		{
			this.pos1=pos;
			this.pos2=-1;
			this.v1=v1;
			this.v2=v2;
			this.leftInclusive=leftInclusive;
			this.rightInclusive=rightInclusive;
		}
		
		@Override
		public String transform(String s)
		{
			StringBuffer value=new StringBuffer(s);
			if (v1==null)
			{
				if (pos1<s.length())
				{
					if (pos2==-1)
					{
						value.delete(pos1,s.length());
					}
					else if (pos2>pos1)
					{
						value.delete(pos1,pos2);
					}
				}
			}
			else
			{
				String s1=v1.get();
				int deleteFrom=s.indexOf(s1,pos1);
				if (deleteFrom!=-1)
				{
					if (!leftInclusive)
					{
						deleteFrom+=s1.length();
					}
					if (deleteFrom<s.length())
					{
						if (v2==null)
						{
							value.delete(deleteFrom,s.length());
						}
						else
						{
							String s2=v2.get();
							int deleteTo=s.indexOf(s2,deleteFrom);
							if (deleteTo!=-1)
							{
								if (rightInclusive)
								{
									deleteTo+=s2.length();
								}
								value.delete(deleteFrom,deleteTo);
							}
						}
					}
				}
			}
			return value.toString();
		}
	}
	
	/**
	 * Utility class to do a "substring" transformation 
	 */
	protected class Substring extends Transformation
	{
		private int pos1;
		private int pos2;
		private TransformationArgument v1;
		private TransformationArgument v2;
		private boolean leftInclusive;
		private boolean rightInclusive;
		
		public Substring(int pos)
		{
			this.pos1=pos;
			this.pos2=-1;
			this.v1=null;
			this.v2=null;
			this.leftInclusive=true;
			this.rightInclusive=false;
		}
		
		public Substring(int pos1,int pos2)
		{
			this.pos1=pos1;
			this.pos2=pos2;
			this.v1=null;
			this.v2=null;
			this.leftInclusive=true;
			this.rightInclusive=false;
		}
		
		public Substring(TransformationArgument v,boolean inclusive)
		{
			this.pos1=0;
			this.pos2=-1;
			this.v1=v;
			this.v2=null;
			this.leftInclusive=inclusive;
			this.rightInclusive=false;
		}
		
		public Substring(TransformationArgument v,int pos,boolean inclusive)
		{
			this.pos1=pos;
			this.pos2=-1;
			this.v1=v;
			this.v2=null;
			this.leftInclusive=inclusive;
			this.rightInclusive=false;
		}
		
		public Substring(TransformationArgument v1,TransformationArgument v2,boolean leftInclusive,
				boolean rightInclusive)
		{
			this.pos1=0;
			this.pos2=-1;
			this.v1=v1;
			this.v2=v2;
			this.leftInclusive=leftInclusive;
			this.rightInclusive=rightInclusive;
		}
		
		public Substring(TransformationArgument v1,TransformationArgument v2,int pos,
				boolean leftInclusive,boolean rightInclusive)
		{
			this.pos1=pos;
			this.pos2=-1;
			this.v1=v1;
			this.v2=v2;
			this.leftInclusive=leftInclusive;
			this.rightInclusive=rightInclusive;
		}
		
		@Override
		public String transform(String s)
		{
			String value="";
			if (v1==null)
			{
				if (pos1<s.length())
				{
					if (pos2==-1)
					{
						value=s.substring(pos1);
					}
					else if (pos2>pos1)
					{
						if (pos2>=s.length())
						{
							value=s.substring(pos1);
						}
						else
						{
							value=s.substring(pos1,pos2);
						}
					}
				}
			}
			else
			{
				String s1=v1.get();
				int substringFrom=s.indexOf(s1,pos1);
				if (substringFrom!=-1)
				{
					if (!leftInclusive)
					{
						substringFrom+=s1.length();
					}
					if (substringFrom<s.length())
					{
						if (v2==null)
						{
							value=s.substring(substringFrom);
						}
						else
						{
							String s2=v2.get();
							int substringTo=s.indexOf(s2,substringFrom);
							if (substringTo!=-1)
							{
								if (rightInclusive)
								{
									substringTo+=s2.length();
								}
								value=s.substring(substringFrom,substringTo);
							}
						}
					}
				}
			}
			return value;
		}
	}
	
	/**
	 * Utility class to count occurrences of a string, taking account possible exceptions 
	 */
	protected class Count extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		
		public Count(TransformationArgument v1)
		{
			this.v1=v1;
			this.v2=null;
		}
		
		public Count(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
		}

		@Override
		public String transform(String s)
		{
			int count=0;
			if (v2!=null)
			{
				List<String> exStrings=
						AnswerChecking.split(v2.get(),',',AnswerChecking.getEscapeSequences());
				for (String exString:exStrings)
				{
					s=s.replace(AnswerChecking.replaceEscapeChars(exString),"");
				}
			}
			int i=s.indexOf(v1.get());
			while (i!=-1)
			{
				count++;
				if (i+v1.get().length()<s.length())
				{
					i=s.indexOf(v1.get(),i+v1.get().length());
				}
				else
				{
					i=-1;
				}
			}
			return Integer.toString(count);
		}
	}
	
	/**
	 * Utility class to check if value is equal to another string value
	 */
	protected class EqualString extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public EqualString(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public EqualString(TransformationArgument v1,TransformationArgument v2,TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			if (s.equals(v1.get()))
			{
				value=v2.get();
			}
			else if (v3!=null)
			{
				value=v3.get();
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is not equal to another string value
	 */
	protected class NotEqualString extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public NotEqualString(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public NotEqualString(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			if (!s.equals(v1.get()))
			{
				value=v2.get();
			}
			else if (v3!=null)
			{
				value=v3.get();
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less than another string value
	 */
	protected class LessString extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public LessString(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public LessString(TransformationArgument v1,TransformationArgument v2,TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			if (s.compareTo(v1.get())<0)
			{
				value=v2.get();
			}
			else if (v3!=null)
			{
				value=v3.get();
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less or equal than another string value
	 */
	protected class LessEqualString extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public LessEqualString(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public LessEqualString(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			if (s.compareTo(v1.get())<=0)
			{
				value=v2.get();
			}
			else if (v3!=null)
			{
				value=v3.get();
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less than another string value
	 */
	protected class GreaterString extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public GreaterString(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public GreaterString(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			if (s.compareTo(v1.get())>0)
			{
				value=v2.get();
			}
			else if (v3!=null)
			{
				value=v3.get();
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less or equal than another string value
	 */
	protected class GreaterEqualString extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public GreaterEqualString(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public GreaterEqualString(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			if (s.compareTo(v1.get())>=0)
			{
				value=v2.get();
			}
			else if (v3!=null)
			{
				value=v3.get();
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value received is a integer or not
	 */
	protected class CheckInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		
		public CheckInteger(TransformationArgument v,boolean checkIfIntOrNotInt)
		{
			if (checkIfIntOrNotInt)
			{
				this.v1=v;
				this.v2=null;
			}
			else
			{
				this.v1=null;
				this.v2=v;
			}
		}
		
		public CheckInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				Integer.parseInt(s);
				if (v1!=null)
				{
					value=v1.get();
				}
			}
			catch (NumberFormatException e)
			{
				if (v2!=null)
				{
					value=v2.get();
				}
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value received is a double or not
	 */
	protected class CheckDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		
		public CheckDouble(TransformationArgument v,boolean checkIfDoubleOrNotDouble)
		{
			if (checkIfDoubleOrNotDouble)
			{
				this.v1=v;
				this.v2=null;
			}
			else
			{
				this.v1=null;
				this.v2=v;
			}
		}
		
		public CheckDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				Double.parseDouble(s);
				if (v1!=null)
				{
					value=v1.get();
				}
			}
			catch (NumberFormatException e)
			{
				if (v2!=null)
				{
					value=v2.get();
				}
			}
			return value;
		}
	}
	
	/**
	 * Utility class to add an integer value to the value supposing it is also a valid integer, 
	 * if some of values passed are not valid integers then value is not changed
	 */
	protected class AddInteger extends Transformation
	{
		private TransformationArgument v;
		
		public AddInteger(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString(Integer.parseInt(s)+Integer.parseInt(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to substract an integer value to the value supposing it is also a valid integer, 
	 * if some of values passed are not valid integers then value is not changed
	 */
	protected class SubstractInteger extends Transformation
	{
		private TransformationArgument v;
		
		public SubstractInteger(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString(Integer.parseInt(s)-Integer.parseInt(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to multiply an integer value to the value supposing it is also a valid integer, 
	 * if some of values passed are not valid integers then value is not changed
	 */
	protected class MultiplyInteger extends Transformation
	{
		private TransformationArgument v;
		
		public MultiplyInteger(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString(Integer.parseInt(s)*Integer.parseInt(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to divide an integer value to the value supposing it is also a valid integer, 
	 * if some of values passed are not valid integers then value is not changed
	 */
	protected class DivideInteger extends Transformation
	{
		private TransformationArgument v;
		
		public DivideInteger(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString(Integer.parseInt(s)/Integer.parseInt(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to add a double value to the value supposing it is also a valid double, 
	 * if some of values passed are not valid doubles then value is not changed
	 */
	protected class AddDouble extends Transformation
	{
		private TransformationArgument v;
		
		public AddDouble(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Double.toString(Double.parseDouble(s)+Double.parseDouble(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to substract a double value to the value supposing it is also a valid double, 
	 * if some of values passed are not valid doubles then value is not changed
	 */
	protected class SubstractDouble extends Transformation
	{
		private TransformationArgument v;
		
		public SubstractDouble(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Double.toString(Double.parseDouble(s)-Double.parseDouble(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to multiply a double value to the value supposing it is also a valid double, 
	 * if some of values passed are not valid doubles then value is not changed
	 */
	protected class MultiplyDouble extends Transformation
	{
		private TransformationArgument v;
		
		public MultiplyDouble(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Double.toString(Double.parseDouble(s)*Double.parseDouble(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to divide a double value to the value supposing it is also a valid double, 
	 * if some of values passed are not valid doubles then value is not changed
	 */
	protected class DivideDouble extends Transformation
	{
		private TransformationArgument v;
		
		public DivideDouble(TransformationArgument v)
		{
			this.v=v;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Double.toString(Double.parseDouble(s)/Double.parseDouble(v.get()));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is equal to an integer value supposing it is also a valid integer, 
	 * if some of values passed are not valid integers then value is not changed
	 */
	protected class EqualInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public EqualInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public EqualInteger(TransformationArgument v1,TransformationArgument v2,TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Integer.parseInt(s)==Integer.parseInt(v1.get()))
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is not equal to an integer value supposing it is also a 
	 * valid integer, if some of values passed are not valid integers then value is not changed
	 */
	protected class NotEqualInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public NotEqualInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public NotEqualInteger(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Integer.parseInt(s)!=Integer.parseInt(v1.get()))
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less than an integer value supposing it is also a valid integer, 
	 * if some of values passed are not valid integers then value is not changed
	 */
	protected class LessInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public LessInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public LessInteger(TransformationArgument v1,TransformationArgument v2,TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Integer.parseInt(s)<Integer.parseInt(v1.get()))
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less or equal than an integer value supposing it is also 
	 * a valid integer, if some of values passed are not valid integers then value is not changed
	 */
	protected class LessEqualInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public LessEqualInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public LessEqualInteger(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Integer.parseInt(s)<=Integer.parseInt(v1.get()))
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is greater than an integer value supposing it is also 
	 * a valid integer, if some of values passed are not valid integers then value is not changed
	 */
	protected class GreaterInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public GreaterInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public GreaterInteger(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Integer.parseInt(s)>Integer.parseInt(v1.get()))
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is greater or equal than an integer value supposing it is also 
	 * a valid integer, if some of values passed are not valid integers then value is not changed
	 */
	protected class GreaterEqualInteger extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public GreaterEqualInteger(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public GreaterEqualInteger(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Integer.parseInt(s)>=Integer.parseInt(v1.get()))
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is equal to an double value supposing it is also a valid double, 
	 * if some of values passed are not valid doubles then value is not changed
	 */
	protected class EqualDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public EqualDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public EqualDouble(TransformationArgument v1,TransformationArgument v2,TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				double d=Double.parseDouble(s);
				double d1=Double.parseDouble(v1.get());
				if (d>=d1-getTolerance() && d<=d1+getTolerance())
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is not equal to a double value supposing it is also a 
	 * valid double, if some of values passed are not valid doubles then value is not changed
	 */
	protected class NotEqualDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public NotEqualDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public NotEqualDouble(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				double d=Double.parseDouble(s);
				double d1=Double.parseDouble(v1.get());
				if (d<d1-getTolerance() || d>d1+getTolerance())
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less than a double value supposing it is also a valid double, 
	 * if some of values passed are not valid doubles then value is not changed
	 */
	protected class LessDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public LessDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public LessDouble(TransformationArgument v1,TransformationArgument v2,TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Double.parseDouble(s)<Double.parseDouble(v1.get())+getTolerance())
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is less or equal than a double value supposing it is also 
	 * a valid double, if some of values passed are not valid doubles then value is not changed
	 */
	protected class LessEqualDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public LessEqualDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public LessEqualDouble(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Double.parseDouble(s)<=Double.parseDouble(v1.get())+getTolerance())
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is greater than a double value supposing it is also 
	 * a valid double, if some of values passed are not valid doubles then value is not changed
	 */
	protected class GreaterDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public GreaterDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public GreaterDouble(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Double.parseDouble(s)>Double.parseDouble(v1.get())-getTolerance())
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to check if value is greater or equal than a double value supposing it is also 
	 * a valid double, if some of values passed are not valid doubles then value is not changed
	 */
	protected class GreaterEqualDouble extends Transformation
	{
		private TransformationArgument v1;
		private TransformationArgument v2;
		private TransformationArgument v3;
		
		public GreaterEqualDouble(TransformationArgument v1,TransformationArgument v2)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=null;
		}
		
		public GreaterEqualDouble(TransformationArgument v1,TransformationArgument v2,
				TransformationArgument v3)
		{
			this.v1=v1;
			this.v2=v2;
			this.v3=v3;
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				if (Double.parseDouble(s)>=Double.parseDouble(v1.get())-getTolerance())
				{
					value=v2.get();
				}
				else if (v3!=null)
				{
					value=v3.get();
				}
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to convert a double value to the closest lower integer value, 
	 * if value passed is not a valid double then value is not changed
	 */
	protected class Floor extends Transformation
	{
		public Floor()
		{
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString((int)Math.floor(Double.parseDouble(s)));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to convert a double value to the closest integer value (can be lower or greater), 
	 * if value passed is not a valid double then value is not changed
	 */
	protected class Round extends Transformation
	{
		public Round()
		{
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString((int)Math.round(Double.parseDouble(s)));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to convert a double value to the closest greater integer value, 
	 * if value passed is not a valid double then value is not changed
	 */
	protected class Ceil extends Transformation
	{
		public Ceil()
		{
		}
		
		@Override
		public String transform(String s)
		{
			String value=s;
			try
			{
				value=Integer.toString((int)Math.ceil(Double.parseDouble(s)));
			}
			catch (NumberFormatException e)
			{
				value=s;
			}
			return value;
		}
	}
	
	/**
	 * Utility class to define an argument of a transformation
	 */
	protected abstract class TransformationArgument
	{
		public abstract String get();
	}
	
	/**
	 * Utility class to define a literal as argument of a transformation
	 */
	protected class LiteralArgument extends TransformationArgument
	{
		private String literal;
		
		public LiteralArgument(String literal)
		{
			this.literal=literal;
		}
		
		@Override
		public String get()
		{
			return literal;
		}
	}
	
	protected class VariableArgument extends TransformationArgument
	{
		private String variable;
		
		public VariableArgument(String variable)
		{
			this.variable=variable;
		}
		
		@Override
		public String get()
		{
			String value="";
			QComponent qc=null;
			try
			{
				StandardQuestion sq=(StandardQuestion)getQuestion();
				qc=sq.getComponent(variable);
				if (qc instanceof RandomComponent)
				{
					value=((RandomComponent)qc).getValue();
				}
				else if (qc instanceof ReplaceholderComponent)
				{
					try
					{
						value=((ReplaceholderComponent)qc).getReplacementValue();
					}
					catch (OmDeveloperException e)
					{
						throw new OmUnexpectedException(e.getMessage(),e);
					}
				}
				else if (qc instanceof VariableComponent)
				{
					value=((VariableComponent)qc).getValue();
				}
				else if (qc instanceof Answerable)
				{
					value=((Answerable)qc).getAnswerLine();
				}
			}
			catch (OmException e)
			{
				CanvasComponent canvas=getCanvas(variable);
				World world=getWorld(canvas,variable);
				Marker marker=getMarker(canvas,variable);
				value=getMarkerAttributeValue(marker,world,variable);
				if (value==null)
				{
					if (variable.startsWith(LITERAL_ATTRIBUTE_ID) && 
							variable.length()>LITERAL_ATTRIBUTE_ID.length())
					{
						String attributeValue=AnswerChecking.replaceEscapeChars(
								variable.substring(LITERAL_ATTRIBUTE_ID.length()));
						AttributeComponent literalAttribute=new AttributeComponent();
						try
						{
							literalAttribute.defineProperties();
						}
						catch (OmDeveloperException e1)
						{
							throw new OmUnexpectedException(e1.getMessage(),e1);
						}
						literalAttribute.setAttribute(attributeValue);
						value=literalAttribute.getAttributeValue();
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<variable>: Unrecognized argument: ");
						error.append(variable);
						throw new VariableFormatRuntimeException(error.toString());
					}
				}
			}
			return value;
		}
	}
	
	/**
	 * Utility class to parse the transformations string, store transformations 
	 * found as objects and can be also used to apply those transformations
	 */
	protected class Transformations
	{
		private String transformationString;
		private List<Transformation> transformations;
		
		public Transformations()
		{
			this.transformationString=null;
			this.transformations=null;
		}
		
		public void parse(String transformationString)
		{
			if (transformationString==null)
			{
				this.transformationString=null;
				this.transformations=new ArrayList<Transformation>();
			}
			else if (!transformationString.equals(this.transformationString))
			{
				this.transformationString=transformationString;
				this.transformations=new ArrayList<Transformation>();
				doParse();
			}
			// Note that if the transformation string has not been changed there is no need 
			// to parse it again
		}
		
		private void doParse()
		{
			int i=0;
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			while (i<transformationString.length())
			{
				char c=transformationString.charAt(i);
				int strCmp=-1;
				switch (c)
				{
					case 't':
						transformations.add(new Trim());
						i++;
						break;
					case 'x':
						transformations.add(new Strip());
						i++;
						break;
					case 'o':
						transformations.add(new SingleSpace());
						i++;
						break;
					case 'l':
						transformations.add(new LowerCase());
						i++;
						break;
					case 'u':
						transformations.add(new UpperCase());
						i++;
						break;
					case 'r':
						if (i+1<transformationString.length())
						{
							TransformationArgument v1=null;
							TransformationArgument v2=null;
							i++;
							int j=-1;
							char c2=transformationString.charAt(i);
							if (c2=='\"')
							{
								i++;
								j=AnswerChecking.indexOfCharacter(
										transformationString,'\"',i,escapeSequences);
								if (j!=-1)
								{
									v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							else if (c2!='#')
							{
								j=AnswerChecking.indexOfCharacter(
										transformationString,'@',i,escapeSequences);
								if (j!=-1)
								{
									v1=new VariableArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							if (v1==null)
							{
								throw new VariableFormatRuntimeException(
										"<variable>: incorrect format for the option r within the set property");
							}
							i=j+1;
							if (i>=transformationString.length() || transformationString.charAt(i)!=',')
							{
								throw new VariableFormatRuntimeException(
										"<variable>: incorrect format for the option r within the set property");
							}
							i++;
							if (i>=transformationString.length())
							{
								throw new VariableFormatRuntimeException(
										"<variable>: incorrect format for the option r within the set property");
							}
							c2=transformationString.charAt(i);
							if (c2=='\"')
							{
								i++;
								j=AnswerChecking.indexOfCharacter(
										transformationString,'\"',i,escapeSequences);
								if (j!=-1)
								{
									v2=new LiteralArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							else if (c2!='#')
							{
								j=AnswerChecking.indexOfCharacter(
										transformationString,'@',i,escapeSequences);
								if (j!=-1)
								{
									v2=new VariableArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							if (v2==null)
							{
								throw new VariableFormatRuntimeException(
										"<variable>: incorrect format for the option r within the set property");
							}
							transformations.add(new Replace(v1,v2));
							i=j+1;
						}
						else
						{
							throw new VariableFormatRuntimeException(
									"<variable>: incorrect format for the option r within the set property");
						}
						break;
					case 'i':
					case 'a':
						if (i+1<transformationString.length())
						{
							TransformationArgument v1=null;
							i++;
							int j=-1;
							char c2=transformationString.charAt(i);
							if (c2=='\"')
							{
								i++;
								j=AnswerChecking.indexOfCharacter(
										transformationString,'\"',i,escapeSequences);
								if (j!=-1)
								{
									v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							else if (c2!='#')
							{
								j=AnswerChecking.indexOfCharacter(
										transformationString,'@',i,escapeSequences);
								if (j!=-1)
								{
									v1=new VariableArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							if (v1==null)
							{
								StringBuffer error=new StringBuffer();
								error.append("<variable>: incorrect format for the option ");
								error.append(c);
								error.append(" within the set property");
								throw new VariableFormatRuntimeException(error.toString());
							}
							i=j+1;
							if (i>=transformationString.length() || transformationString.charAt(i)!=',')
							{
								if (c=='i')
								{
									transformations.add(new Insert(v1));
								}
								else if (c=='a')
								{
									transformations.add(new Append(v1));
								}
							}
							else
							{
								i++;
								if (i>=transformationString.length())
								{
									StringBuffer error=new StringBuffer();
									error.append("<variable>: incorrect format for the option ");
									error.append(c);
									error.append(" within the set property");
									throw new VariableFormatRuntimeException(error.toString());
								}
								c2=transformationString.charAt(i);
								if (c2=='#')
								{
									int pos=-1;
									i++;
									j=i;
									if (i<transformationString.length())
									{
										c2=transformationString.charAt(i);
										while (c2>='0' && c2<='9')
										{
											j++;
											if (j<transformationString.length())
											{
												c2=transformationString.charAt(j);
											}
											else
											{
												c2='\0';
											}
										}
										if (j>i)
										{
											try
											{
												pos=Integer.parseInt(transformationString.substring(i,j));
											}
											catch (NumberFormatException e)
											{
												pos=-1;
											}
										}
									}
									if (pos==-1)
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									if (c=='i')
									{
										transformations.add(new Insert(v1,pos));
									}
									else if (c=='a')
									{
										transformations.add(new Append(v1,pos));
									}
									i=j;
								}
								else
								{
									TransformationArgument v2=null;
									if (c2=='\"')
									{
										i++;
										j=AnswerChecking.indexOfCharacter(
												transformationString,'\"',i,escapeSequences);
										if (j!=-1)
										{
											v2=new LiteralArgument(AnswerChecking.replaceEscapeChars(
													transformationString.substring(i,j)));
										}
									}
									else
									{
										j=AnswerChecking.indexOfCharacter(
												transformationString,'@',i,escapeSequences);
										if (j!=-1)
										{
											v2=new VariableArgument(AnswerChecking.replaceEscapeChars(
													transformationString.substring(i,j)));
										}
									}
									if (v2==null)
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									i=j+1;
									if (i>=transformationString.length() || 
											transformationString.charAt(i)!=',')
									{
										if (c=='i')
										{
											transformations.add(new Insert(v1,v2));
										}
										else if (c=='a')
										{
											transformations.add(new Append(v1,v2));
										}
									}
									else
									{
										int pos=-1;
										i++;
										j=i;
										if (i<transformationString.length())
										{
											c2=transformationString.charAt(i);
											while (c2>='0' && c2<='9')
											{
												j++;
												if (j<transformationString.length())
												{
													c2=transformationString.charAt(j);
												}
												else
												{
													c2='\0';
												}
											}
											if (j>i)
											{
												try
												{
													pos=Integer.parseInt(
															transformationString.substring(i,j));
												}
												catch (NumberFormatException e)
												{
													pos=-1;
												}
											}
										}
										if (pos==-1)
										{
											StringBuffer error=new StringBuffer();
											error.append("<variable>: incorrect format for the option ");
											error.append(c);
											error.append(" within the set property");
											throw new VariableFormatRuntimeException(error.toString());
										}
										if (c=='i')
										{
											transformations.add(new Insert(v1,v2,pos));
										}
										else if (c=='a')
										{
											transformations.add(new Append(v1,v2,pos));
										}
										i=j;
									}
								}
							}
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: incorrect format for the option ");
							error.append(c);
							error.append(" within the set property");
							throw new VariableFormatRuntimeException(error.toString());
						}
						break;
					case 'd':
					case 's':
						if (i+1<transformationString.length())
						{
							i++;
							char c2=transformationString.charAt(i);
							if (c2=='#')
							{
								int pos1=-1;
								i++;
								int j=i;
								if (i<transformationString.length())
								{
									c2=transformationString.charAt(i);
									while (c2>='0' && c2<='9')
									{
										j++;
										if (j<transformationString.length())
										{
											c2=transformationString.charAt(j);
										}
										else
										{
											c2='\0';
										}
									}
									if (j>i)
									{
										try
										{
											pos1=Integer.parseInt(transformationString.substring(i,j));
										}
										catch (NumberFormatException e)
										{
											pos1=-1;
										}
									}
								}
								if (pos1==-1)
								{
									StringBuffer error=new StringBuffer();
									error.append("<variable>: incorrect format for the option ");
									error.append(c);
									error.append(" within the set property");
									throw new VariableFormatRuntimeException(error.toString());
								}
								i=j;
								if (i>=transformationString.length() || 
										transformationString.charAt(i)!=',')
								{
									if (c=='d')
									{
										transformations.add(new Delete(pos1));
									}
									else if (c=='s')
									{
										transformations.add(new Substring(pos1));
									}
								}
								else
								{
									int pos2=-1;
									i++;
									if (i>=transformationString.length() || 
											transformationString.charAt(i)!='#')
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									i++;
									j=i;
									if (i<transformationString.length())
									{
										c2=transformationString.charAt(i);
										while (c2>='0' && c2<='9')
										{
											j++;
											if (j<transformationString.length())
											{
												c2=transformationString.charAt(j);
											}
											else
											{
												c2='\0';
											}
										}
										if (j>i)
										{
											try
											{
												pos2=Integer.parseInt(
														transformationString.substring(i,j));
											}
											catch (NumberFormatException e)
											{
												pos2=-1;
											}
										}
									}
									if (pos2==-1)
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									if (c=='d')
									{
										transformations.add(new Delete(pos1,pos2));
									}
									else if (c=='s')
									{
										transformations.add(new Substring(pos1,pos2));
									}
									i=j;
								}
							}
							else
							{
								TransformationArgument v1=null;
								boolean leftInclusive=true;
								boolean rightInclusive=false;
								int j=-1;
								if (c2=='(' || c2=='{')
								{
									if (c2=='(')
									{
										leftInclusive=false;
									}
									i++;
									if (i<transformationString.length())
									{
										c2=transformationString.charAt(i);
									}
									else
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
								}
								if (c2==')' || c2=='}')
								{
									if (c2=='}')
									{
										rightInclusive=true;
									}
									i++;
									if (i<transformationString.length())
									{
										c2=transformationString.charAt(i);
									}
									else
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
								}
								if (c2=='\"')
								{
									i++;
									j=AnswerChecking.indexOfCharacter(
											transformationString,'\"',i,escapeSequences);
									if (j!=-1)
									{
										v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
												transformationString.substring(i,j)));
									}
								}
								else if (c2!='#')
								{
									j=AnswerChecking.indexOfCharacter(
											transformationString,'@',i,escapeSequences);
									if (j!=-1)
									{
										v1=new VariableArgument(AnswerChecking.replaceEscapeChars(
												transformationString.substring(i,j)));
									}
								}
								if (v1==null)
								{
									StringBuffer error=new StringBuffer();
									error.append("<variable>: incorrect format for the option ");
									error.append(c);
									error.append(" within the set property");
									throw new VariableFormatRuntimeException(error.toString());
								}
								i=j+1;
								if (i>=transformationString.length() || 
										transformationString.charAt(i)!=',')
								{
									if (c=='d')
									{
										transformations.add(new Delete(v1,leftInclusive));
									}
									else if (c=='s')
									{
										transformations.add(new Substring(v1,leftInclusive));
									}
								}
								else
								{
									i++;
									if (i<transformationString.length())
									{
										c2=transformationString.charAt(i);
										if (c2=='#')
										{
											int pos1=-1;
											i++;
											j=i;
											if (i<transformationString.length())
											{
												c2=transformationString.charAt(i);
												while (c2>='0' && c2<='9')
												{
													j++;
													if (j<transformationString.length())
													{
														c2=transformationString.charAt(j);
													}
													else
													{
														c2='\0';
													}
												}
												if (j>i)
												{
													try
													{
														pos1=Integer.parseInt(
																transformationString.substring(i,j));
													}
													catch (NumberFormatException e)
													{
														pos1=-1;
													}
												}
											}
											if (pos1==-1)
											{
												StringBuffer error=new StringBuffer();
												error.append("<variable>: incorrect format for the option ");
												error.append(c);
												error.append(" within the set property");
												throw new VariableFormatRuntimeException(
														error.toString());
											}
											if (c=='d')
											{
												transformations.add(new Delete(v1,pos1,leftInclusive));
											}
											else if (c=='s')
											{
												transformations.add(
														new Substring(v1,pos1,leftInclusive));
											}
											i=j;
										}
										else
										{
											TransformationArgument v2=null;
											if (c2=='\"')
											{
												i++;
												j=AnswerChecking.indexOfCharacter(
														transformationString,'\"',i,escapeSequences);
												if (j!=-1)
												{
													v2=new LiteralArgument(
															AnswerChecking.replaceEscapeChars(
															transformationString.substring(i,j)));
												}
											}
											else
											{
												j=AnswerChecking.indexOfCharacter(
														transformationString,'@',i,escapeSequences);
												if (j!=-1)
												{
													v2=new VariableArgument(
															AnswerChecking.replaceEscapeChars(
															transformationString.substring(i,j)));
												}
											}
											if (v2==null)
											{
												StringBuffer error=new StringBuffer();
												error.append("<variable>: incorrect format for the option ");
												error.append(c);
												error.append(" within the set property");
												throw new VariableFormatRuntimeException(
														error.toString());
											}
											i=j+1;
											if (i>=transformationString.length() || 
													transformationString.charAt(i)!=',')
											{
												if (c=='d')
												{
													transformations.add(new Delete(
															v1,v2,leftInclusive,rightInclusive));
												}
												else if (c=='s')
												{
													transformations.add(new Substring(
															v1,v2,leftInclusive,rightInclusive));
												}
											}
											else
											{
												int pos1=-1;
												i++;
												if (i>=transformationString.length() || 
														transformationString.charAt(i)!='#')
												{
													StringBuffer error=new StringBuffer();
													error.append("<variable>: incorrect format for the option ");
													error.append(c);
													error.append(" within the set property");
													throw new VariableFormatRuntimeException(
															error.toString());
												}
												i++;
												j=i;
												if (i<transformationString.length())
												{
													c2=transformationString.charAt(i);
													while (c2>='0' && c2<='9')
													{
														j++;
														if (j<transformationString.length())
														{
															c2=transformationString.charAt(j);
														}
														else
														{
															c2='\0';
														}
													}
													if (j>i)
													{
														try
														{
															pos1=Integer.parseInt(
																	transformationString.substring(i,j));
														}
														catch (NumberFormatException e)
														{
															pos1=-1;
														}
													}
												}
												if (pos1==-1)
												{
													StringBuffer error=new StringBuffer();
													error.append("<variable>: ");
													error.append("incorrect format for the option ");
													error.append(c);
													error.append(" within the set property");
													throw new VariableFormatRuntimeException(
															error.toString());
												}
												if (c=='d')
												{
													transformations.add(new Delete(
															v1,v2,pos1,leftInclusive,rightInclusive));
												}
												else if (c=='s')
												{
													transformations.add(new Substring(
															v1,v2,pos1,leftInclusive,rightInclusive));
												}
												i=j;
											}
										}
									}
									else
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the option ");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
								}
							}
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: incorrect format for the option ");
							error.append(c);
							error.append(" within the set property");
							throw new VariableFormatRuntimeException(error.toString());
						}
						break;
					case 'c':
						i++;
						if (i<transformationString.length())
						{
							TransformationArgument v1=null;
							int j=-1;
							char c2=transformationString.charAt(i);
							if (c2=='\"')
							{
								i++;
								j=AnswerChecking.indexOfCharacter(
										transformationString,'\"',i,escapeSequences);
								if (j!=-1)
								{
									v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							else if (c2!='#')
							{
								j=AnswerChecking.indexOfCharacter(
										transformationString,'@',i,escapeSequences);
								if (j!=-1)
								{
									v1=new VariableArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							if (v1==null)
							{
								throw new VariableFormatRuntimeException(
										"<variable>: incorrect format for the option c within the set property");
							}
							i=j+1;
							if (i<transformationString.length() && 
									transformationString.charAt(i)==',')
							{
								i++;
								if (i<transformationString.length())
								{
									TransformationArgument v2=null;
									j=-1;
									c2=transformationString.charAt(i);
									if (c2=='\"')
									{
										i++;
										j=AnswerChecking.indexOfCharacter(
												transformationString,'\"',i,escapeSequences);
										if (j!=-1)
										{
											v2=new LiteralArgument(transformationString.substring(i,j));
										}
									}
									else if (c2!='#')
									{
										j=AnswerChecking.indexOfCharacter(
												transformationString,'@',i,escapeSequences);
										if (j!=-1)
										{
											v2=new VariableArgument(transformationString.substring(i,j));
										}
									}
									if (v2==null)
									{
										throw new VariableFormatRuntimeException(
												"<variable>: incorrect format for the option c within the set property");
									}
									i=j+1;
									transformations.add(new Count(v1,v2));
								}
								else
								{
									StringBuffer error=new StringBuffer();
									error.append("<variable>: incorrect format for the numeric option n");
									error.append(c);
									error.append(" within the set property");
									throw new VariableFormatRuntimeException(error.toString());
								}
							}
							else
							{
								transformations.add(new Count(v1));
							}
						}
						else
						{
							throw new VariableFormatRuntimeException(
									"<variable>: incorrect format for the option c within the set property");
						}
						break;
					case 'e':
						i++;
						strCmp=CMP_EQUAL;
						break;
					case 'm':
						i++;
						strCmp=CMP_NOT_EQUAL;
						break;
					case 'k':
						i++;
						if (i<transformationString.length() && transformationString.charAt(i)=='e')
						{
							strCmp=CMP_LESS_EQUAL;
							i++;
						}
						else
						{
							strCmp=CMP_LESS;
						}
						break;
					case 'g':
						i++;
						if (i<transformationString.length() && transformationString.charAt(i)=='e')
						{
							strCmp=CMP_GREATER_EQUAL;
							i++;
						}
						else
						{
							strCmp=CMP_GREATER;
						}
						break;
					case 'n':
						i++;
						if (i<transformationString.length())
						{
							c=transformationString.charAt(i);
							int numCmp=-1;
							switch (c)
							{
								case 'i':
								case 'f':
									boolean checkIfOrNot=true;
									i++;
									if (i<transformationString.length() && 
											transformationString.charAt(i)=='!')
									{
										checkIfOrNot=false;
										i++;
									}
									if (i<transformationString.length())
									{
										TransformationArgument v1=null;
										int j=-1;
										char c2=transformationString.charAt(i);
										if (c2=='\"')
										{
											i++;
											j=AnswerChecking.indexOfCharacter(
													transformationString,'\"',i,escapeSequences);
											if (j!=-1)
											{
												v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
														transformationString.substring(i,j)));
											}
										}
										else if (c2!='#')
										{
											j=AnswerChecking.indexOfCharacter(
													transformationString,'@',i,escapeSequences);
											if (j!=-1)
											{
												v1=new VariableArgument(
														AnswerChecking.replaceEscapeChars(
														transformationString.substring(i,j)));
											}
										}
										if (v1==null)
										{
											StringBuffer error=new StringBuffer();
											error.append("<variable>: incorrect format for the numeric option n");
											error.append(c);
											error.append(" within the set property");
											throw new VariableFormatRuntimeException(error.toString());
										}
										i=j+1;
										if (i<transformationString.length() && 
												transformationString.charAt(i)==',')
										{
											if (checkIfOrNot)
											{
												i++;
												if (i<transformationString.length())
												{
													TransformationArgument v2=null;
													j=-1;
													c2=transformationString.charAt(i);
													if (c2=='\"')
													{
														i++;
														j=AnswerChecking.indexOfCharacter(
																transformationString,'\"',i,
																escapeSequences);
														if (j!=-1)
														{
															v2=new LiteralArgument(
																	AnswerChecking.replaceEscapeChars(
																	transformationString.substring(i,j)));
														}
													}
													else if (c2!='#')
													{
														j=AnswerChecking.indexOfCharacter(
																transformationString,'@',i,
																escapeSequences);
														if (j!=-1)
														{
															v2=new VariableArgument(
																	AnswerChecking.replaceEscapeChars(
																	transformationString.substring(i,j)));
														}
													}
													if (v2==null)
													{
														StringBuffer error=new StringBuffer();
														error.append("<variable>: incorrect format for the numeric option n");
														error.append(c);
														error.append(" within the set property");
														throw new VariableFormatRuntimeException(
																error.toString());
													}
													i=j+1;
													if (c=='i')
													{
														transformations.add(new CheckInteger(v1,v2));
													}
													else if (c=='f')
													{
														transformations.add(new CheckDouble(v1,v2));
													}
												}
												else
												{
													StringBuffer error=new StringBuffer();
													error.append("<variable>: incorrect format for the numeric option n");
													error.append(c);
													error.append(" within the set property");
													throw new VariableFormatRuntimeException(
															error.toString());
												}
											}
											else
											{
												StringBuffer error=new StringBuffer();
												error.append("<variable>: incorrect format for the numeric option n");
												error.append(c);
												error.append(" within the set property");
												throw new VariableFormatRuntimeException(
														error.toString());
											}
										}
										if (c=='i')
										{
											transformations.add(new CheckInteger(v1,checkIfOrNot));
										}
										else if (c=='f')
										{
											transformations.add(new CheckDouble(v1,checkIfOrNot));
										}
									}
									else
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the numeric option n");
										error.append(c);
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									break;
								case 'a':
								case 's':
								case 'm':
								case 'd':
									boolean intOp=false;
									i++;
									if (i<transformationString.length() && 
											transformationString.charAt(i)=='i')
									{
										intOp=true;
										i++;
									}
									if (i<transformationString.length())
									{
										TransformationArgument v=null;
										int j=-1;
										char c2=transformationString.charAt(i);
										if (c2=='\"')
										{
											i++;
											j=AnswerChecking.indexOfCharacter(
													transformationString,'\"',i,escapeSequences);
											if (j!=-1)
											{
												v=new LiteralArgument(AnswerChecking.replaceEscapeChars(
														transformationString.substring(i,j)));
											}
										}
										else if (c2!='#')
										{
											j=AnswerChecking.indexOfCharacter(
													transformationString,'@',i,escapeSequences);
											if (j!=-1)
											{
												v=new VariableArgument(AnswerChecking.replaceEscapeChars(
														transformationString.substring(i,j)));
											}
										}
										if (v==null)
										{
											StringBuffer error=new StringBuffer();
											error.append("<variable>: incorrect format for the numeric option n");
											error.append(c);
											if (intOp)
											{
												error.append('i');
											}
											error.append(" within the set property");
											throw new VariableFormatRuntimeException(error.toString());
										}
										i=j+1;
										if (intOp)
										{
											switch (c)
											{
												case 'a':
													transformations.add(new AddInteger(v));
													break;
												case 's':
													transformations.add(new SubstractInteger(v));
													break;
												case 'm':
													transformations.add(new MultiplyInteger(v));
													break;
												case 'd':
													transformations.add(new DivideInteger(v));
													break;
											}
										}
										else
										{
											switch (c)
											{
												case 'a':
													transformations.add(new AddDouble(v));
													break;
												case 's':
													transformations.add(new SubstractDouble(v));
													break;
												case 'm':
													transformations.add(new MultiplyDouble(v));
													break;
												case 'd':
													transformations.add(new DivideDouble(v));
													break;
											}
										}
									}
									else
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the numeric option n");
										error.append(c);
										if (intOp)
										{
											error.append('i');
										}
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									break;
								case 'e':
									i++;
									numCmp=CMP_EQUAL;
									break;
								case 'n':
									i++;
									numCmp=CMP_NOT_EQUAL;
									break;
								case 'l':
									i++;
									if (i<transformationString.length() && 
											transformationString.charAt(i)=='e')
									{
										numCmp=CMP_LESS_EQUAL;
										i++;
									}
									else
									{
										numCmp=CMP_LESS;
									}
									break;
								case 'g':
									i++;
									if (i<transformationString.length() && 
											transformationString.charAt(i)=='e')
									{
										numCmp=CMP_GREATER_EQUAL;
										i++;
									}
									else
									{
										numCmp=CMP_GREATER;
									}
									break;
								case 't':
									i++;
									transformations.add(new Floor());
									break;
								case 'r':
									i++;
									transformations.add(new Round());
									break;
								case 'c':
									i++;
									transformations.add(new Ceil());
									break;
								default:
									StringBuffer error=new StringBuffer();
									error.append("<variable>: unknown numeric option n");
									error.append(c);
									error.append(" found within the set property");
									throw new VariableFormatRuntimeException(error.toString());
							}
							if (numCmp!=-1)
							{
								boolean intOp=false;
								if (i<transformationString.length() && 
										transformationString.charAt(i)=='i')
								{
									intOp=true;
									i++;
								}
								if (i<transformationString.length())
								{
									TransformationArgument v1=null;
									TransformationArgument v2=null;
									int j=-1;
									char c2=transformationString.charAt(i);
									if (c2=='\"')
									{
										i++;
										j=AnswerChecking.indexOfCharacter(
												transformationString,'\"',i,escapeSequences);
										if (j!=-1)
										{
											v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
													transformationString.substring(i,j)));
										}
									}
									else if (c2!='#')
									{
										j=AnswerChecking.indexOfCharacter(
												transformationString,'@',i,escapeSequences);
										if (j!=-1)
										{
											v1=new VariableArgument(AnswerChecking.replaceEscapeChars(
													transformationString.substring(i,j)));
										}
									}
									if (v1==null)
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the numeric option ");
										switch (numCmp)
										{
											case CMP_EQUAL:
												error.append("ne");
												break;
											case CMP_NOT_EQUAL:
												error.append("nn");
												break;
											case CMP_LESS:
												error.append("nl");
												break;
											case CMP_LESS_EQUAL:
												error.append("nle");
												break;
											case CMP_GREATER:
												error.append("ng");
												break;
											case CMP_GREATER_EQUAL:
												error.append("nge");
												break;
										}
										if (intOp)
										{
											error.append('i');
										}
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									i=j+1;
									if (i>=transformationString.length() || 
											transformationString.charAt(i)!=',')
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the numeric option ");
										switch (numCmp)
										{
											case CMP_EQUAL:
												error.append("ne");
												break;
											case CMP_NOT_EQUAL:
												error.append("nn");
												break;
											case CMP_LESS:
												error.append("nl");
												break;
											case CMP_LESS_EQUAL:
												error.append("nle");
												break;
											case CMP_GREATER:
												error.append("ng");
												break;
											case CMP_GREATER_EQUAL:
												error.append("nge");
												break;
										}
										if (intOp)
										{
											error.append('i');
										}
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									i++;
									if (i>=transformationString.length())
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the numeric option ");
										switch (numCmp)
										{
											case CMP_EQUAL:
												error.append("ne");
												break;
											case CMP_NOT_EQUAL:
												error.append("nn");
												break;
											case CMP_LESS:
												error.append("nl");
												break;
											case CMP_LESS_EQUAL:
												error.append("nle");
												break;
											case CMP_GREATER:
												error.append("ng");
												break;
											case CMP_GREATER_EQUAL:
												error.append("nge");
												break;
										}
										if (intOp)
										{
											error.append('i');
										}
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									c2=transformationString.charAt(i);
									if (c2=='\"')
									{
										i++;
										j=AnswerChecking.indexOfCharacter(
												transformationString,'\"',i,escapeSequences);
										if (j!=-1)
										{
											v2=new LiteralArgument(AnswerChecking.replaceEscapeChars(
													transformationString.substring(i,j)));
										}
									}
									else if (c2!='#')
									{
										j=AnswerChecking.indexOfCharacter(
												transformationString,'@',i,escapeSequences);
										if (j!=-1)
										{
											v2=new VariableArgument(AnswerChecking.replaceEscapeChars(
													transformationString.substring(i,j)));
										}
									}
									if (v2==null)
									{
										StringBuffer error=new StringBuffer();
										error.append("<variable>: incorrect format for the numeric option ");
										switch (numCmp)
										{
											case CMP_EQUAL:
												error.append("ne");
												break;
											case CMP_NOT_EQUAL:
												error.append("nn");
												break;
											case CMP_LESS:
												error.append("nl");
												break;
											case CMP_LESS_EQUAL:
												error.append("nle");
												break;
											case CMP_GREATER:
												error.append("ng");
												break;
											case CMP_GREATER_EQUAL:
												error.append("nge");
												break;
										}
										if (intOp)
										{
											error.append('i');
										}
										error.append(" within the set property");
										throw new VariableFormatRuntimeException(error.toString());
									}
									i=j+1;
									if (i>=transformationString.length() || 
											transformationString.charAt(i)!=',')
									{
										if (intOp)
										{
											switch (numCmp)
											{
												case CMP_EQUAL:
													transformations.add(new EqualInteger(v1,v2));
													break;
												case CMP_NOT_EQUAL:
													transformations.add(new NotEqualInteger(v1,v2));
													break;
												case CMP_LESS:
													transformations.add(new LessInteger(v1,v2));
													break;
												case CMP_LESS_EQUAL:
													transformations.add(new LessEqualInteger(v1,v2));
													break;
												case CMP_GREATER:
													transformations.add(new GreaterInteger(v1,v2));
													break;
												case CMP_GREATER_EQUAL:
													transformations.add(new GreaterEqualInteger(v1,v2));
													break;
											}
										}
										else
										{
											switch (numCmp)
											{
												case CMP_EQUAL:
													transformations.add(new EqualDouble(v1,v2));
													break;
												case CMP_NOT_EQUAL:
													transformations.add(new NotEqualDouble(v1,v2));
													break;
												case CMP_LESS:
													transformations.add(new LessDouble(v1,v2));
													break;
												case CMP_LESS_EQUAL:
													transformations.add(new LessEqualDouble(v1,v2));
													break;
												case CMP_GREATER:
													transformations.add(new GreaterDouble(v1,v2));
													break;
												case CMP_GREATER_EQUAL:
													transformations.add(new GreaterEqualDouble(v1,v2));
													break;
											}
										}
									}
									else
									{
										TransformationArgument v3=null;
										i++;
										if (i>=transformationString.length())
										{
											StringBuffer error=new StringBuffer();
											error.append("<variable>: incorrect format for the numeric option ");
											switch (numCmp)
											{
												case CMP_EQUAL:
													error.append("ne");
													break;
												case CMP_NOT_EQUAL:
													error.append("nn");
													break;
												case CMP_LESS:
													error.append("nl");
													break;
												case CMP_LESS_EQUAL:
													error.append("nle");
													break;
												case CMP_GREATER:
													error.append("ng");
													break;
												case CMP_GREATER_EQUAL:
													error.append("nge");
													break;
											}
											if (intOp)
											{
												error.append('i');
											}
											error.append(" within the set property");
											throw new VariableFormatRuntimeException(error.toString());
										}
										c2=transformationString.charAt(i);
										if (c2=='\"')
										{
											i++;
											j=AnswerChecking.indexOfCharacter(
													transformationString,'\"',i,escapeSequences);
											if (j!=-1)
											{
												v3=new LiteralArgument(AnswerChecking.replaceEscapeChars(
														transformationString.substring(i,j)));
											}
										}
										else if (c2!='#')
										{
											j=AnswerChecking.indexOfCharacter(
													transformationString,'@',i,escapeSequences);
											if (j!=-1)
											{
												v3=new VariableArgument(
														AnswerChecking.replaceEscapeChars(
														transformationString.substring(i,j)));
											}
										}
										if (v3==null)
										{
											StringBuffer error=new StringBuffer();
											error.append("<variable>: incorrect format for the numeric option ");
											switch (numCmp)
											{
												case CMP_EQUAL:
													error.append("ne");
													break;
												case CMP_NOT_EQUAL:
													error.append("nn");
													break;
												case CMP_LESS:
													error.append("nl");
													break;
												case CMP_LESS_EQUAL:
													error.append("nle");
													break;
												case CMP_GREATER:
													error.append("ng");
													break;
												case CMP_GREATER_EQUAL:
													error.append("nge");
													break;
											}
											if (intOp)
											{
												error.append('i');
											}
											error.append(" within the set property");
											throw new VariableFormatRuntimeException(error.toString());
										}
										i=j+1;
										if (intOp)
										{
											switch (numCmp)
											{
												case CMP_EQUAL:
													transformations.add(new EqualInteger(v1,v2,v3));
													break;
												case CMP_NOT_EQUAL:
													transformations.add(new NotEqualInteger(v1,v2,v3));
													break;
												case CMP_LESS:
													transformations.add(new LessInteger(v1,v2,v3));
													break;
												case CMP_LESS_EQUAL:
													transformations.add(new LessEqualInteger(v1,v2,v3));
													break;
												case CMP_GREATER:
													transformations.add(new GreaterInteger(v1,v2,v3));
													break;
												case CMP_GREATER_EQUAL:
													transformations.add(
															new GreaterEqualInteger(v1,v2,v3));
													break;
											}
										}
										else
										{
											switch (numCmp)
											{
												case CMP_EQUAL:
													transformations.add(new EqualDouble(v1,v2,v3));
													break;
												case CMP_NOT_EQUAL:
													transformations.add(new NotEqualDouble(v1,v2,v3));
													break;
												case CMP_LESS:
													transformations.add(new LessDouble(v1,v2,v3));
													break;
												case CMP_LESS_EQUAL:
													transformations.add(new LessEqualDouble(v1,v2,v3));
													break;
												case CMP_GREATER:
													transformations.add(new GreaterDouble(v1,v2,v3));
													break;
												case CMP_GREATER_EQUAL:
													transformations.add(
															new GreaterEqualDouble(v1,v2,v3));
													break;
											}
										}
									}
								}
								else
								{
									StringBuffer error=new StringBuffer();
									error.append("<variable>: incorrect format for the numeric option ");
									switch (numCmp)
									{
										case CMP_EQUAL:
											error.append("ne");
											break;
										case CMP_NOT_EQUAL:
											error.append("nn");
											break;
										case CMP_LESS:
											error.append("nl");
											break;
										case CMP_LESS_EQUAL:
											error.append("nle");
											break;
										case CMP_GREATER:
											error.append("ng");
											break;
										case CMP_GREATER_EQUAL:
											error.append("nge");
											break;
									}
									if (intOp)
									{
										error.append('i');
									}
									error.append(" within the set property");
									throw new VariableFormatRuntimeException(error.toString());
								}
							}
						}
						else
						{
							throw new VariableFormatRuntimeException(
									"<variable>: incorrect format for a numeric option within the set property");
						}
						break;
					default:
						StringBuffer error=new StringBuffer();
						error.append("<variable>: unknown option ");
						error.append(c);
						error.append(" found within the set property");
						throw new VariableFormatRuntimeException(error.toString());
				}
				if (strCmp!=-1)
				{
					if (i<transformationString.length())
					{
						TransformationArgument v1=null;
						TransformationArgument v2=null;
						int j=-1;
						char c2=transformationString.charAt(i);
						if (c2=='\"')
						{
							i++;
							j=AnswerChecking.indexOfCharacter(
									transformationString,'\"',i,escapeSequences);
							if (j!=-1)
							{
								v1=new LiteralArgument(AnswerChecking.replaceEscapeChars(
										transformationString.substring(i,j)));
							}
						}
						else if (c2!='#')
						{
							j=AnswerChecking.indexOfCharacter(
									transformationString,'@',i,escapeSequences);
							if (j!=-1)
							{
								v1=new VariableArgument(AnswerChecking.replaceEscapeChars(
										transformationString.substring(i,j)));
							}
						}
						if (v1==null)
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: incorrect format for the option ");
							switch (strCmp)
							{
								case CMP_EQUAL:
									error.append("e");
									break;
								case CMP_NOT_EQUAL:
									error.append("m");
									break;
								case CMP_LESS:
									error.append("k");
									break;
								case CMP_LESS_EQUAL:
									error.append("ke");
									break;
								case CMP_GREATER:
									error.append("g");
									break;
								case CMP_GREATER_EQUAL:
									error.append("ge");
									break;
							}
							error.append(" within the set property");
							throw new VariableFormatRuntimeException(error.toString());
						}
						i=j+1;
						if (i>=transformationString.length() || 
								transformationString.charAt(i)!=',')
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: incorrect format for the option ");
							switch (strCmp)
							{
								case CMP_EQUAL:
									error.append("e");
									break;
								case CMP_NOT_EQUAL:
									error.append("m");
									break;
								case CMP_LESS:
									error.append("k");
									break;
								case CMP_LESS_EQUAL:
									error.append("ke");
									break;
								case CMP_GREATER:
									error.append("g");
									break;
								case CMP_GREATER_EQUAL:
									error.append("ge");
									break;
							}
							error.append(" within the set property");
							throw new VariableFormatRuntimeException(error.toString());
						}
						i++;
						if (i>=transformationString.length())
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: incorrect format for the option ");
							switch (strCmp)
							{
								case CMP_EQUAL:
									error.append("e");
									break;
								case CMP_NOT_EQUAL:
									error.append("m");
									break;
								case CMP_LESS:
									error.append("k");
									break;
								case CMP_LESS_EQUAL:
									error.append("ke");
									break;
								case CMP_GREATER:
									error.append("g");
									break;
								case CMP_GREATER_EQUAL:
									error.append("ge");
									break;
							}
							error.append(" within the set property");
							throw new VariableFormatRuntimeException(error.toString());
						}
						c2=transformationString.charAt(i);
						if (c2=='\"')
						{
							i++;
							j=AnswerChecking.indexOfCharacter(
									transformationString,'\"',i,escapeSequences);
							if (j!=-1)
							{
								v2=new LiteralArgument(AnswerChecking.replaceEscapeChars(
										transformationString.substring(i,j)));
							}
						}
						else if (c2!='#')
						{
							j=AnswerChecking.indexOfCharacter(
									transformationString,'@',i,escapeSequences);
							if (j!=-1)
							{
								v2=new VariableArgument(AnswerChecking.replaceEscapeChars(
										transformationString.substring(i,j)));
							}
						}
						if (v2==null)
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: incorrect format for the option ");
							switch (strCmp)
							{
								case CMP_EQUAL:
									error.append("e");
									break;
								case CMP_NOT_EQUAL:
									error.append("m");
									break;
								case CMP_LESS:
									error.append("k");
									break;
								case CMP_LESS_EQUAL:
									error.append("ke");
									break;
								case CMP_GREATER:
									error.append("g");
									break;
								case CMP_GREATER_EQUAL:
									error.append("ge");
									break;
							}
							error.append(" within the set property");
							throw new VariableFormatRuntimeException(error.toString());
						}
						i=j+1;
						if (i>=transformationString.length() || 
								transformationString.charAt(i)!=',')
						{
							switch (strCmp)
							{
								case CMP_EQUAL:
									transformations.add(new EqualString(v1,v2));
									break;
								case CMP_NOT_EQUAL:
									transformations.add(new NotEqualString(v1,v2));
									break;
								case CMP_LESS:
									transformations.add(new LessString(v1,v2));
									break;
								case CMP_LESS_EQUAL:
									transformations.add(new LessEqualString(v1,v2));
									break;
								case CMP_GREATER:
									transformations.add(new GreaterString(v1,v2));
									break;
								case CMP_GREATER_EQUAL:
									transformations.add(new GreaterEqualString(v1,v2));
									break;
							}
						}
						else
						{
							TransformationArgument v3=null;
							i++;
							if (i>=transformationString.length())
							{
								StringBuffer error=new StringBuffer();
								error.append("<variable>: incorrect format for the option ");
								switch (strCmp)
								{
									case CMP_EQUAL:
										error.append("e");
										break;
									case CMP_NOT_EQUAL:
										error.append("m");
										break;
									case CMP_LESS:
										error.append("k");
										break;
									case CMP_LESS_EQUAL:
										error.append("ke");
										break;
									case CMP_GREATER:
										error.append("g");
										break;
									case CMP_GREATER_EQUAL:
										error.append("ge");
										break;
								}
								error.append(" within the set property");
								throw new VariableFormatRuntimeException(error.toString());
							}
							c2=transformationString.charAt(i);
							if (c2=='\"')
							{
								i++;
								j=AnswerChecking.indexOfCharacter(
										transformationString,'\"',i,escapeSequences);
								if (j!=-1)
								{
									v3=new LiteralArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							else if (c2!='#')
							{
								j=AnswerChecking.indexOfCharacter(
										transformationString,'@',i,escapeSequences);
								if (j!=-1)
								{
									v3=new VariableArgument(AnswerChecking.replaceEscapeChars(
											transformationString.substring(i,j)));
								}
							}
							if (v3==null)
							{
								StringBuffer error=new StringBuffer();
								error.append("<variable>: incorrect format for the option ");
								switch (strCmp)
								{
									case CMP_EQUAL:
										error.append("e");
										break;
									case CMP_NOT_EQUAL:
										error.append("m");
										break;
									case CMP_LESS:
										error.append("k");
										break;
									case CMP_LESS_EQUAL:
										error.append("ke");
										break;
									case CMP_GREATER:
										error.append("g");
										break;
									case CMP_GREATER_EQUAL:
										error.append("ge");
										break;
								}
								error.append(" within the set property");
								throw new VariableFormatRuntimeException(error.toString());
							}
							i=j+1;
							switch (strCmp)
							{
								case CMP_EQUAL:
									transformations.add(new EqualString(v1,v2,v3));
									break;
								case CMP_NOT_EQUAL:
									transformations.add(new NotEqualString(v1,v2,v3));
									break;
								case CMP_LESS:
									transformations.add(new LessString(v1,v2,v3));
									break;
								case CMP_LESS_EQUAL:
									transformations.add(new LessEqualString(v1,v2,v3));
									break;
								case CMP_GREATER:
									transformations.add(new GreaterString(v1,v2,v3));
									break;
								case CMP_GREATER_EQUAL:
									transformations.add(new GreaterEqualString(v1,v2,v3));
									break;
							}
						}
					}
					else
					{
						StringBuffer error=new StringBuffer();
						error.append("<variable>: incorrect format for the option ");
						switch (strCmp)
						{
							case CMP_EQUAL:
								error.append("e");
								break;
							case CMP_NOT_EQUAL:
								error.append("m");
								break;
							case CMP_LESS:
								error.append("k");
								break;
							case CMP_LESS_EQUAL:
								error.append("ke");
								break;
							case CMP_GREATER:
								error.append("g");
								break;
							case CMP_GREATER_EQUAL:
								error.append("ge");
								break;
						}
						error.append(" within the set property");
						throw new VariableFormatRuntimeException(error.toString());
					}
				}
			}
		}
		
		public String transform(String s)
		{
			String value=s;
			if (transformations!=null)
			{
				for (Transformation t:transformations)
				{
					value=t.transform(value);
				}
			}
			return value;
		}
		
		public List<Transformation> getTransformations()
		{
			return transformations;
		}
	}
	
	@Override
	protected void defineProperties() throws OmDeveloperException
	{
		// First define properties needed by the superclass
		super.defineProperties();
		
		defineBoolean(PROPERTY_ANSWERENABLED);
		setBoolean(PROPERTY_ANSWERENABLED,false);
		
		defineString(PROPERTY_RIGHT);
		setString(PROPERTY_RIGHT,null);
		
		defineString(PROPERTY_SET);
		setString(PROPERTY_SET,DEFAULT_SET);
		
		defineString(PROPERTY_VALUETYPE);
		setString(PROPERTY_VALUETYPE,VALUETYPE_TEXT);
		
		defineBoolean(PROPERTY_CASESENSITIVE);
		setBoolean(PROPERTY_CASESENSITIVE,true);
		
		defineBoolean(PROPERTY_TRIM);
		setBoolean(PROPERTY_TRIM,true);
		
		defineBoolean(PROPERTY_STRIP);
		setBoolean(PROPERTY_STRIP,false);
		
		defineBoolean(PROPERTY_SINGLESPACES);
		setBoolean(PROPERTY_SINGLESPACES,true);
		
		defineBoolean(PROPERTY_NEWLINESPACE);
		setBoolean(PROPERTY_NEWLINESPACE,true);
		
		defineString(PROPERTY_IGNORE);
		setString(PROPERTY_IGNORE,null);
		
		defineString(PROPERTY_IGNOREREGEXP);
		setString(PROPERTY_IGNOREREGEXP,null);
		
		defineBoolean(PROPERTY_IGNOREEMPTYLINES);
		setBoolean(PROPERTY_IGNOREEMPTYLINES,true);
		
		defineDouble(PROPERTY_TOLERANCE);
		setDouble(PROPERTY_TOLERANCE,0.0);
	}
	
	@Override
	public boolean isAnswerEnabled()
	{
		try
		{
			return getBoolean(Answerable.PROPERTY_ANSWERENABLED);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	@Override
	public void setAnswerEnabled(boolean answerEnabled)
	{
		try
		{
			setBoolean(Answerable.PROPERTY_ANSWERENABLED,answerEnabled);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Right answer
	 */
	public String getRight()
	{
		try
		{
			String right=getString(PROPERTY_RIGHT);
			if (right!=null)
			{
				right=AnswerChecking.replaceEscapeChars(right);
			}
			return right;
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the right answer.
	 * @param right Right answer
	 */
	public void setRight(String right)
	{
		try
		{
			setString(PROPERTY_RIGHT,right);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Value to set the variable
	 */
	public String getSet()
	{
		try
		{
			return getString(PROPERTY_SET);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set the value to set the variable
	 * @param set Value to set the variable
	 */
	public void setSet(String set)
	{
		try
		{
			setString(PROPERTY_SET,set);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return Value's type
	 */
	public String getValueType()
	{
		try
		{
			return getString(PROPERTY_VALUETYPE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets the value's type.
	 * @param valuetype answer's type
	 */
	public void setValueType(String valuetype)
	{
		try
		{
			setString(PROPERTY_VALUETYPE,valuetype);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if considering the value to be case sensitive or false if is considered to be 
	 * case insensitive
	 */
	public boolean isCaseSensitive()
	{
		try
		{
			return getBoolean(PROPERTY_CASESENSITIVE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if the value is going to be considered case sensitive (true) or case insensitive (false)
	 * @param casesensitive true if the value is going to be considered case sensitive, 
	 * false if it is going to be considered case insensitive
	 */
	public void setCaseSensitive(boolean casesensitive)
	{
		try
		{
			setBoolean(PROPERTY_CASESENSITIVE,casesensitive);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if starting and ending whitespaces are ignored from value when testing 
	 * if it is right, false otherwise
	 */
	public boolean isTrim()
	{
		try
		{
			return getBoolean(PROPERTY_TRIM);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if starting and ending whitespaces are going to be ignored from value when testing 
	 * if it is right (true) or if they are not going to be ignored (false)
	 * @param trim true if starting and ending whitespaces are going to be ignored from value when testing 
	 * if it is right, false otherwise
	 */
	public void setTrim(boolean trim)
	{
		try
		{
			setBoolean(PROPERTY_TRIM,trim);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if all whitespaces are ignored from value when testing if it is right, 
	 * false otherwise
	 */
	public boolean isStrip()
	{
		try
		{
			return getBoolean(PROPERTY_STRIP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if all whitespaces are ignored from value when testing if it is right (true) or 
	 * if they are not going to be ignored (false)
	 * @param strip true if all whitespaces are ignored from value when testing if it is right,
	 * false otherwise
	 */
	public void setStrip(boolean strip)
	{
		try
		{
			setBoolean(PROPERTY_STRIP,strip);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if together whitespaces from value are considered as a single whitespace 
	 * when testing if it is right, false otherwise
	 */
	public boolean isSingleSpaces()
	{
		try
		{
			return getBoolean(PROPERTY_SINGLESPACES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if together whitespaces from value are considered to be a single whitespace 
	 * when testing if it is right (true) or if they are not considered to be a single whitespace (false)
	 * @param singlespaces true if if together whitespaces from value are considered 
	 * as a single whitespace, false otherwise
	 */
	public void setSingleSpaces(boolean singlespaces)
	{
		try
		{
			setBoolean(PROPERTY_SINGLESPACES,singlespaces);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/** 
	 * @return true if new line characters from value are considered as whitespaces when testing if 
	 * it is right, false otherwise
	 */
	public boolean isNewLineSpace()
	{
		try
		{
			return getBoolean(PROPERTY_NEWLINESPACE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if new line characters from value are considered to be whitespaces when testing if it is 
	 * right (true) or not (false).<br/><br/>
	 * Note that trim, strip and singlespaces properties are affected if this property is set to yes.
	 * @param newlinespace true if if new line characters from value are considered as whitespaces, 
	 * false otherwise
	 */
	public void setNewLineSpace(boolean newlinespace)
	{
		try
		{
			setBoolean(PROPERTY_NEWLINESPACE,newlinespace);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Text ocurrences (separated by commas) to ignore from value
	 */
	public String getIgnore()
	{
		try
		{
			return getString(PROPERTY_IGNORE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set text ocurrences (separated by commas) to ignore from value.
	 * @param ignore Text ocurrences (separated by commas) to ignore from value
	 */
	public void setIgnore(String ignore)
	{
		try
		{
			setString(PROPERTY_IGNORE,ignore);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Regular expression to ignore text ocurrences that match it
	 */
	public String getIgnoreRegExp()
	{
		try
		{
			return getString(PROPERTY_IGNOREREGEXP);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Set regular expression to ignore text ocurrences that match it.
	 * @param regExp Regular expression
	 */
	public void setIgnoreRegExp(String regExp)
	{
		try
		{
			setString(PROPERTY_IGNOREREGEXP,regExp);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return true if empty lines from value are ignored when testing if it is right, false otherwise
	 */
	public boolean isIgnoreEmptyLines()
	{
		try
		{
			return getBoolean(PROPERTY_IGNOREEMPTYLINES);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * Sets if empty lines from value are ignored when testing if it is right (true) or not (false).
	 * @param ignoreemptylines true if if empty lines from value are ignored, false otherwise
	 */
	public void setIgnoreEmptyLines(boolean ignoreemptylines)
	{
		try
		{
			setBoolean(PROPERTY_IGNOREEMPTYLINES,ignoreemptylines);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Error tolerance used in numeric comparisons
	 */
	public double getTolerance()
	{
		try
		{
			return getDouble(PROPERTY_TOLERANCE);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}

	/**
	 * Set error tolerance to use in numeric comparisons
	 * @param tolerance Error tolerance to use in numeric comparisons
	 */
	public void setTolerance(double tolerance)
	{
		try
		{
			setDouble(PROPERTY_TOLERANCE,tolerance);
		}
		catch (OmDeveloperException e)
		{
			throw new OmUnexpectedException(e);
		}
	}
	
	/**
	 * @return Transformations to apply
	 */
	protected Transformations getTransformations()
	{
		return transformations;
	}
	
	/**
	 * Set transformations to apply
	 * @param transformations
	 */
	protected void setTransformations(Transformations transformations)
	{
		this.transformations=transformations;
	}
	
	@Override
	public String getString(String sName) throws OmDeveloperException
	{
		String sValue=null;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			sValue=super.getString(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				sValue=sq.applyPlaceholders(placeholder);
			}
			else
			{
				sValue=placeholder;
			}
			
			// Check properties with restrictions
			if (PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.containsKey(sName))
			{
				String restriction=PROPERTIES_TO_INITIALIZE_PLACEHOLDERS_WITH_RESTRICTIONS.get(sName);
				if (!sValue.matches(restriction))
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' has an invalid value ");
					error.append(sValue);
					throw new OmFormatException(error.toString());
				}
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(sValue);
			}
		}
		return sValue;
	}
	
	@Override
	public String setString(String sName,String sValue) throws OmDeveloperException
	{
		boolean isOldValueNull=false;
		String sOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			sOldValue=getString(sName);
			isOldValueNull=sOldValue==null;
		}
		String sOldAux=super.setString(sName,sValue);
		placeholders.remove(sName);
		return isOldValueNull?null:sOldValue==null?sOldAux:sOldValue;
	}
	
	@Override
	public int getInteger(String sName) throws OmDeveloperException
	{
		int iValue=-1;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			iValue=super.getInteger(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				try
				{
					iValue=Integer.parseInt(placeholderReplaced);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' is not a valid integer");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Integer(iValue));
			}
		}
		return iValue;
	}
	
	@Override
	public int setInteger(String sName,int iValue) throws OmDeveloperException
	{
		Integer iOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			iOldValue=new Integer(getInteger(sName));
		}
		int iOldAux=super.setInteger(sName,iValue);
		placeholders.remove(sName);
		return iOldValue==null?iOldAux:iOldValue.intValue();
	}
	
	@Override
	public boolean getBoolean(String sName) throws OmDeveloperException
	{
		boolean bValue=false;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			bValue=super.getBoolean(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				if (placeholderReplaced.equals("yes"))
				{
					bValue=true;
				}
				else if (placeholderReplaced.equals("no"))
				{
					bValue=false;
				}
				else
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' must be either 'yes' or 'no'");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Boolean(bValue));
			}
		}
		return bValue;
	}
	
	@Override
	public boolean setBoolean(String sName,boolean bValue) throws OmDeveloperException
	{
		Boolean bOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			bOldValue=new Boolean(getBoolean(sName));
		}
		boolean bOldAux=super.setBoolean(sName,bValue);
		placeholders.remove(sName);
		return bOldValue==null?bOldAux:bOldValue.booleanValue();
	}
	
	@Override
	public double getDouble(String sName) throws OmDeveloperException
	{
		double dValue=0.0;
		String placeholder=placeholders.get(sName);
		if (placeholder==null)
		{
			dValue=super.getDouble(sName);
		}
		else
		{
			StandardQuestion sq=(StandardQuestion)getQuestion();
			if (sq.isPlaceholdersInitialized())
			{
				String placeholderReplaced=sq.applyPlaceholders(placeholder);
				try
				{
					dValue=Double.parseDouble(placeholderReplaced);
				}
				catch (NumberFormatException e)
				{
					StringBuffer error=new StringBuffer();
					error.append('<');
					error.append(getTagName());
					error.append(">: property '");
					error.append(sName);
					error.append("' is not a valid double");
					throw new OmFormatException(error.toString());
				}
			}
			else
			{
				StringBuffer error=new StringBuffer();
				error.append('<');
				error.append(getTagName());
				error.append(">: Placeholder ");
				error.append(placeholder);
				error.append(" for property '");
				error.append(sName);
				error.append("' can not be replaced because placeholders have not been initialized");
				throw new OmFormatException(error.toString());
			}
			
			// Do specific check if defined
			if (checks.containsKey(sName))
			{
				checks.get(sName).check(new Double(dValue));
			}
		}
		return dValue;
	}
	
	@Override
	public double setDouble(String sName,double dValue) throws OmDeveloperException
	{
		Double dOldValue=null;
		StandardQuestion sq=(StandardQuestion)getQuestion();
		if (sq.isPlaceholdersInitialized() && placeholders.containsKey(sName))
		{
			dOldValue=new Double(getDouble(sName));
		}
		double dOldAux=super.setDouble(sName,dValue);
		placeholders.remove(sName);
		return dOldValue==null?dOldAux:dOldValue.doubleValue();
	}
	
	/**
	 * Get list of required attribute names; applied by setPropertiesFrom() that have not been set with
	 * a placeholder<br/><br/>
	 * <b>IMPORTANT</b>: Note that only required properties that have not been set with a placeholder are
	 * included in the list.<br/><br/>
	 * The reason for doing it that way is because properties set with placeholders has been removed from XML 
	 * before calling setPropertiesFrom() to avoid a format error, but still setPropertiesFrom() will throw 
	 * an error if any of the required properties from this list are not present in XML, so in order 
	 * to avoid that error we need to return only required attributes that have not been set with a 
	 * placeholder.<br/><br/>
	 * If you want to override this method you must be careful when including properties that allow 
	 * placeholders.
	 * @return List of required attribute names; applied by setPropertiesFrom() that have not been set with
	 * a placeholder
	 */
	@Override
	protected String[] getRequiredAttributes()
	{
		return new String[] {};
	}
	
	@Override
	public void init(QComponent parent,QDocument qd,Element eThis,boolean bImplicit) throws OmException
	{
		Map<String,String> removedAttributes=new HashMap<String,String>();
		
		// First we need to define and set 'id' before calling super.init
		if (!bImplicit && eThis.hasAttribute(PROPERTY_ID))
		{
			String id=eThis.getAttribute(PROPERTY_ID);
			
			// First we need to define 'id' property
			defineString(PROPERTY_ID,PROPERTYRESTRICTION_ID);
			
			// As 'id' property doesn't allow placeholders we can set it using superclass method
			super.setString(PROPERTY_ID,id);
			
			// We remove attribute 'id' before calling setPropertiesFrom method to avoid setting it again
			eThis.removeAttribute(PROPERTY_ID);
			removedAttributes.put(PROPERTY_ID,id);
		}
		
		// We do this trick to initialize placeholders before calling setPropertiesFrom method
		super.init(parent,qd,eThis,true);
		
		// Initialize placeholders needed
		if (!bImplicit)
		{
			for (String property:PROPERTIES_TO_INITIALIZE_PLACEHOLDERS)
			{
				if (eThis.hasAttribute(property))
				{
					String propertyValue=eThis.getAttribute(property);
					if (StandardQuestion.containsPlaceholder(propertyValue))
					{
						// We add a placeholder for this property
						placeholders.put(property,propertyValue);
						
						// We set this property with some value (null for example) to achieve that OM considers 
						// that it is set.
						// We need to do this because overriding isPropertySet method from 
						// om.stdquestion.QComponent it is not enough to achieve it because checkSetProperty 
						// private method from same class does the same check without calling it
						Class<?> type=getPropertyType(property);
						if (type.equals(String.class))
						{
							super.setString(property,null);
						}
						else if (type.equals(Integer.class))
						{
							super.setInteger(property,-1);
						}
						else if (type.equals(Double.class))
						{
							super.setDouble(property,0.0);
						}
						else if (type.equals(Boolean.class))
						{
							super.setBoolean(property,false);
						}
						
						// We remove attribute before calling setPropertiesFrom method to avoid a format error
						eThis.removeAttribute(property);
						removedAttributes.put(property,propertyValue);
					}
				}
			}
			setPropertiesFrom(eThis);
			
			// After calling setPropertiesFrom method we need to set again removed attributes
			for (Map.Entry<String,String> removedAttribute:removedAttributes.entrySet())
			{
				eThis.setAttribute(removedAttribute.getKey(),removedAttribute.getValue());
			}
		}
		
		// Specific initializations
		initializeSpecific(eThis);
		
		// Do now specific checks on properties set without using placeholders
		for (Map.Entry<String,PropertyCheck> check:checks.entrySet())
		{
			if (!placeholders.containsKey(check.getKey()))
			{
				Object value=null;
				Class<?> type=getPropertyType(check.getKey());
				if (type.equals(String.class))
				{
					value=super.getString(check.getKey());
				}
				else if (type.equals(Integer.class))
				{
					value=new Integer(super.getInteger(check.getKey()));
				}
				else if (type.equals(Double.class))
				{
					value=new Double(super.getDouble(check.getKey()));
				}
				else if (type.equals(Boolean.class))
				{
					value=new Boolean(super.getBoolean(check.getKey()));
				}
				check.getValue().check(value);
			}
		}
	}
	
	/**
	 * Does nothing.<br/><br/>
	 * Note that this method has been declared 'final' because we don't want it to be overriden.<br/><br/>
	 * The reason for doing that is that om.stdcomponent.uned components don't allow to check properties
	 * from this method, because properties aren't still defined due to initializations changes to support
	 * placeholders.<br/><br/>
	 * If you need specific initializations you can override initializeSpecific method instead.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	@Override
	protected final void initSpecific(Element eThis) throws OmException
	{
	}
	
	/**
	 * Carries out initialisation specific to component.<br/><br/>
	 * Override this method if you need any.<br/><br/>
	 * However if you need to check some properties instead of checking them inside directly it is recommended 
	 * that you call "addSpecificCheck" method once for every property to be checked with an implementation of the
	 * PropertyCheck interface providing desired check.<br/><br/>
	 * The reason to doing it that way is to be sure that checks will work well even if you set a property using
	 * placeholders.<br/><br/>
	 * Default implementation of this method does nothing.
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error
	 */
	protected void initializeSpecific(Element eThis) throws OmException
	{
	}
	
	/**
	 * Add a specific check for some property.<br/><br/>
	 * Obviously property must be defined before calling this method.<br/><br/>
	 * It is recommended that you invoke this method from initializeSpecific method onece for every property with
	 * specific checks.
	 * @param propertyName Property's name
	 * @param propertyCheck Property's check
	 * @throws OmException
	 */
	protected void addSpecificCheck(String propertyName,PropertyCheck propertyCheck) throws OmException
	{
		if (!isPropertyDefined(propertyName))
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: property '");
			error.append(propertyName);
			error.append("' has not been defined");
			throw new OmDeveloperException(error.toString());
		}
		if (propertyCheck==null)
		{
			StringBuffer error=new StringBuffer();
			error.append('<');
			error.append(getTagName());
			error.append(">: property check implementation for '");
			error.append(propertyName);
			error.append("' has not been provided");
			throw new OmDeveloperException(error.toString());
		}
		checks.put(propertyName,propertyCheck);
	}
	
	/**
	 * Initialises children based on the given XML element.
	 * <br/><br/>
	 * Note that om.stdcomponent.uned components don't allow to check properties from this method, because properties 
	 * aren't still defined due to initializations changes to support placeholders.<br/><br/>
	 * If you override this method and you really need to check properties you need to move that checkings to 
	 * other method (some good candidates depending on your needs are: initializeSpecific, produceVisibleOutput or 
	 * init method from question class if you are overriding generic question class).
	 * @param eThis XML element representing this component
	 * @throws OmException If there's an error initialising the children
	 */
	@Override
	protected void initChildren(Element eThis) throws OmException
	{
		if (eThis.getFirstChild()!=null)
		{
			throw new OmFormatException("<variable> may not include children");
		}
	}
	
	@Override
	public void produceVisibleOutput(QContent qc,boolean bInit,boolean bPlain) throws OmException
	{
	}
	
	/**
	 * @param idComponent Identifier of component selected for variable's value 
	 * (including transformation string if exists)
	 * @return Same identifier but without transformation string 
	 */
	private String getOnlyId(String idComponent)
	{
		String onlyId=idComponent;
		if (getTransformationString(idComponent)!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=
					AnswerChecking.indexOfCharacter(idComponent,SET_TRANSFORMATIONS_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				onlyId=idComponent.substring(0,iStartSelector);
			}
		}
		return AnswerChecking.replaceEscapeChars(onlyId);
	}
	
	/**
	 * Gets transformation string if defined, otherwise return null
	 * @param idComponent Identifier of component selected for variable's value 
	 * (including transformation string if exists)
	 * @return Attribute's selector if defined, null otherwise
	 */
	private String getTransformationString(String idComponent)
	{
		String transformationString=null;
		Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
		if (idComponent!=null)
		{
			int iStartSelector=
					AnswerChecking.indexOfCharacter(idComponent,SET_TRANSFORMATIONS_OPEN,escapeSequences);
			if (iStartSelector!=-1 && iStartSelector+1<idComponent.length())
			{
				iStartSelector++;
				int iEndSelector=AnswerChecking.indexOfCharacter(
						idComponent,SET_TRANSFORMATIONS_CLOSE,iStartSelector,escapeSequences);
				if (iEndSelector!=-1)
				{
					transformationString=idComponent.substring(iStartSelector,iEndSelector);
				}
			}
		}
		return transformationString;
	}
	
	/**
	 * @param id Identifier
	 * @return Canvas from first part of identifier or null if it is not a valid canvas identifier 
	 */
	private CanvasComponent getCanvas(String id)
	{
		CanvasComponent canvas=null;
		if (id!=null)
		{
			String canvasId=null;
			int iEndCanvasId=AnswerChecking.indexOfCharacter(
					id,MARKER_IDENTIFIER_SEPARATOR,AnswerChecking.getEscapeSequences());
			if (iEndCanvasId!=-1)
			{
				canvasId=AnswerChecking.replaceEscapeChars(id.substring(0,iEndCanvasId));
			}
			if (canvasId!=null)
			{
				StandardQuestion sq=(StandardQuestion)getQuestion();
				QComponent qc=null;
				try
				{
					qc=sq.getComponent(canvasId);
				}
				catch (OmDeveloperException e)
				{
					qc=null;
				}
				if (qc!=null && qc instanceof CanvasComponent)
				{
					canvas=(CanvasComponent)qc;
				}
			}
		}
		return canvas;
	}
	
	/**
	 * @param canvas Canvas
	 * @param id Identifier
	 * @return World from optional second part of identifier or null if it is not a valid world identifier
	 * or this part is missing
	 */
	private World getWorld(CanvasComponent canvas,String id)
	{
		World world=null;
		if (canvas!=null && id!=null)
		{
			int iStartWorldId=AnswerChecking.indexOfCharacter(
					id,MARKER_IDENTIFIER_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			int iEndWorldId=-1;
			if (iStartWorldId!=-1)
			{
				iEndWorldId=AnswerChecking.indexOfCharacter(
						id,MARKER_IDENTIFIER_SEPARATOR,iStartWorldId,AnswerChecking.getEscapeSequences());
			}
			if (iEndWorldId>iStartWorldId)
			{
				int iOtherRequiredSeparator=AnswerChecking.indexOfCharacter(
						id,MARKER_IDENTIFIER_SEPARATOR,iEndWorldId+1,AnswerChecking.getEscapeSequences());
				if (iOtherRequiredSeparator!=-1)
				{
					String worldId=
							AnswerChecking.replaceEscapeChars(id.substring(iStartWorldId,iEndWorldId));
					for (World w:canvas.getWorlds())
					{
						if (worldId.equals(w.getID()))
						{
							world=w;
							break;
						}
					}
				}
			}
		}
		return world;
	}
	
	/**
	 * @param canvas Canvas
	 * @param id Identifier
	 * @return Marker from second or third part of identifier (depends on world identifier present or not) 
	 * or null if it not references a valid marker or this part is missing
	 */
	private Marker getMarker(CanvasComponent canvas,String id)
	{
		Marker marker=null;
		if (canvas!=null && id!=null)
		{
			int iStartWorldId=AnswerChecking.indexOfCharacter(
					id,MARKER_IDENTIFIER_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			int iStartMarkerIndex=-1;
			if (iStartWorldId!=-1)
			{
				iStartMarkerIndex=AnswerChecking.indexOfCharacter(
						id,MARKER_IDENTIFIER_SEPARATOR,iStartWorldId,
						AnswerChecking.getEscapeSequences())+1;
			}
			if (iStartMarkerIndex>iStartWorldId)
			{
				int iEndMarkerIndex=AnswerChecking.indexOfCharacter(
						id,MARKER_IDENTIFIER_SEPARATOR,iStartMarkerIndex,
						AnswerChecking.getEscapeSequences());
				if (iEndMarkerIndex==-1)
				{
					iEndMarkerIndex=iStartMarkerIndex-1;
					iStartMarkerIndex=iStartWorldId;
				}
				try
				{
					int markerIndex=Integer.parseInt(id.substring(iStartMarkerIndex,iEndMarkerIndex));
					marker=canvas.getMarkers().get(markerIndex);
				}
				catch (IndexOutOfBoundsException ioobe)
				{
					marker=null;
				}
				catch (NumberFormatException nfe)
				{
					marker=null;
				}
			}
		}
		return marker;
	}
	
	/**
	 * @param marker Marker
	 * @param world World or null
	 * @param id Identifier
	 * @return Value of attribute from last part of identifier or null if it not references a valid
	 * attribute (currently only supported <i>x</i> and <i>y</i> marker attributes), and converting result 
	 * to world co-ordinates if a valid &lt;world&gt; component is received in arguments, 
	 * result will be in pixel co-ordinates otherwise
	 */
	private String getMarkerAttributeValue(Marker marker,World world,String id)
	{
		String value=null;
		if (marker!=null && id!=null)
		{
			int iStartWorldId=AnswerChecking.indexOfCharacter(
					id,MARKER_IDENTIFIER_SEPARATOR,AnswerChecking.getEscapeSequences())+1;
			int iStartMarkerIndex=-1;
			if (iStartWorldId!=-1)
			{
				iStartMarkerIndex=AnswerChecking.indexOfCharacter(
						id,MARKER_IDENTIFIER_SEPARATOR,iStartWorldId,
						AnswerChecking.getEscapeSequences())+1;
			}
			if (iStartMarkerIndex>iStartWorldId)
			{
				int iStartAttribute=AnswerChecking.indexOfCharacter(
						id,MARKER_IDENTIFIER_SEPARATOR,iStartMarkerIndex,
						AnswerChecking.getEscapeSequences())+1;
				if (iStartAttribute<iStartMarkerIndex)
				{
					iStartAttribute=iStartMarkerIndex;
				}
				String attribute=AnswerChecking.replaceEscapeChars(id.substring(iStartAttribute));
				if (attribute.equals(MARKER_ATTRIBUTE_X))
				{
					if (world==null)
					{
						value=Integer.toString(marker.getX());
					}
					else
					{
						value=Double.toString(world.convertXBack(marker.getX()));
					}
				}
				else if (attribute.equals(MARKER_ATTRIBUTE_Y))
				{
					if (world==null)
					{
						value=Integer.toString(marker.getY());
					}
					else
					{
						value=Double.toString(world.convertYBack(marker.getY()));
					}
				}
			}
		}
		return value;
	}
	
	/**
	 * Reset value of this variable component so next call to getValue() will re-calculate it
	 */
	public void resetValue()
	{
		value=null;
	}
	
	/**
	 * @return Value of this variable component reading component indicated 
	 * and/or applying transformations if needed
	 */
	public String getValue()
	{
		if (value==null)
		{
			value="";
			if (getSet().startsWith("@"))
			{
				if (!getSet().equals(DEFAULT_SET))
				{
					value=AnswerChecking.replaceEscapeChars(getSet().substring(1));
				}
			}
			else
			{
				String onlyId=getOnlyId(getSet());
				QComponent qc=null;
				value=null;
				try
				{
					if (onlyId.equals(getID()))
					{
						StringBuffer error=new StringBuffer();
						error.append("<variable>: Invalid reference of variable ");
						error.append(onlyId);
						error.append(" to itself outside transformation string");
						throw new VariableFormatRuntimeException(error.toString());
					}
					StandardQuestion sq=(StandardQuestion)getQuestion();
					qc=sq.getComponent(onlyId);
					if (qc instanceof RandomComponent)
					{
						value=((RandomComponent)qc).getValue();
					}
					else if (qc instanceof ReplaceholderComponent)
					{
						value=((ReplaceholderComponent)qc).getReplacementValue();
					}
					else if (qc instanceof VariableComponent)
					{
						value=((VariableComponent)qc).getValue();
					}
					else if (qc instanceof Answerable)
					{
						value=((Answerable)qc).getAnswerLine();
					}
				}
				catch (OmException e)
				{
					CanvasComponent canvas=getCanvas(onlyId);
					World world=getWorld(canvas,onlyId);
					Marker marker=getMarker(canvas,onlyId);
					value=getMarkerAttributeValue(marker,world,onlyId);
					if (value==null)
					{
						if (onlyId.startsWith(LITERAL_ATTRIBUTE_ID) && 
								onlyId.length()>LITERAL_ATTRIBUTE_ID.length())
						{
							String attributeValue=AnswerChecking.replaceEscapeChars(
									onlyId.substring(LITERAL_ATTRIBUTE_ID.length()));
							AttributeComponent literalAttribute=new AttributeComponent();
							try
							{
								literalAttribute.defineProperties();
							}
							catch (OmDeveloperException e1)
							{
								throw new OmUnexpectedException(e1.getMessage(),e1);
							}
							literalAttribute.setAttribute(attributeValue);
							value=literalAttribute.getAttributeValue();
						}
						else
						{
							StringBuffer error=new StringBuffer();
							error.append("<variable>: Unrecognized identifier: ");
							error.append(onlyId);
							throw new VariableFormatRuntimeException(error.toString());
						}
					}
				}
				if (value==null)
				{
					value="";
				}
				else
				{
					String transformationString=getTransformationString(getSet());
					if (transformationString!=null)
					{
						if (transformations==null)
						{
							transformations=new Transformations();
						}
						transformations.parse(transformationString);
						value=transformations.transform(value);
					}
				}
			}
		}
		return value;
	}
	
	/**
	 * @param valueType Value type selected for testing (including value type's selector if exists)
	 * @return Same value type but without selector 
	 */
	private String getOnlyValueType(String valueType)
	{
		String onlyValueType=valueType;
		if (getValueTypeSelector(valueType)!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=
					AnswerChecking.indexOfCharacter(valueType,VALUETYPE_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				onlyValueType=valueType.substring(0,iStartSelector);
			}
		}
		return onlyValueType;
	}
	
	/**
	 * Gets value type's selector if defined, otherwise return null
	 * @param valueType Value type selected for testing (including value type's selector if exists)
	 * @return Value type's selector if defined, null otherwise
	 */
	private String getValueTypeSelector(String valueType)
	{
		String valueTypeSelector=null;
		if (valueType!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iStartSelector=
					AnswerChecking.indexOfCharacter(valueType,VALUETYPE_SELECTOR_OPEN,escapeSequences);
			if (iStartSelector!=-1)
			{
				iStartSelector++;
				int iEndSelector=AnswerChecking.indexOfCharacter(
						valueType,VALUETYPE_SELECTOR_CLOSE,iStartSelector,escapeSequences);
				if (iEndSelector!=-1)
				{
					valueTypeSelector=valueType.substring(iStartSelector,iEndSelector);
				}
			}
		}
		return valueTypeSelector;
	}
	
	/**
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return Preprocessed value ready to compare with expected value
	 */
	private String getPreprocessedValue(AnswerTestProperties overrideProperties)
	{
		String value=getValue();
		if (overrideProperties.getStrip(isStrip()))
		{
			value=AnswerChecking.stripWhitespace(
					value,overrideProperties.getNewLineSpace(isNewLineSpace()));
		}
		else
		{
			if (overrideProperties.getTrim(isTrim()))
			{
				value=AnswerChecking.trimWhitespace(
						value,overrideProperties.getNewLineSpace(isNewLineSpace()));
			}
			if (overrideProperties.getSingleSpaces(isSingleSpaces()))
			{
				value=AnswerChecking.singledWhitespace(
						value,overrideProperties.getNewLineSpace(isNewLineSpace()));
			}
		}
		if (overrideProperties.getIgnoreEmptyLines(isIgnoreEmptyLines()))
		{
			value=AnswerChecking.removeEmptyLines(value);
		}
		String ignore=overrideProperties.getIgnore(getIgnore());
		if (ignore!=null)
		{
			value=AnswerChecking.removeTextOcurrences(value,ignore);
		}
		String ignoreRegExp=overrideProperties.getIgnoreRegExp(getIgnoreRegExp());
		if (ignoreRegExp!=null)
		{
			value=value.replaceAll(ignoreRegExp,"");
		}
		return value;
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the expected value 
	 * for values of type text.
	 * @param s String with the expected value
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the expected value,
	 * false otherwise (for values of type text)
	 */
	private boolean isValueText(String s,AnswerTestProperties overrideProperties)
	{
		return overrideProperties.getCaseSensitive(isCaseSensitive())?
				getPreprocessedValue(overrideProperties).equals(s):
				getPreprocessedValue(overrideProperties).equalsIgnoreCase(s);
	}
	
	/**
	 * Returns if value of this variable component corresponds to the expected value  
	 * for values of type regexp.
	 * @param s String with the expected value's pattern
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the expected value,
	 * false otherwise (for values of type regexp)
	 */
	private boolean isValueRegExp(String s,AnswerTestProperties overrideProperties)
	{
		String value=getPreprocessedValue(overrideProperties);
		if (!overrideProperties.getCaseSensitive(isCaseSensitive()))
		{
			s=AnswerChecking.regExpToLowerCase(s);
			value=value.toLowerCase();
		}
		return Pattern.compile(s).matcher(value).matches();
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the expected value 
	 * for values of type pmatch.
	 * @param value String with the value of this variable component
	 * @param s String with the expected value's pattern
	 * @param options PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the value expected,
	 * false otherwise (for answers of type pmatch)
	 */
	private boolean isValuePMatch(String value,String s,String options,
			AnswerTestProperties overrideProperties)
	{
		boolean matching=false;
		if (!overrideProperties.getCaseSensitive(isCaseSensitive()))
		{
			s=s.toLowerCase();
			value=value.toLowerCase();
		}
		PMatch pmatch=new PMatch(value);
		if (options==null || options.equals(""))
		{
			matching=pmatch.match(s);
		}
		else
		{
			matching=pmatch.match(options,s);
		}
		return matching;
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the expected value 
	 * for values of type pmatch.
	 * @param s String with the expected value's pattern
	 * @param options PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the expected value,
	 * false otherwise (for values of type pmatch)
	 */
	private boolean isValuePMatch(String s,String options,AnswerTestProperties overrideProperties)
	{
		return isValuePMatch(getPreprocessedValue(overrideProperties),s,options,overrideProperties);
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the numeric value indicated.
	 * @param value String with the variable's value
	 * @param s String with the expected number
	 * @param options Numeric options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the numeric value indicated,
	 * false otherwise (for values of type numeric)
	 */
	private boolean isValueNumeric(String value,String s,NumericOptions options,
			AnswerTestProperties overrideProperties)
	{
		String expectedString=s.trim();
		boolean testing=false;
		double expected=NumericTester.inputNumber(expectedString);
		
		// Transform from scientific input to e notation if needed
		if (options.scientificTransform)
		{
			value=NumericTester.scientificNotationToE(value);
		}
		double numValue=NumericTester.inputNumber(value);
		
		// Precision
		int precision=-1;
		if (options.usePrecision)
		{
			precision=options.significantDigits;
			if (precision==-1)
			{
				precision=NumericTester.countNumberOfSignificantFiguresInString(expectedString);
			}
			else if (precision>0 && precision<100)
			{
				expected=NumericTester.toSigFigs(expected,precision);
			}
			if (precision>0 && precision<100)
			{
				numValue=NumericTester.toSigFigs(numValue,precision);
			}
		}
		
		// Checks
		if (!Double.isNaN(numValue) && !Double.isNaN(expected))
		{
			testing=true;
			if (options.scientificCheck)
			{
				testing=NumericTester.isScientificNotation(value);
			}
			if (testing && options.numberCheck)
			{
				testing=NumericTester.range(
						numValue,expected,overrideProperties.getTolerance(getTolerance()));
			}
			if (testing && options.absoluteCheck)
			{
				double expectedAbs=StrictMath.abs(expected);
				double numValueAbs=StrictMath.abs(numValue);
				testing=NumericTester.range(
						numValueAbs,expectedAbs,overrideProperties.getTolerance(getTolerance()));
			}
			if (testing && options.significantDigitsCheck)
			{
				String expectedSignificantDigits=NumericTester.getSignificantFigures(expectedString);
				String numValueSignificantDigits=NumericTester.getSignificantFigures(value);
				if (precision!=-1)
				{
					if (expectedSignificantDigits.startsWith("-") && 
							expectedSignificantDigits.length()-1>precision)
					{
						expectedSignificantDigits=expectedSignificantDigits.substring(0,precision+1);
					}
					else if (expectedSignificantDigits.length()>precision)
					{
						expectedSignificantDigits=expectedSignificantDigits.substring(0,precision);
					}
					if (numValueSignificantDigits.startsWith("-") && 
							numValueSignificantDigits.length()-1>precision)
					{
						numValueSignificantDigits=numValueSignificantDigits.substring(0,precision+1);
					}
					else if (numValueSignificantDigits.length()>precision)
					{
						numValueSignificantDigits=numValueSignificantDigits.substring(0,precision);
					}
				}
				testing=numValueSignificantDigits.equals(expectedSignificantDigits);
			}
			if (testing && options.exponentCheck)
			{
				int expectedExponent=NumericTester.getExponent(expectedString);
				int numValueExponent=NumericTester.getExponent(value);
				testing=numValueExponent==expectedExponent;
			}
			if (testing && options.precissionCheck)
			{
				int expectedSignificantDigits=options.precissionCheckSignificantDigits;
				if (expectedSignificantDigits==-1)
				{
					expectedSignificantDigits=
							NumericTester.countNumberOfSignificantFiguresInString(expectedString);
				}
				int numValueSignificantDigits=
						NumericTester.countNumberOfSignificantFiguresInString(value);
				switch (options.precissionCheckOp)
				{
					case NumericOptions.EQUAL_PRECISION:
						testing=numValueSignificantDigits==expectedSignificantDigits;
						break;
					case NumericOptions.LESS_PRECISION:
						testing=numValueSignificantDigits<expectedSignificantDigits;
						break;
					case NumericOptions.LESS_EQUAL_PRECISION:
						testing=numValueSignificantDigits<=expectedSignificantDigits;
						break;
					case NumericOptions.GREATER_PRECISION:
						testing=numValueSignificantDigits>expectedSignificantDigits;
						break;
					case NumericOptions.GREATER_EQUAL_PRECISION:
						testing=numValueSignificantDigits>=expectedSignificantDigits;
						break;
					default:
						testing=false;
				}
			}
		}
		return testing;
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the numeric value indicated.
	 * @param s String with the expected number
	 * @param options Numeric options as string
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the numeric value indicated,
	 * false otherwise (for values of type numeric)
	 */
	private boolean isValueNumeric(String s,String options,AnswerTestProperties overrideProperties)
	{
		return isValueNumeric(getPreprocessedValue(overrideProperties),s,new NumericOptions(options),
				overrideProperties);
	}
	
	/**
	 * Get the numeric options from numpmatch options string
	 * @param options numpmatch options string
	 * @return Numeric options from numpmatch options string
	 */
	private String getNumericOptionsForNumPMatchOptions(String options)
	{
		String numericOptions="";
		if (options!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iFirstSeparator=
					AnswerChecking.indexOfCharacter(options,VALUE_NUMPMATCH_SEPARATOR,escapeSequences);
			if (iFirstSeparator!=-1)
			{
				int iSecondSeparator=AnswerChecking.indexOfCharacter(
						options,VALUE_NUMPMATCH_SEPARATOR,iFirstSeparator+1,escapeSequences);
				if (iSecondSeparator!=-1)
				{
					numericOptions=options.substring(0,iFirstSeparator);
				}
			}
		}
		return numericOptions;
	}
	
	/**
	 * Get the separator from numpmatch options string
	 * @param options numpmatch options string
	 * @return Separator from numpmatch options string
	 */
	private String getSeparatorForNumPMatchOptions(String options)
	{
		String separator="";
		if (options!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iFirstSeparator=
					AnswerChecking.indexOfCharacter(options,VALUE_NUMPMATCH_SEPARATOR,escapeSequences);
			if (iFirstSeparator!=-1)
			{
				int iSecondSeparator=AnswerChecking.indexOfCharacter(
						options,VALUE_NUMPMATCH_SEPARATOR,iFirstSeparator+1,escapeSequences);
				if (iSecondSeparator==-1)
				{
					separator=options.substring(0,iFirstSeparator);
				}
				else
				{
					separator=options.substring(iFirstSeparator+1,iSecondSeparator);
				}
			}
		}
		return AnswerChecking.replaceEscapeChars(separator);
	}
	
	/**
	 * Get the pmatch options from numpmatch options string
	 * @param options numpmatch options string
	 * @return pmatch options from numpmatch options string
	 */
	private String getPMatchOptionsForNumPMatchOptions(String options)
	{
		String pMatchOptions=options;
		if (options!=null)
		{
			Map<Character,String> escapeSequences=AnswerChecking.getEscapeSequences();
			int iFirstSeparator=
					AnswerChecking.indexOfCharacter(options,VALUE_NUMPMATCH_SEPARATOR,escapeSequences);
			if (iFirstSeparator!=-1)
			{
				int iSecondSeparator=AnswerChecking.indexOfCharacter(
						options,VALUE_NUMPMATCH_SEPARATOR,iFirstSeparator+1,escapeSequences);
				if (iSecondSeparator==-1)
				{
					pMatchOptions="";
					if (iFirstSeparator+1<options.length())
					{
						pMatchOptions=options.substring(iFirstSeparator+1);
					}
				}
				else
				{
					pMatchOptions="";
					if (iSecondSeparator+1<options.length())
					{
						pMatchOptions=options.substring(iSecondSeparator+1);
					}
				}
			}
		}
		return pMatchOptions;
	}
	
	/**
	 * Get index of the next character to the numeric part of the value of this variable component.
	 * <br/><br/>This function takes care if the value of this variable component 
	 * is expected to be in scientific notation or not.
	 * @param value Value of this variable component
	 * @param nOpts Numeric options
	 * @return index of the next character to the numeric part of the user's answer
	 */
	private int getEndOfNumberPosition(String value,NumericOptions nOpts)
	{
		int endOfNumberPosition=-1;
		if (value!=null)
		{
			int iStart=0;
			char c='\0';
			if (iStart<value.length())
			{
				c=value.charAt(iStart);
			}
			while (c!='\0' && c!='.' && (c<'0' || c>'9'))
			{
				iStart++;
				if (iStart<value.length())
				{
					c=value.charAt(iStart);
				}
				else
				{
					c='\0';
				}
			}
			if (c!='\0')
			{
				endOfNumberPosition=iStart;
				while (c>='0' && c<='9')
				{
					endOfNumberPosition++;
					if (endOfNumberPosition<value.length())
					{
						c=value.charAt(endOfNumberPosition);
					}
					else
					{
						c='\0';
					}
				}
				if (c=='.')
				{
					endOfNumberPosition++;
					if (endOfNumberPosition<value.length())
					{
						c=value.charAt(endOfNumberPosition);
					}
					else
					{
						c='\0';
					}
					while (c>='0' && c<='9')
					{
						endOfNumberPosition++;
						if (endOfNumberPosition<value.length())
						{
							c=value.charAt(endOfNumberPosition);
						}
						else
						{
							c='\0';
						}
					}
				}
				if (nOpts.scientificTransform)
				{
					if (c!='\0')
					{
						StringBuffer mulDot10=new StringBuffer();
						mulDot10.append('\u00d7');
						mulDot10.append("10");
						String x10Start=value.substring(endOfNumberPosition);
						if (x10Start.startsWith("x10") || x10Start.startsWith("X10") || 
								x10Start.startsWith("*10") || 
								x10Start.startsWith(mulDot10.toString()))
						{
							endOfNumberPosition+=3;
							boolean openSup=false;
							int i=endOfNumberPosition+5;
							if (i<value.length())
							{
								String sOpen=value.substring(endOfNumberPosition,i);
								if (sOpen.equalsIgnoreCase("<sup>"))
								{
									openSup=true;
								}
							}
							if (openSup)
							{
								i=value.indexOf('<',i);
								if (i!=-1)
								{
									int j=i+6;
									if (j<value.length())
									{
										String sClose=value.substring(i,j);
										if (sClose.equalsIgnoreCase("</sup>"))
										{
											endOfNumberPosition=j;
										}
									}
								}
							}
						}
					}
				}
				else
				{
					if (c=='e' || c=='E')
					{
						int i=endOfNumberPosition+1;
						if (i<value.length())
						{
							c=value.charAt(i);
						}
						else
						{
							c='\0';
						}
						if (c=='+' || c=='-')
						{
							i++;
							if (i<value.length())
							{
								c=value.charAt(i);
							}
							else
							{
								c='\0';
							}
						}
						if (c>='0' && c<='9')
						{
							endOfNumberPosition=i;
							while (c>='0' && c<='9')
							{
								endOfNumberPosition++;
								if (endOfNumberPosition<value.length())
								{
									c=value.charAt(endOfNumberPosition);
								}
								else
								{
									c='\0';
								}
							}
						}
					}
				}
			}
			else
			{
				endOfNumberPosition=0;
			}
		}
		return endOfNumberPosition;
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the expected value 
	 * for values of type numpmatch.
	 * @param s String with the expected value and pattern separated by ,
	 * @param options Number separator and PMatch options
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the expected value,
	 * false otherwise (for values of type numpmatch)
	 */
	private boolean isValueNumPMatch(String s,String options,AnswerTestProperties overrideProperties)
	{
		boolean testing=false;
		if (s!=null)
		{
			NumericOptions numericOptions=
					new NumericOptions(getNumericOptionsForNumPMatchOptions(options));
			String separator=getSeparatorForNumPMatchOptions(options);
			String pMatchOptions=getPMatchOptionsForNumPMatchOptions(options);
			int iSeparator=s.indexOf(VALUE_NUMPMATCH_SEPARATOR);
			if (iSeparator!=-1)
			{
				String numPart=s.substring(0,iSeparator);
				String patternPart="";
				if (iSeparator+1<s.length())
				{
					patternPart=s.substring(iSeparator+1);
				}
				String userValue=getPreprocessedValue(overrideProperties);
				int endValuePartNumber=getEndOfNumberPosition(userValue,numericOptions);
				String userNumValue=endValuePartNumber>0?userValue.substring(0,endValuePartNumber):"";
				String userNumSeparator="";
				String userPatternValue="";
				if (endValuePartNumber>=0 && endValuePartNumber+separator.length()<userValue.length())
				{
					userNumSeparator=userValue.substring(
							endValuePartNumber,endValuePartNumber+separator.length());
					userPatternValue=userValue.substring(endValuePartNumber+separator.length());
				}
				if (userNumSeparator.equals(separator))
				{
					testing=isValueNumeric(userNumValue,numPart,numericOptions,overrideProperties);				
				}
				if (testing)
				{
					testing=isValuePMatch(userPatternValue,patternPart,pMatchOptions,overrideProperties);
				}
			}
		}
		return testing;
	}
	
	/**
	 * Returns if the value of this variable component corresponds to the expected value.
	 * @param s String with the expected value
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing value purposes
	 * @return true if the value of this variable component corresponds to the expected value,
	 * false otherwise
	 */
	public boolean isValue(String s,AnswerTestProperties overrideProperties)
	{
		boolean value=false;
		if (s!=null)
		{
			String valueType=overrideProperties.getAnswerType(getValueType());
			if (valueType!=null)
			{
				String onlyValueType=getOnlyValueType(valueType);
				if (onlyValueType.equals(VALUETYPE_TEXT))
				{
					value=isValueText(s,overrideProperties);
				}
				else if (onlyValueType.equals(VALUETYPE_REGEXP))
				{
					value=isValueRegExp(s,overrideProperties);
				}
				else if (onlyValueType.equals(VALUETYPE_PMATCH))
				{
					String selector=getValueTypeSelector(valueType);
					value=isValuePMatch(s,selector,overrideProperties);
				}
				else if (onlyValueType.equals(VALUETYPE_NUMERIC))
				{
					String selector=getValueTypeSelector(valueType);
					value=isValueNumeric(s,selector,overrideProperties);
				}
				else if (onlyValueType.equals(VALUETYPE_NUMPMATCH))
				{
					String selector=getValueTypeSelector(valueType);
					value=isValueNumPMatch(s,selector,overrideProperties);
				}
			}
		}
		return value;
	}
	
	/**
	 * Returns if the answer typed in this editfield corresponds to the received answer.
	 * @param s String with the expected answer
	 * @param overrideProperties Utility class instance used to allow other classes to override some
	 * properties of this class for testing answer purposes
	 * @return true if the answer typed in this editfield corresponds to the received answer,
	 * false otherwise
	 */
	@Override
	public boolean isAnswer(String s,AnswerTestProperties overrideProperties)
	{
		return isValue(s,overrideProperties);
	}
	
	/**
	 * Returns answer rightness.
	 * @return true if the answer is right, false if it is wrong
	 */
	@Override
	public boolean isRight()
	{
		return isAnswer(getRight(),new AnswerTestProperties());
	}
	
	/**
	 * Returns answer line.<br/>
	 * It is the value of the variable component after preprocessing for testing if it is right,
	 * but replacing new line characters with whitespaces.
	 * @return Answer line
	 */
	@Override
	public String getAnswerLine()
	{
		return getPreprocessedValue(new AnswerTestProperties()).replace('\r', ' ').replace('\n', ' ');
	}
}
