package com.facilio.bmsconsole.context;

import com.facilio.chain.FacilioContext;

public class APIClient extends FacilioContext {
    private String name;
    private long uid;
    private APIClientType authType;

    public void setAuthTypeEnum(APIClientType authType) {
        this.authType = authType;
    }

    public void setAuthType(long authType) {
        this.authType = APIClientType.valueOf((int) authType);
    }

    public APIClientType getAuthTypeEnum() {
        return authType;
    }

    public long getAuthType() {
        if (authType != null) {
            return authType.getValue();
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public enum APIClientType {
        OAUTH2,
        APIKEY;


        public int getValue() {
            return ordinal() + 1;
        }

        public static APIClientType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values() [value - 1];
            }
            return null;
        }
    }
}
