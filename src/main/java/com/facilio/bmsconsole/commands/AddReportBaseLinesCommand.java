package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;

public class AddReportBaseLinesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<BaseLineContext> baseLines = (List<BaseLineContext>) context.get(FacilioConstants.ContextNames.BASE_LINE_LIST);
		long reportId = (long) context.get(FacilioConstants.ContextNames.REPORT_ID);
		
		BaseLineAPI.addReportBaseLines(reportId, baseLines);
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
