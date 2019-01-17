package com.facilio.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.transaction.FacilioConnectionPool;

public class DBUtil {
	private static final Logger LOGGER = LogManager.getLogger(DBUtil.class.getName());

	public static void close (Statement stmt) {
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing statement ", e);
			}
		}
	}
	
	public static void close (Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing connection ", e);
			}
		}
	}
	
	public static void close (ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing resultset ", e);
			}
		}
	}
	
	public static void closeAll(Connection conn, Statement stmt, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing resultset ", e);
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
				LOGGER.log(Level.ERROR, "Exception while closing statement ", e);
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing connection ", e);
			}
		}
	}
	
	public static void closeAll(Statement stmt, ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing resultset ", e);
			}
		}
		
		if(stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.log(Level.ERROR, "Exception while closing statement ", e);
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
			LOGGER.log(Level.ERROR, "Exception while getting record ", e);
			throw e;
		}
		finally
		{
			closeAll(c, stmt, rs);
		}
		return null;
	}
	
}
