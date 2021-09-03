package com.facilio.agentv2.commands;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agentv2.AgentAction;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.OrgInfoKeys;
import com.facilio.service.FacilioService;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.messageQueue.MessageQueueTopic;
import com.facilio.workflows.util.WorkflowUtil;

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
            AgentType agentType = AgentType.valueOf(agent.getAgentType());
            if (isPartionEnabled(agentType)) {
            		int partitionId = getAndUpdateMaxPartition();
            		agent.setPartitionId(partitionId);
            }
            
            long agentId = AgentApiV2.addAgent(agent);
            agent.setId(agentId);
            Organization currentOrg = AccountUtil.getCurrentOrg();
            switch (agentType) {
                case REST:
                case WATTSENSE:
                    createMessageTopic(currentOrg);
                    break;
                case FACILIO:
                case NIAGARA:
                case CUSTOM:
                    String messageTopic = createMessageTopic(currentOrg);
                    createPolicy(agent,currentOrg, messageTopic);
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
                case AGENT_SERVICE:
                		// TODO Add policy and kafka topic if not added. Increase partiotion every 10 agents
                		CloudAgentUtil.addCloudServiceAgent(agent);
                		createMessageTopic(currentOrg, agent.getPartitionId());
                		break;
                	
            }
            
            return false;
        } else {
            throw new Exception(" agent missing from context " + context);
        }
    }

    private void createPolicy ( FacilioAgent agent,Organization currentOrg, String orgMessageTopic ) throws Exception {
        try{
            LOGGER.debug("download certificate current org domain is :" + orgMessageTopic);
            String certFileId = com.facilio.agent.FacilioAgent.getCertFileId("facilio");
            long orgId = Objects.requireNonNull(currentOrg.getOrgId());
            Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
            if (orgInfo == null || orgInfo.isEmpty()) {
                DownloadCertFile.addCert(orgMessageTopic, "facilio", agent);
            }
            AwsUtil.addClientToPolicy(agent.getName(), orgMessageTopic, "facilio");
        }catch (Exception e){
            LOGGER.error("Exception occurred while adding Agent cert .. ",e);
            throw e;
        }
    }
    private String createMessageTopic ( Organization currentOrg ) throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,()-> AgentAction.getMessageTopic(currentOrg.getDomain(),currentOrg.getOrgId()) );
    }
    
    private boolean createMessageTopic ( Organization currentOrg, int paritionId ) throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,()->  MessageQueueTopic.addMsgTopic(currentOrg.getDomain(), currentOrg.getOrgId(), paritionId) );
    }
    
    // Checking if partition is needed based on agent type for now. This may be needed to be set from UI itself
    private boolean isPartionEnabled(AgentType agentType) {
    		return agentType == AgentType.AGENT_SERVICE;
    }
    
    private int getAndUpdateMaxPartition() throws Exception {
    		int partitionId = 0;
    		String key = OrgInfoKeys.MAX_AGENT_PARTITION;
    		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(key);
    		if (MapUtils.isNotEmpty(orgInfo)) {
    			partitionId = Integer.parseInt(orgInfo.get(key)) + 1;
    		}
    		CommonCommandUtil.insertOrgInfo(key, String.valueOf(partitionId));
    		
    		return partitionId;
    }
}
