package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReportAlarmContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class FetchReportExtraMeta implements Command {
	private static final Logger LOGGER = LogManager.getLogger(FetchReportExtraMeta.class.getName());
	private List<Long> getLongList (List list) {
		if (list != null) {
			return (List<Long>) list.stream().map(x -> (Long.parseLong(x.toString()))).collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if ((report.getType() == ReportType.REGRESSION_REPORT.getValue() || report.getTypeEnum() == ReportType.READING_REPORT) && report.getFilters() == null) { //Temp fix
			List<Object> xValues = (List<Object>) context.get(FacilioConstants.ContextNames.REPORT_X_VALUE_LIST);
			List<Long> globalParentIds = null;
			if (xValues != null && !xValues.isEmpty()) {
				globalParentIds = getLongList(xValues);
				
			}
			if (AccountUtil.getCurrentOrg().getId() == 88) {
				LOGGER.info("Fetching extra meta : "+globalParentIds);
			}
			
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
					
					if(isAlarmEnabled(report.getChartState())) {
						List<Long> parentIds = null;
						if (globalParentIds != null && !globalParentIds.isEmpty()) {
							parentIds = globalParentIds;
						}
						else {
							parentIds = getLongList((List) dataPoint.getMeta(FacilioConstants.ContextNames.PARENT_ID_LIST));
							
							//The following is for backward compatibility
							if(parentIds == null && dataPoint.getCriteria() != null && !dataPoint.getCriteria().getConditions().isEmpty()) {
								Map<String, Condition> conditions = dataPoint.getCriteria().getConditions();
								
								for(String key : conditions.keySet()) {
									Condition condition = conditions.get(key);
									if(condition.getFieldName().equals("parentId")) {
										parentIds = Collections.singletonList(Long.parseLong(condition.getValue()));
									}
								}
							}
						}
						
						if (AccountUtil.getCurrentOrg().getId() == 88) {
							LOGGER.info("Fetching extra meta - parentIds : "+parentIds);
						}
						
						if(report.getDateRange() != null && parentIds != null && !parentIds.isEmpty()) {
							
							long alarmId = -1;
							if(context.get(FacilioConstants.ContextNames.ALARM_ID) != null) {
								alarmId = (long) context.get(FacilioConstants.ContextNames.ALARM_ID);
							}
							
							Map<String, List<ReadingAlarmContext>> alarmProps = new HashMap<>();
							long fieldId = dataPoint.getyAxis().getFieldId();
							
							for (Long parentId : parentIds) {
								List<ReadingAlarmContext> alarms = AlarmAPI.getReadingAlarms(Collections.singletonList(parentId), -1l, fieldId,report.getDateRange().getStartTime(),report.getDateRange().getEndTime(),false);
								alarms = filterAlarmAndGetList(alarms,alarmId);
								for(ReadingAlarmContext alarm :alarms) {
									alarm.setReportMeta(dataPoint.getName()+"_"+FacilioConstants.Reports.ACTUAL_DATA);
								}
								alarmProps.put(FacilioConstants.Reports.ACTUAL_DATA, alarms);
								allAlarms.addAll(alarms);
								if (AccountUtil.getCurrentOrg().getId() == 88) {
									LOGGER.info("Fetching extra meta - alarmProps : "+alarmProps);
								}
							}
							alarmsMap.put(dataPoint.getName(), alarmProps);
						}
					}
				}
			}
			
			List<ReportAlarmContext> reportAlarmContextList = getReportAlarms(allAlarms,report.getDateRange());
			
			if (AccountUtil.getCurrentOrg().getId() == 88) {
				LOGGER.info("Fetching extra meta - alapmsMap : "+alarmsMap);
			}
			
			context.put(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT, safeLimit);
			context.put(FacilioConstants.ContextNames.REPORT_ALARMS, alarmsMap);
			context.put(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT, reportAlarmContextList);
		}
		return false;
	}

	private List<ReadingAlarmContext> filterAlarmAndGetList(List<ReadingAlarmContext> alarms, long alarmId) throws Exception {
		
		List<ReadingAlarmContext> newAlarmList = new ArrayList<>();
		if(alarmId > 0) {
			AlarmContext currentAlarm = AlarmAPI.getAlarm(alarmId);
			for(ReadingAlarmContext alarm : alarms) {
				if(alarm.getEntityId() == currentAlarm.getEntityId()) {
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
		
		if(dateRange.getEndTime() > DateTimeUtil.getCurrenTime()) {
			dateRange.setEndTime(DateTimeUtil.getCurrenTime());
		}
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
	
	public boolean isAlarmEnabled(String chartStateJsonString) {
		try {
			if(chartStateJsonString == null) {
				return Boolean.TRUE;
			}
			JSONObject chartStateSettingJson = getChartStateSetting(chartStateJsonString);
			return (boolean) chartStateSettingJson.get("alarm");
		}
		catch(Exception e) {
			LOGGER.log(Level.ERROR, "Error parsing the string - "+chartStateJsonString);
		}
		return Boolean.FALSE;
	}
	
	public JSONObject getChartStateSetting(String chartStateJsonString) throws ParseException {
		JSONObject chartStateJson = getchartStateJson(chartStateJsonString);
		JSONObject chartStateSettingJson = (JSONObject) chartStateJson.get("settings");
		return chartStateSettingJson;
	}
	
	public JSONObject getchartStateJson(String chartStateJsonString) throws ParseException {
		
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(chartStateJsonString);
		return obj;
	}
}
