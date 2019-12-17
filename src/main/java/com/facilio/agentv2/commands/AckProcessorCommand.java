package com.facilio.agentv2.commands;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class AckProcessorCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        if(containsCheck(AgentConstants.DATA,context)){
            JSONObject jsonObject = (JSONObject) context.get(AgentConstants.DATA);
            if((jsonObject.containsKey(AgentConstants.COMMAND))){
                int commandInt = ((Number) jsonObject.get(AgentConstants.COMMAND)).intValue();
                FacilioCommand command = FacilioCommand.valueOf(commandInt);
                switch (command){
                    case CONFIGURE:
                        processConfigureAck(jsonObject);
                }
            }else {
                throw new Exception(" command missing from payload");
            }
        }else {
            throw new Exception(" error with data key ->"+context);
        }

        return false;
    }

    private void processConfigureAck(JSONObject jsonObject) throws Exception{
        Status status = getStatusFromPAyload(jsonObject);
        //if(containsCheck(AgentConstants.))
    }

    private Status getStatusFromPAyload(JSONObject payload) throws Exception{
        if (containsCheck(AgentConstants.STATUS,payload)) {
            return Status.valueOf(((Number)payload.get(AgentConstants.STATUS)).intValue());
        }
        throw new Exception("status missing from payload");
    }
}
