package com.facilio.ims.handler;


import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;



@Log4j
public class ResourceDecommissioningHandler extends ImsHandler {
    public static final String KEY = "resource-decommission";
    @Override
    @SneakyThrows
    public void processMessage(Message message) {
        try {
            if(message.getOrgId() != null && message.getOrgId() > 0) {
                LOGGER.error("Resource Decommission Handler - Resource "+message.getContent().get("resourceId") );
                FacilioChain chain = (Boolean) message.getContent().get("decommission") ? TransactionChainFactory.updateDecommissionedResources() : TransactionChainFactory.updateRecommissioningResources();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.RESOURCE_ID, message.getContent().get("resourceId"));
                context.put(FacilioConstants.ContextNames.DECOMMISSION, message.getContent().get("decommission"));
                chain.execute();
            }
        } catch (Exception e) {
            LOGGER.error("error occurred", e);
        }
    }
}