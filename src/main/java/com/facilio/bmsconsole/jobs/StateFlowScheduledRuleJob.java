package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;

import java.util.Map;

public class StateFlowScheduledRuleJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long jobId = jc.getJobId();
			Map<String, Object> stateTransitionScheduleInfo = StateFlowRulesAPI.getStateTransitionScheduleInfo(jobId);
			long transitionId = (long) stateTransitionScheduleInfo.get("transitionId");
			long recordId = (long) stateTransitionScheduleInfo.get("recordId");
		
			StateflowTransitionContext stateTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(transitionId);
			if (stateTransition != null && recordId > 0) {
				FacilioContext context = new FacilioContext();
				Chain chain = TransactionChainFactory.getUpdateStateTransitionChain();
				
				long moduleId = stateTransition.getModuleId();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleId);
				
				context.put(FacilioConstants.ContextNames.ID, recordId);
				context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransition.getId());
				context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
				
				chain.execute(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
