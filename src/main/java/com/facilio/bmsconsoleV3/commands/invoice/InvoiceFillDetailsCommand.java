package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;

import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class InvoiceFillDetailsCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(InvoiceFillDetailsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        InvoiceContextV3 invoice = (InvoiceContextV3) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.INVOICE, id);
        if (invoice == null) {
            LOGGER.error("Invoice is empty");
            return true;
        }
        InvoiceAPI.setLineItems(invoice);
        InvoiceAPI.setInvoiceAssociatedTerms(invoice);
        InvoiceAPI.setTaxSplitUpforInvoice(invoice, invoice.getLineItems(), context);

        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchContacts"))  {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
                if(invoice.getInvoiceType() != null) {
                    if (invoice.getInvoiceTypeEnum() == InvoiceContextV3.InvoiceType.TENANT) {
                        if (InvoiceAPI.lookupValueIsNotEmpty(invoice.getTenant())) {
                            List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(invoice.getTenant().getId(), false, false);
                            if (CollectionUtils.isNotEmpty(tenantContacts)) {
                                invoice.getTenant().setPeopleTenantContacts(tenantContacts);
                            }
                        }
                    } else if (invoice.getInvoiceTypeEnum() == InvoiceContextV3.InvoiceType.CLIENT) {
                        if (InvoiceAPI.lookupValueIsNotEmpty(invoice.getClient())) {
                            List<V3ClientContactContext> clientContacts = V3PeopleAPI.getClientContacts(invoice.getClient().getId(), false);
                            if (CollectionUtils.isNotEmpty(clientContacts)) {
                                invoice.getClient().setPeopleClientContacts(clientContacts);
                            }
                        }
                    }
                    else if (invoice.getInvoiceTypeEnum() == InvoiceContextV3.InvoiceType.VENDOR) {
                        if (InvoiceAPI.lookupValueIsNotEmpty(invoice.getVendor())) {
                            List<V3VendorContactContext> vendorContacts = V3PeopleAPI.getVendorContacts(invoice.getVendor().getId(), false);
                            if (CollectionUtils.isNotEmpty(vendorContacts)) {
                                //
                            }
                        }
                    }
                }
            }
        }


        return false;
    }
}
