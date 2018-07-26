package com.facilio.tasker.job;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.queue.ObjectQueue;

public abstract class InstantJob {

    private String receiptHandle;

    public InstantJob (String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    private String getReceiptHandle() {
        return receiptHandle;
    }

    public final void _execute(FacilioContext context) {
        execute(context);
        ObjectQueue.deleteObject("instantJob", getReceiptHandle());
    }

    public abstract void execute(FacilioContext context);

}
