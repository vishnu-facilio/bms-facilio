package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants.EventFieldFactory;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.time.DateRange;
import com.facilio.util.FacilioUtil;

public class FetchReportAdditionalInfoCommand extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(FetchReportAdditionalInfoCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report.getTypeEnum() == ReportType.READING_REPORT || report.getTypeEnum() == ReportType.REGRESSION_REPORT
				|| report.getTypeEnum() == ReportType.TEMPLATE_REPORT) {
			Boolean showAlarms = (Boolean) report.getFromReportState(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS);
			if (showAlarms == null) {
				showAlarms = false;
			}
			Boolean showSafeLimit = (Boolean) report.getFromReportState(FacilioConstants.ContextNames.REPORT_SHOW_SAFE_LIMIT);
			if (showSafeLimit == null) {
				showSafeLimit = false;
			}
			
			Boolean fetchEventBar = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_EVENT_BAR);
			fetchEventBar = fetchEventBar == null ? false : fetchEventBar;
			Long readingRuleId = (Long) context.get(FacilioConstants.ContextNames.READING_RULE_ID);
			
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			if (reportData != null && !reportData.isEmpty()) {
				Collection<Map<String, Object>> csvData = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
				Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);

				if (reportData != null && !reportData.isEmpty()) {
					Long alarmId = null;
					Object currentAlarm = null;
					if (showAlarms || fetchEventBar) {
						alarmId = (Long) context.get(FacilioConstants.ContextNames.ALARM_ID);
						if (alarmId != null && alarmId > 0) {
							if (AccountUtil.isFeatureEnabled(FeatureLicense.NEW_ALARMS)) {
								AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(alarmId);
								currentAlarm = alarmOccurrence.getAlarm();
							}
							else {
								currentAlarm = AlarmAPI.getReadingAlarmContext(alarmId);
							}
						}
					}

					for (ReportDataPointContext dp : report.getDataPoints()) {
						if (!validateFetchingOfAdditionalInfo(dp) || dp.getDynamicKpi() != null) {
							return false; // All datapoints should be valid to fetch alarms
						}

						if (showSafeLimit) {
							reportAggrData.put(dp.getAliases().get("actual")+".safeLimit", getSafeLimit(dp));
						}
					}

					if (showAlarms && !fetchEventBar) {
						if (AccountUtil.isFeatureEnabled(FeatureLicense.NEW_ALARMS)) {
							newAlarm(report, showAlarms, fetchEventBar, alarmId, (BaseAlarmContext) currentAlarm, reportAggrData, context, csvData);
						}
						else {
							oldAlarm(report, showAlarms, fetchEventBar, alarmId, (ReadingAlarmContext) currentAlarm, reportAggrData, context, csvData);
						}
					}
					
					if(fetchEventBar) {
						if (!AccountUtil.isFeatureEnabled(FeatureLicense.NEW_ALARMS)) {
							Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(EventFieldFactory.getEventFields());

							ReadingAlarmContext alarm = (ReadingAlarmContext) currentAlarm;
							Criteria criteria = new Criteria();
							criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("subRuleId"), readingRuleId + "", NumberOperators.EQUALS));
							criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), alarm.getResource().getId() + "", NumberOperators.EQUALS));
							criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), report.getDateRange().getStartTime() + "," + report.getDateRange().getEndTime(), DateOperators.BETWEEN));

							List<EventContext> events = EventAPI.getEvent(criteria);

							if (events != null) {
								Map<Long, EventContext> eventIdMap = new HashMap<>();
								Map<Long, EventContext> eventCreationTimeMap = new HashMap<>();

								for (EventContext event : events) {
									eventIdMap.put(event.getId(), event);
									eventCreationTimeMap.put(event.getCreatedTime(), event);
								}

								reportAggrData.put("events", splitEvents(report.getDateRange(), eventCreationTimeMap, events));
								reportAggrData.put("eventInfo", eventIdMap);
							}
						}
					}
				}
				
			}
		}
		return false;
	}
	
	private void newAlarm(ReportContext report, boolean showAlarms, boolean fetchEventBar, Long occurrenceId, BaseAlarmContext currentAlarm,
			Map<String, Object> reportAggrData, Context context, Collection<Map<String, Object>> csvData) throws Exception {
		Map<Long, AlarmOccurrenceContext> alarmMap = new HashMap<>();
		List<AlarmOccurrenceContext> allAlarms = new ArrayList<>();
		
		for (ReportDataPointContext dp : report.getDataPoints()) {
			List<Long> parentIds = getParentIds(dp);
			
			if (parentIds != null) {
				List<AlarmOccurrenceContext> occurrences = null;
				Long readingFieldId = null;
				if (currentAlarm instanceof MLAnomalyAlarm) {
					MLAnomalyAlarm alarm = (MLAnomalyAlarm) currentAlarm;
					readingFieldId = alarm.getEnergyDataFieldid();
				}
				else if (currentAlarm instanceof ReadingAlarm) {
					ReadingAlarm alarm = (ReadingAlarm) currentAlarm;
					readingFieldId = alarm.getReadingFieldId();
				}
				else if (currentAlarm instanceof OperationAlarmContext) {
					OperationAlarmContext alarm = (OperationAlarmContext) currentAlarm;
					readingFieldId = alarm.getReadingFieldId();
				}
				else if (currentAlarm instanceof SensorRollUpAlarmContext) {
					SensorRollUpAlarmContext alarm = (SensorRollUpAlarmContext) currentAlarm;
					readingFieldId = alarm.getReadingFieldId();
					List<SensorAlarmContext> relatedAlarms = AlarmAPI.getSensorChildAlarms(alarm, report.getDateRange().getStartTime(), report.getDateRange().getEndTime());
					
					JSONArray alarmMetaList = new JSONArray();
					
					for (SensorAlarmContext relatedAlarm : relatedAlarms) {
						Map<String, Object> relatedReportAggrData = new HashMap<>();
						Map<Long, AlarmOccurrenceContext> relatedAlarmMap = new HashMap<>();
						List<AlarmOccurrenceContext> relatedAlarmOccurrences = null;
						
						
						relatedAlarmOccurrences = NewAlarmAPI.getReadingAlarmOccurrences(relatedAlarm.getId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime());
						
						if (relatedAlarmOccurrences != null && !relatedAlarmOccurrences.isEmpty()) {
							relatedReportAggrData.put("alarms",  splitAlarmOccurrence(relatedAlarmOccurrences, report.getDateRange(), relatedAlarmMap));			
							for(Long key : relatedAlarmMap.keySet()) {
								AlarmOccurrenceContext alarmOccurrenceContext = relatedAlarmMap.get(key);
								alarmOccurrenceContext.setAlarm(alarmOccurrenceContext.getAlarm());
								alarmOccurrenceContext.setSubject(alarmOccurrenceContext.getAlarm().getSubject());
							}
							relatedReportAggrData.put("alarmInfo", relatedAlarmMap);
							relatedReportAggrData.put("alarmTitle", relatedAlarm.getSubject());
							alarmMetaList.add(relatedReportAggrData);							
						}
					}
					if (alarmMetaList != null && !alarmMetaList.isEmpty()) {
						reportAggrData.put("relatedAlarms",  alarmMetaList);
					}
					
				}
				
				
				if (occurrenceId == null || occurrenceId == -1) {
					occurrences = NewAlarmAPI.getReadingAlarmOccurrences(parentIds, -1l, dp.getyAxis().getFieldId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), null, null, null);
				}
				else if (currentAlarm != null && (readingFieldId == dp.getyAxis().getFieldId() || (readingFieldId == -1 && currentAlarm instanceof SensorRollUpAlarmContext)) && parentIds.contains(currentAlarm.getResource().getId())) {
					AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(occurrenceId);
					occurrences = NewAlarmAPI.getReadingAlarmOccurrences(alarmOccurrence.getAlarm().getId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime());
				}

//				AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(occurrenceId);
//				if (alarmOccurrence != null && alarmOccurrence.getAlarm() != null) {
//					occurrences = NewAlarmAPI.getReadingAlarmOccurrences(alarmOccurrence.getAlarm().getId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime());

				LOGGER.debug("Occurrences: " + occurrences + "; date: " + report.getDateRangeJson());
				if (CollectionUtils.isNotEmpty(occurrences)) {
					for (AlarmOccurrenceContext occurrence: occurrences) {
						allAlarms.add(occurrence);
					}
				}
//				}
			}
		}
		
		reportAggrData.put("alarms",  splitAlarmOccurrence(allAlarms, report.getDateRange(), alarmMap));
		for(Long key : alarmMap.keySet()) {
			AlarmOccurrenceContext alarmOccurrenceContext = alarmMap.get(key);
//			BaseAlarmContext baseAlarm = NewAlarmAPI.getAlarm(alarmOccurrenceContext.getAlarm().getId());
			alarmOccurrenceContext.setAlarm(alarmOccurrenceContext.getAlarm());
			alarmOccurrenceContext.setSubject(alarmOccurrenceContext.getAlarm().getSubject());
		}
		reportAggrData.put("alarmInfo", alarmMap);
	}

	private void oldAlarm(ReportContext report, boolean showAlarms, boolean fetchEventBar, Long alarmId, ReadingAlarmContext currentAlarm, 
			Map<String, Object> reportAggrData, Context context, Collection<Map<String, Object>> csvData) throws Exception {
		Map<Long, ReadingAlarmContext> alarmMap = new HashMap<>();
		List<ReadingAlarmContext> allAlarms = new ArrayList<>();
		
		for (ReportDataPointContext dp : report.getDataPoints()) {
			List<Long> parentIds = getParentIds(dp);

			if (parentIds != null) {
				List<ReadingAlarmContext> alarms = null;
				if (alarmId == null || alarmId == -1) {
					alarms = AlarmAPI.getReadingAlarms(parentIds, -1l, dp.getyAxis().getFieldId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), false);
				}
				else if (currentAlarm != null && currentAlarm.getReadingFieldId() == dp.getyAxis().getFieldId() && parentIds.contains(currentAlarm.getResource().getId())) {
					alarms = AlarmAPI.getReadingAlarms(currentAlarm.getEntityId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), false);
				}

				if (alarms != null && !alarms.isEmpty()) {
					for (ReadingAlarmContext alarm : alarms) {
						alarm.addAdditionInfo("dataPoint", dp.getName());
						allAlarms.add(alarm);
					}
				}
			}
		}

		reportAggrData.put("alarms", splitAlarms(allAlarms, report.getDateRange(), alarmMap));
		reportAggrData.put("alarmInfo", alarmMap);

		String sortAlias = (String) context.get(FacilioConstants.ContextNames.REPORT_SORT_ALIAS);
		getRecordWiseAlarms(report, csvData, allAlarms, alarmMap, sortAlias);
	}

	private boolean validateFetchingOfAdditionalInfo (ReportDataPointContext dp) {
		return dp.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dp.getxAxis().getDataTypeEnum() == FieldType.DATE;
	}
	
	private void getRecordWiseAlarms (ReportContext report, Collection<Map<String, Object>> csvData, List<ReadingAlarmContext> allAlarms, Map<Long, ReadingAlarmContext> alarmMap, String timeAlias) {
		if (CollectionUtils.isEmpty(csvData)) {
			return;
		}

		Iterator<Map<String, Object>> itr = csvData.iterator();
		Map<String, Object> currentData = itr.next();
		boolean isFirst = true;
		while (itr.hasNext()) {
			Map<String, Object> nextData = itr.next();
			currentData.put("alarms", calculateAlarmRangeAndGetMeta(report, allAlarms, alarmMap, timeAlias, currentData, nextData, isFirst));
			currentData = nextData;
			if (isFirst) {
				isFirst = false;
			}
		}
		currentData.put("alarms", calculateAlarmRangeAndGetMeta(report, allAlarms, alarmMap, timeAlias, currentData, null, isFirst));
	}

	private JSONObject calculateAlarmRangeAndGetMeta(ReportContext report, List<ReadingAlarmContext> allAlarms, Map<Long, ReadingAlarmContext> alarmMap, String timeAlias, Map<String, Object> currentData, Map<String, Object> nextData, boolean isFirst) {
		long startTime, endTime;

		if (isFirst) {
			startTime = report.getDateRange().getStartTime();
		}
		else {
			startTime = (long) currentData.get(timeAlias);
		}

		if (nextData == null) {
			endTime = report.getDateRange().getEndTime();
		}
		else {
			endTime = (long) nextData.get(timeAlias);
		}
		return getAlarmMeta(startTime, endTime, allAlarms, alarmMap);
	}
	
	private Map<String, Object> getSafeLimit (ReportDataPointContext dp) throws Exception {
		Pair<Double, Double> safelimitPair = CommonCommandUtil.getSafeLimitForField(dp.getyAxis().getFieldId());
		
		Map<String, Object> safeLimit = new HashMap<>();
		safeLimit.put("min", safelimitPair.getLeft());
		safeLimit.put("max", safelimitPair.getRight());
		
		return safeLimit;
	}
	
	private List<Long> getLongList (List list) {
		if (list != null) {
			return (List<Long>) list.stream().map(x -> (x instanceof Long ? x : Long.parseLong(x.toString()))).collect(Collectors.toList());
		}
		return null;
	}
	
	private List<Long> getParentIds (ReportDataPointContext dp) {
		List<Long> parentIds = getLongList((List) dp.getMeta(FacilioConstants.ContextNames.PARENT_ID_LIST));
		
		//The following is for backward compatibility
		if(parentIds == null && dp.getCriteria() != null && !dp.getCriteria().getConditions().isEmpty()) {
			Map<String, Condition> conditions = dp.getCriteria().getConditions();
			
			for(String key : conditions.keySet()) {
				Condition condition = conditions.get(key);
				if(condition.getFieldName().equals("parentId")) {
					parentIds = Collections.singletonList(Long.parseLong(condition.getValue()));
				}
			}
		}
		return parentIds;
	}
	
	private static final int MAX_ALARM_INFO_PER_WINDOW = 2;
	static JSONArray splitAlarms (List<ReadingAlarmContext> allAlarms, DateRange range, Map<Long, ReadingAlarmContext> alarmMap) {
		
		if (CollectionUtils.isEmpty(allAlarms)) {
			return null;
		}
		
		Set<Long> times = new TreeSet<>(); //To get sorted set
		long currentTime = System.currentTimeMillis();
		for (ReadingAlarmContext alarm : allAlarms) {
			if (alarm.getCreatedTime() < range.getStartTime()) {
				times.add(range.getStartTime());
			}
			else {
				times.add(alarm.getCreatedTime());
			}
			
			if (alarm.getClearedTime() == -1 || alarm.getCreatedTime() > range.getEndTime()) {
				times.add(range.getEndTime() > currentTime ? currentTime : range.getEndTime());
			}
			else {
				times.add(alarm.getClearedTime());
			}
		}
		
		JSONArray alarmMetaList = new JSONArray();
		List<Long> timesList = new ArrayList<>(times);
		for (int i = 0; i < timesList.size() - 1; i++) {
			long startTime = timesList.get(i);
			long endTime = timesList.get(i+1);
			alarmMetaList.add(getAlarmMeta(startTime, endTime, allAlarms, alarmMap));
		}
		alarmMetaList.add(getAlarmMeta(timesList.get(timesList.size() - 1), -1, null, alarmMap));
		
		return alarmMetaList;
	}
	
	
	static JSONArray splitAlarmOccurrence (List<AlarmOccurrenceContext> alarmOccurrences, DateRange range, Map<Long, AlarmOccurrenceContext> alarmMap) {
		
		if (CollectionUtils.isEmpty(alarmOccurrences)) {
			return null;
		}
		
		Set<Long> times = new TreeSet<>(); //To get sorted set
		long currentTime = System.currentTimeMillis();
		for (AlarmOccurrenceContext alarm : alarmOccurrences) {
			if (alarm.getCreatedTime() < range.getStartTime()) {
				times.add(range.getStartTime());
			}
			else {
				times.add(alarm.getCreatedTime());
			}
			
			if (alarm.getClearedTime() == -1 || alarm.getCreatedTime() > range.getEndTime()) {
				times.add(range.getEndTime() > currentTime ? currentTime : range.getEndTime());
			}
			else {
				times.add(alarm.getClearedTime());
			}
		}
		
		JSONArray alarmMetaList = new JSONArray();
		List<Long> timesList = new ArrayList<>(times);
		for (int i = 0; i < timesList.size() - 1; i++) {
			long startTime = timesList.get(i);
			long endTime = timesList.get(i+1);
			alarmMetaList.add(getAlarmOccurrenceMeta(startTime, endTime, alarmOccurrences, alarmMap));
		}
		alarmMetaList.add(getAlarmOccurrenceMeta(timesList.get(timesList.size() - 1), -1, null, alarmMap));
		
		return alarmMetaList;
	}
	
	static JSONArray splitEvents (DateRange range, Map<Long, EventContext> eventCreatedTimeMap,List<EventContext> allEvents) {
		
		if (allEvents.isEmpty()) {
			return null;
		}
		long nextPosibleEventIntervel = 600000l; // keeping it as 10 for now, should be calculated dynamically using controller settings
		
		Set<Long> times = new TreeSet<>(); //To get sorted set and to avoid duplication
		times.add(range.getStartTime());
		
		for (EventContext event : allEvents) {
			times.add(event.getCreatedTime());
			times.add(event.getCreatedTime()+nextPosibleEventIntervel);
		}
		times.add(range.getEndTime());
		
		JSONArray eventMetaList = new JSONArray();
		List<Long> timesList = new ArrayList<>(times);
		for (int i = 0; i < timesList.size(); i++) {
			long startTime = timesList.get(i);
			
			JSONObject json = new JSONObject();
			json.put("time", startTime);
			
			if(eventCreatedTimeMap.get(startTime) != null) {
				json.put("event", FacilioUtil.getSingleTonJsonArray(eventCreatedTimeMap.get(startTime).getId()));
			}
			
			eventMetaList.add(json);
		}
		
		return eventMetaList;
	}
	
	private static JSONObject getAlarmOccurrenceMeta (long startTime, long endTime, List<AlarmOccurrenceContext> alarmOccurrences, Map<Long, AlarmOccurrenceContext> occurrenceMap) {
		JSONObject json = new JSONObject();
		json.put("time", startTime);
		
		if (alarmOccurrences != null && !alarmOccurrences.isEmpty()) { 
			
			List<AlarmOccurrenceContext> currentOccurrences = alarmOccurrences.stream()
														.filter(a -> a.getCreatedTime() < endTime && (a.getClearedTime() == -1 || a.getClearedTime() > startTime))
														.collect(Collectors.toList());
			
			if (!currentOccurrences.isEmpty()) {
				List<Long> occurrenceIds = new ArrayList<>();
				for (int i = 0; i < currentOccurrences.size(); i++) {
					AlarmOccurrenceContext alarmOccurrence = currentOccurrences.get(i);
					occurrenceIds.add(alarmOccurrence.getId());
					
					if (i < MAX_ALARM_INFO_PER_WINDOW) {
						occurrenceMap.put(alarmOccurrence.getId(), alarmOccurrence);
					}
				}
				json.put("alarm", occurrenceIds);		// changing name to 'alarm' to match previous alarm bar response for client 
			}
		}
		
		return json;
	}

	private static JSONObject getAlarmMeta (long startTime, long endTime, List<ReadingAlarmContext> allAlarms, Map<Long, ReadingAlarmContext> alarmMap) {
		JSONObject json = new JSONObject();
		json.put("time", startTime);
		
		if (allAlarms != null && !allAlarms.isEmpty()) { 
			
			List<ReadingAlarmContext> currentAlarms = allAlarms.stream()
														.filter(a -> a.getCreatedTime() < endTime && (a.getClearedTime() == -1 || a.getClearedTime() > startTime))
														.collect(Collectors.toList());
			
			if (!currentAlarms.isEmpty()) {
				List<Long> alarmIds = new ArrayList<>();
				for (int i = 0; i < currentAlarms.size(); i++) {
					ReadingAlarmContext alarm = currentAlarms.get(i);
					alarmIds.add(alarm.getId());
					
					if (i < MAX_ALARM_INFO_PER_WINDOW) {
						alarmMap.put(alarm.getId(), alarm);
					}
				}
				json.put("alarm", alarmIds);
			}
		}
		
		return json;
	}
}
