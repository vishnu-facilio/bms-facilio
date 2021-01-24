package com.facilio.trigger.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.modules.*;
import com.facilio.trigger.context.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateTimeUtil;

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
					for (TriggerAction action : trigger.getTriggerActions()) {
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
		
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		FacilioModule module = ModuleFactory.getTriggerModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getIdCondition(triggerId, module));
		
		List<Map<String, Object>> props = select.get();
		
		if(props != null && !props.isEmpty()) {
			BaseTriggerContext trigger = FieldUtil.getAsBeanFromMap(props.get(0), BaseTriggerContext.class);
			fillTriggerExtras(Collections.singletonList(trigger));
			return trigger;
		}
		return null;
	}
	
	public static List<BaseTriggerContext> getTriggers(FacilioModule module, List<EventType> activityTypes, Criteria criteria, boolean onlyActive, TriggerType... triggerTypes) throws Exception {
		FacilioModule triggerModule = ModuleFactory.getTriggerModule();
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		Criteria cri = new Criteria();
		cri.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS));

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(triggerModule.getTableName())
				.select(fields)
				.andCriteria(cri)
				;

		if (onlyActive) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS));
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
			fillTriggerExtras(triggers);
		}

		return triggers;
	}
	
	public static void fillTriggerExtras(List<BaseTriggerContext> triggers) throws Exception {
		
		Map<Long, BaseTriggerContext> triggerIDmap = new HashedMap<Long, BaseTriggerContext>();
		for(BaseTriggerContext trigger : triggers) {
			triggerIDmap.put(trigger.getId(), trigger); 
		}
		
		List<FacilioField> fields = FieldFactory.getTriggerActionFields();
		fields.addAll(FieldFactory.getTriggerActionRelFields());
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getTriggerActionModule().getTableName())
				.innerJoin(ModuleFactory.getTriggerActionRelModule().getTableName())
				.on(ModuleFactory.getTriggerActionModule().getTableName()+".ID = "+ModuleFactory.getTriggerActionRelModule().getTableName() +".TRIGGER_ACTION_ID")
				.andCondition(CriteriaAPI.getCondition("TRIGGER_ID", "triggerId", StringUtils.join(triggerIDmap.keySet(), ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = select.get();
		
		for(Map<String, Object> prop :props) {
			Long triggerid = (Long)prop.get("triggerId");
			TriggerAction triggerAction = FieldUtil.getAsBeanFromMap(prop, TriggerAction.class);
			
			triggerIDmap.get(triggerid).addTriggerAction(triggerAction);
		}
	}

	public static void deleteActionFromTrigger(long triggerId, long triggerActionId) {

	}

	public static void addActionToTrigger(BaseTriggerContext trigger, List<TriggerAction> actions) throws Exception {
		trigger.setTriggerActions(actions);

		List<TriggerActionRel> rels = new ArrayList<>();
		for(TriggerAction action : trigger.getTriggerActions()) {
			if(action.getId() < 0) {

				Map<String, Object> props = FieldUtil.getAsProperties(action);

				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getTriggerActionModule().getTableName())
						.fields(FieldFactory.getTriggerActionFields())
						.addRecord(props);

				insert.save();

				action.setId((long)props.get("id"));
			}

			TriggerActionRel rel = new TriggerActionRel(trigger.getId(),action.getId());
			rels.add(rel);
		}

		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTriggerActionRelModule().getTableName())
				.fields(FieldFactory.getTriggerActionRelFields())
				.addRecords(FieldUtil.getAsMapList(rels, TriggerActionRel.class));

		insert.save();
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
}
