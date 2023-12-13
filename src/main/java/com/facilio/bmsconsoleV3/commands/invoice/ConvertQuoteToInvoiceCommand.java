package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationLineItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertQuoteToInvoiceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Integer quoteId = (Integer) context.get(FacilioConstants.ContextNames.RECORD_ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


        if(quoteId !=null) {
            FacilioModule quoteItemModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE_LINE_ITEMS);
            SelectRecordsBuilder<QuotationLineItemsContext> b = new SelectRecordsBuilder<QuotationLineItemsContext>()
                    .module(quoteItemModule)
                    .select(modBean.getAllFields(quoteItemModule.getName()))
                    .beanClass(QuotationLineItemsContext.class)
                    .andCondition(CriteriaAPI.getCondition("QUOTATION_ID", "quote", String.valueOf(quoteId), NumberOperators.EQUALS));
            List<QuotationLineItemsContext> lineItems = b.get();
            List<InvoiceLineItemsContext> invoiceLineItemsContexts = new ArrayList<>();

            for (QuotationLineItemsContext lineItem : lineItems) {
                Map<String, Object> map = FieldUtil.getAsProperties(lineItem);
//                map.put("type",map.get("inventoryType"));
                InvoiceLineItemsContext lineItemsContext = FieldUtil.getAsBeanFromMap(map, InvoiceLineItemsContext.class);
                invoiceLineItemsContexts.add(lineItemsContext);
            }
            InvoiceContextV3 invoiceContextV3 = new InvoiceContextV3();
            invoiceContextV3.setLineItems(invoiceLineItemsContexts);

            QuotationContext quoteContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.QUOTE,quoteId,QuotationContext.class);
            invoiceContextV3.setQuote(quoteContext);

            if(quoteContext.getCustomerType() == null)
            {
                throw new IllegalArgumentException("Quote can not be converted into Invoice with out Customer Type");
            }

            if(quoteContext.getCustomerType().equals(QuotationContext.CustomerType.VENDOR.getIndex()))
            {
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.VENDOR.getIndex());
                invoiceContextV3.setVendor(quoteContext.getVendor());
            }
            else if(quoteContext.getCustomerType().equals(QuotationContext.CustomerType.TENANT.getIndex()))
            {
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.TENANT.getIndex());
                invoiceContextV3.setTenant(quoteContext.getTenant());
            }
            else if(quoteContext.getCustomerType().equals(QuotationContext.CustomerType.CLIENT.getIndex()))
            {
                invoiceContextV3.setInvoiceType(InvoiceContextV3.InvoiceType.CLIENT.getIndex());
                invoiceContextV3.setClient(quoteContext.getClient());
            }



            context.put(FacilioConstants.ContextNames.RECORD, invoiceContextV3);
        }

        return false;
    }

}
