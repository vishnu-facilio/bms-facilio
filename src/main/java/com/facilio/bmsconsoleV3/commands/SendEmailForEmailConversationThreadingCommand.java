package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.util.FacilioUtil;
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
			else if (emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.PUBLIC_NOTE.getIndex() || emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.PRIVATE_NOTE.getIndex()) {
				if(emailConversation.getTo() != null) {
					
					sendNoteNotifyMail(emailConversation);
				}
			}
		}
		
		return false;
	}
	
	private void sendNoteNotifyMail(EmailConversationThreadingContext emailConversation) throws Exception {
		try {
			String message = emailConversation.getHtmlContent();
			String subject = emailConversation.getSubject();
				
			subject = getSubjectForAddNote(emailConversation,message);
			message = getMessageForAddNote(emailConversation,message);
			
			JSONObject mailJson = new JSONObject();
			mailJson.put(EmailClient.SENDER, EmailFactory.getEmailClient().getNoReplyFromEmail());
			mailJson.put(EmailClient.TO, emailConversation.getTo());
			mailJson.put(EmailClient.SUBJECT, subject);
			mailJson.put(EmailClient.MESSAGE, message);
			mailJson.put(EmailClient.MAIL_TYPE,EmailClient.HTML);
			
			Map<String,String> attachements = getAttachments(emailConversation);
			FacilioFactory.getEmailClient().sendEmailWithActiveUserCheck(mailJson,attachements);
		}
		catch(Exception e) {
			LOGGER.error("Error during notify "+e.getMessage(), e);
		}
	}

	private String getSubjectForAddNote(EmailConversationThreadingContext emailConversation, String message) {
		// TODO Auto-generated method stub
		
		String subject = "Note Added - [#"+emailConversation.getRecordId()+"] "+emailConversation.getSubject();
		return subject;
	}

	private String getMessageForAddNote(EmailConversationThreadingContext emailConversation, String message) throws Exception {
		
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(modBean.getModule(emailConversation.getDataModuleId()).getName().equals(FacilioConstants.ContextNames.SERVICE_REQUEST)) {
			
			String serviceRequestLink = "https://"+FacilioProperties.getAppDomain()+"/app/sr/serviceRequest/all/"+emailConversation.getRecordId()+"/overview";
			
			String hrefTag = "<a href=\""+serviceRequestLink+"\">[#"+emailConversation.getRecordId()+"]</a>";
			
			String newMessage = "Hi,<br><br>"+AccountUtil.getCurrentUser().getName() +" added a "+emailConversation.getMessageTypeEnum().getName()+" note to "+hrefTag+" and wants you to have a look<br><br>";
			
			newMessage = newMessage + "Note Content:<br> "+message;
			
			return newMessage;
		}
		
		return message;
	}
	
	private String getMessageForReply(String to, EmailConversationThreadingContext emailConversation, String message) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String appDomain = getAppDomain(to);
		
		if(modBean.getModule(emailConversation.getDataModuleId()).getName().equals(FacilioConstants.ContextNames.SERVICE_REQUEST)) {
			
			String serviceRequestLink = appDomain+"/service/my-requests/service-request/all/"+emailConversation.getRecordId()+"/overview";
			
			String hrefTag = "<a href=\""+serviceRequestLink+"\">[#"+emailConversation.getRecordId()+"]</a>";
			
			String newMessage = "Hi,<br><br>"+AccountUtil.getCurrentUser().getName() +" added a "+emailConversation.getMessageTypeEnum().getName()+" to "+hrefTag+"<br><br>"+message;
			
			return newMessage;
		}
		
		return message;
	}

	private String getAppDomain(String to) {
		// TODO Auto-generated method stub
		
		String appDomain = "https://"+AccountUtil.getCurrentOrg().getDomain()+".facilioportal.com";
		
		try {
			String firstTo = FacilioUtil.splitByComma(to)[0];
			String emailAddress = MailMessageUtil.getEmailFromPrettifiedFromAddress.apply(firstTo);
			PeopleContext people = PeopleAPI.getPeople(emailAddress);
			
			LOGGER.info("people -- "+people);
			if(people != null) {
				LOGGER.info("people type -- "+people.getPeopleTypeEnum());
			}
			
			if(people != null && people.getPeopleTypeEnum() != null) {
				List<AppDomain> appDomains = null;
				switch(people.getPeopleTypeEnum()) {
				case TENANT_CONTACT: {
					appDomains = IAMAppUtil.getAppDomain(AppDomainType.TENANT_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case VENDOR_CONTACT: {
					appDomains = IAMAppUtil.getAppDomain(AppDomainType.VENDOR_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case OCCUPANT: {
					appDomains = IAMAppUtil.getAppDomain(AppDomainType.SERVICE_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				case CLIENT_CONTACT: {
					appDomains = IAMAppUtil.getAppDomain(AppDomainType.CLIENT_PORTAL, AccountUtil.getCurrentOrg().getId());
					break;
				}
				default:
					break;
				}
				
				if(appDomains != null && !appDomains.isEmpty()) {
					appDomain = "https://"+appDomains.get(0).getDomain();
				}
			}
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return appDomain;
		
	}

	private String sendMail(EmailConversationThreadingContext emailConversation,String messageId) throws Exception {
		
		boolean canSendMail = true;
		if(!FacilioProperties.isProduction()) {
			canSendMail = false;
		}
		if(!FacilioProperties.isProduction() && !FacilioProperties.isDevelopment() && (AccountUtil.getCurrentOrg().getOrgId() == 267l || AccountUtil.getCurrentOrg().getOrgId() == 172l)) {
			canSendMail = true;
		}
		
		if(!canSendMail) {
			return null;
		}
		
		JSONObject mailJson = new JSONObject();
		
		String message = emailConversation.getHtmlContent();
			
	    message = getMessageForReply(emailConversation.getTo(),emailConversation,message);
		
		mailJson.put(EmailClient.SENDER, emailConversation.getFrom());
		mailJson.put(EmailClient.TO, emailConversation.getTo());
		mailJson.put(EmailClient.CC, emailConversation.getCc());
		mailJson.put(EmailClient.BCC, emailConversation.getBcc());
		mailJson.put(EmailClient.SUBJECT, "Re: "+emailConversation.getSubject());
		mailJson.put(EmailClient.MESSAGE, message);
		mailJson.put(EmailClient.MAIL_TYPE,EmailClient.HTML);
		
		if(messageId != null) {
			JSONObject HeaderJSON = new JSONObject();
			
			HeaderJSON.put("References", messageId);
			HeaderJSON.put("In-Reply-To", messageId);
			
			mailJson.put(EmailClient.HEADER, HeaderJSON);
		}
		
		Map<String, String> files = getAttachments(emailConversation);
		
		LOGGER.error("emailConversation -- "+emailConversation.getId()+" mail JSON --- "+mailJson +" files -- "+files);
		
		String returnMessageId = AwsUtil.sendMail(mailJson, files);	// using this method to get messageId back
		
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
