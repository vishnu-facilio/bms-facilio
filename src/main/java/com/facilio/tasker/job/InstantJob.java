package com.facilio.tasker.job;

public abstract class InstantJob implements Runnable {

    private String receiptHandle;

    public InstantJob (String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }
}
