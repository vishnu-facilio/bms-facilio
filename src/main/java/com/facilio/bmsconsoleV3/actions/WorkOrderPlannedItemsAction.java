package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class WorkOrderPlannedItemsAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private List<Long> recordIds;
    public List<Long> getRecordIds() {
        return recordIds;
    }
    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }

    public String reserve() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getReserveChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.WO_PLANNED_ITEMS),WorkOrderPlannedItemsContext.class));
        return V3Action.SUCCESS;
    }
}
