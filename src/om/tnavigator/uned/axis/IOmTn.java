package om.tnavigator.uned.axis;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface of web service for Om Test Navigator Web Application.
 */
public interface IOmTn extends Remote
{
	/**
	 * Checks if exist the test and/or deploy xml files for the indicated test name and version.  
	 * @param testName Test's name
	 * @param version Version
	 * @return true if exist the test and/or deploy xml files for the indicated test name and version, 
	 * false otherwise
	 * @throws RemoteException
	 */
	public boolean existTestXmls(String testName,int version) throws RemoteException;
	
	/**
	 * Check if exist the deploy xml file for the indicated question.
	 * @param packageName Package's name
	 * @return true if exist the deploy xml file for the indicated question name, false otherwise
	 * @throws RemoteException
	 */
	public boolean existQuestionXml(String packageName) throws RemoteException;
	
	/**
	 * Checks if exists the jar file for the indicated question.  
	 * @param packageName Package's name
	 * @param version Version or null to get last version
	 * @return true if exists the question's jar, false otherwise
	 * @throws RemoteException
	 */
	public boolean existQuestionJar(String packageName,String version) throws RemoteException;
	
	/**
	 * @param packageName Package's name
	 * @param version Version or null to get date from last version jar file
	 * @return Question's jar last modified date measured as the number of milliseconds since the 
	 * standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT
	 * @throws RemoteException
	 */
	public long getQuestionJarLastModified(String packageName,String version) throws RemoteException;
	
	
	/**
	 * @param testName Test's name
	 * @return Test xml file last modified date measured as the number of milliseconds since the 
	 * standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT
	 * @throws RemoteException
	 */
	public long getTestXmlLastModified(String testName) throws RemoteException;
	
	/**
	 * @param testName Test's name
	 * @param version Version
	 * @return Test xml file last modified date measured as the number of milliseconds since the 
	 * standard base time known as "the epoch", namely January 1, 1970, 00:00:00 GMT
	 * @throws RemoteException
	 */
	public long getDeployXmlLastModified(String testName,int version) throws RemoteException;
	
	/**
	 * Upload a test xml file from an array of Base64 encoded strings.
	 * @param testName Test's name
	 * @param base64TestXml Text xml file content as an array of Base64 encoded strings
	 * @return true if uploading is succesful, false otherwise
	 * @throws RemoteException
	 */
	public boolean uploadTestXml(String testName,String[] base64TestXml) throws RemoteException;
	
	/**
	 * Upload a deploy xml file from an array of Base64 encoded strings.
	 * @param name Test's name or question's name
	 * @param version Version (0 for questions)
	 * @param base64DeployXml Deploy xml file content as an array of Base64 encoded strings
	 * @return true if uploading is succesful, false otherwise
	 * @throws RemoteException
	 */
	public boolean uploadDeployXml(String name,int version,String[] base64DeployXml) throws RemoteException;
	
	/**
	 * Upload a question jar from an array of Base64 encoded strings.
	 * @param packageName Package's name (encrypted if defined a GEPEQ decryption password)
	 * @param version Version or null to upload a new version
	 * @param base64QuestionJar Question jar content as an array of Base64 encoded strings
	 * @return true if uploading is succesful, false otherwise
	 * @throws RemoteException
	 */
	public boolean uploadQuestionJar(String packageName,String version,String[] base64QuestionJar) 
		throws RemoteException;
	
	/**
	 * Delete test and/or deploy xml files for the indicated test name and version if they exist.
	 * @param testName Test's name
	 * @param version Version
	 * @throws RemoteException
	 */
	public void deleteTestXmls(String testName,int version) throws RemoteException;
	
	/**
	 * Delete deploy xml file for the indicated question if it exists.
	 * @param packageName Package's name
	 * @throws RemoteException
	 */
	public void deleteQuestionXml(String packageName) throws RemoteException;
	
	/**
	 * Deletes a question jar file for the indicated question.
	 * @param packageName Package's name
	 * @throws RemoteException
	 */
	public void deleteQuestionJar(String packageName) throws RemoteException;
	
	/**
	 * Stop all active Test Navigator sessions that are using a question.<br/><br/>
	 * It is needed to be sure that the jar file for that question at <i>questioncache</i> folder of
	 * OM Question Engine web application is unlocked so it can be deleted.
	 * @param packageName Package's name
	 * @throws RemoteException
	 */
	public void stopAllSessionsForQuestion(String packageName) throws RemoteException;
	
	/**
	 * @param oucu OUCU
	 * @return true if OUCU is available, false if it is used
	 * @throws RemoteException
	 */
	public boolean isOUCUAvailable(String oucu) throws RemoteException;
	
	/**
	 * Get questions releases metadata as string.<br/><br/>
	 * The result string has the following format:<br/><br/>
	 * <b>u</b><i>question_1_author_id</i><b>.q</b><i>question_1_id</i><b>;</b>
	 * <i>question_1_publisher_oucu</i><b>;</b><i>question_1_release_date</i><b>;</b>
	 * <i>question_1_start_date</i><b>;</b><i>question_1_close_date</i><b>;</b>
	 * <i>question_1_delete_date</i><b>;</b><br/>
	 * <b>u</b><i>question_2_author_id</i><b>.q</b><i>question_2_id</i><b>;</b>
	 * <i>question_2_publisher_oucu</i><b>;</b><i>question_2_release_date</i><b>;</b>
	 * <i>question_2_start_date</i><b>;</b><i>question_2_close_date</i><b>;</b>
	 * <i>question_2_delete_date</i><b>;</b><br/>
	 * ...<br/>
	 * <b>u</b><i>question_n_author_id</i><b>.q</b><i>question_n_id</i><b>;</b>
	 * <i>question_n_publisher_oucu</i><b>;</b><i>question_n_release_date</i><b>;</b>
	 * <i>question_n_start_date</i><b>;</b><i>question_n_close_date</i><b>;</b>
	 * <i>question_n_delete_date</i><br/><br/>
	 * where release dates have the following format:<br/><br/>
	 * <i>yyyy</i>-<i>MM</i>-<i>dd</i> <i>HH</i>:<i>mm</i>:<i>ss</i>
	 * @return Questions releases metadata as string
	 * @throws RemoteException
	 */
	public String getQuestionsReleasesMetadata() throws RemoteException;
	
	/**
	 * Get question release metadata as string.<br/><br/>
	 * The result string has the following format:<br/><br/>
	 * <i>publisher_oucu</i><b>;</b><i>release_date</i><b>;</b><i>start_date</i><b>;</b><i>close_date</i><b>;</b>
	 * <i>delete_date</i><b>;</b><i>warning_date</i><b>;</b><i>all_users_allowed</i><b>;</b>
	 * <i>user_oucu_1</i><b>;</b><i>user_oucu_2</i><b>;</b> ... <b>;</b><i>user_oucu_n</i><b>;&#64;;</b>
	 * <i>user_authid_1</i><b>;</b><i>user_authid_2</i><b>;</b> ... <b>;</b><i>user_authid_n</i><br/><br/>
	 * where <i>release_date</i>,<i>start_date</i>,<i>close_date</i>, <i>delete_date</i> and <i>warning_date</i> 
	 * have the following format:<br/><br/>
	 * <i>yyyy</i>-<i>MM</i>-<i>dd</i> <i>HH</i>:<i>mm</i>:<i>ss</i><br/><br/>
	 * and <i>all_users_allowed</i> can be <b>true</b> or <b>false</b>.
	 * @param packageName Package's name
	 * @return Question release metadata as string
	 * @throws RemoteException
	 */
	public String getQuestionReleaseMetadata(String packageName) throws RemoteException;
	
	/**
	 * Get tests releases metadata as string.<br/><br/>
	 * The result string has the following format:<br/><br/>
	 * <b>u</b><i>test_1_author_id</i><b>.t</b><i>test_1_id</i><b>;</b><i>test_1_version</i><b>;</b>
	 * <i>test_1_publisher_oucu</i><b>;</b><i>test_1_release_date</i><b>;</b><i>test_1_start_date</i><b>;</b>
	 * <i>test_1_close_date</i><b>;</b><i>test_1_delete_date</i><b>;</b><br/>
	 * <b>u</b><i>test_2_author_id</i><b>.t</b><i>test_2_id</i><b>;</b><i>test_2_version</i><b>;</b>
	 * <i>test_2_publisher_oucu</i><b>;</b><i>test_2_release_date</i><b>;</b><i>test_2_start_date</i><b>;</b>
	 * <i>test_2_close_date</i><b>;</b><i>test_2_delete_date</i><b>;</b><br/>
	 * ...<br/>
	 * <b>u</b><i>test_n_author_id</i><b>.t</b><i>test_n_id</i><b>;</b><i>test_n_version</i><b>;</b>
	 * <i>test_n_publisher_oucu</i><b>;</b><i>test_n_release_date</i><b>;</b><i>test_n_start_date</i><b>;</b>
	 * <i>test_n_close_date</i><b>;</b><i>test_n_delete_date</i><br/><br/>
	 * where release dates have the following format:<br/><br/>
	 * <i>yyyy</i>-<i>MM</i>-<i>dd</i> <i>HH</i>:<i>mm</i>:<i>ss</i>
	 * @return Tests releases metadata as string
	 * @throws RemoteException
	 */
	public String getTestsReleasesMetadata() throws RemoteException;
	
	/**
	 * Get test release metadata as string.<br/><br/>
	 * The result string has the following format:<br/><br/>
	 * <i>publisher_oucu</i><b>;</b><i>release_date</i><b>;</b><i>start_date</i><b>;</b><i>close_date</i><b>;</b>
	 * <i>delete_date</i><b>;</b><i>warning_date</i><b>;</b><i>feedback_date</i><b>;</b><i>assessement</i><b>;</b>
	 * <i>all_users_allowed</i><b>;</b><i>allow_admin_reports</i><b>;</b><i>free_summary</i><b>;</b>
	 * <i>free_stop</i><b>;</b><i>summary_questions</i><b>;</b><i>summary_scores</i><b>;</b>
	 * <i>summary_attempts</i><b>;</b><i>navigation</i><b>;</b><i>nav_location</i><b>;</b>
	 * <i>redo_question</i><b>;</b><i>redo_test</i><b>;</b>
	 * <i>support_contacts</i><b>;</b><i>evaluators</i><b>;</b>
	 * <i>user_oucu_1</i><b>;</b><i>user_oucu_2</i><b>;</b> ... <b>;</b><i>user_oucu_n</i><b>;&#64;;</b>
	 * <i>user_authid_1</i><b>;</b><i>user_authid_2</i><b>;</b> ... <b>;</b><i>user_authid_n</i><b>;&#64;;</b>
	 * <i>admin_oucu_1</i><b>;</b><i>admin_oucu_2</i><b>;</b> ... <b>;</b><i>admin_oucu_n</i><b>;&#64;;</b>
	 * <i>admin_authid_1</i><b>;</b><i>admin_authid_2</i><b>;</b> ... <b>;</b><i>admin_authid_n</i><br/><br/>
	 * where <i>release_date</i>,<i>start_date</i>,<i>close_date</i>, <i>delete_date</i>, <i>warning_date</i> 
	 * and <i>feedback_date</i> have the following format:<br/><br/>
	 * <i>yyyy</i>-<i>MM</i>-<i>dd</i> <i>HH</i>:<i>mm</i>:<i>ss</i><br/><br/>
	 * , <i>all_users_allowed</i>, <i>allow_admin_reports</i>, <i>free_summary</i>, <i>free_stop</i>, 
	 * <i>summary_questions</i>, <i>summary_scores</i>, <i>summary_attempts</i>, <i>navigation</i> and 
	 * <i>redo_test</i> can be <b>true</b> or <b>false</b><br/><br/>
	 * , <i>assessement</i> can be <b>ASSESSEMENT_NOT_ASSESSED</b>, <b>ASSESSEMENT_REQUIRED</b> or 
	 * <b>ASSESSEMENT_OPTIONAL</b><br/><br/>
	 * , <i>nav_location</i> can be <b>NAVLOCATION_LEFT</b>, <b>NAVLOCATION_BOTTOM</b> or <b>NAVLOCATION_WIDE</b>
	 * <br/><br/>
	 * , <i>redo_question</i> can be <b>YES</b>, <b>NO</b> or <b>ASK</b><br/><br/>
	 * and <i>support_contacts</i> and <i>evaluators</i> have the following format:<br/><br/>
	 * <i>address_filter_type_1</i><b>:</b><i>address_filter_value_1</i><b>:</b><i>address_list_1</i><b>:</b><br/>
	 * <i>address_filter_type_2</i><b>:</b><i>address_filter_value_2</i><b>:</b><i>address_list_2</i><b>:</b><br/>
	 * ...<br/>
	 * <i>address_filter_type_n</i><b>:</b><i>address_filter_value_n</i><b>:</b><i>address_list_n</i><br/><br/>
	 * where <i>address_list_&lt;i&gt;</i> is a list of email addresses separated by commas<br/><br/> 
	 * , <i>address_filter_type&lt;i&gt;</i> can be <b>all</b>, <b>single-oucu</b>, <b>range-oucu</b>, 
	 * <b>single-name</b>, <b>range-name</b>, <b>single-surname</b> or <b>range-surname</b><br/><br/> 
	 * and <i>address_filter_value_&lt;i&gt;</i> format depends on value of <i>address_filter_type&lt;i&gt;</i> as
	 * indicated in the following table:<br/><br/>
	 * <table border="1">
	 * <tr><th>address_filter_type</th><th>address_filter_value format</th></tr>
	 * <tr><td>all</td><td>&nbsp;</td></tr>
	 * <tr><td>single-oucu</td><td>List of OUCUs separated by commas for filtering users by OUCU</td></tr>
	 * <tr><td>range-oucu</td><td>List of alphabetical ranges separated by commas for filtering users by OUCU</td>
	 * </tr>
	 * <tr><td>single-name</td><td>List of names separated by commas for filtering users by name</td></tr>
	 * <tr><td>range-name</td><td>List of alphabetical ranges separated by commas for filtering users by name</td>
	 * </tr>
	 * <tr><td>single-surname</td><td>List of surnames separated by commas for filtering users by surname</td></tr>
	 * <tr><td>range-surname</td><td>List of alphabetical ranges separated by commas for filtering users by 
	 * surname</td></tr>
	 * </table><br/>
	 * where alphabetical ranges have the following format:<br/><br/>
     * <i>lower_limit_characters_1</i><b>-</b><i>upper_limit_characters_1</i><b>,</b><br/>
	 * <i>lower_limit_characters_2</i><b>-</b><i>upper_limit_characters_2</i><b>,</b><br/>...<br/>
	 * <i>lower_limit_characters_n</i><b>-</b><i>upper_limit_characters_n</i><br/><br/>
	 * for example <i>A-F:JA-JO</i> for filtering users with the required attribute within the alphabetical range 
	 * between <i>A</i> and <i>F</i> or between <i>JA</i> and <i>JO</i> (all limits inclusive). 
	 * @param testName Test's name
	 * @param version Version
	 * @return Test release metadata as string.
	 * @throws RemoteException
	 */
	public String getTestReleaseMetadata(String testName,int version) throws RemoteException;
	
	/**
	 * Get all versions of a test released as string.
	 * The result string has the following format:<br/><br/>
	 * <i>version_1</i><b>;</b><i>version_2</i><b>;</b> ... <b>;</b><i>version_n</i><br/><br/>
	 * where <i>version_1</i>, <i>version_2</i>, ... , <i>version_n</i> have integer values. 
	 * @param testName Test's name
	 * @return All versions of a test released
	 * @throws RemoteException
	 */
	public String getTestReleaseVersions(String testName) throws RemoteException;
}
