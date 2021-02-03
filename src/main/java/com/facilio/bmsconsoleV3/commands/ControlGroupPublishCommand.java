package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupAssetCategory;
import com.facilio.control.ControlGroupAssetContext;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ControlGroupPublishCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		
		ControlGroupTenentContext groupTenent = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(group), ControlGroupTenentContext.class);
		
		groupTenent.setTenant(tenant);
		groupTenent.setParentGroup(group);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<ControlGroupTenentContext> insert = new InsertRecordBuilder<ControlGroupTenentContext>()
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME))
				.addRecord(groupTenent);
		
		insert.save();
		
		
		ControlScheduleUtil.planAssetsForTenant(groupTenent);
		
		FacilioChain chain = TransactionChainFactoryV3.getPlanControlGroupSlotChain();
		
		FacilioContext newContext = chain.getContext();
		
		Map<String, List<ControlGroupContext>> map = Collections.singletonMap(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, Collections.singletonList(group));
		
		newContext.put(FacilioConstants.ContextNames.RECORD_MAP, map);
		
		chain.execute();
		
		
		
		FacilioChain chain1 = TransactionChainFactoryV3.getAddControlGroupAfterSaveChain();
		
		FacilioContext newContext1 = chain1.getContext();
		
		Map<String, List<ControlGroupTenentContext>> map1 = Collections.singletonMap(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME, Collections.singletonList(groupTenent));
		
		newContext1.put(FacilioConstants.ContextNames.RECORD_MAP, map1);
		newContext1.put(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
		
		chain1.execute();
		
		return false;
	}

}
