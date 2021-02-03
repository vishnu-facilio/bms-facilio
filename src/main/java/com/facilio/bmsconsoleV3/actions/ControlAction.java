package com.facilio.bmsconsoleV3.actions;

import java.util.List;

import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupRoutineContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ControlAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlGroupSection.Section_Type type;
	
	long startTime;
	long endTime;
	ControlGroupContext group;
	TenantContext tenant;
	String moduleName;
	
	public String resetTenantChanges() throws Exception {
		
		FacilioChain chain = TransactionChainFactoryV3.controlGroupResetTenantChanges();
		
		FacilioContext context = chain.getContext();
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, group);
		context.put(FacilioConstants.ContextNames.TENANT, tenant);
		
		chain.execute();
		
		return SUCCESS;
	}
	
	public String getSlot() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactoryV3.getControlGroupSlotsChain();
		
		FacilioContext context = chain.getContext();
		
		if(moduleName == null) {
			moduleName = ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME;
		}
		
		group = ControlScheduleUtil.getControlGroup(group.getId(),moduleName);
		
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, group);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		chain.execute();
		
		setData("slots", context.get(ControlScheduleUtil.CONTROL_GROUP_PLANNED_SLOTS));
		
		setData(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, group);
		
		return SUCCESS;
	}
	
	public String getTenantsToBePublished() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactoryV3.getTenantListToBePublished();
		
		FacilioContext context = chain.getContext();
		
		group = ControlScheduleUtil.getControlGroup(group.getId());
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, group);
		
		chain.execute();
		
		setData(FacilioConstants.ContextNames.TENANT_LIST, context.get(FacilioConstants.ContextNames.TENANT_LIST));
		setData("alreadySharedTenantList", context.get("alreadySharedTenantList"));
		
		return SUCCESS;
	}
	
	public String publishToTenant() throws Exception {
		
		FacilioChain chain = TransactionChainFactoryV3.getControlGroupPublishChain();
		
		FacilioContext context = chain.getContext();
		
		group = ControlScheduleUtil.getControlGroup(group.getId());
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, group);
		context.put(FacilioConstants.ContextNames.TENANT, tenant);
		
		chain.execute();
		
		setData(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT));
		
		return SUCCESS;
	}
	
	public String unPublishToTenant() throws Exception {
		
		FacilioChain chain = TransactionChainFactoryV3.getControlGroupUnPublishChain();
		
		FacilioContext context = chain.getContext();
		
		group = ControlScheduleUtil.getControlGroup(group.getId());
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, group);
		context.put(FacilioConstants.ContextNames.TENANT, tenant);
		
		chain.execute();
		
		setData(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT));
		
		return SUCCESS;
	}
	
	public String getCategoryForType() throws Exception {
		
		List<AssetCategoryContext> categories = type.getAssetCategoryList();
		setData("categories", categories);
		return SUCCESS;
	}


	public int getType() {
		if(type != null) {
			type.getIntVal();
		}
		return -1;
	}

	public void setType(int type) {
		this.type = ControlGroupSection.Section_Type.getAllOptions().get(type);
	}
}
