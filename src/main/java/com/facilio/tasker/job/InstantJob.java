package com.facilio.tasker.job;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.jobs.JobLogger;
import com.facilio.chain.FacilioContext;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.executor.InstantJobExecutor;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InstantJob {

    private static final Logger LOGGER = Logger.getLogger(InstantJob.class.getName());

    private String receiptHandle;
    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }
    String getReceiptHandle() {
        return receiptHandle;
    }

    public final void _execute(FacilioContext context, int transactionTimeout) {
    	String jobName = (String) context.remove(InstantJobConf.getJobNameKey());
    	int status = 0;
    	long startTime = System.currentTimeMillis();
        try {
            Account account = (Account) context.remove(InstantJobConf.getAccountKey());

            if (account != null) {
                try {
                    AccountUtil.cleanCurrentAccount();
                    AccountUtil.setCurrentAccount(account.getOrg().getOrgId());
                    AccountUtil.getCurrentAccount().setUser(account.getUser());
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Exception while setting current account ", e);
                }
            }
            context.put(JobConstants.INSTANT_JOB, this);
            
            JobConstants.ChainFactory.instantJobExecutionChain(transactionTimeout).execute(context);
            
//            if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 88 && jobName.equals("ControllerActivityWatcher")) {
//            	LOGGER.info("Executing Job "+jobName+" with props : "+context);
//            }
            InstantJobExecutor.INSTANCE.jobEnd(getReceiptHandle());
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
        }
    }

    public abstract void execute(FacilioContext context) throws Exception;

    public void handleTimeOut() {

    }

}
