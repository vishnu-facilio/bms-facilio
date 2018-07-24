package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class TransformReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Pair<List<ReportDataPointContext>, List<Map<String, Object>>>> reportData = (List<Pair<List<ReportDataPointContext>, List<Map<String, Object>>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Map<String, List<Map<String, Object>>> transformedData = new HashMap<>();
		
		for (Pair<List<ReportDataPointContext>, List<Map<String, Object>>> pair : reportData ) {
			for (ReportDataPointContext dataPoint : pair.getLeft()) {
				List<Map<String, Object>> dataPoints = new ArrayList<>();
				List<Map<String, Object>> props = pair.getRight();
				if (props != null && !props.isEmpty()) {
					for (Map<String, Object> prop : props) {
						Object xVal = prop.get(dataPoint.getxAxisField().getName());
						Object yVal = prop.get(dataPoint.getyAxisField().getName());
						if (xVal != null && yVal != null) {
							Map<String, Object> singlePoint = new HashMap<>();
							singlePoint.put("x", xVal);
							singlePoint.put("y", yVal);
							dataPoints.add(singlePoint);
						}
					}
				}
				transformedData.put(dataPoint.getName(), dataPoints);
			}
		}
		context.put(FacilioConstants.ContextNames.REPORT_DATA, transformedData);
		return false;
	}

}
