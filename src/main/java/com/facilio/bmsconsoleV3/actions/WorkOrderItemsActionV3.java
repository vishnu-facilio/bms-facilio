package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;

import java.util.List;

public class WorkOrderItemsActionV3 extends V3Action {
    private static final long serialVersionUID = 1L;
    private Long workOrderId;

    private List<Long> reservationIds;
    private List<Long> itemIds;

    public List<Long> getReservationIds() {
        return reservationIds;
    }

    public void setReservationIds(List<Long> reservationIds) {
        this.reservationIds = reservationIds;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedWorkOrderItemsListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.ITEM,itemIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_ITEMS,FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS), V3WorkorderItemContext.class));
        return V3Action.SUCCESS;
    }

    public String reservedItemList() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getUnsavedReservedWorkOrderItemsListChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.INVENTORY_RESERVATION,reservationIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_ITEMS, FieldUtil.getAsJSONArray((List)context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS), V3WorkorderItemContext.class) );
        return V3Action.SUCCESS;
    }
}
