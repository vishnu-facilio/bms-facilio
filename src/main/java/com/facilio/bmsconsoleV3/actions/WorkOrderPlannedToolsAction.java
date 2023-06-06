package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class WorkOrderPlannedToolsAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private Long workOrderId;

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Long getPlannedToolId() {
        return plannedToolId;
    }

    public void setPlannedToolId(Long plannedToolId) {
        this.plannedToolId = plannedToolId;
    }

    private Long plannedToolId;

    public String getPlannedToolForActuals() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getPlannedToolForActualsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.TOOL, plannedToolId);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_TOOLS,FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WORKORDER_TOOLS)));
        return V3Action.SUCCESS;
    }
}
