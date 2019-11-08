package com.facilio.agentv2.device.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.List;

public class getFieldDevicesCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(getFieldDevicesCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info(" in fielddevices command");
        if( context.containsKey(AgentConstants.AGENT_ID) && (context.containsKey(AgentConstants.ID)) ){
            long agentId = (long) context.get(AgentConstants.AGENT_ID);
            List<Long> ids = (List<Long>) context.get(AgentConstants.ID);
            List<Device> devices = FieldUtil.getAsBeanListFromMapList(FieldDeviceApi.getDevices(agentId, ids),Device.class);
            LOGGER.info(" field devices are "+devices);
            if( ! devices.isEmpty() ){
                context.put(AgentConstants.FIELD_DEIVICES,devices);
                LOGGER.info(" returning field devices ");
                return false;
            }else {
                LOGGER.info("Exception Occurred,  fields devices can't be empty ");
                throw new Exception(" Devices empty ");
            }
        }else {
            LOGGER.info(" Exception Occurred,  agentId and ids missing from contest to get FieldDevices");
            throw new Exception(" agentId and ids missing from contest to get FieldDevices");
        }
    }
}
