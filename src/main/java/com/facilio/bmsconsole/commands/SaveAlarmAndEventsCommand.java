package com.facilio.bmsconsole.commands;

import java.util.*;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand.PointedList;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class SaveAlarmAndEventsCommand extends FacilioCommand implements PostTransactionCommand {

	private static final Logger LOGGER = LogManager.getLogger(SaveAlarmAndEventsCommand.class.getName());

	private Map<String, PointedList<AlarmOccurrenceContext>> alarmOccurrenceMap;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<BaseEventContext> eventList = (List<BaseEventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		Map<String, BaseAlarmContext> alarmMap = (Map<String, BaseAlarmContext>) context.get("alarmMap");
		alarmOccurrenceMap = (Map<String, PointedList<AlarmOccurrenceContext>>) context.get("alarmOccurrenceMap");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (MapUtils.isNotEmpty(alarmMap)) {
			for (BaseAlarmContext baseAlarm : alarmMap.values()) {
				if (baseAlarm.getId() > 0) {
					UpdateRecordBuilder<BaseAlarmContext> builder = new UpdateRecordBuilder<BaseAlarmContext>()
							.moduleName(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))
							.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), modBean.getModule(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))))
							.fields(modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum())));
					builder.update(baseAlarm);
				}
				else {
					InsertRecordBuilder<BaseAlarmContext> builder = new InsertRecordBuilder<BaseAlarmContext>()
							.moduleName(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()))
							.fields(modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum())));
					LOGGER.debug("Alarm Value: " + FieldUtil.getAsProperties(baseAlarm));
					builder.insert(baseAlarm);
				}
			}
		}
		
		if (MapUtils.isNotEmpty(alarmOccurrenceMap)) {
			Map<AlarmOccurrenceContext.Type, List<AlarmOccurrenceContext>> occurrenceMap = new HashMap<>();
			for (PointedList<AlarmOccurrenceContext> occurrenceList : alarmOccurrenceMap.values()) {
				for (AlarmOccurrenceContext occurrence : occurrenceList) {
					List<AlarmOccurrenceContext> alarmOccurrenceContexts = occurrenceMap.get(occurrence.getTypeEnum());
					if (alarmOccurrenceContexts == null) {
						alarmOccurrenceContexts = new ArrayList<>();
						occurrenceMap.put(occurrence.getTypeEnum(), alarmOccurrenceContexts);
					}
					alarmOccurrenceContexts.add(occurrence);
				}
			}

			for (AlarmOccurrenceContext.Type type : occurrenceMap.keySet()) {
				List<FacilioField> allFields = modBean.getAllFields(NewAlarmAPI.getOccurrenceModuleName(type));
				InsertRecordBuilder<AlarmOccurrenceContext> builder = new InsertRecordBuilder<AlarmOccurrenceContext>()
						.moduleName(NewAlarmAPI.getOccurrenceModuleName(type))
						.fields(allFields);

				List<AlarmOccurrenceContext> records = new ArrayList<>();
				FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(type));
				for (AlarmOccurrenceContext alarmOccurrenceContext : occurrenceMap.get(type)) {
					if (alarmOccurrenceContext.getId() > 0) {
						UpdateRecordBuilder<AlarmOccurrenceContext> updateBuilder = new UpdateRecordBuilder<AlarmOccurrenceContext>()
								.moduleName(NewAlarmAPI.getOccurrenceModuleName(type))
								.andCondition(CriteriaAPI.getIdCondition(alarmOccurrenceContext.getId(), module))
								.fields(allFields);
						updateBuilder.update(alarmOccurrenceContext);
					} else {
						records.add(alarmOccurrenceContext);
					}
				}
				builder.addRecords(records);
				builder.save();
			}
		}
		
		if (MapUtils.isNotEmpty(alarmMap)) {
			for (BaseAlarmContext baseAlarm : alarmMap.values()) {
				List<FacilioField> allBaseAlarmField = modBean.getAllFields(NewAlarmAPI.getAlarmModuleName(baseAlarm.getTypeEnum()));
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allBaseAlarmField);
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getBaseAlarmModule().getTableName())
						.fields(Collections.singletonList(fieldMap.get("lastOccurrenceId")))
						.andCondition(CriteriaAPI.getIdCondition(baseAlarm.getId(), ModuleFactory.getBaseAlarmModule()));
				Map<String, Object> map = new HashMap<>();
				map.put("lastOccurrenceId", baseAlarm.getLastOccurrenceId());
				updateBuilder.update(map);
			}
		}
		
		Map<Type, List<BaseEventContext>>  eventsMap = new HashMap<>();
		if (eventList != null ) {
			for (BaseEventContext baseEvent : eventList) {
				List<BaseEventContext> list = eventsMap.get(baseEvent.getEventTypeEnum());
				if (list == null) {
					list = new ArrayList<>();
					eventsMap.put(baseEvent.getEventTypeEnum(), list);
				}
				list.add(baseEvent);
			}
		}
		if (MapUtils.isNotEmpty(eventsMap)) {
			for (Type type : eventsMap.keySet()) {
				String moduleName = NewEventAPI.getEventModuleName(type);
				InsertRecordBuilder<BaseEventContext> builder = new InsertRecordBuilder<BaseEventContext>()
						.moduleName(moduleName)
						.fields(modBean.getAllFields(moduleName));
				builder.addRecords(eventsMap.get(type));
				builder.save();
			}
		}

		Collection<BaseAlarmContext> alarmList = alarmMap.values();
		if (CollectionUtils.isNotEmpty(alarmList) && alarmList.size() == 1) {
			Map<String, List<BaseAlarmContext>> alarmModuleMap = new HashMap<>();
			for (BaseAlarmContext alarm : alarmList) {
				String alarmModuleName = NewAlarmAPI.getAlarmModuleName(alarm.getTypeEnum());
				List<BaseAlarmContext> baseAlarmContexts = alarmModuleMap.get(alarmModuleName);
				if (baseAlarmContexts == null) {
					baseAlarmContexts = new ArrayList<>();
					alarmModuleMap.put(alarmModuleName, baseAlarmContexts);
				}
				baseAlarmContexts.add(alarm);
			}
			context.put(FacilioConstants.ContextNames.RECORD_MAP, alarmModuleMap);
		}
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		if (MapUtils.isNotEmpty(alarmOccurrenceMap)) {
			Set<Long> alarmOccurrenceIds = new HashSet<>();
			for (String key : alarmOccurrenceMap.keySet()) {
				for (AlarmOccurrenceContext alarmOccurrence : alarmOccurrenceMap.get(key)) {
					alarmOccurrenceIds.add(alarmOccurrence.getId());
				}
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule alarmOccurrenceModule = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
			Map<String, FacilioField> occurrenceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(alarmOccurrenceModule.getName()));
			FacilioField noOfEventsField = occurrenceFieldMap.get("noOfEvents");
			
			FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
			Map<String, FacilioField> eventFieldMap = FieldFactory.getAsMap(modBean.getAllFields(eventModule.getName()));
			List<FacilioField> fields = new ArrayList<>();
			FacilioField alarmOccurrenceField = eventFieldMap.get("alarmOccurrence");
			fields.add(alarmOccurrenceField);
			
			FacilioField countField = new FacilioField();
			countField.setName("count");
			countField.setColumnName("COUNT(*)");
			countField.setDataType(FieldType.NUMBER);
			fields.add(countField);
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(eventModule.getTableName())
					.select(fields)
					.groupBy(alarmOccurrenceField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(alarmOccurrenceField, StringUtils.join(alarmOccurrenceIds, ","), NumberOperators.EQUALS));
			
			List<Map<String, Object>> list = builder.get();
			Map<String, Object> updateMap = new HashMap<>();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("alarmOccurrence")).longValue();
				int numberOfEvents = ((Number) map.get("count")).intValue();

				updateMap.put("noOfEvents", numberOfEvents);

				UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(alarmOccurrenceModule)
						.fields(Collections.singletonList(noOfEventsField))
						.andCondition(CriteriaAPI.getIdCondition(id, alarmOccurrenceModule));

				updateRecordBuilder.updateViaMap(updateMap);
			}


			Set<Long> alarmIds = new HashSet<>();
			for (String key : alarmOccurrenceMap.keySet()) {
				for (AlarmOccurrenceContext alarmOccurrence : alarmOccurrenceMap.get(key)) {
					alarmIds.add(alarmOccurrence.getAlarm().getId());
				}
			}

			FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);
			FacilioField noOfOccurrenceField = modBean.getField("noOfOccurrences", alarmModule.getName());

			fields = new ArrayList<>();
			FacilioField alarmField = occurrenceFieldMap.get("alarm");
			fields.add(alarmField);
			fields.add(countField);
			builder = new GenericSelectRecordBuilder()
					.table(alarmOccurrenceModule.getTableName())
					.select(fields)
					.groupBy(alarmField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(alarmField, StringUtils.join(alarmIds, ","), NumberOperators.EQUALS));
			list = builder.get();
			updateMap = new HashMap<>();
			for (Map<String, Object> map : list) {
				long id = ((Number) map.get("alarm")).longValue();
				int numberOfOccurrences = ((Number) map.get("count")).intValue();

				updateMap.put("noOfOccurrences", numberOfOccurrences);

				UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<WorkOrderContext>()
						.module(alarmModule)
						.fields(Collections.singletonList(noOfOccurrenceField))
						.andCondition(CriteriaAPI.getIdCondition(id, alarmModule));

				updateRecordBuilder.updateViaMap(updateMap);
			}
		}
		return false;
	}

}
