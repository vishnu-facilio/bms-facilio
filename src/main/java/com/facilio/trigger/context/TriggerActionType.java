package com.facilio.trigger.context;

import java.util.*;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.agent.controller.FacilioController;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.tasker.FacilioInstantJobScheduler;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.InstantJob;
import com.facilio.tasker.job.InstantJobExecutionCommand;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.chain.FacilioContext;


public enum TriggerActionType {

	FORMULA_CALCULATION(1) {

		@Override
		public void performAction(FacilioContext context, BaseTriggerContext trigger, String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets, Long recordId) throws Exception {
			// TODO Auto-generated method stub
		}

	},
	RULE_EXECUTION(2) {

		@Override
		public void performAction(FacilioContext context, BaseTriggerContext trigger, String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets, Long recordId) throws Exception {
			WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(recordId);
			if (workflowRule == null) {
				return;
			}

			Map<String, Object> orgPlaceHolders = WorkflowRuleAPI.getOrgPlaceHolders();
			Map<String, Object> placeHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, orgPlaceHolders);
			WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, changeSets, placeHolders, context);
		}
	},
	SCRIPT_EXECUTION(3) {

		@Override
		public void performAction(FacilioContext context, BaseTriggerContext trigger, String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets, Long recordId) throws Exception {
			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(recordId);
			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
			FacilioContext newContext = chain.getContext();
			newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
			List<ResourceContext> resources = (List<ResourceContext>) context.get(FacilioConstants.ContextNames.WORK_FLOW_PARAMS);
			List<Object> params = new ArrayList<>();
			params.add(resources);
            newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, params);
			chain.execute();
		}
	},
	INSTANT_JOB(4) {
		@Override
		public void performAction(FacilioContext context, BaseTriggerContext trigger, String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets, Long recordId) throws Exception {
			String jobName = (String) context.get(FacilioConstants.ContextNames.INSTANT_JOB_NAME);
			context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
			FacilioTimer.scheduleInstantJob(jobName, context);
		}
	}
	;
	
	private int val;

	private TriggerActionType(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	abstract public void performAction(FacilioContext context, BaseTriggerContext trigger, String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets, Long recordId) throws Exception;

	public static TriggerActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}

	private static final Map<Integer, TriggerActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

	private static final Logger LOGGER = LogManager.getLogger(TriggerActionType.class.getName());

	private static Map<Integer, TriggerActionType> initTypeMap() {
		Map<Integer, TriggerActionType> typeMap = new HashMap<>();
		for (TriggerActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}

}
