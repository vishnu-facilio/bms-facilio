package com.facilio.agentv2.iotmessage;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.logging.Logger;

public class AddAndSendIotMessageCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        IotData data = (IotData) context.get(AgentConstants.DATA);
        System.out.println(" in execute command -- "+data.getMessages().size());
        for (IotMessage message : data.getMessages()) {
            Logger.getLogger(" message - "+message);
        }
        IotMessageApiV2.addIotData(data);
        IotMessageApiV2.addIotMessage(data.getId(),data.getMessages());
        IotMessageApiV2.publishIotMessage(data);
        return false;
    }
}
