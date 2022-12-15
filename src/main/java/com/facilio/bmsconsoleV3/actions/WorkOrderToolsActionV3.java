package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class WorkOrderToolsActionV3 extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long workOrderId;
    private List<Long> toolIds;

    public List<Long> getToolIds() {
        return toolIds;
    }

    public void setToolIds(List<Long> toolIds) {
        this.toolIds = toolIds;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedWorkOrderToolsListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.TOOL,toolIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_TOOLS, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.WORKORDER_TOOLS), V3WorkorderToolsContext.class));
        return V3Action.SUCCESS;
    }
}
