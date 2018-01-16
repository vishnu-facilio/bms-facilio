package com.facilio.tasker.job;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class JobStore {
	
	public static void addJob(JobContext job) throws Exception {
		if(job != null) {
			
			checkForNull(job);
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("INSERT INTO Jobs (JOBID, ORGID, JOBNAME, IS_ACTIVE, TRANSACTION_TIMEOUT, IS_PERIODIC, PERIOD, SCHEDULE_INFO, NEXT_EXECUTION_TIME, EXECUTOR_NAME, END_EXECUTION_TIME, MAX_EXECUTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setLong(1, job.getJobId());
				
				if(job.getOrgId() != -1) {
					pstmt.setLong(2, job.getOrgId());
				}
				else {
					pstmt.setNull(2, Types.BIGINT);
				}
				
				pstmt.setString(3, job.getJobName());
				pstmt.setBoolean(4, true);
				
				if(job.getTransactionTimeout() != -1) {
					pstmt.setInt(5, job.getTransactionTimeout());
				}
				else {
					pstmt.setNull(5, Types.INTEGER);
				}
				
				pstmt.setBoolean(6, job.isPeriodic());
				
				if(job.getPeriod() != -1) {
					pstmt.setInt(7, job.getPeriod());
				}
				else {
					pstmt.setNull(7, Types.INTEGER);
				}
				
				if(job.getSchedule() != null) {
					pstmt.setString(8, job.getScheduleJson());
				}
				else {
					pstmt.setNull(8, Types.VARCHAR);
				}
				
				pstmt.setLong(9, job.getExecutionTime());
				pstmt.setString(10, job.getExecutorName());
				
				if(job.getEndExecutionTime() != -1) {
					pstmt.setLong(11, job.getEndExecutionTime());
				}
				else {
					pstmt.setNull(11, Types.BIGINT);
				}
				
				if(job.getMaxExecution() != -1) {
					pstmt.setInt(12, job.getMaxExecution());
				}
				else {
					pstmt.setNull(12, Types.INTEGER);
				}
				
				if(pstmt.executeUpdate() < 1) {
					throw new Exception("Unable to schedule");
				}
				else {
					System.out.println("Added job : "+job);
				}
			}
			catch(SQLException e) {
				throw e;
			}
			finally {
				DBUtil.closeAll(conn, pstmt, rs);
			}
		}
		else {
			throw new IllegalArgumentException("Job object cannot be null");
		}
	}
	
	private static void checkForNull(JobContext job) {
		if(job.getJobId() == -1) {
			throw new IllegalArgumentException("Job ID cannot be null");
		}
		
		if(job.getJobName() == null || job.getJobName().isEmpty()) {
			throw new IllegalArgumentException("Job name cannot be null");
		}
		
		if(job.getExecutorName() == null || job.getExecutorName().isEmpty()) {
			throw new IllegalArgumentException("Job executor name cannot be null. Job : "+job.getJobName());
		}
		
		if(job.getExecutionTime() == -1) {
			throw new IllegalArgumentException("Invalid execution time for Job : "+job.getJobName());
		}
		
		if(job.isPeriodic() && job.getPeriod() == -1 && job.getSchedule() == null) {
			throw new IllegalArgumentException("Either period or schedule info should be specified for recurring job : "+job);
		}
	}
	
	public static long updateNextExecutionTimeAndCount(long jobId, String jobName, long nextExecutionTime, int executionCount) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set NEXT_EXECUTION_TIME = ?, CURRENT_EXECUTION_COUNT = ? where JOBID = ? AND JOBNAME = ?");
			
			pstmt.setLong(1, nextExecutionTime);
			pstmt.setInt(2, executionCount);
			pstmt.setLong(3, jobId);
			pstmt.setString(4, jobName);
			
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static long setInActiveAndUpdateCount(long jobId, String jobName, int executionCount) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set IS_ACTIVE = ?, CURRENT_EXECUTION_COUNT = ? where JOBID = ? AND JOBNAME = ?");
			
			pstmt.setBoolean(1, false);
			pstmt.setInt(2, executionCount);
			pstmt.setLong(3, jobId);
			pstmt.setString(4, jobName);
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static long setInActive(long jobId, String jobName) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set IS_ACTIVE = ? where JOBID = ? AND JOBNAME = ?");
			
			pstmt.setBoolean(1, false);
			pstmt.setLong(2, jobId);
			pstmt.setString(3, jobName);
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static List<JobContext> getJobs(String executorName, long startTime, long endTime) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		Connection conn = null;
		PreparedStatement getPstmt = null;
		ResultSet rs = null;
		
		List<JobContext> jcs = new ArrayList<JobContext>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			getPstmt = conn.prepareStatement("SELECT * FROM Jobs WHERE NEXT_EXECUTION_TIME < ? and EXECUTOR_NAME = ? AND IS_ACTIVE = ?");
			getPstmt.setLong(1, endTime);
			getPstmt.setString(2, executorName);
			getPstmt.setBoolean(3, true);
			
			rs = getPstmt.executeQuery();
			while(rs.next()) {
				jcs.add(getJobFromRS(rs));
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, getPstmt, rs);
		}
		
		return jcs;
	}
	
	private static JobContext getJobFromRS(ResultSet rs) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		JobContext jc = new JobContext();
		
		jc.setJobId(rs.getLong("JOBID"));
		
		if(rs.getObject("ORGID") != null) {
			jc.setOrgId(rs.getLong("ORGID"));
		}
		
		jc.setJobName(rs.getString("JOBNAME"));
		
		jc.setActive(rs.getBoolean("IS_ACTIVE"));
		
		if(rs.getObject("TRANSACTION_TIMEOUT") != null) {
			jc.setTransactionTimeout(rs.getInt("TRANSACTION_TIMEOUT"));
		}
		
		jc.setIsPeriodic(rs.getBoolean("IS_PERIODIC"));
		
		if(rs.getObject("PERIOD") != null) {
			jc.setPeriod(rs.getInt("PERIOD"));
		}
		
		if(rs.getObject("SCHEDULE_INFO") != null) {
			jc.setScheduleJson(rs.getString("SCHEDULE_INFO"));
		}
		
		jc.setExecutionTime(rs.getLong("NEXT_EXECUTION_TIME"));
		
		jc.setExecutorName(rs.getString("EXECUTOR_NAME"));
		
		if(rs.getObject("END_EXECUTION_TIME") != null) {
			jc.setEndExecutionTime(rs.getLong("END_EXECUTION_TIME"));
		}
		
		if(rs.getObject("MAX_EXECUTION") != null) {
			jc.setMaxExecution(rs.getInt("MAX_EXECUTION"));
		}
		
		if(rs.getObject("CURRENT_EXECUTION_COUNT") != null) {
			jc.setCurrentExecutionCount(rs.getInt("CURRENT_EXECUTION_COUNT"));
		}
		
		return jc;
	}
	
	public static void deleteJob(long jobId, String jobName) throws SQLException {
		String deleteSql = "DELETE FROM Jobs WHERE JOBID = ? AND JOBNAME = ?";
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
			pstmt.setLong(1, jobId);
			pstmt.setString(2, jobName);
			if(pstmt.executeUpdate() < 1) {
				System.out.println("Deletion failed with Job ID : "+jobId+" and Jobname : "+jobName);
			}
			else {
				System.out.println("Successfully job with Job ID : "+jobId+" and Jobname : "+jobName);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	public static JobContext getJob(long jobId, String jobName) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = getPStmt(conn, jobId, jobName); ResultSet rs = pstmt.executeQuery()) {
			if(rs.next()) {
				return getJobFromRS(rs);
			}
			return null;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	private static PreparedStatement getPStmt(Connection conn, long jobId, String jobName) throws SQLException {
		String sql = "SELECT * FROM Jobs WHERE JOBID = ? AND JOBNAME = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setLong(1, jobId);
		pstmt.setString(2, jobName);
		
		return pstmt;
	}
}
