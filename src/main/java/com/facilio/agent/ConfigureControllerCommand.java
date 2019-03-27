package com.facilio.agent;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class ConfigureControllerCommand implements Command
{
    @Override
    public boolean execute(Context context) throws Exception {
        return ControllerUtil.editController((JSONObject) context.get(AgentKeys.CONTROLLER_TABLE));
    }
}
