package com.facilio.bmsconsole.jobs;

import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class StateFlowScheduledRuleJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long jobId = jc.getJobId();
			Map<String, Object> stateTransistionScheduleInfo = StateFlowRulesAPI.getStateTransistionScheduleInfo(jobId);
			long transistionId = (long) stateTransistionScheduleInfo.get("transistionId");
			long recordId = (long) stateTransistionScheduleInfo.get("recordId");
		
			StateflowTransistionContext stateTransistion = (StateflowTransistionContext) WorkflowRuleAPI.getWorkflowRule(transistionId);
			if (stateTransistion != null && recordId > 0) {
				FacilioContext context = new FacilioContext();
				Chain chain = TransactionChainFactory.getUpdateStateTransistionChain();
				
				long moduleId = stateTransistion.getModuleId();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				
				context.put(FacilioConstants.ContextNames.ID, recordId);
				context.put("transistion_id", stateTransistion.getId());
				context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
				
				chain.execute(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
