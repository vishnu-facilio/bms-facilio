package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class V3InventoryRequestLineItemContext extends BaseLineItemContext {

    private static final long serialVersionUID = 1L;

    private V3InventoryRequestContext inventoryRequestId;

    public V3InventoryRequestContext getInventoryRequestId() {
        return inventoryRequestId;
    }

    public void setInventoryRequestId(V3InventoryRequestContext inventoryRequestId) {
        this.inventoryRequestId = inventoryRequestId;
    }

    private InventoryType inventoryType;

    public InventoryType getInventoryTypeEnum() {
        return inventoryType;
    }

    public int getInventoryType() {
        if (inventoryType != null) {
            return inventoryType.getValue();
        }
        return -1;
    }

    public void setInventoryType(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
    }

    public void setInventoryType(int inventoryType) {
        this.inventoryType = InventoryType.valueOf(inventoryType);
    }

    private V3ItemTypesContext itemType;

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    private V3ToolTypesContext toolType;

    public V3ToolTypesContext getToolType() {
        return toolType;
    }

    public void setToolType(V3ToolTypesContext toolType) {
        this.toolType = toolType;
    }

    private Double quantity = null;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }


    private Long parentId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private List<Long> assetIds;

    public List<Long> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(List<Long> assetIds) {
        this.assetIds = assetIds;
    }

    private AssetContext asset;

    public AssetContext getAsset() {
        return asset;
    }

    public void setAsset(AssetContext asset) {
        this.asset = asset;
    }

    private Status status;

    public Status getStatusEnum() {
        return status;
    }

    public Integer getStatus() {
        if (status != null) {
            return status.getValue();
        }
        return null;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = Status.valueOf(status);
    }

    public static enum Status {
        ISSUED(),
        YET_TO_BE_ISSUED(),
        RETURNED();

        public Integer getValue() {
            return ordinal() + 1;
        }

        public static Status valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Double usedQuantity;

    public Double getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(Double usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    private Double issuedQuantity;

    public Double getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(Double issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public WorkorderItemContext constructWorkOrderItemContext() throws Exception {
        WorkorderItemContext woItem = new WorkorderItemContext();
        if (this.getStoreRoom() == null) {
            throw new IllegalArgumentException("No appropriate Item found");
        }
        ItemContext item = ItemsApi.getItemsForTypeAndStore(this.getStoreRoom().getId(), this.getItemType().getId());
        woItem.setItem(item);
        woItem.setParentId(this.getParentId());
        if (this.getAsset() != null && this.getAsset().getId() > 0) {
            woItem.setAssetIds(Collections.singletonList(this.getAsset().getId()));
        }
        woItem.setQuantity(this.getQuantity());
        // woItem.setRequestedLineItem(this);
        return woItem;
    }

    public WorkorderToolsContext constructWorkOrderToolContext() throws Exception {
        WorkorderToolsContext woTool = new WorkorderToolsContext();
        if (this.getStoreRoom() == null) {
            throw new IllegalArgumentException("No appropriate Tool found");
        }
        ToolContext tool = ToolsApi.getToolsForTypeAndStore(this.getStoreRoom().getId(), this.getToolType().getId());
        woTool.setTool(tool);
        woTool.setParentId(this.getParentId());
        if (this.getAsset() != null && this.getAsset().getId() > 0) {
            woTool.setAssetIds(Collections.singletonList(this.getAsset().getId()));
        }
        woTool.setQuantity(this.getQuantity());
        woTool.setDuration(3600000);
        //woTool.setRequestedLineItem(this);

        return woTool;
    }

    public V3ToolTransactionContext contructManualToolTransactionContext(User requestedFor) throws Exception {
        V3ToolTransactionContext transaction = new V3ToolTransactionContext();
        if (this.getStoreRoom() == null) {
            throw new IllegalArgumentException("No appropriate Tool found");
        }
        V3ToolContext tool = V3ToolsApi.getToolsForTypeAndStore(this.getStoreRoom().getId(), this.getToolType().getId());
        transaction.setTool(tool);
        transaction.setIssuedTo(requestedFor);
        transaction.setParentId(requestedFor.getOuid());
        transaction.setRequestedLineItem(this);
        transaction.setTransactionType(3);
        transaction.setTransactionState(2);
        transaction.setQuantity(this.getQuantity());

        if (this.getAsset() != null && this.getAsset().getId() > 0) {
            transaction.setAssetIds(Collections.singletonList(this.getAsset().getId()));
        }
        return transaction;

    }

        public V3ItemTransactionsContext contructManualItemTransactionContext(User requestedFor) throws Exception {
            V3ItemTransactionsContext transaction = new V3ItemTransactionsContext();
        if(this.getStoreRoom() == null) {
            throw new IllegalArgumentException("No appropriate Item found");
        }
        V3ItemContext item = V3ItemsApi.getItemsForTypeAndStore(this.getStoreRoom().getId(), this.getItemType().getId());
        transaction.setItem(item);
        transaction.setIssuedTo(requestedFor);
        transaction.setParentId(requestedFor.getOuid());
        transaction.setRequestedLineItem(this);
        transaction.setTransactionType(3);
        transaction.setTransactionState(2);
        transaction.setQuantity(this.getQuantity());
        if(this.getAsset() != null && this.getAsset().getId() > 0) {
            transaction.setAssetIds(Collections.singletonList(this.getAsset().getId()));
        }
        return transaction;

    }

    private V3StoreRoomContext storeRoom;

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    private ReservationType reservationType;

    public Integer getReservationType() {
        if (reservationType != null) {
            return reservationType.getIndex();
        }
        return null;
    }

    public void setReservationType(Integer reservationType) {
        if (reservationType != null) {
            this.reservationType = ReservationType.valueOf(reservationType);
        }
    }
    public ReservationType getReservationTypeEnum() {
        return reservationType;
    }
    private Boolean isReserved;

    public Boolean getIsReserved() {
        if(isReserved != null){
            return isReserved;
        }
        return false;
    }

    public void setIsReserved(Boolean reserved) {
        isReserved = reserved;
    }
}
