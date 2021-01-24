package com.facilio.accounts.sso;

import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DomainSSO {
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private long appDomainId;

    @Getter
    @Setter
    private String name;

    private AccountSSO.SSOType ssoType = AccountSSO.SSOType.SAML;

    public int getSsoType() {
        if (ssoType != null) {
            return ssoType.getIndex();
        }
        return -1;
    }

    public void setSsoType(int ssoType) {
        this.ssoType = AccountSSO.SSOType.valueOf(ssoType);
    }
    public void setSsoType(AccountSSO.SSOType ssoType) {
        this.ssoType = ssoType;
    }
    public AccountSSO.SSOType getSsoTypeEnum() {
        return ssoType;
    }

    private SSOConfig config;

    public SSOConfig getSSOConfig() {
        return this.config;
    }

    public JSONObject getConfig() throws Exception {
        if (this.config != null) {
            return FieldUtil.getAsJSON(this.config);
        }
        return null;
    }

    public void setConfig(JSONObject configJson) throws Exception {
        this.config = FieldUtil.getAsBeanFromJson(configJson, SamlSSOConfig.class);
    }

    public String getConfigJSON() throws Exception {
        if (this.config != null) {
            return FieldUtil.getAsJSON(this.config).toJSONString();
        }
        return null;
    }

    public void setConfigJSON(String configJsonStr) throws Exception {
        if (configJsonStr != null && !configJsonStr.trim().isEmpty()) {
            JSONObject configJson = (JSONObject) new JSONParser().parse(configJsonStr);
            this.setConfig(configJson);
        }
    }

    @Getter
    @Setter
    private Boolean isActive;

    @Getter
    @Setter
    private Boolean isCreateUser;

    @Getter
    @Setter
    private long createdTime;

    @Getter
    @Setter
    private long createdBy;

    @Getter
    @Setter
    private long modifiedTime;

    @Getter
    @Setter
    private long modifiedBy;
}
