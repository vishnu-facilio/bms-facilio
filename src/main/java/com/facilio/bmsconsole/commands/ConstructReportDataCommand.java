package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportAxisContext;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;

public class ConstructReportDataCommand implements Command {

	private static final String DEFAULT_X_ALIAS = "X";
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		List<Map<String, Object>> transformedData = new ArrayList<> ();
		Map<String, Object> intermediateData = new HashMap<>();
		for (ReportDataContext data : reportData ) {
			Map<String, List<Map<String, Object>>> reportProps = data.getProps();
			if (reportProps != null && !reportProps.isEmpty()) {
				for (ReportDataPointContext dataPoint : data.getDataPoints()) {
					for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
						List<Map<String, Object>> props = entry.getValue();
						constructData(report, dataPoint, props, data.getBaseLineMap().get(entry.getKey()), transformedData, intermediateData);
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.REPORT_DATA, transformedData);
		return false;
	}
	
	private void constructData(ReportContext report, ReportDataPointContext dataPoint, List<Map<String, Object>> props, ReportBaseLineContext baseLine, List<Map<String, Object>> transformedData, Map<String, Object> directHelperData) throws Exception {
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
				if (xVal != null) {
					xVal = getBaseLineAdjustedXVal(xVal, dataPoint.getxAxis(), baseLine);
					xVal = formatVal(dataPoint.getxAxis().getField(), dataPoint.getxAxis().getAggrEnum(), xVal);
					Object yVal = prop.get(dataPoint.getyAxis().getField().getName());
					yVal = formatVal(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum(), yVal);
					if (dataPoint.getGroupByFields() == null || dataPoint.getGroupByFields().isEmpty()) {
						constructAndAddData(xVal, yVal, getyAlias(dataPoint, baseLine), report, transformedData, directHelperData);
					}
					else {
//						Map<String, Object> data = new HashMap<>();
//						data.put(getyAlias(dataPoint, baseLine), yVal);
//						for (ReportGroupByField groupBy : dataPoint.getGroupByFields()) {
//							FacilioField field = groupBy.getField();
//							Object groupByVal = prop.get(field.getName());
//							groupByVal = formatVal(field, null, groupByVal);
//							data.put(groupBy.getAlias(), groupByVal);
//						}
					}
				}
			}
		}
	}
	
	private void constructAndAddData(Object xVal, Object yVal, String yAlias, ReportContext report, List<Map<String, Object>> transformedData, Map<String, Object> intermediateData) {
		Map<String, Object> data = (Map<String, Object>) intermediateData.get(xVal.toString());
		if (data == null) {
			data = new HashMap<>();
			data.put(getxAlias(report), xVal);
			intermediateData.put(xVal.toString(), data);
			transformedData.add(data);
		}
		data.put(yAlias, yVal);
	}
	
	private String getyAlias(ReportDataPointContext dataPoint, ReportBaseLineContext baseLine) {
		return baseLine == null ? dataPoint.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA) : dataPoint.getAliases().get(baseLine.getBaseLine().getName());
	}
	
	private String getxAlias(ReportContext report) {
		return report.getxAlias() == null ? DEFAULT_X_ALIAS : report.getxAlias();
	}
	
	private Object getBaseLineAdjustedXVal(Object xVal, ReportAxisContext xAxis, ReportBaseLineContext baseLine) throws Exception {
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
	private Object formatVal(FacilioField field, AggregateOperator aggr, Object val) {
		if (val == null) {
			return "";
		}
		
		if (aggr != null && aggr instanceof DateAggregateOperator) {
			val = ((DateAggregateOperator)aggr).getAdjustedTimestamp((long) val);
		}
		
		if (field.getDataTypeEnum() == FieldType.DECIMAL) {
			return DECIMAL_FORMAT.format(val);
		}
		return val;
	}

}
