package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceSharingInfoContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class AltayerAudienceValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            List<Long> tuIds = new ArrayList<Long>();
            List<Long> buildingIds = new ArrayList<Long>();
            Criteria criteria = new Criteria();

            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                List<Long> ids = AltayerTenantSiteValueGenerator.getTenantContactForUser(AccountUtil.getCurrentUser().getId());
                if (CollectionUtils.isNotEmpty(ids)) {
                        List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenantList(ids);
                        if (CollectionUtils.isNotEmpty(tenantUnits)) {
                            for (TenantUnitSpaceContext ts : tenantUnits) {
                                if(ts.getBuilding() != null){
                                    if(!buildingIds.contains(ts.getBuilding().getId())) {
                                        buildingIds.add(ts.getBuilding().getId());
                                    }
                                }
                                if(!tuIds.contains(ts.getId())) {
                                    tuIds.add(ts.getId());
                                }
                            }

                            Criteria spaceCriteria = new Criteria();
                            spaceCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType", "1,4", StringOperators.IS));
                            Criteria spaceSubCriteria = new Criteria();
                            spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(tuIds, ","), PickListOperators.IS));
                            spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", StringUtils.join(buildingIds, ","), PickListOperators.IS));
                            spaceSubCriteria.addOrCondition(CriteriaAPI.getCondition("SHARED_TO_SPACE_ID", "sharedToSpace", "1", CommonOperators.IS_EMPTY));

                            spaceCriteria.andCriteria(spaceSubCriteria);

                            criteria.andCriteria(spaceCriteria);
                            Criteria roleCriteria = new Criteria();
                            roleCriteria.orCriteria(getRoleCriteria());
                            roleCriteria.addAndCondition(CriteriaAPI.getCondition("FILTER_SHARING_TYPE", "filterSharingType","1", CommonOperators.IS_EMPTY));
                            criteria.orCriteria(roleCriteria);
                            criteria.orCriteria(getPeopleCriteria());

                        }
                    }
                }
            if(!criteria.isEmpty()) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule subModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
                List<FacilioField> fields = modBean.getAllFields(subModule.getName());
                fields.addAll(modBean.getAllFields(ModuleFactory.getAudienceModule().getName()));
                SelectRecordsBuilder<AudienceSharingInfoContext> builderCategory = new SelectRecordsBuilder<AudienceSharingInfoContext>()
                        .module(subModule)
                        .innerJoin(ModuleFactory.getAudienceModule().getTableName())
                        .on(ModuleFactory.getAudienceModule().getTableName() + ".ID = " + subModule.getTableName() + ".AUDIENCE_ID")                        .beanClass(AudienceSharingInfoContext.class)
                        .select(fields)
                        .andCriteria(criteria)
                        ;

                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                List<LookupField> supplements = new ArrayList<>();

                LookupField audienceField = (LookupField) fieldsMap.get("audienceId");
                supplements.add(audienceField);

                builderCategory.fetchSupplements(supplements);

                List<AudienceSharingInfoContext> list = builderCategory.get();

                if(CollectionUtils.isNotEmpty(list)){
                    List<Long> ids = new ArrayList<>();
                    List<Long> filterAppliedAudience = new ArrayList<>();
                    for(AudienceSharingInfoContext sh : list) {
                        if(filterAppliedAudience.contains(sh.getAudienceId().getId())) {
                            continue;
                        }
                        if((sh.getSharingTypeEnum() == CommunitySharingInfoContext.SharingType.BUILDING || sh.getSharingTypeEnum() == CommunitySharingInfoContext.SharingType.TENANT_UNIT) && sh.getAudienceId().getFilterSharingType() != null && sh.getAudienceId().getFilterSharingType().equals(2l)) {
                            filterAppliedAudience.add(sh.getAudienceId().getId());
                            Criteria roleSharingInfoCriteria = new Criteria();
                            roleSharingInfoCriteria.addAndCondition(CriteriaAPI.getCondition("AUDIENCE_ID", "audienceId",String.valueOf(sh.getAudienceId().getId()), NumberOperators.EQUALS));
                            roleSharingInfoCriteria.addAndCondition(CriteriaAPI.getCondition("SHARING_TYPE", "sharingType",String.valueOf(CommunitySharingInfoContext.SharingType.ROLE.getIndex()), NumberOperators.EQUALS));

                            List<AudienceSharingInfoContext> roleAudienceSharingInfo = V3RecordAPI.getRecordsListWithSupplements(subModule.getName(), null, AudienceSharingInfoContext.class, roleSharingInfoCriteria,null);
                            List<AudienceSharingInfoContext> roleFilter = new ArrayList<>();
                            if(CollectionUtils.isNotEmpty(roleAudienceSharingInfo)) {
                                roleFilter = roleAudienceSharingInfo.stream()
                                        .filter(s -> s.getAudienceId().getId() == sh.getAudienceId().getId() && s.getSharingTypeEnum() == CommunitySharingInfoContext.SharingType.ROLE && s.getSharedToRoleId() == AccountUtil.getCurrentUser().getRole().getRoleId())
                                        .collect(Collectors.toList());
                            }
                            if(CollectionUtils.isEmpty(roleFilter)) {
                                continue;
                            }
                        }

                        ids.add(sh.getAudienceId().getId());
                    }
                    return StringUtils.join(ids, ",");
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
        return "Altayer Audience";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerAudienceValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.AUDIENCE;
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 90;
    }

    @Override
    public Criteria getCriteria(FacilioField field,List<Long> values) {
        return null;
    }
}
