package com.facilio.services.email;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.*;
import java.net.URL;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;

public abstract class EmailClient {

    private static final Logger LOGGER = LogManager.getLogger(EmailClient.class.getName());
    public static final String SENDER="sender";
    public static final String MESSAGE="message";
    public static final String SUBJECT="subject";
    public static final String CC="cc";
    public static final String BCC="bcc";
    public static final String MAIL_TYPE="mailType";
    public static final String CONTENT_TYPE_TEXT_HTML="text/html; charset=UTF-8";
    public static final String CONTENT_TYPE_TEXT_PLAIN="text/plain; charset=UTF-8";
    public static final String MIME_MULTIPART_SUBTYPE_ALTERNATIVE="alternative";
    public static final String MIME_MULTIPART_SUBTYPE_MIXED="mixed";
    public static final String HEADER="header";
    public static final String CONTENT_TRANSFER_ENCODING="Content-Transfer-Encoding";
    public static final String BASE_64 = "base64";
    public static final String HTML="html";
    public static final String HOST = "host";
    public static final String TO = "to";
    public static final String ERROR_MAIL_FROM="mlerror@facilio.com";
    public static final String ERROR_MAIL_TO="ai@facilio.com";
    public static final String ERROR_AT_FACILIO="error@facilio.com";
    public static final String ERROR_AND_ALERT_AT_FACILIO="error+alert@facilio.com";

    protected abstract Session getSession();
    public abstract void sendEmail(JSONObject mailJson) throws Exception;
    public abstract void sendEmail(JSONObject mailJson, Map<String, String> files) throws Exception;

    MimeMessage getEmailMessage(JSONObject mailJson, Map<String, String> files) throws Exception {


        Session session = getSession();
        MimeMessage message = constructMimeMessageContent(mailJson,session,files);
        message.addHeader(HOST, FacilioProperties.getAppDomain());
        return message;
    }

    public static String getNoReplyFromEmail() {
        return getFromEmail("noreply");
    }

    public static String getFromEmail(String localPart) {
        StringBuilder builder = new StringBuilder(localPart)
                                        .append("@");
        if (AccountUtil.getCurrentOrg() != null) {
            builder.append(AccountUtil.getCurrentOrg().getDomain()).append(".");
        }
        builder.append("facilio.com");

        return builder.toString();
    }

    public static MimeMessage constructMimeMessageContent(JSONObject mailJson, Session session,Map<String, String> files) throws Exception {
        //String DefaultCharSet = MimeUtility.getDefaultJavaCharset();

        String sender = (String) mailJson.get(SENDER);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse((String) mailJson.get("to")));
        String cc = (String) mailJson.get("cc");
        if (cc != null && StringUtils.isNotEmpty(cc)) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        String bcc = (String) mailJson.get("bcc");
        if (bcc != null && StringUtils.isNotEmpty(bcc)) {
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
        }
        message.setSubject((String) mailJson.get(SUBJECT));

        MimeMultipart messageBody = new MimeMultipart(MIME_MULTIPART_SUBTYPE_ALTERNATIVE);
        MimeBodyPart textPart = new MimeBodyPart();

        String type = CONTENT_TYPE_TEXT_PLAIN;
        if(mailJson.get(MAIL_TYPE) != null && mailJson.get(MAIL_TYPE).equals(HTML)) {
            type = CONTENT_TYPE_TEXT_HTML;
        }
        textPart.setContent((String) mailJson.get(MESSAGE), type);
        textPart.setHeader(CONTENT_TRANSFER_ENCODING, BASE_64);
        messageBody.addBodyPart(textPart);

        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(messageBody);

        MimeMultipart messageContent = new MimeMultipart(MIME_MULTIPART_SUBTYPE_MIXED);
        messageContent.addBodyPart(wrap);

        if(files != null) {
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
        }
        
        if(mailJson.get(HEADER) != null) {
        	JSONObject headerJSON = (JSONObject)mailJson.get(HEADER);
        	
        	for(Object headerNameObject : headerJSON.keySet()) {
        		String headerName = (String) headerNameObject;
        		String headerValue = (String) headerJSON.get(headerNameObject);
        		
        		message.setHeader(headerName, headerValue);
        	}
        }

        message.setContent(messageContent);
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
            String toAddress = (String) mailJson.get("to");
            String ccAddress = null, bccAddress = null;
            if (mailJson.get("cc") != null) {
                ccAddress = (String) mailJson.get("cc");
            }
            if (mailJson.get("bcc") != null) {
                bccAddress = (String) mailJson.get("bcc");
            }
            if (!ERROR_AND_ALERT_AT_FACILIO.equals(toAddress) && !ERROR_AT_FACILIO.equals(toAddress)) {
                toAddress = toAddress == null ? "" : toAddress;
                JSONObject info = new JSONObject();
                info.put(SUBJECT, mailJson.get(SUBJECT));
                if (mailJson.get(CC) != null) {
                    info.put("cc", mailJson.get(CC));
                }
                if (mailJson.get(BCC) != null) {
                    info.put("bcc", mailJson.get(BCC));
                }
                CommonAPI.addNotificationLogger(CommonAPI.NotificationType.EMAIL, toAddress, info);
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
        if (FacilioProperties.isDevelopment()) {
            return false;
        }
        return (getEmailAddresses(mailJson, TO).size() >0 );
    }
    HashSet<String> getEmailAddresses(JSONObject mailJson, String key){
        String emailAddressString = (String)mailJson.get(key);
        HashSet<String> emailAddress = new HashSet<>();
        if( !FacilioProperties.isProduction() ) {
            if(emailAddressString != null) {
                for(String address : emailAddressString.split(",")) {
                    if(address.contains("@facilio.com")) {
                        emailAddress.add(address);
                    }
                }
            }
        } else {
            if(emailAddressString != null) {
                for (String address : emailAddressString.split(",")) {
                    if (address != null && address.contains("@")) {
                        emailAddress.add(address);
                    }
                }
            }
        }
        return emailAddress;
    }

}
