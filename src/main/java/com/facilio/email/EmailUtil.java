package com.facilio.email;

import java.net.URL;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.util.FacilioUtil;

public class EmailUtil {

    private static final Logger LOGGER = LogManager.getLogger(EmailUtil.class.getName());

    private static Properties getSMTPProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", AwsUtil.getConfig("mail.smtp.host"));
        props.put("mail.smtp.socketFactory.port", AwsUtil.getConfig("mail.smtp.socketFactory.port"));
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", AwsUtil.getConfig("mail.smtp.port"));
        String startTls = AwsUtil.getConfig("mail.smtp.starttls.enable");
        if(startTls != null) {
            props.put("mail.smtp.starttls.enable", startTls);
        }

        return props;
    }

    public static void sendEmail(JSONObject mailJson) throws Exception {
        String sender = AwsUtil.getConfig("mail.username");

        Session session = Session.getDefaultInstance(getSMTPProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, AwsUtil.getConfig("mail.password"));
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

    public static void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception {

        String user = AwsUtil.getConfig("mail.username");

        Session session = Session.getDefaultInstance(getSMTPProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, AwsUtil.getConfig("mail.password"));
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
                    if (AwsUtil.isDevelopment()) {
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
                if (AwsUtil.getServerName() != null) {
                    message.addHeader("host", AwsUtil.getServerName());
                }
                Transport.send(message);
            }
        } catch (MessagingException e) {
            LOGGER.info("Exception while sending message ", e);
        }
    }
}
