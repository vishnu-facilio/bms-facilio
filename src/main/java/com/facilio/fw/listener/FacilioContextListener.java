package com.facilio.fw.listener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import com.facilio.cache.RedisManager;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.transaction.FacilioTransactionManager;

public class FacilioContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
//		System.out.println("Listener Destroyed");
		RedisManager.getInstance().release(); // destroying redis connection pool
	}

	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		initDBConnectionPool();
		
		try {
			try {
			migrateSchemaChanges();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			BeanFactory.initBeans();
			FacilioScheduler.initScheduler();
		//	FacilioTransactionManager.INSTANCE.getTransactionManager();
			
			RedisManager.getInstance().connect(); // creating redis connection pool
			
			//FacilioTimer.schedulePeriodicJob("IotConnector", 15, 20, "facilio");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void migrateSchemaChanges() {
		System.out.println("Flyway migration handler started...");
		
		DataSource ds = FacilioConnectionPool.getInstance().getDataSource();
		
		Flyway flyway = new Flyway();
		flyway.setDataSource(ds);
		flyway.setBaselineOnMigrate(true);
		int mig_status = flyway.migrate();
		
		System.out.println("Flyway migration status: "+mig_status);
	}
	
	/*private void createTables() throws SQLException, IOException {
		File file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/createTables.sql").getFile());
		SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, false);
		scriptRunner.runScript();
	}*/
	
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
			DBUtil.closeAll(conn, stmt, rs);
		}
	}

}
