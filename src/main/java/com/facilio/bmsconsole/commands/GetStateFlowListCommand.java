package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetStateFlowListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (StringUtils.isEmpty(moduleName)) {
			throw new Exception("Module cannot be empty");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		if (module == null) {
			throw new Exception("Module cannot be empty");
		}
		
		List<WorkflowRuleContext> allStateFlow = (List<WorkflowRuleContext>) StateFlowRulesAPI.getAllStateFlow(module);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_LIST, allStateFlow);
		return false;
	}

}
