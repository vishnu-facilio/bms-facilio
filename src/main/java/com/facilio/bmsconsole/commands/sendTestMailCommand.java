package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sendTestMailCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName= (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long recordId= (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        FacilioModule module = modBean.getModule(moduleName);

        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summary,moduleName);
        ModuleBaseWithCustomFields currentRecord = CollectionUtils.isNotEmpty(moduleBaseWithCustomFields) ? moduleBaseWithCustomFields.get(0) : null;

        long templateId= (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
        Template template= TemplateAPI.getTemplate(templateId);
        EMailStructure emailStructure= (EMailStructure) template;
        Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
        Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(module.getName(), currentRecord, placeHolders);
        JSONObject actionObj =template.getTemplate(recordPlaceHolders);

        String email=AccountUtil.getCurrentUser().getEmail();
        JSONObject emailJson=new JSONObject();
         emailJson.put("sender", email);
         emailJson.put("to", email);
         emailJson.put("subject", actionObj.get("subject"));
         emailJson.put("message", actionObj.get("message"));
         emailJson.put("html", emailStructure.isHtml());
         emailJson.put("isAttachmentAdded",emailStructure.getIsAttachmentAdded());
         if (emailStructure.isHtml()) {
            emailJson.put("mailType", "html");
         }
         if(emailStructure.getIsAttachmentAdded()){
             if (CollectionUtils.isNotEmpty(template.getAttachments())) {
                 Map<String,String> attachmentMap = new HashMap<String, String>();
                 for (TemplateAttachment attachment: template.getAttachments()) {
                     String url = attachment.fetchFileUrl(currentRecord);
                     if (url != null) {
                         attachmentMap.put(attachment.getFileName(), url);
                     }
                 }
                 emailJson.put(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST, attachmentMap);
             }
         }
         FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(emailJson);

        return false;
    }
}
