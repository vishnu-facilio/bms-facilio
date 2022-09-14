package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

public class WorkOrderPlannedServicesAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private Long workOrderId;

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String cost() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getCostChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrderId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
        chain.execute();
        setData(FacilioConstants.ContextNames.COST, context.get(FacilioConstants.ContextNames.COST));
        return V3Action.SUCCESS;
    }
}
