package com.facilio.agent;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;

public class DeleteAgentCommand extends FacilioCommand
{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject payload = new JSONObject();
        if(context.containsKey(AgentKeys.ID)) {
            return AgentUtil.agentDelete(String.valueOf(context.get(AgentKeys.ID)));
        }
        return false;
    }

}


