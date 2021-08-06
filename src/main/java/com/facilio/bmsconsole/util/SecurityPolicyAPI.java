package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.service.FacilioService;

import java.util.*;

public class SecurityPolicyAPI {
    public static long createSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
        securityPolicy.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().createSecurityPolicy(securityPolicy));
    }

    public static void updateSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
        securityPolicy.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
        FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().updateSecurityPolicy(securityPolicy));
    }

    public static SecurityPolicy fetchSecurityPolicy(long id) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().fetchSecurityPolicy(id, orgId));
    }

    public static List<SecurityPolicy> fetchAllSecurityPolicies(long orgId) throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().fetchAllSecurityPolicies(orgId));
    }

    public static SecurityPolicy fetchDefaultSecurityPolicy() throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().fetchDefaultSecurityPolicy(orgId));
    }


    public static void deleteSecurityPolicy(long id) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().deleteSecurityPolicy(id, orgId));
    }

}
