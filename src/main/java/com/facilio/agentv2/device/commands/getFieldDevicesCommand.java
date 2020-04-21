package com.facilio.agentv2.device.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.Collections;
import java.util.List;

public class getFieldDevicesCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(getFieldDevicesCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if((context.containsKey(AgentConstants.ID)) ){
            Long deviceId = (Long) context.get(AgentConstants.ID);
            List<Device> devices = FieldUtil.getAsBeanListFromMapList(FieldDeviceApi.getDevices(Collections.singletonList(deviceId)), Device.class);
            if(devices.isEmpty()){
                throw new Exception(" no device found ");
            }
            if(devices.size() > 1){
                throw new Exception(" multiple devices found->"+devices.size());
            }
                if(devices.get(0).getAgentId() > 0){
                    context.put(AgentConstants.AGENT_ID,devices.get(0).getAgentId());
                }else {
                   throw new Exception("Exception while getting device object, agentId is 0");
                }
                context.put(AgentConstants.FIELD_DEIVICES,devices.get(0));
                return false;
        }else {
            LOGGER.info(" Exception Occurred,  agentId and ids missing from contest to get FieldDevices");
            throw new Exception(" agentId and ids missing from contest to get FieldDevices");
        }
    }
    private static boolean containdAndNotNull(Context context, String key){
        return ((context != null) && (key != null) && context.containsKey(key) && (context.get(key) != null) );
    }
}
