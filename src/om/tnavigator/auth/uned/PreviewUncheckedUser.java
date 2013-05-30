package om.tnavigator.auth.uned;

/**
 * The implementation of UncheckedUserDetails for a user (not yet) authenticated by PreviewAuth.
 */
public class PreviewUncheckedUser extends GepeqUncheckedUser
{
	/**
	 * @param cookie Cookie value
	 */
	public PreviewUncheckedUser(String cookie)
	{
		super(cookie);
	}
}
