package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DeletePointCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(DeletePointCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.CONTROLLER_ID,context)){
            if(checkNumber((Number) context.get(AgentConstants.CONTROLLER_ID))){
                // delete point
            }else {
                throw new Exception("controller Id can't be less than 1");
            }
        }else {
            throw new Exception(" controller id missing from context");
        }
        return false;
    }
}
