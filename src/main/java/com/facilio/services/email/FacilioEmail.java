package com.facilio.services.email;

import java.net.URL;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.time.DateTimeUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class FacilioEmail implements EmailClient {

    private static final Logger LOGGER = LogManager.getLogger(FacilioEmail.class.getName());
    private static FacilioEmail instance = null;
    FacilioEmail(){}
    private static Properties getSMTPProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", FacilioProperties.getConfig("mail.smtp.host"));
        props.put("mail.smtp.socketFactory.port", FacilioProperties.getConfig("mail.smtp.socketFactory.port"));
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", FacilioProperties.getConfig("mail.smtp.port"));
        String startTls = FacilioProperties.getConfig("mail.smtp.starttls.enable");
        if(startTls != null) {
            props.put("mail.smtp.starttls.enable", startTls);
        }

        return props;
    }
    public static FacilioEmail getClient(){
        if(instance==null) return new FacilioEmail();
        return instance;
    }

    public void sendEmail(JSONObject mailJson) throws Exception {
        String sender = FacilioProperties.getConfig("mail.username");

        Session session = Session.getDefaultInstance(getSMTPProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, FacilioProperties.getConfig("mail.password"));
                    }
                });

        try {

            if(sender != null) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((String) mailJson.get("to")));
                message.setSubject((String) mailJson.get("subject"));
                message.setText((String) mailJson.get("message"));

                Transport.send(message);

                LOGGER.info("Sent email to " + mailJson.get("to") + " subject " + mailJson.get("subject"));
            } else {
                LOGGER.info("mail.user is not configured, please configure in facilio.properties");
            }

        } catch (MessagingException e) {
            LOGGER.info("Exception while sending message ", e);
        }
    }

    public void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception {

        String user = FacilioProperties.getConfig("mail.username");

        Session session = Session.getDefaultInstance(getSMTPProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, FacilioProperties.getConfig("mail.password"));
                    }
                });

        try {

            if (user != null) {

                String DefaultCharSet = MimeUtility.getDefaultJavaCharset();

                String sender = (String) mailJson.get("sender");

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse((String) mailJson.get("to")));
                message.setSubject((String) mailJson.get("subject"));

                MimeMultipart messageBody = new MimeMultipart("alternative");
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(MimeUtility.encodeText((String) mailJson.get("message"), DefaultCharSet, "B"), "text/plain; charset=UTF-8");
                textPart.setHeader("Content-Transfer-Encoding", "base64");
                messageBody.addBodyPart(textPart);

                MimeBodyPart wrap = new MimeBodyPart();
                wrap.setContent(messageBody);

                MimeMultipart messageContent = new MimeMultipart("mixed");
                messageContent.addBodyPart(wrap);

                for (Map.Entry<String, String> file : files.entrySet()) {
                    String fileUrl = file.getValue();
                    if (fileUrl == null) {    // Temporary check for local filestore.
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
                if (FacilioProperties.getServerName() != null) {
                    message.addHeader("host", FacilioProperties.getServerName());
                }
                Transport.send(message);
            }
        } catch (MessagingException e) {
            LOGGER.info("Exception while sending message ", e);
        }
    }

    @Override
    public void sendErrorMail(long orgid, long ml_id, String error) {
        try
        {
            JSONObject json = new JSONObject();
            json.put("sender", "mlerror@facilio.com");
            json.put("to", "ai@facilio.com");
            json.put("subject", orgid+" - "+ml_id);

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
            json.put("message", body.toString());

            sendEmail(json);
        }
        catch(Exception e)
        {
            LOGGER.error("Error while sending mail ",e);
        }
    }

    @Override
    public void logEmail(JSONObject mailJson) {
        try {
            if (AccountUtil.getCurrentOrg() != null) {
                String toAddress = (String) mailJson.get("to");
                if (!"error+alert@facilio.com".equals(toAddress) && !"error@facilio.com".equals(toAddress)) {
                    toAddress = toAddress == null ? "" : toAddress;
                    JSONObject info = new JSONObject();
                    info.put("subject", mailJson.get("subject"));
                    CommonAPI.addNotificationLogger(CommonAPI.NotificationType.EMAIL, toAddress, info);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while logging email", e);
        }
    }
}
