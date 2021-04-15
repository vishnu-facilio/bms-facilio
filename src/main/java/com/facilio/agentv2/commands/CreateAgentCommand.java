package com.facilio.agentv2.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.agent.AgentType;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agentv2.AgentAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.service.FacilioService;
import com.facilio.services.messageQueue.MessageQueueFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.Map;
import java.util.Objects;

public class CreateAgentCommand extends AgentV2Command {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(CreateAgentCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT, context)) {
            FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
            if (agent.getWorkflow() != null) {
	            	if(agent.getWorkflow().validateWorkflow()) {
	            		long workflowId = WorkflowUtil.addWorkflow(agent.getWorkflow());
	            		agent.setWorkflowId(workflowId);
	    			}
	    			else {
	    				throw new IllegalArgumentException(agent.getWorkflow().getErrorListener().getErrorsAsString());
	    			}
            }
            long agentId = AgentApiV2.addAgent(agent);
            agent.setId(agentId);
            Organization currentOrg = AccountUtil.getCurrentOrg();
            switch (AgentType.valueOf(agent.getAgentType())) {
                case REST:
                case WATTSENSE:
                    createMessageTopic(currentOrg);
                    break;
                case FACILIO:
                case NIAGARA:
                case CUSTOM:
                    createMessageTopic(currentOrg);
                    createPolicy(agent,currentOrg);
                    return true;
                case CLOUD:
                    //creating kinesis stream
                    AwsUtil.createKinesisStream(AwsUtil.getKinesisClient(), currentOrg.getDomain());
                    //creating kafka topic
                    MessageQueueFactory.getMessageQueue().createQueue(currentOrg.getDomain());
                    //adding topic to db
                    createMessageTopic(currentOrg);
                    //createPolicy(agent,currentOrg);
                    AgentApiV2.scheduleRestJob(agent);
                    return true;
            }
            return false;
        } else {
            throw new Exception(" agent missing from context " + context);
        }
    }

    private void createPolicy ( FacilioAgent agent,Organization currentOrg ) {
        try{
            String orgMessageTopic = createMessageTopic(currentOrg);
            LOGGER.info("download certificate current org domain is :" + orgMessageTopic);
            String certFileId = com.facilio.agent.FacilioAgent.getCertFileId("facilio");
            long orgId = Objects.requireNonNull(currentOrg.getOrgId());
            Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
            if (orgInfo == null || orgInfo.isEmpty()) {
                DownloadCertFile.addCert(orgMessageTopic, "facilio");
            }
            AwsUtil.addClientToPolicy(agent.getName(), orgMessageTopic, "facilio");
        }catch (Exception e){
            LOGGER.error("Exception occurred while adding Agent cert .. ",e);
        }
    }
    private String createMessageTopic ( Organization currentOrg ) throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,()-> AgentAction.getMessageTopic(currentOrg.getDomain(),currentOrg.getOrgId()));
    }
}
