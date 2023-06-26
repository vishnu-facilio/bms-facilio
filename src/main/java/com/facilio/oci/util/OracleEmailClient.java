package com.facilio.oci.util;

import com.facilio.aws.util.FacilioProperties;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.email.EmailClient;
import com.oracle.bmc.email.model.CreateSenderDetails;
import com.oracle.bmc.email.requests.CreateSenderRequest;
import com.oracle.bmc.email.responses.CreateSenderResponse;
import com.oracle.bmc.model.BmcException;
import lombok.extern.log4j.Log4j;

@Log4j
public class OracleEmailClient {

    private static EmailClient CLIENT;
    private final String COMPARTMENT_ID = FacilioProperties.getConfig("oci.compartmentid");

    public static EmailClient initClient(AbstractAuthenticationDetailsProvider auth) {
        CLIENT = EmailClient.builder()
                .region(Region.UK_LONDON_1)
                .build(auth);
        return CLIENT;
    }

    private EmailClient getClient() {
        if(CLIENT == null) {
            return OracleEmailClient.initClient(OCIUtil.getAuth());
        }
        return CLIENT;
    }

    public CreateSenderResponse addSender(String senderAddr) {
        CreateSenderDetails createSenderDetails = CreateSenderDetails.builder()
                .compartmentId(COMPARTMENT_ID)
                .emailAddress(senderAddr)
                .build();

        CreateSenderRequest createSenderRequest = CreateSenderRequest.builder()
                .createSenderDetails(createSenderDetails)
                .build();

        try {
            return getClient().createSender(createSenderRequest);
        } catch (BmcException e) {
            LOGGER.error(e);
            return null;
        }
    }

}
