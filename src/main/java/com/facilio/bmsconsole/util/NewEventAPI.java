package com.facilio.bmsconsole.util;

import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.alarms.sensor.context.sensoralarm.SensorEventContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class NewEventAPI {

	public static Class getEventClass(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid event type");
		}

		switch (type) {
			case READING_ALARM:
				return ReadingEventContext.class;

			case ML_ANOMALY_ALARM:
				return MLAnomalyEvent.class;

			case RCA_ALARM:
				return RCAEvent.class;

			case READING_RCA_ALARM:
				return ReadingRCAEvent.class;

			case BMS_ALARM:
				return BMSEventContext.class;
				
			case VIOLATION_ALARM:
				return ViolationEventContext.class;

			case AGENT_ALARM:
				return AgentEventContext.class;
			case PRE_ALARM:
				return PreEventContext.class;
			case OPERATION_ALARM:
				return OperationAlarmEventContext.class;
			case RULE_ROLLUP_ALARM:
				return RuleRollUpEvent.class;
			case ASSET_ROLLUP_ALARM:
				return AssetRollUpEvent.class;
			case SENSOR_ALARM:
				return SensorEventContext.class;
			case SENSOR_ROLLUP_ALARM:
				return SensorRollUpEventContext.class;
			case MULTIVARIATE_ANOMALY_ALARM:
				return MultiVariateAnomalyEvent.class;
			default:
				throw new IllegalArgumentException("Invalid event type");
		}
	}

	public static String getEventModuleName(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid event type");
		}

		switch (type) {
			case READING_ALARM:
				return "readingevent";

			case ML_ANOMALY_ALARM:
				return "mlanomalyevent";

			case RCA_ALARM:
				return "RcaEvent";

			case READING_RCA_ALARM:
				return "readingrcaevent";

			case BMS_ALARM:
				return "bmsevent";
				
			case VIOLATION_ALARM:
				return "violationevent";
			case AGENT_ALARM:
				return "agentAlarmEvent";
			case CONTROLLER_ALARM:
				return "controllerAlarmEvent";
			case  PRE_ALARM:
				return "preevent";
			case OPERATION_ALARM:
				return "operationevent";

			case RULE_ROLLUP_ALARM:
				return "rulerollupevent";
			case ASSET_ROLLUP_ALARM:
				return FacilioConstants.ContextNames.ASSET_ROLLUP_EVENT;
			case SENSOR_ALARM:
				return FacilioConstants.ContextNames.SENSOR_EVENT;
			case SENSOR_ROLLUP_ALARM:
				return FacilioConstants.ContextNames.SENSOR_ROLLUP_EVENT;
			case MULTIVARIATE_ANOMALY_ALARM:
				return FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_EVENT;

			default:
				throw new IllegalArgumentException("Invalid event type");
		}
	}

	public static BMSEventContext transformEvent(BMSEventContext event, JSONTemplate template, Map<String, Object> placeHolders) throws Exception {
		Map<String, Object> eventProp = FieldUtil.getAsProperties(event);
		JSONObject content = template.getTemplate(placeHolders);
		if (content != null && !content.isEmpty()) {
			content.put("severityString", content.remove("severity"));
		}
		eventProp.putAll(FieldUtil.getAsProperties(content));
		event = FieldUtil.getAsBeanFromMap(eventProp, BMSEventContext.class);
		event.setMessageKey(null);
		eventProp.put("messageKey", event.getMessageKey()); //Setting the new key in case if it's updated
		CommonCommandUtil.appendModuleNameInKey(null, "event", eventProp, placeHolders);//Updating the placeholders with the new event props
		return event;
	}

	public static BaseEventContext getEvent(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		SelectRecordsBuilder<BaseEventContext> builder = new SelectRecordsBuilder<BaseEventContext>()
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		BaseEventContext baseEventContext = builder.fetchFirst();
		if (baseEventContext != null) {
			List<BaseEventContext> list = getExtendedEvent(Collections.singletonList(baseEventContext));
			if (CollectionUtils.isNotEmpty(list)) {
				return list.get(0);
			}
		}
		return null;
	}

	public static List<BaseEventContext> getExtendedEvent(List<BaseEventContext> baseEventContexts) throws Exception {
		if (CollectionUtils.isEmpty(baseEventContexts)) {
			return new ArrayList<>();
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		Map<Type, List<Long>> map = new HashMap<>();
		for (BaseEventContext event : baseEventContexts) {
			List<Long> list = map.get(event.getEventTypeEnum());
			if (list == null) {
				list = new ArrayList<>();
				map.put(event.getEventTypeEnum(), list);
			}
			list.add(event.getId());
		}

		List<BaseEventContext> newList = new ArrayList<>();
		for (Type type : map.keySet()) {
//			if (CollectionUtils.isNotEmpty(map.get(type))) {
//				continue;
//			}
			String moduleName = NewEventAPI.getEventModuleName(type);
			FacilioModule module = modBean.getModule(moduleName);

			SelectRecordsBuilder<BaseEventContext> builder = new SelectRecordsBuilder<BaseEventContext>()
					.module(module)
					.select(modBean.getAllFields(module.getName()))
					.beanClass(NewEventAPI.getEventClass(type))
					.andCondition(CriteriaAPI.getIdCondition(map.get(type), module));
			
			if(moduleName.equals(FacilioConstants.ContextNames.PRE_EVENT)) {
				List<FacilioField> fields = modBean.getAllFields(moduleName);
			    Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			    
		        List<LookupField> additionaLookups = new ArrayList<LookupField>();
		        LookupField ruleField = (LookupField) fieldsAsMap.get("rule");
		        LookupField subRuleField = (LookupField) fieldsAsMap.get("subRule");
		        additionaLookups.add(ruleField);
		        additionaLookups.add(subRuleField);
		        
		        builder.fetchSupplements(additionaLookups);
			}		       

			newList.addAll(builder.get());
		}
		return newList;
	}
	
	public static Map<Long, AlarmOccurrenceContext> updateLatestOccurrenceFromEvent(Map<Long, AlarmOccurrenceContext> latestOccurrenceMap) throws Exception {
		if (latestOccurrenceMap == null || MapUtils.isEmpty(latestOccurrenceMap)) {
			return null;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_EVENT);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		for(Long id:latestOccurrenceMap.keySet()) 
		{
			AlarmOccurrenceContext latestOccurrence = latestOccurrenceMap.get(id);
			
			SelectRecordsBuilder<BaseEventContext> selectbuilder = new SelectRecordsBuilder<BaseEventContext>()
					.beanClass(BaseEventContext.class)
					.module(module)
					.select(fields)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("baseAlarm"), ""+latestOccurrence.getAlarm().getId(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarmOccurrence"), ""+id, NumberOperators.EQUALS))
					.orderBy("CREATED_TIME DESC, ID DESC").limit(1);
			
			BaseEventContext newLatestEvent =  selectbuilder.fetchFirst();
			if (newLatestEvent != null) {
				latestOccurrence.setLastOccurredTime(newLatestEvent.getCreatedTime());
			}	
		}
		return latestOccurrenceMap;
	}

	public static List<? extends BaseEventContext> getExtendedEvents(Type alarmType, Criteria criteria) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String eventModuleName = getEventModuleName(alarmType);
		SelectRecordsBuilder builder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModuleName))
				.module(modBean.getModule(eventModuleName))
				.beanClass(getEventClass(alarmType));
		if (criteria != null) {
			builder.andCriteria(criteria);
		}
		return builder.get();
	}
}
