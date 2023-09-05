package com.facilio.wmsv2.handler;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.handler.ImsHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.util.logging.Logger;
@Log4j
public class ControlActionGenerationHandler extends ImsHandler {

    @Override
    public void processMessage(Message message) {
        try {
            Long controlActionId = getControlActionTemplateId(message);
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ID,controlActionId);
            context.put("startTime",getStartTime(message));
            context.put("endTime",getEndTime(message));
            FacilioChain chain = TransactionChainFactoryV3.getControlActionGenerationChain();
            chain.setContext(context);
            chain.execute();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    private Long getControlActionTemplateId(Message message) {
        JSONObject object = message.getContent();
        return Long.parseLong(String.valueOf(object.get(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_ID)));
    }
    private Long getStartTime(Message message){
        JSONObject object = message.getContent();
        return Long.parseLong(String.valueOf(object.get("startTime")));
    }
    private Long getEndTime(Message message){
        JSONObject object = message.getContent();
        return Long.parseLong(String.valueOf(object.get("endTime")));
    }
}
