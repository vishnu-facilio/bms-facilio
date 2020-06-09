package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3TenantsAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetTenantSpaceAndContactsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
                List<BaseSpaceContext> spaces = V3TenantsAPI.fetchTenantSpaces(tenant.getId());
                if (CollectionUtils.isNotEmpty(spaces)) {
                    tenant.setSpaces(spaces);
                }
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
                    List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(tenant.getId(), false);
                    tenant.setPeopleTenantContacts(tenantContacts);
                } else {
                    List<V3ContactsContext> tenantcontacts = V3TenantsAPI.getTenantContacts(tenant.getId());
                    if (CollectionUtils.isNotEmpty(tenantcontacts)) {
                        tenant.setTenantContacts(tenantcontacts);
                    }
                }
            }
        }
        return false;
    }
}
