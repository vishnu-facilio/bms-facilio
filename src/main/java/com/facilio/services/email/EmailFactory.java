package com.facilio.services.email;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.email.MandrillEmailClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EmailFactory {

    private static final Logger LOGGER = LogManager.getLogger(EmailFactory.class.getName());
    private static EmailClient emailClient = null;

    public static EmailClient getEmailClient(){

        if (emailClient != null) return emailClient;
        String emailProp = FacilioProperties.getEmailClient();
        if ("aws".equals(emailProp)) {
            emailClient = AwsEmailClient.getClient();
        } else if ("oci".equals(emailProp)) {
            emailClient = OracleSMTPEmailClient.getClient();
        }else if ( "smtp".equals(emailProp)) {
            emailClient = SMTPEmailClient.getClient();
        } else if ( "mandrill".equals(emailProp)) {
            emailClient = MandrillEmailClient.getClient();
        }
        else if("azure".equals(emailProp)){
            emailClient = AzureEmailClient.getClient();
        }
        else if("sendgrid".equals(emailProp)){
            emailClient = SGEmailClient.getClient();
        }else {
            LOGGER.info("Email client is not configured");
        }
        return emailClient;
    }
}
