package com.facilio.tasker.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class JobStore {
	
	public static long addJob(JobContext job) throws Exception {
		if(job != null) {
			
			if(job.getJobName() == null || job.getJobName().isEmpty()) {
				throw new IllegalArgumentException("Job name cannot be null");
			}
			
			if(job.getExecutorName() == null || job.getExecutorName().isEmpty()) {
				throw new IllegalArgumentException("Job executor name cannot be null. Job : "+job.getJobName());
			}
			
			if(job.getExecutionTime() == -1) {
				throw new IllegalArgumentException("Invalid execution time for Job : "+job.getJobName());
			}
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("INSERT INTO Jobs (ORGID, JOBNAME, IS_ACTIVE, TRANSACTION_TIMEOUT, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				
				if(job.getOrgId() != -1) {
					pstmt.setLong(1, job.getOrgId());
				}
				else {
					pstmt.setNull(1, Types.BIGINT);
				}
				
				pstmt.setString(2, job.getJobName());
				pstmt.setBoolean(3, true);
				
				if(job.getTransactionTimeout() != -1) {
					pstmt.setInt(4, job.getTransactionTimeout());
				}
				else {
					pstmt.setNull(4, Types.INTEGER);
				}
				
				pstmt.setBoolean(5, job.isPeriodic());
				pstmt.setInt(6, job.getPeriod());
				pstmt.setLong(7, job.getExecutionTime());
				pstmt.setString(8, job.getExecutorName());
				
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
				DBUtil.closeAll(conn, pstmt, rs);
			}
		}
		else {
			throw new IllegalArgumentException("Job object cannot be null");
		}
	}
	
	public static long updateNextExecutionTime(long jobId, long nextExecutionTime) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set NEXT_EXECUTION_TIME = ? where JOBID = ?");
			
			pstmt.setLong(1, nextExecutionTime);
			pstmt.setLong(2, jobId);
			
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static long setInActive(long jobId) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set IS_ACTIVE = ? where JOBID = ?");
			
			pstmt.setBoolean(1, false);
			pstmt.setLong(2, jobId);
			
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		
	}
	
	public static List<JobContext> getJobs(String executorName, long startTime, long endTime) throws SQLException {
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
				jc.setPeriod(rs.getInt("PERIOD"));
				jc.setExecutionTime(rs.getLong("NEXT_EXECUTION_TIME"));
				jc.setExecutorName(rs.getString("EXECUTOR_NAME"));
				
				jcs.add(jc);
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
}
