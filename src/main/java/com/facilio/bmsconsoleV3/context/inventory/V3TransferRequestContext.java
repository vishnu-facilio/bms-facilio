package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.v3.context.V3Context;
import java.util.List;

public class V3TransferRequestContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String requestSubject;
    private String description;
    private V3StoreRoomContext transferFromStore;
    private V3StoreRoomContext transferToStore;
    private V3PeopleContext transferredBy;
    private List<V3TransferRequestLineItemContext> transferrequestlineitems;
    private Long transferInitiatedOn;
    private Long expectedCompletionDate;
    private Boolean isStaged;
    private Boolean isShipped;
    private Boolean isCompleted;
    private Boolean isShipmentTrackingNeeded;
    private Long shipmentId;

    public String getRequestSubject() {
        return requestSubject;
    }

    public void setRequestSubject(String requestSubject) {
        this.requestSubject = requestSubject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public V3StoreRoomContext getTransferFromStore() {
        return transferFromStore;
    }

    public void setTransferFromStore(V3StoreRoomContext transferFromStore) {
        this.transferFromStore = transferFromStore;
    }

    public V3StoreRoomContext getTransferToStore() {
        return transferToStore;
    }

    public void setTransferToStore(V3StoreRoomContext transferToStore) {
        this.transferToStore = transferToStore;
    }

    public V3PeopleContext getTransferredBy() {
        return transferredBy;
    }

    public void setTransferredBy(V3PeopleContext transferredBy) {
        this.transferredBy = transferredBy;
    }

    public List<V3TransferRequestLineItemContext> getTransferrequestlineitems() {
        return transferrequestlineitems;
    }

    public void setTransferrequestlineitems(List<V3TransferRequestLineItemContext> transferrequestlineitems) {
        this.transferrequestlineitems = transferrequestlineitems;
    }

    public Long getTransferInitiatedOn() {
        return transferInitiatedOn;
    }

    public void setTransferInitiatedOn(Long transferInitiatedOn) {
        this.transferInitiatedOn = transferInitiatedOn;
    }

    public Long getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Long expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public Boolean getIsStaged() {
        if (isStaged != null) {
            return isStaged.booleanValue();
        }
        return false;
    }

    public void setIsStaged(Boolean staged) {
        isStaged = staged;
    }

    public Boolean getIsShipped() {
        if (isShipped != null) {
            return isShipped.booleanValue();
        }
        return false;
    }

    public void setIsShipped(Boolean shipped) {
        isShipped = shipped;
    }

    public Boolean getIsCompleted() {
        if (isCompleted != null) {
            return isCompleted.booleanValue();
        }
        return false;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Boolean getIsShipmentTrackingNeeded() {
        if (isShipmentTrackingNeeded != null) {
            return isShipmentTrackingNeeded.booleanValue();
        }
        return false;
    }

    public void setIsShipmentTrackingNeeded(Boolean shipmentTrackingNeeded) {
        isShipmentTrackingNeeded = shipmentTrackingNeeded;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }
}
