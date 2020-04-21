package com.facilio.agentv2.controller.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.Device;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldDevicesToControllerCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FieldDevicesToControllerCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> devicesToDelete = new ArrayList<>();
        if(context.containsKey(AgentConstants.FIELD_DEIVICES) && context.containsKey(AgentConstants.AGENT_ID)){
            Device device = (Device) context.get(AgentConstants.FIELD_DEIVICES);
            Map<Long, Controller> deviceIdControllerMap = ControllerUtilV2.fieldDeviceToController(device);
            if (deviceIdControllerMap.isEmpty()) {
                throw new Exception(" No controllers for devices->"+device.getId());
            }
            context.put(AgentConstants.CONTROLLER,deviceIdControllerMap.values());
                if(deviceIdControllerMap.containsKey(device.getId())){
                    devicesToDelete.add(device.getId());
                    context.put(AgentConstants.CONTROLLER,deviceIdControllerMap.get(device.getId()));
                }
            return false;
        }else {
            LOGGER.info("Exception Occurred, fieldDevices and agent id are mandatory -- context->"+context);
            throw new Exception(" fieldDevices and agent id are mandatory -- context->"+context);
        }
    }
}
