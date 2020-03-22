package com.facilio.agent.integration.queue.preprocessor;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Interface for pre processing the data from AgentMessageIntegrationQueues
 */
public interface AgentMessagePreProcessor {

    public abstract List<JSONObject> preProcess(Object o) throws Exception;
}
