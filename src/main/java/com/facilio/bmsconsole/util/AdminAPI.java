package com.facilio.bmsconsole.util;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminAPI 
{
	private static Logger logger = Logger.getLogger(AdminAPI.class.getName());
	
	public static List<Map<String, Object>> getSystemJobs() throws SQLException
	{
		List<Map<String, Object>> jobList = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM System_Jobs LEFT JOIN Jobs ON System_Jobs.JOBID = Jobs.JOBID");
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				Map<String, Object> jobs = new HashMap<>();
				jobs.put("jobId", rs.getLong("JOBID"));
				jobs.put("name", rs.getString("JOBNAME"));
				jobs.put("period", rs.getInt("PERIOD"));
				jobs.put("lastexecutiontime", rs.getLong("LAST_EXECUTION_TIME"));
				jobList.add(jobs);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all jobs" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return jobList;
	}
	
	public static Map<String, Object> getSystemJob(String name) throws SQLException
	{
		Map<String, Object> jobs = new HashMap<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Jobs LEFT JOIN System_Jobs ON Jobs.JOBID = System_Jobs.JOBID WHERE JOBNAME = ?");
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				jobs.put("jobId", rs.getLong("JOBID"));
				jobs.put("name", rs.getString("JOBNAME"));
				jobs.put("period", rs.getInt("PERIOD"));
				jobs.put("lastexecutiontime", rs.getLong("LAST_EXECUTION_TIME"));
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all jobs" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return jobs;
	}
	
	public static void addSystemJob(Long jobId) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO System_Jobs (JOBID, LAST_EXECUTION_TIME) VALUES (?, ?)");
			pstmt.setLong(1, jobId);
			pstmt.setLong(2, System.currentTimeMillis());
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add job");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding job" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static void updateSystemJob(Long jobId, Long lastExecutionTime) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE System_Jobs SET LAST_EXECUTION_TIME = ? WHERE JOBID = ?");
			pstmt.setLong(1, lastExecutionTime);
			pstmt.setLong(2, jobId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add job");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding job" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static void deleteSystemJob(Long jobId) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE from System_Jobs WHERE JOBID = ?");
			pstmt.setLong(1, jobId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to delete system job");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while system job" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		deleteJob(jobId);
	}
	
	public static void deleteJob(Long jobId) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE from Jobs WHERE JOBID = ?");
			pstmt.setLong(1, jobId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to delete job");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while system job" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
}
