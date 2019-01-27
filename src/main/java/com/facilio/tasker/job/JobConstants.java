package com.facilio.tasker.job;

import com.facilio.chain.FacilioChain;

class JobConstants {
    static final int JOB_IN_PROGRESS = 2;
    static final int JOB_COMPLETED = 3;
    static final int INITIAL_EXECUTION_COUNT = 0;
    static final boolean ENABLED = Boolean.TRUE;
    static final boolean DISABLED = Boolean.FALSE;
    static final long DEFAULT_SERVER_ID = 0L;
    
    static final String JOB_CONTEXT = "jobContext";
    static final String FACILIO_JOB = "facilioJob";
    static final String INSTANT_JOB = "instantJob";
    
    static class ChainFactory {
    	
    	static FacilioChain jobExecutionChain(int timeout) {
    		FacilioChain c = new FacilioChain(timeout);
    		c.addCommand(new CalculateNextExecutionTimeCommand());
    		c.addCommand(new JobExecutionCommand());
    		c.addCommand(new UpdateNextExecutionCommand());
    		return c;
    	}
    	
    	static FacilioChain instantJobExecutionChain (int timeout) {
    		FacilioChain c = new FacilioChain(timeout);
    		c.addCommand(new InstantJobExecutionCommand());
    		return c;
    	}
    }
}
