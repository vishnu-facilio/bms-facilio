package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.constants.FacilioConstants;

public class AddReportColumnsCommand extends FacilioCommand {  // delete this class

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
		return false;
	}

}
