package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VendorQuoteBeforeUpdateCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        List<V3VendorQuotesContext> vendorQuotes = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(vendorQuotes)){
            for(V3VendorQuotesContext vendorQuote : vendorQuotes){
                if(vendorQuote.getRequestForQuotation() != null) {
                    Long rfqId = vendorQuote.getRequestForQuotation().getId();
                    V3RequestForQuotationContext requestForQuotation = V3RecordAPI.getRecord(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, rfqId, V3RequestForQuotationContext.class);
                    checkIsRfqDiscarded(requestForQuotation);
//                    if(!vendorQuote.getIsFinalized()) {
//                        checkIsRfqSubmissionClosed(requestForQuotation);
//                        checkIsRfqAwarded(requestForQuotation);
//                    }
                    if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("negotiation") && (boolean) bodyParams.get("negotiation")){
                        checkIsRfqSubmissionClosed(requestForQuotation);
                        checkIsRfqAwarded(requestForQuotation);
                        if(requestForQuotation.getIsAwarded()) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quote has been Awarded");
                        }
                        vendorQuote.setNegotiation(true);
                        vendorQuote.setIsFinalized(false);
                    }
                    if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("finalizeVendorQuote") && (boolean) bodyParams.get("finalizeVendorQuote")){
                        checkIsRfqSubmissionClosed(requestForQuotation);
                        checkIsRfqAwarded(requestForQuotation);
                        if(vendorQuote.getVendor()!=null && V3InventoryUtil.hasVendorPortalAccess(vendorQuote.getVendor().getId()) && !AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "You do not have permission to finalize this vendor quote");
                        }
                        vendorQuote.setIsFinalized(true);
                        vendorQuote.setNegotiation(false);
                    }
                    if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("isPoCreated") && (boolean) bodyParams.get("isPoCreated") && bodyParams.get("purchaseOrderId")!=null){
                        V3PurchaseOrderContext purchaseOrder = new V3PurchaseOrderContext();
                        purchaseOrder.setId((Long) bodyParams.get("purchaseOrderId"));
                        vendorQuote.setPurchaseOrder(purchaseOrder);
                    }
                }
                else {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quote Not Available");
                }
            }
        }
        recordMap.put(moduleName, vendorQuotes);
        return false;
    }

    private void checkIsRfqSubmissionClosed(V3RequestForQuotationContext requestForQuotation) throws RESTException {
        if(requestForQuotation.getIsQuoteReceived()){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quote Submission has been Closed");
        }
    }

    private void checkIsRfqAwarded(V3RequestForQuotationContext requestForQuotation) throws RESTException {
        if(requestForQuotation.getIsAwarded()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quote has been Awarded");
        }
    }

    private void checkIsRfqDiscarded(V3RequestForQuotationContext requestForQuotation) throws RESTException {
        if(requestForQuotation.getIsDiscarded()){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quote has been Discarded");
        }
    }
}
