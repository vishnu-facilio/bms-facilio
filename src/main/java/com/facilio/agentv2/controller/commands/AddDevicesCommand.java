package com.facilio.agentv2.controller.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class AddDevicesCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(AddDevicesCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (context.containsKey(AgentConstants.DATA)) {
            FacilioContext deviceContex = new FacilioContext();
            List<Device> devices = (List<Device>) context.get(AgentConstants.DATA);
            FieldDeviceApi.addFieldDevices(devices);
            return true;
        }
        return false;
    }
}
