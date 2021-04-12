package com.facilio.iam.accounts.context;

import lombok.Data;

@Data
public class SecurityPolicy {
    private String name;
    private long id = -1l;
    private Boolean isDefault;
    private Boolean isTOTPEnabled;
    private Boolean isMOTPEnabled;
    private Boolean isPwdPolicyEnabled;
    private Integer pwdMinLength;
    private Boolean pwdIsMixed;
    private Integer pwdMinSplChars;
    private Integer pwdMinNumDigits;
    private Integer pwdMinAge;
    private Integer pwdPrevPassRefusal;
    private Boolean isWebSessManagementEnabled;
    private Integer webSessLifeTime;
    private Integer idleSessionTimeOut;
}
