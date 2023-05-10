package com.facilio.bmsconsole.jobs;

import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.v3.util.V3Util;
import org.json.simple.JSONObject;

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
				Object recordObj = V3Util.getRecord(stateTransition.getModuleName(), recordId, null);
				Map<String,Object> recordMap = FieldUtil.getAsProperties(recordObj);
				JSONObject bodyParam = new JSONObject();
				bodyParam.put(FacilioConstants.ContextNames.SKIP_APPROVAL,true);
				V3Util.processAndUpdateSingleRecord(stateTransition.getModuleName(), recordId, recordMap , bodyParam, null ,stateTransition.getId(), null,
						null,null, null,null);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
