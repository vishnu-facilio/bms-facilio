package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class RequestForQuotationAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private List<Long> recordIds;
    public List<Long> getRecordIds() {
        return recordIds;
    }
    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }

    private V3RequestForQuotationContext requestForQuotation;
    private Long vendorId;
    private Long vendorQuote;

    public Long getVendorQuote() {
        return vendorQuote;
    }

    public void setVendorQuote(Long vendorQuote) {
        this.vendorQuote = vendorQuote;
    }

    public V3RequestForQuotationContext getRequestForQuotation() {
        return requestForQuotation;
    }

    public void setRequestForQuotation(V3RequestForQuotationContext requestForQuotation) {
        this.requestForQuotation = requestForQuotation;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String convertPrToRfq() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getConvertPrToRfqChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION)));

        return V3Action.SUCCESS;
    }
    public String convertRfqToPo() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getConvertRfqToPoChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, requestForQuotation);
        context.put(FacilioConstants.ContextNames.VENDOR_ID, vendorId);
        chain.execute();
        setData(FacilioConstants.ContextNames.PURCHASE_ORDER,  FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.PURCHASE_ORDER)));
        return V3Action.SUCCESS;
    }
    public String convertVendorQuoteToPo() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getConvertVendorQuoteToPoChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.VENDOR_QUOTE_ID, vendorQuote);
        chain.execute();
        setData(FacilioConstants.ContextNames.PURCHASE_ORDER, FieldUtil.getAsJSON(context.get(FacilioConstants.ContextNames.PURCHASE_ORDER)));
        return V3Action.SUCCESS;
    }


}
