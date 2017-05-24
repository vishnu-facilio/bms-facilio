package com.facilio.tasker.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.facilio.transaction.FacilioConnectionPool;

public class JobStore {
	
	public static long addJob(String orgId, String jobName, boolean isPeriodic, int period, long nextExecutionTime, String executorName) throws Exception {
		
		if(orgId == null || orgId.isEmpty()) {
			throw new IllegalArgumentException("Invalid Org Id");
		}
		
		if(jobName == null || jobName.isEmpty()) {
			throw new IllegalArgumentException("Invalid JobName");
		}
		
		if(executorName == null || executorName.isEmpty()) {
			throw new IllegalArgumentException("Invalid Executor Name");
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Jobs (ORGID, JOBNAME, ISPERIODIC, PERIOD, NEXTEXECUTIONTIME, EXECUTORNAME) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setString(1, orgId);
			pstmt.setString(2, jobName);
			pstmt.setBoolean(3, isPeriodic);
			pstmt.setInt(4, period);
			pstmt.setLong(5, nextExecutionTime);
			pstmt.setString(6, executorName);
			
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
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
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
	
	public static long updateNextExecutionTime(long jobId, long nextExecutionTime) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Jobs set NEXTEXECUTIONTIME = ? where JOBID = ?");
			
			pstmt.setLong(1, nextExecutionTime);
			pstmt.setLong(2, jobId);
			
			return pstmt.executeUpdate();
		}
		catch(SQLException e) {
			throw e;
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
			if(pstmt != null) {
				try {
					pstmt.close();
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
	
	public static List<JobContext> getJobs(String executorName, long startTime, long endTime) throws SQLException {
		Connection conn = null;
		PreparedStatement getPstmt = null;
		ResultSet rs = null;
		
		List<JobContext> jcs = new ArrayList<JobContext>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			getPstmt = conn.prepareStatement("SELECT * FROM Jobs WHERE NEXTEXECUTIONTIME >= ? and NEXTEXECUTIONTIME < ? and EXECUTORNAME = ?");
			getPstmt.setLong(1, startTime);
			getPstmt.setLong(2, endTime);
			getPstmt.setString(3, executorName);
			
			rs = getPstmt.executeQuery();
			while(rs.next()) {
				long jobId = rs.getLong("JOBID");
				String orgId = rs.getString("ORGID");
				String jobName = rs.getString("JOBNAME");
				boolean isPeriodic = rs.getBoolean("ISPERIODIC");
				int period = rs.getInt("PERIOD");
				long nextExecutionTime = rs.getLong("NEXTEXECUTIONTIME");
				
				JobContext jc = new JobContext(jobId, orgId, jobName, period, isPeriodic, nextExecutionTime);
				jcs.add(jc);
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			if(rs != null) {
				rs.close();
			}
			if(getPstmt != null) {
				getPstmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
		
		return jcs;
	}
}
