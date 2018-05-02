package com.facilio.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.transaction.FacilioConnectionPool;

public class DBUtil {
	private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());

	public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Exception while closing resource ", e);
			}
		}
		closeAll(conn, stmt);
	}
	
	public static void closeAll(Connection conn, Statement stmt) {
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Exception while closing resource ", e);
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Exception while closing resource ", e);
			}
		}
	}
	
	public static void closeAll(Statement stmt, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Exception while closing resource ", e);
			}
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.SEVERE, "Exception while closing resource ", e);
			}
		}
	}
	
	public static HashMap getRecord(String query) throws Exception
	{
		Connection c = FacilioConnectionPool.getInstance().getConnection();
		Statement stmt =null;
		ResultSet rs = null;
		
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next())
			{
				ResultSetMetaData md = rs.getMetaData();
				  int columns = md.getColumnCount();
				HashMap row = new HashMap(columns);
			     for(int i=1; i<=columns; ++i){           
			      row.put(md.getColumnName(i),rs.getObject(i));
			     }
			     return row;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Exception while getting record ", e);
			throw e;
		}
		finally
		{
			closeAll(c, stmt, rs);
		}
		return null;
	}
	
}
