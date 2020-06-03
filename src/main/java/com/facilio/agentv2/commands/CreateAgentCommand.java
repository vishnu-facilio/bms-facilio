package com.facilio.agentv2.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.aws.util.AwsUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

public class CreateAgentCommand extends AgentV2Command {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(CreateAgentCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT, context)) {
            String orgDomainname = AccountUtil.getCurrentOrg().getDomain();
            String agentDownloadUrl = "agentDownloadUrl";
            FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
            String certFileDownloadUrl = "certDownloadUrl";
            long agentId = AgentApiV2.addAgent(agent);
            agent.setId(agentId);
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
        } else {
            throw new Exception(" agent missing from context " + context);
        }
    }

    private String packAgent(FacilioAgent agent) {
        return "no implementation yet"; //TODO implement agent packing.
    }
}
