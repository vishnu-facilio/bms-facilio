package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConvertVendorQuoteToPoCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long vendorQuoteId = (Long) context.get(FacilioConstants.ContextNames.VENDOR_QUOTE_ID);

       FacilioContext vendorQuoteSummary = V3Util.getSummary(FacilioConstants.ContextNames.VENDOR_QUOTES, Collections.singletonList(vendorQuoteId));
        V3VendorQuotesContext vendorQuote =(V3VendorQuotesContext) ((Map<String,List>)vendorQuoteSummary.get("recordMap")).get("vendorQuotes").get(0);
        V3PurchaseOrderContext purchaseOrder = getPurchaseOrder(vendorQuote);
       List<V3PurchaseOrderLineItemContext> purchaseOrderLineItems = getPurchaseOrderLineItems(vendorQuote.getVendorQuotesLineItems());
       if(CollectionUtils.isNotEmpty(purchaseOrderLineItems)){
           purchaseOrder.setLineItems(purchaseOrderLineItems);
       }
        context.put(FacilioConstants.ContextNames.PURCHASE_ORDER, purchaseOrder);
        return false;
    }
    private List<V3PurchaseOrderLineItemContext> getPurchaseOrderLineItems(List<V3VendorQuotesLineItemsContext> vendorQuoteLineItems)throws Exception{
        List<V3PurchaseOrderLineItemContext> purchaseOrderLineItems = new ArrayList<>();
        for(V3VendorQuotesLineItemsContext vendorQuotesLineItem : vendorQuoteLineItems){
            if(vendorQuotesLineItem.getIsLineItemAwarded()!=null && vendorQuotesLineItem.getIsLineItemAwarded()){
                V3PurchaseOrderLineItemContext purchaseOrderLineItem = new V3PurchaseOrderLineItemContext();
                if(vendorQuotesLineItem.getQuantity()!=null){
                    purchaseOrderLineItem.setQuantity(vendorQuotesLineItem.getQuantity());
                }
                if(vendorQuotesLineItem.getCounterPrice()!=null){
                    purchaseOrderLineItem.setUnitPrice(vendorQuotesLineItem.getCounterPrice());
                }
                if(vendorQuotesLineItem.getInventoryType()!=null){
                    purchaseOrderLineItem.setInventoryType(vendorQuotesLineItem.getInventoryType());
                }
                if(vendorQuotesLineItem.getRequestForQuotationLineItem().getUnitOfMeasure()!=null){
                    purchaseOrderLineItem.setUnitOfMeasure(vendorQuotesLineItem.getRequestForQuotationLineItem().getUnitOfMeasure());
                }
                if(vendorQuotesLineItem.getDescription()!=null){
                    purchaseOrderLineItem.setDescription(vendorQuotesLineItem.getDescription());
                }
                if(vendorQuotesLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM.getIndex())){
                    purchaseOrderLineItem.setItemType(vendorQuotesLineItem.getItemType());
                }
                else if(vendorQuotesLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL.getIndex())){
                    purchaseOrderLineItem.setToolType(vendorQuotesLineItem.getToolType());
                }
                else if(vendorQuotesLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex())){
                    purchaseOrderLineItem.setService(vendorQuotesLineItem.getService());
                }
                purchaseOrderLineItems.add(purchaseOrderLineItem);
            }
        }
        return purchaseOrderLineItems;
    }
    private V3PurchaseOrderContext getPurchaseOrder(V3VendorQuotesContext vendorQuote){
        V3PurchaseOrderContext purchaseOrder = new V3PurchaseOrderContext();
        if(vendorQuote.getVendor()!=null){
            purchaseOrder.setVendor(vendorQuote.getVendor());
        }
        if(vendorQuote.getRequestForQuotation()!=null){
            if(vendorQuote.getRequestForQuotation().getName()!=null){
                purchaseOrder.setName(vendorQuote.getRequestForQuotation().getName());
            }
            if(vendorQuote.getRequestForQuotation().getDescription()!=null){
                purchaseOrder.setDescription(vendorQuote.getRequestForQuotation().getDescription());
            }
            if(vendorQuote.getRequestForQuotation().getBillToAddress()!=null){
                purchaseOrder.setBillToAddress(vendorQuote.getRequestForQuotation().getBillToAddress());
            }
            if(vendorQuote.getRequestForQuotation().getShipToAddress()!=null){
                purchaseOrder.setShipToAddress(vendorQuote.getRequestForQuotation().getShipToAddress());
            }
            if(vendorQuote.getRequestForQuotation().getStoreRoom()!=null){
                purchaseOrder.setStoreRoom(vendorQuote.getRequestForQuotation().getStoreRoom());
            }
        }

        return purchaseOrder;
    }
}
