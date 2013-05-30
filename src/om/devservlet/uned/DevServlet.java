package om.devservlet.uned;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet used to build and test questions on a developer machine.<br/><br/>
 * Not suitable for any production or public demonstration use.<br/><br/>
 * Only supports one instance of one question at a time.<br/><br/>
 * This is a special implementation to support several features needed by GEPEQ authoring tool.
 */
@SuppressWarnings("serial")
public class DevServlet extends om.devservlet.DevServlet
{
    /** Group of flags that indicates which links to display when running a question */
    private Links links=new Links();
	
    /** Only used to group some flags that indicates which links to display when running a question */
    private class Links
    {
    	private boolean restartLink;
    	private boolean versionsLinks;
    	private boolean plainLink;
    	private boolean colourLink;
    	private boolean bigLink;
    	private boolean rebuildLink;
    	private boolean listLink;
    	private boolean saveLink;
    	
    	private Links()
    	{
    		setAll(true);
    	}
    	
    	private void setAll(boolean display)
    	{
    		restartLink=display;
    		versionsLinks=display;
    		plainLink=display;
    		colourLink=display;
    		bigLink=display;
    		rebuildLink=display;
    		listLink=display;
    		saveLink=display;
    	}
    	
    	private boolean isAll(boolean display)
    	{
    		return restartLink==display && versionsLinks==display && plainLink==display && 
    			colourLink==display && bigLink==display && rebuildLink==display && listLink==display && 
    			saveLink == display;
    	}
    	
    	private boolean isSome(boolean display)
    	{
    		return restartLink==display || versionsLinks==display || plainLink==display || 
			colourLink==display || bigLink==display || rebuildLink==display || listLink==display || 
			saveLink == display;
    	}
    }
    
	/**
	 * @param sRemainingPath Remaining path which has not yet been parsed
	 * @return true if remaining path references a resource, false otherwise
	 */
	private boolean isResource(String sRemainingPath)
	{
		return sRemainingPath.endsWith("style.css") || sRemainingPath.indexOf("resources/")!=-1;
	}
	
	/**
	 * @param sRemainingPath Remaining path which has not been already parsed
	 * @return Same remaining path but without any resource reference
	 */
	private String getPathWithoutResource(String sRemainingPath)
	{
		String path=sRemainingPath;
		if (sRemainingPath.endsWith("style.css"))
		{
			path=sRemainingPath.substring(0,sRemainingPath.indexOf("style.css"));
		}
		else
		{
			int iResources=sRemainingPath.indexOf("resources/");
			if (iResources!=-1)
			{
				path=sRemainingPath.substring(0,iResources);
			}
		}
		return path;
	}
	
	/**
	 * Checks if remaining path contains a valid nolinks option.<br/><br/>
	 * Note that it assumes that "nolinks/" has been found at start of remaining path.
	 * @param createBuild Flag that indicates if we are invoking this method from createbuild option or not
	 * @param sRemainingPath Remaining path which has not yet been parsed
	 * @return true if remaining path contains a valid nolinks option, false otherwise
	 */
	private boolean checkNoLinksFromPath(boolean createBuild,String sRemainingPath)
	{
		boolean check=true;
		int i="nolinks/".length();
		if (i<sRemainingPath.length())
		{
			int iOtherBackslash=sRemainingPath.indexOf('/',i);
			if (iOtherBackslash!=-1 && 
					(createBuild || iOtherBackslash<sRemainingPath.length()-1))
			{
				while (check && i<iOtherBackslash)
				{
					char c=sRemainingPath.charAt(i);
					if (c!='r' && c!='v' && c!='p' && c!='c' && c!='x' && c!='b' && c!='l' && c!='s')
					{
						check=false;
					}
					i++;
				}
			}
		}
		return check;
	}
	
	/** 
	 * Returns part of remaining path with information used by nolinks option.<br/><br/>
	 * If parse argument is true also parses it to set on or off flags that indicates which links to display 
	 * when running a question. 
	 * @param sRemainingPath Remaining path which has not yet been parsed
	 * @param parse Flag that indicates if we want to parse nolinks option (true)
	 * or only to get the remaining path with information used by nolinks option (false)
	 * @returns Remaining path with information used by nolinks option.
	 */
	private String getNoLinksFromPath(String sRemainingPath,boolean parse)
	{
		StringBuffer noLinks=new StringBuffer("nolinks/");
		int i="nolinks/".length();
		int iOtherBackslash=sRemainingPath.indexOf('/',i);
		if (iOtherBackslash!=-1 && iOtherBackslash<sRemainingPath.length()-1)
		{
			if (parse)
			{
				links.setAll(true);
			}
			while (i<iOtherBackslash)
			{
				char c=sRemainingPath.charAt(i);
				if (parse)
				{
					switch (c)
					{
						case 'r':
							links.restartLink=false;
							break;
						case 'v':
							links.versionsLinks=false;
							break;
						case 'p':
							links.plainLink=false;
							break;
						case 'c':
							links.colourLink=false;
							break;
						case 'x':
							links.bigLink=false;
							break;
						case 'b':
							links.rebuildLink=false;
							break;
						case 'l':
							links.listLink=false;
							break;
						case 's':
							links.saveLink=false;
							break;
					}
				}
				noLinks.append(c);
				i++;
			}
			noLinks.append('/');
		}
		else if (parse)
		{
			links.setAll(false);
		}
		return noLinks.toString();
	}
	
	/** 
	 * @return Current nolinks options as a string
	 */
	private String getNoLinks()
	{
		StringBuffer noLinksOpts=new StringBuffer();
		if (links.isAll(false))
		{
			noLinksOpts.append("nolinks/");
		}
		else if (links.isSome(false))
		{
			noLinksOpts.append("nolinks/");
			if (!links.restartLink)
			{
				noLinksOpts.append('r');
			}
			if (!links.versionsLinks)
			{
				noLinksOpts.append('v');
			}
			if (!links.plainLink)
			{
				noLinksOpts.append('p');
			}
			if (!links.colourLink)
			{
				noLinksOpts.append('c');
			}
			if (!links.bigLink)
			{
				noLinksOpts.append('x');
			}
			if (!links.rebuildLink)
			{
				noLinksOpts.append('b');
			}
			if (!links.listLink)
			{
				noLinksOpts.append('l');
			}
			if (!links.saveLink)
			{
				noLinksOpts.append('s');
			}
			noLinksOpts.append('/');
		}
		return noLinksOpts.toString();
	}
	
	/** 
	 * Default implementation respects old behaviour of this class and returns all error links.<br/><br/>
	 * However this method can be overriden in subclasses providing a different behaviour.
	 * @param request Request information object
	 * @return Send error links taking account nolink options
	 */
	@Override
	protected String getSendErrorLinks(HttpServletRequest request)
	{
		StringBuffer errorLinks=new StringBuffer();
		String pathInfo=request.getPathInfo();
		if (getQuestionInProgressId()!=null && pathInfo.startsWith("/run/"))
		{
			StringBuffer sQuestion=new StringBuffer(getQuestionInProgressId());
			sQuestion.append("/");
			if (pathInfo.endsWith(sQuestion.toString()))
			{
				String path=request.getRequestURI().substring(0,request.getRequestURI().indexOf("/run/"));
				errorLinks.append("<a href='");
				errorLinks.append(path);
				errorLinks.append("/run/");
				errorLinks.append(sQuestion.toString());
				errorLinks.append("'>[Restart]</a> <a href='");
				errorLinks.append(path);
				errorLinks.append("/build/");
				errorLinks.append(sQuestion.toString());
				errorLinks.append("'>[Rebuild]</a> ");
			}
		}
		errorLinks.append("<a href='");
		if (pathInfo.startsWith("/createbuild/"))
		{
			errorLinks.append(
					request.getRequestURI().substring(0,request.getRequestURI().indexOf("/createbuild/")));
		}
		else if (pathInfo.startsWith("/build/"))
		{
			errorLinks.append(
					request.getRequestURI().substring(0,request.getRequestURI().indexOf("/build/")));
		}
		else if (pathInfo.startsWith("/run/"))
		{
			errorLinks.append(request.getRequestURI().substring(0,request.getRequestURI().indexOf("/run/")));
		}
		else
		{
			errorLinks.append("../..");
		}
		errorLinks.append("/'>[List]</a> ");
		return errorLinks.toString();
	}
	
	/**
	 * Handles <i>createbuild</i> option that allows creating, building and running the question in a single 
	 * step, useful to invoke a question preview from URL.
	 * @param sRemainingPath Remaining path which has not yet been parsed
	 * @param request Request information object
	 * @param response Response information object
	 * @throws Exception
	 */
	@Override
	protected void handleCreateBuild(String sRemainingPath,HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		// Check noLinks option
		if (sRemainingPath.startsWith("nolinks/") && !checkNoLinksFromPath(true,sRemainingPath))
		{
			sendError(request,response,HttpServletResponse.SC_NOT_FOUND,
					"Not found","The URL you requested is not provided by this server.", null);
		}
		
		// We create the XML with information about the question
		createQuestion(request,response);
		
		// Finally we redirect to automatically build the question
		StringBuffer buildURI=new StringBuffer();
		buildURI.append(request.getRequestURI().substring(0,request.getRequestURI().indexOf("/createbuild/")));
		buildURI.append("/build/");
		if (!sRemainingPath.equals(""))
		{
			buildURI.append(sRemainingPath);
			if (!sRemainingPath.endsWith("/"))
			{
				buildURI.append('/');
			}
		}
		buildURI.append(request.getParameter("package"));
		buildURI.append('/');
		response.sendRedirect(buildURI.toString());
	}
	
	/**
	 * Handles <i>nolinks</i> option for build.
	 * @param sRemainingPath Remaining path
	 * @param request Servlet request
	 * @param response Servlet response
	 * @return New remaining path (without the <i>nolinks</i> option)
	 * @throws Exception
	 */
	@Override
	protected String handleBuildNoLinks(String sRemainingPath,HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String noLinks="";
		if (sRemainingPath.startsWith("nolinks/"))
		{
			if (checkNoLinksFromPath(false,sRemainingPath))
			{
				noLinks=getNoLinksFromPath(sRemainingPath,true);
			}
			else
			{
				sendError(request,response,HttpServletResponse.SC_NOT_FOUND,"Not found",
					"The URL you requested is not provided by this server.",null);
			}
		}
		return sRemainingPath.substring(noLinks.length());
	}
	
	/**
	 * Prints build error links.<br/><br/>
	 * This implementation takes care of <i>nolinks</i> option.
	 * @param pw Print writer
	 * @param request Servlet request
	 */
	@Override
	protected void printBuildErrorLinks(PrintWriter pw,HttpServletRequest request)
	{
		StringBuffer buildErrorLinks=new StringBuffer();
		buildErrorLinks.append("<p>");
		if (links.rebuildLink)
		{
			buildErrorLinks.append("[<a href='javascript:location.reload()'>Rebuild</a>]");
		}
		if (links.listLink)
		{
			if (links.rebuildLink)
			{
				buildErrorLinks.append(" &nbsp; ");
			}
			buildErrorLinks.append("[<a href='");
			buildErrorLinks.append(
					request.getRequestURI().substring(0,request.getRequestURI().indexOf("/build/")));
			buildErrorLinks.append("/'>List</a>]");
		}
		buildErrorLinks.append("</p>");
		pw.println(buildErrorLinks.toString());
	}
	
	/**
	 * Handles <i>nolinks</i> option for run.<br/><br/>
	 * @param sRemainingPath Remaining path
	 * @param request Servlet request
	 * @param response Servlet response
	 * @return Substring with noLinks option used and its arguments
	 * @throws Exception
	 */
	@Override
	protected String handleRunNoLinks(String sRemainingPath,HttpServletRequest request,
		HttpServletResponse response) throws Exception
	{
		String noLinks="";
		if (isResource(sRemainingPath))
		{
			if (sRemainingPath.startsWith("nolinks/"))
			{
				String pathWithoutResource=getPathWithoutResource(sRemainingPath);
				if (checkNoLinksFromPath(false,pathWithoutResource))
				{
					noLinks=getNoLinksFromPath(pathWithoutResource,false);
				}
				else
				{
					sendError(request,response,HttpServletResponse.SC_NOT_FOUND,
							"Not found","The URL you requested is not provided by this server.",null);
				}
			}
		}
		else
		{
			if (sRemainingPath.startsWith("nolinks/"))
			{
				if (checkNoLinksFromPath(false,sRemainingPath))
				{
					noLinks=getNoLinksFromPath(sRemainingPath,true);
				}
				else
				{
					sendError(request,response,HttpServletResponse.SC_NOT_FOUND,
							"Not found","The URL you requested is not provided by this server.",null);
				}
			}
			else
			{
				links.setAll(true);
			}
		}
		return noLinks;
	}
	
	/**
	 * Prints rebuild link.<br/><br/>
	 * This implementation takes care of noLinks option.
	 * @param sQuestion Package's name that identifies a question
	 * @param noLinks String with noLinks option used and its arguments
	 * @param pw Print writer
	 * @param request Servlet request
	 */
	@Override
	protected void printRebuildLink(String sQuestion,String noLinks,PrintWriter pw,
		HttpServletRequest request)
	{
		StringBuffer rebuildLink=new StringBuffer();
		rebuildLink.append("<p>");
		if (links.rebuildLink)
		{
			rebuildLink.append("<a href='");
			rebuildLink.append(request.getRequestURI().substring(
					0,request.getRequestURI().indexOf("/run/")));
			rebuildLink.append("/build/");
			rebuildLink.append(noLinks);
			rebuildLink.append(sQuestion);
			rebuildLink.append("/'>Rebuild</a>");
		}
		rebuildLink.append("</p>");
		pw.println(rebuildLink.toString());
	}
	
	/**
	 * Prints restart link.<br/><br/>
	 * This implementation takes care of noLinks option and also if the question has ended or not.
	 * @param questionEnded Flag indication if question has ended (true) or not (false)
	 * @param pw Print writer
	 */
	@Override
	protected void printRestartLink(boolean questionEnded,PrintWriter pw)
	{
		if (!questionEnded)
		{
			pw.println(
				"<div style='border: 1px solid #888; padding: 1em; background: #fdc; font-weight: bold'>Error: The question ended without sending back any results.</div>");
			if (links.restartLink)
			{
				pw.println("<p><a href='./'>Restart</a></p>");
			}
		}
		else
		{
			if (links.restartLink)
			{
				pw.println("<p>Question ended. <a href='./'>Restart</a></p>");
			}
			else
			{
				pw.println("<p>Question ended.</p>");
			}
		}
	}
	
	/**
	 * Get HTML code to display links taking care of nolinks option.
	 * @param path URL path to this web application
	 * @param sQuestion Package's name that identifies a question
	 * @return HTML code to display links taking care of nolinks option
	 */
	@Override
	protected String getHTMLLinks(String path,String sQuestion)
	{
		StringBuffer HTMLLinks=new StringBuffer();
		if (links.isSome(true))
		{
			if (links.restartLink || links.versionsLinks || links.plainLink || links.colourLink || 
					links.bigLink)
			{
				boolean addWhitespace=false;
				boolean addSmall=true;
				boolean addEndSmall=false;
				HTMLLinks.append(" [");
				if (links.restartLink)
				{
					HTMLLinks.append("<a href='./'>Restart</a>");
					addWhitespace=true;
				}
				if (links.versionsLinks)
				{
					if (addWhitespace)
					{
						HTMLLinks.append(' ');
					}
					else
					{
						addWhitespace=true;
					}
					HTMLLinks.append("<small>");
					HTMLLinks.append("<a href='./v0'>0</a> <a href='./v1'>1</a> <a href='./v2'>2</a> ");
					HTMLLinks.append("<a href='./v3'>3</a> <a href='./v4'>4</a>");
					addSmall=false;
					addEndSmall=true;
				}
				if (links.plainLink)
				{
					if (addWhitespace)
					{
						HTMLLinks.append(' ');
					}
					else
					{
						addWhitespace=true;
					}
					if (addSmall)
					{
						HTMLLinks.append("<small>");
						addSmall=false;
						addEndSmall=true;
					}
					HTMLLinks.append("<a href='./?access=plain'>Plain</a>");
				}
				if (links.colourLink)
				{
					if (addWhitespace)
					{
						HTMLLinks.append(' ');
					}
					else
					{
						addWhitespace=true;
					}
					if (addSmall)
					{
						HTMLLinks.append("<small>");
						addSmall=false;
						addEndSmall=true;
					}
					HTMLLinks.append("<a href='./?access=bw'>Colour</a>");
				}
				if (links.bigLink)
				{
					if (addWhitespace)
					{
						HTMLLinks.append(' ');
					}
					else
					{
						addWhitespace=true;
					}
					if (addSmall)
					{
						HTMLLinks.append("<small>");
						addSmall=false;
						addEndSmall=true;
					}
					HTMLLinks.append("<a href='./?access=big'>Big</a>");
				}
				if (addEndSmall)
				{
					HTMLLinks.append("</small>");
				}
				HTMLLinks.append(']');
			}
			if (links.rebuildLink)
			{
				HTMLLinks.append(" [<a href='");
				HTMLLinks.append(path);
				HTMLLinks.append("/build/");
				HTMLLinks.append(getNoLinks());
				HTMLLinks.append(sQuestion);
				HTMLLinks.append("/'>Rebuild</a>]");
			}
			if (links.listLink)
			{
				HTMLLinks.append(" [<a href='");
				HTMLLinks.append(path);
				HTMLLinks.append("/'>List</a>]");
			}
			if (links.saveLink)
			{
				HTMLLinks.append(" <small>[<a href='");
				HTMLLinks.append(path);
				HTMLLinks.append("/run/");
				HTMLLinks.append(getNoLinks());
				HTMLLinks.append(sQuestion);
				HTMLLinks.append("/?save'>Save</a>]</small>");
			}
		}
		return HTMLLinks.toString();
	}
}
