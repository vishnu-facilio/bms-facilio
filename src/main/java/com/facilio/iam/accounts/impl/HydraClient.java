package com.facilio.iam.accounts.impl;

import com.facilio.aws.util.FacilioProperties;
import sh.ory.hydra.ApiClient;
import sh.ory.hydra.Configuration;
import sh.ory.hydra.api.AdminApi;
import sh.ory.hydra.model.OAuth2TokenIntrospection;

public class HydraClient {
    private AdminApi adminApi;

    public HydraClient() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(FacilioProperties.getHydraUrl());
        this.adminApi = new AdminApi(defaultClient);
    }

    public String getClientIdForToken(String token) throws Exception {
        OAuth2TokenIntrospection oAuth2TokenIntrospection = adminApi.introspectOAuth2Token(token, null);
        Boolean active = oAuth2TokenIntrospection.getActive();
        if (active == null || !active) {
            return null;
        }

        return oAuth2TokenIntrospection.getClientId();
    }


}
