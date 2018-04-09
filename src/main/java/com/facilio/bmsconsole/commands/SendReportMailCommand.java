package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.reports.ReportExportUtil;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.pdf.PdfUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class SendReportMailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
//		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT_CONTEXT);
		
		int type = (int) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		EMailTemplate eMailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
		eMailTemplate.setFrom("report@${org.domain}.facilio.com");
		if(eMailTemplate.getWorkflow() != null && eMailTemplate.getWorkflow().getWorkflowString() == null) {
			eMailTemplate.getWorkflow().setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(eMailTemplate.getWorkflow()));
		}
		
		String fileName = "Report_" + (module.getDisplayName() != null && !module.getDisplayName().isEmpty() ? module.getDisplayName() : module.getName()) + "_" + reportContext.getName();
		Map<String, String> files = new HashMap<>();
		FileFormat fileFormat = FileFormat.getFileFormat(type);
		
		JSONArray reportData = (JSONArray) context.get(FacilioConstants.ContextNames.REPORT);
		
		Map<String,Object> table;
		if (reportContext.getReportChartType() == ReportContext.ReportChartType.TABULAR) {
			List<ReportColumnContext> reportColumns = (List<ReportColumnContext>) context.get(FacilioConstants.ContextNames.REPORT_COLUMN_LIST);
			table = ReportExportUtil.getTabularReportData(reportData, reportContext, reportColumns);
			String fileUrl = ExportUtil.exportData(fileFormat, module, table);
			files.put(fileName + fileFormat.getExtention(), fileUrl);
		}
		else {
			Long baseLineComparisionDiff = (Long) context.get(FacilioConstants.ContextNames.BASE_LINE);
			table = ReportExportUtil.getDataInExportFormat(reportData, reportContext, baseLineComparisionDiff);
		}
		
		if(fileFormat == FileFormat.PDF || fileFormat == FileFormat.IMAGE) {
			String url = ReportsUtil.getReportClientUrl(moduleName, reportContext.getId(), fileFormat);
			String fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(),url, fileFormat);
			files.put(fileName + fileFormat.getExtention(), fileUrl);
			if (fileFormat == FileFormat.IMAGE) {
				String fileUrl2 = ExportUtil.exportData(fileFormat, module, table);
				files.put(fileName + FileFormat.CSV.getExtention(), fileUrl2);
			}
		}
		else {
			String fileUrl = ExportUtil.exportData(fileFormat, module, table);
			files.put(fileName + fileFormat.getExtention(), fileUrl);
		}
		
		/*else {
			List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if(fileFormat == FileFormat.PDF || fileFormat == FileFormat.IMAGE) {
				String url = ReportsUtil.getReportClientUrl(moduleName, reportContext.getId(), fileFormat);
				String fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(),url, fileFormat);
				files.put(fileName + fileFormat.getExtention(), fileUrl);
				if (fileFormat == FileFormat.IMAGE) {
					String fileUrl2 = ExportUtil.exportData(FileFormat.CSV, module, view.getFields(), records);
					files.put(fileName + FileFormat.CSV.getExtention(), fileUrl2);
				}
			}
			else {
				String fileUrl = ExportUtil.exportData(fileFormat, module, view.getFields(), records);
				files.put(fileName + fileFormat.getExtention(), fileUrl);
			}
		}*/
		
		Map<String, Object> parameters = new HashMap<String,Object>();
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), parameters);
		
		JSONObject template = eMailTemplate.getTemplate(parameters);
		String toList;
		if (template.get("to") instanceof JSONArray) {
			JSONArray array = (JSONArray) template.get("to");
			toList = StringUtils.join(array, ",");
		}
		else {
			toList = (String) template.get("to");
		}
		template.replace("to", toList);
 		AwsUtil.sendEmail(template, files);
 		
		return false;
	}

}
