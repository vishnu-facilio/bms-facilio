package com.facilio.ims.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fw.BeanFactory;
import lombok.extern.log4j.Log4j;
import org.apache.xpath.operations.Bool;

import java.util.List;

@Log4j
public class ServicePMCreationFromTemplateHandler extends ImsHandler{
    @Override
    public void processMessage(Message message) {
        try{
            Long servicePMTemplateId = Long.parseLong(String.valueOf(message.getContent().get("servicePMTemplateId")));
            Integer type = Integer.parseInt(String.valueOf(message.getContent().get("type")));
            List<Long> siteIds  = (List<Long>) message.getContent().get("siteIds");
            List<Long> assetIds=(List<Long>) message.getContent().get("assetIds");
            List<Long> spaceIds=(List<Long>) message.getContent().get("spaceIds");
            ServicePMTriggerContext trigger = (ServicePMTriggerContext) message.getContent().get("trigger");
            Boolean publish = (Boolean) message.getContent().get("publish");
            FacilioContext context = new FacilioContext();
            context.put("servicePMTemplateId",servicePMTemplateId);
            context.put("type",type);
            context.put("siteIds",siteIds);
            context.put("assetIds",assetIds);
            context.put("spaceIds",spaceIds);
            context.put("trigger",trigger);
            context.put("publish",publish);

            FacilioChain chain = FsmTransactionChainFactoryV3.createServicePMFromTemplateChain();
            chain.setContext(context);
            chain.execute();
        }catch(Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }
}
