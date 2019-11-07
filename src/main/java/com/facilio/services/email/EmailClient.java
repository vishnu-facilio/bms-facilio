package com.facilio.services.email;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.time.DateTimeUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Session;
import javax.mail.internet.*;
import java.net.URL;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;

public abstract class EmailClient {

    private static final Logger LOGGER = LogManager.getLogger(EmailClient.class.getName());
    static final String SENDER="sender";
    static final String MESSAGE="message";
    static final String SUBJECT="subject";
    static final String MAIL_TYPE="mailType";
    static final String CONTENT_TYPE_TEXT_HTML="text/html; charset=UTF-8";
    private static final String CONTENT_TYPE_TEXT_PLAIN="text/plain; charset=UTF-8";
    private static final String MIME_MULTIPART_SUBTYPE_ALTERNATIVE="alternative";
    private static final String MIME_MULTIPART_SUBTYPE_MIXED="mixed";
    private static final String CONTENT_TRANSFER_ENCODING="Content-Transfer-Encoding";
    private static final String BASE_64 = "base64";
    static final String HTML="html";
    private static final String HOST = "host";
    static final String TO = "to";
    private static final String ERROR_MAIL_FROM="mlerror@facilio.com";
    private static final String ERROR_MAIL_TO="ai@facilio.com";
    private static final String ERROR_AT_FACILIO="error@facilio.com";
    private static final String ERROR_AND_ALERT_AT_FACILIO="error+alert@facilio.com";

    protected abstract Session getSession();
    public abstract void sendEmail(JSONObject mailJson) throws Exception;
    public abstract void sendEmail(JSONObject mailJson, Map<String, String> files) throws Exception;

    MimeMessage getEmailMessage(JSONObject mailJson, Map<String, String> files) throws Exception {
        String DefaultCharSet = MimeUtility.getDefaultJavaCharset();

        String sender = (String) mailJson.get(SENDER);

        Session session = getSession();
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse((String) mailJson.get("to")));
        message.setSubject((String) mailJson.get(SUBJECT));

        MimeMultipart messageBody = new MimeMultipart(MIME_MULTIPART_SUBTYPE_ALTERNATIVE);
        MimeBodyPart textPart = new MimeBodyPart();

        String type = CONTENT_TYPE_TEXT_PLAIN;
        if(mailJson.get(MAIL_TYPE) != null && mailJson.get(MAIL_TYPE).equals(HTML)) {
            type = CONTENT_TYPE_TEXT_HTML;
        }
        textPart.setContent(MimeUtility.encodeText((String) mailJson.get(MESSAGE),DefaultCharSet,"B"), type);
        textPart.setHeader(CONTENT_TRANSFER_ENCODING, BASE_64);
        messageBody.addBodyPart(textPart);

        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(messageBody);

        MimeMultipart messageContent = new MimeMultipart(MIME_MULTIPART_SUBTYPE_MIXED);
        messageContent.addBodyPart(wrap);

        for (Map.Entry<String, String> file : files.entrySet()) {
            String fileUrl = file.getValue();
            if(fileUrl == null) {	// Temporary check for local filestore.
                continue;
            }
            MimeBodyPart attachment = new MimeBodyPart();
            DataSource fileDataSource = null;
            if (FacilioProperties.isDevelopment()) {
                fileDataSource = new FileDataSource(fileUrl);
            } else {
                URL url = new URL(fileUrl);
                fileDataSource = new URLDataSource(url);
            }
            attachment.setDataHandler(new DataHandler(fileDataSource));
            attachment.setFileName(file.getKey());
            messageContent.addBodyPart(attachment);
        }

        message.setContent(messageContent);
        message.addHeader(HOST, FacilioProperties.getAppDomain());
        return message;
    }
    public void sendErrorMail(long orgid,long ml_id,String error)
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put(SENDER, ERROR_MAIL_FROM);
            json.put(TO, ERROR_MAIL_TO);
            json.put(SUBJECT, orgid+" - "+ml_id);

            StringBuilder body = new StringBuilder()
                    .append(error)
                    .append("\n\nInfo : \n--------\n")
                    .append("\n Org Time : ").append(DateTimeUtil.getDateTime())
                    .append("\n Indian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
                    .append("\n\nMsg : ")
                    .append(error)
                    .append("\n\nOrg Info : \n--------\n")
                    .append(orgid)
                    ;
            json.put(MESSAGE, body.toString());

            sendEmail(json);
        }
        catch(Exception e)
        {
            LOGGER.error("Error while sending mail ",e);
        }
    }
    void logEmail(JSONObject mailJson) {
        try {
            if (AccountUtil.getCurrentOrg() != null) {
                String toAddress = (String) mailJson.get("to");
                if (!ERROR_AND_ALERT_AT_FACILIO.equals(toAddress) && !ERROR_AT_FACILIO.equals(toAddress)) {
                    toAddress = toAddress == null ? "" : toAddress;
                    JSONObject info = new JSONObject();
                    info.put(SUBJECT, mailJson.get(SUBJECT));
                    CommonAPI.addNotificationLogger(CommonAPI.NotificationType.EMAIL, toAddress, info);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while logging email", e);
        }
    }
    boolean canSendEmail(JSONObject mailJson, Map<String, String> files) throws Exception {
        if(files == null || files.isEmpty() || FacilioProperties.isDevelopment()) {
            sendEmail(mailJson);
            return false;
        }
        return canSendEmail(mailJson);
    }

    boolean canSendEmail(JSONObject mailJson) {

        return (getToAddresses(mailJson).size() >0 );
    }
    HashSet<String> getToAddresses(JSONObject mailJson){
        String toAddress = (String)mailJson.get(TO);
        HashSet<String> to = new HashSet<>();
        if( !FacilioProperties.isProduction() ) {
            if(toAddress != null) {
                for(String address : toAddress.split(",")) {
                    if(address.contains("@facilio.com")) {
                        to.add(address);
                    }
                }
            }
        } else {
            for(String address : toAddress.split(",")) {
                if(address != null && address.contains("@")) {
                    to.add(address);
                }
            }
        }
        return to;
    }
}
