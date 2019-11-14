package com.facilio.agentIntegration.AgentIntegrationQueue;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentIntegration.AgentIntegrationQueue.preprocessor.AltairSmartEdge;
import com.facilio.beans.ModuleCRUDBeanImpl;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgentIntegrationQueueFactory {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationQueueFactory.class.getName());

    public static List<AgentIntegrationQueue> getIntegrationQueues() throws Exception {
        List<AgentIntegrationQueue> list = new ArrayList<>();
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentMessageIntegrationModule().getTableName())
                .select(FieldFactory.getAgentMessageIntegrationFields()). limit(100);
        List<Map<String, Object>> rows = select.get();

        LOGGER.info("anand.h 999"+ select.toString());
        for (Map<String,Object> row: rows ){
            LOGGER.info("anand.h "+1001);
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
                integrationQueue.setOrgiD(orgId);
                list.add(integrationQueue);
            }
        }
        LOGGER.info("anand.h 900"+ list.size());
        return list;
    }

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
