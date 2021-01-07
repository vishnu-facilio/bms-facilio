package com.facilio.accounts.sso;

import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    private AccountSSO.SSOType ssoType = AccountSSO.SSOType.SAML;

    @Getter
    @Setter
    private SSOConfig config;

    @Getter
    @Setter
    private Boolean isActive;

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
