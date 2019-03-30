package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.SpaceAggregateOperator;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

public class TransformReportDataCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(TransformReportDataCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		String reportCallingFrom = (String) context.get(FacilioConstants.ContextNames.REPORT_CALLING_FROM);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		if(reportCallingFrom != null && reportCallingFrom.equals("card") && reportData != null) {
			
			Map<Object,Object> cardResult = new HashMap<>();
			for (ReportDataContext data : reportData ) {
				Map<String, List<Map<String, Object>>> reportProps = data.getProps();
				if (reportProps != null && !reportProps.isEmpty()) {
					for (ReportDataPointContext dataPoint : data.getDataPoints()) {
						for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
							List<Map<String, Object>> props = entry.getValue();
							
							for(Map<String, Object> prop :props) {
								Object yVal = prop.get(dataPoint.getyAxis().getField().getName());
								Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
								cardResult.put(xVal, yVal);
							}
						}
					}
				}
			}
			context.put(FacilioConstants.ContextNames.REPORT_CARD_DATA, cardResult);
		}
		
		if (reportData != null) {
			Map<String, Map<String, Map<Object, Object>>> transformedData = new HashMap<>();
			Set<Object> xValues = report.getFilters() == null ? new TreeSet<>() : new LinkedHashSet<>();
			for (ReportDataContext data : reportData ) {
				Map<String, List<Map<String, Object>>> reportProps = data.getProps();
				if (reportProps != null && !reportProps.isEmpty()) {
					for (ReportDataPointContext dataPoint : data.getDataPoints()) {
						Map<String, Map<Object, Object>> dataPointMap = new HashMap<>();
						for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
							List<Map<String, Object>> props = entry.getValue();
							Map<Object, Object> dataPoints = null;
							if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
								dataPoints = transformData(dataPoint, report.getxAggrEnum(), xValues, props, null);
							}
							else {
								dataPoints = transformData(dataPoint, report.getxAggrEnum(), xValues, props, data.getBaseLineMap().get(entry.getKey()));  
							}
							
							if (dataPoints != null) {
								dataPointMap.put(entry.getKey(), dataPoints);
							}
						}
						transformedData.put(dataPoint.getName(), dataPointMap);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.REPORT_X_VALUES, xValues);
			context.put(FacilioConstants.ContextNames.REPORT_DATA, transformedData);
		}
		return false;
	}
	
	private Object getBaseLineAdjustedXVal(Object xVal, ReportFieldContext xAxis, ReportBaseLineContext baseLine) throws Exception {
		if (baseLine != null) {
			switch (xAxis.getField().getDataTypeEnum()) {
				case DATE:
				case DATE_TIME:
					return (long) xVal + baseLine.getDiff();
				default:
					break;
			}
		}
		return xVal;
	}

	private Map<Object, Object> transformData(ReportDataPointContext dataPoint, AggregateOperator xAggr, Set<Object> xValues, List<Map<String, Object>> props, ReportBaseLineContext baseLine) throws Exception {
		if (props != null && !props.isEmpty()) {
			Map<Object, Object> dataPoints = new LinkedHashMap<>();
			for (Map<String, Object> prop : props) {
				Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
				if (xVal != null) {
					xVal = getBaseLineAdjustedXVal(xVal, dataPoint.getxAxis(), baseLine);
					xVal = formatVal(dataPoint.getxAxis().getField(), xAggr, xVal, dataPoint.isFetchResource());
					xValues.add(xVal);
					Object yVal = prop.get(dataPoint.getyAxis().getField().getName());
					if (yVal != null) { //Ignoring null values
//						if (AccountUtil.getCurrentOrg().getId() == 134) {
							LOGGER.debug("Before transform : (x, y)=>("+xVal+", "+yVal+")");
//						}
						yVal = formatVal(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum(), yVal, false);
						if (dataPoint.getGroupByFields() == null || dataPoint.getGroupByFields().isEmpty()) {
//							if (AccountUtil.getCurrentOrg().getId() == 134) {
								LOGGER.debug("After transform : (x, y)=>("+xVal+", "+yVal+")");
//							}
							dataPoints.put(xVal, yVal.toString());
						}
						else {
							Map<String, Object> currentMap = (Map<String, Object>) dataPoints.get(xVal);
							if (currentMap == null) {
								currentMap = new HashMap<>();
								dataPoints.put(xVal, currentMap);
							}
							for (int i = 0; i < dataPoint.getGroupByFields().size(); i++) {
								FacilioField field = dataPoint.getGroupByFields().get(i).getField();
								Object groupByVal = prop.get(field.getName());
								groupByVal = formatVal(field, null, groupByVal, false);
								if (i == dataPoint.getGroupByFields().size() - 1) {
									currentMap.put(groupByVal.toString(), yVal);
								}
								else {
									Map<String, Object> currentGroupMap = (Map<String, Object>) currentMap.get(groupByVal.toString());
									if (currentGroupMap == null) {
										currentGroupMap = new HashMap<>();
										currentMap.put(groupByVal.toString(), currentGroupMap);
									}
									currentMap = currentGroupMap;
								}
							}
						}
					}
				}
			}
			return dataPoints;
		}
		return null;
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private Object formatVal(FacilioField field, AggregateOperator aggr, Object val, boolean fetchResource) throws NumberFormatException, Exception {
		if (val == null) {
			return "";
		}
		
		boolean alreadyFetchedResource = false;
		if (aggr != null) {
			if (aggr instanceof DateAggregateOperator) {
				val = ((DateAggregateOperator)aggr).getAdjustedTimestamp((long) val);
			}
			else if (aggr instanceof SpaceAggregateOperator) {
				ResourceContext resource = ResourceAPI.getResource(Long.parseLong(val.toString()));
				val = resource.getName();
				alreadyFetchedResource = true;
			}
		}
		
		if (!alreadyFetchedResource && fetchResource) {
			ResourceContext resource = ResourceAPI.getResource(Long.parseLong(val.toString()));
			val = resource.getName();
		}
		
		
		switch (field.getDataTypeEnum()) {
			case DECIMAL:
				return DECIMAL_FORMAT.format(val);
			case BOOLEAN:
				if (val.toString().equals("true")) {
					return 1;
				}
				else if (val.toString().equals("false")) {
					return 0;
				}
				break;
			default:
				break;
		}
		
		return val;
	}
}
