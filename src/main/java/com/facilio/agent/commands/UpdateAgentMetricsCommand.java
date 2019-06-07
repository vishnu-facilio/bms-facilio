package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class UpdateAgentMetricsCommand implements Command
{
    private static final Logger LOGGER = LogManager.getLogger(UpdateAgentMetricsCommand.class.getName());

    @Override
    public boolean execute(Context context) throws Exception {
        ModuleCRUDBean bean;
        bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Long.parseLong(context.get(AgentKeys.ORG_ID).toString()) );
        int rowsUpdated =0;
        if(context.containsKey(FacilioConstants.ContextNames.TO_UPDATE_MAP) && context.containsKey(FacilioConstants.ContextNames.CRITERIA)) {
           rowsUpdated = bean.updateAgentMetrics((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP), (Map<String, Object>) context.get(FacilioConstants.ContextNames.CRITERIA));
        }
        if(rowsUpdated > 0){
            return true;
        }
        LOGGER.info("Updating agent Metrics failed");
        return false;
    }
}
