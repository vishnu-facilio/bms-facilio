package com.facilio.bmsconsole.commands;

import com.facilio.modules.BaseLineContext;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

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
