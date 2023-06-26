package com.facilio.oci.util;

import com.facilio.aws.util.FacilioProperties;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;

import java.io.IOException;

public class OCIUtil {


    private static AbstractAuthenticationDetailsProvider AUTH_PROVIDER;

    private static OracleStorageClient OBJECT_STORAGE_CLIENT = null;
    private static OracleEmailClient EMAIL_CLIENT = null;

    public static OracleStorageClient getOracleStorageClient() {
        if(OBJECT_STORAGE_CLIENT == null) {
            OBJECT_STORAGE_CLIENT = new OracleStorageClient();
        }
        return OBJECT_STORAGE_CLIENT;
    }

    public static OracleEmailClient getOracleEmailClient() {
        if(EMAIL_CLIENT == null) {
            EMAIL_CLIENT = new OracleEmailClient();
        }
        return EMAIL_CLIENT;
    }

    public static AbstractAuthenticationDetailsProvider getAuth() {
        if(AUTH_PROVIDER == null) {
            if (FacilioProperties.isDevelopment()) {
                return getConfPrincipleAuth();
            } else {
                return getInstancePrincipleAuth();
            }
        }
        return AUTH_PROVIDER;
    }

    private static AbstractAuthenticationDetailsProvider getConfPrincipleAuth() {
        try {
            AUTH_PROVIDER = new ConfigFileAuthenticationDetailsProvider(ConfigFileReader.parseDefault());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return AUTH_PROVIDER;
    }

    private static AbstractAuthenticationDetailsProvider getInstancePrincipleAuth() {
        AUTH_PROVIDER = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        return AUTH_PROVIDER;
    }

}
