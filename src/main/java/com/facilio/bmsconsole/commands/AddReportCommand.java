package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.util.ReportUtil;

public class AddReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		report.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		ReportUtil.addReport(report);
		
		List <ReportFieldContext> reportFields = new ArrayList<>();
		
		List<Long> fieldids = new ArrayList<>();
		for(ReportDataPointContext dataPoint : report.getDataPoints()) {
			
			if(dataPoint.getxAxis() != null && dataPoint.getxAxis().getFieldId() > 0 && !fieldids.contains(dataPoint.getxAxis().getFieldId())) {
				fieldids.add(dataPoint.getxAxis().getFieldId());
			}
			
			if(dataPoint.getyAxis() != null && dataPoint.getyAxis().getFieldId() > 0 && !fieldids.contains(dataPoint.getyAxis().getFieldId())) {
				
				fieldids.add(dataPoint.getyAxis().getFieldId());
			}
			
			if(dataPoint.getDateFieldId() > 0 && !fieldids.contains(dataPoint.getDateFieldId()) ) {
				fieldids.add(dataPoint.getyAxis().getFieldId());
			}
		}
		
		for(Long fieldid :fieldids) {
			
			ReportFieldContext reportFieldContext = new ReportFieldContext();
			reportFieldContext.setReportId(report.getId());
			reportFieldContext.setFieldId(fieldid);
			reportFields.add(reportFieldContext);
		}
		ReportUtil.addReportFields(reportFields);
		return false;
	}

}
