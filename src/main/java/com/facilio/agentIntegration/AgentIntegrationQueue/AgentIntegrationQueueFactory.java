package com.facilio.agentIntegration.AgentIntegrationQueue;

import com.facilio.agentIntegration.AgentIntegrationQueue.preprocessor.AltairSmartEdge;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Factory method for creating AgentMessageIntegrationQueue threads and starting them
 */
public class AgentIntegrationQueueFactory {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationQueueFactory.class.getName());
    /**
     * returns list of all AgentIntegrationQueue threads
     */
    public static List<AgentIntegrationQueue> getIntegrationQueues() throws Exception {
        List<AgentIntegrationQueue> list = new ArrayList<>();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentMessageIntegrationModule().getTableName())
                .select(FieldFactory.getAgentMessageIntegrationFields()). limit(100);
        List<Map<String, Object>> rows = select.get();

        for (Map<String,Object> row: rows ){
            int queueType = (int)row.get("queueType");
            int preProcessorType = (int)row.get("preProcessorType");
            long orgId = 100;
            AgentIntegrationQueue integrationQueue = null;
            switch (queueType){
                case 2: integrationQueue = new GooglePubSub((String)row.get("topic"),(String)row.get("clientId"));
                        LOGGER.info("GooglePubsubCreated....");
                        break;
                default: LOGGER.info("Invalid QUEUE_TYPE ");
            }
            switch (preProcessorType){
                case 2: integrationQueue.setPreprocessor(new AltairSmartEdge());
                        LOGGER.info("Altair smartEdge...");
                        break;
                default: LOGGER.info("Invalid Preprocessor_type");
            }
            if (integrationQueue != null && integrationQueue.getPreProcessor() != null) {
                integrationQueue.setOrgId(orgId);
                list.add(integrationQueue);
            }
        }

        return list;
    }
    /**
     * Starts all the AgentIntegrationQueue threads
     */
    public static void startIntegrationQueues() throws Exception {
        List<AgentIntegrationQueue> list = AgentIntegrationQueueFactory.getIntegrationQueues();
        for (AgentIntegrationQueue item : list) {
            try {
                new Thread(item).start();
            } catch (Exception ex){
                LOGGER.info("Exception while starting integeration queue : "+ item);
            }
        }
    }
}
