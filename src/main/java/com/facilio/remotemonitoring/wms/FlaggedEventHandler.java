package com.facilio.remotemonitoring.wms;

import com.facilio.fms.message.Message;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.modules.FieldUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import java.util.Arrays;

@Log4j
public class FlaggedEventHandler extends ImsHandler {
    public void processMessage(Message message) {

        try {
            JSONObject json = message.getContent();
            FlaggedEventContext flaggedEventContext = FieldUtil.getAsBeanFromJson(json, FlaggedEventContext.class);
            if(flaggedEventContext != null) {
                V3Util.postCreateRecord(FlaggedEventModule.MODULE_NAME, Arrays.asList(flaggedEventContext.getId()), null, null, null);
            }
        } catch (Exception e) {
            LOGGER.error("Error Occured while adding Job: " + e);
        }
    }
}