package com.facilio.agent.integration.wattsense;

import com.facilio.agent.integration.AgentIntegrationKeys;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;

public class AgentIntegrationDeleteCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationDeleteCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleCRUDBean bean;
        bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
        if(context.containsKey(AgentIntegrationKeys.CLIENT_ID)) {
            if (AgentUtil.agentDelete(String.valueOf(context.get(AgentIntegrationKeys.CLIENT_ID)))) {
                if (bean.updateTable((FacilioContext)context) > 0) {
                    return true;
                }
                LOGGER.info("Exception occurred crud bean fail");
                return false;
            }
            LOGGER.info(" Exception occurred delete failed");
            return false;
        }
        LOGGER.info("Exception occurred client id missing");
        return false;
    }
}
