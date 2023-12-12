package com.facilio.trigger.util;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.triggers.PostTimeseriesTriggerContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.FieldChangeFieldContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
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
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.trigger.command.AddOrUpdateTriggerCommand;
import com.facilio.trigger.context.*;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TriggerUtil {
	
	public static final String TRIGGER_CONTEXT = "triggerContext";
	public static final String TRIGGERS_LIST = "triggerList";
	public static final String TRIGGER_ID = "triggerId";
	public static final String INVOKE_TRIGGER_TYPE = "invokeTriggerType";

	public static void executeTriggerActions(List<BaseTriggerContext> triggers, FacilioContext context,
											 String moduleName, ModuleBaseWithCustomFields record, List<UpdateChangeSet> changeSets) throws Exception {
		List<TriggerLog> logs = new ArrayList<>();
		for(BaseTriggerContext trigger :triggers) {
			if (trigger.shouldInvoke()) {
				if (trigger.getTriggerActions() != null) {
					for (TriggerActionContext action : trigger.getTriggerActions()) {
						action.execute(context, trigger, moduleName, record, changeSets);
						TriggerLog log = new TriggerLog();
						log.setTriggerId(trigger.getId());
						log.setTriggerActionId(action.getId());
						if (record != null) {
							log.setRecordId(record.getId());
						}
						log.setExecutionTime(DateTimeUtil.getCurrenTime());
						logs.add(log);
					}
				}
			}
		}
		addTriggerLogs(logs);
	}
	
	public static void addTriggerLogs(List<TriggerLog> logs) throws Exception {
		
		List<Map<String, Object>> props = FieldUtil.getAsMapList(logs, TriggerLog.class);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTriggerLogModule().getTableName())
				.fields(FieldFactory.getTriggerLogFields())
				.addRecords(props);
		
		insert.save();
	}

	public static BaseTriggerContext getTrigger(long triggerId) throws Exception {
		return getTrigger(triggerId, false);
	}
	public static BaseTriggerContext getTrigger(long triggerId, boolean fetchExtendedLookups) throws Exception {
		List<BaseTriggerContext> triggers = getTriggers(Collections.singletonList(triggerId), fetchExtendedLookups);
		if(CollectionUtils.isNotEmpty(triggers)) {
			BaseTriggerContext trigger = triggers.get(0);
			fillTriggerExtras(Collections.singletonList(trigger), fetchExtendedLookups, trigger.getTypeEnum());
			trigger.handleGet();
			return trigger;
		}
		return null;
	}

	public static List<TriggerFieldRelContext> getTriggersForEventTypeAndModule(List<Long> triggerIds) throws Exception{

		if (CollectionUtils.isEmpty(triggerIds)){
			return null;
		}

		List<FacilioField> fields = FieldFactory.getTriggerFieldRelFields();
		fields.addAll(FieldFactory.getTriggerFields());

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerFieldRelModule().getTableName())
				.select(fields)
				.innerJoin(ModuleFactory.getTriggerModule().getTableName())
				.on(ModuleFactory.getTriggerFieldRelModule().getTableName() + ".ID = " + "Facilio_Trigger.ID ")
				.andCondition(CriteriaAPI.getIdCondition(triggerIds,ModuleFactory.getTriggerFieldRelModule()));

		List<TriggerFieldRelContext> fieldRels = builder.get() != null ? FieldUtil.getAsBeanListFromMapList(builder.get(), TriggerFieldRelContext.class) : null;
		return fieldRels;
	}
	public static <T extends BaseTriggerContext> List<T> getTriggersForEventTypeAndModule(long moduleId, EventType eventType, Class clazz) throws Exception{

		if (moduleId <= 0 && eventType == null){
			return null;
		}

		List<FacilioField> fields = FieldFactory.getTriggerFieldRelFields();
		fields.addAll(FieldFactory.getTriggerFields());

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerFieldRelModule().getTableName())
				.select(fields)
				.innerJoin(ModuleFactory.getTriggerModule().getTableName())
				.on(ModuleFactory.getTriggerFieldRelModule().getTableName() + ".ID = " + "Facilio_Trigger.ID ")
				.andCondition(CriteriaAPI.getCondition("MODULE_ID","moduleId", String.valueOf(moduleId),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("EVENT_TYPE","eventType", String.valueOf(eventType.getValue()),NumberOperators.EQUALS));

		if (clazz == null){
			clazz = BaseTriggerContext.class;
		}
		return FieldUtil.getAsBeanListFromMapList(builder.get(), clazz);
	}

	public static TriggerFieldRelContext getFieldRel(long id) throws Exception {

		if (id <= 0) {
			return null;
		}

		List<FacilioField> fields = FieldFactory.getTriggerFieldRelFields();
		fields.addAll(FieldFactory.getTriggerFields());

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerFieldRelModule().getTableName())
				.select(fields)
				.innerJoin(ModuleFactory.getTriggerModule().getTableName())
				.on(ModuleFactory.getTriggerFieldRelModule().getTableName() + ".ID = " + "Facilio_Trigger.ID ")
				.andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getTriggerFieldRelModule()));

		TriggerFieldRelContext fieldRel = builder.fetchFirst() != null ? FieldUtil.getAsBeanFromMap(builder.fetchFirst(), TriggerFieldRelContext.class) : null;
		return fieldRel;
	}
	public static List<BaseTriggerContext> getTriggers(List<Long> ids, boolean fetchExtendedLookups) throws Exception {
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		FacilioModule module = ModuleFactory.getTriggerModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(ids, module));
		List<BaseTriggerContext> triggerList = FieldUtil.getAsBeanListFromMapList(select.get(), BaseTriggerContext.class);
		List<Long> fieldIds = triggerList.stream().filter(trigger -> (trigger.getEventTypeEnum().equals(EventType.FIELD_CHANGE) ||
				trigger.getEventTypeEnum().equals(EventType.SCHEDULED))).map(BaseTriggerContext::getId).collect(Collectors.toList());
		List<BaseTriggerContext> triggers = triggerList.stream().filter(trigger -> (trigger.getEventTypeEnum().equals(EventType.CREATE) ||
				trigger.getEventTypeEnum().equals(EventType.EDIT) || trigger.getEventTypeEnum().equals(EventType.CREATE_OR_EDIT) ||
				trigger.getEventTypeEnum().equals(EventType.TIMESERIES_COMPLETE))).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(fieldIds)) {
			triggers = CollectionUtils.isEmpty(triggers) ? new ArrayList<>() : triggers;
			triggers.addAll(getTriggersForEventTypeAndModule(fieldIds));
		}
		if (fetchExtendedLookups){
			fillTriggerExtras(triggers,fetchExtendedLookups,TriggerType.MODULE_TRIGGER);
		}
        return getExtendedTriggers(triggers, null, fetchExtendedLookups);
    }

    private static List<BaseTriggerContext> getExtendedTriggers(List<BaseTriggerContext> triggerList, Criteria criteria, boolean fetchExtendedLookups) throws Exception {
		List<BaseTriggerContext> triggers = new ArrayList<>();
		if (CollectionUtils.isEmpty(triggerList)) {
			return triggers;
		}

		Map<TriggerType, List<BaseTriggerContext>> triggerMap = new HashMap<>();
		for (BaseTriggerContext trigger : triggerList) {
			List<BaseTriggerContext> triggerIds = triggerMap.get(trigger.getTypeEnum());
			if (triggerIds == null) {
				triggerIds = new ArrayList<>();
				triggerMap.put(trigger.getTypeEnum(), triggerIds);
			}
			triggerIds.add(trigger);
		}

		for (Map.Entry<TriggerType, List<BaseTriggerContext>> entry : triggerMap.entrySet()) {
			TriggerType key = entry.getKey();
			FacilioModule triggerModule = AddOrUpdateTriggerCommand.getTriggerModule(key);

			List<BaseTriggerContext> triggerValues = entry.getValue();
			if ("trigger".equals(triggerModule.getName())) {
				triggers.addAll(triggerValues);
				continue;
			}
			
			List<FacilioField> triggerFields = AddOrUpdateTriggerCommand.getTriggerFields(key);
			Map<FacilioModule, List<FacilioField>> triggerFieldMap = new HashMap<>();
			for (FacilioField field : triggerFields) {
				FacilioModule fieldModule = field.getModule();
				List<FacilioField> list = triggerFieldMap.get(fieldModule);
				if (list == null) {
					list = new ArrayList<>();
					triggerFieldMap.put(fieldModule, list);
				}
				list.add(field);
			}

			Map<Long, BaseTriggerContext> triggerValueMap = triggerValues.stream().collect(Collectors.toMap(BaseTriggerContext::getId, Function.identity()));
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(triggerModule.getTableName())
					.select(triggerFieldMap.get(triggerModule))
					.andCondition(CriteriaAPI.getIdCondition(triggerValueMap.keySet(), triggerModule));
            if (criteria != null) {
                builder.andCriteria(criteria);
            }
			List<Map<String, Object>> maps = builder.get();
			for (Map<String, Object> map : maps) {
				long id = (long) map.get("id");
				map.putAll(FieldUtil.getAsProperties(triggerValueMap.get(id)));
			}
			List resolvedTriggerList = FieldUtil.getAsBeanListFromMapList(maps, AddOrUpdateTriggerCommand.getTriggerClass(key));
			triggers.addAll(resolvedTriggerList);
			
			if (!triggers.isEmpty()) {
				switch(key) {
					case AGENT_TRIGGER:
						fetchAgentTriggerLookups(resolvedTriggerList, fetchExtendedLookups);
						break;
				}
			}
		}
		return triggers;
	}

    private static void fetchAgentTriggerLookups(List<PostTimeseriesTriggerContext> triggers, boolean fetchExtendedLookups) throws Exception {
    	List<Long> criteriaIds = new ArrayList<>();
    	List<Long> agentIds = new ArrayList<>();
    	for (PostTimeseriesTriggerContext trigger: triggers) {
    		criteriaIds.add(trigger.getCriteriaId());
    		agentIds.add(trigger.getAgentId());
    	}
    	
    	Map<Long, Criteria> criteriaMap = null;
    	if (fetchExtendedLookups) {
    		criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
    	}
		AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
    	Map<Long, FacilioAgent> agentMap = agentBean.getAgentMap(agentIds, !fetchExtendedLookups);
    	for(PostTimeseriesTriggerContext trigger : triggers) {
    		if (criteriaMap != null) {
    			trigger.setCriteria(criteriaMap.get(trigger.getCriteriaId()));
    		}
    		trigger.setAgent(agentMap.get(trigger.getAgentId()));
    	}
    	
    }

    public static List<BaseTriggerContext> getTriggers(FacilioModule module, List<EventType> activityTypes, Criteria criteria, boolean onlyActive, boolean excludeInternal, Criteria extendedCriteria, boolean fetchExtendedLookups, TriggerType... triggerTypes) throws Exception {
		FacilioModule triggerModule = ModuleFactory.getTriggerModule();
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		Criteria cri = new Criteria();
		if (module != null && module.getModuleId() > 0) {
			cri.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS));
		}

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(triggerModule.getTableName())
				.select(fields)
				;
		if (!cri.isEmpty()) {
			select.andCriteria(cri);
		}

		if (onlyActive) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS));
		}
		if (excludeInternal) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("internal"), Boolean.FALSE.toString(), BooleanOperators.IS));
		}
		
		if(triggerTypes != null && triggerTypes.length > 0) {
			StringJoiner ids = new StringJoiner(",");
			for(TriggerType type : triggerTypes) {
				ids.add(String.valueOf(type.getValue()));
			}

			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), ids.toString(), NumberOperators.EQUALS));
		}
		
		if (criteria != null) {
			select.andCriteria(criteria);
		}
		
		if (CollectionUtils.isNotEmpty(activityTypes)) {
			Criteria activityCriteria = new Criteria();
			for (EventType type : activityTypes) {
				activityCriteria.addOrCondition(CriteriaAPI.getCondition("EVENT_TYPE", "eventType", String.valueOf(type.getValue()), NumberOperators.EQUALS));
			}
			select.andCriteria(activityCriteria);
		}

		List<Map<String, Object>> props = select.get();

		List<BaseTriggerContext> triggers = FieldUtil.getAsBeanListFromMapList(props, BaseTriggerContext.class);
		
		if(triggers != null && !triggers.isEmpty()) {
			fillTriggerExtras(triggers, fetchExtendedLookups, triggerTypes);
		}

        return getExtendedTriggers(triggers, extendedCriteria, fetchExtendedLookups);
	}
	
	public static void fillTriggerExtras(List<? extends BaseTriggerContext> triggers, boolean fetchLookups, TriggerType... triggerTypes) throws Exception {
		
		Map<Long, BaseTriggerContext> triggerIDmap = new HashedMap<Long, BaseTriggerContext>();
		for(BaseTriggerContext trigger : triggers) {
			triggerIDmap.put(trigger.getId(), trigger); 
		}
		
		List<FacilioField> fields = FieldFactory.getTriggerActionFields();

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getTriggerActionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("TRIGGER_ID", "triggerId", StringUtils.join(triggerIDmap.keySet(), ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = select.get();
		
		List<Long> typeRefIds = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			Long triggerid = (Long)prop.get("triggerId");
			TriggerActionContext triggerAction = FieldUtil.getAsBeanFromMap(prop, TriggerActionContext.class);
			typeRefIds.add(triggerAction.getTypeRefPrimaryId());
			triggerIDmap.get(triggerid).addTriggerAction(triggerAction);
		}
		if (fetchLookups) {
			fetchTypeRefObjects(typeRefIds, triggers, triggerTypes);
		}
	}
	
	private static void fetchTypeRefObjects(List<Long> typeRefIds, List<? extends BaseTriggerContext> triggers, TriggerType... triggerTypes) throws Exception {
		if (triggerTypes.length != 1) {
			return;
		}
		
		switch(triggerTypes[0]) {
			case AGENT_TRIGGER:
				getAgentTriggerTypeRef(typeRefIds, triggers);
				break;
		}
	}
	private static void getAgentTriggerTypeRef(List<Long> typeRefIds, List<? extends BaseTriggerContext> triggers) throws Exception {
		Map<Long, WorkflowContext> workflowsMap = WorkflowUtil.getWorkflowsAsMap(typeRefIds);
		for(BaseTriggerContext trigger: triggers){
			for(TriggerActionContext action: trigger.getTriggerActions()) {
				action.setTypeRefObj(FieldUtil.getAsProperties(workflowsMap.get(action.getTypeRefPrimaryId())));
			}
		}
	}

	public static void deleteActionFromTrigger(long triggerId, long triggerActionId) {

	}

	public static void addActionToTrigger(BaseTriggerContext trigger, List<TriggerActionContext> actions) throws Exception {
		trigger.setTriggerActions(actions);

		for(TriggerActionContext action : trigger.getTriggerActions()) {
			if(action.getId() < 0) {

				Map<String, Object> props = FieldUtil.getAsProperties(action);

				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getTriggerActionModule().getTableName())
						.fields(FieldFactory.getTriggerActionFields())
						.addRecord(props);

				insert.save();

				action.setId((long)props.get("id"));
			}
		}
	}
	
	public static int getMaxSchedulingDaysForScheduleFrequency(ScheduleInfo scheduleInfo){
		
		switch(scheduleInfo.getFrequencyTypeEnum()) 
		{
			case DO_NOT_REPEAT:
				return -1;
			case DAILY:
				return 21;
			case WEEKLY:
				return 8*2;
			case MONTHLY_DAY:
			case MONTHLY_WEEK:
			case YEARLY:
			case YEARLY_WEEK:
			case QUARTERLY_DAY:
			case QUARTERLY_WEEK:
			case HALF_YEARLY_DAY:
			case HALF_YEARLY_WEEK:
				return 366;
			default:
				return -1;	
		}
	}

	public static Map<Long, Set<BaseTriggerContext>> getRuleTriggerMap(List<Long> ruleIds,String moduleName,Set<EventType> eventTypes) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerActionModule().getTableName())
				.select(FieldFactory.getTriggerActionFields())
				.andCondition(CriteriaAPI.getCondition("TYPE", "actionType", String.valueOf(TriggerActionType.RULE_EXECUTION.getVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("TYPE_PRIMARY_ID", "typeRefPrimaryId", StringUtils.join(ruleIds, ","), NumberOperators.EQUALS));
		List<TriggerActionContext> actionList = FieldUtil.getAsBeanListFromMapList(builder.get(), TriggerActionContext.class);
		Map<Long, Set<BaseTriggerContext>> ruleVsTriggerMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(actionList)) {
			List<Long> triggerList = actionList.stream().map(TriggerActionContext::getTriggerId).collect(Collectors.toList());
			List<BaseTriggerContext> triggers = getTriggers(triggerList, false);
			Map<Long, List<BaseTriggerContext>> triggerContextMap = triggers.stream().collect(Collectors.groupingBy(BaseTriggerContext::getId,
							Collectors.mapping(Function.identity(),Collectors.toList())));
			for (TriggerActionContext action : actionList) {
				Set<BaseTriggerContext> list = ruleVsTriggerMap.get(action.getTypeRefPrimaryId());
				if (list == null) {
					list = new HashSet<>();
					ruleVsTriggerMap.put(action.getTypeRefPrimaryId(), list);
				}
				if (MapUtils.isNotEmpty(triggerContextMap) && CollectionUtils.isNotEmpty(triggerContextMap.get(action.getTriggerId()))) {
					list.addAll(triggerContextMap.get(action.getTriggerId()));
				}
			}
		}
		return ruleVsTriggerMap;
	}

	private static void addTrigger()throws Exception{
		BaseTriggerContext trigger = new BaseTriggerContext();
		trigger.setExecutionOrder(AddOrUpdateTriggerCommand.getTriggerMaxExecutionOrder(trigger.getModuleId()) + 1);
		Map<String,Object> props = FieldUtil.getAsProperties(trigger);
			GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getTriggerModule().getTableName())
					.fields(FieldFactory.getTriggerFields())
					.addRecord(props);
			insert.save();
			trigger.setId((long) props.get("id"));
	}

	public static void deleteFieldTrigger(long triggerId) throws Exception{
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTriggerFieldRelModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(triggerId, ModuleFactory.getTriggerFieldRelModule()));
		builder.delete();
	}

	public static BaseTriggerContext getTrigger(long id,EventType eventType) throws Exception{

		FacilioChain triggerChain = TriggerChainUtil.getTriggerSummaryChain(eventType);
		FacilioContext context = triggerChain.getContext();
		context.put(FacilioConstants.ContextNames.ID,id);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE,eventType);
		triggerChain.execute();

		BaseTriggerContext trigger = (BaseTriggerContext) context.get(FacilioConstants.ContextNames.TRIGGER);
		return trigger;
	}

	public static boolean isFieldTriggerPresent(long triggerId)throws Exception{
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerFieldRelModule().getTableName())
				.select(FieldFactory.getTriggerFieldRelFields())
				.andCondition(CriteriaAPI.getIdCondition(triggerId,ModuleFactory.getTriggerFieldRelModule()));

		List<Map<String, Object>> props = builder.get();
		boolean isPresent = props != null && props.size() > 0;
		return isPresent;
	}

	public static List<BaseTriggerContext> addTriggerForRule(WorkflowRuleContext rule) throws Exception{
			List<BaseTriggerContext> triggerList = new ArrayList<>();
			switch (rule.getActivityTypeEnum()){
				case FIELD_CHANGE:
					List<FieldChangeFieldContext> fields = rule.getFields();
					for (FieldChangeFieldContext field : fields) {
						TriggerFieldRelContext fieldRel = new TriggerFieldRelContext();
						fieldRel.setModuleId(rule.getModuleId());
						fieldRel.setName("Field_Change_Trigger " + rule.getName());
						fieldRel.setType(TriggerType.MODULE_TRIGGER);
						fieldRel.setFieldId(field.getFieldId());
						fieldRel.setOldValue(field.getOldValue());
						fieldRel.setNewValue(field.getNewValue());
						fieldRel.setEventType(EventType.FIELD_CHANGE);
						triggerList.add(fieldRel);
					}
					break;

				case SCHEDULED:
					TriggerFieldRelContext scheduleRel = new TriggerFieldRelContext();
					scheduleRel.setName("Schedule_Trigger " + rule.getName());
					scheduleRel.setType(TriggerType.MODULE_TRIGGER);
					scheduleRel.setFieldId(rule.getDateFieldId());
					scheduleRel.setScheduleType(rule.getScheduleType());
					scheduleRel.setTimeInterval(rule.getInterval());
					scheduleRel.setTime(rule.getTime());
					scheduleRel.setEventType(rule.getActivityTypeEnum());
					triggerList.add(scheduleRel);
					break;

				case CREATE_OR_EDIT:
					BaseTriggerContext createTrigger = new BaseTriggerContext("Create_Trigger " + rule.getName(),rule.getModuleId(),TriggerType.MODULE_TRIGGER,EventType.CREATE);
					BaseTriggerContext editTrigger = new BaseTriggerContext("Edit_Trigger " + rule.getName(),rule.getModuleId(),TriggerType.MODULE_TRIGGER,EventType.EDIT);
					triggerList.add(createTrigger);
					triggerList.add(editTrigger);
					break;
				default:
					BaseTriggerContext otherTrigger = new BaseTriggerContext("Trigger " + rule.getName(),rule.getModuleId(),TriggerType.MODULE_TRIGGER,rule.getActivityTypeEnum());
					triggerList.add(otherTrigger);
					break;
			}

			List<BaseTriggerContext> updatedTriggerList = new ArrayList<>();
			for (BaseTriggerContext trigger : triggerList) {
				FacilioChain triggerChain = TriggerChainUtil.getTriggerCreateChain(trigger.getEventTypeEnum());
				FacilioContext triggerContext = triggerChain.getContext();
				triggerContext.put(TriggerUtil.TRIGGER_CONTEXT, trigger);
				triggerContext.put(FacilioConstants.ContextNames.MODULE_NAME, rule.getModuleName());
				triggerChain.execute();
				updatedTriggerList.add((BaseTriggerContext)triggerContext.get(TriggerUtil.TRIGGER_CONTEXT));
			}
			return updatedTriggerList;
	}

	public static void addTriggersForWorkflowRule(WorkflowRuleContext rule) throws Exception {

		if (rule.getRuleTypeEnum().equals(WorkflowRuleContext.RuleType.MODULE_RULE) || rule.getRuleTypeEnum().equals(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION)) {

			if (!((rule.getActivityTypeEnum().equals(EventType.CREATE)) || (rule.getActivityTypeEnum().equals(EventType.EDIT)) ||
					(rule.getActivityTypeEnum().equals(EventType.CREATE_OR_EDIT)) || (rule.getActivityTypeEnum().equals(EventType.FIELD_CHANGE)) ||
					(rule.getActivityTypeEnum().equals(EventType.SCHEDULED)))) {
				return;
			}

			List<BaseTriggerContext> triggers = CollectionUtils.isNotEmpty(rule.getTriggers()) ? rule.getTriggers() : getTriggers(rule.getModuleName(), rule.getActivityTypeEnum(), rule.getFields(), rule.getDateFieldId(), rule.getScheduleTypeEnum(), rule.getInterval(), rule.getTimeObj());
			boolean flag = false;
			Set<EventType> eventTypes = triggers.stream().map(BaseTriggerContext::getEventTypeEnum).collect(Collectors.toSet());

			if (rule.getActivityTypeEnum().equals(EventType.CREATE_OR_EDIT) && CollectionUtils.isNotEmpty(triggers)) {
				if (eventTypes.contains(EventType.CREATE) && !(eventTypes.contains(EventType.EDIT))) {
					rule.setActivityType(EventType.EDIT);
					flag = true;
				} else if (eventTypes.contains(EventType.EDIT) && !(eventTypes.contains(EventType.CREATE))) {
					rule.setActivityType(EventType.CREATE);
					flag = true;
				}
			}

			if (flag || CollectionUtils.isEmpty(triggers)) {
				triggers = new ArrayList<>();
				triggers.addAll(addTriggerForRule(rule));
			}

			addTriggersForWorkflowRule(rule, triggers);
		}
	}

	public static List<BaseTriggerContext> getTriggers(String moduleName, EventType eventType, List<FieldChangeFieldContext> fields, Long dateFieldId, WorkflowRuleContext.ScheduledRuleType scheduledRuleType, Long timeInterval, LocalTime timeValue) throws Exception{

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<BaseTriggerContext> triggerList = getTriggers(module, null, null, false, true, null, false, TriggerType.MODULE_TRIGGER);
		FacilioChain chain = TriggerChainUtil.getTriggerListChain(eventType);
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		if (CollectionUtils.isNotEmpty(fields)){
			List<Long> fieldIds = fields.stream().map(FieldChangeFieldContext::getFieldId).collect(Collectors.toList());
			context.put(FacilioConstants.ContextNames.FIELD_IDS,fieldIds);
		}
		if (dateFieldId != null && dateFieldId > 0){
			List<Long> fieldIds = new ArrayList<>();
			fieldIds.add(dateFieldId);
			context.put(FacilioConstants.ContextNames.FIELD_IDS,fieldIds);
			context.put(FacilioConstants.ContextNames.SCHEDULE_RULE_TYPE,scheduledRuleType);
			context.put(FacilioConstants.ContextNames.TIME_INTERVAL,timeInterval);
			context.put(FacilioConstants.ContextNames.TIME_VALUE,timeValue);
		}
		context.put(FacilioConstants.ContextNames.EVENT_TYPE,eventType);
		context.put(FacilioConstants.ContextNames.TRIGGERS, triggerList);
		chain.execute();
		List<BaseTriggerContext> triggers = (List<BaseTriggerContext>) context.get(TriggerUtil.TRIGGERS_LIST);
		triggers = CollectionUtils.isEmpty(triggers) ? new ArrayList<>() : triggers;
		return triggers;
	}
	private static void addTriggersForWorkflowRule(WorkflowRuleContext rule, List<BaseTriggerContext> triggers) throws Exception {
		if (rule == null || CollectionUtils.isEmpty(triggers)) {
			return;
		}


		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTriggerActionModule().getTableName())
				.fields(FieldFactory.getTriggerActionFields());

		Map<Long, BaseTriggerContext> triggerMap = triggers.stream().collect(Collectors.toMap(BaseTriggerContext::getId, Function.identity()));
		Map<Long, Integer> triggerMaxOrder = getTriggerMaxOrder(triggerMap.keySet());
		for (BaseTriggerContext trigger : triggerMap.values()) {
			TriggerActionContext action = new TriggerActionContext();
			if (StringUtils.isEmpty(action.getName())) {
				action.setName(rule.getName());
			}
			action.setActionType(TriggerActionType.RULE_EXECUTION.getVal());
			action.setTypeRefPrimaryId(rule.getId());
			action.setTriggerId(trigger.getId());
			action.setExecutionOrder(triggerMaxOrder.get(trigger.getId()) + 1);
			builder.addRecord(FieldUtil.getAsProperties(action));
		}
		builder.save();

	}

	public static void updateTriggersForWorkflowRule(WorkflowRuleContext rule) throws Exception {

		if (!((rule.getActivityTypeEnum().equals(EventType.CREATE)) || (rule.getActivityTypeEnum().equals(EventType.EDIT)) ||
				(rule.getActivityTypeEnum().equals(EventType.CREATE_OR_EDIT)) || (rule.getActivityTypeEnum().equals(EventType.FIELD_CHANGE)) ||
				(rule.getActivityTypeEnum().equals(EventType.SCHEDULED)))){
			return;
		}

		Map<Long, Set<BaseTriggerContext>> ruleTriggerMap = getRuleTriggerMap(Collections.singletonList(rule.getId()),rule.getModuleName(),Collections.singleton(rule.getActivityTypeEnum()));
		Set<BaseTriggerContext> existingTriggers = ruleTriggerMap.getOrDefault(rule.getId(), new HashSet<>());
		List<BaseTriggerContext> triggers = rule.getTriggers() == null ? TriggerUtil.getTriggers(rule.getModuleName(),rule.getActivityTypeEnum(),rule.getFields(),rule.getDateFieldId(),rule.getScheduleTypeEnum(),rule.getInterval(),rule.getTimeObj())
											: rule.getTriggers();

		if (CollectionUtils.isEmpty(triggers)){
			triggers = addTriggerForRule(rule);
		}

		List<BaseTriggerContext> toBeDeleted = (List<BaseTriggerContext>) CollectionUtils.subtract(existingTriggers, triggers);
		Set<Long> triggerIds = toBeDeleted.stream().map(BaseTriggerContext::getId).collect(Collectors.toSet());
		deleteTriggersForWorkflowRule(rule, toBeDeleted);
		List<BaseTriggerContext> toBeAdded = (List<BaseTriggerContext>) CollectionUtils.subtract(triggers, existingTriggers);
		addTriggersForWorkflowRule(rule, toBeAdded);
		reOrderTriggerActions(triggerIds);
		System.out.println("Check the value");
	}

	private static void reOrderTriggerActions(Set<Long> triggerIds) throws Exception{
		if (CollectionUtils.isEmpty(triggerIds)){
			return;
		}
		FacilioModule module = ModuleFactory.getTriggerActionModule();
		List<FacilioField> fields = FieldFactory.getTriggerActionFields();
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(triggerIds.stream().collect(Collectors.toList()), true);

		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

		for (BaseTriggerContext trigger : triggers) {
			List<TriggerActionContext> actionContextList = trigger.getTriggerActions();
			if (CollectionUtils.isEmpty(actionContextList)){
				continue;
			}
			long executionOrder =  0;
			for (TriggerActionContext action : actionContextList) {
				executionOrder += 1;
				GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
				updateVal.addWhereValue("triggerId", trigger.getId());
				updateVal.addWhereValue("id",action.getId());
				updateVal.addUpdateValue("executionOrder", executionOrder);
				batchUpdateList.add(updateVal);
			}
		}

		List<FacilioField> whereFields = new ArrayList<>();
		whereFields.add(fieldMap.get("triggerId"));
		whereFields.add(fieldMap.get("id"));

		if (CollectionUtils.isNotEmpty(batchUpdateList)) {
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(Collections.singletonList(fieldMap.get("executionOrder")));
			updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);
		}
	}

	private static Map<Long, Integer> getTriggerMaxOrder(Set<Long> triggerIds) throws Exception {
		Map<Long, Integer> triggerMaxOrder = new HashMap<>();
		if (CollectionUtils.isEmpty(triggerIds)) {
			return triggerMaxOrder;
		}

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getTriggerActionFields());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTriggerActionModule().getTableName())
				.select(Arrays.asList(fieldMap.get("triggerId")))
				.aggregate(BmsAggregateOperators.NumberAggregateOperator.MAX, fieldMap.get("executionOrder"))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerId"), StringUtils.join(triggerIds, ","), NumberOperators.EQUALS))
				.groupBy(fieldMap.get("triggerId").getCompleteColumnName());
		List<Map<String, Object>> maps = builder.get();

		if (CollectionUtils.isNotEmpty(maps)) {
			for (Map<String, Object> map : maps) {
				triggerMaxOrder.put((Long) map.get("triggerId"), (Integer) map.get("executionOrder"));
			}
		}
		for (Long triggerId : triggerIds) {
			triggerMaxOrder.putIfAbsent(triggerId, 0);
		}
		return triggerMaxOrder;
	}

	public static void deleteTriggersForWorkflowRule(WorkflowRuleContext rule) throws Exception {
		deleteTriggersForWorkflowRule(rule, rule.getTriggers());
	}

	public static void deleteTriggersForWorkflowRule(WorkflowRuleContext rule, List<BaseTriggerContext> triggers) throws Exception {
		if (rule == null) {
			return;
		}

		if (CollectionUtils.isEmpty(triggers)) {
			return;
		}

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getTriggerActionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("TYPE", "actionType", String.valueOf(TriggerActionType.RULE_EXECUTION.getVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("TYPE_PRIMARY_ID", "typeRefPrimaryId", String.valueOf(rule.getId()), NumberOperators.EQUALS));

			List<Long> triggerIds = triggers.stream().map(BaseTriggerContext::getId).collect(Collectors.toList());
			builder.andCondition(CriteriaAPI.getCondition("TRIGGER_ID", "triggerId", StringUtils.join(triggerIds, ","), NumberOperators.EQUALS));

		builder.delete();
	}

	public static void deleteTriggers(Collection<Long> triggerSet) throws Exception {
		if (CollectionUtils.isEmpty(triggerSet)) {
			return;
		}

		FacilioModule module = ModuleFactory.getTriggerModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(triggerSet, module));
		builder.delete();
	}
	
	public static void deleteTypeRefObj(List<TriggerActionContext> actions, TriggerType triggerType) throws Exception {
		switch(triggerType) {
			case AGENT_TRIGGER:
				List<Long> workflowIds = actions.stream().map(TriggerActionContext::getTypeRefPrimaryId).collect(Collectors.toList());
				WorkflowUtil.deleteWorkflows(workflowIds);
				break;
		}
	}
	
	public static void deleteTriggerChildLookups(BaseTriggerContext oldTrigger, BaseTriggerContext trigger) throws Exception {
		switch(oldTrigger.getTypeEnum()) {
			case  AGENT_TRIGGER:
				deleteAgentTriggerLookups((PostTimeseriesTriggerContext) oldTrigger, (PostTimeseriesTriggerContext) trigger);
				break;
		}
	}
	private static void deleteAgentTriggerLookups(PostTimeseriesTriggerContext oldTrigger, PostTimeseriesTriggerContext trigger) throws Exception {
		if (oldTrigger.getCriteriaId() > 0 && (trigger == null || (trigger.getCriteria() != null && !trigger.getCriteria().isEmpty()) )) {
			CriteriaAPI.deleteCriteria(oldTrigger.getCriteriaId());
		}
	}

	public static List<ScheduleTriggerRecordRelationContext> getScheduleTriggerRecordRel(List<Long> recordIds, Long moduleId) throws Exception {
		return getScheduleTriggerRecordRel(recordIds,moduleId, null);
	}
	public static List<ScheduleTriggerRecordRelationContext> getScheduleTriggerRecordRel(List<Long> recordIds, Long moduleId, List<Long> ruleIds) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduleTriggerRelationFields())
				.table(ModuleFactory.getScheduleTriggerRecordRelationModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULE_ID","moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RECORD_ID","recordId", StringUtils.join(recordIds,","), NumberOperators.EQUALS));

		if(ruleIds != null) {
			builder.andCondition(CriteriaAPI.getCondition("TRIGGER_ID", "triggerId", StringUtils.join(ruleIds, ","), NumberOperators.EQUALS));
		}

		return FieldUtil.getAsBeanListFromMapList(builder.get(),ScheduleTriggerRecordRelationContext.class);
	}

	public static Long getOneTimeTriggerExecutionTime(TriggerFieldRelContext trigger, Long dateFieldValue) {
		Long interval = trigger.getTimeInterval();
		Long executionTime = -1L;

		if(dateFieldValue == null){
			return null;
		}

		dateFieldValue = dateFieldValue / 1000;

		switch (trigger.getScheduleTypeEnum()) {
			case BEFORE:
				executionTime = dateFieldValue - interval;
				break;
			case ON:
				executionTime = dateFieldValue;
				break;
			case AFTER:
				executionTime = dateFieldValue + interval;
				break;
		}

		return executionTime;
	}

	public static Long addScheduledTriggerRecordRelation(Long recordId,TriggerFieldRelContext trigger, Long dateField,Long executionTime) throws Exception {
		if(executionTime < (System.currentTimeMillis() / 1000)){
			return null;
		}

		FacilioModule module = ModuleFactory.getScheduleTriggerRecordRelationModule();
		List<FacilioField> fields = FieldFactory.getScheduleTriggerRelationFields();
		HashMap<String,Object> record = new HashMap<>();
		record.put("recordId",recordId);
		record.put("triggerId",trigger.getId());
		record.put("moduleId",trigger.getModuleId());
		record.put("dateFieldValue",dateField);
		record.put("executionTime",executionTime);

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.addRecord(record);

		insertBuilder.save();
		Long id = (Long) record.get("id");

		if(id != null) {
			FacilioTimer.scheduleOneTimeJobWithTimestampInSec(id, "ScheduleOneTimeTriggerExecution",executionTime,"priority");
		}

		return id;
	}

	public static int deleteTriggerRecordRelationshipTable(List<ScheduleTriggerRecordRelationContext> triggerRecordRelList) throws Exception {
		List<Long> triggerRecordRelIds = triggerRecordRelList.stream().map(triggerRecordRel -> triggerRecordRel.getId())
				.collect(Collectors.toList());

		FacilioTimer.deleteJobs(triggerRecordRelIds, "ScheduleOneTimeTriggerExecution");

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getScheduleTriggerRecordRelationModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(triggerRecordRelIds,ModuleFactory.getScheduleTriggerRecordRelationModule()));

		int rowsDeleted = builder.delete();
		return rowsDeleted;
	}

	public static void deleteTriggerRecordRelationshipTable(long triggerId) throws Exception {

		if (triggerId < -1){
			return;
		}


		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getScheduleTriggerRecordRelationModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("TRIGGER_ID","triggerId", String.valueOf(triggerId),NumberOperators.EQUALS));

		builder.delete();

	}

}
