package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class SetOrgIdConditionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioReportContext repContext = (FacilioReportContext)context;
		String moduleName = repContext.getModuleName();
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the report");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		Condition orgCondition = new Condition();
		orgCondition.setField(AccountConstants.getOrgIdField(module));
		orgCondition.setOperator(NumberOperators.EQUALS);
		orgCondition.setValue(String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));

		repContext.put(FacilioConstants.Reports.ORG_CONDITION, orgCondition);
		return false;
	}

}
