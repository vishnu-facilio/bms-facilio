package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.enums.InventoryReservationStatus;
import com.facilio.bmsconsoleV3.enums.ReservationSource;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.v3.context.V3Context;

public class ServiceInventoryReservationContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ReservationSource reservationSource;
    private ReservationType reservationType;
    private InventoryReservationStatus reservationStatus;
    private V3StoreRoomContext storeRoom;
    private ServiceOrderContext serviceOrder;
    private V3InventoryRequestContext inventoryRequest;
    private V3PeopleContext requestedBy;
    private V3ItemTypesContext itemType;
    private Double reservedQuantity;
    private Double issuedQuantity;
    private Double balanceReservedQuantity;
    private ServiceOrderPlannedItemsContext serviceOrderPlannedItem;
    private V3InventoryRequestLineItemContext inventoryRequestLineItem;

    public Integer getReservationSource() {
        if (reservationSource != null) {
            return reservationSource.getIndex();
        }
        return null;
    }
    public void setReservationSource(Integer reservationSource) {
        if (reservationSource != null) {
            this.reservationSource = ReservationSource.valueOf(reservationSource);
        }
    }
    public ReservationSource getReservationSourceEnum() {
        return reservationSource;
    }


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

    public Integer getReservationStatus() {
        if (reservationStatus != null) {
            return reservationStatus.getIndex();
        }
        return null;
    }

    public void setReservationStatus(Integer reservationStatus) {
        if (reservationStatus != null) {
            this.reservationStatus = InventoryReservationStatus.valueOf(reservationStatus);
        }
    }
    public InventoryReservationStatus getReservationStatusEnum() {
        return reservationStatus;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public V3InventoryRequestContext getInventoryRequest() {
        return inventoryRequest;
    }

    public void setInventoryRequest(V3InventoryRequestContext inventoryRequest) {
        this.inventoryRequest = inventoryRequest;
    }

    public V3PeopleContext getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(V3PeopleContext requestedBy) {
        this.requestedBy = requestedBy;
    }

    public V3ItemTypesContext getItemType() {
        return itemType;
    }

    public void setItemType(V3ItemTypesContext itemType) {
        this.itemType = itemType;
    }

    public Double getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Double reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Double getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(Double issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public Double getBalanceReservedQuantity() {
        return balanceReservedQuantity;
    }

    public void setBalanceReservedQuantity(Double balanceReservedQuantity) {
        this.balanceReservedQuantity = balanceReservedQuantity;
    }
    public V3InventoryRequestLineItemContext getInventoryRequestLineItem() {
        return inventoryRequestLineItem;
    }

    public void setInventoryRequestLineItem(V3InventoryRequestLineItemContext inventoryRequestLineItem) {
        this.inventoryRequestLineItem = inventoryRequestLineItem;
    }

    public ServiceOrderContext getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrderContext serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public ServiceOrderPlannedItemsContext getServiceOrderPlannedItem() {
        return serviceOrderPlannedItem;
    }

    public void setServiceOrderPlannedItem(ServiceOrderPlannedItemsContext serviceOrderPlannedItem) {
        this.serviceOrderPlannedItem = serviceOrderPlannedItem;
    }
}
