package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GetStateFlowListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
