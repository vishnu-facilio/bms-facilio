package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceSharingInfoContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;

import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AudienceValueGenerator extends ValueGenerator{
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(AudienceValueGenerator.class);

    @Override
    public Object generateValueForCondition(int appType) {
        List<Long> tuIds = new ArrayList<Long>();
        List<Long> buildingIds = new ArrayList<Long>();

        try {
            Criteria criteria = new Criteria();
            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex() || appType == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
                if (tenant != null) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
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
            } else if (appType == AppDomain.AppDomainType.FACILIO.getIndex()) {

            } else if (appType == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {

            } else if (appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
                criteria.andCriteria(getRoleCriteria());
                criteria.orCriteria(getPeopleCriteria());
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
        return 90;
    }

    @Override
    public Criteria getCriteria(FacilioField field,List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria resultCriteria = new Criteria();
            resultCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(getModuleName())),StringUtils.join(value,","),NumberOperators.EQUALS));
            return resultCriteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}
