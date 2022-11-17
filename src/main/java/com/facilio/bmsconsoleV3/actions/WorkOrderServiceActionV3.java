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
    private Long serviceId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkorderService() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getWorkOrderServiceChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.SERVICE,serviceId);
        chain.execute();
        setData(FacilioConstants.ContextNames.WO_SERVICE, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WO_SERVICE)));
        return V3Action.SUCCESS;
    }
}
