package com.facilio.agentv2.device.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.Collection;

public class DeleteFieldDevice extends FacilioCommand
{
    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(DeleteFieldDevice.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info(" in delete fields command ");
        if(context.containsKey(AgentConstants.ID)){
            Collection<Long> deviceId = (Collection<Long>) context.get(AgentConstants.ID);
            int rowsUpdated = FieldDeviceApi.deleteDevices(deviceId);
                if (rowsUpdated>0) {
                    return false;
                }
            }
        return false;
    }
}
