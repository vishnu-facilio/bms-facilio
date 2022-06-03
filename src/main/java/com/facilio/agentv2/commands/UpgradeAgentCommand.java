package com.facilio.agentv2.commands;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.actions.VersionLogAction;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.iotmessage.IotData;
import com.facilio.agentv2.upgrade.AgentVersionApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.service.FacilioService;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Objects;

public class UpgradeAgentCommand extends AgentV2Command {
	private static final Logger LOGGER = LogManager.getLogger(UpgradeAgentCommand.class.getName());
	@Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT_ID, context)) {
            long agentId = (Long) context.get(AgentConstants.AGENT_ID);
            if (containsCheck(AgentConstants.VERSION_ID, context)) {
                long versionId = (long) context.get(AgentConstants.VERSION_ID);
                Map<String, Object> agentVersion = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,() -> AgentVersionApi.getAgentVersion(versionId));
                if (containsCheck(AgentConstants.VERSION, agentVersion)) {
                    String url = AgentVersionApi.getAgentDownloadUrl();
                    String version = agentVersion.get(AgentConstants.VERSION).toString();
                    String authKey = AgentVersionApi.getAuthKey();
                    Organization currentOrg = AccountUtil.getCurrentOrg();
                    Objects.requireNonNull(currentOrg);
                    long orgIg = currentOrg.getOrgId();
                    AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
                    FacilioAgent agent = agentBean.getAgent(agentId);
                    FacilioService.runAsService(FacilioConstants.Services.AGENT_SERVICE,() -> AgentVersionApi.logAgentUpgrateRequest(agent, versionId, authKey, orgIg));
                    IotData iotData = AgentMessenger.sendAgentUpgradeCommand(agentId, version, url, authKey);
                    context.put(AgentConstants.DATA, iotData);
                } else {
                    throw new Exception(" version missing from agent versionData");
                }
            }
        }

        return false;
    }


}
