package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;

public class ConstructReportDataCommand implements Command {

	private static final String DEFAULT_X_ALIAS = "X";
	
	private List<Map<String, Object>> initList() { //In case we wanna implement a sorted list
		return new ArrayList<>();
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		List<Map<String, Object>> transformedData = initList();
		Map<String, Object> intermediateData = new HashMap<>();
		for (ReportDataContext data : reportData ) {
			Map<String, List<Map<String, Object>>> reportProps = data.getProps();
			if (reportProps != null && !reportProps.isEmpty()) {
				for (ReportDataPointContext dataPoint : data.getDataPoints()) {
					for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
						List<Map<String, Object>> props = entry.getValue();
						if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
							constructData(report, dataPoint, props, null, transformedData, intermediateData);
						}
						else {
							constructData(report, dataPoint, props, data.getBaseLineMap().get(entry.getKey()), transformedData, intermediateData);
						}
					}
				}
			}
		}
		JSONObject data = new JSONObject();
		data.put(FacilioConstants.ContextNames.DATA_KEY, transformedData);
		context.put(FacilioConstants.ContextNames.REPORT_SORT_ALIAS, getxAlias(report));
		context.put(FacilioConstants.ContextNames.REPORT_DATA, data);
		return false;
	}
	
	private void constructData(ReportContext report, ReportDataPointContext dataPoint, List<Map<String, Object>> props, ReportBaseLineContext baseLine, List<Map<String, Object>> transformedData, Map<String, Object> directHelperData) throws Exception {
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
				if (xVal != null) {
					xVal = getBaseLineAdjustedXVal(xVal, dataPoint.getxAxis(), baseLine);
					Object formattedxVal = formatVal(dataPoint.getxAxis().getField(), report.getxAggrEnum(), xVal, null, false);
					Object yVal = prop.get(dataPoint.getyAxis().getField().getName());
					if (yVal != null) {
						yVal = formatVal(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum(), yVal, xVal, dataPoint.isHandleEnum());
						
						StringJoiner key = new StringJoiner("|");
						key.add(formattedxVal.toString());
						Map<String, Object> data = null;
						if (dataPoint.getGroupByFields() != null && !dataPoint.getGroupByFields().isEmpty()) {
							data = new HashMap<>();
							for (ReportGroupByField groupBy : dataPoint.getGroupByFields()) {
								FacilioField field = groupBy.getField();
								Object groupByVal = prop.get(field.getName());
								groupByVal = formatVal(field, null, groupByVal, xVal, dataPoint.isHandleEnum());
								data.put(groupBy.getAlias(), groupByVal);
								key.add(groupBy.getAlias()+"_"+groupByVal.toString());
							}
						}
						constructAndAddData(key.toString(), data, formattedxVal, yVal, getyAlias(dataPoint, baseLine), report, dataPoint, transformedData, directHelperData);
					}
				}
			}
		}
	}
	
	private void constructAndAddData(String key, Map<String, Object> existingData, Object xVal, Object yVal, String yAlias, ReportContext report, ReportDataPointContext dataPoint, List<Map<String, Object>> transformedData, Map<String, Object> intermediateData) {
		Map<String, Object> data = (Map<String, Object>) intermediateData.get(key);
		if (data == null) {
			data = existingData == null ? new HashMap<>() : existingData;
			data.put(getxAlias(report), xVal);
			intermediateData.put(key, data);
			transformedData.add(data);
		}
		
		if (dataPoint.isHandleEnum()) {
			List<SimpleEntry<Long, Integer>> value = (List<SimpleEntry<Long, Integer>>) data.get(yAlias);
			if (value == null) {
				value = new ArrayList<>();
				data.put(yAlias, yVal);
			}
			value.add((SimpleEntry<Long, Integer>) yVal);
		}
		else {
			data.put(yAlias, yVal);
		}
	}
	
	private String getyAlias(ReportDataPointContext dataPoint, ReportBaseLineContext baseLine) {
		return baseLine == null ? dataPoint.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA) : dataPoint.getAliases().get(baseLine.getBaseLine().getName());
	}
	
	private String getxAlias(ReportContext report) {
		return report.getxAlias() == null ? DEFAULT_X_ALIAS : report.getxAlias();
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
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private Object formatVal(FacilioField field, AggregateOperator aggr, Object val, Object actualxVal, boolean handleEnum) {
		if (val == null) {
			return "";
		}
		
		if (aggr != null && aggr instanceof DateAggregateOperator) {
			val = ((DateAggregateOperator)aggr).getAdjustedTimestamp((long) val);
		}
		
		switch (field.getDataTypeEnum()) {
			case DECIMAL:
				val = DECIMAL_FORMAT.format(val);
				break;
			case BOOLEAN:
				if (val.toString().equals("true")) {
					val = 1;
				}
				else if (val.toString().equals("false")) {
					val = 0;
				}
				if (handleEnum && actualxVal != null) {
					val = new SimpleEntry<Long, Integer>((Long)actualxVal, (Integer) val);
				}
				break;
			case ENUM:
				if (handleEnum && actualxVal != null) {
					val = new SimpleEntry<Long, Integer>((Long)actualxVal, (Integer) val);
				}
				break;
			default:
				break;
		}
		return val;
	}

}
