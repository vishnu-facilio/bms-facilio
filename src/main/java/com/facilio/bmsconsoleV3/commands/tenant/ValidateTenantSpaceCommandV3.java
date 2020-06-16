package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.util.V3TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateTenantSpaceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
                context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
                if(CollectionUtils.isNotEmpty(tenant.getSpaces())) {
                    List<EventType> eventTypes = CommonCommandUtil.getEventTypes(context);
                    if (eventTypes != null && eventTypes.contains(EventType.EDIT)) {
                        List<BaseSpaceContext> tenantSpaces = V3TenantsAPI.fetchTenantSpaces(tenant.getId(), false);
                        if (CollectionUtils.isNotEmpty(tenantSpaces)) {
                            List<Long> existingSpaceIds = tenantSpaces.stream().map(BaseSpaceContext::getId).collect(Collectors.toList());
                            List<BaseSpaceContext> filteredSpaces = tenant.getSpaces().stream().filter(space -> !existingSpaceIds.contains(space.getId())).collect(Collectors.toList());
                            tenant.setSpaces(filteredSpaces);
                        }
                    }
                    checkSpaceOccupancy(tenant.getSpaces());
                }
                TenantsAPI.addAddress(tenant.getName(), tenant.getAddress());
            }
        }

        return false;
    }

    private void checkSpaceOccupancy(List<BaseSpaceContext> spaces) throws Exception {
        List<Long> spaceIds = spaces.stream().map(space -> space.getId()).collect(Collectors.toList());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<V3TenantSpaceContext> selectBuilder = new SelectRecordsBuilder<V3TenantSpaceContext>()
                .module(module)
                .beanClass(V3TenantSpaceContext.class)
                .select(Collections.singletonList(fieldMap.get("tenant")))
                ;


        Set<Long> baseSpaceParentIds = SpaceAPI.getBaseSpaceParentIds(spaceIds);

        Criteria criteria = new Criteria();
        criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space"), spaceIds, BuildingOperator.BUILDING_IS));
        if (CollectionUtils.isNotEmpty(baseSpaceParentIds)) {
            for(long parentId: baseSpaceParentIds) {
                if (spaceIds.contains(parentId)) {
                    throw new IllegalArgumentException("Please select only parent space");
                }
            }
            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space"), baseSpaceParentIds, NumberOperators.EQUALS));
        }

        selectBuilder.andCriteria(criteria);
        List<V3TenantSpaceContext> tenantSpaces = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(tenantSpaces)) {
            List<Long> tenantIds = tenantSpaces.stream().map(ts -> ts.getTenant().getId()).collect(Collectors.toList());

            FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
            Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(modBean.getAllFields(tenantModule.getName()));
            FacilioStatus status = TicketAPI.getStatus(tenantModule, "Active");

            SelectRecordsBuilder<V3TenantContext> builder = new SelectRecordsBuilder<V3TenantContext>()
                    .module(tenantModule)
                    .beanClass(V3TenantContext.class)
                    .select(new HashSet<>())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(tenantModule))
                    .andCondition(CriteriaAPI.getIdCondition(tenantIds, tenantModule))
                    .andCondition(CriteriaAPI.getCondition(tenantFieldMap.get("moduleState"),String.valueOf(status.getId()) , NumberOperators.EQUALS))
                    ;

            List<Map<String, Object>> props = builder.getAsProps();
            if (CollectionUtils.isNotEmpty(props)) {
                long count = (long) props.get(0).get("id");
                if (count > 0) {
                    throw new IllegalArgumentException((spaceIds.size() == 1 ? "Selected " : "Some of the ") + " space is already occupied by another tenant");
                }
            }
        }
    }

}
