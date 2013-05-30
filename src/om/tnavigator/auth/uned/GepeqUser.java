package om.tnavigator.auth.uned;

import java.util.HashSet;
import java.util.Set;

import om.tnavigator.auth.UserDetails;

/**
 * The implementation of UserDetails for a user authenticated by SimpleAuth.
 */
public class GepeqUser implements UserDetails
{
	public final static GepeqUser NOT_LOGGED_IN=new GepeqUser(null,null,null);
	
	private String cookie;
	private String username;
	private String email;
	
	private Set<String> groups=new HashSet<String>();
	
	/**
	 * @param cookie Cookie value
	 * @param username Username
	 * @param email Email for mail alerts or null if user should not receive test mail
	 */
	public GepeqUser(String cookie,String username,String email)
	{
		this.cookie=cookie;
		this.username=username;
		this.email=email;
	}
	
	public void addGroup(String group)
	{
		groups.add(group);
	}
	
	@Override
	public String getAuthIDsAsString()
	{
		StringBuffer authIds=new StringBuffer();
		for (String group:groups)
		{
			authIds.append(group);
			authIds.append(' ');
		}
		return authIds.length()>0?authIds.substring(0,authIds.length()-1):"";
	}
	
	@Override
	public String getCookie()
	{
		return cookie;
	}
	
	@Override
	public String getPersonID()
	{
		return username;
	}
	
	@Override
	public String getUsername()
	{
		return username;
	}
	
	@Override
	public boolean hasAuthID(String sAuthId)
	{
		return groups.contains(sAuthId);
	}
	
	@Override
	public boolean isLoggedIn()
	{
		return this!=NOT_LOGGED_IN;
	}
	
	@Override
	public boolean shouldReceiveTestMail()
	{
		return email!=null;
	}
	
	@Override
	public boolean isSysTest()
	{
		return username!=null && username.startsWith("!tst");
	}
}
