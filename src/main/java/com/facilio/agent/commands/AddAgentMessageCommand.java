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

public class AddAgentMessageCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(AddAgentMessageCommand.class.getName());

    @Override
    public boolean execute(Context context) throws Exception {
        ModuleCRUDBean bean;
        bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Long.parseLong(context.get(AgentKeys.ORG_ID).toString()) );
        if(context.containsKey(FacilioConstants.ContextNames.TO_UPDATE_MAP)) {
            Long rowCreated = bean.addAgentMessage((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
            if (rowCreated > -1L) {
                return true;
            }
        }
        LOGGER.info("Agent Message addition failed");
        return false;
    }
}
