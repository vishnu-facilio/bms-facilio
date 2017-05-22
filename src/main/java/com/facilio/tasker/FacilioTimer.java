package com.facilio.tasker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.facilio.transaction.FacilioConnectionPool;

public class FacilioTimer {
	
	private FacilioTimer() {
		// TODO Auto-generated constructor stub
	}

	public static long schedulePeriodicJob(String jobName, long delay, int period) throws Exception
	{
		String orgId = "test1";
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		return addJob(orgId, jobName, true, period, nextExecutionTime);
	}
	
	public static long scheduleOneTimeJob(String jobName, int delay) throws Exception {
		String orgId = "test1";
		long nextExecutionTime = (System.currentTimeMillis()/1000)+delay;
		
		return addJob(orgId, jobName, false, delay, nextExecutionTime);
	}
	
	static long addJob(String orgId, String jobName, boolean isPeriodic, int period, long nextExecutionTime) throws Exception {
		
		/*
		create table Jobs (
				  JOBID BIGINT AUTO_INCREMENT PRIMARY KEY,
				  ORGID VARCHAR(50),
				  JOBNAME VARCHAR(50),
				  ISPERIODIC BOOLEAN,
				  PERIOD INT,
				  NEXTEXECUTIONTIME BIGINT
				)
		*/
		
		if(orgId == null || orgId.isEmpty()) {
			throw new IllegalArgumentException("Invalid Org Id");
		}
		
		if(jobName == null || jobName.isEmpty()) {
			throw new IllegalArgumentException("Invalid JobName");
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Jobs (ORGID, JOBNAME, ISPERIODIC, PERIOD, NEXTEXECUTIONTIME) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setString(1, orgId);
			pstmt.setString(2, jobName);
			pstmt.setBoolean(3, isPeriodic);
			pstmt.setInt(4, period);
			pstmt.setLong(5, nextExecutionTime);
			
			if(pstmt.executeUpdate() < 1) {
				throw new Exception("Unable to schedule");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long jobId = rs.getLong(1);
				System.out.println("Added job with job id : "+jobId);
				return jobId;
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			if(rs != null) {
				rs.close();
			}
			if(pstmt != null) {
				pstmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
		
	}
}
