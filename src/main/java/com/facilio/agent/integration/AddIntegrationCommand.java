package com.facilio.agent.integration;

import com.facilio.agent.commands.AddLogChainCommand;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AddIntegrationCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddLogChainCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        return false;
    }
}
