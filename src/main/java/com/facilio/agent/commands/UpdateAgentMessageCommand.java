package com.facilio.agent.commands;

import com.facilio.agent.AgentKeys;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class UpdateAgentMessageCommand extends FacilioCommand
{
    private static final Logger LOGGER = LogManager.getLogger(UpdateAgentMessageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleCRUDBean bean;
        if( context.containsKey(AgentKeys.ORG_ID) && context.containsKey(FacilioConstants.ContextNames.TO_UPDATE_MAP) ) {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Long.parseLong(context.get(AgentKeys.ORG_ID).toString()));
            Long rowsAffected = bean.updateAgentMessage((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
            if (rowsAffected > 0L) {
                return true;
            }
        }
        LOGGER.info(" Agent Message updation failed ");
        return false;
    }
}
