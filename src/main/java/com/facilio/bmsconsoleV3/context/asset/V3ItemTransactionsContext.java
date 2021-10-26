package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3ItemTransactionsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ItemTypesContext itemType;

    public ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    private ItemContext item;

    public ItemContext getItem() {
        return item;
    }

    public void setItem(ItemContext inventory) {
        this.item = inventory;
    }

    private PurchasedItemContext purchasedItem;

    public PurchasedItemContext getPurchasedItem() {
        return purchasedItem;
    }

    public void setPurchasedItem(PurchasedItemContext inventoryCost) {
        this.purchasedItem = inventoryCost;
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

    private TicketContext workorder;
    public TicketContext getWorkorder() {
        return workorder;
    }
    public void setWorkorder(TicketContext workorder) {
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


    private InventoryRequestLineItemContext requestedLineItem;
    public InventoryRequestLineItemContext getRequestedLineItem() {
        return requestedLineItem;
    }
    public void setRequestedLineItem(InventoryRequestLineItemContext requestedLineItem) {
        this.requestedLineItem = requestedLineItem;
    }

    private ResourceContext resource;

    public ResourceContext getResource() {
        return resource;
    }

    public void setResource(ResourceContext resource) {
        this.resource = resource;
    }

    private double transactionCost;

    public double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(double transactionCost) {
        this.transactionCost = transactionCost;
    }

    private StoreRoomContext storeRoom;

    public StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

}