package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceSharingInfoContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AudienceValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        List<Long> tuIds = new ArrayList<Long>();

        try {
            Criteria criteria = new Criteria();
            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex() || appType == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
                if (tenant != null) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
                    if (CollectionUtils.isNotEmpty(tenantUnits)) {
                        for (TenantUnitSpaceContext ts : tenantUnits) {
                            if(!tuIds.contains(ts.getId())) {
                                tuIds.add(ts.getId());
                            }
                        }

                        Criteria spaceCriteria = new Criteria();
                        spaceCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "1", StringOperators.IS));
                        Criteria spaceSubCriteria = new Criteria();
                        spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(tuIds, ","), PickListOperators.IS));
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
        roleCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "2", StringOperators.IS));

        Criteria roleSubCriteria = new Criteria();
        roleSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_ROLE_ID", "sharedToRoleId", StringUtils.join(AccountUtil.getCurrentUser().getRole().getId()), PickListOperators.IS));
        roleSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_ROLE_ID", "sharedToRoleId", "1", CommonOperators.IS_EMPTY));

        roleCriteria.andCriteria(roleSubCriteria);
        return roleCriteria;
    }

    private Criteria getPeopleCriteria() {
        Criteria pplCriteria = new Criteria();
        pplCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "3", StringOperators.IS));

        Criteria peopleSubCriteria = new Criteria();
        peopleSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_PEOPLE_ID", "sharedToPeopleId", StringUtils.join(AccountUtil.getCurrentUser().getPeopleId()), PickListOperators.IS));
        peopleSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_PEOPLE_ID", "sharedToPeopleId", "1", CommonOperators.IS_EMPTY));

        pplCriteria.andCriteria(peopleSubCriteria);

        return pplCriteria;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.AUDIENCE;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AudienceValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.AUDIENCE;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }
}
