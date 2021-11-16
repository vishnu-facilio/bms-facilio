package com.facilio.agentv2.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.queue.source.MessageSource;
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
                    createPolicy(agent, currentOrg, messageTopic);
                    break;
                case CLOUD:
                    //adding topic to db
                    createMessageTopic(currentOrg);
                    AgentApiV2.scheduleRestJob(agent);
                    break;
                case AGENT_SERVICE:
                	CloudAgentUtil.addCloudServiceAgent(agent);
                	createMessageTopic(currentOrg);
                	break;
                	
            }
            
            return false;
        } else {
            throw new Exception(" agent missing from context " + context);
        }
    }
    
    private void createPolicy( FacilioAgent agent,Organization currentOrg, String orgMessageTopic ) throws Exception {
    	try{
            LOGGER.debug("download certificate current org domain is :" + orgMessageTopic);
            String certFileId = com.facilio.agent.FacilioAgent.getCertFileId("facilio");
            long orgId = Objects.requireNonNull(currentOrg.getOrgId());
            Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(orgId, certFileId);
            if (orgInfo == null || orgInfo.isEmpty()) {
                DownloadCertFile.addCertificate(orgMessageTopic, "facilio");
            }
            AwsUtil.addClientToPolicy(agent.getName(), orgMessageTopic, "facilio");
        }catch (Exception e){
            LOGGER.error("Exception occurred while adding Agent cert .. ",e);
            throw e;
        }
    }

    private String createMessageTopic ( Organization currentOrg) throws Exception {
    	long orgId = currentOrg.getOrgId();
    	List<Map<String, Object>> topics = MessageQueueTopic.getTopics(Collections.singletonList(orgId), null);
    	if(CollectionUtils.isEmpty(topics)) {
    		String domain = currentOrg.getDomain();
    		MessageSource source = AgentUtilV2.getMessageSource(null);
    		FacilioService.runAsService(FacilioConstants.Services.AGENT_SERVICE,()->  MessageQueueTopic.addMsgTopic(domain, orgId, source));
    		
    		// Adding kafka topic
     		MessageQueueFactory.getMessageQueue(source).createQueue(domain);
     		return domain;		
    	}
    	return topics.get(0).get("topic").toString();
    }
    
}
