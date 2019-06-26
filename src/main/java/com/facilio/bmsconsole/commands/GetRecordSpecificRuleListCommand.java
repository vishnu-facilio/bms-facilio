package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SingleRecordRuleAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetRecordSpecificRuleListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		if (StringUtils.isEmpty(moduleName)) {
			throw new Exception("Module cannot be empty");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		if (module == null) {
			throw new Exception("Module cannot be empty");
		}
		
		List<WorkflowRuleContext> allWorkFlowRule = (List<WorkflowRuleContext>) SingleRecordRuleAPI.getAllWorkFlowRule(parentId, module);
		context.put(FacilioConstants.ContextNames.RECORD_RULE_LIST, allWorkFlowRule);
		return false;
	}

}
