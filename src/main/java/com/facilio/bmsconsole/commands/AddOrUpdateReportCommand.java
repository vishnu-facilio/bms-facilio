package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportFilterContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class AddOrUpdateReportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		report.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		if(report.getId() <= 0) {
			addWorkflow(report);
			ReportUtil.addReport(report);
		}
		else {
			if (report.getTransformWorkflow() != null || report.getWorkflowId() == -99) {
				ReportContext oldReport = ReportUtil.getReport(report.getId(), true);
				if (oldReport.getWorkflowId() != -1) {
					WorkflowUtil.deleteWorkflow(oldReport.getWorkflowId());
				}
				addWorkflow(report);
			}
			ReportUtil.updateReport(report);
			ReportUtil.deleteReportFields(report.getId());
		}
		
		Set<ReportFieldContext> reportFields = new HashSet<>();
		for(ReportDataPointContext dataPoint : report.getDataPoints()) {
			if (dataPoint.getTypeEnum() == DataPointType.DERIVATION) {
				continue;
			}
			
			if(dataPoint.getxAxis() != null)  {
				reportFields.add(constructReportField(dataPoint.getxAxis().getModule(), dataPoint.getxAxis().getField(), report.getId()));
			}
			
			if(dataPoint.getyAxis() != null) {
				reportFields.add(constructReportField(dataPoint.getyAxis().getModule(), dataPoint.getyAxis().getField(), report.getId()));
			}
			
			if(dataPoint.getDateField() != null) {
				reportFields.add(constructReportField(dataPoint.getDateField().getModule(), dataPoint.getDateField().getField(), report.getId()));
			}
		}
		if (report.getFilters() != null && !report.getFilters().isEmpty()) {
			for (ReportFilterContext filter : report.getFilters()) {
				reportFields.add(constructReportField(filter.getModule(), filter.getField(), report.getId()));
			}
		}
		
		ReportUtil.addReportFields(reportFields);
		return false;
	}
	
	private ReportFieldContext constructReportField (FacilioModule module, FacilioField field, long reportId) {
		ReportFieldContext reportFieldContext = new ReportFieldContext();
		reportFieldContext.setReportId(reportId);
		
		if (field.getFieldId() > 0) {
			reportFieldContext.setFieldId(field.getFieldId());
		}
		else if (field.getModule() != null && field.getModule().getName() != null && !field.getModule().getName().isEmpty() && field.getName() != null && !field.getName().isEmpty()){
//			reportFieldContext.setModuleName(field.getModule().getName());
			reportFieldContext.setField(module, field);
		}
		else {
			throw new IllegalArgumentException("Invalid field object for ReportFields addition");
		}
		return reportFieldContext;
	}
	
	private void addWorkflow(ReportContext report) throws Exception {
		if (report.getTransformWorkflow() != null) {
			long workflowId = WorkflowUtil.addWorkflow(report.getTransformWorkflow());
			report.setWorkflowId(workflowId);
		}
	}

}
