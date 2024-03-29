package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RfqBeforeCreateOrUpdateCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        List<V3RequestForQuotationContext> requestForQuotationContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(requestForQuotationContexts)) {
            for (V3RequestForQuotationContext requestForQuotationContext : requestForQuotationContexts) {
                if (requestForQuotationContext != null) {
                    //if vendors are selected edit should be restricted
                    if(requestForQuotationContext.getIsRfqFinalized() && requestForQuotationContext.getVendor().size()>0 && MapUtils.isEmpty(bodyParams) && requestForQuotationContext.getStateFlowId()<0 && requestForQuotationContext.getId()>0 && !requestForQuotationContext.getIsDiscarded()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "RFQ cannot be updated after Finalizing Rfq");
                    }
                    if (requestForQuotationContext.getId() <= 0 && CollectionUtils.isEmpty(requestForQuotationContext.getRequestForQuotationLineItems())) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                    }

                    //storeroom validation
                    // checkForStoreRoom(requestForQuotationContext, requestForQuotationContext.getRequestForQuotationLineItems());

                   // to set addresses
                    requestForQuotationContext.setShipToAddress(LocationAPI.getPoPrLocation(requestForQuotationContext.getStoreRoom(), requestForQuotationContext.getShipToAddress(), "SHIP_TO_Location", true, true));
                    //requestForQuotationContext.setBillToAddress(LocationAPI.getPoPrLocation(requestForQuotationContext.getVendor(), requestForQuotationContext.getBillToAddress(), "BILL_TO_Location", false, true));

                    // to set requestedDate
                    if (requestForQuotationContext.getRequestedDate() == null) {
                        requestForQuotationContext.setRequestedDate(System.currentTimeMillis());
                    }

                }
            }
        }
        return false;
    }

    private void checkForStoreRoom(V3RequestForQuotationContext rfq, List<V3RequestForQuotationLineItemsContext> lineItems) throws RESTException {
        if (CollectionUtils.isNotEmpty(lineItems)) {
            for (V3RequestForQuotationLineItemsContext lineItem : lineItems) {
                if ((lineItem.getInventoryType() == InventoryType.ITEM.getValue() || lineItem.getInventoryType() == InventoryType.TOOL.getValue()) && rfq.getStoreRoom() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be null for pos with items and tools");
                }
            }
        }
    }
}