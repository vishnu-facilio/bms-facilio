package com.facilio.services.email;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.mail.Session;
import java.util.Map;
import java.util.Properties;

class AwsEmailClient extends EmailClient {

    private static final Logger LOGGER = LogManager.getLogger(AwsEmailClient.class.getName());

    private static final AwsEmailClient INSTANCE =new AwsEmailClient();
    private static final Object LOCK = new Object();
    private static volatile AWSCredentialsProvider credentialsProvider = null;

    private AwsEmailClient(){
        LOGGER.error("AWS Email Client created");
    }

    public String sendEmail(JSONObject mailJson) throws Exception  {
        return sendEmailViaAws(mailJson, null);
    }
    public static EmailClient getClient(){
        return INSTANCE;
    }

    public String sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception  {
        return sendEmailViaAws(mailJson, files);
    }

    private String sendEmailViaAws(JSONObject mailJson, Map<String,String> files) throws Exception  {
        logEmail(mailJson);
        if(canSendEmail(mailJson)) {
            try {
                if(!"stage".equals(FacilioProperties.getEnvironment()) && (files == null || files.isEmpty())) {
                    return AwsUtil.sendMail(mailJson, getEmailAddresses(mailJson, TO), getEmailAddresses(mailJson, CC),getEmailAddresses(mailJson, BCC));
                }
                return AwsUtil.sendMail(mailJson, files);
            } catch (Exception ex) {
                LOGGER.info("The email was not sent.");
                LOGGER.info("Error message: "+ex.getMessage());
                throw ex;
            }
        }
        return null;
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
