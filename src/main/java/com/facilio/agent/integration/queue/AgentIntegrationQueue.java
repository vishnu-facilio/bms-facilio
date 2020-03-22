package com.facilio.agent.integration.queue;

import com.facilio.agent.integration.queue.preprocessor.AgentMessagePreProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Abstract class for the AgentIntegrationQueue thread
 */
public abstract class AgentIntegrationQueue implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationQueue.class.getName());
    private long orgId = -1;
    private AgentMessagePreProcessor preprocessor = null;

    public long getOrgId() {
        return orgId;
    }

    public void initialize() {
        Thread thread = Thread.currentThread();
        String threadName = "agent-integration-" + orgId + "-" + getPreProcessor().getClass();
        LOGGER.info("Message Integration thread name set to " + threadName);
        thread.setName(threadName);
    }

    void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    /**
     * Sets the pre processor
     *
     * @param preprocessor
     */
    void setPreprocessor(AgentMessagePreProcessor preprocessor) {
        this.preprocessor = preprocessor;
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
    public AgentMessagePreProcessor getPreProcessor(){return preprocessor; }
    @Override
    public void run(){
        if (orgId !=-1)
            startProcessor(orgId);
    }
}
