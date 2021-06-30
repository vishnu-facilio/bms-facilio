package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.SecurityPolicyAPI;
import com.facilio.iam.accounts.context.SecurityPolicy;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

public class SecurityPoliciesSettingsAction extends FacilioAction {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private SecurityPolicy securityPolicy;

    @Getter
    @Setter
    private List<SecurityPolicy> securityPolicyList;

    public String createSecurityPolicy() throws Exception {
        Objects.requireNonNull(securityPolicy, "Security policy should not be empty").setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        SecurityPolicyAPI.createSecurityPolicy(securityPolicy);
        this.setResult("securityPolicy", securityPolicy);
        return SUCCESS;
    }

    public String updateSecurityPolicy() throws Exception {
        SecurityPolicyAPI.updateSecurityPolicy(securityPolicy);
        this.setResult("securityPolicy", securityPolicy);
        return SUCCESS;
    }

    public String fetchSecurityPolicy() throws Exception {
        this.securityPolicy = SecurityPolicyAPI.fetchSecurityPolicy(this.id);
        this.setResult("securityPolicy", this.securityPolicy);
        return SUCCESS;
    }

    public String getAllSecurityPolicy() throws Exception {
        this.securityPolicyList = SecurityPolicyAPI.fetchAllSecurityPolicies(AccountUtil.getCurrentOrg().getOrgId());
        this.setResult("securityPolicies", this.securityPolicyList);
        return SUCCESS;
    }
}
