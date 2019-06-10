package com.facilio.tasker.job;

import java.util.concurrent.Future;

public class JobTimeOutInfo {


    private long executionTime = -1;

    public JobTimeOutInfo(long executionTime, long timeOut, Future future, InstantJob instantJob) {
        this.executionTime = executionTime;
        this.timeOut = timeOut;
        this.future = future;
        this.instantJob = instantJob;
    }

    public JobTimeOutInfo(long executionTime, long timeOut, Future future, FacilioJob facilioJob) {
        this.executionTime = executionTime;
        this.timeOut = timeOut;
        this.future = future;
        this.facilioJob = facilioJob;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    private long timeOut = -1;
    public long getTimeOut() {
        return timeOut;
    }

    private Future future;
    public Future getFuture() {
        return future;
    }

    private InstantJob instantJob;
    public InstantJob getInstantJob() {
        return instantJob;
    }

    private FacilioJob facilioJob;
    public FacilioJob getFacilioJob() {
        return facilioJob;
    }
}
