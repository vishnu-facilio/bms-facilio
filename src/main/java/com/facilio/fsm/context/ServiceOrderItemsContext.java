package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.v3.context.V3Context;

public class ServiceOrderItemsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3ItemContext item;
    private V3ItemTypesContext itemType;
    private V3StoreRoomContext storeRoom;
    private Double quantity;
    private V3AssetContext rotatingAsset;
    private Double totalCost;
    private Double unitPrice;
    private ServiceOrderContext serviceOrder;
    private ServiceTaskContext serviceTask;

    public V3ItemContext getItem() {
        return item;
    }

    public void setItem(V3ItemContext item) {
        this.item = item;
    }

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public V3AssetContext getRotatingAsset() {
        return rotatingAsset;
    }

    public void setRotatingAsset(V3AssetContext rotatingAsset) {
        this.rotatingAsset = rotatingAsset;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public ServiceOrderContext getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrderContext serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public ServiceTaskContext getServiceTask() {
        return serviceTask;
    }

    public void setServiceTask(ServiceTaskContext serviceTask) {
        this.serviceTask = serviceTask;
    }
}
