package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.scriptengine.context.Value;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerTenantSiteValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                V3TenantContactContext tenantContact = V3PeopleAPI.getTenantContactForUser(AccountUtil.getCurrentUser().getId(), true);
                List<Long> ids = new ArrayList<>();

                if (tenantContact != null) {
                    Map<String, Object> map = FieldUtil.getAsProperties(tenantContact);
                    if (map.containsKey("tenantmulti")) {
                        List<V3TenantContext> tenantsServing = (List<V3TenantContext>) map.get("tenantmulti");
                        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(tenantsServing)) {
                            for (V3TenantContext t : tenantsServing) {
                                ids.add(t.getId());
                            }
                        }
                    } else if (tenantContact.getTenant() != null) {
                        ids.add(tenantContact.getTenant().getId());
                    }
                }
                if(CollectionUtils.isNotEmpty(ids)) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenantList(ids);
                    if (CollectionUtils.isNotEmpty(tenantUnits)) {
                        List<Long> siteIDs = new ArrayList<>();
                        for (TenantUnitSpaceContext tu : tenantUnits) {
                            siteIDs.add(tu.getSiteId());
                        }
                        return StringUtils.join(siteIDs, ",");
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Altayer Tenant Sites";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerTenantSiteValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SITE;
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

}
