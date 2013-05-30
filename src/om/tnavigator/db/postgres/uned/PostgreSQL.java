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
package om.tnavigator.db.postgres.uned;

import java.sql.ResultSet;
import java.sql.SQLException;

import om.OmVersion;
import om.tnavigator.db.DatabaseAccess.Transaction;
import om.tnavigator.Log;

import util.misc.NavVersion;
import util.misc.NameValuePairs;

// Perhaps it will make sense add these changes to superclass so this class won't be needed

// UNED: 21-06-2011 - dballestin
/**
 * Specialisation of PostgreSQL for UNED.
 */
public class PostgreSQL extends om.tnavigator.db.postgres.PostgreSQL
{
	/**
	 * @param prefix the database prefix to use.
	 */
	public PostgreSQL(String prefix)
	{
		super(prefix);
	}

	/* ok, add database updates here , it would probabaly be better to add this to a config file rather than hard code, but its sooooo rare
	 * that I think here will suffice
	 * @param dat database connection
	 * @param l Log
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * */
	@Override
	protected void upgradeDatabaseToLatest(Transaction dat, Log l) throws SQLException,IllegalArgumentException
	{
		/* add all database updates here
		 * first get the database version by reading the navconfig table
		 */		
		String currversion=OmVersion.getVersion();	
		l.logDebug("DatabaseUpgrade "+currversion);

		NavVersion DBversion = new NavVersion("");
				
		/* read the navconfig table into a namepair list */
		ResultSet rs=queryNavConfig(dat);
		
		NameValuePairs navconfigDB=new NameValuePairs();
		while(rs.next())
		{ 
			navconfigDB.add(rs.getString(1),rs.getString(2));
		}
		
		/* now find the version */
		String Names[]=navconfigDB.getNames();
		String Values[]=navconfigDB.getValues();
		/* read the current version from the database */
		for (int i=0;i<Names.length;i++)
		{
			if (Names[i].compareToIgnoreCase("dbversion")==0)
			{
				DBversion.setVersion(Values[i]);
			}
		}
		/* if the current database version is less than the navigator version, check for updates */
		if(DBversion.isLessThanStr(currversion))
		{
			/* make sure these are listed in version order, because the function updates the DB version as we go */			
			l.logDebug("DatabaseUpgrade", "Applying database upgrades, version before update "+DBversion.getVersion());
			
			updateDatabase("1.10.1",DBversion,
					"ALTER TABLE "+getPrefix()+"params ALTER COLUMN paramvalue TYPE VARCHAR(4000)",
					l,dat);
					
			/* finally having applied all the updates set the DB version to the current */
			l.logDebug("DatabaseUpgrade", "Update DB version to current "+currversion);
			dat.update("UPDATE "+getPrefix()+"navconfig SET value = \'"+currversion+"\' where name=\'dbversion\'");
			
			applyUpdateForEmailNotification(dat, l, DBversion);
			
		}
		else
		{
			l.logDebug("DatabaseUpgrade", "Database up to date at version "+DBversion.getVersion()+" no updates attempted.");
		}
	}
	
	private void applyUpdateForEmailNotification(Transaction dat, Log l, NavVersion DBversion) 
			throws SQLException,IllegalArgumentException 
	{
		
		if (!columnExistsInTable(dat, "tests", "datewarningemailsent"))
		{
			updateDatabase("1.12",DBversion,
				"ALTER TABLE " + getPrefix() + "tests ADD dateWarningEmailSent TIMESTAMP WITHOUT TIME ZONE DEFAULT ('now'::text)::timestamp(6) with time zone",
				l,dat);
		}
	}
}
