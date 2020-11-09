package com.facilio.trigger.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.trigger.context.Trigger;
import com.facilio.trigger.context.Trigger.Trigger_Type;
import com.facilio.trigger.context.TriggerAction;
import com.facilio.trigger.context.TriggerLog;

public class TriggerUtil {
	
	public static final String TRIGGER_CONTEXT = "triggerContext";
	
	public static void executeTriggerActions(List<Trigger> triggers, FacilioContext context) throws Exception {
		
		List<TriggerLog> logs = new ArrayList<TriggerLog>();
		
		for(Trigger trigger :triggers) {
			
			if(trigger.getTriggerActions() != null) {
				
				for(TriggerAction action : trigger.getTriggerActions()) {
					
					action.execute((FacilioContext)context);
				
					TriggerLog log = new TriggerLog();
					log.setTriggerId(trigger.getId());
					log.setTriggerActionId(action.getId());
					log.setExecutionTime(DateTimeUtil.getCurrenTime());
					
					logs.add(log);
				}
			}
		}
		
		addTriggerLogs(logs);
	}
	
	public static void addTriggerLogs(List<TriggerLog> logs) throws Exception {
		
		List<Map<String, Object>> props = FieldUtil.getAsMapList(logs, TriggerLog.class);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTriggerModule().getTableName())
				.fields(FieldFactory.getTriggerFields())
				.addRecords(props);
		
		insert.save();
	}

	public static Trigger getTrigger(long triggerId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		FacilioModule module = ModuleFactory.getTriggerModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getIdCondition(triggerId, module));
		
		List<Map<String, Object>> props = select.get();
		
		if(props != null && !props.isEmpty()) {
			Trigger trigger = FieldUtil.getAsBeanFromMap(props.get(0), Trigger.class);
			fillTriggerActions(Collections.singletonList(trigger));
			return trigger;
		}
		return null;
	}
	
	public static List<Trigger> getTriggers(FacilioModule module, List<EventType> activityTypes,Criteria criteria, Trigger_Type... triggerTypes) throws Exception {
		FacilioModule triggerModule = ModuleFactory.getTriggerModule();
		List<FacilioField> fields = FieldFactory.getTriggerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		Criteria cri = new Criteria();
		
		cri.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS));
		cri.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("childModuleId"), module.getExtendedModuleIds(), NumberOperators.EQUALS));
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(triggerModule.getTableName())
				.select(fields)
				.andCriteria(cri)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), Boolean.TRUE.toString(), BooleanOperators.IS))
				;
		
		if(triggerTypes != null && triggerTypes.length > 0) {
			StringJoiner ids = new StringJoiner(",");
			for(Trigger_Type type : triggerTypes) {
				ids.add(String.valueOf(type.getValue()));
			}

			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), ids.toString(), NumberOperators.EQUALS));
		}
		
		if (criteria != null) {
			select.andCriteria(criteria);
		}
		
		StringBuilder activityTypeWhere = new StringBuilder();
		List<Integer> values = new ArrayList<>();
		boolean first = true;
		for (EventType type : activityTypes) {
			if(first) {
				first = false;
			}
			else {
				activityTypeWhere.append(" OR ");
			}
			activityTypeWhere.append("? & Facilio_Trigger.EVENT_TYPE = ?");
			values.add(type.getValue());
			values.add(type.getValue());
		}
		select.andCustomWhere(activityTypeWhere.toString(), values.toArray());

		List<Map<String, Object>> props = select.get();

		List<Trigger> triggers = FieldUtil.getAsBeanListFromMapList(props, Trigger.class);
		
		fillTriggerActions(triggers);

		return triggers;
	}
	
	public static void fillTriggerActions(List<Trigger> triggers) throws Exception {
		
		Map<Long,Trigger> triggerIDmap = new HashedMap<Long, Trigger>();
		for(Trigger trigger : triggers) { 
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
}
