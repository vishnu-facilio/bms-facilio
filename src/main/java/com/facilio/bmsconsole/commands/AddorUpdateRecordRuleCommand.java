package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.RecordSpecificRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class AddorUpdateRecordRuleCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		//RecordSpecificRuleContext recordRule = (RecordSpecificRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		WorkflowRuleContext recordRule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (recordRule != null) {
			
//			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			FacilioModule facilioModule = modBean.getModule(recordRule.getModuleId());
//			if (facilioModule == null) {
//				facilioModule = modBean.getModule(recordRule.getModuleName());
//			}
//			if (facilioModule == null) {
//				throw new Exception("Invalid Module");
//			}
//			recordRule.setModuleId(facilioModule.getModuleId());
			
			if (recordRule.getExecutionOrder() == -1) {
				recordRule.setExecutionOrder(0);
			}
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, recordRule);
			Chain ruleChain;
			if (recordRule.getId() > 0) {
				ruleChain = TransactionChainFactory.updateWorkflowRuleChain();
			} else {
				ruleChain = TransactionChainFactory.addWorkflowRuleChain();
			}
			ruleChain.execute(context);
		}
		return false;
	}

}
