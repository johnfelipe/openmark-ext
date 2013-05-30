package om.tnavigator.auth.uned;

/**
 * The implementation of UserDetails for a user authenticated by PreviewAuth.
 */
public class PreviewUser extends GepeqUser
{
	/**
	 * @param cookie Cookie value
	 * @param username Username
	 */
	public PreviewUser(String cookie,String username)
	{
		super(cookie,username,null);
	}
}
