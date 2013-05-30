package om.tnavigator.uned;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import om.tnavigator.db.DatabaseAccess;
import util.misc.Strings;

/**
 * Utility class used to define an user filter that can be used with support contacts and evaluators to restrict
 * the received mails to the actions of the users accepted by the filter.
 */
public class UnedUserFilter
{
	public final static String ALL_USER_FILTER="all";
	public final static String SINGLE_OUCU_USER_FILTER="single-oucu";
	public final static String RANGE_OUCU_USER_FILTER="range-oucu";
	public final static String SINGLE_NAME_USER_FILTER="single-name";
	public final static String RANGE_NAME_USER_FILTER="range-name";
	public final static String SINGLE_SURNAME_USER_FILTER="single-surname";
	public final static String RANGE_SURNAME_USER_FILTER="range-surname";
	
	private final static String VALUE_SEPARATOR=",";
	
	private final static char RANGE_SEPARATOR_CHAR='-';
	
	private UnedNavigatorServlet ns;
	private String type;
	private String value;
	
	/**
	 * Constructs an user filter without defining the navigator servlet.<br/><br>
	 * <b>WARNING</b>: Calling method <i>accept</i> can cause an error because a valid navigator servlet is needed.
	 * @param type Filter type
	 * @param value Filter value
	 */
	public UnedUserFilter(String type,String value)
	{
		this(null,type,value);
	}
	
	/**
	 * Constructs an user filter.
	 * @param ns Navigator servlet
	 * @param type Filter type
	 * @param value Filter value
	 */
	public UnedUserFilter(UnedNavigatorServlet ns,String type,String value)
	{
		this.ns=ns;
		this.type=type;
		this.value=value;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type=type;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value=value;
	}
	
	/**
	 * @param sOUCU User's OUCU
	 * @return true if the user with the indicated OUCU is accepted by the filter, false otherwise
	 * @throws IOException
	 */
	public boolean accept(String sOUCU) throws IOException
	{
		boolean ok=false;
		if (ALL_USER_FILTER.equals(type))
		{
			ok=true;
		}
		else if (SINGLE_OUCU_USER_FILTER.equals(type))
		{
			ok=checkSingle(sOUCU);
		}
		else if (RANGE_OUCU_USER_FILTER.equals(type))
		{
			ok=checkRange(sOUCU);
		}
		else if (SINGLE_NAME_USER_FILTER.equals(type))
		{
			String sName=getUserName(sOUCU);
			ok=checkSingle(sName);
		}
		else if (RANGE_NAME_USER_FILTER.equals(type))
		{
			String sName=getUserName(sOUCU);
			ok=checkRange(sName);
		}
		else if (SINGLE_SURNAME_USER_FILTER.equals(type))
		{
			String sSurname=getUserSurname(sOUCU);
			ok=checkSingle(sSurname);
		}
		else if (RANGE_SURNAME_USER_FILTER.equals(type))
		{
			String sSurname=getUserSurname(sOUCU);
			ok=checkRange(sSurname);
		}
		return ok;
	}
	
	private String getUserName(String sOucu) throws IOException
	{
		return getUserFieldValue(sOucu,"name");
	}
	
	private String getUserSurname(String sOucu) throws IOException
	{
		return getUserFieldValue(sOucu,"surname");
	}
	
	private String getUserFieldValue(String sOucu,String fieldName) throws IOException
	{
		String fieldValue=null;
		DatabaseAccess.Transaction dat=null;
		try
		{
			StringBuffer query=new StringBuffer("SELECT ");
			query.append(fieldName);
			query.append(" FROM users WHERE oucu=");
			query.append(Strings.sqlQuote(sOucu));
			
			dat=ns.getLoginDatabaseAccess().newTransaction();
			ResultSet rs=dat.query(query.toString());
			if (rs.next())
			{
				fieldValue=rs.getString(1);
			}
		}
		catch (SQLException se)
		{
			StringBuffer errorMessage=new StringBuffer("Error accessing login database: ");
			errorMessage.append(se.getMessage());
			throw new IOException(errorMessage.toString(),se);
		}
		finally
		{
			if (dat!=null)
			{
				dat.finish();
			}
		}
		return fieldValue;
	}
	
	private boolean checkSingle(String valueToCheck)
	{
		boolean ok=false;
		if (valueToCheck!=null && value!=null && !"".equals(value))
		{
			for (String singleValue:value.split(VALUE_SEPARATOR))
			{
				if (singleValue.equals(valueToCheck))
				{
					ok=true;
					break;
				}
			}
		}
		return ok;
	}
	
	private boolean checkRange(String valueToCheck)
	{
		boolean ok=false;
		if (valueToCheck!=null && value!=null && !"".equals(value))
		{
			for (String singleValue:value.split(VALUE_SEPARATOR))
			{
				int i=singleValue.indexOf(RANGE_SEPARATOR_CHAR);
				String lowerLimitValue=singleValue.substring(0,i);
				String upperLimitValue=singleValue.substring(i+1);
				String valueToCheckTmp=valueToCheck.substring(0,lowerLimitValue.length());
				if ("".equals(lowerLimitValue) || lowerLimitValue.compareToIgnoreCase(valueToCheckTmp)<=0)
				{
					valueToCheckTmp=valueToCheck.substring(0,upperLimitValue.length());
					if ("".equals(upperLimitValue) || upperLimitValue.compareToIgnoreCase(valueToCheckTmp)>=0)
					{
						ok=true;
						break;
					}
				}
			}
		}
		return ok;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof UnedUserFilter && ns==((UnedUserFilter)obj).ns && 
			type.equals(((UnedUserFilter)obj).type) && value.equalsIgnoreCase(((UnedUserFilter)obj).value);
	}
}
