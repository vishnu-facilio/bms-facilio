package com.facilio.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public enum FacilioConnectionPool {
	INSTANCE;
	
	private DataSource ds = null;
	
	private FacilioConnectionPool() {
		Context initCtx = null;
		Context envCtx = null;
		try {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/BmsDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ExceptionInInitializerError("Unable to initialize DB connection pool due to the following error : \n"+e.getMessage());
		}
		finally {
			if(initCtx != null) {
				try {
					initCtx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static FacilioConnectionPool getInstance() {
		return INSTANCE;
	}
	
	public DataSource getDataSource() {
		return ds;
	}
	
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}
