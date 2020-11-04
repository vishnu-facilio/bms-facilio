package com.facilio.agentv2.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class CreateAgentCommand extends AgentV2Command {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(CreateAgentCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT, context)) {
            String orgDomainname = AccountUtil.getCurrentOrg().getDomain();
            String agentDownloadUrl = "agentDownloadUrl";
            FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
            String certFileDownloadUrl = "certDownloadUrl";
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
            String type = agent.getType();
            if (type.equalsIgnoreCase("facilio")
                    || type.equalsIgnoreCase("niagara")) {
                return createPolicy(context, orgDomainname, agent, certFileDownloadUrl);
            } else if (type.equalsIgnoreCase("rest")) {
            		AgentApiV2.scheduleRestJob(agent);
            }
            return false;
        } else {
            throw new Exception(" agent missing from context " + context);
        }
    }

    private boolean createPolicy(Context context, String orgDomainname, FacilioAgent agent, String certFileDownloadUrl) throws Exception {
        String agentDownloadUrl;
        AwsUtil.addClientToPolicy(agent.getName(), orgDomainname, "facilio");
        if (certFileDownloadUrl != null) {
            context.put(AgentConstants.CERT_FILE_DOWNLOAD_URL, certFileDownloadUrl);
            // pack agent and give a download link
            agentDownloadUrl = packAgent(agent);
            if ((agentDownloadUrl != null) && (agent.getType().equals("facilio")) && (agent.getType().equals("niagara"))) {
                switch (agent.getType()) {
                    case "facilio":
                    case "niagara":
                        context.put(AgentConstants.DOWNLOAD_AGENT, agentDownloadUrl);
                        break;
                    default:
                }
            } else {
                LOGGER.info(" agentDownload link cant be null ");
            }

            return false;
        } else {
            throw new Exception(" certFile download url can't be null ");
        }
    }

    private String packAgent(FacilioAgent agent) {
        return "no implementation yet"; //TODO implement agent packing.
    }
}
