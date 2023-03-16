package com.facilio.services.email;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.util.DBConf;
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
        LOGGER.info("OG_MAIL_LOG :: AWS Email Client created");
    }

    @Override
    public String sendEmailImpl(JSONObject mailJson) throws Exception  {
        return sendEmailImpl(mailJson, null);
    }
    public static EmailClient getClient(){
        return INSTANCE;
    }

    @Override
    public String sendEmailImpl(JSONObject mailJson, Map<String,String> files) throws Exception  {
        return sendEmailViaAws(mailJson, files);
    }

    private String sendEmailViaAws(JSONObject mailJson, Map<String,String> files) throws Exception  {
        if(canSendEmail(mailJson)) {
            try {
                boolean isTrackingConfNotFound = DBConf.getInstance().getMailTrackingConfName()==null;
                if(isTrackingConfNotFound) { // normal behaviour for production env
                    if(files==null || files.isEmpty()) {
                        return AwsUtil.sendMail(mailJson, getEmailAddresses(mailJson, TO), getEmailAddresses(mailJson, CC),getEmailAddresses(mailJson, BCC));
                    }
                }
                return AwsUtil.sendMail(mailJson, files);
            } catch (Exception ex) {
                LOGGER.info("OG_MAIL_ERROR :: The email was not sent.");
                LOGGER.info("OG_MAIL_ERROR :: Error message: "+ex.getMessage());
                throw ex;
            }
        } else {
            LOGGER.info("OG_MAIL_LOG :: Can't send email. Because the TO address is empty");
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
