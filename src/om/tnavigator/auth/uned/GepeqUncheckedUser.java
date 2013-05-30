package om.tnavigator.auth.uned;

import om.tnavigator.auth.UncheckedUserDetails;

/**
 * The implementation of UncheckedUserDetails for a user (not yet) authenticated by GepeqAuth.
 */
public class GepeqUncheckedUser implements UncheckedUserDetails
{
	private String cookie;
	private String username;
	private boolean usernameInitialized;
	
	/**
	 * @param cookie Cookie value
	 */
	public GepeqUncheckedUser(String cookie)
	{
		this.cookie=cookie;
		this.username=null;
		this.usernameInitialized=false;
	}
	
	@Override
	public String getCookie()
	{
		return cookie;
	}
	
	@Override
	public String getPersonID()
	{
		return getUsername();
	}
	
	@Override
	public String getUsername()
	{
		if (!usernameInitialized)
		{
			username=readUsernameFromCookie(cookie);
			usernameInitialized=true;
		}
		return username;
	}
	
	/**
	 * @param cookie Cookie (string)
	 * @return Username from cookie
	 */
	private String readUsernameFromCookie(String cookie)
	{
		String username=null;
		if (cookie!=null)
		{
			int endPos=cookie.indexOf(GepeqAuth.COOKIE_SEPARATOR);
			if (endPos!=-1)
			{
				username=cookie.substring(0,endPos);
			}
		}
		return username;
	}
}
