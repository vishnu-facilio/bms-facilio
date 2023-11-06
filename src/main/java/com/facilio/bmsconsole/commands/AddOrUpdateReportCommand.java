package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.Set;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportFilterContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class AddOrUpdateReportCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		
		report.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		if(report.getId() <= 0 || context.containsKey("dashboard_clone")) {
			addWorkflow(report);
			ReportUtil.addReport(report,context);
		}
		else {
			long oldWorkflowId = -1;
			ReportContext oldReport = ReportUtil.getReport(report.getId(), true);
			if (report.getTransformWorkflow() != null || ( report.getWorkflowId() != null && report.getWorkflowId() > 0 )) {
				if(oldReport.getWorkflowId() != null) {
					oldWorkflowId = oldReport.getWorkflowId();
				}
				addWorkflow(report);
			}
			report.setCreatedBy(oldReport.getCreatedBy() != null ?  oldReport.getCreatedBy() : null);
			report.setCreatedTime(oldReport.getCreatedTime() != null ?  oldReport.getCreatedTime() : null);
			ReportUtil.updateReport(report);
			if (oldWorkflowId != -1) {
				WorkflowUtil.deleteWorkflow(oldWorkflowId);
			}
			ReportUtil.deleteReportFields(report.getId());
		}
		
		Set<ReportFieldContext> reportFields = new HashSet<>();
		if(report.getDataPoints() != null && report.getDataPoints().size() >0){
			for(ReportDataPointContext dataPoint : report.getDataPoints()) {
				if (dataPoint.getTypeEnum() == DataPointType.DERIVATION) {
					continue;
				}

				if(dataPoint.getxAxis() != null)  {
					reportFields.add(constructReportField(dataPoint.getxAxis().getModule(), dataPoint.getxAxis().getField(), report.getId()));
				}

				if(dataPoint.getyAxis() != null && dataPoint.getDynamicKpi() == null) {
					reportFields.add(constructReportField(dataPoint.getyAxis().getModule(), dataPoint.getyAxis().getField(), report.getId()));
				}

				if(dataPoint.getDateField() != null) {
					reportFields.add(constructReportField(dataPoint.getDateField().getModule(), dataPoint.getDateField().getField(), report.getId()));
				}
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
