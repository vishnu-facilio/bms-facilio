package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
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
					
					Map<String, List<ReadingAlarmContext>> alarmProps = new HashMap<>();
					
					JSONObject filterJson = new JSONObject();
					
					Long fieldId = dataPoint.getyAxis().getFieldId();
					
					JSONObject fieldJson = new JSONObject();
					fieldJson.put("operatorId", NumberOperators.EQUALS.getOperatorId());
					JSONArray value = new JSONArray();
					value.add(""+fieldId);
					fieldJson.put("value", value);
					
					filterJson.put("readingFieldId", fieldJson);
					
					JSONObject resourceJson = new JSONObject();
					resourceJson.put("operatorId", PickListOperators.IS.getOperatorId());
					
					value = new JSONArray();
					value.add(""+parentId);
					
					resourceJson.put("value", value);
					
					filterJson.put("resource", resourceJson);
					
					JSONObject timeRangeJson = new JSONObject();
					timeRangeJson.put("operatorId", DateOperators.BETWEEN.getOperatorId());
					
					value = new JSONArray();
					
					value.add(report.getDateRange().getStartTime()+"");
					value.add(report.getDateRange().getEndTime()+"");
					
					timeRangeJson.put("value", value);
					
					filterJson.put("startTime", timeRangeJson);
					
					AlarmAction alarmAction = new AlarmAction();
					alarmAction.setFilters(filterJson.toJSONString());
					
					alarmAction.fetchReadingAlarms();
					
					List<ReadingAlarmContext> alarms = alarmAction.getReadingAlarms();
					
					for(ReadingAlarmContext alarm :alarms) {
						alarm.setReportMeta(dataPoint.getName()+"_"+FacilioConstants.Reports.ACTUAL_DATA);
					}
					alarmProps.put(FacilioConstants.Reports.ACTUAL_DATA, alarms);
					
					allAlarms.addAll(alarms);
					
					if (report.getBaseLines() != null && !report.getBaseLines().isEmpty()) {
						for (ReportBaseLineContext reportBaseLine : report.getBaseLines()) {

							if(reportBaseLine.getBaseLineRange() != null) {
								
								timeRangeJson = new JSONObject();
								timeRangeJson.put("operatorId", DateOperators.BETWEEN.getOperatorId());
								
								value = new JSONArray();
								
								value.add(reportBaseLine.getBaseLineRange().getStartTime()+"");
								value.add(reportBaseLine.getBaseLineRange().getEndTime()+"");
								
								timeRangeJson.put("value", value);
								filterJson.remove("startTime");
								filterJson.put("startTime", timeRangeJson);
								
								alarmAction.setFilters(filterJson.toJSONString());
								
								alarmAction.fetchReadingAlarms();
								
								alarms = alarmAction.getReadingAlarms();
								
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
		
		List<ReportAlarmContext> reportAlarmContextList = getReportAlarms(allAlarms);
		
		context.put(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT, safeLimit);
		context.put(FacilioConstants.ContextNames.REPORT_ALARMS, alarmsMap);
		context.put(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT, reportAlarmContextList);
		return false;
	}

	private List<ReportAlarmContext> getReportAlarms(List<ReadingAlarmContext> allAlarms) {
		List<Long> alarmTime = new ArrayList<>();
		
		boolean isCurrentTimeAdded = false;
		for(ReadingAlarmContext alarm :allAlarms) {
			if(alarm.getStartTime() > 0) {
				if(!alarmTime.contains(alarm.getStartTime())) {
					alarmTime.add(alarm.getStartTime());
				}
			}
			if(alarm.getEndTime() > 0) {
				if(!alarmTime.contains(alarm.getEndTime())) {
					alarmTime.add(alarm.getEndTime());
				}
			}
			else if(!isCurrentTimeAdded) {
				isCurrentTimeAdded = true;
				alarmTime.add(DateTimeUtil.getCurrenTime());
			}
		}
		Collections.sort(alarmTime);
		
		
		List<ReportAlarmContext> reportAlarmContextList = new ArrayList<>();
		
		for(int i=0;i<alarmTime.size()-1;i++) {
			
			ReportAlarmContext reportAlarmContext = new ReportAlarmContext();
			reportAlarmContext.setStartTime(alarmTime.get(i));
			reportAlarmContext.setEndTime(alarmTime.get(i+1));
			
			reportAlarmContextList.add(reportAlarmContext);
		}
		
		for(ReadingAlarmContext alarm :allAlarms) {
			if(alarm.getStartTime() > 0) {
				for(ReportAlarmContext reportAlarmContext :reportAlarmContextList) {
					
					if(reportAlarmContext.getStartTime() <= alarm.getStartTime() && reportAlarmContext.getStartTime() < alarm.getEndTime()) {
						reportAlarmContext.addOrder();
						reportAlarmContext.addAlarmContext(alarm);
					}
				}
			}
		}
		return reportAlarmContextList;
	}
}
