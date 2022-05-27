package com.facilio.bmsconsoleV3.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.util.CollectionUtils;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.util.EmailMessageParser;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

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
            
            if(textContentString != null) {
            	try {
            		String actualReply = EmailMessageParser.read(textContentString).getReply();
                	mailContext.setTextContent(textContentString);
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
