package com.facilio.bmsconsole.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.time.DateRange;


public class FormatHeatMapDataCommand extends FacilioCommand {
	
	private static final Logger LOGGER = Logger.getLogger(FormatHeatMapDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		long start=System.currentTimeMillis();
		
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);

		String chartType = null; 
		if (reportContext.getChartState() != null && reportContext.getChartState() != "null" && !"".equals(reportContext.getChartState())) {
			JSONObject jobj = (JSONObject) new JSONParser().parse(reportContext.getChartState());
			if(jobj != null) {
			chartType = (String) jobj.get("type");
			}
		}
		
		
		if((reportContext.getAnalyticsType() == 3 && (reportContext.getType() == 1 || reportContext.getType() == 4))|| ("heatmap".equalsIgnoreCase(chartType))) {
			Collection<Map<String, Object>> data = (Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY);
			
			DateRange range = reportContext.getDateRange();
			SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy MM dd HH");
			sdf.setTimeZone(TimeZone.getTimeZone(AccountUtil.getCurrentAccount().getTimeZone()));
			Long startTime=range.getStartTime();
			Long endTime=range.getEndTime();
			List<Map<String, Object>>  violatedReadings = new ArrayList<>();
			List<Map<String, Object>> heatMapData = new ArrayList<>();
			long timeStep = endTime-startTime;

			Boolean isV2Analytics = (Boolean) context.get("isV2Analytics");
			if(isV2Analytics)
			{
				V2ReportContext report_v2 = context.get("report_v2") != null ? (V2ReportContext) context.get("report_v2") : (V2ReportContext) context.get("v2_report");
				if(report_v2 != null && report_v2.getMeasures().size() == 1)
				{
					V2MeasuresContext measure = report_v2.getMeasures().get(0);
					if(measure.getHmAggr().equals("hours"))
					{
						timeStep = 3600000;
					}
					else if(measure.getHmAggr().equals("days") || measure.getHmAggr().equals("weeks")){
						timeStep = 86400000;
					}
				}
			}
			else
			{
				if((reportContext.getAnalyticsType() == 3 && (reportContext.getType() == 1 || reportContext.getType() == 4))) {
					violatedReadings = getViolatedReadings(reportContext,startTime,endTime);
				}
				if(reportContext.getxAggr() == 20) {
					timeStep = 3600000;
				}
				else if(reportContext.getxAggr() == 12 || reportContext.getxAggr() == 11) {
					sdf = new java.text.SimpleDateFormat("yyyy MM dd");
					timeStep = 86400000;
				}
			}

			HashMap reportDataMap= new HashMap();
			for(Map<String, Object> record : data) {
				Date recordDate = new java.util.Date(((long) record.get(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS)));
				record.put("Y", record.get(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS));
				reportDataMap.put(sdf.format(recordDate), record);
				}
			
			HashMap markedDataMap= new HashMap();
			if(reportContext.getAnalyticsType() == 3 && (reportContext.getType() == 1 || reportContext.getType() == 4)) {
			for(Map<String, Object> record : violatedReadings) {
				Date recordDate = new java.util.Date(((long) record.get("ttime")));
				record.put("X", record.get("ttime"));
				record.put("Y", record.get("ttime"));
				record.put("violated_value",record.get("value"));
				markedDataMap.put(record.get("label"), record);
				}
			}
			long loopstart=System.currentTimeMillis();
			
			while(startTime<=endTime) {
				HashMap mapData= new HashMap();
				mapData.put("X", startTime);
				mapData.put("Y",startTime);
				Date date = new java.util.Date(startTime);
				if(reportContext.getAnalyticsType() == 3 && (reportContext.getType() == 1 || reportContext.getType() == 4)) {
				if(reportDataMap.containsKey(sdf.format(date)) && !markedDataMap.containsKey(sdf.format(date))) {
					mapData=(HashMap) reportDataMap.get(sdf.format(date));
				}
				else if(markedDataMap.containsKey(sdf.format(date))) {
					mapData=(HashMap) markedDataMap.get(sdf.format(date));
				}
				}
				else if(reportContext.getType() == 2){
					if(reportDataMap.containsKey(sdf.format(date))) {
						mapData=(HashMap) reportDataMap.get(sdf.format(date));
					}
				}
				startTime=startTime+timeStep;
				heatMapData.add(mapData);
				}
			reportData.put("heatMapData", heatMapData);
			
			long loopend=System.currentTimeMillis()- loopstart;
			long commandend=System.currentTimeMillis()-start;

			LOGGER.info("time taken for loop  -->  "+loopend);
			LOGGER.info("time taken for command  -->  "+commandend);
			
		}
		
		
		return false;
	}
	
	private List<Map<String, Object>> getViolatedReadings(ReportContext reportContext, Long startTime, Long endTime) throws Exception {		// report functions
		
		
		List<Integer> markType = new ArrayList<>();
		if (reportContext.getxAggr() == DateAggregateOperator.DAYSOFMONTH.getValue() || reportContext.getxAggr() == DateAggregateOperator.FULLDATE.getValue()) {
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_DAILY_VIOLATION.getValue());
			markType.add(MarkedReadingContext.MarkType.DECREMENTAL_VALUE.getValue());
		}
		else if (reportContext.getxAggr() == DateAggregateOperator.HOURSOFDAY.getValue() || reportContext.getxAggr() == DateAggregateOperator.HOURSOFDAYONLY.getValue()) {
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_HOURLY_VIOLATION.getValue());
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_DAILY_VIOLATION.getValue());
			markType.add(MarkedReadingContext.MarkType.DECREMENTAL_VALUE.getValue());
		}
		
		List<Long> timeRange = new ArrayList<>();
		timeRange.add(startTime);
		timeRange.add(endTime);
		
		Criteria criteria = new Criteria(); 
		if(timeRange!=null && !timeRange.isEmpty()) {
			if (timeRange.size() == 1) {
				Integer operatorId = Integer.parseInt(timeRange.get(0).toString());
				Condition condition = new Condition();
				condition.setFieldName("TTIME");
				condition.setColumnName("TTIME");
				condition.setOperatorId(operatorId);
				
				criteria.addAndCondition(condition);
			}
			else {
				criteria.addAndCondition(CriteriaAPI.getCondition("TTIME","TTIME", 
						StringUtils.join(timeRange, ","), DateOperators.BETWEEN));
			}
		}
		
		if(markType!=null && !markType.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("MARK_TYPE","MARK_TYPE", 
					StringUtils.join(markType,","), NumberOperators.EQUALS));
		}
		
		List<ReportDataPointContext> dpList=reportContext.getDataPoints();
		ReportDataPointContext dp=dpList.get(0);
		List<Long> parentIds =(List<Long>) dp.getMeta("parentIds");
		
		List<Long> deviceList = new ArrayList<>();
		deviceList.add(parentIds.get(0));
		if(deviceList!=null && !deviceList.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_ID","RESOURCE_ID", 
					StringUtils.join(deviceList,","), NumberOperators.EQUALS));
		}
		
		List<Long> fieldList = new ArrayList<>();
		fieldList.add(dp.getyAxis().getFieldId());
		if(fieldList!=null && !fieldList.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("FIELD_ID","FIELD_ID", 
					StringUtils.join(fieldList,","), NumberOperators.EQUALS));
			}
		
		List<Long> moduleList = new ArrayList<>();
		moduleList.add(dp.getyAxis().getModuleId());
		if(moduleList!=null && !moduleList.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","MODULEID", 
					StringUtils.join(moduleList,","), NumberOperators.EQUALS));
			}
		
		FacilioField label = new FacilioField();
		label.setColumnName("TTIME");
		label.setName("label");
		label = reportContext.getxAggrEnum().getSelectField(label);
		
		FacilioField value = new FacilioField();
		value.setColumnName("ACTUAL_VALUE");
		value.setName("value");
		value = NumberAggregateOperator.SUM.getSelectField(value);
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(label);
		selectFields.add(value);
		
		FacilioModule module = ModuleFactory.getMarkedReadingModule();
		
		selectFields.add(NumberAggregateOperator.MAX.getSelectField(FieldFactory.getField("ttime", "TTIME", module, FieldType.NUMBER)));
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(ModuleFactory.getMarkedReadingModule().getTableName());
		if(!criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}
		builder.groupBy("label");
		
		List<Map<String, Object>> markedReadings = builder.get();
		
		LOGGER.severe("marked readings builder--->"+builder);
		return markedReadings;
		
	}
}