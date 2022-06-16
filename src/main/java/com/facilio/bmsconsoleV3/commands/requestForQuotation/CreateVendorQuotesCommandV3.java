package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationVendorsContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
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
        //deleting already created quotes
        deleteLineItemsCreated(requestForQuotationLineItems);
        deleteVendorQuotesCreated(requestForQuotation);

        for (V3RequestForQuotationVendorsContext vendor : vendors) {
                V3VendorContext vendorContext = new V3VendorContext();
                vendorContext.setId(vendor.getId());

                List<V3VendorQuotesLineItemsContext> vendorQuotesLineItems = setVendorQuoteLineItems(requestForQuotationLineItems);
                V3VendorQuotesContext vendorQuote = setVendorQuote(vendorContext,requestForQuotation,vendorQuotesLineItems);

                V3Util.createRecord(modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES), FieldUtil.getAsJSON(vendorQuote));

        }
        return false;
    }
    private void deleteVendorQuotesCreated(V3RequestForQuotationContext requestForQuotation) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.VENDOR_QUOTES;
        FacilioModule module = modBean.getModule(moduleName);

        DeleteRecordBuilder<V3VendorQuotesContext> deleteBuilder = new DeleteRecordBuilder<V3VendorQuotesContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("RFQ_ID", "requestForQuotation", String.valueOf(requestForQuotation.getId()), NumberOperators.EQUALS));
        deleteBuilder.delete();
    }
    private void deleteLineItemsCreated(List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.VENDOR_QUOTES_LINE_ITEMS;
        FacilioModule module = modBean.getModule(moduleName);
        for(V3RequestForQuotationLineItemsContext lineItem : requestForQuotationLineItems){
            DeleteRecordBuilder<V3VendorQuotesLineItemsContext> deleteBuilder = new DeleteRecordBuilder<V3VendorQuotesLineItemsContext>()
                    .module(module)
                    .andCondition(CriteriaAPI.getCondition("RFQ_LINE_ITEM_ID", "requestForQuotationLineItem", String.valueOf(lineItem.getId()), NumberOperators.EQUALS));
            deleteBuilder.delete();
        }

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
