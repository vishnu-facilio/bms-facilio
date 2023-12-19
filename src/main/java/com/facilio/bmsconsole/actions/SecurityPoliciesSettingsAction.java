package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.SecurityPolicyAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.context.SecurityPolicy;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

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
    @Getter
    @Setter
    private String moduleName;

    public String createSecurityPolicy() throws Exception {
        securityPolicy.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
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
        if (moduleName != null) {
            List<String> allowedModules =  Arrays.asList(
                    FacilioConstants.ContextNames.TENANT_CONTACT,
                    FacilioConstants.ContextNames.VENDOR_CONTACT,
                    FacilioConstants.ContextNames.CLIENT_CONTACT,
                    FacilioConstants.ContextNames.EMPLOYEE
            );

            if (!allowedModules.contains(moduleName)) {
                throw new IllegalArgumentException("Sorry, you don't have permission to access this module.");
            }
        }
        this.securityPolicyList = SecurityPolicyAPI.fetchAllSecurityPolicies(AccountUtil.getCurrentOrg().getOrgId());
        this.setResult("securityPolicies", this.securityPolicyList);
        return SUCCESS;
    }

    public String deleteSecurityPolicy() throws Exception {
        SecurityPolicyAPI.deleteSecurityPolicy(getId());
        return SUCCESS;
    }
}
