package com.facilio.bmsconsoleV3.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.fs.FileInfo;
import com.facilio.pdf.PdfUtil;
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
    private String message;
    private Boolean html;
    private Long messageUID;
    private Long supportMailId;
    private Long parentId;
    private String contentType;
    private Long receivedDate;
    private Long sentDate;
    private String content;

    public static BaseMailMessageContext instance(Message message) throws Exception {
        BaseMailMessageContext mailContext = new BaseMailMessageContext();

        try {
            Address[] froms = message.getFrom();
            String from = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
            mailContext.from = from;
            mailContext.message = message.getSubject();
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
                mailContext.cc = message.getRecipients(Message.RecipientType.CC).toString();
            }
            if (message.getRecipients(Message.RecipientType.BCC) != null) {
                mailContext.bcc = message.getRecipients(Message.RecipientType.BCC).toString();
            }
            mailContext.content = MailMessageUtil.parseMessageContent(message, mailContext);
            
            String[] messageIDList = message.getHeader("Message-ID");
            String[] referenceHeader = message.getHeader("References");
            String[] inReplyTo = message.getHeader("In-Reply-To");
            
            if(messageIDList != null && messageIDList[0] != null) {
            	
            	mailContext.setMessageId(MailMessageUtil.getFirstMessageId.apply(messageIDList[0]));
            }
            if(referenceHeader != null && referenceHeader[0] != null) {
            	mailContext.setReferenceMessageId(MailMessageUtil.getFirstMessageId.apply(referenceHeader[0]));
            }
            if(inReplyTo != null && inReplyTo[0] != null) {
            	mailContext.setInReplyToMessageId(MailMessageUtil.getFirstMessageId.apply(inReplyTo[0]));
            }
            
        } catch (MessagingException | IOException ex) {
            ex.printStackTrace();
        }
        return mailContext;
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
