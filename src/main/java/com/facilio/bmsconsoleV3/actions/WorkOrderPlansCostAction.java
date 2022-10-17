package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

public class WorkOrderPlansCostAction  extends V3Action {
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
        chain.execute();
        setData(FacilioConstants.ContextNames.PLANNED_ITEMS_COST, context.get(FacilioConstants.ContextNames.PLANNED_ITEMS_COST));
        setData(FacilioConstants.ContextNames.PLANNED_TOOLS_COST, context.get(FacilioConstants.ContextNames.PLANNED_TOOLS_COST));
        setData(FacilioConstants.ContextNames.PLANNED_SERVICES_COST, context.get(FacilioConstants.ContextNames.PLANNED_SERVICES_COST));
        setData(FacilioConstants.ContextNames.PLANNED_LABOUR_COST, context.get(FacilioConstants.ContextNames.PLANNED_LABOUR_COST));
        return V3Action.SUCCESS;
    }
}
