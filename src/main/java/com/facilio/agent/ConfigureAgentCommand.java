package com.facilio.agent;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class ConfigureAgentCommand implements Command
{

    @Override
    public boolean execute(Context context) throws Exception {
            JSONObject agentContext = (JSONObject) context.get(AgentKeys.AGENT);
            return  AgentUtil.agentEdit(agentContext);
    }

}
