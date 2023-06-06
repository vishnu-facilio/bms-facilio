package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class WorkOrderPlannedServicesAction  extends V3Action {
    private static final long serialVersionUID = 1L;

    private Long plannedServiceId;

    public Long getPlannedServiceId() {
        return plannedServiceId;
    }

    public void setPlannedServiceId(Long plannedServiceId) {
        this.plannedServiceId = plannedServiceId;
    }

    public String getPlannedServiceForActuals() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getPlannedServiceForActualsChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, plannedServiceId);
        chain.execute();
        setData(FacilioConstants.ContextNames.WO_SERVICE,FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WO_SERVICE)));
        return V3Action.SUCCESS;
    }
}
