package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteExistingReportBaseLineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long reportId = (long) context.get(FacilioConstants.ContextNames.REPORT_ID);
		if (reportId != -1) {
			BaseLineAPI.deleteExistingReportBaseLines(reportId);
		}
		else {
			throw new IllegalArgumentException("Report ID cannot be null during deletion of report base lines");
		}
		return false;
	}

}
