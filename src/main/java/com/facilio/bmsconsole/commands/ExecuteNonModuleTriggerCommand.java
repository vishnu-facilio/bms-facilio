package com.facilio.bmsconsole.commands;

import com.facilio.trigger.context.TriggerType;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ExecuteNonModuleTriggerCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(ExecuteNonModuleTriggerCommand.class.getName());

    public ExecuteNonModuleTriggerCommand() {
    }

    @Override
    public boolean execute(Context context) throws Exception {
        return false;
    }
}
