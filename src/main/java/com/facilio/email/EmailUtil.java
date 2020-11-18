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

import com.facilio.aws.util.AwsUtil;
import com.facilio.services.email.EmailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;

public class EmailUtil {

    private static final Logger LOGGER = LogManager.getLogger(EmailUtil.class.getName());

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

    public static void sendEmail(JSONObject mailJson) throws Exception {
        String sender = FacilioProperties.getConfig("mail.from") != null ? FacilioProperties.getConfig("mail.from") : FacilioProperties.getConfig("mail.username");

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

    public static void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception {

        String user = FacilioProperties.getConfig("mail.username");

        Session session = Session.getDefaultInstance(getSMTPProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, FacilioProperties.getConfig("mail.password"));
                    }
                });

        try {

            if (user != null) {
                MimeMessage message = EmailClient.constructMimeMessageContent(mailJson,session,files);

                if (FacilioProperties.getServerName() != null) {
                    message.addHeader("host", FacilioProperties.getServerName());
                }
                Transport.send(message);
            }
        } catch (MessagingException e) {
            LOGGER.info("Exception while sending message ", e);
        }
    }
}
