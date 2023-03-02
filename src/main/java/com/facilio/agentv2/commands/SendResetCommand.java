package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SendResetCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(DeletePointCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.CONTROLLER_ID,context)){
            if(checkNumber((Number) context.get(AgentConstants.CONTROLLER_ID))){
                AgentConstants.getControllerBean().resetConfiguredPoints((Long) context.get(AgentConstants.CONTROLLER_ID));
                ControllerMessenger.resetController((Long) context.get(AgentConstants.CONTROLLER_ID));
            }else {
                throw new Exception(" controller id can't be less than 1");
            }
        }else {
            throw new Exception("controller id missing from context->"+context);
        }
        return false;
    }
}
