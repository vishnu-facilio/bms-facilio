package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
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

public class QuotationFillDetailsCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(QuotationFillDetailsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        QuotationContext quotation = (QuotationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.QUOTE, id);
        if (quotation == null) {
            LOGGER.error("Quotation is empty");
        }
        QuotationAPI.setLineItems(quotation);
        QuotationAPI.setQuotationAssociatedTerms(quotation);
        QuotationAPI.setTaxSplitUp(quotation, quotation.getLineItems());

        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchContacts"))  {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
                if(quotation.getCustomerType() != null) {
                    if (quotation.getCustomerTypeEnum() == QuotationContext.CustomerType.TENANT) {
                        if (QuotationAPI.lookupValueIsNotEmpty(quotation.getTenant())) {
                            List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(quotation.getTenant().getId(), false, false);
                            if (CollectionUtils.isNotEmpty(tenantContacts)) {
                                quotation.getTenant().setPeopleTenantContacts(tenantContacts);
                            }
                        }
                    } else if (quotation.getCustomerTypeEnum() == QuotationContext.CustomerType.CLIENT) {
                        if (QuotationAPI.lookupValueIsNotEmpty(quotation.getClient())) {
                            List<V3ClientContactContext> clientContacts = V3PeopleAPI.getClientContacts(quotation.getClient().getId(), false);
                            if (CollectionUtils.isNotEmpty(clientContacts)) {
                                quotation.getClient().setPeopleClientContacts(clientContacts);
                            }
                        }
                    }
                }
            }
        }


        return false;
    }
}
