package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class AddReportColumnsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportContext> reports = (List<ReportContext>) context.get(FacilioConstants.ContextNames.REPORT_LIST);
		List<ReportColumnContext> columns = new ArrayList<>();
		for (ReportContext report : reports) {
			ReportColumnContext reportColumn = new ReportColumnContext();
			reportColumn.setActive(true);
			reportColumn.setEntityId(report.getReportEntityId());
			reportColumn.setReportId(report.getId());
			reportColumn.setBaseLineId(report.getBaseLineId());
			reportColumn.setBaseLineAdjust(true);
			columns.add(reportColumn);
		}
		DashboardUtil.addReportColumns(columns);
		return false;
	}

}
