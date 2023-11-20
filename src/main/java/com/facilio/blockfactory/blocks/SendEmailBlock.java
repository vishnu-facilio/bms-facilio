package com.facilio.blockfactory.blocks;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.flowLog.FlowLogLevel;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailSourceType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.services.factory.FacilioFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Log4j
public class SendEmailBlock extends BaseBlock{
    private Long fromMailId;
    private String to;
    private String cc;
    private String bcc;
    private Long templateId;
    private Boolean sendAsSeparateMail;
    public SendEmailBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
       try{
           init();
           Template emailTemplate = TemplateAPI.getTemplate(templateId);
           if(emailTemplate == null){
               throw new FlowException("Template Id:"+templateId+" doesn't exists");
           }
           EmailFromAddress fromAddress = MailMessageUtil.getEmailFromAddress(fromMailId,EmailFromAddress.SourceType.NOTIFICATION,true);
           if(fromAddress == null){
               throw new FlowException("from mail Id:"+fromMailId+" doesn't exists");
           }
           ModuleBaseWithCustomFields currentRecord = FieldUtil.getAsBeanFromMap(flowEngineInterFace.getCurrentRecord(), ModuleBaseWithCustomFields.class);
           EMailStructure emailStructure= (EMailStructure) emailTemplate;

           String moduleName = flowEngineInterFace.getFlow().getModuleName();
           Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
           Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName,currentRecord, placeHolders);
           JSONObject emailReplacedJSON =emailStructure.getTemplate(recordPlaceHolders);

           JSONObject emailJson=new JSONObject();

           emailJson.put("to", to);
           emailJson.put("cc", cc);
           emailJson.put("bcc", bcc);

           emailJson = (JSONObject) FlowEngineUtil.replacePlaceHolder(emailJson,memory);

           emailJson.put("sender", fromAddress.getEmail());
           emailJson.put("subject", emailReplacedJSON.get("subject"));
           emailJson.put("message", emailReplacedJSON.get("message"));
           emailJson.put("html", emailStructure.isHtml());
           emailJson.put("sendAsSeparateMail",sendAsSeparateMail);
           emailJson.put("isAttachmentAdded",emailStructure.getIsAttachmentAdded());
           if (emailStructure.isHtml()) {
               emailJson.put("mailType", "html");
           }

           if(emailStructure.getIsAttachmentAdded()){
               if (CollectionUtils.isNotEmpty(emailStructure.getAttachments())) {
                   Map<String,String> attachmentMap = new HashMap<String, String>();
                   for (TemplateAttachment attachment: emailStructure.getAttachments()) {
                       String url = attachment.fetchFileUrl(currentRecord);
                       if (url != null) {
                           attachmentMap.put(attachment.getFileName(), url);
                       }
                   }
                   emailJson.put(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST, attachmentMap);
               }
           }

           sendMail(emailJson,currentRecord);

       }catch (Exception e){
           String exceptionMsg = "Exception in SendEmailBlock:"+e.getMessage();
           LOGGER.debug(exceptionMsg);
           flowEngineInterFace.log(FlowLogLevel.SEVERE,exceptionMsg);
           FlowException flowException = e instanceof FlowException?(FlowException)e:new FlowException(exceptionMsg);
           flowEngineInterFace.emitBlockError(this,memory,flowException);
       }
    }
    private void init(){
        this.fromMailId = (Long) config.get(Constants.EmailBlockConstants.FROM_MAIL_ID);
        this.to = (String) config.get(Constants.EmailBlockConstants.TO);
        this.cc = (String) config.get(Constants.EmailBlockConstants.CC);
        this.bcc = (String) config.get(Constants.EmailBlockConstants.BCC);
        this.templateId = (Long) config.get(Constants.EmailBlockConstants.TEMPLATE_ID);
        this.sendAsSeparateMail = (Boolean) config.getOrDefault(Constants.EmailBlockConstants.SEND_AS_SEPARATE_MAIL,false);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object fromMailId = config.get(Constants.EmailBlockConstants.FROM_MAIL_ID);
        Object to = config.get(Constants.EmailBlockConstants.TO);
        Object cc = config.get(Constants.EmailBlockConstants.CC);
        Object bcc = config.get(Constants.EmailBlockConstants.BCC);
        Object templateId = config.get(Constants.EmailBlockConstants.TEMPLATE_ID);
        Object sendAsSeparateMail = config.getOrDefault(Constants.EmailBlockConstants.SEND_AS_SEPARATE_MAIL,false);

        if(templateId==null){
            throw new FlowException("templateId can not be empty for SendEmailBlock");
        }
        if(!(templateId instanceof Long)){
            throw new FlowException("templateId is not a number");
        }
        if(fromMailId == null){
            throw new FlowException("fromMailId can not be empty for SendEmailBlock");
        }
        if(!(fromMailId instanceof Long)){
            throw new FlowException("fromMailId is not a number");
        }
        if((Long) fromMailId <=0){
            throw new FlowException("fromMailId can not be empty for SendEmailBlock");
        }
        if(to == null){
            throw new FlowException("to can not be empty for SendEmailBlock");
        }
        if(!(to instanceof String)){
            throw new FlowException("to is not a string");
        }
        if(cc!=null && !(cc instanceof String)){
            throw new FlowException("cc is not a string");
        }
        if(bcc!=null && !(bcc instanceof String)){
            throw new FlowException("bcc is not a string");
        }
        if(sendAsSeparateMail!=null && !(sendAsSeparateMail instanceof Boolean)){
            throw new FlowException("sendAsSeparateMail is not a boolean");
        }

    }
    private static void sendMail(JSONObject emailJson,ModuleBaseWithCustomFields currentRecord) throws Exception {
        if(emailJson == null || emailJson.isEmpty()){
            return;
        }

        JSONArray toEmails = null;
        Object toAddr = emailJson.remove("to");
        Map<String,String> attachments = (Map<String, String>) emailJson.remove(FacilioConstants.ContextNames.ATTACHMENT_MAP_FILE_LIST);

        if (toAddr instanceof String) {
            toEmails = getTo(toAddr.toString());
        }


        emailJson.put(MailConstants.Params.RECORD_ID, currentRecord.getId());
        emailJson.put(MailConstants.Params.RECORD_CREATED_TIME,currentRecord.getSysCreatedTime());
        emailJson.put(MailConstants.Params.RECORDS_MODULE_ID, currentRecord.getModuleId());
        emailJson.put(MailConstants.Params.SOURCE_TYPE, MailSourceType.FLOW.name());

        if(toEmails == null || toEmails.isEmpty()){
            return;
        }

        Boolean sendAsSeparateMail = (Boolean) emailJson.get("sendAsSeparateMail");
        if (sendAsSeparateMail == null || !sendAsSeparateMail) {
            StringJoiner activeToEmails = new StringJoiner(",");
            for (Object toEmail : toEmails) {
                String to = (String) toEmail;
                if (StringUtils.isNotEmpty(to)) {
                    activeToEmails.add(to);
                }
            }
            emailJson.put("to", activeToEmails.toString());
            FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(emailJson, attachments);
        } else {
            for (Object toEmail : toEmails) {
                String to = (String) toEmail;
                if (StringUtils.isNotEmpty(to)) {
                    emailJson.put("to", to);
                    FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(emailJson, attachments);
                }
            }
        }
    }

    private static JSONArray getTo(String to) {
        if(to != null && !to.isEmpty()) {
            JSONArray toList = new JSONArray();
            if(to.contains(",")) {
                String[] tos = to.trim().split("\\s*,\\s*");
                for(String toAddr : tos) {
                    toList.add(toAddr);
                }
            }
            else {
                toList.add(to);
            }
            return toList;
        }
        return null;
    }

}
