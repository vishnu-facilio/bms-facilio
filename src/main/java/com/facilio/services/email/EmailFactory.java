package com.facilio.services.email;

import com.facilio.aws.util.FacilioProperties;

public class EmailFactory {
    private static EmailClient emailClient = null;

    public static EmailClient getEmailClient(){

        if (emailClient!=null) return emailClient;
        String emailProp = FacilioProperties.getEmailClient();
        if ("aws".equals(emailProp)) {
            emailClient = AwsEmailClient.getClient();
        } else {
            emailClient = FacilioEmailClient.getClient();
        }
        return emailClient;
    }

}
