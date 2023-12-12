package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AssociateInvoiceTermsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        List<InvoiceAssociatedTermsContext> terms = (List<InvoiceAssociatedTermsContext>)context.get(FacilioConstants.ContextNames.INVOICE_ASSOCIATED_TERMS);

        if (CollectionUtils.isNotEmpty(terms) && recordId !=null) {
            InvoiceContextV3 invoice = new InvoiceContextV3();
            invoice.setId(recordId);
            for (InvoiceAssociatedTermsContext invoiceTerm: terms) {
                invoiceTerm.setInvoice(invoice);
            }
            InvoiceAPI.updateTermsAssociatedV3(terms);
        }


        return false;

    }

}
