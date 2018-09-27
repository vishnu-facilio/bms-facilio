package com.facilio.tasker.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

import javax.transaction.SystemException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.server.ServerInfo;
import com.facilio.tasker.executor.Executor;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.transaction.FacilioTransactionManager;

public abstract class FacilioJob implements Runnable {

    private static Logger LOGGER = LogManager.getLogger(FacilioJob.class.getName());
	private JobContext jc = null;
	public void setJobContext(JobContext jc) {
		this.jc = jc;
	}
	
	private Executor executor = null;
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	private int retryExecutionCount = 1;
	
	@Override
	public void run() {
		long startTime = 0L;
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		currentThread.setName(threadName + "-" + jc.getJobId()+"-"+ jc.getJobName());
		try {
			if ( updateStartExecution(jc.getJobId(), jc.getJobName(), jc.getJobStartTime(), jc.getJobExecutionCount()) < 1 ) {
				return;
			}
			startTime = System.currentTimeMillis();
			LOGGER.info("Starting job " + jc.getJobId()+"-"+ jc.getJobName());
			AccountUtil.cleanCurrentAccount();
			retryExecutionCount++;

			FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
			if(jc.getTransactionTimeout() != -1) {
				FacilioTransactionManager.INSTANCE.getTransactionManager().setTransactionTimeout(jc.getTransactionTimeout());
			}

			long orgId = jc.getOrgId();
			if(orgId != -1) {
				AccountUtil.setCurrentAccount(orgId);
			}
			long nextExecutionTime = getNextExecutionTime();
			jc.setNextExecutionTime(nextExecutionTime);

			execute(jc);

			if(nextExecutionTime != -1) {
				JobStore.updateNextExecutionTimeAndCount(jc.getJobId(), jc.getJobName(),  nextExecutionTime, jc.getCurrentExecutionCount()+1);
			} else {
				JobStore.setInActiveAndUpdateCount(jc.getJobId(), jc.getJobName(), jc.getCurrentExecutionCount()+1);
			}
			FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
		}
		catch(Exception e) {
			try {
				FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {
				LOGGER.error("Exception occurred ", e1);
			}
			LOGGER.error("Job execution failed for Job :"+jc.getJobId()+" : "+ jc.getJobName(),e);
			CommonCommandUtil.emailException(FacilioJob.class.getName(), "Job execution failed for Job : "+jc.getJobId()+" : "+ jc.getJobName(), e);
			reschedule();
		} finally {
			LOGGER.info("Job completed " +jc.getJobId()+"-"+ jc.getJobName() + " time taken : " + (System.currentTimeMillis()-startTime));
			currentThread.setName(threadName);
		}
	}

	private int updateStartExecution(long jobId, String jobName, long jobStartTime, int jobExecutionCount) {
		int rowsUpdated = 0;
		String query = "update Jobs set STATUS = 2, JOB_SERVER_ID = ?, CURRENT_EXECUTION_TIME = ?, EXECUTION_ERROR_COUNT = ? where JOBID = ? and JOBNAME= ? and CURRENT_EXECUTION_TIME = ? and EXECUTION_ERROR_COUNT = ?";
		try(Connection connection = FacilioConnectionPool.getInstance().getConnectionFromPool();
		PreparedStatement statement = connection.prepareStatement(query)){
			statement.setLong(1, ServerInfo.getServerId());
			statement.setLong(2, System.currentTimeMillis());
			statement.setInt(3, jobExecutionCount+1);
			statement.setLong(4, jobId);
			statement.setString(5, jobName);
			statement.setLong(6, jobStartTime);
			statement.setInt(7, jobExecutionCount);
			rowsUpdated = statement.executeUpdate();
			LOGGER.debug("query : " + statement.toString());
		} catch (SQLException e) {
			LOGGER.error("Exception while updating Job " + jobName + "_" + jobId, e);
		}
		LOGGER.debug("Updated Job " + jobName + " " + rowsUpdated );
		return rowsUpdated;
	}

	private long getNextExecutionTime() {
		if(jc.isPeriodic() && (jc.getMaxExecution() == -1 || jc.getCurrentExecutionCount()+1 < jc.getMaxExecution())) {
			long nextExecutionTime = -1; 
			if(jc.getPeriod() != -1) {
				nextExecutionTime = (Instant.now().getEpochSecond())+jc.getPeriod();
			}
			else if(jc.getSchedule() != null) {
				nextExecutionTime = jc.getSchedule().nextExecutionTime(jc.getExecutionTime());
				if(nextExecutionTime == jc.getExecutionTime()) {// One time job
					return -1;
		 		}
				while(nextExecutionTime <= Instant.now().getEpochSecond()) {
					nextExecutionTime = jc.getSchedule().nextExecutionTime(nextExecutionTime);
				}
			}
			if(jc.getEndExecutionTime() == -1 || nextExecutionTime <= jc.getEndExecutionTime()) {
				return nextExecutionTime;
			}
		}
		return -1;
	}
	
	private void reschedule() {
		if(retryExecutionCount <= executor.getMaxRetry()) {
			LOGGER.error("Rescheduling : "+jc.getJobId()+"::"+jc.getJobName()+" for the "+retryExecutionCount+" time.");
			executor.schedule(this, 1);
		}
		else {
			LOGGER.error("Max retry exceeded for : "+jc+".\nSo making it inactive");
			CommonCommandUtil.emailException(FacilioJob.class.getName(), "Max retry exceeded for Job : "+jc.getJobId()+" : "+ jc.getJobName(), "Since max retries exceeded for job : "+jc.getJobId()+"-"+jc.getJobName()+", making it inactive.");
			try {
				JobStore.setInActive(jc.getJobId(), jc.getJobName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.error("Error ",e);
			}
		}
	}
	public abstract void execute(JobContext jc) throws Exception;
	
	@Override
	public String toString() {
		return this.getClass().getName();
	}

}
