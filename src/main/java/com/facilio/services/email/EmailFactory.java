package com.facilio.services.email;

import com.facilio.aws.util.FacilioProperties;

public class EmailFactory {
    private static EmailClient emailClient = null;

    public static EmailClient getEmailClient(){

        if (emailClient!=null) return emailClient;
        String emailProp = FacilioProperties.getEmailClient();
        switch (emailProp){
            case "aws":
                emailClient= new AwsEmailClient(); break;
            case "smtp":
                emailClient = new FacilioEmailClient(); break;
            default: emailClient = new FacilioEmailClient();
        }
        return emailClient;
    }

}
