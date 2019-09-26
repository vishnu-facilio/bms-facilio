package com.facilio.services.email;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

class AwsEmailClient extends EmailClient {
    private static AwsEmailClient instance =new AwsEmailClient();
    private static final Object LOCK = new Object();
    private static final Logger LOGGER = LogManager.getLogger(AwsEmailClient.class.getName());
    private static volatile AWSCredentialsProvider credentialsProvider = null;

    AwsEmailClient(){
        LOGGER.error("AWS Email Client created");
    }



    public void sendEmail(JSONObject mailJson) throws Exception  {
            logEmail(mailJson);
            sendEmailViaAws(mailJson);

    }
    public static AwsEmailClient getClient(){
        return instance;
    }
    private void sendEmailViaAws(JSONObject mailJson) throws Exception  {

        if(canSendEmail(mailJson)) {
            Destination destination = new Destination().withToAddresses(getToAddresses(mailJson));
            Content subjectContent = new Content().withData((String) mailJson.get(SUBJECT));
            Content bodyContent = new Content().withData((String) mailJson.get(MESSAGE));

            Body body = null;
            if(mailJson.get(MAIL_TYPE) != null && mailJson.get(MAIL_TYPE).equals(HTML)) {
                body = new Body().withHtml(bodyContent);
            }
            else {
                body = new Body().withText(bodyContent);
            }

            Message message = new Message().withSubject(subjectContent).withBody(body);

            try {

                SendEmailRequest request = new SendEmailRequest().withSource((String) mailJson.get(SENDER))
                        .withDestination(destination).withMessage(message);
                AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
                client.sendEmail(request);
                if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 151) {
                    LOGGER.info("Email sent to "+mailJson);
                }
            } catch (Exception ex) {
                LOGGER.info("Error message: " + ex.getMessage());
                throw ex;
            }
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

        if(canSendEmail(mailJson,files)) {
            try {


                MimeMessage message = getEmailMessage(mailJson, files);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                message.writeTo(outputStream);
                RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
                SendRawEmailRequest request = new SendRawEmailRequest(rawMessage);

                AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
                client.sendRawEmail(request);

            } catch (Exception ex) {
                LOGGER.info("The email was not sent.");
                LOGGER.info("Error message: "+ex.getMessage());
                throw ex;
            }
        }
    }
    private static AWSCredentialsProvider getAWSCredentialsProvider() {
        if(credentialsProvider == null){
            synchronized (LOCK) {
                if(credentialsProvider == null){
                    credentialsProvider = InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(false);
                }
            }
        }
        return credentialsProvider;
    }
    @Override
    protected Session getSession() {
        return Session.getDefaultInstance(new Properties());
    }




}
