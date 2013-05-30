/* OpenMark online assessment system
   Copyright (C) 2007 The Open University

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package es.uned.lsi.gepec.questions.onechoice;

import java.io.*;
import org.w3c.dom.*;

import om.*;
import om.helper.SimpleQuestion1;
import util.xml.*;

/** Example 4: tests students can reduce simple equations */
public class OneChoiceQuestion extends SimpleQuestion1
{
	/** Numbers for question variants */
	private final static String	question = "Como se llama el amigo de Epi?";
	// now provide text for use in plain mode
	private final static String r1	= "Nicolas";
	private final static String r2	= "Jacinto";
	private final static String r3	= "Manolo";
	private final static String r4	= "Blas";

	//	--------------------------------------------------------------------------------------------

	protected void init() throws OmException
	{
		//InputStream is = 
		//	this.getClass().getResourceAsStream("/es/uned/lsi/gepec/questions/onechoice data.xml");
		
		InputStream is = 
			OneChoiceQuestion.class.getResourceAsStream("data.xml");
		
		String question = "caca";
		String a;
		String b;
		String c;
		String d;
		
		Document data;
		
		try
		{
			data = XML.parse(is);
			
			question = data.getElementsByTagName("questiontext").item(0).getTextContent();
			a = data.getElementsByTagName("answer").item(0).getTextContent();
			b = data.getElementsByTagName("answer").item(1).getTextContent();
			c = data.getElementsByTagName("answer").item(2).getTextContent();
			d = data.getElementsByTagName("answer").item(3).getTextContent();
		}
		catch(IOException ioe)
		{
			throw new OmException("Failed to read question data",ioe);
		}

		setPlaceholder("QUESTION", question);
		setPlaceholder("A", a);
		setPlaceholder("B", b);
		setPlaceholder("C", c);
		setPlaceholder("D", d);
		
		getResults().setQuestionLine(question);
	}
	//--------------------------------------------------------------------------------------------

	protected boolean isRight(int iAttempt) throws OmDeveloperException
	{
  		int		i, answerBox = -1;

  		for (i = 0; i < 4; ++i) {
  			if (getRadioBox("box"+i).isChecked())
  				answerBox = i;
  		}
  		getResults().setAnswerLine("Selected equation box:" + answerBox);
  		getResults().appendActionSummary("Attempt "+iAttempt+": box"+answerBox);

       	if (answerBox == 3)
       		return(true);
       	else if (iAttempt == 2) {
  			setFeedbackID("answer"+answerBox);
       	}

  		return false;
	}
 	//--------------------------------------------------------------------------------------------

}	// class ends
///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
