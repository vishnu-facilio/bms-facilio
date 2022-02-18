package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ValueGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerTenantUserValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
            try {
                V3TenantContactContext tenantContact = V3PeopleAPI.getTenantContactForUser(AccountUtil.getCurrentUser().getId(), true);
                if(tenantContact != null) {
                    List<Long> ids = new ArrayList<>();
                    Map<String, Object> map = FieldUtil.getAsProperties(tenantContact);
                    if (map.containsKey("tenantmulti")) {
                         List<V3TenantContext> tenantsServing = (List<V3TenantContext>)map.get("tenantmulti");
                         if(CollectionUtils.isNotEmpty(tenantsServing)) {
                             for(V3TenantContext t : tenantsServing) {
                                 ids.add(t.getId());
                             }
                             return StringUtils.join(ids, ",");
                         }
                    }
                    else if(tenantContact.getTenant() != null) {
                            return String.valueOf(tenantContact.getTenant().getId());
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Altayer Tenant(s) For Logged In User";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerTenantUserValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.USERS;
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 90;
    }
}
