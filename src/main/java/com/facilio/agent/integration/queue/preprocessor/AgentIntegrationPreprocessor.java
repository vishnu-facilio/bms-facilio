package com.facilio.agent.integration.queue.preprocessor;

import org.json.simple.JSONObject;

/**
 * Interface for pre processing the data from AgentMessageIntegrationQueues
 */
public interface AgentIntegrationPreprocessor {

    public abstract JSONObject preProcess(Object o) throws Exception;
}
