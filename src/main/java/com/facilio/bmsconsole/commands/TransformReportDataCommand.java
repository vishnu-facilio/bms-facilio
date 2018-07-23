package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;

public class TransformReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<Map<String, Object>>> reportData = (Map<String, List<Map<String, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		Map<String, List<Map<String, Object>>> transformedData = new HashMap<>();
		for (ReportDataPointContext dataPoint : report.getDataPoints()) {
			List<Map<String, Object>> dataPoints = new ArrayList<>();
			List<Map<String, Object>> props = reportData.get(dataPoint.getName());
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					Map<String, Object> singlePoint = new HashMap<>();
					singlePoint.put("x", prop.get(dataPoint.getxAxisField().getName()));
					singlePoint.put("y", prop.get(dataPoint.getyAxisField().getName()));
					dataPoints.add(singlePoint);
				}
			}
			transformedData.put(dataPoint.getName(), dataPoints);
		}
		context.put(FacilioConstants.ContextNames.REPORT_DATA, transformedData);
		return false;
	}

}
