package com.facilio.tasker.job;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.config.InstantJobConf;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InstantJob {

    private static final Logger LOGGER = Logger.getLogger(InstantJob.class.getName());

    private String receiptHandle;

    public InstantJob (String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    private String getReceiptHandle() {
        return receiptHandle;
    }

    public final void _execute(FacilioContext context) {
        Account account = (Account) context.get(InstantJobConf.getAccountKey());
        if(account != null) {
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
    }

    public abstract void execute(FacilioContext context);

}
