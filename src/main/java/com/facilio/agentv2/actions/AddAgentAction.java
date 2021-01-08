package com.facilio.agentv2.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.workflows.context.WorkflowContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Setter @Getter
public class AddAgentAction extends AgentActionV2
{
    private static final Logger LOGGER = LogManager.getLogger(AddAgentAction.class.getName());


    @NotNull
    @Size(min = 3,max = 100,message = "Agent name must have 3-100 characters")
    private String agentName;

    @Min(value = 10,message = "Data interval can't be less than 10")
    @NotNull
    private Long dataInterval;

    @NotNull
    @Min(value = 1,message = "Site can't be less than 1")
    private Long siteId;
    private String type;
    @NotNull @Size(min = 2,max = 100)
    private String displayName;
    private WorkflowContext workflow;
    private Integer agentType;
    private Long orgUserId=-1L;
	public String createAgent() {
        try {
            FacilioChain addAgentChain = TransactionChainFactory.createAgentChain();
            FacilioContext context = addAgentChain.getContext();
            FacilioAgent agent = new FacilioAgent();
            agent.setName(getAgentName());
            agent.setInterval(getDataInterval());
            agent.setSiteId(getSiteId()); //TODO validate SITE ID.
            agent.setType(getType());
            agent.setAgentType(getAgentType());
            agent.setDisplayName(getDisplayName());
            agent.setWorkflow(getWorkflow());
            if (getAgentType() != AgentType.CUSTOM.getKey()) {
                agent.setProcessorVersion(2);
            }
            if(getAgentType() == AgentType.REST.getKey()){
                long orgId = AccountUtil.getCurrentOrg().getOrgId();
                long inboundId = FacilioService.runAsServiceWihReturn(()->insertApiKey(getAgentName(),getOrgUserId(),orgId));
                agent.setInboundConnectionId(inboundId);
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

    private String generateKey ( String agentName ) {
        return Base64.getEncoder().encodeToString(agentName.getBytes());
    }

    private long insertApiKey(String agentName,long orgUserId,long orgId) throws Exception {
        Map<String,Object> prop = new HashMap<>();
        prop.put(AgentConstants.API_KEY,generateKey(agentName));
        prop.put("sender",agentName);
        prop.put(AgentConstants.NAME,agentName);
        prop.put(AgentConstants.CREATED_TIME,System.currentTimeMillis());
        prop.put("authType",1);
        prop.put(AgentConstants.ORGID,orgId);
        prop.put(AgentConstants.CREATED_BY,orgUserId);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
        .fields(FieldFactory.getInboundConnectionsFields())
        .table(ModuleFactory.getInboundConnectionsModule().getTableName());
       return builder.insert(prop);
    }
}
