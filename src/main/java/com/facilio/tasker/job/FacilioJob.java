package com.facilio.tasker.job;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.executor.Executor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;

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
			if ( JobStore.updateStartExecution(jc.getJobId(), jc.getJobName(), jc.getJobStartTime(), jc.getJobExecutionCount()) < 1 ) {
				return;
			}
			startTime = System.currentTimeMillis();
			LOGGER.debug("Starting job " + jc.getJobId()+"-"+ jc.getJobName());
			AccountUtil.cleanCurrentAccount();
			retryExecutionCount++;

			long orgId = jc.getOrgId();
			if(orgId != -1) {
				AccountUtil.setCurrentAccount(orgId);
				AccountUtil.setReqUri(jc.getJobName());
			}
			
			FacilioContext context = new FacilioContext();
			context.put(JobConstants.JOB_CONTEXT, jc);
			context.put(JobConstants.FACILIO_JOB, this);
			JobConstants.ChainFactory.jobExecutionChain(jc.getTransactionTimeout()).execute(context);
		}
		catch(Exception e) {
			LOGGER.error("Job execution failed for Job :"+jc.getJobId()+" : "+ jc.getJobName(),e);
			CommonCommandUtil.emailException("FacilioJob", "Job execution failed for Job : "+jc.getJobId()+" : "+ jc.getJobName(), e);
			reschedule();
		} finally {
			LOGGER.debug("Job completed " +jc.getJobId()+"-"+ jc.getJobName() + " time taken : " + (System.currentTimeMillis()-startTime));
			currentThread.setName(threadName);
		}
	}

	private void reschedule() {
		if(retryExecutionCount <= executor.getMaxRetry()) {
			LOGGER.error("Rescheduling : "+jc.getJobId()+"::"+jc.getJobName()+" for the "+retryExecutionCount+" time.");
			executor.schedule(this, 1);
		}
		else {
			LOGGER.error("Max retry exceeded for : "+jc+".\nSo making it inactive");
			CommonCommandUtil.emailException("FacilioJob", "Max retry exceeded for Job : "+jc.getJobId()+" : "+ jc.getJobName(), "Since max retries exceeded for job : "+jc.getJobId()+"-"+jc.getJobName()+", making it inactive.");
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
