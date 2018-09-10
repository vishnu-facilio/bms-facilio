package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;

public class TransformReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		Map<String, Map<String, Map<Object, Object>>> transformedData = new HashMap<>();
		Set<Object> xValues = new LinkedHashSet<>();
		for (ReportDataContext data : reportData ) {
			Map<String, List<Map<String, Object>>> reportProps = data.getProps();
			if (reportProps != null && !reportProps.isEmpty()) {
				for (ReportDataPointContext dataPoint : data.getDataPoints()) {
					Map<String, Map<Object, Object>> dataPointMap = new HashMap<>();
					for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
						List<Map<String, Object>> props = entry.getValue();
						Map<Object, Object> dataPoints = null;
						if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
							dataPoints = transformData(dataPoint, xValues, props);
						}
						else {
							dataPoints = transformData(dataPoint, null, props); //xValues are ignored for baseline 
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
		return false;
	}

	private Map<Object, Object> transformData(ReportDataPointContext dataPoint, Set<Object> xValues, List<Map<String, Object>> props) throws Exception {
		if (props != null && !props.isEmpty()) {
			Map<Object, Object> dataPoints = new LinkedHashMap<>();
			for (Map<String, Object> prop : props) {
				Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
				if (xVal != null) {
					xVal = getFormattedVal(dataPoint.getxAxis().getField(), xVal);
					if (xValues != null) {
						xValues.add(xVal);
					}
					Object yVal = prop.get(dataPoint.getyAxis().getField().getName());
					yVal = getFormattedVal(dataPoint.getyAxis().getField(), yVal);
					if (dataPoint.getGroupByFields() == null || dataPoint.getGroupByFields().isEmpty()) {
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
							groupByVal = getFormattedVal(field, groupByVal);
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
			return dataPoints;
		}
		return null;
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private Object getFormattedVal (FacilioField field, Object val) {
		if (val == null) {
			return "";
		}
		if (field.getDataTypeEnum() == FieldType.DECIMAL) {
			return DECIMAL_FORMAT.format(FieldUtil.castOrParseValueAsPerType(field, val));
		}
		return val;
	}
}
