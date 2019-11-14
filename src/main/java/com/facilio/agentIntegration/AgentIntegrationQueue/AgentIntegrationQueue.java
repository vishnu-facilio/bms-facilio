package com.facilio.agentIntegration.AgentIntegrationQueue;

import com.facilio.agentIntegration.AgentIntegrationQueue.preprocessor.AgentIntegrationPreprocessor;
/**
 * Abstract class for the AgentIntegrationQueue thread
 */
public abstract class AgentIntegrationQueue implements Runnable{
    private long orgId = -1;
    private AgentIntegrationPreprocessor preprocessor = null;

    public long getOrgId() {
        return orgId;
    }

    void setOrgId(long orgId) {
        this.orgId = orgId;
    }
    /**
     * Sets the pre processor
     * @param preprocessor
     */
    void setPreprocessor(AgentIntegrationPreprocessor preprocessor) {
        this.preprocessor=preprocessor;
    }

    /**
     * Starts the pre processor thread (Asynchronous)
     * @param orgId
     */
    abstract void startProcessor(long orgId);

    /**
     * returns pre processor
     * @return
     */
    public AgentIntegrationPreprocessor getPreProcessor(){return preprocessor; }
    @Override
    public void run(){
        if (orgId !=-1)
            startProcessor(orgId);
    }
}
