package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class WorkOrderServiceActionV3 extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long workOrderId;
    private List<Long> serviceIds;

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedWorkOrderServiceListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.SERVICE,serviceIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.WO_SERVICE, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.WO_SERVICE), V3WorkOrderServiceContext.class));
        return V3Action.SUCCESS;
    }
}
