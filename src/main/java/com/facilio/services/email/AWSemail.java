package com.facilio.services.email;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
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
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

public class AWSemail implements EmailClient {
    private static AWSemail instance =null;
    private static final Object LOCK = new Object();
    private static final Logger LOGGER = LogManager.getLogger(AWSemail.class.getName());
    private static volatile AWSCredentialsProvider credentialsProvider = null;

    AWSemail(){}

    public void sendEmail(JSONObject mailJson) throws Exception  {
            logEmail(mailJson);
            sendEmailViaAws(mailJson);

    }
    public static AWSemail getClient(){
        if (instance == null) {
            instance = new AWSemail();
        }
        return instance;
    }
    private static void sendEmailViaAws(JSONObject mailJson) throws Exception  {
        String toAddress = (String)mailJson.get("to");
        boolean sendEmail = true;
        HashSet<String> to = new HashSet<>();
        if( !FacilioProperties.isProduction() ) {
            if(toAddress != null) {
                for(String address : toAddress.split(",")) {
                    if(address.contains("@facilio.com")) {
                        to.add(address);
                    }
                }
                if(to.size() == 0 ) {
                    sendEmail = false;
                }
            } else {
                sendEmail = false;
            }
        } else {
            for(String address : toAddress.split(",")) {
                if(address!= null && address.contains("@")) {
                    to.add(address);
                }
            }
        }
        if(sendEmail && to.size() > 0) {
            Destination destination = new Destination().withToAddresses(to);
            Content subjectContent = new Content().withData((String) mailJson.get("subject"));
            Content bodyContent = new Content().withData((String) mailJson.get("message"));

            Body body = null;
            if(mailJson.get("mailType") != null && mailJson.get("mailType").equals("html")) {
                body = new Body().withHtml(bodyContent);
            }
            else {
                body = new Body().withText(bodyContent);
            }

            Message message = new Message().withSubject(subjectContent).withBody(body);

            try {
                if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getId() == 104 || AccountUtil.getCurrentOrg().getId() == 151)) {
                    LOGGER.info("Sending email : "+mailJson.toJSONString());
                }
                SendEmailRequest request = new SendEmailRequest().withSource((String) mailJson.get("sender"))
                        .withDestination(destination).withMessage(message);
                AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
                client.sendEmail(request);
                if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 151) {
                    LOGGER.info("Email sent to "+toAddress+"\n"+mailJson);
                }
            } catch (Exception ex) {
                LOGGER.info("Error message: " + toAddress + " " + ex.getMessage());
                throw ex;
            }
        }
    }


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

    public void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception  {
        logEmail(mailJson);
        if(files == null || files.isEmpty()) {
            sendEmail(mailJson);
            return;
        }
        sendEmailViaAws(mailJson, files);

    }

    private void sendEmailViaAws(JSONObject mailJson, Map<String,String> files) throws Exception  {
        if(files == null || files.isEmpty()) {
            sendEmail(mailJson);
            return;
        }
        String toAddress = (String)mailJson.get("to");
        HashSet<String> to = new HashSet<>();
        boolean sendEmail = true;
        if( !FacilioProperties.isProduction() ) {
            if(toAddress != null) {
                for(String address : toAddress.split(",")) {
                    if(address.contains("@facilio.com")) {
                        to.add(address);
                    }
                }
            } else {
                sendEmail = false;
            }
        } else {
            for(String address : toAddress.split(",")) {
                if(address != null && address.contains("@")) {
                    to.add(address);
                }
            }
        }
        if(sendEmail && to.size() > 0) {
            try {
                if (FacilioProperties.isDevelopment()) {
//					mailJson.put("subject", "Local - " + mailJson.get("subject"));
                    return;
                }

                MimeMessage message = getEmailMessage(mailJson, files);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                message.writeTo(outputStream);
                RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
                SendRawEmailRequest request = new SendRawEmailRequest(rawMessage);

                AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
                client.sendRawEmail(request);
                // LOGGER.info("Email sent!");

                if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 151) {
                    LOGGER.info("Email sent to "+toAddress+"\n"+mailJson);
                }

            } catch (Exception ex) {
                LOGGER.info("The email was not sent.");
                LOGGER.info("Error message: " + toAddress+" " + ex.getMessage());
                throw ex;
            }
        }
    }
    public static AWSCredentialsProvider getAWSCredentialsProvider() {
        if(credentialsProvider == null){
            synchronized (LOCK) {
                if(credentialsProvider == null){
                    credentialsProvider = InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(false);
                }
            }
        }
        return credentialsProvider;
    }
    public void sendErrorMail(long orgid,long ml_id,String error)
    {
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
    private static MimeMessage getEmailMessage(JSONObject mailJson, Map<String,String> files) throws Exception {
        String DefaultCharSet = MimeUtility.getDefaultJavaCharset();

        String sender = (String) mailJson.get("sender");

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse((String) mailJson.get("to")));
        message.setSubject((String) mailJson.get("subject"));

        MimeMultipart messageBody = new MimeMultipart("alternative");
        MimeBodyPart textPart = new MimeBodyPart();

        String type = "text/plain; charset=UTF-8";
        if(mailJson.get("mailType") != null && mailJson.get("mailType").equals("html")) {
            type = "text/html; charset=UTF-8";
        }
        textPart.setContent(MimeUtility.encodeText((String) mailJson.get("message"),DefaultCharSet,"B"), type);
        textPart.setHeader("Content-Transfer-Encoding", "base64");
        messageBody.addBodyPart(textPart);

        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(messageBody);

        MimeMultipart messageContent = new MimeMultipart("mixed");
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
        message.addHeader("host", FacilioProperties.getAppDomain());
        return message;
    }
}
