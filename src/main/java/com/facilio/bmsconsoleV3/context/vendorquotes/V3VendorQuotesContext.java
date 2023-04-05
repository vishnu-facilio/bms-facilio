package com.facilio.bmsconsoleV3.context.vendorquotes;

import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3VendorQuotesContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3RequestForQuotationContext requestForQuotation;
    private V3VendorContext vendor;
    private List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems;
    private Long replyDate;
    private Boolean isFinalized;
    private Boolean isDiscarded;
    private Long expectedReplyDate;
    private Boolean negotiation;
    private V3PurchaseOrderContext purchaseOrder;
    private Boolean vendorPortalAccess;
    private AwardStatus awardStatus;

    public Boolean getIsDiscarded() {
        if(isDiscarded != null){
            return isDiscarded.booleanValue();
        }
        return false;
    }

    public void setIsDiscarded(Boolean isDiscarded) {
        this.isDiscarded = isDiscarded;
    }
    public Boolean getIsFinalized() {
        if(isFinalized != null){
            return isFinalized.booleanValue();
        }
        return false;
    }

    public void setIsFinalized(Boolean isFinalized) {
        this.isFinalized = isFinalized;
    }

    public Long getExpectedReplyDate() {
        return expectedReplyDate;
    }

    public void setExpectedReplyDate(Long expectedReplyDate) {
        this.expectedReplyDate = expectedReplyDate;
    }

    public V3RequestForQuotationContext getRequestForQuotation() {
        return requestForQuotation;
    }

    public void setRequestForQuotation(V3RequestForQuotationContext requestForQuotation) {
        this.requestForQuotation = requestForQuotation;
    }

    public V3VendorContext getVendor() {
        return vendor;
    }

    public void setVendor(V3VendorContext vendor) {
        this.vendor = vendor;
    }

    public List<V3VendorQuotesLineItemsContext> getVendorQuotesLineItems() {
        return vendorQuotesLineItems;
    }

    public void setVendorQuotesLineItems(List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems) {
        this.vendorQuotesLineItems = vendorQuotesLineItems;
    }

    public Long getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Long replyDate) {
        this.replyDate = replyDate;
    }

    public Boolean getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(Boolean negotiation) {
        this.negotiation = negotiation;
    }

    public V3PurchaseOrderContext getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(V3PurchaseOrderContext purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Boolean getVendorPortalAccess() {
        return vendorPortalAccess;
    }

    public void setVendorPortalAccess(Boolean vendorPortalAccess) {
        this.vendorPortalAccess = vendorPortalAccess;
    }
    public Integer getAwardStatus() {
        if (awardStatus != null) {
            return awardStatus.getIndex();
        }
        return null;
    }

    public void setAwardStatus(Integer awardStatus) {
        if (awardStatus != null) {
            this.awardStatus = AwardStatus.valueOf(awardStatus);
        }
    }
    public AwardStatus getAwardStatusEnum() {
        return awardStatus;
    }

    public enum AwardStatus implements FacilioIntEnum {
        NOT_AWARDED("Not Awarded"), AWARDED("Awarded");

        private final String value;

        AwardStatus(String value) {
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

        public static AwardStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
