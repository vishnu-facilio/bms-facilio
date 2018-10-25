package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.AlarmAction;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReportAlarmContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class FetchReportExtraMeta implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		if (report.getDataPoints() == null || report.getDataPoints().isEmpty()) {
			return false;
		}
		
		JSONObject safeLimit = new JSONObject();
		
		Map<String,Map<String, List<ReadingAlarmContext>>> alarmsMap = new HashMap<>();
		
		List<ReadingAlarmContext> allAlarms = new ArrayList<>();
		
		for(ReportDataPointContext dataPoint :report.getDataPoints()) {
			
			if(dataPoint.getyAxis() != null && dataPoint.getyAxis().getFieldId() > 0 ) {
				
				// safe limit starts
				Pair<Double, Double> safelimitPair = CommonCommandUtil.getSafeLimitForField(dataPoint.getyAxis().getFieldId());
				
				JSONArray safeLimtJson = new JSONArray();
				safeLimtJson.add(safelimitPair.getKey());
				safeLimtJson.add(safelimitPair.getValue());
				
				safeLimit.put(dataPoint.getName(), safeLimtJson);
				// safe limit ends
				Long parentId = null;
				if(dataPoint.getCriteria() != null && !dataPoint.getCriteria().getConditions().isEmpty()) {
					
					Map<Integer, Condition> conditions = dataPoint.getCriteria().getConditions();
					
					for(Integer key : conditions.keySet()) {
						Condition condition = conditions.get(key);
						if(condition.getFieldName().equals("parentId")) {
							
							parentId = Long.parseLong(condition.getValue());
						}
					}
				}
				if(report.getDateRange() != null && parentId != null) {
					
					long alarmId = -1;
					if(context.get(FacilioConstants.ContextNames.ALARM_ID) != null) {
						alarmId = (long) context.get(FacilioConstants.ContextNames.ALARM_ID);
					}
					
					Map<String, List<ReadingAlarmContext>> alarmProps = new HashMap<>();
					
					
					Long fieldId = dataPoint.getyAxis().getFieldId();
					
					List<ReadingAlarmContext> alarms = AlarmAPI.getReadingAlarms(parentId,fieldId,report.getDateRange().getStartTime(),report.getDateRange().getEndTime(),false);
					
					alarms = filterAlarmAndGetList(alarms,alarmId);
					
					for(ReadingAlarmContext alarm :alarms) {
						alarm.setReportMeta(dataPoint.getName()+"_"+FacilioConstants.Reports.ACTUAL_DATA);
					}
					alarmProps.put(FacilioConstants.Reports.ACTUAL_DATA, alarms);
					
					allAlarms.addAll(alarms);
					
					if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
						for (ReportBaseLineContext reportBaseLine : report.getBaseLines()) {

							if(reportBaseLine.getBaseLineRange() != null) {
								
								alarms = AlarmAPI.getReadingAlarms(parentId,fieldId,reportBaseLine.getBaseLineRange().getStartTime(),reportBaseLine.getBaseLineRange().getEndTime(),false);
								
								alarms = filterAlarmAndGetList(alarms,alarmId);
								
								for(ReadingAlarmContext alarm :alarms) {
									alarm.setReportMeta(dataPoint.getName()+"_"+reportBaseLine.getBaseLine().getName());
								}
								
								alarmProps.put(reportBaseLine.getBaseLine().getName(), alarms);
								allAlarms.addAll(alarms);
							}
						}
					}
					alarmsMap.put(dataPoint.getName(), alarmProps);
				}
			}
		}
		
		List<ReportAlarmContext> reportAlarmContextList = getReportAlarms(allAlarms,report.getDateRange());
		
		context.put(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT, safeLimit);
		context.put(FacilioConstants.ContextNames.REPORT_ALARMS, alarmsMap);
		context.put(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT, reportAlarmContextList);
		return false;
	}

	private List<ReadingAlarmContext> filterAlarmAndGetList(List<ReadingAlarmContext> alarms, long alarmId) {
		
		List<ReadingAlarmContext> newAlarmList = new ArrayList<>();
		
		if(alarmId > 0) {
			for(ReadingAlarmContext alarm : alarms) {
				if(alarm.getId() == alarmId) {
					newAlarmList.add(alarm);
				}
			}
		}
		else {
			newAlarmList = alarms;
		}
		return newAlarmList;
	}

	private List<ReportAlarmContext> getReportAlarms(List<ReadingAlarmContext> allAlarms, DateRange dateRange) {
		List<Long> alarmTime = new ArrayList<>();
		
		boolean isCurrentTimeAdded = false;
		for(ReadingAlarmContext alarm :allAlarms) {
			if(alarm.getCreatedTime() > 0) {
				if(!alarmTime.contains(alarm.getCreatedTime()) ) {
					alarmTime.add(alarm.getCreatedTime());
				}
			}
			if(alarm.getClearedTime() > 0) {
				if(!alarmTime.contains(alarm.getClearedTime()) ) {
					alarmTime.add(alarm.getClearedTime());
				}
			}
			else if(!isCurrentTimeAdded) {
				isCurrentTimeAdded = true;
				alarmTime.add(dateRange.getEndTime());
			}
		}
		if(!alarmTime.contains(dateRange.getStartTime()) ) {
			alarmTime.add(dateRange.getStartTime());
		}
		if(!alarmTime.contains(dateRange.getEndTime()) ) {
			alarmTime.add(dateRange.getEndTime());
		}
		Collections.sort(alarmTime);
		
		List<ReportAlarmContext> reportAlarmContextList = new ArrayList<>();
		
		for(int i=0;i<alarmTime.size()-1;i++) {
			
			if(alarmTime.get(i) < dateRange.getStartTime()) {
				continue;
			}
			if(alarmTime.get(i) >= dateRange.getEndTime()) {
				break;
			}
			ReportAlarmContext reportAlarmContext = new ReportAlarmContext();
			reportAlarmContext.setStartTime(alarmTime.get(i));
			reportAlarmContext.setEndTime(alarmTime.get(i+1));
			
			reportAlarmContextList.add(reportAlarmContext);
		}
		
		for(ReportAlarmContext reportAlarmContext :reportAlarmContextList) {
			
			for(ReadingAlarmContext alarm :allAlarms) {
				
				if(alarm.getCreatedTime() > 0) {
					
					Long clearedTime = null;
					if(alarm.getClearedTime() > 0) {
						clearedTime = alarm.getClearedTime() < dateRange.getEndTime() ? alarm.getClearedTime() : dateRange.getEndTime();  
					}
					else {
						clearedTime = dateRange.getEndTime();
					}
					
					if(reportAlarmContext.getStartTime() >= alarm.getCreatedTime() && reportAlarmContext.getStartTime() < clearedTime) {
						reportAlarmContext.addOrder();
						reportAlarmContext.addAlarmContext(alarm);
					}
				}
			}
			
		}
		List<ReportAlarmContext> reportAlarmContextListnew = new ArrayList<>();
		for(ReportAlarmContext reportAlarmContext :reportAlarmContextList) {
			if(reportAlarmContext.getOrder() > 0) {
				reportAlarmContextListnew.add(reportAlarmContext);
			}
		}
		return reportAlarmContextListnew;
	}
}
