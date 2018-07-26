package com.facilio.tasker.job;

import com.facilio.queue.ObjectQueue;

public abstract class InstantJob extends FacilioJob {

    private String receiptHandle;

    public InstantJob (String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public abstract void execute();

    public void run() {
        execute();
        ObjectQueue.deleteObject("instantJob", getReceiptHandle());
    }

}
