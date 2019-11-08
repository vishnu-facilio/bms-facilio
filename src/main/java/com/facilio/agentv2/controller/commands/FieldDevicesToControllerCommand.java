package com.facilio.agentv2.controller.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.Device;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.List;

public class FieldDevicesToControllerCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FieldDevicesToControllerCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info(" in field device to controller ");
        if(context.containsKey(AgentConstants.FIELD_DEIVICES) && context.containsKey(AgentConstants.AGENT_ID)){
            List<Device> devices = (List<Device>) context.get(AgentConstants.FIELD_DEIVICES);
            long agentId = (long) context.get(AgentConstants.AGENT_ID);
            List<Long> devicesToDelete = ControllerUtilV2.fieldDeviceToController(agentId, devices);
            context.replace(AgentConstants.ID,devicesToDelete);
            return false;
        }else {
            LOGGER.info("Exception Occurred, fieldDevices and agent id are mandatory -- context->"+context);
            throw new Exception(" fieldDevices and agent id are mandatory -- context->"+context);
        }
    }
}
