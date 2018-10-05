package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.workflows.util.WorkflowUtil;

public class NewTransformReportDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> reportData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report != null && report.getTransformWorkflow() != null && reportData != null && !reportData.isEmpty()) {
			String wfXmlString = WorkflowUtil.getXmlStringFromWorkflow(report.getTransformWorkflow());
			Map<String,Object> params = new HashMap<>();
			params.put("data", reportData);
			List<Map<String, Object>> transformedData = (List<Map<String, Object>>) WorkflowUtil.getWorkflowExpressionResult(wfXmlString, params,null,true,false);
			context.put(FacilioConstants.ContextNames.REPORT_DATA, transformedData);
		}
		return false;
	}

}
