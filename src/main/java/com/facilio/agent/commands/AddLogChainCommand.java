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

public class AddLogChainCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddLogChainCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(context.containsKey(AgentKeys.ORG_ID) && context.containsKey(FacilioConstants.ContextNames.TO_INSERT_MAP)) {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", Long.parseLong(context.get(AgentKeys.ORG_ID).toString()));
            Long rowCreated = bean.addLog((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_INSERT_MAP));
            if (rowCreated > -1) {
                return true;
            }
        }
        LOGGER.info("adding Agent Log failed");
        return false;
    }
}
