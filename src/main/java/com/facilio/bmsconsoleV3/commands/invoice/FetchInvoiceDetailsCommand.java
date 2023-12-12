package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;

import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchInvoiceDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> invoiceIdList = new ArrayList<>();
        if(context.containsKey(FacilioConstants.INVOICE.INVOICE_ID)) {
            Long invoice = (Long) context.get(FacilioConstants.INVOICE.INVOICE_ID);
            if (invoice == null) {
                throw new IllegalArgumentException(invoice+"Invoice Can't Be Empty");
            }
            invoiceIdList.add(invoice);
        }
        else if(context.containsKey("invoiceIds")){
            List<Long> invoiceIds = (List<Long>) context.get("invoiceIds");
            if(invoiceIds == null || invoiceIds.isEmpty()){
                throw new IllegalArgumentException("Must contain atLeast One Invoice");
            }
            invoiceIdList.addAll(invoiceIds);
        }
        List<InvoiceContextV3> invoiceContextList = new ArrayList<>();
        for(long invoiceId : invoiceIdList){
            InvoiceContextV3 invoiceContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVOICE,invoiceId, InvoiceContextV3.class);
            List<InvoiceLineItemsContext> lineItemContextList = InvoiceAPI.getInvoiceLineItems(invoiceId);
            invoiceContext.setLineItems(lineItemContextList);
            invoiceContextList.add(invoiceContext);
        }
        Map<String,List> recordMap = new HashMap<>();
        recordMap.put(FacilioConstants.INVOICE.INVOICE_LIST,invoiceContextList);
        recordMap.put("oldInvoiceList",invoiceContextList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}
