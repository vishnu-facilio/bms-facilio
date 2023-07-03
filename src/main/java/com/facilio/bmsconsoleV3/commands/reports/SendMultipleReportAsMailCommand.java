package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailSourceType;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMultipleReportAsMailCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Map<String, String> files_list = (HashMap<String, String>) context.get("files");
        String emailFrom = EmailClient.getFromEmail("report");;
        EMailTemplate eMailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
        if(eMailTemplate.getFrom() == null || eMailTemplate.getFrom().equals("")){
            eMailTemplate.setFrom(emailFrom);
        }
        if(eMailTemplate.getWorkflow() != null && eMailTemplate.getWorkflow().getWorkflowString() == null) {
            eMailTemplate.getWorkflow().setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(eMailTemplate.getWorkflow()));
        }
        Map<String, Object> parameters = new HashMap<String,Object>();
        CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), parameters);

        JSONObject template = eMailTemplate.getTemplate(parameters);
        template.put(MailConstants.Params.SOURCE_TYPE, MailSourceType.REPORT.name());
        ReportContext reportContext = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
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
        FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(template, files_list);

        return false;
    }
}
