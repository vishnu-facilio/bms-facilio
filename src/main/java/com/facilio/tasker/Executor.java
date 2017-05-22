package com.facilio.tasker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.facilio.transaction.FacilioConnectionPool;

public class Executor extends Thread {
	
	private Map<String, FacilioJob> jobsMap;
	private ScheduledExecutorService executor = null;
	
	public Executor(Map<String, FacilioJob> jobsMap, int noOfThreads) {
		// TODO Auto-generated constructor stub
		this.jobsMap = jobsMap;
		executor = Executors.newScheduledThreadPool(noOfThreads);
	}
	
	final int BUFFERPERIOD = 1*60; //in Seconds
	
	@Override
	public void run()
	{
		while (true)
		{
		// 	select * from JobsTable where nextexecutiontime between currenttime and currenttime+10minutes
			
			try {
				long startTime = System.currentTimeMillis()/1000;
				long endTime = startTime+BUFFERPERIOD;
				
				List<JobContext> jobs = getJobs(startTime, endTime);
				
				for(JobContext jc : jobs) {
					try {
						scheduleJob(jc);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				Thread.sleep((endTime*1000)-System.currentTimeMillis());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	void scheduleJob(JobContext jc) {
		FacilioJob job = jobsMap.get(jc.getJobName());
		if(job != null) {
			job.setJobContext(jc);
			System.out.println("Scheduling : "+jc);
			executor.schedule(job, (jc.getExecutionTime()-(System.currentTimeMillis()/1000)), TimeUnit.SECONDS);
		}
	}
	
	private List<JobContext> getJobs(long startTime, long endTime) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<JobContext> jcs = new ArrayList<JobContext>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Jobs WHERE NEXTEXECUTIONTIME >= ? and NEXTEXECUTIONTIME < ?");
			pstmt.setLong(1, startTime);
			pstmt.setLong(2, endTime);
			
			rs = pstmt.executeQuery();
			
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
			if(pstmt != null) {
				pstmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
		
		return jcs;
	}

}
