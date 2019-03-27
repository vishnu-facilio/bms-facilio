package com.facilio.agent;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class DeleteAgentCommand implements Command
{
    @Override
    public boolean execute(Context context) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put(AgentKeys.ID,context.get(AgentKeys.ID));
        return AgentUtil.agentDelete(payload);
    }

}


