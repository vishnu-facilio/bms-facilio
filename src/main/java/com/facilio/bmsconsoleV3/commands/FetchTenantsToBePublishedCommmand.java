package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class FetchTenantsToBePublishedCommmand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_TENENT_SHARING_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ControlGroupTenentContext> select = new SelectRecordsBuilder<ControlGroupTenentContext>()
				.select(fields)
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_TENENT_SHARING_MODULE_NAME)
				.beanClass(ControlGroupTenentContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentGroup"), group.getId()+"", NumberOperators.EQUALS));
		
		
		List<ControlGroupTenentContext> sharedTenents = select.get();
		
		List<Long> alreadySharedTenentIds = new ArrayList<Long>();
		for(ControlGroupTenentContext sharedTenent :sharedTenents) {
			
			alreadySharedTenentIds.add(sharedTenent.getTenant().getId());
		}
		
		
		fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_SPACES);
		
		fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<TenantSpaceContext> select1 = new SelectRecordsBuilder<TenantSpaceContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.TENANT_SPACES)
				.beanClass(TenantSpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space"), group.getSpace().getId()+"", BuildingOperator.BUILDING_IS));
		
		List<TenantSpaceContext> tenantSpaces = select1.get();
		
		List<Long> tenantIds = new ArrayList<Long>();
		
		for(TenantSpaceContext tenantSpace:tenantSpaces) {
			if(!tenantIds.contains(tenantSpace.getTenant().getId())) {
				tenantIds.add(tenantSpace.getTenant().getId());
			}
		}
		
		List<TenantContext> tenents = TenantsAPI.getTenants(tenantIds);
		
		List<TenantContext> alreadySharedTenants = new ArrayList<TenantContext>();
		List<TenantContext> yetToBeSharedTenants = new ArrayList<TenantContext>();
		
		for(TenantContext tenent :tenents) {
			if(alreadySharedTenentIds.contains(tenent.getId())) {
				alreadySharedTenants.add(tenent);
			}
			else {
				yetToBeSharedTenants.add(tenent);
			}
		}
		
		
		context.put(FacilioConstants.ContextNames.TENANT_LIST, yetToBeSharedTenants);
		context.put("alreadySharedTenantList", alreadySharedTenants);
		
		return false;
	}

}
