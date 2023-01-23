package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommunityBuildingValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {

        List<Object> buildingIds = new ArrayList<Object>();
        List<Long> siteIds = new ArrayList<Long>();

        try {
            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex() || appType == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
                TenantContext tenant = PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId());
                if (tenant != null) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
                    if (CollectionUtils.isNotEmpty(tenantUnits)) {
                        for (TenantUnitSpaceContext ts : tenantUnits) {
                            if(!siteIds.contains(ts.getSiteId())) {
                                siteIds.add(ts.getSiteId());
                            }
                            if (ts.getBuilding() != null) {
                                if(!buildingIds.contains(ts.getBuilding().getId())) {
                                    buildingIds.add(ts.getBuilding().getId());
                                }
                            }
                        }
                        Criteria criteria = new Criteria();
                        criteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(buildingIds, ","), PickListOperators.IS));
                        criteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(siteIds, ","), PickListOperators.IS));
                        criteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", "1", CommonOperators.IS_EMPTY));
                        return criteria;
                    }
                }
            } else if (appType == AppDomain.AppDomainType.FACILIO.getIndex()) {

            } else if (appType == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {

            } else if (appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Community Modules";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.CommunityBuildingValueGenerator";
    }

    @Override
    public String getModuleName() {
        return "Community Modules";
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }

}
