package com.facilio.iam.accounts.context;

import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SecurityPolicy implements Serializable {
    private String name;
    private String description;
    private long id = -1L;
    private Boolean isDefault;
    private Boolean isTOTPEnabled;
    private Boolean isMOTPEnabled;
    private Boolean isPwdPolicyEnabled;
    private Boolean isMFAEnabled;
    private Integer pwdMinLength;
    private Boolean pwdIsMixed;
    private Integer pwdMinSplChars;
    private Integer pwdMinNumDigits;
    private Integer pwdMinAge;
    private Integer pwdPrevPassRefusal;
    private Boolean isWebSessManagementEnabled;
    private Integer webSessLifeTime;
    private Integer idleSessionTimeOut;
    private long orgId = -1L;

    @JsonIgnore
    @SneakyThrows
    public Map<String, Object> getAsMap() {
        SecurityPolicy securityPolicy = new SecurityPolicy(this);
        securityPolicy.pwdMinLength = securityPolicy.pwdMinLength == null ? -99 : securityPolicy.pwdMinLength;
        securityPolicy.pwdMinSplChars = securityPolicy.pwdMinSplChars == null ? -99 : securityPolicy.pwdMinSplChars;
        securityPolicy.pwdMinNumDigits = securityPolicy.pwdMinNumDigits == null ?  -99 : securityPolicy.pwdMinNumDigits;
        securityPolicy.pwdMinAge = securityPolicy.pwdMinAge == null ? -99 : securityPolicy.pwdMinAge;
        securityPolicy.pwdPrevPassRefusal = securityPolicy.pwdPrevPassRefusal == null ? -99 : securityPolicy.pwdPrevPassRefusal;
        securityPolicy.webSessLifeTime = securityPolicy.webSessLifeTime == null ? -99 : securityPolicy.webSessLifeTime;
        securityPolicy.idleSessionTimeOut = securityPolicy.idleSessionTimeOut == null ? -99 : securityPolicy.idleSessionTimeOut;
        return FieldUtil.getAsProperties(securityPolicy);
    }


    public SecurityPolicy(SecurityPolicy securityPolicy) {
        this.name = securityPolicy.name;
        this.description = securityPolicy.description;
        this.id = securityPolicy.id;
        this.isDefault = securityPolicy.isDefault;
        this.isTOTPEnabled = securityPolicy.isTOTPEnabled;
        this.isMOTPEnabled = securityPolicy.isMOTPEnabled;
        this.isPwdPolicyEnabled = securityPolicy.isPwdPolicyEnabled;
        this.isMFAEnabled = securityPolicy.isMFAEnabled;
        this.pwdMinLength = securityPolicy.pwdMinLength;
        this.pwdIsMixed = securityPolicy.pwdIsMixed;
        this.pwdMinSplChars = securityPolicy.pwdMinSplChars;
        this.pwdMinNumDigits = securityPolicy.pwdMinNumDigits;
        this.pwdMinAge = securityPolicy.pwdMinAge;
        this.pwdPrevPassRefusal = securityPolicy.pwdPrevPassRefusal;
        this.isWebSessManagementEnabled = securityPolicy.isWebSessManagementEnabled;
        this.webSessLifeTime = securityPolicy.webSessLifeTime;
        this.idleSessionTimeOut = securityPolicy.idleSessionTimeOut;
        this.orgId = securityPolicy.orgId;
    }

}
