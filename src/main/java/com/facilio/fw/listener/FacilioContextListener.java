package com.facilio.fw.listener;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.cache.RedisManager;
import com.facilio.fw.BeanFactory;
import com.facilio.kinesis.KinesisProcessor;
import com.facilio.sql.DBUtil;
import com.facilio.sql.SQLScriptRunner;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.transaction.FacilioConnectionPool;

public class FacilioContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
//		System.out.println("Listener Destroyed");
		RedisManager.getInstance().release(); // destroying redis connection pool
		FacilioScheduler.stopSchedulers();
	}

	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		initDBConnectionPool();
		Operator test = Operator.OPERATOR_MAP.get(1);
		try {
			try {
			migrateSchemaChanges();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			BeanFactory.initBeans();
			
			if(Boolean.parseBoolean(AwsUtil.getConfig("schedulerServer"))) {
				FacilioScheduler.initScheduler();
			}
		//	FacilioTransactionManager.INSTANCE.getTransactionManager();
			
			RedisManager.getInstance().connect(); // creating redis connection pool
			
			File file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/leedconsole.sql").getFile());
			SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, null);
			//Connection c = FacilioConnectionPool.getInstance().getConnection();
			try
			{
			scriptRunner.runScript();
			}
			catch(Exception e)
			{
				
			}
			finally
			{
				
			}
			file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/eventconsole.sql").getFile());
			scriptRunner = new SQLScriptRunner(file, true, null);
		//	c = FacilioConnectionPool.getInstance().getConnection();
			try
			{
			scriptRunner.runScript();
			}
			catch(Exception e)
			{
				
			}
			
			try {
				KinesisProcessor.startProcessor();
			} catch (Exception e){
				e.printStackTrace();
			}


//			FacilioTimer.schedulePeriodicJob(2, "suresh", 30, 300, "system");
			
//			ScheduleInfo schedule = new ScheduleInfo();
//			schedule.setFrequencyType(FrequencyType.WEEKLY);
//			schedule.setFrequency(2);
//			schedule.addTime(LocalTime.of(16, 53));
//			schedule.addValue(DayOfWeek.THURSDAY.getValue());
////			schedule.setWeekFrequency(3);
//			
//			FacilioTimer.scheduleCalendarJob(2, "suresh", 30, schedule, "priority", 21);
			
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
