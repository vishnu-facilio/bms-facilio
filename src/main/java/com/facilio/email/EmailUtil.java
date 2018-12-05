package com.facilio.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.util.FacilioUtil;

public class EmailUtil {

    private static final Logger LOGGER = LogManager.getLogger(EmailUtil.class.getName());

    public static void sendEmail(JSONObject mailJson) throws Exception {
        String sender = FacilioUtil.getProperty("mail.username");

        Properties props = new Properties();
        props.put("mail.smtp.host", FacilioUtil.getProperty("mail.smtp.host"));
        props.put("mail.smtp.socketFactory.port", FacilioUtil.getProperty("mail.smtp.socketFactory.port"));
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", FacilioUtil.getProperty("mail.smtp.port"));

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, FacilioUtil.getProperty("mail.password"));
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
            throw new RuntimeException(e);
        }
    }
}
