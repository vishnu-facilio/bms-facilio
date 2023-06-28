package com.facilio.ims.handler;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;

@Log4j
public class CommissioningLogHandler extends ImsHandler {

    public static final String KEY = "decommission-log";
    @Override
    @SneakyThrows
    public void processMessage(Message message) {
        try {
            if(message.getOrgId() != null && message.getOrgId() > 0) {
                LOGGER.error("Decommission Log Handler - Resource ID :  "+message.getContent().get("resourceId") );
                FacilioChain chain = TransactionChainFactoryV3.addDecommissionLog();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.LOG_CONTEXT, message.getContent());
                chain.execute();
            }
        } catch (Exception e) {
            LOGGER.error("error occurred", e);
        }
    }

}
