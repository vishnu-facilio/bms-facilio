package com.facilio.agentIntegration.wattsense;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentUtil;
import com.facilio.agentIntegration.AgentIntegrationKeys;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AgentIntegrationDeleteCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(Wattsense.class.getName());

    @Override
    public boolean execute(Context context) throws Exception {
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
