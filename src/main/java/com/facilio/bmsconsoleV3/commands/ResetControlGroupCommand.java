package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ResetControlGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupTenentContext childGroup = (ControlGroupTenentContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		ControlGroupContext parentGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT_OLD);
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		
		ControlScheduleUtil.deleteControlGroupRelated(childGroup);
		ControlScheduleUtil.deleteControlGroupSlotRelated(childGroup);

		childGroup.setSections(parentGroup.getSections());
		
		ControlScheduleUtil.planAssetsForTenant(childGroup);
		
		FacilioChain chain1 = TransactionChainFactoryV3.getAddControlGroupAfterSaveChain();
		
		FacilioContext newContext1 = chain1.getContext();
		
		Map<String, List<ControlGroupTenentContext>> map1 = Collections.singletonMap(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME, Collections.singletonList(childGroup));
		
		newContext1.put(FacilioConstants.ContextNames.RECORD_MAP, map1);
		newContext1.put(FacilioConstants.ContextNames.MODULE_NAME, ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
		
		chain1.execute();
		return false;
	}

}
