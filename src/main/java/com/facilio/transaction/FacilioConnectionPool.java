package com.facilio.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;


public enum FacilioConnectionPool {
	INSTANCE;
	
	private DataSource ds = null;
	
	private FacilioConnectionPool() {
		Context initCtx = null;
		Context envCtx = null;
		try {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/pcmDB");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError("Unable to initialize DB connection pool due to the following error : \n"+e.getMessage());
		}
		finally {
			if(initCtx != null) {
				try {
					initCtx.close();
				} catch (NamingException e) {
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
		
		
			TransactionManager tm = FTransactionManager.getTransactionManager();
			try {
				Transaction t = tm.getTransaction();
				if(t != null ) {
                   return   ((FacilioTransaction)t).getConnection();
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}
			return getConnectionFromPool();
		
		
	/*	Properties info =new Properties();
	//	info.setProperty(TransactionalDriver.userName, "root");
	//	info.setProperty(TransactionalDriver.password, "");
		info.put(TransactionalDriver.XADataSource, ds);
		//info.getProperty(TransactionalDriver.dynamicClass);
		//xaDataSource = info.get(TransactionalDriver.XADataSource);
		Connection c = DriverManager.getConnection("jdbc:arjuna:java:comp/env/jdbc/pcmDB",info);
	//	print(c);
		return c;*/
	}
	
    public Connection getConnectionFromPool() throws SQLException {
        return ds.getConnection();
	}
}
