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
        if(context.containsKey(AgentConstants.ID)){
            Collection<Long> deviceId = (Collection<Long>) context.get(AgentConstants.ID);
            if( deviceId.isEmpty() ){
                throw new Exception(" No controllers added ");
            }
            int rowsUpdated = FieldDeviceApi.deleteDevices(deviceId);
                if (rowsUpdated>0) {
                    return true;
                }else {
                    throw new Exception("No device deleted rowsUpdated->"+rowsUpdated+" , deviceId->"+deviceId);
                }
            }
        return false;
    }
}
