package com.facilio.tasker.job;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.jobs.JobLogger;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.executor.FacilioInstantJobExecutor;
import com.facilio.tasker.executor.InstantJobExecutor;

public abstract class InstantJob {

    private static final Logger LOGGER = Logger.getLogger(InstantJob.class.getName());

    private String receiptHandle;
    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }
    String getReceiptHandle() {
        return receiptHandle;
    }
    private String messageId;
    public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public final void _execute(FacilioContext context, int transactionTimeout) {
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        currentThread.setName(threadName + "-instant-job-" + StringUtils.truncate(getReceiptHandle(), 50));
    	String jobName = (String) context.remove(InstantJobConf.getJobNameKey());
    	int status = 0;
    	long startTime = System.currentTimeMillis();
        try {
            Account account = (Account) context.remove(InstantJobConf.getAccountKey());

            if (account != null) {
                try {
                    account.clearCounters();
                    AccountUtil.cleanCurrentAccount();
                    AccountUtil.setCurrentAccount(account);

                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Exception while setting current account ", e);
                }
            }
            context.put(JobConstants.INSTANT_JOB, this);
            
            JobConstants.ChainFactory.instantJobExecutionChain(transactionTimeout).execute(context);
//            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 88 && jobName.equals("ControllerActivityWatcher")) {
//            	LOGGER.info("Executing Job "+jobName+" with props : "+context);
//            }
            status = 1;
        } catch (Exception e) {
            status = 2;
           LOGGER.severe("Exception occurred during execution of instant job "+jobName);
        } finally {
            JobContext job = new JobContext();
            job.setJobName(jobName);
            job.setExecutorName("instant");
            job.setIsPeriodic(false);
            JobLogger.log(job, (System.currentTimeMillis() - startTime), status);
            currentThread.setName(threadName);
//            if(FacilioProperties.isProduction() && StringUtils.isNotBlank(getReceiptHandle())) {
//            	InstantJobExecutor.INSTANCE.jobEnd(getReceiptHandle());
//            }
            if(getMessageId() !=null) {
            	FacilioInstantJobExecutor.INSTANCE.jobEnd(getMessageId());
            } else {
            	InstantJobExecutor.INSTANCE.jobEnd(getReceiptHandle());
            }
        }
    }

    public abstract void execute(FacilioContext context) throws Exception;

    public void handleTimeOut() {

    }

}
