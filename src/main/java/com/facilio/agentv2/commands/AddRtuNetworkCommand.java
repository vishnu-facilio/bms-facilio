package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AddRtuNetworkCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(AddRtuNetworkCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(FacilioConstants.ContextNames.RTU_NETWORK, context)) {
            ModbusRtuControllerContext controller = (ModbusRtuControllerContext) context.get(FacilioConstants.ContextNames.RECORD);
            RtuNetworkContext rtuNetwork = (RtuNetworkContext) context.get(AgentConstants.RTU_NETWORK);
            try {
                if (rtuNetwork.getId() > 0) {
                    controller.setNetworkId(rtuNetwork.getId());
                } else {
                    long id = RtuNetworkContext.addRtuNetworkCommand(rtuNetwork);
                    if (id > 0) {
                        controller.setNetworkId(id);
                    }
                }

            } catch (java.sql.BatchUpdateException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    LOGGER.info(" duplicate enrty for network and sending command to agent ");
                } else {
                    throw e;
                }
            }
        }
        return false;
    }
}
