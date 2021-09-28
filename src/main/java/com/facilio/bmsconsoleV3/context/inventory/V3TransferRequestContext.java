package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.v3.context.V3Context;
import java.util.List;

public class V3TransferRequestContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String requestSubject;
    private V3StoreRoomContext transferFromStore;
    private V3StoreRoomContext transferToStore;
    private V3PeopleContext transferredBy;
    private List<V3TransferRequestLineItemContext> lineItems;
    private Long transferInitiatedOn;
    private Long expectedCompletionDate;
    private Boolean isStaged;
    private Boolean isShipped;
    private Boolean isCompleted;

    public String getRequestSubject() {
        return requestSubject;
    }

    public void setRequestSubject(String requestSubject) {
        this.requestSubject = requestSubject;
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

    public List<V3TransferRequestLineItemContext> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<V3TransferRequestLineItemContext> lineItems) {
        this.lineItems = lineItems;
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

    public Boolean getStaged() {
        if (isStaged != null) {
            return isStaged.booleanValue();
        }
        return false;
    }

    public void setStaged(Boolean staged) {
        isStaged = staged;
    }

    public Boolean getShipped() {
        if (isShipped != null) {
            return isShipped.booleanValue();
        }
        return false;
    }

    public void setShipped(Boolean shipped) {
        isShipped = shipped;
    }

    public Boolean getCompleted() {
        if (isCompleted != null) {
            return isCompleted.booleanValue();
        }
        return false;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

}
