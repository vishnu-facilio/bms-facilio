package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateInvoiceAfterCloningCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        List<InvoiceContextV3> invoices = (List<InvoiceContextV3>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.INVOICE.INVOICE_LIST));
        List<ModuleBaseWithCustomFields> invoiceContextList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(invoices)){
            for(InvoiceContextV3 invoice : invoices){
                if(invoice == null){
                    continue;
                }
                invoiceContextList.add(invoice);

            }
            FacilioContext facilioContext = V3Util.createRecord(invoiceModule,invoiceContextList);
            List<InvoiceContextV3> newInvoices = (List<InvoiceContextV3>) (((Map<String,Object>)facilioContext.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.ContextNames.INVOICE));
            context.put("newInvoices",newInvoices);
        }
        return false;
    }
}
