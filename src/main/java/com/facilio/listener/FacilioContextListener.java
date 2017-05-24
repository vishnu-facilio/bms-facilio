package com.facilio.listener;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.facilio.sql.SQLScriptRunner;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.transaction.FacilioConnectionPool;


public class FacilioContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
//		System.out.println("Listener Destroyed");
	}

	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		initDBConnectionPool();
		
		try {
			createTables();
			
//			System.out.println("Scheduling=>"+FacilioTimer.schedulePeriodicJob("test1", 60, 60, "facilio"));
//			System.out.println("Scheduling=>"+FacilioTimer.schedulePeriodicJob("test2", 60, 120, "facilio"));
			
			FacilioScheduler.initScheduler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createTables() throws SQLException, IOException {
		File file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/createTables.sql").getFile());
		SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, false);
		scriptRunner.runScript();
	}
	
	private void initDBConnectionPool() {
		System.out.println("Initializing DB Connection Pool");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select 1"); //Test Connection
			
			while(rs.next()) {
				System.out.println(rs.getInt(1));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
