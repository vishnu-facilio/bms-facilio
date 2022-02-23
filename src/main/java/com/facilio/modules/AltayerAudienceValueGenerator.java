package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AltayerAudienceValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            Criteria criteria = new Criteria();

            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                V3TenantContactContext tenantContact = AltayerTenantSiteValueGenerator.getTenantContactForUser(AccountUtil.getCurrentUser().getId());
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
                    if (CollectionUtils.isNotEmpty(ids)) {
                        List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenantList(ids);
                        if (CollectionUtils.isNotEmpty(tenantUnits)) {
                            List<Long> tenantUnitIds = new ArrayList<>();
                            for (TenantUnitSpaceContext tu : tenantUnits) {
                                tenantUnitIds.add(tu.getId());
                            }

                            Criteria spaceCriteria = new Criteria();
                            spaceCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "1", StringOperators.IS));
                            Criteria spaceSubCriteria = new Criteria();
                            spaceSubCriteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(tenantUnitIds, ","), PickListOperators.IS));
                            spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", "1", CommonOperators.IS_EMPTY));

                            spaceCriteria.andCriteria(spaceSubCriteria);

                            criteria.andCriteria(spaceCriteria);
                            criteria.orCriteria(getRoleCriteria());
                            criteria.orCriteria(getPeopleCriteria());

                        }
                    }
                }
              }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    private Criteria getRoleCriteria() {
        Criteria roleCriteria = new Criteria();
        roleCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "2", StringOperators.IS));
        roleCriteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_ROLE_ID", "sharedToRoleId", StringUtils.join(AccountUtil.getCurrentUser().getRole().getId()), PickListOperators.IS));
        roleCriteria.andCriteria(roleCriteria);
        return roleCriteria;
    }

    private Criteria getPeopleCriteria() {
        Criteria pplCriteria = new Criteria();
        pplCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "3", StringOperators.IS));
        pplCriteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_PEOPLE_ID", "sharedToPeopleId", StringUtils.join(AccountUtil.getCurrentUser().getPeopleId()), PickListOperators.IS));
        return pplCriteria;
    }

    @Override
    public String getValueGeneratorName() {
        return null;
    }

    @Override
    public String getLinkName() {
        return null;
    }

    @Override
    public String getModuleName() {
        return null;
    }

    @Override
    public Boolean getIsHidden() {
        return null;
    }

    @Override
    public Integer getOperatorId() {
        return null;
    }
}
