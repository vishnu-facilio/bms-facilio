package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;

public class FetchReportAdditionalInfoCommand implements Command {

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
			
			JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			if (reportData != null && !reportData.isEmpty()) {
				List<Map<String, Object>> csvData = (List<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
				Map<String, Object> reportAggrData = (Map<String, Object>) reportData.get(FacilioConstants.ContextNames.AGGR_KEY);
				
				if (reportData != null && !reportData.isEmpty()) {
					Long alarmId = null;
					ReadingAlarmContext currentAlarm = null;
					if (showAlarms) {
						alarmId = (Long) context.get(FacilioConstants.ContextNames.ALARM_ID);
						if (alarmId != null) {
							currentAlarm = AlarmAPI.getReadingAlarmContext(alarmId);
						}
					}
					
					Map<Long, ReadingAlarmContext> alarmMap = new HashMap<>();
					for (ReportDataPointContext dp : report.getDataPoints()) {
						if (showSafeLimit) {
							reportAggrData.put(dp.getAliases().get("actual")+".safeLimit", getSafeLimit(dp));
						}
						
						if (showAlarms) {
							List<Long> parentIds = getParentIds(dp);
							if (parentIds != null) {
								List<ReadingAlarmContext> alarms = null;
								if (alarmId == null) {
									alarms = AlarmAPI.getReadingAlarms(parentIds, dp.getyAxis().getFieldId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), false);
								}
								else if (currentAlarm != null && currentAlarm.getReadingFieldId() == dp.getyAxis().getFieldId() && parentIds.contains(currentAlarm.getResource().getId())) {
									alarms = AlarmAPI.getReadingAlarms(currentAlarm.getEntityId(), report.getDateRange().getStartTime(), report.getDateRange().getEndTime(), false);
								}
								
								if (alarms != null && !alarms.isEmpty()) {
									reportAggrData.put(dp.getAliases().get("actual")+".alarms", splitAlarms(alarms, report.getDateRange(), alarmMap));
								}
							}
						}
					}
					
					if (showAlarms) {
						reportAggrData.put("alarmInfo", alarmMap);
					}
				}
			}
		}
		return false;
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
			Map<Integer, Condition> conditions = dp.getCriteria().getConditions();
			
			for(Integer key : conditions.keySet()) {
				Condition condition = conditions.get(key);
				if(condition.getFieldName().equals("parentId")) {
					parentIds = Collections.singletonList(Long.parseLong(condition.getValue()));
				}
			}
		}
		return parentIds;
	}
	
	private static final int MAX_ALARM_INFO_PER_WINDOW = 2;
	private JSONArray splitAlarms (List<ReadingAlarmContext> alarms, DateRange range, Map<Long, ReadingAlarmContext> alarmMap) {
		Set<Long> times = new TreeSet<>();
		
		for (ReadingAlarmContext alarm : alarms) {
			if (alarm.getCreatedTime() < range.getStartTime()) {
				times.add(range.getStartTime());
			}
			else {
				times.add(alarm.getCreatedTime());
			}
			
			if (alarm.getClearedTime() == -1 || alarm.getCreatedTime() > range.getEndTime()) {
				times.add(range.getEndTime());
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
			
			List<ReadingAlarmContext> currentAlarms = alarms.stream()
															.filter(a -> a.getCreatedTime() < endTime && (a.getClearedTime() == -1 || a.getClearedTime() >= startTime))
															.collect(Collectors.toList());
			alarmMetaList.add(getAlarmMeta(startTime, currentAlarms, alarmMap));
		}
		alarmMetaList.add(getAlarmMeta(timesList.get(timesList.size() - 1), null, alarmMap));
		
		return alarmMetaList;
	}

	private JSONObject getAlarmMeta (long time, List<ReadingAlarmContext> alarms, Map<Long, ReadingAlarmContext> alarmMap) {
		JSONObject json = new JSONObject();
		json.put("time", time);
		
		if (alarms != null && !alarms.isEmpty()) { 
			List<Long> alarmIds = new ArrayList<>();
			for (int i = 0; i < alarms.size(); i++) {
				ReadingAlarmContext alarm = alarms.get(i);
				alarmIds.add(alarm.getId());
				
				if (i < MAX_ALARM_INFO_PER_WINDOW) {
					alarmMap.put(alarm.getId(), alarm);
				}
			}
			json.put("alarm", alarmIds);
		}
		
		return json;
	}
}
