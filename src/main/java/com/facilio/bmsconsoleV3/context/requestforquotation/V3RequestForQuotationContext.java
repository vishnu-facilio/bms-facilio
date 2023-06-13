package com.facilio.bmsconsoleV3.context.requestforquotation;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class V3RequestForQuotationContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private V3StoreRoomContext storeRoom;
    private Long requestedDate;
    private Long requiredDate;
    private V3PeopleContext requestedBy;
    private LocationContext billToAddress;
    private LocationContext shipToAddress;
    private List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems;
    private List<V3RequestForQuotationVendorsContext> vendor;
    private Boolean isQuoteReceived;
    private Boolean isAwarded;
    private Boolean isPoCreated;
    private List<Long> recordIds;
    private Long expectedReplyDate;
    private Boolean isRfqFinalized;
    private Boolean isDiscarded;

    public Long getExpectedReplyDate() {
        return expectedReplyDate;
    }

    public void setExpectedReplyDate(Long expectedReplyDate) {
        this.expectedReplyDate = expectedReplyDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public V3StoreRoomContext getStoreRoom() {
        return storeRoom;
    }

    public void setStoreRoom(V3StoreRoomContext storeRoom) {
        this.storeRoom = storeRoom;
    }

    public Long getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Long requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Long getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Long requiredDate) {
        this.requiredDate = requiredDate;
    }

    public V3PeopleContext getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(V3PeopleContext requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocationContext getBillToAddress() {
        return billToAddress;
    }

    public void setBillToAddress(LocationContext billToAddress) {
        this.billToAddress = billToAddress;
    }

    public LocationContext getShipToAddress() {
        return shipToAddress;
    }

    public void setShipToAddress(LocationContext shipToAddress) {
        this.shipToAddress = shipToAddress;
    }

    public List<V3RequestForQuotationLineItemsContext> getRequestForQuotationLineItems() {
        return requestForQuotationLineItems;
    }

    public void setRequestForQuotationLineItems(List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems) {
        this.requestForQuotationLineItems = requestForQuotationLineItems;
    }

    public List<V3RequestForQuotationVendorsContext> getVendor() {
        return vendor;
    }

    public void setVendor(List<V3RequestForQuotationVendorsContext> vendor) {
        this.vendor = vendor;
    }

    public Boolean getIsQuoteReceived() {
        if(isQuoteReceived!=null){
            return isQuoteReceived.booleanValue();
        }
        return false;
    }

    public void setIsQuoteReceived(Boolean quoteReceived) {
        isQuoteReceived = quoteReceived;
    }

    public Boolean getIsAwarded() {
        if(isAwarded!=null){
            return isAwarded.booleanValue();
        }
        return false;
    }

    public void setIsAwarded(Boolean awarded) {
        isAwarded = awarded;
    }

    public Boolean getIsPoCreated() {
        if (isPoCreated != null) {
            return isPoCreated.booleanValue();
        }
        return false;
    }

    public void setIsPoCreated(Boolean poCreated) {
        isPoCreated = poCreated;
    }

    public Boolean getIsRfqFinalized() {
        if (isRfqFinalized != null) {
            return isRfqFinalized.booleanValue();
        }
        return false;
    }
    public void setIsRfqFinalized(Boolean rfqFinalized) { isRfqFinalized = rfqFinalized; }

    public Boolean getIsDiscarded() {
        if(isDiscarded != null){
            return isDiscarded.booleanValue();
        }
        return false;
    }
    public void setIsDiscarded(Boolean discarded) { isDiscarded = discarded; }

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }
    public enum RfqTypes implements FacilioStringEnum {
        OPEN_BID("Open Bid"),
        CLOSED_BID("Closed Bid");
        private final String displayName;
        RfqTypes(String displayName){
            this.displayName = displayName;
        }
        @Override
        public String getValue() {
            return displayName;
        }
    }
    @Getter @Setter
    private V3RequestForQuotationContext.RfqTypes rfqType;
}
