package com.facilio.agent.integration.queue;

import com.facilio.agent.integration.queue.preprocessor.AltairSmartEdge;
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
        try {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentMessageIntegrationModule().getTableName())
                .select(FieldFactory.getAgentMessageIntegrationFields()).limit(100);
        List<Map<String, Object>> rows = select.get();

        for (Map<String, Object> row : rows) {
            int queueType = (int) row.get("queueType");
            int preProcessorType = (int) row.get("preProcessorType");
            long orgId = Long.parseLong(row.get("orgId").toString());
            AgentIntegrationQueue integrationQueue = null;
            if (queueType == 2) {
                integrationQueue = new GooglePubSub((String) row.get("topic"), (String) row.get("clientId"));
                LOGGER.info("Google Pub SubCreated....");
            } else {
                LOGGER.info("Invalid QUEUE_TYPE ");
            }
            if (preProcessorType == 2) {
                if (integrationQueue != null) {
                    integrationQueue.setPreprocessor(new AltairSmartEdge());
                    LOGGER.info("Altair smartEdge...");
                } else {
                    LOGGER.info("integration queue is null");
                }
            } else {
                LOGGER.info("Invalid Preprocessor_type");
            }
            if (integrationQueue != null && integrationQueue.getPreProcessor() != null) {
                integrationQueue.setOrgId(orgId);
                list.add(integrationQueue);
            }
        }
        } catch (Exception e) {
            LOGGER.info("Exception while getting queue ", e);
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
                item.initialize();
                new Thread(item).start();
            } catch (Exception ex){
                LOGGER.info("Exception while starting integeration queue : "+ item);
            }
        }
    }
}
