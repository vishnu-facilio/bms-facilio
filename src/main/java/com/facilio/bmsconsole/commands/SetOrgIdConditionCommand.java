package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;

public class SetOrgIdConditionCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		String moduleName = repContext.getModuleName();
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the report");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		Condition orgCondition = new Condition();
		orgCondition.setField(FieldFactory.getOrgIdField(module));
		orgCondition.setOperator(NumberOperators.EQUALS);
		orgCondition.setValue(String.valueOf(OrgInfo.getCurrentOrgInfo().getOrgid()));

		repContext.put(FacilioConstants.Reports.ORG_CONDITION, orgCondition);
		return false;
	}

}
