package com.facilio.bmsconsoleV3.context.vendorquotes;

import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3VendorQuotesContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3RequestForQuotationContext requestForQuotation;
    private V3VendorContext vendor;
    private List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems;
    private Long replyDate;
    private Boolean isFinalized;

    private Long expectedReplyDate;

    public Boolean getFinalized() {
        return isFinalized;
    }

    public void setFinalized(Boolean isFinalized) {
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
}
