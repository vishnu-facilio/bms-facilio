package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;


public class POBeforeCreateOrEditV3Command extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseOrderContext> purchaseOrderContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(purchaseOrderContexts)) {
            for (V3PurchaseOrderContext purchaseOrderContext : purchaseOrderContexts) {
                if (purchaseOrderContext != null) {
                    if (purchaseOrderContext.getVendor() == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Vendor cannot be empty");
                    }
                    if (purchaseOrderContext.getId() <= 0 && CollectionUtils.isEmpty(purchaseOrderContext.getLineItems())) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                    }

                    checkForStoreRoom(purchaseOrderContext, purchaseOrderContext.getLineItems());
                    QuotationAPI.lineItemsCostCalculations(purchaseOrderContext, purchaseOrderContext.getLineItems());

                    // setting current user to requestedBy
                    if (purchaseOrderContext.getRequestedBy() == null) {
                        purchaseOrderContext.setRequestedBy(AccountUtil.getCurrentUser());
                    }

                    purchaseOrderContext.setShipToAddress(LocationAPI.getPoPrLocation(purchaseOrderContext.getStoreRoom(), purchaseOrderContext.getShipToAddress(), "SHIP_TO_Location", true, true));
                    purchaseOrderContext.setBillToAddress(LocationAPI.getPoPrLocation(purchaseOrderContext.getVendor(), purchaseOrderContext.getBillToAddress(), "BILL_TO_Location", false, true));


                    if (purchaseOrderContext.getOrderedTime() == null) {
                        purchaseOrderContext.setOrderedTime(System.currentTimeMillis());
                    }
                    if(purchaseOrderContext.getQuantityReceived() == null || purchaseOrderContext.getQuantityReceived() <= 0){
                        purchaseOrderContext.setReceivableStatus(V3PurchaseOrderContext.ReceivableStatus.YET_TO_RECEIVE.getIndex());
                    }

                    if(purchaseOrderContext.getVendorQuote()!=null){
                        V3VendorQuotesContext vendorQuote = V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_QUOTES,purchaseOrderContext.getVendorQuote().getId());
                        if(vendorQuote.getRequestForQuotation()!=null){
                            purchaseOrderContext.setRequestForQuotation(vendorQuote.getRequestForQuotation());
                        }
                    }


                }
            }
        }
        return false;
    }

    private void checkForStoreRoom(V3PurchaseOrderContext po, List<V3PurchaseOrderLineItemContext> lineItems) throws RESTException {
        if (CollectionUtils.isNotEmpty(lineItems)) {
            for (V3PurchaseOrderLineItemContext lineItem : lineItems) {
                if ((lineItem.getInventoryType() == InventoryType.ITEM.getValue() || lineItem.getInventoryType() == InventoryType.TOOL.getValue()) && po.getStoreRoom() == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be null for pos with items and tools");
                }
            }
        }
    }

}
