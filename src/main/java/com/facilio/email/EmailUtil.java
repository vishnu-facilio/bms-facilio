package com.facilio.email;

import com.facilio.aws.util.AwsUtil;
import com.facilio.util.FacilioUtil;
import org.json.simple.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

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

                System.out.println("Sent email to " + mailJson.get("to") + " subject " + mailJson.get("subject"));
            } else {
                System.out.println("mail.user is not configured, please configure in facilio.properties");
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
