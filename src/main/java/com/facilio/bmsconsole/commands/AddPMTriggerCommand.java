package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import com.facilio.fw.BeanFactory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

public class AddPMTriggerCommand extends FacilioCommand {
	
	private boolean isBulkUpdate = false;
	
	public AddPMTriggerCommand() {}
	
	public AddPMTriggerCommand(boolean isBulkUpdate) {
		this.isBulkUpdate = isBulkUpdate;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<PreventiveMaintenance> pms;
		if (isBulkUpdate) {
			pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		}
		else {
			pms = Collections.singletonList((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		}
		
		if (pms == null) {
			return false;
		}
		
		for(PreventiveMaintenance pm: pms) {
			if (pm.getTriggers() != null && pm.isActive()) {
				addTriggersAndReadings(pm);
			}
		}
		return false;
	}
	
	private void addTriggersAndReadings(PreventiveMaintenance pm) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPMTriggersModule().getTableName()).fields(FieldFactory.getPMTriggerFields());

		for (PMTriggerContext trigger : pm.getTriggers()) {
			trigger.setPmId(pm.getId());
			trigger.setOrgId(AccountUtil.getCurrentOrg().getId());
			long ruleId;
			switch (trigger.getTriggerExecutionSourceEnum()) {
				case ALARMRULE: {
					WorkflowRuleContext workFlowRule = trigger.getWorkFlowRule();
					workFlowRule.setName("PM_" + pm.getId());
					workFlowRule.setRuleType(RuleType.PM_ALARM_RULE);
					workFlowRule.setActivityType(EventType.CREATE);
					workFlowRule.setModuleName("alarm");
					ruleId = WorkflowRuleAPI.addWorkflowRule(workFlowRule);
					trigger.setRuleId(ruleId);

					ActionContext action = new ActionContext();
					action.setActionType(ActionType.EXECUTE_PM);
					action.setStatus(true);

					List<ActionContext> actions = Collections.singletonList(action);
					ActionAPI.addActions(actions);
					ActionAPI.addWorkflowRuleActionRel(ruleId, actions);
				}
				break;
				case READING: {
					trigger.getReadingRule().setName("PM_" + pm.getId());
					trigger.getReadingRule().setRuleGroupId(-1);
					ruleId = WorkflowRuleAPI.addWorkflowRule(trigger.getReadingRule());
					trigger.setRuleId(ruleId);

					ActionContext action = new ActionContext();
					action.setActionType(ActionType.EXECUTE_PM);
					action.setStatus(true);

					List<ActionContext> actions = Collections.singletonList(action);
					ActionAPI.addActions(actions);
					ActionAPI.addWorkflowRuleActionRel(ruleId, actions);
				}
				break;
				case SCHEDULE:
//				trigger.setStartTime(System.currentTimeMillis());
					break;
				case CUSTOM:
					addScheduleRuleForTrigger(trigger, pm);
					break;
			}
			insertBuilder.addRecord(FieldUtil.getAsProperties(trigger));
		}
		insertBuilder.save();

		List<Map<String, Object>> triggerProps = insertBuilder.getRecords();
		Map<String,PMTriggerContext> triggerMap = new HashMap<>();
		for (int i = 0; i < triggerProps.size(); i++) {
			Map<String, Object> triggerProp = triggerProps.get(i);
			pm.getTriggers().get(i).setId((long) triggerProp.get("id"));
			if(pm.getTriggers().get(i).getName() != null) {
				triggerMap.put(pm.getTriggers().get(i).getName(), pm.getTriggers().get(i));
			}
		}
		pm.setTriggerMap(triggerMap);

		saveSharingContext(triggerMap);
	}

	private void saveSharingContext(Map<String,PMTriggerContext> triggerMap) throws Exception {
		Collection<PMTriggerContext> triggerContexts = triggerMap.values();
		List<PMTriggerContext> userTriggers = triggerContexts.stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.USER).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(userTriggers)) {
			return;
		}

		for (PMTriggerContext userTrigger: userTriggers) {
			SharingContext<SingleSharingContext> sharingContext = userTrigger.getSharingContext();
			if (sharingContext != null && !sharingContext.isEmpty()) {
				SharingAPI.addSharing(sharingContext, userTrigger.getId(), ModuleFactory.getPMExecSharingModule());
			}
		}
	}

	private void addScheduleRuleForTrigger(PMTriggerContext trigger, PreventiveMaintenance pm) throws Exception {
		WorkflowRuleContext rule = new WorkflowRuleContext();
		rule.setDateFieldId(trigger.getFieldId());
		rule.setModuleId(trigger.getCustomModuleId());
		rule.setInterval(trigger.getExecutionOffset() > 0 ? trigger.getExecutionOffset() : 0);
		rule.setRuleType(WorkflowRuleContext.RuleType.PM_CUSTOM_TRIGGER_RULE);
		rule.setName(trigger.getName()+"_"+System.currentTimeMillis());
		if (trigger.getExecuteOnEnum() == PMTriggerContext.ExecuteOn.ON) {
			rule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.ON);
		} else if (trigger.getExecuteOnEnum() == PMTriggerContext.ExecuteOn.AFTER) {
			rule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.AFTER);
		} else if (trigger.getExecuteOnEnum() == PMTriggerContext.ExecuteOn.BEFORE) {
			rule.setScheduleType(WorkflowRuleContext.ScheduledRuleType.BEFORE);
		}
		// rule.setSiteId(pm.getSiteId());
		rule.setActivityType(EventType.SCHEDULED);
		long ruleId = WorkflowRuleAPI.addWorkflowRule(rule);
		trigger.setRuleId(ruleId);
		ActionContext action = new ActionContext();
		action.setActionType(ActionType.EXECUTE_PM);
		action.setStatus(true);

		List<ActionContext> actions = Collections.singletonList(action);
		ActionAPI.addActions(actions);
		ActionAPI.addWorkflowRuleActionRel(ruleId, actions);
	}
}
