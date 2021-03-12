package com.facilio.agentv2.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class AgentIntegrationAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationAction.class.getName());
    private String name;
    private String projectId;
    private String subscriptionName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getAltairIntegrations() {
        try {
            setResult(AgentConstants.DATA,FacilioService.runAsServiceWihReturn(this::getIntegrationList));
        } catch (Exception e) {
            LOGGER.info("Exception while getting altair integrations", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
    private List<Map<String, Object>> getIntegrationList() throws Exception {
        Collection<Long> preProcessorType = new ArrayList<>();
        preProcessorType.add(2L);
        Collection<Long> queueType = new ArrayList<>();
        queueType.add(2L);
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAgentMessageIntegrationModule().getTableName())
                .select(FieldFactory.getAgentMessageIntegrationFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getAgentMessageIntegrationFields()).get("preProcessorType"), preProcessorType, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getAgentMessageIntegrationFields()).get("queueType"), queueType, NumberOperators.EQUALS))
                .limit(100);
        List<Map<String, Object>> rows = select.get();
        return rows;
    }
    public String addAltairIntegration() {
        try {
            Map<String, Object> values = new HashMap<>();
            values.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
            values.put("name",getName());
            values.put("topic",getProjectId());
            values.put("clientId",getSubscriptionName());
            values.put("queueType",2);
            values.put("preProcessorType",2);
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder().table(ModuleFactory.getAgentMessageIntegrationModule().getTableName())
                    .fields(FieldFactory.getAgentMessageIntegrationFields()).addRecord(values);
            insertRecordBuilder.save();
            setResult(AgentConstants.DATA,FacilioService.runAsServiceWihReturn(this::getIntegrationList));
        } catch (Exception e) {
            LOGGER.info("Exception while adding altair integrations", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
}
