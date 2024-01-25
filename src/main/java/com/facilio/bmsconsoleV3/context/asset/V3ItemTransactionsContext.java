package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.fsm.context.ServiceInventoryReservationContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderItemsContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class V3ItemTransactionsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3ItemTypesContext itemType;

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    private V3ItemContext item;

    public V3ItemContext getItem() {
        return item;
    }

    public void setItem(V3ItemContext inventory) {
        this.item = inventory;
    }

    private V3PurchasedItemContext purchasedItem;

    public V3PurchasedItemContext getPurchasedItem() {
        return purchasedItem;
    }

    public void setPurchasedItem(V3PurchasedItemContext inventoryCost) {
        this.purchasedItem = inventoryCost;
    }
    private V3BinContext bin;

    public V3BinContext getBin() {
        return bin;
    }

    public void setBin(V3BinContext bin) {
        this.bin = bin;
    }

    private double quantity = -1;

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    private long parentId = -1;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private Boolean isReturnable;

    public Boolean getIsReturnable() {
        return isReturnable;
    }

    public void setIsReturnable(Boolean isReturnable) {
        this.isReturnable = isReturnable;
    }

    public boolean isReturnable() {
        if (isReturnable != null) {
            return isReturnable.booleanValue();
        }
        return false;
    }
    @Getter @Setter
    private String remarks;


    private TransactionType transactionType;

    public TransactionType getTransactionTypeEnum() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getTransactionType() {
        if (transactionType != null) {
            return transactionType.getValue();
        }
        return -1;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = TransactionType.valueOf(transactionType);
    }

    private TransactionState transactionState;

    public TransactionState getTransactionStateEnum() {
        return transactionState;
    }

    public String getTransactionStateLabel() { return transactionState.getLabel(); }

    public void setTransactionState(TransactionState transactionState) {
        this.transactionState = transactionState;
    }

    public int getTransactionState() {
        if (transactionState != null) {
            return transactionState.getValue();
        }
        return -1;
    }

    public void setTransactionState(int transactionState) {
        this.transactionState = TransactionState.valueOf(transactionState);
    }

    private long parentTransactionId = -1;

    public long getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(long parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    private User issuedTo;

    public User getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(User owner) {
        this.issuedTo = owner;
    }

    private Double remainingQuantity;

    public Double getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Double remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    private List<Long> purchasedItems;
    public List<Long> getPurchasedItems() {
        return purchasedItems;
    }
    public void setPurchasedItems(List<Long> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    private ApprovalState approvedState;

    public ApprovalState getApprovedStateEnum() {
        return approvedState;
    }

    public void setApprovedState(ApprovalState approvedState) {
        this.approvedState = approvedState;
    }

    public int getApprovedState() {
        if (approvedState != null) {
            return approvedState.getValue();
        }
        return -1;
    }

    public void setApprovedState(int approvedState) {
        this.approvedState = ApprovalState.valueOf(approvedState);
    }

    private V3TicketContext workorder;
    public V3TicketContext getWorkorder() {
        return workorder;
    }
    public void setWorkorder(V3TicketContext workorder) {
        this.workorder = workorder;
    }

    private GatePassContext gatePass;
    public GatePassContext getGatePass() {
        return gatePass;
    }
    public void setGatePass(GatePassContext gatePass) {
        this.gatePass = gatePass;
    }

    private V3AssetContext asset;
    public V3AssetContext getAsset() {
        return asset;
    }
    public void setAsset(V3AssetContext asset) {
        this.asset = asset;
    }

    private List<Long> assetIds;
    public List<Long> getAssetIds() {
        return assetIds;
    }
    public void setAssetIds(List<Long> assetIds) {
        this.assetIds = assetIds;
    }

    private Long shipment;

    public Long getShipment() {
        return shipment;
    }

    public void setShipment(Long shipment) {
        this.shipment = shipment;
    }


    private V3InventoryRequestLineItemContext requestedLineItem;
    public V3InventoryRequestLineItemContext getRequestedLineItem() {
        return requestedLineItem;
    }
    public void setRequestedLineItem(V3InventoryRequestLineItemContext requestedLineItem) {
        this.requestedLineItem = requestedLineItem;
    }

    private V3ResourceContext resource;

    public V3ResourceContext getResource() {
        return resource;
    }

    public void setResource(V3ResourceContext resource) {
        this.resource = resource;
    }

    private double transactionCost;

    public double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(double transactionCost) {
        this.transactionCost = transactionCost;
    }

    private V3StoreRoomContext storeRoom;

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public InventoryReservationContext inventoryReservation;

    public InventoryReservationContext getInventoryReservation() {
        return inventoryReservation;
    }

    public void setInventoryReservation(InventoryReservationContext inventoryReservation) {
        this.inventoryReservation = inventoryReservation;
    }
    private ServiceOrderContext serviceOrder;

    public ServiceOrderContext getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrderContext serviceOrder) {
        this.serviceOrder = serviceOrder;
    }
    private ServiceOrderItemsContext serviceOrderItem;

    public ServiceOrderItemsContext getServiceOrderItem() {
        return serviceOrderItem;
    }

    public void setServiceOrderItem(ServiceOrderItemsContext serviceOrderItem) {
        this.serviceOrderItem = serviceOrderItem;
    }
    private ServiceInventoryReservationContext serviceInventoryReservation;

    public ServiceInventoryReservationContext getServiceInventoryReservation() {
        return serviceInventoryReservation;
    }

    public void setServiceInventoryReservation(ServiceInventoryReservationContext serviceInventoryReservation) {
        this.serviceInventoryReservation = serviceInventoryReservation;
    }
}
