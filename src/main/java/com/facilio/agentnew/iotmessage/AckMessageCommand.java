package com.facilio.agentnew.iotmessage;

import com.facilio.agent.fw.constants.Status;
import com.facilio.agentnew.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AckMessageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        return NewIotMessageAPI.acknowdledgeMessage((long)context.get(AgentConstants.ID), (Status) context.get(AgentConstants.STATUS),(JSONObject) context.get(AgentConstants.DATA));
    }
}
