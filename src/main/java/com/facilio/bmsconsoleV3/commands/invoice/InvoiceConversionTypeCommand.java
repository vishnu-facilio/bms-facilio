package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
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

public class InvoiceConversionTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        Integer invoiceType = (Integer) context.get(FacilioConstants.ContextNames.TYPE);
        List<InvoiceContextV3> invoices = (List<InvoiceContextV3>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(FacilioConstants.INVOICE.INVOICE_LIST));
        List<ModuleBaseWithCustomFields> invoiceContextList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(invoices)){
            for(InvoiceContextV3 invoice : invoices){
                if(invoiceType == InvoiceContextV3.InvoiceType.CLIENT.getIndex()){
                    invoice.setInvoiceType(InvoiceContextV3.InvoiceType.CLIENT.getIndex());
                }
                else if(invoiceType == InvoiceContextV3.InvoiceType.TENANT.getIndex())
                {
                    invoice.setInvoiceType(InvoiceContextV3.InvoiceType.TENANT.getIndex());
                }
                invoice.setVendor(null);
                invoiceContextList.add(invoice);

            }
            context.put("newInvoices",invoices);
        }

        return false;
    }
}
