package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.context.invoice.InvoiceSettingContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class FetchInvoiceSetting extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        InvoiceSettingContext invoiceSettingDATA = InvoiceAPI.getInvoiceSetting();
        if (invoiceSettingDATA != null) {
            invoiceSettingDATA.setShowMarkupValue(InvoiceAPI.showMarkupValue(context, null));
            context.put(FacilioConstants.ContextNames.INVOICE_SETTING, invoiceSettingDATA);
        }

        return false;
    }
}