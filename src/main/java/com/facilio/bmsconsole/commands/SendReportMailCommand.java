package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReportColumnContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportSpaceFilterContext;
import com.facilio.bmsconsole.reports.ReportExportUtil;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.pdf.PdfUtil;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendReportMailCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String,Object> table;
		Map<String, String> files = new HashMap<>();
		int type = (int) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		FileFormat fileFormat = FileFormat.getFileFormat(type);
		String fileName;
		String moduleName;
		String url;
		
		if (context.containsKey(FacilioConstants.ContextNames.REPORT)) {
			ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT_CONTEXT);
			moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			fileName = "Report_" + (module.getDisplayName() != null && !module.getDisplayName().isEmpty() ? module.getDisplayName() : module.getName()) + "_" + reportContext.getName();
			url = ReportsUtil.getReportClientUrl(module.getName(), reportContext.getId(), fileFormat);
			
			JSONArray reportData = (JSONArray) context.get(FacilioConstants.ContextNames.REPORT);
			if (reportContext.getReportChartType() == ReportContext.ReportChartType.TABULAR) {
				List<ReportColumnContext> reportColumns = (List<ReportColumnContext>) context.get(FacilioConstants.ContextNames.REPORT_COLUMN_LIST);
				table = ReportExportUtil.getTabularReportData(reportData, reportContext, reportColumns);
			}
			else {
				Long baseLineComparisionDiff = (Long) context.get(FacilioConstants.ContextNames.BASE_LINE);
				ReportSpaceFilterContext reportSpaceFilterContext = (ReportSpaceFilterContext) context.get(FacilioConstants.ContextNames.FILTERS);
				JSONArray dateFilter = (JSONArray) context.get(FacilioConstants.ContextNames.DATE_FILTER);
				table = ReportExportUtil.getDataInExportFormat(reportData, reportContext, baseLineComparisionDiff, reportSpaceFilterContext, dateFilter);
			}
			
		}
		else {
			Map<String, Object> config = (Map<String, Object>) context.get(FacilioConstants.ContextNames.CONFIG);
			List<Map<String, Object>> analyticsDataList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.REPORT_LIST);
			table = ReportExportUtil.getAnalyticsData(analyticsDataList, config);
			fileName = (String) config.get("name");
			moduleName = fileName;
			url = ReportsUtil.getAnalyticsClientUrl(config, fileFormat);
		}
		
		EMailTemplate eMailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
		eMailTemplate.setFrom("report@${org.domain}.facilio.com");
		if(eMailTemplate.getWorkflow() != null && eMailTemplate.getWorkflow().getWorkflowString() == null) {
			eMailTemplate.getWorkflow().setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(eMailTemplate.getWorkflow()));
		}
		
		if(fileFormat == FileFormat.PDF || fileFormat == FileFormat.IMAGE) {
			String fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(),url, fileFormat);
			files.put(fileName + fileFormat.getExtention(), fileUrl);
			if (fileFormat == FileFormat.IMAGE) {
				String fileUrl2 = ExportUtil.exportData(fileFormat, moduleName, table, true);
				files.put(fileName + FileFormat.CSV.getExtention(), fileUrl2);
			}
		}
		else {
			String fileUrl = ExportUtil.exportData(fileFormat, moduleName, table, true);
			files.put(fileName + fileFormat.getExtention(), fileUrl);
		}
		
		/*else {
		 	FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
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
