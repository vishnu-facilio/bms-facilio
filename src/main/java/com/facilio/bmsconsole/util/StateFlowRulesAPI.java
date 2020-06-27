package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.stateflow.TimerFieldUtil;
import com.facilio.bmsconsole.stateflow.TimerFieldUtil.TimerField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext.TransitionType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class StateFlowRulesAPI extends WorkflowRuleAPI {

	private static Logger LOGGER = LogManager.getLogger(StateFlowRulesAPI.class.getName());

	public static WorkflowRuleContext constructStateRuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		return constructStateRuleFromProps(prop, modBean, true);
	}

	public static void constructStateRule(List<WorkflowRuleContext> list) throws Exception {
		if (CollectionUtils.isNotEmpty(list)) {
			List<Long> formIds = null;
			for (WorkflowRuleContext workflowRuleContext : list) {
				if (workflowRuleContext instanceof ApproverWorkflowRuleContext) {
					ApproverWorkflowRuleContext approverWorkflowRuleContext = (ApproverWorkflowRuleContext) workflowRuleContext;
					approverWorkflowRuleContext.setApprovers(SharingAPI.getSharing(approverWorkflowRuleContext.getId(), ModuleFactory.getApproversModule(), ApproverContext.class));
				}

				if (workflowRuleContext instanceof AbstractStateTransitionRuleContext) {
					AbstractStateTransitionRuleContext abstractStateTransitionRuleContext = (AbstractStateTransitionRuleContext) workflowRuleContext;
					if (abstractStateTransitionRuleContext.getFormId() > 0) {
						if (formIds == null) {
							formIds = new ArrayList<>();
						}
						formIds.add(abstractStateTransitionRuleContext.getFormId());
					}
				}

				if (workflowRuleContext instanceof StateflowTransitionContext) {
					StateflowTransitionContext stateFlowRule = (StateflowTransitionContext) workflowRuleContext;
					List<ValidationContext> validations = ApprovalRulesAPI.getValidations(stateFlowRule.getId());
					stateFlowRule.setValidations(validations);
				}
			}

			if (CollectionUtils.isNotEmpty(formIds)) {
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getIdCondition(formIds, ModuleFactory.getFormModule()));
				List<FacilioForm> forms = FormsAPI.getFormFromDB(criteria);
				if (forms == null) {
					forms = new ArrayList<>();
				}
				Map<Long, FacilioForm> map = new HashMap<>();
				for (FacilioForm form : forms) {
					map.put(form.getId(), form);
				}
				for (WorkflowRuleContext workflowRuleContext : list) {
					if (workflowRuleContext instanceof AbstractStateTransitionRuleContext) {
						AbstractStateTransitionRuleContext stateFlowRule = (AbstractStateTransitionRuleContext) workflowRuleContext;
						stateFlowRule.setForm(map.get(stateFlowRule.getFormId()));
					}
				}
			}
		}
	}

	public static WorkflowRuleContext constructStateRuleFromProps(Map<String, Object> prop, ModuleBean modBean, boolean fetchChildren) throws Exception {
		StateflowTransitionContext stateFlowRule = FieldUtil.getAsBeanFromMap(prop, StateflowTransitionContext.class);
		stateFlowRule.setApprovers(SharingAPI.getSharing(stateFlowRule.getId(), ModuleFactory.getApproversModule(), ApproverContext.class));
		
		List<ValidationContext> validations = ApprovalRulesAPI.getValidations(stateFlowRule.getId());
		stateFlowRule.setValidations(validations);

		if (fetchChildren) {
			getStateFlowTransitionChildren(stateFlowRule);
		}
		return stateFlowRule;
	}

	public static void updateState(ModuleBaseWithCustomFields record, FacilioModule module, FacilioStatus facilioStatus, boolean includeStateFlowChange, Context context) throws Exception {
		if (facilioStatus == null) {
			LOGGER.error("Status cannot be empty");
			if (record != null) {
				LOGGER.error(FieldUtil.getAsJSON(record));
			}
			return;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioStatus oldState = record.getModuleState();
		if (oldState != null) {
			oldState = getStateContext(oldState.getId());
		}
		record.setModuleState(facilioStatus);

		Map<String, Object> prop = FieldUtil.getAsProperties(record);

		boolean shouldChangeTimer = facilioStatus.shouldChangeTimer(oldState);
		TimerField timerField = TimerFieldUtil.getTimerField(module.getName());
		if (shouldChangeTimer) {
			handleTimerUpdation(prop, facilioStatus, timerField, module, record);
		}

		// Update start and end time of the record
		if (facilioStatus.getType() == StatusType.CLOSED) {
			if (timerField != null && prop.get(timerField.getEndTimeFieldName()) == null) {
				FacilioModule timeLogModule = getTimeLogModule(module);
				if (timeLogModule != null) {
					Map<String, Object> lastTimerLog = TimerLogUtil.getLastTimerLog(timeLogModule, record.getId());
					if (lastTimerLog != null) {
						prop.put(timerField.getEndTimeFieldName(), lastTimerLog.get("endTime"));
					}
				}
			}
		}
		else if (facilioStatus.isTimerEnabled() && facilioStatus.getType() == StatusType.OPEN) {
			if (timerField != null) {
				if (prop.get(timerField.getEndTimeFieldName()) != null) {
					prop.put(timerField.getEndTimeFieldName(), -99);
				}
				if (prop.get(timerField.getStartTimeFieldName()) == null) {
					prop.put(timerField.getStartTimeFieldName(), record.getCurrentTime());
				}
			}
		}

		List<FacilioField> fields = new ArrayList<>();
		fields.add(modBean.getField("moduleState", module.getName()));

		// backward compatibility for workorder, to update existing status field
		if (module.getName().equals("workorder")) {
			fields.add(modBean.getField("status", module.getName()));
			prop.put("status", FieldUtil.getAsProperties(facilioStatus));

			if (facilioStatus.getStatus().equals("Resolved") &&
					record.getStateFlowId() == StateFlowRulesAPI.getDefaultStateFlow(module).getId()) {
				if (timerField != null) {
					prop.put(timerField.getEndTimeFieldName(), record.getCurrentTime());
				}
			}
		}

		if (timerField != null && timerField.isTimerEnabled()) {
			fields.addAll(timerField.getAllFields(modBean, module.getName()));
		}
		if (includeStateFlowChange) {
			fields.add(modBean.getField("stateFlowId", module.getName()));
		}
		UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(record.getId(), module));
		updateBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
		updateBuilder.updateViaMap(prop);
		Map<Long, List<UpdateChangeSet>> changeSet = updateBuilder.getChangeSet();
		CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,module.getName());

		if (oldState != null && oldState.getDisplayName() != null && facilioStatus != null && facilioStatus.getDisplayName() != null) {
			JSONObject info = new JSONObject();
			info.put("status", facilioStatus.getDisplayName());
			info.put("oldValue", oldState.getDisplayName());
			info.put("newValue", facilioStatus.getDisplayName());
			ActivityType activityType = null;
			if ((module.getName().contains("workorder"))) {
				activityType = WorkOrderActivityType.UPDATE_STATUS;
			}
			else if ((module.getName().contains("asset"))) {
				activityType = AssetActivityType.UPDATE_STATUS;
			}
			else if ((module.getName().contains("assetmovement"))) {
				activityType = AssetActivityType.LOCATION;
			} else if (module.getName().contains(FacilioConstants.ContextNames.QUOTE)) {
				activityType = CommonActivityType.UPDATE_STATUS;
			}
			if (activityType != null) {
				CommonCommandUtil.addActivityToContext(record.getId(), record.getCurrentTime(), activityType, info, (FacilioContext) context);
			}
		}

		checkAutomatedCondition(facilioStatus, module, record, context);
		addScheduledJobIfAny(facilioStatus.getId(), module.getName(), record, (FacilioContext) context);
	}

	private static void checkAutomatedCondition(FacilioStatus facilioStatus, FacilioModule module, ModuleBaseWithCustomFields record, Context context) throws Exception {
		List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(record.getStateFlowId(), facilioStatus.getId(), module.getName(), record, (FacilioContext) context, TransitionType.CONDITIONED);
		if (CollectionUtils.isNotEmpty(availableState)) {
			for (WorkflowRuleContext rule : availableState) {
				Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), record, WorkflowRuleAPI.getOrgPlaceHolders());
				WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(rule, module.getName(), record, getDefaultFieldChangeSet(module.getName(), record.getId()), recordPlaceHolders, (FacilioContext) context, true);
			}
		}
	}

	private static void addScheduledJobIfAny(long fromStateId, String moduleName, ModuleBaseWithCustomFields record, FacilioContext context) throws Exception {
		// remove scheduled jobs if any
		FacilioModule stateFlowScheduleModule = ModuleFactory.getStateFlowScheduleModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(stateFlowScheduleModule.getTableName())
				.select(FieldFactory.getStateFlowScheduleFields())
				.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(record.getId()), NumberOperators.EQUALS))
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(stateFlowScheduleModule));
				;
		List<Map<String, Object>> list = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			List<Long> jobIds = new ArrayList<>();
			for (Map<String, Object> map : list) {
				jobIds.add((Long) map.get("id"));
			}
			FacilioTimer.deleteJobs(jobIds, "StateFlowScheduledRule");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(stateFlowScheduleModule.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(jobIds, stateFlowScheduleModule));
			deleteBuilder.delete();
		}

		List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(record.getStateFlowId(), fromStateId, moduleName, record, context, TransitionType.SCHEDULED);
		if (CollectionUtils.isNotEmpty(availableState)) {
			for (WorkflowRuleContext rule : availableState) {
				StateflowTransitionContext state = (StateflowTransitionContext) rule;
				if (state.getTypeEnum() == TransitionType.SCHEDULED) {
					scheduleJob(record.getId(), state);
				}
			}
		}
	}

	private static void scheduleJob(long recordId, WorkflowRuleContext ruleContext) throws Exception {
		FacilioModule module = ModuleFactory.getStateFlowScheduleModule();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getStateFlowScheduleFields());
		Map<String, Object> prop = new HashMap<>();
		prop.put("recordId", recordId);
		prop.put("transitionId", ruleContext.getId());
		builder.addRecord(prop);
		builder.save();

		StateflowTransitionContext stateFlow = (StateflowTransitionContext) ruleContext;
		FacilioTimer.scheduleOneTimeJobWithDelay((long) prop.get("id"), "StateFlowScheduledRule", stateFlow.getScheduleTime(), "priority");
	}

	private static FacilioModule getTimeLogModule(FacilioModule module) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioModule> subModules = modBean.getSubModules(module.getModuleId(), ModuleType.TIME_LOG);
		if (CollectionUtils.isNotEmpty(subModules)) {
			if (subModules.size() > 1) {
				throw new Exception("Module " + module.getName() + " cannot have more than one time log module");
			}
			return subModules.get(0);
		}
		return null;
	}

	private static void handleTimerUpdation(Map<String, Object> prop, FacilioStatus ticketStatus, TimerField timerField, FacilioModule module, ModuleBaseWithCustomFields record) throws Exception {
		if (timerField == null || !timerField.isTimerEnabled()) {
			return;
		}

		long currentTime = record.getCurrentTime();

		if (ticketStatus.isTimerEnabled()) {
			prop.put(timerField.getResumeTimeFieldName(), currentTime);
		} else {
			Long totalTime = (Long) prop.get(timerField.getTotalTimeFieldName());
			if (totalTime == null) {
				totalTime = 0l;
			}
			Long lastTime = (Long) prop.get(timerField.getResumeTimeFieldName());
			if (lastTime != null) {
				totalTime += ((currentTime - lastTime) / 1000);
			}
			prop.put(timerField.getTotalTimeFieldName(), totalTime);
		}

		// Add or Update Time Log Entries
		FacilioModule timeLogModule = getTimeLogModule(module);
		if (timeLogModule != null) {
			long parentId = (long) prop.get("id");

			Map<String, Object> timeLogProp = new HashMap<>();

			long id = -1;
			long startTime = -1;
			long endTime = -1;

			if (!ticketStatus.isTimerEnabled()) {
				endTime = currentTime;
				Map<String, Object> lastTimerLog = TimerLogUtil.getLastTimerActiveLog(timeLogModule, parentId);
				if (lastTimerLog != null) {
					id = (long) lastTimerLog.get("id");
					startTime = (long) lastTimerLog.get("startTime");
				} else {
					return; // start_time has not recorded, so return
				}
			}
			else {
				startTime = currentTime;
			}
			timeLogProp.put("id", id);
			timeLogProp.put("parentId", parentId);
			timeLogProp.put("startTime", startTime);
			timeLogProp.put("endTime", endTime);

			long duration = -1;
			if (startTime > 0 && endTime > 0) {
				duration = (endTime - startTime) / 1000;
			}
			timeLogProp.put("duration", duration);

			TimerLogUtil.addOrUpdate(timeLogModule, timeLogProp);

			timeLogProp.clear();
		}
	}

	public static FacilioStatus getStateContext(long stateId) throws Exception {
		if (stateId < 0) {
			return null;
		}

		FacilioStatus stateContext = TicketAPI.getStatus(stateId);
		return stateContext;
	}

	public static List<UpdateChangeSet> getDefaultFieldChangeSet(String moduleName, long recordId) throws Exception {
		if (StringUtils.isEmpty(moduleName) || recordId == -1) {
			return null;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField("moduleState", moduleName);

		List<UpdateChangeSet> changeSet = new ArrayList<>();
		UpdateChangeSet updateChangeSet = new UpdateChangeSet();
		updateChangeSet.setFieldId(field.getFieldId());
		updateChangeSet.setRecordId(recordId);
		changeSet.add(updateChangeSet);
		return changeSet;
	}

	public static Map<String, Object> getStateTransitionScheduleInfo(long id) throws Exception {
		FacilioModule module = ModuleFactory.getStateFlowScheduleModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getStateFlowScheduleFields())
				.andCondition(CriteriaAPI.getIdCondition(id, module));

		return builder.fetchFirst();
	}

	public static List<WorkflowRuleContext> getAvailableState(long stateFlowId, long fromStateId, String moduleName, ModuleBaseWithCustomFields record, FacilioContext context, TransitionType... types) throws Exception {
		return getAvailableState(stateFlowId, fromStateId, -1, moduleName, record, context, types);
	}

	public static List<WorkflowRuleContext> getAvailableState(long stateFlowId, long fromStateId, long toStateId, String moduleName, ModuleBaseWithCustomFields record,
															  Context context, TransitionType... types) throws Exception {

		Map<String, Long> stateIds = new HashMap<>();
		stateIds.put("stateFlowId", stateFlowId);
		stateIds.put("fromStateId", fromStateId);
		stateIds.put("toStateId", toStateId);
		List<Map<String, Long>> ids = Collections.singletonList(stateIds);

		List<WorkflowRuleContext> stateFlows = getStateTransitions(ids, types);
		evaluateStateFlowAndExecuteActions(stateFlows, moduleName, record, context);

		return stateFlows;
	}

	private static List<WorkflowRuleContext> getStateTransitions(FacilioModule stateModule, List<FacilioField> fields, Criteria criteria) throws Exception {
		fields.addAll(FieldFactory.getWorkflowRuleFields());
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(stateModule.getTableName()).on("Workflow_Rule.ID = " + stateModule.getTableName() + ".ID")
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				;

		if (criteria != null && !criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}

		List<Map<String, Object>> list = builder.get();
		List<WorkflowRuleContext> stateFlows = getWorkFlowsFromMapList(list, true, true);
		return stateFlows;
	}

	public static List<WorkflowRuleContext> getStateTransitions(List<Map<String, Long>> stateIds, TransitionType... types) throws Exception {
		return getStateTransitions(stateIds, null, types);
	}

	public static List<WorkflowRuleContext> getStateTransitions(List<Map<String, Long>> stateIds, Criteria criteria, TransitionType... types) throws Exception {
		if (CollectionUtils.isEmpty(stateIds)) {
			return null;
		}

		Criteria stateCriteria = new Criteria();
		if (criteria != null && !criteria.isEmpty()) {
			stateCriteria.andCriteria(criteria);
		}
		for(Map<String, Long> ids: stateIds) {
			Long fromStateId = ids.get("fromStateId");
			long stateFlowId = ids.get("stateFlowId");
			Long toStateId = ids.get("toStateId");
			Criteria c = new Criteria();
			c.addAndCondition(CriteriaAPI.getCondition("STATE_FLOW_ID", "stateFlowId", String.valueOf(stateFlowId), NumberOperators.EQUALS));
			if (fromStateId != null && fromStateId > 0) {
				c.addAndCondition(CriteriaAPI.getCondition("FROM_STATE_ID", "fromStateId", String.valueOf(fromStateId), NumberOperators.EQUALS));
			}
			if (toStateId != null && toStateId > 0) {
				c.addAndCondition(CriteriaAPI.getCondition("TO_STATE_ID", "toStateId", String.valueOf(toStateId), NumberOperators.EQUALS));
			}
			stateCriteria.orCriteria(c);
		}

		if (types != null && types.length > 0) {
			Criteria c = new Criteria();
			StringBuilder criteriaValue = new StringBuilder();
			for (int i = 0; i < types.length; i++) {
				if (i != 0) {
					criteriaValue.append(", ");
				}
				criteriaValue.append(types[i].getValue());
			}
			c.addOrCondition(CriteriaAPI.getCondition("TYPE", "type", criteriaValue.toString(), EnumOperators.IS));
			stateCriteria.andCriteria(c);
		}

//		List<FacilioField> fields = FieldFactory.getStateRuleTransitionFields();
//		fields.addAll(FieldFactory.getStateRuleTransitionFields());
		return getStateTransitions(ModuleFactory.getStateRuleTransitionModule(), FieldFactory.getStateRuleTransitionFields(), stateCriteria);
	}

	public static List<WorkflowRuleContext> evaluateStateFlowAndExecuteActions(List<WorkflowRuleContext> stateFlows, String moduleName, ModuleBaseWithCustomFields record, Context context) throws Exception {
		if (CollectionUtils.isNotEmpty(stateFlows)) {

			Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
			List<UpdateChangeSet> changeSet = getDefaultFieldChangeSet(moduleName, record.getId());

			Iterator<WorkflowRuleContext> iterator = stateFlows.iterator();
			while (iterator.hasNext()) {
				WorkflowRuleContext stateFlow = iterator.next();
				boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateFlow, moduleName, record, changeSet, recordPlaceHolders, (FacilioContext) context, false);
				if (!evaluate) {
					iterator.remove();
				}
			}
		}
		return stateFlows;
	}

	public static Map<String, List<WorkflowRuleContext>> getAvailableStates(List<? extends ModuleBaseWithCustomFields> records) throws Exception {
		List<Map<String, Long>> stateIds = new ArrayList<>();
		for(ModuleBaseWithCustomFields record: records) {
			if (record.getApprovalStatus() != null) {
				Map<String, Long> ids = new HashMap<>();
				ids.put("fromStateId", record.getApprovalStatus().getId());
				ids.put("stateFlowId", record.getApprovalFlowId());
				stateIds.add(ids);
			}
			else if(record.getModuleState() != null) {
				Map<String, Long> ids = new HashMap<>();
				ids.put("fromStateId", record.getModuleState().getId());
				ids.put("stateFlowId", record.getStateFlowId());
				stateIds.add(ids);
			}
		}
		List<WorkflowRuleContext> stateFlows = getStateTransitions(stateIds);
		return getStateTransitionMap(stateFlows);
	}

	private static Map<String, List<WorkflowRuleContext>> getStateTransitionMap(List<WorkflowRuleContext> stateFlows) {
		Map<String, List<WorkflowRuleContext>> stateFlowMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(stateFlows)) {
			for(WorkflowRuleContext flow: stateFlows) {
				AbstractStateTransitionRuleContext stateFlowTransition = (AbstractStateTransitionRuleContext)flow;
				String key = stateFlowTransition.getStateFlowId() + "_" + stateFlowTransition.getFromStateId();
				List<WorkflowRuleContext> transitions = stateFlowMap.get(key);
				if (transitions == null) {
					transitions = new ArrayList<>();
					stateFlowMap.put(key, transitions);
				}
				transitions.add(stateFlowTransition);
			}
		}

		return stateFlowMap;
	}

	public static StateFlowRuleContext getStateFlowContext(long id) throws Exception {
		return (StateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(id);
	}

	public static List<? extends WorkflowRuleContext> getAllStateFlow(FacilioModule module) throws Exception {
		List<FacilioField> fields = FieldFactory.getStateFlowFields();
		fields.addAll(FieldFactory.getWorkflowRuleFields());

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DRAFT_PARENT_ID", "draftParentId", "", CommonOperators.IS_EMPTY));
		criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(WorkflowRuleContext.RuleType.STATE_FLOW.getIntVal()), NumberOperators.EQUALS));

		List<WorkflowRuleContext> stateFlowList = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(),
				criteria, null, null, StateFlowRuleContext.class);
		return WorkflowRuleAPI.getWorkFlowsFromMapList(FieldUtil.getAsMapList(stateFlowList, StateFlowRuleContext.class), true, true);
	}

	public static List<WorkflowRuleContext> getAllStateTransitionList(long stateFlowId) throws Exception {
		Map<String, Long> stateIds = new HashMap<>();
		stateIds.put("stateFlowId", stateFlowId);
		List<WorkflowRuleContext> stateTransitions = getStateTransitions(Collections.singletonList(stateIds));
		return stateTransitions;
	}

	public static List<WorkflowRuleContext> getAllStateTransitionList(StateFlowRuleContext stateFlowContext) throws Exception {
		if (stateFlowContext == null) {
			return null;
		}

		Map<String, Long> stateIds = new HashMap<>();
		stateIds.put("stateFlowId", stateFlowContext.getId());
		List<WorkflowRuleContext> stateTransitions = getStateTransitions(Collections.singletonList(stateIds));
		return stateTransitions;
	}

	public static WorkflowRuleContext getStateTransition(long stateFlowID, long stateTransitionId) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(stateTransitionId, ModuleFactory.getStateRuleTransitionModule()));
		criteria.addAndCondition(CriteriaAPI.getCondition("STATE_FLOW_ID", "stateFlowId", String.valueOf(stateFlowID), NumberOperators.EQUALS));

		List<FacilioField> fields = FieldFactory.getStateRuleTransitionFields();
		List<WorkflowRuleContext> stateTransitions = getStateTransitions(ModuleFactory.getStateRuleTransitionModule(), fields, criteria);
		if (CollectionUtils.isNotEmpty(stateTransitions)) {
			return stateTransitions.get(0);
		}
		return null;
	}

	public static void deleteStateTransition(long stateFlowID, long stateTransitionId) throws Exception {
		WorkflowRuleContext stateTransition = getStateTransition(stateFlowID, stateTransitionId);
		WorkflowRuleAPI.deleteWorkflowRule(stateTransition.getId());
	}

	public static StateFlowRuleContext getDefaultStateFlow(FacilioModule module) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("DEFAULT_STATE_FLOW", "defaltStateFlow", String.valueOf("true"), BooleanOperators.IS));
//		criteria.addAndCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getStateFlowModule()));
		;

		FacilioField moduleIdField = new FacilioField();
		moduleIdField.setName("moduleId");
		moduleIdField.setColumnName("MODULEID");
		moduleIdField.setModule(ModuleFactory.getWorkflowRuleModule());
		criteria.addAndCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

		List<WorkflowRuleContext> stateTransitions = getStateTransitions(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(), criteria);
		if (CollectionUtils.isNotEmpty(stateTransitions)) {
			return (StateFlowRuleContext) stateTransitions.get(0);
		}
		return null;
	}

	public static void addOrUpdateFormDetails(FormInterface rule) throws Exception {
		FacilioForm form = rule.getForm();

		String formName = "Enter Details" + "_" + rule.getId();
		formName = formName.toLowerCase().replaceAll("[^a-zA-Z0-9_]+", "");
		long ruleModuleId = rule.getModuleId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule ruleModule = modBean.getModule(ruleModuleId);
		if (ruleModule == null) {
			throw new IllegalArgumentException("Invalid module");
		}
		FacilioForm formFromDB = FormsAPI.getFormFromDB(formName, ruleModule);
		if (formFromDB != null) {
			FormsAPI.deleteForms(Collections.singletonList(formFromDB.getId()));
		}

		if (form == null || CollectionUtils.isEmpty(form.getSections())) {
			if (rule.getFormId() > 0) {
				rule.setFormId(-99);
			}
		}
		else {
			Context context = new FacilioContext();

			FacilioModule module = modBean.getModule(ruleModuleId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());

			form.setName(formName);
			context.put(FacilioConstants.ContextNames.FORM, form);

			FacilioChain chain = TransactionChainFactory.getAddFormCommand();
			chain.execute(context);

			rule.setFormId(form.getId());
		}
	}

	public static void getStateFlowTransitionChildren(StateflowTransitionContext rule) throws Exception {
		if (rule.getFormId() > 0) {
			rule.setForm(FormsAPI.getFormFromDB(rule.getFormId()));
		}
	}

	public static void deleteFormRuleContext(FormInterface rule) throws Exception {
		if (rule.getFormId() > 0) {
			FormsAPI.deleteForms(Collections.singletonList(rule.getFormId()));
		}
	}

	public static StateFlowRuleContext getDraftStateFlowForParent(long parentStateFlowId) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("DRAFT", "draft", String.valueOf(true), BooleanOperators.IS));
		criteria.addAndCondition(CriteriaAPI.getCondition("DRAFT_PARENT_ID", "draftParentId", String.valueOf(parentStateFlowId), NumberOperators.EQUALS));

		List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getExtendedWorkflowRules(ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields(), criteria, null, null, StateFlowRuleContext.class);
		if (CollectionUtils.isNotEmpty(workflowRules)) {
			if (workflowRules.size() > 1) {
				LOGGER.error("Cannot have more than one draft for one stateflow. Stateflow ID: " + parentStateFlowId);
			}
			return (StateFlowRuleContext) workflowRules.get(0);
		}
		return null;
	}

	public static void updateFormLevel(long stateFlowId, boolean formLevel) throws Exception {
		GenericUpdateRecordBuilder stateUpdate = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getStateFlowModule().getTableName())
				.fields(FieldFactory.getStateFlowFields())
				.andCondition(CriteriaAPI.getIdCondition(stateFlowId, ModuleFactory.getStateFlowModule()));
		Map<String, Object> map = new HashMap<>();
		map.put("formLevel", formLevel);
		stateUpdate.update(map);
	}

	public static List<StateFlowRuleContext> getStateFlowBaseDetails(List<Long> stateFlowIds) throws Exception {
		if (CollectionUtils.isEmpty(stateFlowIds)) {
			return new ArrayList<>();
		}
		List<FacilioField> fields = FieldFactory.getStateFlowFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getStateFlowModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(stateFlowIds, ModuleFactory.getStateFlowModule()));
		return FieldUtil.getAsBeanListFromMapList(builder.get(), StateFlowRuleContext.class);
	}

	public static void updateStateTransitionExecutionOrder(FacilioModule module, WorkflowRuleContext.RuleType ruleType) throws Exception {
		FacilioField executionOrderField = FieldFactory.getField("executionOrder", "EXECUTION_ORDER", ModuleFactory.getWorkflowRuleModule(), FieldType.NUMBER);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.innerJoin(ModuleFactory.getStateFlowModule().getTableName())
				.on(ModuleFactory.getWorkflowRuleModule().getTableName() + ".ID = " + ModuleFactory.getStateFlowModule().getTableName() + ".ID")
				.select(Arrays.asList(FieldFactory.getIdField(ModuleFactory.getWorkflowRuleModule()), executionOrderField))
				.andCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(ruleType.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> list = selectBuilder.get();

		if (CollectionUtils.isNotEmpty(list)) {
			for (Map<String, Object> map : list) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWorkflowRuleModule().getTableName())
						.fields(Collections.singletonList(executionOrderField))
						.andCondition(CriteriaAPI.getIdCondition((long) map.get("id"), ModuleFactory.getWorkflowRuleModule()));
				Integer executionOrder = ((Integer) map.get("executionOrder"));
				if (executionOrder == null) {
					executionOrder = 0;
				}
				map.put("executionOrder", executionOrder + 1);
				builder.update(map);
			}
		}
	}
}
