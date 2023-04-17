package com.facilio.agentv2.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
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
import com.facilio.workflows.context.WorkflowContext;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.DatatypeConverter;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Setter @Getter
public class AddAgentAction extends AgentActionV2
{
    private static final Logger LOGGER = LogManager.getLogger(AddAgentAction.class.getName());



    @Size(min = 0,max = 100,message = "Agent name must have 3-100 characters")
    private String agentName;

    @Min(value = 1,message = "Data interval can't be less than 1")
    @NotNull
    private long dataInterval;
    private long messageSourceId;
    private String subscribeTopics;
    @NotNull
    @Min(value = 1,message = "Site can't be less than 1")
    private long siteId;
    private String type;
    @NotNull @Size(min = 2,max = 100)
    private String displayName;
    private WorkflowContext workflow;
    private int agentType=-1;
    private String userName;
    private String password;
    private String url;
    private int partitionId = 0;

	public String createAgent() {
        try {
            FacilioChain addAgentChain = TransactionChainFactory.createAgentChain();
            FacilioContext context = addAgentChain.getContext();
            FacilioAgent agent = new FacilioAgent();

            if(AgentType.valueOf(agentType) == AgentType.NIAGARA || StringUtils.isNotEmpty(getAgentName())){
                agent.setName(getAgentName());
            }
            else {
                agent.setName(AccountUtil.getCurrentOrg().getDomain()+"-"+getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]",""));
            }
            agent.setInterval(getDataInterval());
            agent.setSiteId(getSiteId()); //TODO validate SITE ID.
            agent.setAgentType(getAgentType());
            agent.setDisplayName(getDisplayName());
            agent.setWorkflow(getWorkflow());
            agent.setProcessorVersion(2);
//            agent.setPartitionId(partitionId);
            agent.setMessageSourceId(getMessageSourceId());
            agent.setSubscribeTopics(getSubscribeTopics());
            switch (AgentType.valueOf(agentType)){
                case REST:
                    long orgId = AccountUtil.getCurrentOrg().getOrgId();
                    long inboundId = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE, () -> insertApiKey(agent.getName(), orgId));
                    agent.setInboundConnectionId(inboundId);
                case CLOUD:
                case CUSTOM:
                    agent.setConnected(true);
                    break;
                case RDM:
                    agent.setUrl(getUrl());
                    agent.setUserName(getUserName());
                    agent.setPassword(getPassword());
                    break;
            }
            context.put(AgentConstants.AGENT,agent);
            addAgentChain.execute();
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while adding agent",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            internalError();
        }
        return SUCCESS;
    }

    private String generateKey () {
    	SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[256/8];
        random.nextBytes(bytes);
    	return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    private long insertApiKey(String agentName,long orgId) throws Exception {
        Map<String,Object> prop = new HashMap<>();
        prop.put(AgentConstants.API_KEY,generateKey());
        prop.put("sender",agentName);
        prop.put(AgentConstants.NAME,agentName);
        prop.put(AgentConstants.CREATED_TIME,System.currentTimeMillis());
        prop.put("authType",1);
        prop.put(AgentConstants.ORGID,orgId);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
        .fields(FieldFactory.getInboundConnectionsFields())
        .table(ModuleFactory.getInboundConnectionsModule().getTableName());
       return builder.insert(prop);
    }
}
