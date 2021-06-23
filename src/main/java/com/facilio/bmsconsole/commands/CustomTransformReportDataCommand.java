package com.facilio.bmsconsole.commands;

import java.util.Map;
import java.util.Set;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.TransformReportDataIfc;

public class CustomTransformReportDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report != null) {
			Map<String, Map<String, Map<Object, Object>>> reportData = (Map<String, Map<String, Map<Object, Object>>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
			Set<Object> xValues = (Set<Object>) context.get(FacilioConstants.ContextNames.REPORT_X_VALUES);
			if (report.getTransformClassObject() != null) {
				TransformReportDataIfc transformObj = report.getTransformClassObject().newInstance();
				transformObj.transformReportData(report, reportData, xValues);
				context.put(FacilioConstants.ContextNames.REPORT_X_VALUES, transformObj.getxValues());
				context.put(FacilioConstants.ContextNames.REPORT_DATA, transformObj.getReportData());
			}
		}
		return false;
	}

}
