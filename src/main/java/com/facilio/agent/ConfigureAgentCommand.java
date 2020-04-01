package com.facilio.agent;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;

public class ConfigureAgentCommand extends FacilioCommand
{

    @Override
    public boolean executeCommand(Context context) throws Exception {
            JSONObject agentContext = (JSONObject) context.get(PublishType.AGENT.getValue());
            return  AgentUtil.agentEdit(agentContext);
    }

}
