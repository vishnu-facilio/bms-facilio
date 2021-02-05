package com.facilio.trigger.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
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
//			return WorkflowUtil.getResult(recordId, null);
		}
	},
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
