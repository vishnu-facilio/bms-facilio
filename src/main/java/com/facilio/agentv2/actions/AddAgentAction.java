package com.facilio.agentv2.actions;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.DatatypeConverter;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class AddAgentAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AddAgentAction.class.getName());
    @Size(min = 0, max = 100, message = "Agent name must have 3-100 characters")
    private String agentName;
    @Min(value = 1, message = "Data interval can't be less than 1")
    @NotNull
    private long dataInterval;
    private long messageSourceId;
    private String subscribeTopics;
    private long siteId;
    private long autoMappingParentFieldId;
    private int readingScope;
    private String type;
    private String displayName;
    private int agentType = -1;
    private String userName;
    private String password;
    private String url;
    private int partitionId = 0;
    private Integer port;
    private Integer controllerAlarmIntervalInMins;
    private String ipAddress;
    private String agentSourceType = AgentConstants.AgentSourceType.WEB.getValue();
    private boolean allowAutoMapping;

	public String createAgent() {
        try {
            FacilioChain addAgentChain = TransactionChainFactory.createAgentChain();
            FacilioContext context = addAgentChain.getContext();
            FacilioAgent agent = getFacilioAgent();
            context.put(AgentConstants.AGENT, agent);
            addAgentChain.execute();
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while adding agent", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    private FacilioAgent getFacilioAgent() throws Exception {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        FacilioAgent agent = new FacilioAgent();
        agent.setDisplayName(getDisplayName());
        agent.setAgentType(getAgentType());
        setAgentName(agent, currentOrg);
        agent.setInterval(getDataInterval());
        agent.setSiteId(getSiteId());
        agent.setProcessorVersion(2);
        agent.setMessageSourceId(getMessageSourceId());
        agent.setSubscribeTopics(getSubscribeTopics());
        agent.setWritable(isAgentWritable());
        switch (AgentType.valueOf(agentType)) {
            case REST:
                long orgId = currentOrg.getOrgId();
                long inboundId = getInboundId(agent, orgId);
                agent.setInboundConnectionId(inboundId);
            case CLOUD:
            case CUSTOM:
                if(isAllowAutoMapping()){
                    agent.setAllowAutoMapping(true);
                    agent.setReadingScope(getReadingScope());
                    agent.setAutoMappingParentFieldId(getAutoMappingParentFieldId());
                }
                agent.setConnected(true);
                break;
            case RDM:
                agent.setUrl(getUrl());
                agent.setUserName(getUserName());
                agent.setPassword(getPassword());
                break;
            case E2:
                agent.setPort(getPort());
                agent.setIpAddress(getIpAddress());
                agent.setControllerAlarmIntervalInMins(getControllerAlarmIntervalInMins());
                break;
        }
        return agent;
    }

    private boolean isAgentWritable() {
        return AgentType.valueOf(agentType) != AgentType.NIAGARA && AgentType.valueOf(agentType) != AgentType.FACILIO;
    }

    private Long getInboundId(FacilioAgent agent, long orgId) throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE, () -> insertApiKey(agent.getName(), orgId));
    }

    private void setAgentName(FacilioAgent agent, Organization currentOrg) {
        String name = getAgentName();
        if (agent.getAgentTypeEnum() == AgentType.NIAGARA && validateNiagaraHostId(name)) {
            agent.setName(name);
            return;
        }
        agent.setName(currentOrg.getDomain() + "-" + getRegexAgentName((name == null || name.isEmpty()) ? agent.getDisplayName() : name));
    }

    private boolean validateNiagaraHostId(String hostId) {
        if (!hostId.matches(".*(-[0-9A-F]+){3,5}")) {
            throw new IllegalArgumentException("Invalid Niagara Host Id");
        }
        return true;
    }

    private String getRegexAgentName(String name) {
        return name.toLowerCase().replaceAll("[^a-zA-Z0-9\\-]+", "");
    }

    private String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[256 / 8];
        random.nextBytes(bytes);
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    private long insertApiKey(String agentName, long orgId) throws Exception {
        Map<String, Object> prop = new HashMap<>();
        prop.put(AgentConstants.API_KEY, generateKey());
        prop.put("sender", agentName);
        prop.put(AgentConstants.NAME, agentName);
        prop.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        prop.put("authType", 1);
        prop.put(AgentConstants.ORGID, orgId);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getInboundConnectionsFields())
                .table(ModuleFactory.getInboundConnectionsModule().getTableName());
        return builder.insert(prop);
    }
}
