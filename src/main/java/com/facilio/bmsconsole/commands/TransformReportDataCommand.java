package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportDataPointContext;

public class TransformReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Pair<List<ReportDataPointContext>, List<Map<String, Object>>>> reportData = (List<Pair<List<ReportDataPointContext>, List<Map<String, Object>>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		Map<String, Map<Object, Object>> transformedData = new HashMap<>();
		Set<Object> xValues = new LinkedHashSet<>();
		for (Pair<List<ReportDataPointContext>, List<Map<String, Object>>> pair : reportData ) {
			for (ReportDataPointContext dataPoint : pair.getLeft()) {
				Map<Object, Object> dataPoints = new LinkedHashMap<>();
				List<Map<String, Object>> props = pair.getRight();
				if (props != null && !props.isEmpty()) {
					for (Map<String, Object> prop : props) {
						Object xVal = prop.get(dataPoint.getxAxisField().getName());
						if (xVal != null) {
							xValues.add(xVal);
							Object yVal = prop.get(dataPoint.getyAxisField().getName());
							yVal = yVal == null ? "" : yVal;
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
									FacilioField field = dataPoint.getGroupByFields().get(i);
									Object groupByVal = prop.get(field.getName());
									groupByVal = groupByVal == null ? "" : groupByVal;
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
				transformedData.put(dataPoint.getName(), dataPoints);
			}
		}
		context.put(FacilioConstants.ContextNames.REPORT_X_VALUES, xValues);
		context.put(FacilioConstants.ContextNames.REPORT_DATA, transformedData);
		return false;
	}

}
