package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.workflows.util.WorkflowUtil;

public class SendReadingReportMailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String, String> files = new HashMap<>();
		String fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		files.put("Report Data" + fileFormat.getExtention(), fileUrl);
		String emailFrom;
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		if (viewName != null) {
			emailFrom = "noreply@${org.domain}.facilio.com";
		} else {
			emailFrom = "report@${org.domain}.facilio.com";
		}
		EMailTemplate eMailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
		eMailTemplate.setFrom(emailFrom);
		if(eMailTemplate.getWorkflow() != null && eMailTemplate.getWorkflow().getWorkflowString() == null) {
			eMailTemplate.getWorkflow().setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(eMailTemplate.getWorkflow()));
		}
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
