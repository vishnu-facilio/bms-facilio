package com.facilio.agentv2.iotmessage;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

public class AddAndSendIotMessageCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(AddAndSendIotMessageCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info(" add and send iot message command ");
        IotData data = (IotData) context.get(AgentConstants.DATA);
        for (IotMessage message : data.getMessages()) {
            LOGGER.info(" message - " + message);
        }
        IotMessageApiV2.addIotData(data);
        IotMessageApiV2.addIotMessage(data.getId(), data.getMessages());
        IotMessageApiV2.publishIotMessage(data);
        return false;
    }
}
