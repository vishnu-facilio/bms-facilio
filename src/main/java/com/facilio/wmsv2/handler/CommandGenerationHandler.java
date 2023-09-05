package com.facilio.wmsv2.handler;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.handler.ImsHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class CommandGenerationHandler extends ImsHandler {

    @Override
    public void processMessage(Message message) {
        try {
            Long controlActionId = getControlActionId(message);
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.Control_Action.CONTROL_ACTION_ID,controlActionId);
            FacilioChain chain = TransactionChainFactoryV3.getCommandGenerationChain();
            chain.setContext(context);
            chain.execute();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    private Long getControlActionId(Message message) {
        JSONObject object = message.getContent();
        return Long.parseLong(String.valueOf(object.get(FacilioConstants.Control_Action.CONTROL_ACTION_ID)));
    }

}
