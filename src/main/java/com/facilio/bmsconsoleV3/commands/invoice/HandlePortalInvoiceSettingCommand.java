package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsoleV3.commands.quotation.QuotationFillDetailsCommand;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceSettingContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationSettingContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class HandlePortalInvoiceSettingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(HandlePortalInvoiceSettingCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {


        ApplicationContext app = AccountUtil.getCurrentApp();

        InvoiceSettingContext invoiceSetting = InvoiceAPI.getInvoiceSetting();

        if(invoiceSetting != null) {
            context.put(FacilioConstants.ContextNames.INVOICE_SETTING, invoiceSetting);

        }

        if(app != null && app.getLinkName() != null && ((app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) || (app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)))) {

            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<InvoiceContextV3> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {
                for (InvoiceContextV3 invoice : list) {
                    if(invoiceSetting != null) {
                        invoice.setShowMarkupValue(InvoiceAPI.showMarkupValue(context, null));
                    }
                }
            }

        }

        return false;
    }
}

