package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Map;

public class AddMessageSourceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = CloudAgentUtil.addMessageSource((Map<String, Object>) context.get(AgentConstants.MESSAGE_SOURCE));
        context.put(AgentConstants.ID,id);
        return false;
    }
}
