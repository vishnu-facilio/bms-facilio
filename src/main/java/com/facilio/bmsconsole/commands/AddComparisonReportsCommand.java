package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddComparisonReportsCommand implements Command {		// delete this class

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportContext> reports = (List<ReportContext>) context.get(FacilioConstants.ContextNames.REPORT_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (reports != null && !reports.isEmpty()) {
			ReportContext firstReport = reports.get(0);
			if (firstReport.getParentFolderId() == null || firstReport.getParentFolderId() < 0) {
				ReportFolderContext defaultFolder = DashboardUtil.getDefaultReportFolder(moduleName);
				firstReport.setParentFolderId(defaultFolder.getId());
			}
			context.put(FacilioConstants.ContextNames.RECORD_ID, firstReport.getId());
			
			for (int i = 1; i<reports.size(); i++) {
				ReportContext report = reports.get(i);
				report.setReportEntityId(firstReport.getReportEntityId());
				report.setIsComparisionReport(true);
			}
		}
		else {
			throw new IllegalArgumentException("Reports cannot be null/ empty during addition");
		}
		
		return false;
	}

}
