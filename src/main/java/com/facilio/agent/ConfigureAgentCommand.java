package com.facilio.agent;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;

public class ConfigureAgentCommand extends FacilioCommand
{

    @Override
    public boolean executeCommand(Context context) throws Exception {
            JSONObject agentContext = (JSONObject) context.get(PublishType.agent.getValue());
            return  AgentUtil.agentEdit(agentContext);
    }

}
