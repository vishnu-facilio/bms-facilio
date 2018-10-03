package com.facilio.bmsconsole.commands;

import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;

public class CustomTransformReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report != null && report.getTransformWorkflow() != null) {
			Map<String, Map<String, Map<Object, Object>>> reportData = (Map<String, Map<String, Map<Object, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			Set<Object> xValues = (Set<Object>) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
			if (report.getTransformClassObject() != null) {
				reportData = report.getTransformClassObject().newInstance().transformReportData(report, reportData, xValues);
				context.put(FacilioConstants.ContextNames.REPORT_X_VALUES, xValues);
				context.put(FacilioConstants.ContextNames.REPORT_DATA, reportData);
			}
		}
		return false;
	}

}
