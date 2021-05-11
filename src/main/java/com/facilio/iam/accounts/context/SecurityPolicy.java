package com.facilio.iam.accounts.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SecurityPolicy implements Serializable {
    private String name;
    private long id = -1L;
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
    private List<Long> users;
    private long orgId = -1L;
}
