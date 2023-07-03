package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailSourceType;
import com.facilio.report.context.ReportContext;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class SendReadingReportMailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, String> files = new HashMap<>();
		String fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String fileName = (String) context.get(FacilioConstants.ContextNames.FILE_NAME);
		files.put((StringUtils.isNotEmpty(fileName) ? fileName : "Report Data") + fileFormat.getExtention(), fileUrl);
		String emailFrom;
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		if (viewName != null) {
			emailFrom = EmailClient.getNoReplyFromEmail();
		} else {
			emailFrom = EmailClient.getFromEmail("report");
		}
		EMailTemplate eMailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
		eMailTemplate.setFrom(emailFrom);
		if(eMailTemplate.getFromID() != null && eMailTemplate.getFromID() > 0){
			String fromEmailAddress = ActionAPI.getEMailAddress(eMailTemplate.getFromID());
			if(fromEmailAddress != null) {
				eMailTemplate.setFrom(fromEmailAddress);
			}
		}
		if(eMailTemplate.getWorkflow() != null && eMailTemplate.getWorkflow().getWorkflowString() == null) {
			eMailTemplate.getWorkflow().setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(eMailTemplate.getWorkflow()));
		}
		Map<String, Object> parameters = new HashMap<String,Object>();
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), parameters);
		
		JSONObject template = eMailTemplate.getTemplate(parameters);
		ReportContext reportContext = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
		template.put(MailConstants.Params.SOURCE_TYPE, MailSourceType.REPORT.name());
		if(reportContext != null){
			template.put(MailConstants.Params.RECORDS_MODULE_ID, reportContext.getModuleId());
			template.put(MailConstants.Params.RECORD_ID, reportContext.getId());
		}
		String toList;
		if (template.get("to") instanceof JSONArray) {
			JSONArray array = (JSONArray) template.get("to");
			toList = StringUtils.join(array, ",");
		}
		else {
			toList = (String) template.get("to");
		}
		template.replace("to", toList);
 		FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(template, files);
		
		return false;
	}

}
