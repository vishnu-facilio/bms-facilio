package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class ValidateTenantSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (CollectionUtils.isNotEmpty(tenant.getSpaces())) {
			
			List<EventType> eventTypes = CommonCommandUtil.getEventTypes(context);
			if (eventTypes != null && eventTypes.contains(EventType.EDIT)) {
				List<TenantSpaceContext> spaces = TenantsAPI.fetchTenantSpaces(tenant.getId(), false);
				if (CollectionUtils.isNotEmpty(spaces)) {
					List<BaseSpaceContext> tenantSpaces = spaces.stream().map(TenantSpaceContext::getSpace).collect(Collectors.toList());
					List<Long> existingSpaceIds = tenantSpaces.stream().map(BaseSpaceContext::getId).collect(Collectors.toList());
					List<BaseSpaceContext> filteredSpaces = tenant.getSpaces().stream().filter(space -> !existingSpaceIds.contains(space.getId())).collect(Collectors.toList());
					tenant.setSpaces(filteredSpaces);
				}
			}
			
			checkSpaceOccupancy(tenant.getSpaces());
		}
		
		return false;
	}
	
	private void checkSpaceOccupancy(List<BaseSpaceContext> spaces) throws Exception {
		List<Long> spaceIds = spaces.stream().map(space -> space.getId()).collect(Collectors.toList());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.TENANT_SPACES);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		
		SelectRecordsBuilder<TenantSpaceContext> selectBuilder = new SelectRecordsBuilder<TenantSpaceContext>()
				  .module(module)
				  .beanClass(TenantSpaceContext.class)
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
		List<TenantSpaceContext> tenantSpaces = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(tenantSpaces)) {
			List<Long> tenantIds = tenantSpaces.stream().map(ts -> ts.getTenant().getId()).collect(Collectors.toList());
			
			FacilioModule tenantModule = modBean.getModule(ContextNames.TENANT);
			Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(modBean.getAllFields(tenantModule.getName()));
			FacilioStatus status = TicketAPI.getStatus(tenantModule, "Active");
			
			SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
					  .module(tenantModule)
					  .beanClass(TenantContext.class)
					  .select(new HashSet<>())
					  .aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(tenantModule))
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
