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

    private Long reservationId;
    private Long itemId;
    private Long itemTransactionId;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Long getItemTransactionId() {
        return itemTransactionId;
    }

    public void setItemTransactionId(Long itemTransactionId) {
        this.itemTransactionId = itemTransactionId;
    }

    public String getWorkorderItem() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getWorkOrderItemChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        context.put(FacilioConstants.ContextNames.ITEM,itemId);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_ITEMS,FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS)));
        return V3Action.SUCCESS;
    }

    public String getWorkorderItemFromReservation() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getWorkorderItemFromReservationChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.INVENTORY_RESERVATION,reservationId);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_ITEMS, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS)));
        return V3Action.SUCCESS;
    }
    public String getWorkOrderItemFromIssuedItem() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getWorkOrderItemFromIssuedItemChainV3();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ITEM_TRANSACTION_ID,itemTransactionId);
        context.put(FacilioConstants.ContextNames.WORK_ORDER,workOrderId);
        chain.execute();
        setData(FacilioConstants.ContextNames.WORKORDER_ITEMS, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.WORKORDER_ITEMS)));
        return V3Action.SUCCESS;
    }
}
