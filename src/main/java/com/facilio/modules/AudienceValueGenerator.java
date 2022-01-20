package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceSharingInfoContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AudienceValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        List<Object> buildingIds = new ArrayList<Object>();
        List<Long> siteIds = new ArrayList<Long>();

        try {
            Criteria criteria = new Criteria();
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

                        Criteria spaceCriteria = new Criteria();
                        spaceCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "1,2,3", StringOperators.IS));
                        Criteria spaceSubCriteria = new Criteria();
                        spaceSubCriteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(buildingIds, ","), PickListOperators.IS));
                        spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(siteIds, ","), PickListOperators.IS));
                        spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", "1", CommonOperators.IS_EMPTY));

                        spaceCriteria.andCriteria(spaceSubCriteria);

                        criteria.andCriteria(spaceCriteria);
                        criteria.orCriteria(getRoleCriteria());
                        criteria.orCriteria(getPeopleCriteria());

                    }
                }
            } else if (appType == AppDomain.AppDomainType.FACILIO.getIndex()) {

            } else if (appType == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {

            } else if (appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
                criteria.andCriteria(getRoleCriteria());
                criteria.orCriteria(getPeopleCriteria());
            }

            if(!criteria.isEmpty()) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

                FacilioModule subModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
                SelectRecordsBuilder<AudienceSharingInfoContext> builderCategory = new SelectRecordsBuilder<AudienceSharingInfoContext>()
                        .module(subModule)
                        .beanClass(AudienceSharingInfoContext.class)
                        .select(modBean.getAllFields(subModule.getName()))
                        .andCriteria(criteria)
                        ;
                List<AudienceSharingInfoContext> list = builderCategory.get();
                if(CollectionUtils.isNotEmpty(list)){
                    List<Long> ids = new ArrayList<>();
                    for(AudienceSharingInfoContext sh : list) {
                        ids.add(sh.getAudienceId().getId());
                    }
                    return StringUtils.join(ids, ",");
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Criteria getRoleCriteria() {
        Criteria roleCriteria = new Criteria();
        roleCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "4", StringOperators.IS));
        roleCriteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_ROLE_ID", "sharedToRoleId", StringUtils.join(AccountUtil.getCurrentUser().getRole().getId()), PickListOperators.IS));
        roleCriteria.andCriteria(roleCriteria);
        return roleCriteria;
    }

    private Criteria getPeopleCriteria() {
        Criteria pplCriteria = new Criteria();
        pplCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "5", StringOperators.IS));
        pplCriteria.addAndCondition(CriteriaAPI.getCondition("SHARED_TO_PEOPLE_ID", "sharedToPeopleId", StringUtils.join(AccountUtil.getCurrentUser().getPeopleId()), PickListOperators.IS));
        return pplCriteria;
    }
}
