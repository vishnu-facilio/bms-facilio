package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3ControlActionTemplateAction extends V3Action {
    private Long controlActionTemplateId;
    public String activateControlActionTemplate() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put("controlActionTemplateId",controlActionTemplateId);
        FacilioChain chain = TransactionChainFactoryV3.getActivateControlActionTemplateChain();
        chain.setContext(context);
        chain.execute();
        return SUCCESS;
    }
    public String inActivateControlActionTemplate() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put("controlActionTemplateId",controlActionTemplateId);
        FacilioChain chain = TransactionChainFactoryV3.getInActivateControlActionTemplateChain();
        chain.setContext(context);
        chain.execute();
        return SUCCESS;
    }

}
