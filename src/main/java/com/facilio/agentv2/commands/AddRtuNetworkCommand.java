package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import org.apache.commons.chain.Context;

public class AddRtuNetworkCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.RTU_NETWORK, context)) {
            RtuNetworkContext rtuNetwork = (RtuNetworkContext) context.get(AgentConstants.RTU_NETWORK);
            RtuNetworkContext.addRtuNetworkCommand(rtuNetwork);
            AgentMessenger.sendConfigureModbusRtuNetworkCommand(rtuNetwork);
        } else {
            throw new Exception(" context is missinf rtu network key ->" + context);
        }
        return false;
    }
}
