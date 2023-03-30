package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.workflows.util.WorkflowUtil;

public class NewTransformReportDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if (report != null && report.getTransformWorkflow() != null && reportData != null && !reportData.isEmpty()) {
			
			if(report.getTransformWorkflow().getWorkflowString() == null && report.getTransformWorkflow().getWorkflowV2String() == null) {
				String wfXmlString = WorkflowUtil.getXmlStringFromWorkflow(report.getTransformWorkflow());
				report.getTransformWorkflow().setWorkflowString(wfXmlString);
			}
			
			Map<String,Object> params = new HashMap<>();
			params.put(FacilioConstants.ContextNames.DATA_KEY, new ArrayList<>((Collection<Map<String, Object>>) reportData.get(FacilioConstants.ContextNames.DATA_KEY)));
			params.put(FacilioConstants.ContextNames.AGGR_KEY, reportData.get(FacilioConstants.ContextNames.AGGR_KEY));
			
			Map<String, Object> transformedData =  WorkflowUtil.getExpressionResultMap(report.getTransformWorkflow(), params);
			JSONObject data = new JSONObject();
			if (transformedData != null) {
				data.put(FacilioConstants.ContextNames.DATA_KEY, transformedData.get(FacilioConstants.ContextNames.DATA_KEY));
				data.put(FacilioConstants.ContextNames.AGGR_KEY, transformedData.get(FacilioConstants.ContextNames.AGGR_KEY));
			}
			context.put(FacilioConstants.ContextNames.REPORT_DATA, data);
		}
		else {
			context.put(FacilioConstants.ContextNames.CALCULATE_REPORT_AGGR_DATA, false);
		}
		return false;
	}

}
