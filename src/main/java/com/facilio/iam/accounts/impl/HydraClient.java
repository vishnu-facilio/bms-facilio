package com.facilio.iam.accounts.impl;

import com.facilio.aws.util.FacilioProperties;
import sh.ory.hydra.ApiClient;
import sh.ory.hydra.Configuration;
import sh.ory.hydra.api.AdminApi;
import sh.ory.hydra.api.PublicApi;
import sh.ory.hydra.model.OAuth2Client;
import sh.ory.hydra.model.OAuth2TokenIntrospection;

import java.util.Arrays;

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

    public OAuth2Client createClient(String name) throws Exception {
        OAuth2Client oAuth2Client = new OAuth2Client();
        oAuth2Client.setClientName(name);
        oAuth2Client.tokenEndpointAuthMethod("none");
        oAuth2Client.setGrantTypes(Arrays.asList("client_credentials"));
        oAuth2Client.setTokenEndpointAuthMethod("client_secret_post");
        return adminApi.createOAuth2Client(oAuth2Client);
    }

    public boolean isAlive() throws Exception {
        return adminApi.isInstanceAlive().getStatus().equalsIgnoreCase("ok");
    }

}
