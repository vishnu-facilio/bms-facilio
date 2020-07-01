package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.commands.AddAttachmentCommand;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.MailContext;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.bmsconsoleV3.context.quotation.TaxContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3MailMessageContext extends ModuleBaseWithCustomFields {

    private static Logger logger = LogManager.getLogger(V3MailMessageContext.class.getName());
    private static final long serialVersionUID = 1L;
    private String from;
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    private String to;
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }


    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    private String replyTo;


    public String getCc() {
        return cc;
    }

    private String cc;

    private String bcc;

    private String subject;
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    private Long bodyId;
    public Long getBodyId() {
        return bodyId;
    }
    public void setBodyId(Long bodyId) {
        this.bodyId = bodyId;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }


    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    private Boolean html;
    public Boolean getHtml() {
        return html;
    }
    public void setHtml(Boolean html) {
        this.html = html;
    }
    public boolean isHtml() {
        if (html != null) {
            return html.booleanValue();
        }
        return false;
    }

    public Long getMessageUID() {
        return messageUID;
    }

    public void setMessageUID(Long messageUID) {
        this.messageUID = messageUID;
    }

    private Long messageUID ;


    public Long getSupportMailId() {
        return supportMailId;
    }

    public void setSupportMailId(Long supportMailId) {
        this.supportMailId = supportMailId;
    }

    private Long supportMailId ;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType;

    public Long getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Long receivedDate) {
        this.receivedDate = receivedDate;
    }

    private Long receivedDate;


    public Long getSentDate() {
        return sentDate;
    }

    public void setSentDate(Long sentDate) {
        this.sentDate = sentDate;
    }

    private Long sentDate;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;


    public enum Flags implements FacilioEnum {
        ANSWERED("Answered"),
        DELETED("Deleted"),
        DRAFT("Draft"),
        FLAGGED( "Flagged"),
        RECENT("Recent"),
        SEEN("Seen"),
        USER("User");

        private String name;

        Flags(String name) {
            this.name = name;
        }

        public static Flags valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public Flags getFlagEnum() {
        return flag;
    }
    public int getFlag() {
        if (flag != null) {
            return flag.getIndex();
        }
        return -1;
    }

    public void setFlag(int flag) {
        this.flag = Flags.valueOf(flag);
    }

    private Flags flag;

//    private List<ReadingEventContext> readingEvent;
    public static V3MailMessageContext instance(Message message) throws Exception {
        V3MailMessageContext mailContext = new V3MailMessageContext();

        try {
            mailContext.from = InternetAddress.toString(message.getFrom());
            mailContext.message = message.getSubject();
            mailContext.replyTo = InternetAddress.toString(message
                    .getReplyTo());
            mailContext.to = InternetAddress.toString(message
                    .getRecipients(Message.RecipientType.TO));
            mailContext.subject = message.getSubject();
            mailContext.contentType = message.getContentType();
            mailContext.receivedDate = message.getReceivedDate().getTime();
            mailContext.sentDate = message.getSentDate().getTime();
            if (message.getRecipients(Message.RecipientType.CC) != null) {
                mailContext.cc = message.getRecipients(Message.RecipientType.CC).toString();
            }
            if (message.getRecipients(Message.RecipientType.BCC) != null) {
                mailContext.bcc = message.getRecipients(Message.RecipientType.BCC).toString();
            }
            mailContext.content = parseMessageContent(message, mailContext);
        } catch (MessagingException | IOException ex) {
            ex.printStackTrace();
        }

        return mailContext;
    }
    private static String parseMessageContent(Part part, V3MailMessageContext mailContext) throws Exception {
        if (part.isMimeType("text/*")) {
            String s = (String) part.getContent();
            mailContext.setContent(s);
        }
        if (part.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart)part.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text =  (String) bp.getContent();
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = (String) bp.getContent();
                    if (s != null)
                        return s;
                } else {
                    return getMimeMultipartFromMessage(((MimeMultipart)part.getContent()), mailContext);
                }
            }
            mailContext.setContent(text);
        } else if (part.isMimeType("multipart/*")) {
            return getMimeMultipartFromMessage(((MimeMultipart)part.getContent()), mailContext);
        }

        return null;
    }

    public List<Object> getAttachmentsList() {
        return attachmentsList;
    }

    public void setAttachmentsList(List<Object> attachmentsList) {
        this.attachmentsList = attachmentsList;
    }

    List<Object> attachmentsList = new ArrayList<>();

    public void addAttachmentList(Object attachment) {
        if (attachmentsList == null) {
            attachmentsList = new ArrayList<>();
        }
        attachmentsList.add(attachment);
    }

    private static String getMimeMultipartFromMessage(MimeMultipart mimeMultipart, V3MailMessageContext mailContext) throws Exception {
        String result = "";
        int partCount = mimeMultipart.getCount();
        for (int i = 0; i < partCount; i++) {
            MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getMimeMultipartFromMessage((MimeMultipart) bodyPart.getContent(), mailContext);
            } else  if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                String fileName = bodyPart.getFileName();
                MimeMessage attachmentMessage = new MimeMessage(null, bodyPart.getInputStream());
                MimeMessageParser parser = new MimeMessageParser(attachmentMessage);
                parser.parse();
                JSONObject attachmentObject = new JSONObject();
                attachmentObject.put("attachedFilesFileName", bodyPart.getFileName());
                attachmentObject.put("attachedFilesContentType", bodyPart.getContentType());
                File file = File.createTempFile(fileName, "");
                FileUtils.copyInputStreamToFile(bodyPart.getInputStream(), file);
                attachmentObject.put("attachedFiles", file);
                mailContext.addAttachmentList(attachmentObject);
            }
        }
        return result;
    }
}
