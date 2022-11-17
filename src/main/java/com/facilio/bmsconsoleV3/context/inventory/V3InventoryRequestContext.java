package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsoleV3.context.BaseLineItemsParentModuleContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.modules.FacilioIntEnum;

import java.util.ArrayList;
import java.util.List;

public class V3InventoryRequestContext extends BaseLineItemsParentModuleContext {

    private static final long serialVersionUID = 1L;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    private List<V3InventoryRequestLineItemContext> inventoryrequestlineitems;

    public List<V3InventoryRequestLineItemContext>getInventoryrequestlineitems() {
        return inventoryrequestlineitems;
    }

    public void setInventoryrequestlineitems(List<V3InventoryRequestLineItemContext> inventoryrequestlineitems) {
        this.inventoryrequestlineitems = inventoryrequestlineitems;
    }

    public static enum Status {
        REQUESTED(),
        APPROVED(),
        REJECTED(),
        PARTIALLY_APPROVED(),
        PARTIALLY_REJECTED(),
        ISSUED();

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

    private Long requestedTime = null;

    public Long getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(Long requestedTime) {
        this.requestedTime = requestedTime;
    }

    private Long requiredTime = null;

    public Long getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(Long requiredTime) {
        this.requiredTime = requiredTime;
    }

    public void addLineItem(V3InventoryRequestLineItemContext lineItems) {
        if (lineItems == null) {
            return;
        }
        if (this.inventoryrequestlineitems == null) {
            this.inventoryrequestlineitems = new ArrayList<V3InventoryRequestLineItemContext>();
        }
        this.inventoryrequestlineitems.add(lineItems);
    }

    private User requestedBy;

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    private User requestedFor;

    public User getRequestedFor() {
        return requestedFor;
    }

    public void setRequestedFor(User requestedFor) {
        this.requestedFor = requestedFor;
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

    private V3StoreRoomContext storeRoom;

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    private Long workOrderLocalId;

    public Long getWorkOrderLocalId() {
        return workOrderLocalId;
    }

    public void setWorkOrderLocalId(Long workOrderLocalId) {
        this.workOrderLocalId = workOrderLocalId;
    }

    private Boolean isIssued;

    public Boolean getIsIssued() {
        return isIssued;
    }

    public void setIsIssued(Boolean isIssued) {
        this.isIssued = isIssued;
    }

    public Boolean isIssued() {
        if (isIssued != null) {
            return isIssued.booleanValue();
        }
        return false;
    }
    private V3WorkOrderContext workorder;
    public V3WorkOrderContext getWorkorder() {
        return workorder;
    }
    public void setWorkorder(V3WorkOrderContext workorder) {
        this.workorder = workorder;
    }
    private TransactionType transactionType;
    public Integer getTransactionType() {
        if (transactionType != null) {
            return transactionType.getIndex();
        }
        return null;
    }
    public void setTransactionType(Integer transactionType) {
        if (transactionType != null) {
            this.transactionType = TransactionType.valueOf(transactionType);
        }
    }
    public TransactionType getTransactionTypeEnum() {
        return transactionType;
    }
    public enum TransactionType implements FacilioIntEnum {
        ISSUE("Issue"), RETURN("Return");
        private final String value;
        TransactionType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static TransactionType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private InventoryRequestReservationStatus inventoryRequestReservationStatus;
    public Integer getInventoryRequestReservationStatus() {
        if (inventoryRequestReservationStatus != null) {
            return inventoryRequestReservationStatus.getIndex();
        }
        return null;
    }
    public void setInventoryRequestReservationStatus(Integer inventoryRequestReservationStatus) {
        if (inventoryRequestReservationStatus != null) {
            this.inventoryRequestReservationStatus = InventoryRequestReservationStatus.valueOf(inventoryRequestReservationStatus);
        }
    }
    public InventoryRequestReservationStatus getInventoryRequestReservationStatusEnum() {
        return inventoryRequestReservationStatus;
    }
    public enum InventoryRequestReservationStatus implements FacilioIntEnum {
        PENDING("Pending"), PARTIALLY_RESERVED("Partially Reserved"), FULLY_RESERVED("Fully Reserved");

        private final String value;

        InventoryRequestReservationStatus(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static InventoryRequestReservationStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
