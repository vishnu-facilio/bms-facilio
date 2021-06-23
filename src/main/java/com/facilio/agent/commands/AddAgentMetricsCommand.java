package com.facilio.agent.commands;

import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agent.AgentKeys;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddAgentMetricsCommand extends FacilioCommand
{
    private static final Logger LOGGER = LogManager.getLogger(AddAgentMetricsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleCRUDBean bean;
        if(context.containsKey(AgentKeys.ORG_ID) && context.containsKey(FacilioConstants.ContextNames.TO_INSERT_MAP)) {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Long.parseLong(context.get(AgentKeys.ORG_ID).toString()));
            if (context.containsKey(FacilioConstants.ContextNames.TO_INSERT_MAP)) {
                bean.insertAgentMetrics((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_INSERT_MAP));
                return true;
            }
        }
        LOGGER.info("Adding Agent Message failed");
        return false;
    }
}
