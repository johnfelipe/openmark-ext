package util.xml.uned;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import util.xml.XML;
import util.xml.XMLException;

/** Additional XML static utility methods */
public class UnedXML extends XML
{
	/**
	 * Finds an element with the given tag name.
	 * @param d document
	 * @param sTag Desired tag name
	 * @return First element matching the desired tag name
	 * @throws XMLException If there is no element with that tag name (note:
	 *   if you don't want this behaviour, try the other find method which
	 *   only returns null)
	 */
	public static Element findByTagName(Document d,String sTag) throws XMLException
	{
		Element eResult=findByTagName(d.getDocumentElement(),sTag);
		if (eResult==null)
		{
			StringBuffer sError=new StringBuffer("Failed to find element with tag: ");
			sError.append(sTag);
			throw new XMLException(sError.toString());
		}
		return eResult;
	}
	
	/**
	 * Recursively finds element with the given tag name.
	 * @param eParent Parent to search within (will also be checked)
	 * @param sTag Desired tag name
	 * @return First element matching the desired tag name, or null if none
	 */
	public static Element findByTagName(Element eParent,String sTag)
	{
		Element eResult=null;
		if (sTag.equals(eParent.getTagName()))
		{
			// If this matches, return it
			eResult=eParent;
		}
		else
		{
			// Try all children
			for (Node n=eParent.getFirstChild();eResult==null && n!=null;n=n.getNextSibling())
			{
				if (n instanceof Element)
				{
					eResult=findByTagName((Element)n,sTag);
				}
			}
		}
		return eResult;
	}
	
	/**
	 * Recursively finds all elements with the given tag name.
	 * @param dParent Parent to search within
	 * @param sTag Desired tag name
	 * @return All elements matching that request, or empty array if none
	 */
	public static Element[] findAllByTagName(Document dParent,String sTag)
	{
		return findAllByTagName(dParent.getDocumentElement(),sTag);
	}
	
	/**
	 * Recursively finds all elements with the given tag name.
	 * @param eParent Parent to search within (will also be checked)
	 * @param sTag Desired tag name
	 * @return All elements matching that request, or empty array if none
	 */
	public static Element[] findAllByTagName(Element eParent,String sTag)
	{
		List<Element> lResult=new LinkedList<Element>();
		findAllInternalByTagName(eParent,sTag,lResult);
		return lResult.toArray(new Element[0]);
	}
	
	/**
	 * Recursively finds all elements with the given tag name.
	 * @param eParent Parent to search within (will also be checked)
	 * @param sTag Desired tag name
	 * @param lResult List to which matching elements are added
	 */
	private static void findAllInternalByTagName(Element eParent,String sTag,List<Element> lResult)
	{
		// If this matches, add it
		if (sTag.equals(eParent.getTagName()))
		{
			lResult.add(eParent);
		}
		
		// Try all children
		for (Node n=eParent.getFirstChild();n!=null;n=n.getNextSibling())
		{
			if(n instanceof Element)
			{
				findAllInternalByTagName((Element)n,sTag,lResult);
			}
		}
	}
}
