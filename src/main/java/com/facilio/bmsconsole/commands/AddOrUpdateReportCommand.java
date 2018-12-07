package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.util.ReportUtil;

public class AddOrUpdateReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		report.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		if(report.getId() <= 0) {
			ReportUtil.addReport(report);
		}
		else {
			ReportUtil.updateReport(report);
			ReportUtil.deleteReportFields(report.getId());
		}
		
		Set<ReportFieldContext> reportFields = new HashSet<>();
		for(ReportDataPointContext dataPoint : report.getDataPoints()) {
			if (dataPoint.getTypeEnum() == DataPointType.DERIVATION) {
				continue;
			}
			
			if(dataPoint.getxAxis() != null)  {
				reportFields.add(constructReportField(dataPoint.getxAxis().getField(), report.getId()));
			}
			
			if(dataPoint.getyAxis() != null) {
				reportFields.add(constructReportField(dataPoint.getyAxis().getField(), report.getId()));
			}
			
			if(dataPoint.getDateField() != null) {
				reportFields.add(constructReportField(dataPoint.getDateField(), report.getId()));
			}
		}
		if (report.getxCriteria() != null) {
			reportFields.add(constructReportField(report.getxCriteria().getxField(), report.getId()));
		}
		
		ReportUtil.addReportFields(reportFields);
		return false;
	}
	
	private ReportFieldContext constructReportField (FacilioField field, long reportId) {
		ReportFieldContext reportFieldContext = new ReportFieldContext();
		reportFieldContext.setReportId(reportId);
		
		if (field.getFieldId() > 0) {
			reportFieldContext.setFieldId(field.getFieldId());
		}
		else if (field.getModule() != null && field.getModule().getName() != null && !field.getModule().getName().isEmpty() && field.getName() != null && !field.getName().isEmpty()){
			reportFieldContext.setModuleName(field.getModule().getName());
			reportFieldContext.setFieldName(field.getName());
		}
		else {
			throw new IllegalArgumentException("Invalid field object for ReportFields addition");
		}
		return reportFieldContext;
	}

}
