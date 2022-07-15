package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

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
        setData(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
        return V3Action.SUCCESS;
    }
    public String convertRfqToPo() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getConvertRfqToPoChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, requestForQuotation);
        context.put(FacilioConstants.ContextNames.VENDOR_ID, vendorId);
        chain.execute();
        setData(FacilioConstants.ContextNames.PURCHASE_ORDER, context.get(FacilioConstants.ContextNames.PURCHASE_ORDER));
        return V3Action.SUCCESS;
    }


}
