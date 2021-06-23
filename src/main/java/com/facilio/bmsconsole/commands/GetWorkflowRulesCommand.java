package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetWorkflowRulesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule((String)context.get(FacilioConstants.ContextNames.MODULE));
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, WorkflowRuleAPI.getWorkflowRules(module.getModuleId()));
		return false;
	}
}
