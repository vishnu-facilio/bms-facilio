package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetWorkflowRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowAPI.getWorkflowRules(AccountUtil.getCurrentOrg().getOrgId(), energyDataModule.getModuleId()));
		return false;
	}
}
