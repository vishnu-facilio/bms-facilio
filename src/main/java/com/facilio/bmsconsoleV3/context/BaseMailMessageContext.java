package com.facilio.bmsconsoleV3.context;

import com.amazonaws.util.CollectionUtils;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.util.EmailMessageParser;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class BaseMailMessageContext extends V3Context {

    private static final Logger LOGGER = LogManager.getLogger(BaseMailMessageContext.class.getName());
    private static final long serialVersionUID = 1L;
    private String from;
    private String messageId;
    private String referenceMessageId;
    private String inReplyToMessageId;
    private String to;
    private String replyTo;
    private String bcc;
    private String cc;
    private String subject;
    private Boolean html;
    private Long messageUID;
    private Long supportMailId;
    private Long parentId;
    private String contentType;
    private Long receivedDate;
    private Long sentDate;
    private String content;
    private String htmlContent;
    private String textContent;
    private String recipient;
    private BaseMailLogStatus status;
    private BaseMailConversionType mailConversionType;
    private BaseMailType mailType;
    private Long parentRecordId;
    private Long parentModuleId;

   //Getting Module Display Name For logs UI
    private String parentModuleName;
    private Long conversationId;
//    public void setCc(List<String> ccList) {
//    	
//    	if(ccList != null && !ccList.isEmpty()) {
//    		cc = ccList.stream().collect(Collectors.joining(","));
//    	}
//    }
//    public void setBcc(List<String> bccList) {
//    	if(bccList != null && !bccList.isEmpty()) {
//    		bcc = bccList.stream().collect(Collectors.joining(","));
//    	}
//    }
    public Integer getStatus(){
        return status == null ? null : status.getIndex();
    }
    public Integer getMailConversionType(){
        return mailConversionType == null ? null : mailConversionType.getIndex();
    }
    public Integer getMailType(){
        return mailType == null ? null : mailType.getIndex();
    }
    public void setStatus(Integer status){
        this.status = status == null ? null : BaseMailLogStatus.valueOf(status);
    }
    public void setMailConversionType(Integer mailConversionType){
        this.mailConversionType =  mailConversionType == null ? null : BaseMailConversionType.valueOf(mailConversionType);
    }
    public void setMailType(Integer mailType){
        this.mailType =  mailType == null ? null : BaseMailType.valueOf(mailType);
    }
    public String getParentModuleName() throws Exception {
        return this.getParentModuleId() == null ? null : Constants.getModBean().getModule(this.getParentModuleId()).getDisplayName();
    }

    @AllArgsConstructor
    @Getter
    public enum BaseMailType implements FacilioIntEnum {
        CONVERSATION("Conversation"),
        RECORD( "Record"),
        ERROR("Error")
        ;
        String name;
        public String getValue() {
            return this.name;
        }
        public  int getVal() {
            return ordinal() + 1;
        }
        private static final BaseMailType[] CREATION_TYPES = BaseMailType.values();
        public static BaseMailType valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
    @AllArgsConstructor
    @Getter
    public enum BaseMailConversionType implements FacilioIntEnum {
        CONVERSATION("Conversation"),
        RECORD( "Record"),
        ERROR("Error")
        ;
        String name;
        public String getValue() {
            return this.name;
        }
        public  int getVal() {
            return ordinal() + 1;
        }
        private static final BaseMailConversionType[] CREATION_TYPES = BaseMailConversionType.values();
        public static BaseMailConversionType valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }
    @AllArgsConstructor
    @Getter
    public enum BaseMailLogStatus implements FacilioIntEnum {
        SUCCESS("Success"),
        FAILURE( "Failure")
        ;

        String name;
        @Override
        public String getValue() {
            return this.name;
        }
        public  int getVal() {
            return ordinal() + 1;
        }
        private static final BaseMailLogStatus[] CREATION_TYPES = BaseMailLogStatus.values();
        public static BaseMailLogStatus valueOf(int type) {
            if (type > 0 && type <= CREATION_TYPES.length) {
                return CREATION_TYPES[type - 1];
            }
            return null;
        }
    }

    public static BaseMailMessageContext instance(Message message) throws Exception {
        BaseMailMessageContext mailContext = new BaseMailMessageContext();

        try {
            Address[] froms = message.getFrom();
            String from = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
            mailContext.from = from;
            mailContext.subject = message.getSubject();
            mailContext.replyTo = InternetAddress.toString(message
                    .getReplyTo());
            mailContext.to = InternetAddress.toString(message
                    .getRecipients(Message.RecipientType.TO));
            mailContext.subject = message.getSubject();
            mailContext.contentType = message.getContentType();
            if (message.getReceivedDate() != null ) {
                mailContext.receivedDate = message.getReceivedDate().getTime();
            }
            if (message.getSentDate() != null ) {
                mailContext.sentDate = message.getSentDate().getTime();
            }
            if (message.getRecipients(Message.RecipientType.CC) != null) {
                mailContext.cc = InternetAddress.toString(message.getRecipients(Message.RecipientType.CC));
            }
            if (message.getRecipients(Message.RecipientType.BCC) != null) {
                mailContext.bcc = InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC));
            }
            
            String htmlContentString = MailMessageUtil.getContentFromMessage(message, MailMessageUtil.HTML_CONTENT_TYPE);
            
            if(htmlContentString != null) {
            	mailContext.setHtmlContent(htmlContentString);
            }
            
            String textContentString = MailMessageUtil.getContentFromMessage(message, MailMessageUtil.TEXT_CONTENT_TYPE);
            
            if((textContentString == null || textContentString.isEmpty()) && (htmlContentString != null && !htmlContentString.isEmpty())) {
            	textContentString = htmlContentString;
            }
            
            mailContext.setTextContent(textContentString);
            
            if(textContentString != null) {
            	try {
            		String actualReply = EmailMessageParser.read(textContentString).getReply();
                	mailContext.setContent(actualReply);
            	}
            	catch(Exception e) {
            		LOGGER.error(e);
            	}
            }
            
            List<Map<String, Object>> attachments = MailMessageUtil.getAttachments(message);
            
            MailMessageUtil.getInlineImages(message, attachments);
            
            if(!CollectionUtils.isNullOrEmpty(attachments)) {
            	mailContext.setAttachmentsList(attachments);
            }
            
            
            String[] messageIDList = message.getHeader("Message-ID");
            String[] referenceHeader = message.getHeader("References");
            String[] inReplyTo = message.getHeader("In-Reply-To");
            
            if(messageIDList != null && messageIDList[0] != null) {
            	
            	mailContext.setMessageId(MailMessageUtil.getEmailFromPrettifiedFromAddress.apply(messageIDList[0]));
            }
            if(referenceHeader != null && referenceHeader.length > 0) {
            	List<String> refferenceMails = new ArrayList<String>();
            	for(int i=0;i<referenceHeader.length;i++) {
            		LOGGER.info("referenceHeader ---- "+ i +" -- "+ referenceHeader[i]);
            		refferenceMails.addAll(getReferenceMailList(referenceHeader[i]));
            	}
            	mailContext.setReferenceMessageId(StringUtils.join(refferenceMails, ','));
            }
            if(inReplyTo != null && inReplyTo[0] != null) {
            	mailContext.setInReplyToMessageId(MailMessageUtil.getEmailFromPrettifiedFromAddress.apply(inReplyTo[0]));
            }
            
        } catch (MessagingException | IOException ex) {
            ex.printStackTrace();
        }
        return mailContext;
    }
    
    public static List<String> getReferenceMailList(String refferenceIds) {
		
		int sIndex = 0;
		
		List<String> refferenceMails = new ArrayList<String>();
		
		while(sIndex >= 0 && sIndex < refferenceIds.length()) {
			
			sIndex = refferenceIds.indexOf("<", sIndex);
			if(sIndex < 0) {
				break;
			}
			int eIndex = refferenceIds.indexOf(">", sIndex);
			if(eIndex < 0) {
				break;
			}
			String mail = refferenceIds.substring(sIndex+1, eIndex);
			
			sIndex = eIndex;
			
			refferenceMails.add(mail);
		}
		
		return refferenceMails;
	}
    
    public List<Map<String, Object>> getAttachmentsList() {
        return attachmentsList;
    }

    public void setAttachmentsList(List<Map<String, Object>> attachmentsList) {
        this.attachmentsList = attachmentsList;
    }

    List<Map<String, Object>> attachmentsList = new ArrayList<>();

    public void addAttachmentList(Map<String, Object> attachment) {
        if (attachmentsList == null) {
            attachmentsList = new ArrayList<>();
        }
        attachmentsList.add(attachment);
    }

}
