package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
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
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateTenantSpaceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
    	Boolean spacesUpdate = false;
    	if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("spacesUpdate")) {
             spacesUpdate = true;
         }
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
                context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
                if(CollectionUtils.isNotEmpty(tenant.getSpaces())) {
                    List<EventType> eventTypes = CommonCommandUtil.getEventTypes(context);
                    if (eventTypes != null && eventTypes.contains(EventType.EDIT)) {
                    		List<V3TenantSpaceContext> tenantSpaces = V3TenantsAPI.fetchTenantSpacesAssociation(tenant.getId(), false , "desc");                        
                    		if (CollectionUtils.isNotEmpty(tenantSpaces)) {
	                    		List<Long> existingSpaceIds = tenantSpaces.stream().map(V3TenantSpaceContext::getId).collect(Collectors.toList());
	                            List<BaseSpaceContext> filteredSpaces = tenant.getSpaces().stream().filter(space -> !existingSpaceIds.contains(space.getId())).collect(Collectors.toList());
	                            tenant.setSpaces(filteredSpaces);
                        }
                    }
                    if(spacesUpdate) {
                        checkSpaceOccupancy(tenant.getSpaces());
                    }
                }
                TenantsAPI.addAddress(tenant.getName(), tenant.getAddress());
            }
        }

        return false;
    }

    private void checkSpaceOccupancy(List<BaseSpaceContext> spaces) throws Exception {
    	if(CollectionUtils.isNotEmpty(spaces)) {
	        List<Long> spaceIds = spaces.stream().map(space -> space.getId()).collect(Collectors.toList());
	        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
	        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
 
	        SelectRecordsBuilder<V3TenantUnitSpaceContext> selectBuilder = new SelectRecordsBuilder<V3TenantUnitSpaceContext>()
	                .module(module)
	                .beanClass(V3TenantUnitSpaceContext.class)
	                .select(Collections.singletonList(fieldMap.get("tenant")))
                ;
	
	
	        Set<Long> baseSpaceParentIds = SpaceAPI.getBaseSpaceParentIds(spaceIds);
	
	        Criteria criteria = new Criteria();
	        criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space"), spaceIds, BuildingOperator.BUILDING_IS));
	        if (CollectionUtils.isNotEmpty(baseSpaceParentIds)) {
	            for(long parentId: baseSpaceParentIds) {
	                if (spaceIds.contains(parentId)) {
	                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please select only parent space");
	                }
	            }
	            criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("space"), baseSpaceParentIds, NumberOperators.EQUALS));
	        }
	
	        selectBuilder.andCriteria(criteria);
	        List<V3TenantUnitSpaceContext> tenantUnitSpaces = selectBuilder.get();
	        if (CollectionUtils.isNotEmpty(tenantUnitSpaces)) {
	            List<Long> tenantIds = tenantUnitSpaces.stream().filter(unit -> unit.getTenant() != null).map(unit -> unit.getTenant().getId()).collect(Collectors.toList());
	
	            if(CollectionUtils.isNotEmpty(tenantIds) && tenantIds.size() > 0) {

		            FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);		
		            Criteria criteriaCount = new Criteria();
		            criteriaCount.addAndCondition(CriteriaAPI.getIdCondition(tenantIds, tenantModule));
		            FacilioField aggregateField = FieldFactory.getIdField(tenantModule);
		            List<Map<String, Object>> props = V3RecordAPI.getRecordsAggregateValue(tenantModule.getName(), null, V3TenantContext.class,criteriaCount,BmsAggregateOperators.CommonAggregateOperator.COUNT, aggregateField, null);
		            if(props != null) {
		    			Long count = (Long) props.get(0).get(aggregateField.getName());
		    			if(count != null && count > 0) {
		                    throw new RESTException(ErrorCode.VALIDATION_ERROR, (spaceIds.size() == 1 ? "Selected " : "Some of the ") + " space is already occupied by another tenant");
		    			}
			        }
		        }
	        }
	    }
    }

}
