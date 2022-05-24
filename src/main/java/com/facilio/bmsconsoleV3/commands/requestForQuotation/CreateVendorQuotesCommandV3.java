package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationVendorsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class CreateVendorQuotesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3RequestForQuotationContext requestForQuotation = (V3RequestForQuotationContext) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION);
        List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems = (List<V3RequestForQuotationLineItemsContext>) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS);
        List<V3RequestForQuotationVendorsContext> vendors = requestForQuotation.getVendor();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        for (V3RequestForQuotationVendorsContext vendor : vendors) {
            V3VendorContext vendorContext = new V3VendorContext();
            vendorContext.setId(vendor.getId());

            List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems = setVendorQuoteLineItems(requestForQuotationLineItems);
            V3VendorQuotesContext vendorQuote = setVendorQuote(vendorContext,requestForQuotation,vendorQuotesLineItems);

            V3Util.createRecord(modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES), FieldUtil.getAsJSON(vendorQuote));
        }
        return false;
    }

    private V3VendorQuotesContext setVendorQuote(V3VendorContext vendor, V3RequestForQuotationContext requestForQuotation, List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems) {
        V3VendorQuotesContext vendorQuote = new V3VendorQuotesContext();
        vendorQuote.setVendor(vendor);
        vendorQuote.setRequestForQuotation(requestForQuotation);
        vendorQuote.setVendorQuotesLineItems(vendorQuotesLineItems);
        return vendorQuote;
    }

    private List<V3VendorQuotesLineItemsContext> setVendorQuoteLineItems(List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems) {

        List<V3VendorQuotesLineItemsContext> lineItemsToBeCreated = new ArrayList<>();

        for (V3RequestForQuotationLineItemsContext requestForQuotationLineItem : requestForQuotationLineItems) {
            V3VendorQuotesLineItemsContext lineItem = new V3VendorQuotesLineItemsContext();
            lineItem.setRequestForQuotationLineItem(requestForQuotationLineItem);
            lineItem.setInventoryType(requestForQuotationLineItem.getInventoryType());
            if (requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.ITEM.getIndex())) {
                lineItem.setItemType(requestForQuotationLineItem.getItemType());
            } else if (requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.TOOL.getIndex())) {
                lineItem.setToolType(requestForQuotationLineItem.getToolType());
            } else if (requestForQuotationLineItem.getInventoryType().equals(V3RequestForQuotationLineItemsContext.InventoryTypeRfq.SERVICE.getIndex())) {
                lineItem.setService(requestForQuotationLineItem.getService());
            }
            if (requestForQuotationLineItem.getDescription() != null) {
                lineItem.setDescription(requestForQuotationLineItem.getDescription());
            }
            lineItem.setUnitPrice(requestForQuotationLineItem.getUnitPrice());
            lineItem.setQuantity(requestForQuotationLineItem.getQuantity());
            lineItemsToBeCreated.add(lineItem);
        }
        return  lineItemsToBeCreated;
    }
}
