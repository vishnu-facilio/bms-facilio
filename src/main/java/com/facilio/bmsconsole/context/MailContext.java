package com.facilio.bmsconsole.context;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;

public class MailContext {
    private static Logger log = LogManager.getLogger(MailContext.class.getName());

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

        private long bodyId;
        public long getBodyId() {
            return bodyId;
        }
        public void setBodyId(long bodyId) {
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

    public long getMessageUID() {
        return messageUID;
    }

    public void setMessageUID(long messageUID) {
        this.messageUID = messageUID;
    }

    private long messageUID ;


    public long getSupportMailId() {
        return supportMailId;
    }

    public void setSupportMailId(long supportMailId) {
        this.supportMailId = supportMailId;
    }

    private long supportMailId ;

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

    public static MailContext instance(Message message) {
        MailContext mailContext = new MailContext();

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
                log.info("Message Count " + message.getContentType());
                if (mailContext.contentType.contains("TEXT/PLAIN")
                        || mailContext.contentType.contains("TEXT/HTML")) {
                        Object content = message.getContent();
                        if (content != null) {
                            mailContext.content = content.toString();
                        }
                }
            log.info("Message Count " + message.getReceivedDate());

        } catch (MessagingException | IOException ex) {
            ex.printStackTrace();
        }

        return mailContext;
    }
}
