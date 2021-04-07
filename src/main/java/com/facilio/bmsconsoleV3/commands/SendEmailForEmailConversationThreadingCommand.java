package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;

public class SendEmailForEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EmailConversationThreadingContext emailConversation = (EmailConversationThreadingContext) ControlScheduleUtil.getObjectFromRecordMap(context, MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
		
		
		if(emailConversation.getFromType() == EmailConversationThreadingContext.From_Type.ADMIN.getValue()) {
			if(emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.REPLY.getValue()) {

				EmailToModuleDataContext emailToModuleContext = MailMessageUtil.getEmailToModuleContext(emailConversation.getRecordId(), emailConversation.getDataModuleId());
				
				if(emailToModuleContext != null) {
					
					String messageId = emailToModuleContext.getMessageId();
					
					sendMail(emailConversation, messageId);
				}
				else {
					
					EmailConversationThreadingContext firstEmailConversationOfThisRecord = MailMessageUtil.getEmailConversationThreadingContext(emailConversation.getRecordId(), emailConversation.getModuleId());
					
					if(firstEmailConversationOfThisRecord != null) {
						
						String messageId = firstEmailConversationOfThisRecord.getMessageId();
						
						sendMail(emailConversation, messageId);
					}
					
					else {
						sendMail(emailConversation, null);
					}
				}
				
			}
		}
		return false;
	}
	
	private void sendMail(EmailConversationThreadingContext emailConversation,String messageId) throws Exception {
		
		JSONObject mailJson = new JSONObject();
		
		mailJson.put(EmailClient.SENDER, emailConversation.getFrom());
		mailJson.put(EmailClient.TO, emailConversation.getTo());
		mailJson.put(EmailClient.CC, emailConversation.getCc());
		mailJson.put(EmailClient.BCC, emailConversation.getBcc());
		mailJson.put(EmailClient.SUBJECT, "Re: "+emailConversation.getSubject());
		mailJson.put(EmailClient.MESSAGE, emailConversation.getContent());
		mailJson.put(EmailClient.MAIL_TYPE,EmailClient.HTML);
		
		if(messageId != null) {
			JSONObject HeaderJSON = new JSONObject();
			
			HeaderJSON.put("References", messageId);
			HeaderJSON.put("In-Reply-To", messageId);
			
			mailJson.put(EmailClient.HEADER, HeaderJSON);
		}
		
		AwsUtil.sendEmailViaMimeMessage(mailJson, null);
		
//		FacilioFactory.getEmailClient().sendEmail(mailJson);
	}

}
