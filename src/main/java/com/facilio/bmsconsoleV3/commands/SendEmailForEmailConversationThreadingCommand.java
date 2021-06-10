package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.v3.context.AttachmentV3Context;

public class SendEmailForEmailConversationThreadingCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(SendEmailForEmailConversationThreadingCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EmailConversationThreadingContext emailConversation = (EmailConversationThreadingContext) ControlScheduleUtil.getObjectFromRecordMap(context, MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(emailConversation.getFromType() == EmailConversationThreadingContext.From_Type.ADMIN.getIndex()) {
			if(emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.REPLY.getIndex()) {

				EmailToModuleDataContext emailToModuleContext = MailMessageUtil.getEmailToModuleContext(emailConversation.getRecordId(), emailConversation.getDataModuleId());
				
				if(emailToModuleContext != null) {
					
					String messageId = emailToModuleContext.getMessageId();
					
					emailConversation.setMessageId(sendMail(emailConversation, messageId));
				}
				else {
					
					EmailConversationThreadingContext firstEmailConversationOfThisRecord = MailMessageUtil.getEmailConversationThreadingContext(emailConversation.getRecordId(), emailConversation.getModuleId());
					
					if(firstEmailConversationOfThisRecord != null) {
						
						String messageId = firstEmailConversationOfThisRecord.getMessageId();
						
						emailConversation.setMessageId(sendMail(emailConversation, messageId));
					}
					
					else {
						emailConversation.setMessageId(sendMail(emailConversation, null));
					}
				}
				
				RecordAPI.updateRecord(emailConversation, modBean.getModule(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME), modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME));
			}
			else {
				if(emailConversation.getTo() != null) {
					
					sendNoteNotifyMail(emailConversation);
				}
			}
		}
		
		return false;
	}
	
	private void sendNoteNotifyMail(EmailConversationThreadingContext emailConversation) throws Exception {
		try {
			JSONObject mailJson = new JSONObject();
			mailJson.put(EmailClient.SENDER, EmailFactory.getEmailClient().getNoReplyFromEmail());
			mailJson.put(EmailClient.TO, emailConversation.getTo());
			mailJson.put(EmailClient.SUBJECT, "Re: "+emailConversation.getSubject());
			mailJson.put(EmailClient.MESSAGE, emailConversation.getHtmlContent());
			mailJson.put(EmailClient.MAIL_TYPE,EmailClient.HTML);
			
			Map<String,String> attachements = getAttachments(emailConversation);
			FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(mailJson,attachements);
		}
		catch(Exception e) {
			LOGGER.error("Error during notify "+e.getMessage(), e);
		}
	}

	private String sendMail(EmailConversationThreadingContext emailConversation,String messageId) throws Exception {
		
		if(!FacilioProperties.isProduction()) {
			return null;
		}
		
		JSONObject mailJson = new JSONObject();
		
		mailJson.put(EmailClient.SENDER, emailConversation.getFrom());
		mailJson.put(EmailClient.TO, emailConversation.getTo());
		mailJson.put(EmailClient.CC, emailConversation.getCc());
		mailJson.put(EmailClient.BCC, emailConversation.getBcc());
		mailJson.put(EmailClient.SUBJECT, "Re: "+emailConversation.getSubject());
		mailJson.put(EmailClient.MESSAGE, emailConversation.getHtmlContent());
		mailJson.put(EmailClient.MAIL_TYPE,EmailClient.HTML);
		
		if(messageId != null) {
			JSONObject HeaderJSON = new JSONObject();
			
			HeaderJSON.put("References", messageId);
			HeaderJSON.put("In-Reply-To", messageId);
			
			mailJson.put(EmailClient.HEADER, HeaderJSON);
		}
		
		Map<String, String> files = getAttachments(emailConversation);
		
		LOGGER.error("emailConversation -- "+emailConversation.getId()+" mail JSON --- "+mailJson +" files -- "+files);
		
		String returnMessageId = AwsUtil.sendEmailViaMimeMessage(mailJson, files);
		
		return returnMessageId;
		
//		FacilioFactory.getEmailClient().sendEmail(mailJson);
	}
	
	private Map<String, String> getAttachments(EmailConversationThreadingContext emailConversation) throws Exception {
		
		List<AttachmentV3Context> attachments = V3AttachmentAPI.getAttachments(emailConversation.getId(), MailMessageUtil.EMAIL_CONVERSATION_THREADING_ATTACHMENT_MODULE);
		
		Map<String, String> files = null;
		
		FileStore fs = FacilioFactory.getFileStore();
		if(attachments != null && !attachments.isEmpty()) {
			files = new HashMap<String, String>();
			for(AttachmentV3Context attachment : attachments) {
				String fileName = attachment.getFileFileName();
				if(attachment.getDatum("contentId") != null) {
					fileName = (String) attachment.getDatum("contentId");
				}
				files.put(fileName, fs.getOrgiDownloadUrl(attachment.getFileId()));
			}
		}
		
		return files;
	}

}
