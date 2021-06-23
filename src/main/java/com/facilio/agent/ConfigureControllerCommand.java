package com.facilio.agent;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;

public class ConfigureControllerCommand extends FacilioCommand
{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        return ControllerUtil.editController((JSONObject) context.get(AgentKeys.CONTROLLER_TABLE));
    }
}
