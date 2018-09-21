package com.facilio.bmsconsole.commands;

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
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class FetchReportExtraMeta implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		JSONObject safeLimit = new JSONObject();
		
		Map<String,Map<String, List<ReadingAlarmContext>>> alarmsMap = new HashMap<>();
		
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
					
					Map<String, List<ReadingAlarmContext>> alarmProps = new HashMap<>();
					alarmProps.put(FacilioConstants.Reports.ACTUAL_DATA, alarms);
					
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
								
								alarmProps.put(reportBaseLine.getBaseLine().getName(), alarms);
							}
							
						}
					}
					alarmsMap.put(dataPoint.getName(), alarmProps);
				}
			}
		}
		context.put(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT, safeLimit);
		context.put(FacilioConstants.ContextNames.REPORT_ALARMS, alarmsMap);
		return false;
	}

}
