package com.facilio.workflows.functions;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.HashMap;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.ANALYTICS_FUNCTION)
public class FacilioAnalyticsFunctions {
	public Object getData(Map<String, Object> globalParam, Object... objects) throws Exception {

		HashMap<String,Object> dataObjectMap = (HashMap<String,Object>)objects[0];

		FacilioContext context =  ReportUtil.getReportData(dataObjectMap);
		if(context.get(FacilioConstants.ContextNames.REPORT) != null) {
			ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
			context.put("report", FieldUtil.getAsProperties(reportContext));
		}
		FieldUtil.getAsProperties(context);

		return context;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}