package com.facilio.services.email;

import com.facilio.aws.util.FacilioProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

class SMTPEmailClient extends EmailClient {

    private static final Logger LOGGER = LogManager.getLogger(SMTPEmailClient.class.getName());
    private static final SMTPEmailClient INSTANCE = new SMTPEmailClient();

    private static final String MAIL_USERNAME="mail.username";
    private static final String MAIL_FROM="mail.from";
    private static final String MAIL_PASSWORD="mail.password";
    private static final String SMTP_HOST="mail.smtp.host";
    private static final String SMTP_AUTH="mail.smtp.auth";
    private static final String SMTP_PORT="mail.smtp.port";

    private static final String SMTP_STARTTLS="mail.smtp.starttls.enable";
    private static final String SSL_SOCKET_FACTORY="javax.net.ssl.SSLSocketFactory";
    private static final String SSL_FACTORY_CLASS="mail.smtp.socketFactory.class";
    private static final String SOCKET_FACTORY_PORT= "mail.smtp.socketFactory.port";


    public SMTPEmailClient(){
        LOGGER.info("Facilio SMTP Email client created");
    }

    private static Properties getSMTPProperties() {
        Properties props = new Properties();
        props.put(SMTP_HOST, FacilioProperties.getConfig(SMTP_HOST));
        props.put(SOCKET_FACTORY_PORT, FacilioProperties.getConfig(SOCKET_FACTORY_PORT));
        props.put(SSL_FACTORY_CLASS,SSL_SOCKET_FACTORY);
        props.put(SMTP_AUTH, "true");
        props.put(SMTP_PORT, FacilioProperties.getConfig(SMTP_PORT));
        String startTls = FacilioProperties.getConfig(SMTP_STARTTLS);
        if(startTls != null) {
            props.put(SMTP_STARTTLS, startTls);
        }

        return props;
    }
    public static EmailClient getClient(){
        return INSTANCE;
    }

    @Override
    public String sendEmailImpl(JSONObject mailJson) {
        String sender = FacilioProperties.getConfig(MAIL_FROM) != null ? FacilioProperties.getConfig(MAIL_FROM) : FacilioProperties.getConfig(MAIL_USERNAME);
        return sendMail(sender, mailJson);
    }

    protected String sendMail(String sender, JSONObject mailJson) {
        Session session = getSession();
        try {

            if(sender != null) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((String) mailJson.get(TO)));
                String cc = (String) mailJson.get("cc");
                if (cc != null && StringUtils.isNotEmpty(cc)) {
                    message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
                }
                String bcc = (String) mailJson.get("bcc");
                if (bcc != null && StringUtils.isNotEmpty(bcc)) {
                    message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
                }
                message.setSubject((String) mailJson.get(SUBJECT));

                if(mailJson.get(MAIL_TYPE) != null && mailJson.get(MAIL_TYPE).equals(HTML)) {
                    message.setContent(mailJson.get(MESSAGE),CONTENT_TYPE_TEXT_HTML);
                }
                else {
                    message.setText((String) mailJson.get(MESSAGE));
                }

                Transport.send(message);

                LOGGER.info("Sent email to " + mailJson.get(TO) + " "+SUBJECT+" " + mailJson.get(SUBJECT));
            } else {
                LOGGER.info("mail.user is not configured, please configure in facilio.properties");
            }

        } catch (MessagingException e) {
            LOGGER.info("Exception while sending message ", e);
        }
        return null;
    }

    @Override
    public String sendEmailImpl(JSONObject mailJson, Map<String,String> files) throws Exception {
        try {
            if(canSendEmail(mailJson,files)) {
                    MimeMessage message = getEmailMessage(mailJson, files);
                    String sender = FacilioProperties.getConfig(MAIL_FROM) != null ? FacilioProperties.getConfig(MAIL_FROM) : FacilioProperties.getConfig(MAIL_USERNAME);
                    if (StringUtils.isNotEmpty(sender)) {
                        message.setFrom(sender);
                    }
                    Transport.send(message);
            }
        }catch (Exception ex){
            LOGGER.info("Exception while sending message ", ex);
        }
        return null;
    }

    protected Session getSession() {
        String sender = FacilioProperties.getConfig(MAIL_USERNAME);
        return Session.getDefaultInstance(getSMTPProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, FacilioProperties.getConfig(MAIL_PASSWORD));
                    }
                });
    }

    @Override
    public JSONObject getSuppressionStatus(String email) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "not handled in smtp email client");
        return jsonObject;
    }

}
