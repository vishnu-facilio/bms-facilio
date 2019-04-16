package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants.EventFieldFactory;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.util.FacilioUtil;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class FetchReportAdditionalInfoCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(FetchReportAdditionalInfoCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report.getTypeEnum() == ReportType.READING_REPORT) {
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
					ReadingAlarmContext currentAlarm = null;
					if (showAlarms || fetchEventBar) {
						alarmId = (Long) context.get(FacilioConstants.ContextNames.ALARM_ID);
						if (alarmId != null) {
							currentAlarm = AlarmAPI.getReadingAlarmContext(alarmId);
						}
					}

					Map<Long, ReadingAlarmContext> alarmMap = showAlarms ? new HashMap<>() : null;
					List<ReadingAlarmContext> allAlarms = showAlarms ? new ArrayList<>() : null;
					for (ReportDataPointContext dp : report.getDataPoints()) {
						if (!validateFetchingOfAdditionalInfo(dp)) {
							return false; // All datapoints should be valid to fetch alarms
						}

						if (showSafeLimit) {
							reportAggrData.put(dp.getAliases().get("actual")+".safeLimit", getSafeLimit(dp));
						}

						if (showAlarms && !fetchEventBar) {
							List<Long> parentIds = getParentIds(dp);

//							if (AccountUtil.getCurrentOrg().getId() == 75) {
//								LOGGER.info("Parent IDs of alarms to be fetched : "+parentIds);
//							}

							if (parentIds != null) {
								List<ReadingAlarmContext> alarms = null;
								if (alarmId == null || alarmId == -1) {
									alarms = AlarmAPI.getReadingAlarms(parentIds, dp.getyAxis().getFieldId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), false);
								}
								else if (currentAlarm != null && currentAlarm.getReadingFieldId() == dp.getyAxis().getFieldId() && parentIds.contains(currentAlarm.getResource().getId())) {
									alarms = AlarmAPI.getReadingAlarms(currentAlarm.getEntityId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), false);
								}

//								if (AccountUtil.getCurrentOrg().getId() == 75) {
//									LOGGER.info("Fetched Alarms : "+alarms);
//								}

								if (alarms != null && !alarms.isEmpty()) {
									for (ReadingAlarmContext alarm : alarms) {
										alarm.addAdditionInfo("dataPoint", dp.getName());
										allAlarms.add(alarm);
									}
								}
							}
						}
					}

					if (showAlarms && !fetchEventBar) {
						reportAggrData.put("alarms", splitAlarms(allAlarms, report.getDateRange(), alarmMap));
						reportAggrData.put("alarmInfo", alarmMap);

						String sortAlias = (String) context.get(FacilioConstants.ContextNames.REPORT_SORT_ALIAS);
						getRecordWiseAlarms(report, csvData, allAlarms, alarmMap, sortAlias);
					}
					
					if(fetchEventBar) {
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(EventFieldFactory.getEventFields());
						
						Criteria criteria = new Criteria();
						criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("subRuleId"), readingRuleId+"", NumberOperators.EQUALS));
						criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), currentAlarm.getResource().getId()+"", NumberOperators.EQUALS));
						criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), report.getDateRange().getStartTime()+","+ report.getDateRange().getEndTime(), DateOperators.BETWEEN));
						
						List<EventContext> events = EventAPI.getEvent(criteria);
						
						if(events != null) {
							Map<Long,EventContext> eventIdMap = new HashMap<>();
							Map<Long,EventContext> eventCreationTimeMap = new HashMap<>();
							
							for(EventContext event :events) {
								eventIdMap.put(event.getId(), event);
								eventCreationTimeMap.put(event.getCreatedTime(), event);
							}
							
							reportAggrData.put("events", splitEvents( report.getDateRange(), eventCreationTimeMap,events));
							reportAggrData.put("eventInfo", eventIdMap);
						}
					}
				}
				
			}
		}
		return false;
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
