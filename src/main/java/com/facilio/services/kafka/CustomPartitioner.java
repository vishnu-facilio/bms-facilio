package com.facilio.services.kafka;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.fw.FacilioException;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class CustomPartitioner implements Partitioner {
    private static final Logger LOGGER = LogManager.getLogger(CustomPartitioner.class.getName());

    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {

        String key = (String) o;
        int partition = 0;
        try {
            FacilioAgent agent = AgentApiV2.getAgent(key);
            if (agent != null) {
                partition = agent.getPartitionId();
            } else {
                throw new FacilioException("Agent is null");
            }
        } catch (Exception e) {
            LOGGER.info("Exception while assigning partition id ", e);
        }
        return partition;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
