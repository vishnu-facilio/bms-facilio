package com.facilio.fsm.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceOrderServicesAction extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long plannedServiceId;
    public String serviceOrderServiceFromPlan() throws Exception {
        FacilioChain chain = FsmTransactionChainFactoryV3.getServiceOrderServiceFromPlan();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICE_ID,plannedServiceId);
        chain.execute();
        setData(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES)));
        return V3Action.SUCCESS;
    }
}
