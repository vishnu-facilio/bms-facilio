package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;

public class AddReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		report.setOrgId(AccountUtil.getCurrentOrg().getId());
		ReportUtil.addReport(report);
		
		return false;
	}

}
