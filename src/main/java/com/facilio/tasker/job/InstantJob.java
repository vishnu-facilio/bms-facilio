package com.facilio.tasker.job;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.transaction.FacilioTransactionManager;

public abstract class InstantJob {

    private static final Logger LOGGER = Logger.getLogger(InstantJob.class.getName());

    private String receiptHandle;

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }
    private String getReceiptHandle() {
        return receiptHandle;
    }

    public final void _execute(FacilioContext context) {
        try {
            Account account = (Account) context.get(InstantJobConf.getAccountKey());
            FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
            FacilioTransactionManager.INSTANCE.getTransactionManager().setTransactionTimeout(1795000);

            if (account != null) {
                try {
                    AccountUtil.setCurrentAccount(account);
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Exception while setting current account ", e);
                }
            }
            context.remove(InstantJobConf.getAccountKey());
            context.remove(InstantJobConf.getJobNameKey());
            execute(context);
            ObjectQueue.deleteObject(InstantJobConf.getInstantJobQueue(), getReceiptHandle());
            FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
        } catch (NotSupportedException | SystemException | RollbackException |  HeuristicMixedException | HeuristicRollbackException e) {
            try {
				FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {
				LOGGER.log(Level.WARNING,"Exception occurred ", e1);
			}
        }
    }

    public abstract void execute(FacilioContext context);

}
