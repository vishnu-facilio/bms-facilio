package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class QuotationFillDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(Constants.RECORD_ID);
        QuotationContext quotation = (QuotationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.QUOTATION, id);
        QuotationAPI.setLineItems(quotation);
        QuotationAPI.setQuotationAssociatedTerms(quotation);
        QuotationAPI.setTaxSplitUp(quotation);

        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if (MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchTenantContacts") && QuotationAPI.lookupValueIsNotEmpty(quotation.getTenant())) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
                List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(quotation.getTenant().getId(), false);
                if (CollectionUtils.isNotEmpty(tenantContacts)) {
                    quotation.getTenant().setPeopleTenantContacts(tenantContacts);
                }
            }
        }


        return false;
    }
}
