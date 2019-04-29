package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
		
		List<StateFlowContext> allStateFlow = StateFlowRulesAPI.getAllStateFlow(module);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_LIST, allStateFlow);
		return false;
	}

}
